# HW1

## Ex 1. Preparation

#### 1. Done. Please refer to mapper.sh file in the same directory.
#### 2. I created a single text file with a few letters in its content and uploaded it to hadoop filesystem. I went to localhost:9870 > "Utilities" > "Browse the filesystem", and saw 128 MB given for my text file in "Block size" tab.

## Ex 2. Filecrush

#### 1. We sometimes have so many small files that dont fill up to even half of their assigned block sizes. By using filecrush, we can save incredible amount of space by combining small files into less larger files.

#### 2. Filecrush options

* &emsp;-Dfs.block.size=128000000 changes the block size to 128 megabytes in HDFS. However, we have 128 MB as default in our current working hadoop version, so we might want to change it to some other values, otherwise, the current value is unnecessary.

* &emsp;--clone Useful for external Hive tables. Clones the input directory to output directory and writes the output files inside the input directory.

* &emsp;--compress gzip Specifies the compression codec for output files.

* &emsp;--input-format text Specify the format of input files. text specifically is a shortcut for org.apache.hadoop.mapred.TextInputFormat.

* &emsp;--output-format sequence Specify the format of output files. sequence specifically is a shortcut for org.apache.hadoop.mapred.SequenceFileOutputFormat, and it's the default format for both input and output formats.

#### 3. I couldn't even install it. When the compiled jar is ran using hadoop, Null pointer exception error is being caused, most likely due to outdated code and unmatching package versions.


## Ex 3. S3DistCp

 --groupBy=PATTERN option can help us merge the files that have a specific shared pattern in their names. For example: ‑‑groupBy=.*subnetid.*([0-9]+-[0-9]+-[0-9]+-[0-9]+).* The other options that might be useful are: ‑‑src=LOCATION, location of the input files, --dest=LOCATION, location of the output files to be put, ‑‑outputCodec=CODEC, specifies the compression format for output files.


## Ex 4. Avro

#### 1. "Snappy is a compression/decompression library that does not aim for maximum compression, or compatibility with any other compression library; instead, it aims for very high speeds and reasonable compression. For instance, compared to the fastest mode of zlib, Snappy is an order of magnitude faster for most inputs, but the resulting compressed files are anywhere from 20% to 100% bigger." (taken from snappy's website) Snappy can be used when fast storage optimization is needed.

#### 2. Please check avro-proj folder. I commented the specifying Snappy codec part because it's causing null pointer exception.
#### 3. Same with 2. 