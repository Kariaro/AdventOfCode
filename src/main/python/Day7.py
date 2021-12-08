import Utils as Utils

@Utils.time_it_and_evaluate
def partOne(lines, maxValue):
    bestTotal = 2**32
    for i in range(maxValue):
        total = sum(abs(j - i) for j in lines)
        if total > bestTotal:
            break
        bestTotal = total
    
    return bestTotal

@Utils.time_it_and_evaluate
def partTwo(lines, maxValue):
    bestTotal = 2**32
    for i in range(maxValue):
        total = sum(int(abs(j - i) * (abs(j - i) + 1) / 2) for j in lines)
        if total > bestTotal:
            break
        bestTotal = total
    
    return bestTotal

if __name__ == '__main__':
    lines = [ int(x) for x in Utils.readAll(2021, 'day7').rstrip().split(',') ]
    maxValue = max(lines)
    
    print('Day 7')
    partOne(lines, maxValue)
    partTwo(lines, maxValue)