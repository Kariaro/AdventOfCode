import Utils as Utils

def mostCommon(lines, index):
    return sum(-1 if x[index] == '0' else 1 for x in lines) >= 0

@Utils.time_it_and_evaluate
def partOne(lines, bitsInLine):
    digits  = [ mostCommon(lines, x) for x in range(bitsInLine) ]
    common  = ''.join('1' if x else '0' for x in digits)
    epsilon = ''.join('0' if x else '1' for x in digits)
    return int(common, 2) * int(epsilon, 2)

@Utils.time_it_and_evaluate
def partTwo(lines, bitsInLine):
    oxy_list = lines.copy()
    co2_list = lines.copy()
    
    for x in range(bitsInLine):
        if len(oxy_list) > 1:
            oxy_digit = '1' if mostCommon(oxy_list, x) else '0'
            oxy_list = [ v for v in oxy_list if v[x] != oxy_digit ]

        if len(co2_list) > 1:
            co2_digit = '0' if mostCommon(co2_list, x) else '1'
            co2_list = [ v for v in co2_list if v[x] != co2_digit ]

    return int(oxy_list[0], 2) * int(co2_list[0], 2)

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day3')
    bitsInLine = len(lines[0])
    
    print('Day 3')
    partOne(lines, bitsInLine)
    partTwo(lines, bitsInLine)