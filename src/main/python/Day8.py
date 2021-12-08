import Utils as Utils

def getMatch(lines, digits):
    return [ str for str in lines if digits.issubset(set(str)) ][0]

def figureOutMapping(lines):
    onePattern = ''
    fourPattern = ''
    sevenPattern = ''
    eightPattern = ''
    
    for str in lines:
        strLen = len(str)
        if strLen == 2:
            onePattern = str
        elif strLen == 3:
            sevenPattern = str
        elif strLen == 4:
            fourPattern = str
        elif strLen == 7:
            eightPattern = str
    
    # Remove previous patterns
    lines.remove(onePattern)
    lines.remove(fourPattern)
    lines.remove(sevenPattern)
    lines.remove(eightPattern)
    
    # If it matches Four we found Nine
    ninePattern = getMatch(lines, set(fourPattern))
    lines.remove(ninePattern)
    
    # If it matches Eight minus One we found Six
    sixPattern = getMatch(lines, set(eightPattern) - set(onePattern))
    lines.remove(sixPattern)
    
    # If it has 6 length we found Zero
    zeroPattern = [ str for str in lines if len(str) == 6 ][0]
    lines.remove(zeroPattern)
    
    # If it matches Seven we found Three
    threePattern = getMatch(lines, set(sevenPattern))
    lines.remove(threePattern)
    
    # If it matches Four minus One we found Five
    fivePattern = getMatch(lines, set(fourPattern) - set(onePattern))
    lines.remove(fivePattern)
    
    # Last element is Two
    twoPattern = lines[0]
    
    return [ set(x) for x in [ zeroPattern, onePattern, twoPattern, threePattern, fourPattern, fivePattern, sixPattern, sevenPattern, eightPattern, ninePattern ] ]

@Utils.time_it_and_evaluate
def partOne(lines):
    match = { 2, 3, 4, 7 }
    return sum(sum(1 for x in [ len(str) for str in line.split(' | ')[1].split(' ') ] if x in match) for line in lines)

@Utils.time_it_and_evaluate
def partTwo(lines):
    total = 0
    for line in lines:
        parts = line.split(' | ')
        map = figureOutMapping(parts[0].split(' '))
        
        result = 0
        for str in parts[1].split(' '):
            strSet = set(str)
            for i in range(10):
                if map[i] == strSet:
                    result = result * 10 + i
                    break
        
        total += result
    return total

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day8')
    
    print('Day 8')
    partOne(lines)
    partTwo(lines)