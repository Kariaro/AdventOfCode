import Utils as Utils
import re

def Line(value):
    x1, y1, x2, y2 = [ int(x) for x in re.split(' -> |,', value) ]
    return x1, y1, x2, y2, (x1 == x2) or (y1 == y2)

def draw(data, system):
    x1, y1, x2, y2, _ = data
    minX = min(x1, x2)
    minY = min(y1, y2)
    maxX = max(x1, x2)
    maxY = max(y1, y2)
    if y1 == y2:
        for x in range(minX, maxX + 1):
            system[x + minY * 1000] += 1
    elif x1 == x2:
        for y in range(minY, maxY + 1):
            system[minX + y * 1000] += 1
    else:
        xd = 1 if x1 < x2 else -1
        yd = 1 if y1 < y2 else -1
        dd = max(maxX - minX, maxY - minY)
        for i in range(0, dd + 1):
            system[(x1 + xd * i) + (y1 + yd * i) * 1000] += 1

@Utils.time_it_and_evaluate
def partOne(lines):
    system = [ 0 ] * 1000000
    for line in lines:
        if line[4]:
            draw(line, system)
    
    return sum(1 for x in system if x > 1)

@Utils.time_it_and_evaluate
def partTwo(lines):
    system = [ 0 ] * 1000000
    for line in lines:
        draw(line, system)
    
    return sum(1 for x in system if x > 1)

if __name__ == '__main__':
    lines = [ Line(x) for x in Utils.readAllLines(2021, 'day5') ]
    
    print('Day 5 Fast')
    partOne(lines)
    partTwo(lines)