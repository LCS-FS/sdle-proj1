#!/bin/bash

# Define the source and destination directories
source_dir="node"
destination_dir=""

# Find a unique destination directory name
counter=1
while [ -d "${source_dir}${counter}" ]; do
  ((counter++))
done
destination_dir="${source_dir}_${counter}"

# Copy the source directory to the destination directory
cp -r "$source_dir" "$destination_dir"

# Change to the destination directory
cd "$destination_dir" || exit

# Run the desired command
./gradlew bootRun

# Optionally, you can print a message indicating the process is complete
echo "Script completed successfully. Source directory: $source_dir, Destination directory: $destination_dir"
