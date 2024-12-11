#!/usr/bin/env bash

# Script to unzip files

# Get local path
localpath=$(pwd)
echo "Local path: $localpath"

# Set download path
export downloadpath="$localpath/download"
echo "Download path: $downloadpath"

# Set list path
listpath="$localpath/list"
echo "List path: $listpath"

# Create raw path
export rawpath="$localpath/raw"
mkdir -p $rawpath
echo "Raw path: $rawpath"

# Unzip files in parallel
cat $listpath/files.txt | tail -n +2 | xargs -P14 -n1 bash -c '
  filename="${0%.*}"
  echo $downloadpath/$0
  echo $rawpath/$filename
  unzip $downloadpath/$0 -d $rawpath/$filename
'
