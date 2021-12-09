import Utils as Utils

def getLowPoint(lines, x, y, width, height):
    value = lines[y][x]
    
    return 0 if ((x > 0 and value >= lines[y][x - 1])
    or (x < width - 1 and value >= lines[y][x + 1])
    or (y > 0 and value >= lines[y - 1][x])
    or (y < height - 1 and value >= lines[y + 1][x])) else value + 1

def getBasinSize(lines, coordinateSet, x, y, width, height):
    value = lines[y][x]
    if value == 9:
        return 0
    
    if (x, y) in coordinateSet:
        return 0
    coordinateSet.add((x, y))
    
    count = 1
    if x > 0 and value < lines[y][x - 1]:
        count += getBasinSize(lines, coordinateSet, x - 1, y, width, height)
    if x < width - 1 and value < lines[y][x + 1]:
        count += getBasinSize(lines, coordinateSet, x + 1, y, width, height)
    if y > 0 and value < lines[y - 1][x]:
        count += getBasinSize(lines, coordinateSet, x, y - 1, width, height)
    if y < height - 1 and value < lines[y + 1][x]:
        count += getBasinSize(lines, coordinateSet, x, y + 1, width, height)
    
    return count

@Utils.time_it_and_evaluate
def partOne(lines, width, height):
    score = 0
    for y in range(height):
        for x in range(width):
            score += getLowPoint(lines, x, y, width, height)
    return score

@Utils.time_it_and_evaluate
def partTwo(lines, width, height):
    basinSizes = []
    coordinateSet = set()
    
    for y in range(height):
        for x in range(width):
            lowPoint = getLowPoint(lines, x, y, width, height)
            if lowPoint > 0:
                basinSizes.append(getBasinSize(lines, coordinateSet, x, y, width, height))
                coordinateSet.clear()
    basinSizes.sort(reverse=True)
    return basinSizes[0] * basinSizes[1] * basinSizes[2]

if __name__ == '__main__':
    lines = [ [ int(i) for i in x ] for x in Utils.readAllLines(2021, 'day9') ]
    width = len(lines[0])
    height = len(lines)
    
    print('Day 9')
    partOne(lines, width, height)
    partTwo(lines, width, height)