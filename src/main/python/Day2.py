import Utils as Utils

def partAll(lines):
    horizontal_position = 0
    depth = 0
    aim = 0
    
    for line in lines:
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

@Utils.time_it_and_evaluate
def partOne(lines):
    return partAll(lines)[0]

@Utils.time_it_and_evaluate
def partTwo(lines):
    return partAll(lines)[1]

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day2')
    
    print('Day 2')
    partOne(lines)
    partTwo(lines)