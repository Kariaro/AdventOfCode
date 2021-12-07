import Utils as Utils

def partOne(list):
    count = 0
    last = 2**32
    for value in list:
        if value > last:
            count += 1
        last = value
    
    return count

def partTwo(list):
    count = 0
    last = 2**32
    for i in range(len(list) - 2):
        value = list[i] + list[i + 1] + list[i + 2];
        if value > last:
            count += 1
        last = value
    
    return count

if __name__ == '__main__':
    list = [ int(x) for x in Utils.readAllLines('day1/input') ]
    print('Day 1')
    print('PartOne:', partOne(list))
    print('PartTwo:', partTwo(list))