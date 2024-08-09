# README

## Requirements

1. **Mount the Million Song Dataset's ISO:**
   - Mount the Million Song Dataset's ISO into the `mnt` folder in the current directory.
   - Example command to mount:
     ```bash
     sudo mount -o loop path_to_msd.iso ./mnt
     ```

2. **Dataset Preparation:**
   - Ensure the avro dataset generated in previous steps is named `songs_advanced.avro` and placed in the `files` folder.
   - Example directory structure:
     ```
     .
     ├── files
     │   └── songs_advanced.avro
     └── mnt
     ```

## Usage

1. **Driver Script Help:**
   - Run the following command to get help on how to run the driver code:
     ```bash
     python3 driver.py -h
     ```

### Running MapReduce and Spark separately
2. **Graph File:**
   - Ensure `graph.txt` is generated and available in the `files` directory.
   - Use the following command to run MapReduce:
     ```bash
     python3 bfs_mr ./files/graph.txt > ./output/output.txt
     ```

4. **Run Spark:**
   - Use the following command to run Spark:
     ```bash
     python3 bfs_spark.py ./files/graph.txt > ./output/output.txt
     ```
