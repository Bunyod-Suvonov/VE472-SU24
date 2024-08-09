import sys
from mrjob.job import MRJob
from mrjob.protocol import RawValueProtocol
from mrjob.step import SparkStep
from pyspark import SparkContext

BFS_DEPTH = 100

class GraphNode:
    def __init__(self):
        self.nodeID = ''
        self.edges = []
        self.distance = float('inf')
        self.state = 'WHITE'

    def from_string(self, line):
        parts = line.split('|')
        if len(parts) == 4:
            self.nodeID = parts[0]
            self.edges = parts[1].split(',') if parts[1] else []
            self.distance = int(parts[2])
            self.state = parts[3].strip()

    def to_string(self):
        edges_str = ','.join(self.edges)
        return '|'.join([self.nodeID, edges_str, str(self.distance), self.state])

class SparkBFS(MRJob):
    INPUT_PROTOCOL = RawValueProtocol
    OUTPUT_PROTOCOL = RawValueProtocol
    
    def spark(self, input_path, output_path):
        sc = SparkContext(appName='mrjob Spark BFS script')

        lines = sc.textFile(input_path)
        nodes = lines.map(self.from_line)

        for iteration in range(BFS_DEPTH):
            mapped = nodes.flatMap(self.map_step)
            nodes = mapped.reduceByKey(self.reduce_step)
        
        nodes.saveAsTextFile(output_path)
        sc.stop()

    def from_line(self, line):
        node = GraphNode()
        node.from_string(line)
        return (node.nodeID, (node.edges, node.distance, node.state))

    def map_step(self, node):
        nodeID, (edges, distance, state) = node
        node_obj = GraphNode()
        node_obj.nodeID = nodeID
        node_obj.edges = edges
        node_obj.distance = distance
        node_obj.state = state

        results = []

        if node_obj.state == 'WHITE':
            self.increment_counter('', "Remaining Nodes", 1)
        
        if node_obj.state == 'GRAY':
            for neighbor in node_obj.edges:
                neighbor_node = GraphNode()
                neighbor_node.nodeID = neighbor
                neighbor_node.distance = node_obj.distance + 1
                neighbor_node.state = 'GRAY'
                results.append((neighbor_node.nodeID, (neighbor_node.edges, neighbor_node.distance, neighbor_node.state)))

            node_obj.state = 'BLACK'

        results.append((node_obj.nodeID, (node_obj.edges, node_obj.distance, node_obj.state)))
        return results

    def reduce_step(self, data1, data2):
        edges1, distance1, state1 = data1
        edges2, distance2, state2 = data2

        all_edges = list(set(edges1 + edges2))
        min_distance = min(distance1, distance2)
        final_state = 'BLACK' if state1 == 'BLACK' or state2 == 'BLACK' else 'GRAY' if state1 == 'GRAY' or state2 == 'GRAY' else 'WHITE'

        return (all_edges, min_distance, final_state)

if __name__ == '__main__':
    SparkBFS.run()
