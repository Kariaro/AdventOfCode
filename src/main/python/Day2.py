import Utils as Utils

def partAll(list):
    horizontal_position = 0
    depth = 0
    aim = 0
    
    for line in list:
        if line.startswith('forward '):
            value = int(line[8:])
            horizontal_position += value
            depth += aim * value
        elif line.startswith('down '):
            value = int(line[5:])
            aim += value
        elif line.startswith('up '):
            value = int(line[3:])
            aim -= value
    
    return horizontal_position * aim, horizontal_position * depth

if __name__ == '__main__':
    list = Utils.readAllLines('day2/input')
    one, two = partAll(list)
    
    print('Day 2')
    print('PartOne:', one)
    print('PartTwo:', two)