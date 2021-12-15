import Utils as Utils

def djikstra(board, width, height):
    nodes = [ [ None for col in range(width) ] for row in range(height) ]
    
    for i in range(height):
        for j in range(width):
            nodes[i][j] = Utils.Node(board[i][j])
    
    for i in range(height - 1):
        for j in range(width - 1):
            n00 = nodes[i    ][j    ]
            n10 = nodes[i + 1][j    ]
            n01 = nodes[i    ][j + 1]
            n11 = nodes[i + 1][j + 1]
            
            # Horizontal
            n00.connect_nodes(n10)
            n01.connect_nodes(n11)
            
            # Vertical
            n00.connect_nodes(n01)
            n10.connect_nodes(n11)
    
    return Utils.Djikstra.get_shortest_distance(nodes[0][0], nodes[-1][-1])

@Utils.time_it_and_evaluate
def partOne(board, width, height):
    return djikstra(board, width, height)

@Utils.time_it_and_evaluate
def partTwo(board, width, height):
    largeBoard = []
    
    for yy in range(5):
        for y in range(height):
            new_col = [ 0 ] * width * 5
            
            for xx in range(5):
                for x in range(width):
                    new_col[xx * width + x] = ((board[y][x] - 1 + xx + yy) % 9) + 1
            
            largeBoard.append(new_col);
    
    return djikstra(largeBoard, width * 5, height * 5)

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day15')
    width = len(lines[0])
    height = len(lines)
    board = [ [ int(i) for i in line ] for line in lines ]
    
    print('Day 15')
    partOne(board, width, height)
    partTwo(board, width, height)