import Utils as Utils

def processCount(line, rules, count):
    patterns = dict()
    chars = [ 0 ] * 26
    
    for i in range(len(line)):
        if i + 1 < len(line):
            key = line[i:i+2]
            patterns[key] = patterns.get(key, 0) + 1
        chars[ord(line[i]) - 65] += 1
    
    for i in range(count):
        nextPatterns = dict()
        for key, amount in patterns.items():
            ruleCharacter = rules[key]
            aa = key[0] + ruleCharacter
            bb = ruleCharacter + key[1]
            nextPatterns[aa] = nextPatterns.get(aa, 0) + amount
            nextPatterns[bb] = nextPatterns.get(bb, 0) + amount
            chars[ord(ruleCharacter) - 65] += amount
        patterns = nextPatterns
    
    return max(chars) - min(x for x in chars if x != 0)

@Utils.time_it_and_evaluate
def partOne(line, rules):
    return processCount(line, rules, 10)

@Utils.time_it_and_evaluate
def partTwo(line, rules):
    return processCount(line, rules, 40)

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day14')
    
    line = lines[0]
    rules = dict()
    for x in lines[2:]:
        key, val = x.split(' -> ')
        rules[key] = val
    
    print('Day 14')
    partOne(line, rules)
    partTwo(line, rules)