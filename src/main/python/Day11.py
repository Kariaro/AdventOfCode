import Utils as Utils

def spread(lines, visited, x, y, width, height):
    index = x + y * width
    if index in visited:
        return
    visited.add(index)
    
    for i in [ 0, 1, 2, 3, 5, 6, 7, 8 ]:
        nx = x + (i % 3) - 1
        ny = y + (i // 3) - 1
        if nx >= 0 and nx < width and ny >= 0 and ny < height:
            lines[nx + ny * width] += 1
            if lines[nx + ny * width] > 9:
                spread(lines, visited, nx, ny, width, height)

def performSteps(lines, width, height):
    visited = set()
    
    # Add one to all elements in the array
    for i in range(width * height):
        lines[i] += 1
    
    # Then for all values that are greater than nine propagate the spread
    for i in range(width * height):
        if lines[i] > 9:
            spread(lines, visited, i % width, i // width, width, height)
    
    # All octopus that has flasheed will be set to zero
    for i in visited:
        lines[i] = 0
    
    return len(visited)

@Utils.time_it_and_evaluate
def partOne(lines, width, height):
    score = 0
    for i in range(100):
        score += performSteps(lines, width, height)
    return score

@Utils.time_it_and_evaluate
def partTwo(lines, width, height):
    index = 0
    while True:
        score = performSteps(lines, width, height)
        index += 1
        
        if score == len(lines):
            return index

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day11')
    values = [ int(x) for x in ''.join(lines) ]
    width = len(lines[0])
    height = len(lines)
    
    print('Day 11')
    partOne(values.copy(), width, height)
    partTwo(values.copy(), width, height)