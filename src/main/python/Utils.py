# Utility class
import time

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