#!/bin/bash

# Define the source directory
source_dir="node"

# If a node ID is provided as an argument, use it; otherwise, find the next available ID
if [ -n "$1" ]; then
  node_id="$1"
  echo "Starting node ${node_id}..."
else
  # Find a unique destination directory name
  counter=1
  while [ -d "${source_dir}_${counter}" ]; do
    ((counter++))
  done
  node_id="$counter"
  destination_dir="${source_dir}_${node_id}"
  cp -r "$source_dir" "$destination_dir"
  echo "Starting a new node with id ${node_id}"
fi

destination_dir="${source_dir}_${node_id}"

# Check if the destination directory exists
if [ ! -d "$destination_dir" ]; then
  echo "Error: Node with ID $node_id does not exist."
  exit 1
fi

# Change to the destination directory
cd "$destination_dir" || exit

# Run the desired command
./gradlew bootRun --args="${node_id}"
