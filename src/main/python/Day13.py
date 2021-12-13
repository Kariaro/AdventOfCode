import Utils as Utils

def foldPaper(paper, fold, maxX, maxY):
    xAxis, value = fold
    
    if xAxis:
        for x in range(value + 1):
            dx = 2 * value - x
            for y in range(maxY):
                paper[x + y * maxX] += paper[dx + y * maxX]
                paper[dx + y * maxX] = 0
    else:
        for y in range(value + 1):
            dy = 2 * value - y
            
            for x in range(maxX):
                paper[x + y * maxX] += paper[x + dy * maxX]
                paper[x + dy * maxX] = 0

def printBoard(paper, maxX):
    for y in range(0, len(paper), maxX):
        test = ':'
        for x in range(maxX):
            test += '#.' if paper[x + y] > 0 else '  '
        
        test = test.strip()[1:]
        if y > 10 and len(test) == 0:
            break
        
        print(test)
    print()

@Utils.time_it_and_evaluate
def partOne(paper, folds, maxX, maxY):
    foldPaper(paper, folds[0], maxX, maxY)
    return sum(1 for i in paper if i > 0)

@Utils.time_it_and_evaluate
def partTwo(paper, folds, maxX, maxY):
    for fold in folds:
        foldPaper(paper, fold, maxX, maxY)
    printBoard(paper, maxX)
    return 0

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day13')
    dots = []
    folds = []
    maxX = 0
    maxY = 0
    
    for line in lines:
        if line.startswith('fold along '):
            parts = line[11:].split('=')
            folds.append((parts[0] == 'x', int(parts[1])))
        elif len(line) > 0:
            parts = line.split(',')
            point = [ int(parts[0]), int(parts[1]) ]
            dots.append(point)
            
            maxX = max(maxX, point[0] + 1)
            maxY = max(maxY, point[1] + 1)
    
    board = [ 0 ] * (maxX * maxY)
    for x,y in dots:
        board[x + y * maxX] += 1
    
    print('Day 13')
    partOne(board.copy(), folds, maxX, maxY)
    partTwo(board.copy(), folds, maxX, maxY)