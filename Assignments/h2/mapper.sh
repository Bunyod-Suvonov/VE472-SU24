#!/bin/sh

input_file="students.csv"
output_dir="csv_files"
mkdir -p "$output_dir"

line_count=0
file_count=1

output_file="$output_dir/students_part_$file_count.csv"

while IFS= read -r line
do
    echo "$line" >> "$output_file"
    line_count=$((line_count + 1))

    if [ "$line_count" -eq 400 ]; then
        line_count=0
        file_count=$((file_count + 1))
        output_file="$output_dir/students_part_$file_count.csv"
    fi
done < "$input_file"
