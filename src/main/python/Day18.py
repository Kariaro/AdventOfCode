import Utils as Utils

class Snailfish:
    def __init__(self, left=None, right=None, value=0):
        self.value = value
        self.left = left
        self.right = right
        self.parent = None
        
        if left:
            left.parent = self
        if right:
            right.parent = self
    
    def index(self):
        return (0 if self.parent.left == self else 1) if self.parent != None else 0
    
    def is_number(self):
        return self.left == None
    
    def set(self, idx, fish):
        fish.parent = self
        if idx == 0:
            self.left = fish
        else:
            self.right = fish
    
    def get(self, idx):
        return self.left if idx == 0 else self.right
    
    def __str__(self):
        return str(self.value) if self.is_number() else ('[%s,%s]' % (self.left, self.right))

def getSnailfish(line):
    values = []
    
    for c in line:
        if c == '[' or c == ',':
            continue
        if c == ']':
            b = values.pop()
            a = values.pop()
            values.append(Snailfish(a, b))
        else:
            values.append(Snailfish(value=int(c)))
    
    return values[0]

def findSnailfish(fish, dir):
    current = fish
    while current.index() == dir:
        current = current.parent
        if current.parent == None:
            return None
    
    current = current.parent.get(dir)
    if current.is_number():
        return current
    
    current = current.parent.get(dir)
    while not current.is_number():
        current = current.get(1 - dir)
    
    return current

def calculate(fish):
    return fish.value if fish.is_number() else (calculate(fish.left) * 3 + calculate(fish.right) * 2)

def reduceSnailfishRoot(fish):
    while reduceSnailfish(fish, 0, True) or reduceSnailfish(fish, 0, False):
        pass
    return fish

def reduceSnailfish(fish, depth, explode):
    if not fish.is_number():
        if reduceSnailfish(fish.left, depth + 1, explode) or reduceSnailfish(fish.right, depth + 1, explode):
            return True
    else:
        if not explode and fish.value > 9:
            al = fish.value // 2
            ar = fish.value - al
            fish.parent.set(fish.index(), Snailfish(Snailfish(value=al), Snailfish(value=ar)))
            return True
        return False
    
    if depth >= 4:
        left = findSnailfish(fish, 0)
        if left:
            left.value += fish.get(0).value
        
        right = findSnailfish(fish, 1)
        if right:
            right.value += fish.get(1).value
        
        fish.parent.set(fish.index(), Snailfish())
        return True
    return False

@Utils.time_it_and_evaluate
def partOne(lines):
    fish_sum = None
    for x in lines:
        fish = getSnailfish(x)
        fish_sum = fish if fish_sum == None else reduceSnailfishRoot(Snailfish(fish_sum, fish))
    return calculate(fish_sum)

@Utils.time_it_and_evaluate
def partTwo(lines):
    result = 0
    
    for a in lines:
        for b in lines:
            if a == b:
                continue
            
            result = max(
                result,
                calculate(reduceSnailfishRoot(Snailfish(getSnailfish(a), getSnailfish(b)))),
                calculate(reduceSnailfishRoot(Snailfish(getSnailfish(b), getSnailfish(a))))
            )
    return result

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day18')
    
    print('Day 18')
    partOne(lines)
    partTwo(lines)