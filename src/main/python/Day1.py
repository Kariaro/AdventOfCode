import Utils as Utils

@Utils.time_it_and_evaluate
def partOne(lines):
    count = 0
    last = 2**32
    for value in lines:
        if value > last:
            count += 1
        last = value
    
    return count

@Utils.time_it_and_evaluate
def partTwo(lines):
    count = 0
    last = 2**32
    for i in range(len(lines) - 2):
        value = lines[i] + lines[i + 1] + lines[i + 2];
        if value > last:
            count += 1
        last = value
    
    return count

if __name__ == '__main__':
    lines = [ int(x) for x in Utils.readAllLines(2021, 'day1') ]
    
    print('Day 1')
    partOne(lines)
    partTwo(lines)