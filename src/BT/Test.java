package BT;

import java.util.HashMap;
import java.util.LinkedList;

public class Test {

	public static LinkedList<int[][]> allCases_GridGraph;
	
	public static LinkedList<int[][]> allCases_CompleteGraph;
	
	public static void main(String[] args) {
		allCases_GridGraph = SAT.Test.readArrayFromFile();
		
		/*********************
		 * TEST SESSION*****Grid Graph
		 *****************************/
		
		// Recursive method
		for (int[][] map : allCases_GridGraph) {

			if (allCases_GridGraph.indexOf(map) < 3) {
				System.out.println("\ncase " + allCases_GridGraph.indexOf(map) + " :");
				// NBLK.Print2DArray(map);
				System.out.println("****************************");
				long t1 = System.currentTimeMillis();
				testBT(map, false);
				long t2 = System.currentTimeMillis();
				System.out.println("Total time : " + (t2 - t1) + " ms.\n");
			}
		}

		// Iterative method
		for (int[][] map : allCases_GridGraph) {

			if (allCases_GridGraph.indexOf(map) < 5) {
				System.out.println("\ncase " + allCases_GridGraph.indexOf(map) + " :");
				// NBLK.Print2DArray(map);
				System.out.println("****************************");
				long t1 = System.currentTimeMillis();
				testBT1(map, false);
				long t2 = System.currentTimeMillis();
				System.out.println("Total time : " + (t2 - t1) + " ms.\n");
			}
		}
		
		/*********************
		 * TEST SESSION*****Complete Graph
		 *****************************/
		// Recursive method
		for (int[][] map : allCases_GridGraph) {

			if (allCases_GridGraph.indexOf(map) < 3) {
				System.out.println("\ncase " + allCases_GridGraph.indexOf(map) + " : Complete Graph");
				// NBLK.Print2DArray(map);
				System.out.println("****************************");
				long t1 = System.currentTimeMillis();
				testBTCompleteGraph(map);
				long t2 = System.currentTimeMillis();
				System.out.println("Total time : " + (t2 - t1) + " ms.\n");
			}
		}

		// Iterative method
		for (int[][] map : allCases_GridGraph) {

			if (allCases_GridGraph.indexOf(map) < 5) {
				System.out.println("\ncase " + allCases_GridGraph.indexOf(map) + " :");
				// NBLK.Print2DArray(map);
				System.out.println("****************************");
				long t1 = System.currentTimeMillis();
				testBT1(map, false);
				long t2 = System.currentTimeMillis();
				System.out.println("Total time : " + (t2 - t1) + " ms.\n");
			}
		}
	}
	
	public static void testBT(int[][] origMap,boolean print) {
		
		NBLK sol=new NBLK(origMap);
		
		if(!sol.solve())
			System.out.print("No Solution!");
		else {
			System.out.print("A solution exists.");
			if(print)
				 NBLK.Print2DArray(sol.print());
		}
	
	}
	
	public static void testBT1(int[][] origMap,boolean print) {
		
		NBLK1 sol=new NBLK1(origMap);
		
		if(!sol.solve())
			System.out.print("No Solution!");
		else {
			System.out.print("A solution exists.");
			if(print)
				 NBLK.Print2DArray(sol.print());
		}
	
	}
	
	public static void testBTCompleteGraph(int[][] origMap) {
		
		HashMap<Integer, Node> SPoint=new HashMap<Integer, Node>();
		HashMap<Integer, Node> EPoint=new HashMap<Integer, Node>();
		int[][] map=completeGraph(origMap,SPoint,EPoint);
		NBLK sol=new NBLK(map,SPoint,EPoint);
		
		if(!sol.solve())
			System.out.print("No Solution!");
		else {
			System.out.print("A solution exists.");

		}
	
	}
	
	public static void testBT1CompleteGraph(int[][] origMap,boolean print) {
		
		NBLK1 sol=new NBLK1(origMap);
		
		if(!sol.solve())
			System.out.print("No Solution!");
		else {
			System.out.print("A solution exists.");
			if(print)
				 NBLK.Print2DArray(sol.print());
		}
	
	}
	
	public static int[][] completeGraph(int[][] origMap,HashMap<Integer, Node> SPoint, HashMap<Integer, Node> EPoint) {
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
					if(SPoint.containsKey(origMap[i][j])) {
						EPoint.put(origMap[i][j], new Node(-1,-1,index,origMap[i][j]));
						
					}else {
						SPoint.put(origMap[i][j], new Node(-1,-1,index,origMap[i][j]));
					}
				}
			}
		}
		return res;
	}

	
}
