# Utility class
import time
import sys

def getResourcePath(year, name):
    return '../resources/aoc' + str(year) + '/' + name

def readAllLines(year, name):
    with open(getResourcePath(year, name)) as file:
        return [line.rstrip() for line in file]

def readAll(year, name):
    with open(getResourcePath(year, name)) as file:
        return file.read()

def time_it_and_evaluate(method):
    def wrapper_method(*arg, **kw):
        start = time.perf_counter_ns()
        ret = method(*arg, **kw)
        end = time.perf_counter_ns()
        
        print(f'{method.__name__}: {ret}')
        print(f'{(end - start) / 1e6 :2.2f} ms\n')
        return ret

    return wrapper_method
 
class Node:
    def __init__(self, value):
        self.adjacent = dict()
        self.distance = sys.maxsize
        self.value = value
    
    def connect_nodes(self, node):
        self.adjacent[node] = node.value
        node.adjacent[self] = self.value
    
    def connect_node(self, node, distance):
        self.adjacent[node] = distance

class Djikstra:
    def get_shortest_distance(start, end):
        start.distance = 0
        
        settled = set()
        unsettled = set()
        unsettled.add(start)
        
        while len(unsettled):
            current = Djikstra.get_lowest_distance(unsettled)
            current_distance = current.distance
            unsettled.remove(current)
            
            for adjacent, distance in current.adjacent.items():
                if not adjacent in settled:
                    next_distance = current_distance + distance
                    if next_distance < adjacent.distance:
                        adjacent.distance = next_distance
                    unsettled.add(adjacent)
            settled.add(current)
        return end.distance
    
    def get_lowest_distance(unsettled):
        lowest_node = None
        lowest_distance = sys.maxsize
        
        for node in unsettled:
            node_distance = node.distance
            if node_distance < lowest_distance:
                lowest_distance = node_distance
                lowest_node = node
        return lowest_node
