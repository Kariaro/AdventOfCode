import Utils as Utils

def mostCommon(list, index):
    return sum([ -1 if x[index] == '0' else 1 for x in list ]) >= 0

def partOne(list, bitsInLine):
    digits  = [ mostCommon(list, x) for x in range(bitsInLine) ]
    common  = ''.join([ '1' if x else '0' for x in digits ])
    epsilon = ''.join([ '0' if x else '1' for x in digits ])
    return int(common, 2) * int(epsilon, 2)

def partTwo(list, bitsInLine):
    oxy_list = list.copy()
    co2_list = list.copy()
    
    for x in range(bitsInLine):
        if len(oxy_list) > 1:
            oxy_digit = '1' if mostCommon(oxy_list, x) else '0'
            oxy_list = [ v for v in oxy_list if v[x] != oxy_digit ]

        if len(co2_list) > 1:
            co2_digit = '0' if mostCommon(co2_list, x) else '1'
            co2_list = [ v for v in co2_list if v[x] != co2_digit ]

    return int(oxy_list[0], 2) * int(co2_list[0], 2)

if __name__ == '__main__':
    list = Utils.readAllLines('day3/input')
    bitsInLine = len(list[0])
    
    print('Day 3')
    print('PartOne:', partOne(list, bitsInLine))
    print('PartTwo:', partTwo(list, bitsInLine))