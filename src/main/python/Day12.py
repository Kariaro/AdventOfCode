import Utils as Utils

def distinctRoutes(routes, path, smallCaves, pickedTwice, hasTwo):
    paths = 0
    
    for option in routes[path]:
        if option == 'end':
            paths += 1
            continue
        if option == 'start':
            continue
        if option.lower() == option:
            if option in smallCaves:
                if not hasTwo:
                    paths += distinctRoutes(routes, option, smallCaves, option, True)
            else:
                smallCaves.add(option)
                paths += distinctRoutes(routes, option, smallCaves, pickedTwice, hasTwo)
                smallCaves.remove(option)
        else:
            paths += distinctRoutes(routes, option, smallCaves, pickedTwice, hasTwo)
        
    return paths

@Utils.time_it_and_evaluate
def partOne(routes):
    return distinctRoutes(routes, 'start', set(), '', True)

@Utils.time_it_and_evaluate
def partTwo(routes):
    return distinctRoutes(routes, 'start', set(), '', False)

if __name__ == '__main__':
    lines = Utils.readAllLines(2021, 'day12')
    
    routes = dict()
    for line in lines:
        apart, bpart = line.split('-')
        a = routes.get(apart, set())
        b = routes.get(bpart, set())
        a.add(bpart)
        b.add(apart)
        routes[apart] = a
        routes[bpart] = b
    
    print('Day 12')
    partOne(routes)
    partTwo(routes)