#!/bin/bash

# Define the input file name
input_file="./title.ratings.tsv"

# Define a temporary file for storing the modified content
temp_file="${input_file}.temp"

# Replace all double quotes with an empty string using sed
sed 's/"//g' "$input_file" > "$temp_file"

# Move the modified content back to the original file
mv "$temp_file" "$input_file"

# Optionally, you can remove the temporary file if no longer needed
# rm "$temp_file"

echo "Double quotes removed from $input_file"
