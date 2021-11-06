#!/usr/bin/python3
from os import listdir
from os.path import isfile, join
import re
import sys

FILES_LOCATION = "conf/routes-build/src"
OUTPUT_LOCATION = "conf/routes"
CONF_FILE = "conf/private/server-properties"

def getLevelFromServerProps():
    """Search the server props file for the configured security level"""
    pattern = r"RoutesSecurityLevel=(\d+)"
    file = open(CONF_FILE, "r")
    for line in file:
        result = re.match(pattern, line)
        if result:
            return int(result.group(1))

def getLevelFromFileName(fileName):
    """Given a (path-less) filename, get the level from it"""
    pattern = r'(\d+)\_.*'
    result = re.match(pattern, fileName)
    return int(result.group(1))

def getFiles():
    """Get all files in the FILES_LOCATION (file, fileWithPath, integer Level)"""
    concatFullPath = lambda f : join(FILES_LOCATION, f)
    excludeDirs = lambda f: isfile(join(FILES_LOCATION, f))
    unsorted = [(f, concatFullPath(f), getLevelFromFileName(f)) for f in listdir(FILES_LOCATION) if excludeDirs(f)]
    return sorted(unsorted, key=lambda t: t[2])

def combineFiles(inPaths, outPath):
    """Write all the inPaths to outPath"""
    with open(outPath, 'w') as outfile:
        for fname in inPaths:
            with open(fname) as infile:
                outfile.write(infile.read())

def main():
    if (len(sys.argv) > 1):
        level = int(sys.argv[1])
    else:
        level = getLevelFromServerProps()
    print("Building routes with security level " + str(level))
    pathsToKeep = []
    pathsToDrop = []
    for f in getFiles():
        if (f[2] <= level):
            pathsToKeep.append(f[1])
        else :
            pathsToDrop.append(f[1])
    combineFiles(pathsToKeep, OUTPUT_LOCATION)
    print("Building:")
    for f in pathsToKeep:
        print("*   " + f)
    print("Ignoring:")
    for f in pathsToDrop:
        print("X   " + f)

main()
