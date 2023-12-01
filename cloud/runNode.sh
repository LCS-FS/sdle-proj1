#!/bin/bash

# Define the source and destination directories
source_dir="node"
destination_dir=""

# Find a unique destination directory name
counter=1
while [ -d "${source_dir}_${counter}" ]; do
  ((counter++))
done
destination_dir="${source_dir}_${counter}"

# Copy the source directory to the destination directory
cp -r "$source_dir" "$destination_dir"

# Change to the destination directory
cd "$destination_dir" || exit

# Run the desired command
./gradlew bootRun --args="${counter}"
