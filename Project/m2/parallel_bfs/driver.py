import fastavro
import pandas as pd
import os
import sys
import argparse
from subprocess import call

from gen_similarities import generate_similarities
from construct_graph import construct_graph

def run_bfs_mapreduce_local():
    os.system('python3 bfs_mr.py ./files/graph.txt > ./output/output.txt')

def run_bfs_mapreduce_cluster():
    hdfs_graph_path = './files/graph.txt'
    hdfs_output_dir = './output'
    
    os.system(f'hdfs dfs -rm {hdfs_graph_path}')
    os.system(f'hdfs dfs -rm -r {hdfs_output_dir}')
    os.system(f'hdfs dfs -put ./files/graph.txt /')
    os.system(f'python3 bfs_mr.py -r hadoop hdfs://{hdfs_graph_path} -o hdfs://{hdfs_output_dir}')
    os.system(f'hdfs dfs -get {hdfs_output_dir}/part-00000 ./output/output.txt')

def run_bfs_spark_local():
    os.system('python3 bfs_spark.py ./files/graph.txt > ./output/output.txt')

def run_bfs_spark_cluster():
    hdfs_graph_path = './files/graph.txt'
    hdfs_output_dir = './output'
    
    os.system(f'hdfs dfs -rm {hdfs_graph_path}')
    os.system(f'hdfs dfs -rm -r {hdfs_output_dir}')
    os.system(f'hdfs dfs -put ./files/graph.txt /')
    os.system(f'spark-submit --master yarn bfs_spark.py hdfs://{hdfs_graph_path} hdfs://{hdfs_output_dir}')
    os.system(f'hdfs dfs -get {hdfs_output_dir}/part-00000 ./output/output.txt')

def main():
    parser = argparse.ArgumentParser(description='Process artist similarities.')
    parser.add_argument('starting_song_id', type=str, help='The starting song ID for the BFS process.')
    parser.add_argument('--local', action='store_true', help='Run the code in local mode.')
    parser.add_argument('--cluster', action='store_true', help='Run the code in cluster mode.')
    parser.add_argument('--mapreduce', action='store_true', help='Use MapReduce for BFS.')
    parser.add_argument('--pyspark', action='store_true', help='Use PySpark for BFS.')

    args = parser.parse_args()

    if not args.local and not args.cluster:
        print("You must specify either --local or --cluster.")
        sys.exit(1)

    if args.local and args.cluster:
        print("You must specify only one of --local or --cluster.")
        sys.exit(1)

    if not args.mapreduce and not args.pyspark:
        print("You must specify either --mapreduce or --pyspark.")
        sys.exit(1)

    if args.mapreduce and args.pyspark:
        print("You must specify only one of --mapreduce or --pyspark.")
        sys.exit(1)

    starting_song_id = args.starting_song_id

    print("Processing the avro dataset to retrieve artist ID from the starting song ID")
    songs = []
    with open('./files/songs_advanced.avro', 'rb') as f:
        reader = fastavro.reader(f)
        for record in reader:
            songs.append(record)

    df = pd.DataFrame(songs)

    df['song_id'] = df['song_id'].apply(lambda x: x.decode('utf-8') if isinstance(x, bytes) else x)
    df['artist_id'] = df['artist_id'].apply(lambda x: x.decode('utf-8') if isinstance(x, bytes) else x)

    artist_id = df[df['song_id'] == starting_song_id]['artist_id'].iloc[0]
    
    print("Generating a similar artists list out of artist_similarity.db from the Million Song dataset")
    generate_similarities()

    print("Constructing a graph out of similar artists list")
    construct_graph(artist_id)

    if args.local:
        if args.mapreduce:
            print("Running BFS on graph in local mode using MapReduce")
            run_bfs_mapreduce_local()
        elif args.pyspark:
            print("Running BFS on graph in local mode using PySpark")
            run_bfs_spark_local()
    elif args.cluster:
        if args.mapreduce:
            print("Running BFS on graph in cluster mode using MapReduce")
            run_bfs_mapreduce_cluster()
        elif args.pyspark:
            print("Running BFS on graph in cluster mode using PySpark")
            run_bfs_spark_cluster()
    
    print("BFS successful! Processing the suggested artists")

    artist_ids = []

    with open('output/output.txt', 'r') as file:
        for line in file:
            if args.mapreduce:
                fields = line.strip().split('|')
                if fields[3] == "BLACK":
                    artist_id = fields[0]
                    artist_ids.append(artist_id)
            elif args.pyspark:
                fields = eval(line.strip())
                node_id = fields[0]
                connections, distance, color = fields[1]
                if color == "BLACK":
                    artist_ids.append(node_id)

    print(f"Extracted {len(artist_ids)} artist IDs")

    df['title'] = df['title'].apply(lambda x: x.decode('utf-8') if isinstance(x, bytes) else x)
    df['artist_name'] = df['artist_name'].apply(lambda x: x.decode('utf-8') if isinstance(x, bytes) else x)

    df['artist_hotness'] = df['artist_hotness'].fillna(df['artist_hotness'].mean())
    df['song_hotness'] = df['song_hotness'].fillna(df['song_hotness'].mean())

    df['total_hotness'] = df['artist_hotness'] + df['song_hotness']

    print("Processing done, extracting songs")
    filtered_songs = df[(df['artist_id'].isin(artist_ids)) & (df['song_id'] != starting_song_id)]

    print(f"Number of similar songs: {len(filtered_songs)}")

    if not filtered_songs.empty:
        hottest_song = filtered_songs.loc[filtered_songs['total_hotness'].idxmax()]
        print(f"Suggested Song: {hottest_song['title']}, Artist: {hottest_song['artist_name']}")
    else:
        print("No songs found for the extracted artist IDs.")

if __name__ == "__main__":
    main()

