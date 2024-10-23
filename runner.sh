#!/bin/bash

cd "$(dirname "$0")"

MAPS_DIR="maps"

for map_file in "$MAPS_DIR"/*.txt; do
  map_name=$(basename "$map_file" .txt)

  echo "Running map: $map_name"
  java -classpath out main.Driver "$map_name" bot
done

echo "Press any key to exit..."
read -n 1 -s
