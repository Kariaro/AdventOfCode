import Utils as Utils
import re

class Line:
    def __init__(self, value):
        parts = re.split(' -> |,', value)
        self.x1 = int(parts[0])
        self.y1 = int(parts[1])
        self.x2 = int(parts[2])
        self.y2 = int(parts[3])
        self.minX = min(self.x1, self.x2)
        self.minY = min(self.y1, self.y2)
        self.maxX = max(self.x1, self.x2)
        self.maxY = max(self.y1, self.y2)
        self.isAxisBounded = (self.x1 == self.x2) or (self.y1 == self.y2)
        
def increment(system, point):
    system[point] = system.get(point, 0) + 1

def draw(line, system):
    if line.y1 == line.y2:
        for x in range(line.minX, line.maxX + 1):
            increment(system, (x, line.minY))
    elif line.x1 == line.x2:
        for y in range(line.minY, line.maxY + 1):
            increment(system, (line.minX, y))
    else:
        xd = 1 if line.x1 < line.x2 else -1
        yd = 1 if line.y1 < line.y2 else -1
        dd = max(line.maxX - line.minX, line.maxY - line.minY)
        for i in range(0, dd + 1):
            increment(system, (line.x1 + xd * i, line.y1 + yd * i))

@Utils.time_it_and_evaluate
def partOne(lines):
    system = {}
    for line in lines:
        if line.isAxisBounded:
            draw(line, system)
    
    return sum(1 if v > 1 else 0 for k, v in system.items())

@Utils.time_it_and_evaluate
def partTwo(lines):
    system = {}
    for line in lines:
        draw(line, system)
    
    return sum(1 if v > 1 else 0 for k, v in system.items())

if __name__ == '__main__':
    lines = [ Line(x) for x in Utils.readAllLines(2021, 'day5') ]
    
    print('Day 5')
    partOne(lines)
    partTwo(lines)