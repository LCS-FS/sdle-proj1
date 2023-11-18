#!/bin/bash

# Change to the destination directory
cd proxy || exit

# Run the desired command
./gradlew bootRun

# Optionally, you can print a message indicating the process is complete
echo "Script completed successfully. Source directory: $source_dir, Destination directory: $destination_dir"
