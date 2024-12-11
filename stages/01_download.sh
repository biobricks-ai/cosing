#!/usr/bin/env bash

# Script to download files

# Get local path
localpath=$(pwd)
echo "Local path: $localpath"

# Create the list directory to save list of remote files and directories
listpath="$localpath/list"
echo "List path: $listpath"
mkdir -p $listpath
cd $listpath;

# Define the FTP base address
export ftpbase=""

# Retrieve the list of files to download from FTP base address
wget --no-remove-listing $ftpbase
cat index.html | grep -Po '(?<=href=")[^"]*' | sort | cut -d "/" -f 10 > files.txt
rm .listing
rm index.html

# Create the download directory
export downloadpath="$localpath/download"
echo "Download path: $downloadpath"
mkdir -p "$downloadpath"
cd $downloadpath;

# Download files in parallel
cat $listpath/files.txt | xargs -P14 -n1 bash -c '
  echo $0
  wget -nH -q -nc -P $downloadpath $ftpbase$0
'

echo "Download done."
