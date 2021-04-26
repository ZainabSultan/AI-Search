# AI-Search
Java implementation of search algorithms to help Ethan Hunt accomplish his mission.

# Situation:
Agent Ethan Hunt is stranded in Nova island (M x N grid where 5<=M,N<=15), his comrades are spread across the grid at different locations and are badly injured. The level of injury can be thought of as a number from 0-100, where an injury of 100 means death. Ethan must gather his comrades into his truck and head to the submarine. His first priority is minimising the number of deaths, his second priority is minimising the level of injury.

# Solving
Call the method solve(String grid, String strategy, boolean visualize)

Grid is a String in the form:
m,n; ex,ey; sx,sy;
x1,y1, ...,xk,yk;
h1,...,hk;
c

Where: 
* m and n represent the width and height of the grid respectively.
* ex and ey represent the x and y starting positions of Ethan.
* sx and sy represent the x and y positions of the submarine.
* xi,yi represent the x and y position of IMF member i where 1  i  k and
k is the total number of IMF members.
* hi represent the health of IMF member i where 1  i  k and k is the total
number of IMF members.
* c is the maximum number of members the truck can carry at a time.

strategy is a symbol indicating the search strategy to be applied:
* BF for breadth-first search,
* DF for depth-first search,
* ID for iterative deepening search,
* UC for uniform cost search,
* GRi for greedy search, with i 2 f1; 2g distinguishing the two heuristics, and
* ASi for A* search, with i 2 f1; 2g distinguishing the two heuristics.

visualize is a boolean parameter which, when set to true, results in
side-effecting a visual presentation of the grid as it undergoes the different
steps of the discovered solution (if one was discovered).

## Output

The function solve returns a String of the following format: plan;deaths;healths;nodes
where
– plan is a string representing the operators Ethan needs to follow separated by
commas. The possible operator names are: up, down, left, right, carry
and drop.
– deaths is a number representing the number of deaths in the found goal state.
– heaths is a string of the format h1,...,hk where hi is the health of IMF member
i in the found goal state.
– nodes is the number of nodes chosen for expansion during the search.


