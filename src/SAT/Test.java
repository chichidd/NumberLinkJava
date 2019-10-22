package SAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Test {


	public static LinkedList<int[][]> allCases_GridGraph;

	public static void initCasesGridGraph() {
		
		allCases_GridGraph=new LinkedList<int[][]>();
		int[][] a=new int[3][3]; 
		a[0][0] = a[1][2] = 1;
		a[2][0] = a[2][2] = 2;
		allCases_GridGraph.add(a);	
		a=new int[5][5];
		a[2][4] = a[4][1] = 1;
		a[1][1] = a[1][4] = 2;
		a[3][1] = a[2][3] = 3;
		a[0][4] = a[4][0] = 4;
		allCases_GridGraph.add(a);	
		a=new int[7][7];
		a[2][4] = a[5][2] = 1;
		a[1][4] = a[6][0] = 2;
		a[0][3] = a[6][6] = 4;
		a[1][1] = a[2][3] = 3;
		a[1][5] = a[3][3] = 5;
		allCases_GridGraph.add(a);
		a=new int[10][10];
		a[0][0]=a[0][2]=1;
		a[0][1]=a[5][1]=2;
		a[0][3]=a[9][9]=3;
		a[7][4]=a[8][9]=4;
		a[1][5]=a[8][8]=5;
		a[3][6]=a[8][1]=6;
		a[7][5]=a[8][7]=7;
		allCases_GridGraph.add(a);

		a=new int[][]{{5,6,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,6},{7,0,0,7,0,0,0,0,3,0,0},{0,0,0,2,0,8,0,0,0,8,0},{0,3,0,0,0,5,0,0,0,0,0},{0,0,4,0,0,0,0,0,0,4,9},{1,0,1,9,0,10,0,0,0,11,0},{0,0,0,12,0,0,0,13,0,0,0},{0,0,0,0,0,0,0,0,0,0,0},{0,2,0,0,0,10,0,0,0,11,0},{0,0,0,12,0,0,0,13,0,0,0}};
		allCases_GridGraph.add(a);
		a=new int[][] {{0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0},{0,0,1,2,3,4,0,0,0,5,0,0},{0,0,0,0,0,0,0,6,0,6,0,0},{0,0,0,0,0,0,0,0,0,7,0,0},{0,0,0,0,0,0,2,0,0,8,0,0},{0,0,4,0,0,9,0,0,0,0,0,0},{0,0,8,0,0,0,0,0,0,0,0,0},{0,0,10,0,3,0,0,0,0,0,0,0},{0,0,1,0,0,0,10,5,9,7,0,0},{0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0}};
		allCases_GridGraph.add(a);
		a=new int[10][18];
		a[0][0]=a[4][15]=1;
		a[1][2]=a[9][17]=2;
		a[1][4]=a[3][1]=3;
		a[8][15]=a[1][12]=4;
		a[1][16]=a[6][12]=5;
		a[7][3]=a[2][4]=6;
		a[2][8]=a[5][3]=7;
		a[2][9]=a[5][2]=8;
		a[2][14]=a[6][5]=9;
		a[3][5]=a[3][12]=10;
		a[4][14]=a[7][9]=11;
		a[8][11]=a[6][16]=12;
		a[8][1]=a[7][8]=13;
		a[8][5]=a[7][13]=14;
		allCases_GridGraph.add(a);
		//This one is too difficult.
		//a=new int[][]{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,11,12,0,0,0,0,0,0,0,0,0,0,0,0,20,0,0,0,0,0,0,0,0},{0,0,0,0,0,13,14,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,12,0,0,0,0,0,10,0,0,0,8,0,0,21,0,0,0,0,22,0,0,0},{0,0,0,0,0,0,11,0,0,0,1,0,0,5,0,0,22,0,0,0,0,0,0,0},{0,13,0,0,0,0,0,0,0,2,0,0,0,0,2,0,0,0,6,0,0,0,0,0},{10,9,0,0,0,0,0,9,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,21,0,0,0,0,0,14,20},{0,0,0,0,0,15,0,0,0,4,0,0,0,0,6,0,0,0,0,0,0,0,18,0},{0,0,0,0,0,0,0,17,0,0,1,0,0,5,0,0,0,8,0,0,0,0,0,0},{0,0,0,16,0,0,0,0,16,0,0,3,0,0,0,7,0,0,0,0,0,19,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,17,4,0,0,0,0,0},{0,0,0,0,0,0,0,0,15,0,0,0,0,0,0,0,0,0,0,0,0,19,18,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
		//allCases_GridGraph.add(a);
	}

	public static void main(String[] args) {
		

		
		/*****************TEST SESSION 1**********Grid Graph****************/
		//use default test cases
		initCasesGridGraph();
		//use custom test cases
		//allCases_GridGraph= readArrayFromFile();
		//writeArray2File();
	/*
		//Test for method of Task4 
		for (int[][] map : allCases_GridGraph) {
			int n=allCases_GridGraph.indexOf(map);
			System.out.println("\ncase "+n+" :");
			System.out.println("puzzle size : " + map.length+" * "+map[0].length +".");
			Print2DArray(map);
			System.out.println("**********************\nTest 4:");
			long t1=System.currentTimeMillis();
			long tt=test4(map,false);
			long t2=System.currentTimeMillis();
			System.out.println("**********************\nTotal time:"+(t2-t1)+"ms, transformation time:"+tt+"ms.\n");
		}
		
		// Test for method of Task3
		for (int[][] map : allCases_GridGraph) {
			int n = allCases_GridGraph.indexOf(map);
			System.out.println("\ncase " + n + " :");
			System.out.println("puzzle size : " + map.length+" * "+map[0].length +".");
			Print2DArray(map);
			System.out.println("**********************\nTest 3:");
			long t1 = System.currentTimeMillis();
			long tt = test3(map,false);
			long t2 = System.currentTimeMillis();
			System.out.println(
					"**********************\nTotal time:" + (t2 - t1) + "ms, transformation time:" + tt + "ms.\n");
		}
		 */
		/*	
		// Test for method of Task2
				for (int[][] map : allCases_GridGraph) {
					int n = allCases_GridGraph.indexOf(map);
					System.out.println("\ncase " + n + " :");
					System.out.println("puzzle size : " + map.length+" * "+map[0].length +".");
					Print2DArray(map);
					System.out.println("**********************\nTest 2:");
					long t1 = System.currentTimeMillis();
					long tt = test2(map,false);
					long t2 = System.currentTimeMillis();
					System.out.println(
							"**********************\nTotal time:" + (t2 - t1) + "ms, transformation time:" + tt + "ms.\n");
				}

				*/
		//Test for method of Task1 
		for (int[][] map : allCases_GridGraph) 
		if(map.length<10){
			
			int n=allCases_GridGraph.indexOf(map);
			System.out.println("\ncase "+n+" :");
			System.out.println("puzzle size : " + map.length+" * "+map[0].length +".");
			Print2DArray(map);
			System.out.println("**********************\nTest 1:");
			long t1=System.currentTimeMillis();
			long tt=test1(map,true);
			long t2=System.currentTimeMillis();
			System.out.println("**********************\nTotal time:"+(t2-t1)+"ms, transformation time:"+tt+"ms.\n");
		}
		
		
				
		/*****************TEST SESSION 2************Complete Graph**************/
		/*
		// Test for method of Task3
		for (int[][] map : allCases_GridGraph) {
			int n = allCases_GridGraph.indexOf(map);
			System.out.println("\ncase " + n + " :");
			System.out.println("Graph contains " + map.length *map[0].length + " nodes.");
			System.out.println("**********************\nTest 3: Complete Graph");
			long t1 = System.currentTimeMillis();
			long tt = test3CompleteGraph(map);
			long t2 = System.currentTimeMillis();
			System.out.println("**********************\nTotal time:" + (t2 - t1) + "ms, transformation time:" + tt + "ms.\n");
		}
	
		// Test for method of Task2
		for (int[][] map : allCases_GridGraph) {
			int n = allCases_GridGraph.indexOf(map);
			System.out.println("\ncase " + n + " :");
			System.out.println("Graph contains " + map.length *map[0].length + " nodes.");
			System.out.println("**********************\nTest 2: Complete Graph");
			long t1 = System.currentTimeMillis();
			long tt = test2CompleteGraph(map);
			long t2 = System.currentTimeMillis();
			System.out.println(
					"**********************\nTotal time:" + (t2 - t1) + "ms, transformation time:" + tt + "ms.\n");
		}
*/
		// Test for method of Task1
		for (int[][] map : allCases_GridGraph)
			if (map.length < 10) {

				int n = allCases_GridGraph.indexOf(map);
				System.out.println("\ncase " + n + " :");
				System.out.println("Graph contains " + map.length *map[0].length + " nodes.");
				System.out.println("**********************\nTest 1: Complete Graph");
				long t1 = System.currentTimeMillis();
				long tt = test1(map, false);
				long t2 = System.currentTimeMillis();
				System.out.println(
						"**********************\nTotal time:" + (t2 - t1) + "ms, transformation time:" + tt + "ms.\n");
			}
		
	
	}

	public static long test1(int[][] origMap,boolean print) {

		Task1 t = new Task1();
		List<LinkedList<Integer>> l = new LinkedList<LinkedList<Integer>>();
		HashMap<Integer, LinkedList<Integer>> bijection_map = new HashMap<Integer, LinkedList<Integer>>();
		int[][] map = transform(origMap, l, bijection_map);
		//bijection_map tells the index of row and index of column when we give the index of point, not necessary in some case
		int[] solution = null;
		// Create the solver
		long t1=System.currentTimeMillis();
		ISolver solver = t.task1(map, l);
		long t2=System.currentTimeMillis();
		// Feed the solver using arrays of int in Dimacs format

		// Print parameters of the problem
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {
				System.out.println("Satisfiable problem!");
				if (print) {
					solution = solver.model();
					int[][] res = origMap.clone();
					for (int i = 0; i < solution.length; i++) {
						Varia var_temp = t.inverse_solmap.get(Math.abs(solution[i]));
						if (solution[i] > 0 && var_temp.v < map.length) {
							LinkedList<Integer> l_temp = bijection_map.get(var_temp.v);
							int row_p = l_temp.get(0);
							int col_p = l_temp.get(1);
							if (res[row_p][col_p] == 0)
								res[row_p][col_p] = var_temp.i;
						}
					}
					Print2DArray(res);
				}
			} else {

				System.out.println("Unsatisfiable problem!");

			}
		} catch (TimeoutException e) {

			System.out.println("Timeout, sorry!");
		}
		return t2-t1;
	}
	
	//help function for test1()
	public static int[][] transform(int[][] originalMap, List<LinkedList<Integer>> list,
			HashMap<Integer, LinkedList<Integer>> bijection_map) {

		int n_row = originalMap.length;
		int n_col = originalMap[0].length;
		int l = n_row * n_col;
		int[][] res = new int[l][l];
		HashMap<Integer, LinkedList<Integer>> hmap = new HashMap<Integer, LinkedList<Integer>>();

		for (int i = 0; i < n_row; i++) {
			for (int j = 0; j < n_col; j++) {
				int index = i * n_col + j;
				LinkedList<Integer> l_temp0 = new LinkedList<Integer>();
				l_temp0.addFirst(i);
				l_temp0.addLast(j);
				if(bijection_map!=null)
					bijection_map.put(index, l_temp0);

				if (i > 0) {
					res[index][(i - 1) * n_col + j] = 1;
					res[(i - 1) * n_col + j][index] = 1;
				}
				if (i < n_row - 1) {
					res[index][(i + 1) * n_col + j] = 1;
					res[(i + 1) * n_col + j][index] = 1;
				}
				if (j > 0) {
					res[index][i * n_col + j - 1] = 1;
					res[i * n_col + j - 1][index] = 1;
				}
				if (j < n_col - 1) {
					res[index][i * n_col + j + 1] = 1;
					res[i * n_col + j + 1][index] = 1;
				}

				if (originalMap[i][j] > 0) {
					LinkedList<Integer> l_temp = null;
					if (!hmap.containsKey(originalMap[i][j])) {
						l_temp = new LinkedList<Integer>();
						l_temp.add(index);
						hmap.put(originalMap[i][j], l_temp);
					} else {
						l_temp = hmap.get(originalMap[i][j]);
						l_temp.add(index);
						hmap.replace(originalMap[i][j], l_temp);
					}
				}

			}
		}

		for (LinkedList<Integer> liste : hmap.values()) {
			list.add(liste);
		}
		return res;
	}

	public static long test1CompleteGraph(int[][] map,List<LinkedList<Integer>> l) {
		Task1 t = new Task1();
		
		// Create the solver
		long t1=System.currentTimeMillis();
		ISolver solver = t.task1(map, l);
		long t2=System.currentTimeMillis();
		// Feed the solver using arrays of int in Dimacs format

		// Print parameters of the problem
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {
				
				System.out.println("Satisfiable problem!");
			} else {

				System.out.println("Unsatisfiable problem!");

			}
		} catch (TimeoutException e) {

			System.out.println("Timeout, sorry!");
		}
		return t2-t1;
	}
	
	public static long test2CompleteGraph(int[][] origMap) {
		
		HashMap<Integer,LinkedList<Integer>> pM=new HashMap<Integer,LinkedList<Integer>>();
		int[][] aM=completeGraph(origMap,pM);
		Task2 t=new Task2(aM,pM);
		long t1=System.currentTimeMillis();
		t.task2();
		ISolver solver=t.solver;
		long t2=System.currentTimeMillis();
		// Print parameters of the problem
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {
				
				System.out.println("Satisfiable problem!");
			} else {

				System.out.println("Unsatisfiable problem!");

			}
		} catch (TimeoutException e) {

			System.out.println("Timeout, sorry!");
		}
		
		return t2-t1;
	}
	
	
	public static long test3CompleteGraph(int[][] origMap) {

		HashMap<Integer, LinkedList<Integer>> pM = new HashMap<Integer, LinkedList<Integer>>();
		int[][] aM = completeGraph(origMap, pM);
		Task3 t = new Task3(aM, pM);
		long t1 = System.currentTimeMillis();
		t.task3();
		ISolver solver = t.solver;
		long t2 = System.currentTimeMillis();
		// Print parameters of the problem
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {

				System.out.println("Satisfiable problem!");
			} else {

				System.out.println("Unsatisfiable problem!");

			}
		} catch (TimeoutException e) {

			System.out.println("Timeout, sorry!");
		}

		return t2 - t1;
	}
	
	
	public static long test2(int[][] origMap,boolean print) {
		Task2 t = new Task2(origMap);
		int[] solution = null;
		int[][] res = origMap.clone();
		// Create the solver
		ISolver solver = SolverFactory.newDefault();
		long t1=System.currentTimeMillis();
		solver = t.task2();
		long t2=System.currentTimeMillis();
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {
				System.out.println("Satisfiable problem!");
				if (print) {
					solution = solver.model();

					for (int i = 0; i < solution.length; i++) {
						if (solution[i] > 0) {
							VariaEdge var = t.varEdgeMap.get(solution[i]);
							Edge e = var.edge;
							int path = var.i;
							Vertex v1 = e.vertex1;
							Vertex v2 = e.vertex2;
							res[v1.row_index][v1.col_index] = path;
							res[v2.row_index][v2.col_index] = path;
						}
					}
					Print2DArray(res);
				}
			} else {
				System.out.println("Unsatisfiable problem!");
			}
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
		}
		return t2-t1;
	}
	
	public static long test3(int[][] origMap,boolean print) {
		Task3 t = new Task3(origMap);
		int[] solution = null;
		int[][] res = origMap.clone();
		// Create the solver
		ISolver solver = SolverFactory.newDefault();
		long t1=System.currentTimeMillis();
		solver = t.task3();
		long t2=System.currentTimeMillis();
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {
				System.out.println("Satisfiable problem!");
				if (print) {
					solution = solver.model();

					for (int i = 0; i < solution.length; i++) {
						if (solution[i] > 0) {
							VariaEdge var = t.varEdgeMap.get(solution[i]);
							Edge e = var.edge;
							int path = var.i;
							Vertex v1 = e.vertex1;
							Vertex v2 = e.vertex2;
							res[v1.row_index][v1.col_index] = path;
							res[v2.row_index][v2.col_index] = path;
						}
					}
					Print2DArray(res);
				}
			} else {
				System.out.println("Unsatisfiable problem!");
			}
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
		}
		return t2-t1;
	}

	public static long test4(int[][] origMap,boolean print) {
		Task4 t = new Task4();
		int[] solution = null;
		int col = origMap[0].length;
		int row = origMap.length;
		int[][] res = new int[2 * row + 1][2 * col + 1];
		// Create the solver
		ISolver solver = SolverFactory.newDefault();
		long t1=System.currentTimeMillis();
		solver = t.task4(origMap);
		long t2=System.currentTimeMillis();
		System.out.println("Number of variables: " + solver.nVars());
		System.out.println("Number of constraints: " + solver.nConstraints());
		// Solve the problem
		try {
			if (solver.isSatisfiable()) {
				System.out.println("Satisfiable problem!");
				if (print) {
					solution = solver.model();
					HashMap<Integer, VariaVertex> vMI = Task4.varMapInverse;
					for (int i = 0; i < solution.length; i++) {

						if (solution[i] > 0) {
							VariaVertex vv = vMI.get(solution[i]);
							int path = vv.path;
							String sens = vv.shape;
							int rown = vv.vertice.row_index;
							int coln = vv.vertice.col_index;
							res[rown * 2 + 1][coln * 2 + 1] = path;
							switch (sens) {
							case "LR":
								res[rown * 2 + 1][coln * 2] = -path;
								res[rown * 2 + 1][coln * 2 + 2] = -path;
								break;
							case "RB":
								res[rown * 2 + 1][coln * 2 + 2] = -path;
								res[rown * 2 + 2][coln * 2 + 1] = -path;
								break;
							case "RT":
								res[rown * 2][coln * 2 + 1] = -path;
								res[rown * 2 + 1][coln * 2 + 2] = -path;
								break;

							case "LB":
								res[rown * 2 + 1][coln * 2] = -path;
								res[rown * 2 + 2][coln * 2 + 1] = -path;
								break;
							case "LT":
								res[rown * 2 + 1][coln * 2] = -path;
								res[rown * 2][coln * 2 + 1] = -path;
								break;
							case "TB":
								res[rown * 2][coln * 2 + 1] = -path;
								res[rown * 2 + 2][coln * 2 + 1] = -path;
								break;
							}
						}
					}

					Print2DArray(res);
				}
			} else {
				System.out.println("Unsatisfiable problem!");
			}
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
		}
		return t2-t1;
	}

	public static void Print2DArray(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				System.out.print(a[i][j]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	//read an origMap and transform it into a complete graph with same start point and end point.
	public static int[][] completeGraph(int[][] origMap,HashMap<Integer, LinkedList<Integer>> list) {
		int length=origMap.length*origMap[0].length;
		int[][] res= new int[length][length];
		for(int i=0;i!=length;i++)
			for(int j=0;j!=length;j++) {
				if(i!=j)
					res[i][j]=1;
			}
		for(int i=0;i!=origMap.length;i++) {
			for(int j=0;j!=origMap[0].length;j++)
			{
				if(origMap[i][j]>0) {
					int index=i*origMap[0].length+j;
					if(list.containsKey(origMap[i][j])) {
						LinkedList<Integer> l=list.get(origMap[i][j]);
						l.add(index);
						
					}else {
						LinkedList<Integer> l=new LinkedList<Integer>();
						l.add(index);
						list.put(origMap[i][j], l);
					}
				}
			}
		}
		return res;
	}

	
	//write the puzzle array into file ./puzzle.txt
	public static void writeArray2File() {
		try {
			File writename = new File(".\\puzzles.txt"); 
			writename.createNewFile(); 
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			for (int[][] map : allCases_GridGraph) {
				out.write(allCases_GridGraph.indexOf(map) + "\r\n");
				// be careful in different system \r\n may be changed to \n or \r?
				for (int i = 0; i != map.length; i++) {
					for (int j = 0; j != map[0].length; j++) {
						out.write(map[i][j] + "\t");
					}
					out.write("\r\n");
				}
			}
			out.flush(); 
			out.close(); 

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	//read puzzles from file ./puzzle.txt.
	public static LinkedList<int[][]> readArrayFromFile() {
		LinkedList<int[][]> res=new LinkedList<int[][]>();
		try { 
			String pathname = ".\\puzzles.txt"; 
			File filename = new File(pathname); 
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); 
			BufferedReader br = new BufferedReader(reader); 
			String line = "";
			line = br.readLine();
			while (line != null) {
				
				if (line.length() == 1) {
					// start reading a new puzzle
					LinkedList<int[]> temp = new LinkedList<int[]>();
					line = br.readLine();
					while (line!=null && line.length() != 1 ) {

						String[] oneRowNumberstr = line.split("\t");
						int[] oneRowNumber = new int[oneRowNumberstr.length];
						for (int i = 0; i != oneRowNumber.length; i++) {
							oneRowNumber[i]=Integer.valueOf(oneRowNumberstr[i]);
						}
						
						temp.add(oneRowNumber);
						line = br.readLine();
					}
					
					int[][] thePuzzle=new int[temp.size()][temp.get(0).length];
					for(int i=0;i!=temp.size();i++) {						
							thePuzzle[i]=temp.get(i);
					}
					res.add(thePuzzle);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
