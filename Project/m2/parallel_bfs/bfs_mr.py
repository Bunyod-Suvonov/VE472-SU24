import sys
from mrjob.job import MRJob
from mrjob.step import MRStep
from mrjob.protocol import RawValueProtocol

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

class BFS(MRJob):

    INPUT_PROTOCOL = RawValueProtocol
    OUTPUT_PROTOCOL = RawValueProtocol

    def map_step(self, _, line):
        node = GraphNode()
        node.from_string(line)

        if node.state == 'WHITE': 
            self.increment_counter('', "Remaining Nodes", 1)
        
        if node.state == 'GRAY':
            for neighbor in node.edges:
                neighbor_node = GraphNode()
                neighbor_node.nodeID = neighbor
                neighbor_node.distance = node.distance + 1
                neighbor_node.state = 'GRAY'
                yield neighbor, neighbor_node.to_string()

            node.state = 'BLACK'

        yield node.nodeID, node.to_string()

    def reduce_step(self, key, values):
        all_edges = []
        min_distance = float('inf')
        final_state = 'WHITE'
        
        for value in values:
            node = GraphNode()
            node.from_string(value)

            if node.edges:
                all_edges.extend(node.edges)

            if node.distance < min_distance:
                min_distance = node.distance

            if node.state == 'BLACK':
                final_state = 'BLACK'

            if node.state == 'GRAY' and final_state == 'WHITE':
                final_state = 'GRAY'

        result_node = GraphNode()
        result_node.nodeID = key
        result_node.distance = min_distance
        result_node.state = final_state
        result_node.edges = all_edges

        yield key, result_node.to_string()

    def steps(self):
        return [MRStep(mapper=self.map_step, reducer=self.reduce_step) for _ in range(BFS_DEPTH)]

if __name__ == '__main__':
    BFS.run()
