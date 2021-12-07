import Utils as Utils

def hasBingo(list, set):
    for i in range(5):
        vertical = True
        horizontal = True
        
        for j in range(5):
            vertical &= list[i + j * 5] in set
            horizontal &= list[j + i * 5] in set
        
        if vertical or horizontal:
            return True

    return False

def sumOfUnmarked(list, set):
    return sum([ x for x in list if not x in set ])

def partOne(random, boards):
    system = set()
    for x in random:
        system.add(x)
        for board in boards:
            if hasBingo(board, system):
                return sumOfUnmarked(board, system) * x

    return 0

def partTwo(random, boards):
    system = set()
    for x in random:
        system.add(x)
        last_boards = []
        last_win = []
        for board in boards:
            if hasBingo(board, system):
                last_win = board
            else:
                last_boards.append(board)
        
        boards = last_boards
        if len(boards) == 0:
            return sumOfUnmarked(last_win, system) * x

    return 0

if __name__ == '__main__':
    lines = Utils.readAllLines('day4/input')
    random = [ int(x) for x in lines[0].split(',') ]
    boards = [ [ int(x) for x in ' '.join(lines[i:i+6]).split(' ') if not x == '' ] for i in range(1, len(lines), 6) ]
    
    print('Day 4')
    print('PartOne:', partOne(random, boards))
    print('PartTwo:', partTwo(random, boards))