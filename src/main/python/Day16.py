import Utils as Utils

def sum_packet_versions(packet):
    return packet['version'] + sum(sum_packet_versions(p) for p in packet['packets'])

def calculate_expression(packet):
    result = 0
    type_id = packet['type_id']
    result = packet['value'] if type_id == 4 else calculate_expression(packet['packets'][0])
    for x in range(1, len(packet['packets'])):
        value = calculate_expression(packet['packets'][x])
        match type_id:
            case 0: result += value
            case 1: result *= value
            case 2: result = min(result, value)
            case 3: result = max(result, value)
            case 5: result = 1 if result > value else 0
            case 6: result = 1 if result < value else 0
            case 7: result = 1 if result == value else 0
    return result

def read_n(packetIter, count):
    return ''.join(next(packetIter) for x in range(count))

def read_packet(packetIter):
    packet = dict()
    packet['version'] = int(read_n(packetIter, 3), 2)
    packet['type_id'] = int(read_n(packetIter, 3), 2)
    packets = []
    packet['packets'] = packets
    
    if packet['type_id'] == 4:
        sb = ''
        while True:
            pack = read_n(packetIter, 5)
            sb += pack[1:]
            if pack[0] == '0':
                packet['value'] = int(sb, 2)
                break
    else:
        if next(packetIter) == '0':
            length = int(read_n(packetIter, 15), 2)
            packetSubIter = iter(read_n(packetIter, length))
            while True:
                try:
                    packets.append(read_packet(packetSubIter))
                except:
                    break
        else:
            length = int(read_n(packetIter, 11), 2)
            for x in range(length):
                packets.append(read_packet(packetIter))
    return packet

def hex_to_bin(line):
    return ''.join(('%6s' % bin(int(x, 16))).replace('0b', '').replace(' ', '0') for x in line)

@Utils.time_it_and_evaluate
def partOne(packet):
    return sum_packet_versions(packet)

@Utils.time_it_and_evaluate
def partTwo(packet):
    return calculate_expression(packet)

if __name__ == '__main__':
    line = Utils.readAll(2021, 'day16').strip()
    packet = read_packet(iter(hex_to_bin(line)))
    
    print('Day 16')
    partOne(packet)
    partTwo(packet)