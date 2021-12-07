# Utility class

def readAllLines(path):
    with open('../resources/' + path) as file:
        return [line.rstrip() for line in file]

def readAll(path):
    lines = []
    with open('../resources/' + path) as file:
        return file.read()