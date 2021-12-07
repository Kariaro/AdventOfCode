import Utils as Utils

def partOne(lines, maxValue):
    bestTotal = 2**32
    for i in range(maxValue):
        total = sum([ abs(j - i) for j in lines ])
        if total > bestTotal:
            break
        bestTotal = total
    
    return bestTotal

def partTwo(lines, maxValue):
    bestTotal = 2**32
    for i in range(maxValue):
        total = sum([ int(abs(j - i) * (abs(j - i) + 1) / 2) for j in lines ])
        if total > bestTotal:
            break
        bestTotal = total
    
    return bestTotal

if __name__ == '__main__':
    lines = [ int(x) for x in Utils.readAll('day7/input').rstrip().split(',') ]
    maxValue = max(lines)
    
    print('Day 7')
    print('PartOne:', partOne(lines, maxValue))
    print('PartTwo:', partTwo(lines, maxValue))