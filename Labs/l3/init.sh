#!/bin/bash

# define variables
USER_DIR="$HOME"
VAR_DIR="$USER_DIR/vvv"
mkdir "$VAR_DIR"

# extract .tar file
tar -xvf "$USER_DIR/imdb.tar" -C "$VAR_DIR"

# extract tsv.gz files
find "$VAR_DIR" -name "*.tsv.gz" -exec gzip -d {} \;

# remove the first line of all tsv files:
for file in "$VAR_DIR"/*.tsv; do
	sed -i '1d' "$file"
done
