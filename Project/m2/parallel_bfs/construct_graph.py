import sys

def construct_graph(starting_artist_id):
    print(f'BFS starting input for artist {starting_artist_id}')

    with open("./files/graph.txt", 'w') as out:
        with open("./files/artist_similars.txt") as f:
            for line in f:
                line = line.strip()  # Remove any leading or trailing whitespace
                if line:  # Ensure the line is not empty
                    fields = line.split('|')
                    artist_id = fields[0]
                    numConnections = len(fields[1].split(',')) - 1 if fields[1] else 0
                    connections = fields[1] if fields[1] else ''

                    color = 'WHITE'
                    distance = 9999

                    if artist_id == starting_artist_id:
                        color = 'GRAY'
                        distance = 0

                    outStr = '|'.join((artist_id, connections, str(distance), color))
                    out.write(outStr + "\n")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python construct_graph.py <starting_artist_id>")
        sys.exit(1)

    starting_artist_id = sys.argv[1]
    construct_graph(starting_artist_id)
