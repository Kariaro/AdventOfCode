import Utils as Utils

def calculateLanternFish(list, days):
    for i in range(days):
        amount = list[0]
        list = list[1:]
        list[6] += amount
        list.append(amount)
    return sum(list)

def partOne(list):
    return calculateLanternFish(list.copy(), 80)

def partTwo(list):
    return calculateLanternFish(list.copy(), 256)

if __name__ == '__main__':
    lines = [ int(x) for x in Utils.readAll('day6/input').rstrip().split(',') ]
    list = [ 0 ] * 9
    for x in lines:
        list[x] += 1

    print('Day 6')
    print('PartOne:', partOne(list))
    print('PartTwo:', partTwo(list))