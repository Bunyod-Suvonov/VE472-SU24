# import logging
# import datetime
import avro.schema
from avro.datafile import DataFileWriter
from avro.io import DatumWriter
import os
import h5py
import fastavro
from loguru import logger
import numpy as np

from pyspark import SparkContext
sc = SparkContext()
base_path = '/home/hadoopuser/ece472/data'
alphabet = [chr(i + ord('A')) for i in range(26)]
schema = avro.schema.parse(open("avroPredict.avsc", "rb").read())

# Let Spark Decide Job numbers

def traverse_folder(folder_path: str, writer: DataFileWriter):
    for item in os.listdir(folder_path):
        file_path = os.path.join(folder_path, item)
        if os.path.isfile(file_path):
            # now_time = datetime.datetime.now()
            # logging.info("[" + str(now_time) + "] " + "Reading File: " + str(file_path))
            file = h5py.File(file_path, 'r')
            data = extract_data(file)
            writer.append(data)
            file.close()
        else:
            print("In folder: "+ str(file_path))
            traverse_folder(file_path, writer)

def extract_data(file: h5py.File):
    analysis = file['analysis']
    metadata = file['metadata']
    musicbrainz = file['musicbrainz']

    # data in 'songs' field
    analysis_data = list(analysis['songs'][()])[0]
    metadata_data = list(metadata['songs'][()])[0]
    musicbrainz_data = list(musicbrainz['songs'][()])[0]

    return {
        "loudness": float(analysis_data[-8]),
        "segments_loudness_max_mean": float(np.array(analysis['segments_loudness_max']).mean()),
        "segments_loudness_max_time_mean": float(np.array(analysis['segments_loudness_max_time']).mean()),
        "artist_7digitalid": int(metadata_data[1]),
        "time_signature": float(analysis_data[-3]),
        "time_signature_confidence": float(analysis_data[-2]),
        
        "segments_confidence_mean": float(np.array(analysis['segments_confidence']).mean()),
        "artist_familiarity": float(metadata_data[2]),
        "segments_pitches_mean": float(np.array(analysis['segments_pitches']).mean()),
        
        "segments_start_mean": float(np.array(analysis['segments_start']).mean()),
        "bars_start_mean": float(np.array(analysis['bars_start']).mean()),
        "beats_start_mean": float(np.array(analysis['beats_start']).mean()),
        "sections_start_mean": float(np.array(analysis['sections_start']).mean()),
        "tatums_start_mean": float(np.array(analysis['tatums_start']).mean()),

        "release_7digitalid": int(metadata_data[-5]),
        "track_7digitalid": int(metadata_data[-1]),
        "duration": float(analysis_data[3]),
        "artist_hotness": float(metadata_data[3]),

        "song_hotness": float(metadata_data[-4]),
        "tempo": float(analysis_data[-4]),
        "key": float(analysis_data[-10]),

        "bar_num": float(len(analysis['bars_start'][()])),
        "beat_num": float(len(analysis['beats_start'][()])),
        "section_num": float(len(analysis['sections_start'][()])),
        "segment_num": float(len(analysis['segments_start'][()])),

        "end_of_fade_in": float(analysis_data[4]),
        "mode": float(analysis_data[-7]),
        "start_of_fade_out": float(analysis_data[-5]),
        
        "tatum_num": float(len(analysis['tatums_start'][()])),

        "artist_id": metadata_data[4],
        "artist_name": metadata_data[9],
        "artist_longitude": float(metadata_data[7]),
        "artist_latitude": float(metadata_data[5]),
        "artist_terms_weight_mean": float(np.array(metadata['artist_terms_weight']).mean()),
        "artist_terms_freq_mean": float(np.array(metadata['artist_terms_freq']).mean()),

        "song_id": metadata_data[-3],
        "title": metadata_data[-2],

        "album_id": int(metadata_data[-5]),
        "album_name": metadata_data[-6],
        "year": float(musicbrainz_data[1])
    }

def avro_with_letter(letter: str):
    folder_path = os.path.join(base_path, letter)
    writer = DataFileWriter(open(f"./avro_files/songs_{letter}.avro", "wb"), DatumWriter(), schema)
    traverse_folder(folder_path, writer)
    writer.close()

def merge_avro_files():
    merged_results = []
    merged_schema = None
    for letter in alphabet:
        file_path = f"./avro_files/songs_{letter}.avro"
        with open(file_path, 'rb') as f:
            reader = fastavro.reader(f)
            records = list(reader)
            if merged_results:
                merged_results.extend(records)
            else:
                merged_results = records
                merged_schema = reader.writer_schema
    return merged_results, merged_schema

if __name__ == "__main__":
    try:
        result_rdd = sc.parallelize(alphabet, 26).map(avro_with_letter)
        result_rdd.collect()

        merged_results, merged_schema = merge_avro_files()

        with open("songs_advanced.avro", 'wb') as f:
            fastavro.writer(f, merged_schema, merged_results)
    except Exception as error:
        logger.exception(error)

