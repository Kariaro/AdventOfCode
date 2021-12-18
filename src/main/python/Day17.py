import Utils as Utils

def isInside(x, y, area):
    x_min, y_min, x_max, y_max = area
    return x >= x_min and x < x_max and y >= y_min and y < y_max

def hitTarget(vx, vy, area):
    x_min, y_min, x_max, y_max = area
    x = 0
    y = 0
    ly = 0
    while True:
        if isInside(x, y, area):
            return ly
        
        if y < y_min or x > x_max:
            return -1
        
        x += vx
        y += vy
        ly = max(ly, y)
        vx -= 1
        vy -= 1
        if vx < 0:
            vx = 0

@Utils.time_it_and_evaluate
def partOne(area):
    largest_y = 0
    for y in range(-100, 100):
        for x in range(area[2]):
            largest_y = max(largest_y, hitTarget(x, y, area))
    return largest_y

@Utils.time_it_and_evaluate
def partTwo(area):
    times = 0
    for y in range(-100, 100):
        for x in range(area[2]):
            times += 1 if hitTarget(x, y, area) >= 0 else 0
    return times

if __name__ == '__main__':
    line = Utils.readAll(2021, 'day17').strip()
    parts = line[13:].split(', ')
    xparts = parts[0][2:].split("..")
    yparts = parts[1][2:].split("..")
    area = (int(xparts[0]), int(yparts[0]), int(xparts[1]) + 1, int(yparts[1]) + 1)
    
    print('Day 17')
    partOne(area)
    partTwo(area)