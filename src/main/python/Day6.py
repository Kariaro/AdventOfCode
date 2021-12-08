import Utils as Utils

def calculateLanternFish(lines, days):
    for i in range(days):
        amount = lines[0]
        lines = lines[1:]
        lines[6] += amount
        lines.append(amount)
    return sum(lines)

@Utils.time_it_and_evaluate
def partOne(lines):
    return calculateLanternFish(lines.copy(), 80)

@Utils.time_it_and_evaluate
def partTwo(lines):
    return calculateLanternFish(lines.copy(), 256)

if __name__ == '__main__':
    lines = [ int(x) for x in Utils.readAll(2021, 'day6').rstrip().split(',') ]
    countList = [ 0 ] * 9
    for x in lines:
        countList[x] += 1

    print('Day 6')
    partOne(countList)
    partTwo(countList)