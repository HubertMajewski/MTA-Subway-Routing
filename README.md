# MTA-Subway-Routing
Utilizes JSoup to read the MTA's website. This program applies Dijkstra's Algorithm 
recursively to find the shortest path to travel between two given stations.

I did what I thought was an easy approach to make a graph, stored all of the 
vertecies in a hashset. Each vertex will have a hashmap containing the adjacent
vertex and the weight associated with it.

The algorithm must be recursive as there is no way to iterate through all of 
the edges of the graph, instead it recurses through every adjacent node and 
determines the shortest path from the given starting node. Origionally, if 
there is no path, the recursive function will return a null, however,
if the ind path unction is called from the graph class, the function will detect 
if the path is null and replace it with an empty linked list.

Reading the map into the graph data structure is convoluted as the MTA does 
not have a pure consistant way of storing subway stations for every line. Some
stations are listed in the 2ed cell while others in the 3rd. Other stations 
were stores in a different table, I fixed this by reading everything and ignore 
the empty strings.

This program also incorporates a simple Swing GUI which takes the input of two stations,
the start station, end station, and the result all have their own pages.
