****INPUT****
To represent a puzzle, we use 2D array in which 0 means a non-occupied node and non-zero numbers are start point or end point of the path.
As for complete graph, we use its adjacent matrix and a List of two-number lists as parameter for functions.

To input test suite, we can write it in file .\puzzle.txt in form of:
0
0 0 1
1 2 0
2 0 0
1
0 0 0 4 0 0 0
0 3 0 0 2 5 0
0 0 0 3 1 0 0
0 0 0 5 0 0 0
0 0 0 0 0 0 0
0 0 1 0 0 0 0
2 0 0 0 4 0 0
where the first zero is puzzle's order and the next matrix is puzzle to put in.

Then, we call the function readArrayFromFile() of Test.java in package SAT. The function returns a LinkedList of 2D array, then we can begin to test.

****RULE OF INDEX****

In some cases, we use only index of nodes. To transform a node in 2D array, we follow the rule : the index of a node in i-th row and j-th column is (i-1)*(column number)+j-1.
For example in the matrix above, the 1 on the top right corner is of index 2 because it's in the first row and thrid column.

***Part 1:***

For Task1.java, we only offer one possibility of parameters, which is the graph's adjacent matrix and a list of list contains information of start point and end point. For example, to input the matrix mentionned above,
we can input its adjacent matrix (a 9*9 matrix) and a list {{2,3},{4,7}}, because 2 is index of start point (on the top right corner) of path 1, and 3 is the index of end point (on the left) of path 1. 4 is index of start point (in the middle) of path 2,
and 7 is index of end point (on the bottom left corner) of path 2.

The function to calculate SAT solver is task1(int[][] map, List<LinkedList<Integer>> l).


As for Taskx (x=2 or x=3), we offer two kinds of constructor for each. One is for normal 2D array to present a puzzle, just like the matrix above, and another one is for general graph.

The constructor of form Taskx(int[][] origMap) accept 2D array, and the other one Taskx(int[][] adjaMatrix, HashMap<Integer, LinkedList<Integer>> pairM)  accept an adjacent matrix as the first parameter and a hash map of list of start and end point as the second paramter.

For example, for the first 3*3 puzzle, the hashmap is {1={2,3},2={4,7}}.

To calculate solver , we use function taskx(), where x=2 or 3.

In Task4.java, only 2D array can be accepted. Use function task4(int[][] origMap) to calculate a solver.

In Task5.java, we can calculate all solution by allSolution(int[][] origMap,boolean print,int maxSolution), where origMap is 2D array of puzzle. If print is true, then it will print the result. If there are more solution than maxSolution, then it will return and stop calculating more function.

In Test.java, we offer some cases written in allCases_GridGraph initialized by function initCasesGridGraph(), and also test functions.

***Part 2:***

The main classes are NBLK, NBLK1 and CreatePuzzle. In NBLK.java, we implements a recursive method to find solutions and in NBLK1.java, we try a iterative method.

NBLK.java and NBLK1.java:

We can build the constructor by 2D array of puzzle or for a general graph, its adjacent matrix , a hashmap for start points and a hashmap for end points.

The function solve() solves the puzzle can if the puzzle is solvable, it returns true, otherwise false. The function count(int k,boolean print) calculates if there are more than k solutions and prints it if print=true.

The function print() returns the final state and we can use Print2DArray to print it.

In CreatePuzzle.java, we can use CreatePuzzle(int[][] origMap,int nrow,int ncol) to create a puzzle in the first nrow rows and ncol columns of origMap. Note that -1 represents a node that can't be occupied, so we can change origMap to create forms we want.

Also, CreatePuzzle(int[][] adjaMatrix,int l) is used for adjacent matrix of a graph. By changing l, we can limit the number of graph to be calculate.


	