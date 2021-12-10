import Utils as Utils

CLOSE_CHAR = ( ')', ']', '}', '>' )
CLOSES = { '(': ')', '[': ']', '{': '}', '<': '>' }
FIRST_SCORE = { ')': 3, ']': 57, '}': 1197, '>': 25137 }
CLOSE_SCORE = { '(': 1, '[': 2, '{': 3, '<': 4 }

def getFirstScore(str):
    listStart = []
    for x in str:
        if x in CLOSE_CHAR:
            prev = listStart.pop()
            
            if CLOSES[prev] != x:
                return FIRST_SCORE[x]
        else:
            listStart.append(x)
    return 0

def getCloseScore(str):
    listStart = []
    for x in str:
        if x in CLOSE_CHAR:
            prev = listStart.pop()
            
            if CLOSES[prev] != x:
                return -1
        else:
            listStart.append(x)
    score = 0
    listStart.reverse()
    for x in listStart:
        score = score * 5 + CLOSE_SCORE[x]
    return score

@Utils.time_it_and_evaluate
def partOne(lines):
    score = 0
    for line in lines:
        score += getFirstScore(line)
    return score

@Utils.time_it_and_evaluate
def partTwo(lines):
    scores = []
    for line in lines:
        value = getCloseScore(line)
        if value > 0:
            scores.append(value)
    scores.sort()
    return scores[int(len(scores) / 2)]

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day10')
    
    print('Day 10')
    partOne(lines)
    partTwo(lines)