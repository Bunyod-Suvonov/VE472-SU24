import sqlite3

def fetch_artists_and_similars(db_path):
    # Connect to the SQLite database
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Query to fetch artists and their similar artists
    query = """
    SELECT a.artist_id, GROUP_CONCAT(s.similar)
    FROM artists a
    LEFT JOIN similarity s ON a.artist_id = s.target
    GROUP BY a.artist_id;
    """
    
    cursor.execute(query)
    
    # Fetch all results
    results = cursor.fetchall()
    
    # Close the database connection
    conn.close()
    
    return results

def write_to_file(data, output_file):
    with open(output_file, 'w') as f:
        for artist_id, similars in data:
            if similars:
                f.write(f"{artist_id}|{similars}\n")
            else:
                f.write(f"{artist_id}|\n")

def generate_similarities():
    db_path = './mnt/AdditionalFiles/artist_similarity.db'  # Path to your SQLite database
    output_file = './files/artist_similars.txt'  # Output file
    
    artists_and_similars = fetch_artists_and_similars(db_path)
    write_to_file(artists_and_similars, output_file)

    print(f"Artists' list has been written to ./files/{output_file}")

if __name__ == "__main__":
    generate_similarities()
