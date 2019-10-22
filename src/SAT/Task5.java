package SAT;
import java.util.HashMap;
import java.util.LinkedList;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;


public class Task5{
	
	public static int allSolution(int[][] origMap,boolean print,int maxSolution) {
		Task4 finder=new Task4();
		ISolver sol=finder.task4(origMap);
		int numberOfSolution=0;
		LinkedList<VecInt> clauses=new LinkedList<VecInt>();
		System.out.println("Number of variables: " + sol.nVars());
		System.out.println("Number of constraints: " + sol.nConstraints());
		try {
			if(!sol.isSatisfiable()) {
				System.out.println("No solution!");
			}else {
				
				int col = origMap[0].length;
				int row = origMap.length;
				while(sol.isSatisfiable()) {
					if(numberOfSolution>maxSolution)
						break;
					numberOfSolution++;
					
					int[] solution = sol.model();
					String[][] res = new String[2 * row + 1][2 * col + 1];
					
					HashMap<Integer, VariaVertex> vMI = Task4.varMapInverse;
					LinkedList<Integer> trueVar=new LinkedList<Integer>();
					for (int i = 0; i < solution.length; i++) {

						if (solution[i] > 0) {
							trueVar.add(solution[i]);
							VariaVertex vv = vMI.get(solution[i]);
							String path = Integer.toString(vv.path);
							String sens = vv.shape;
							int rown = vv.vertice.row_index;
							int coln = vv.vertice.col_index;
							res[rown * 2 + 1][coln * 2 + 1] = path;
							switch (sens) {
							case "LR":
								res[rown * 2 + 1][coln * 2] = "--";
								res[rown * 2 + 1][coln * 2 + 2] = "--";
								break;
							case "RB":
								res[rown * 2 + 1][coln * 2 + 2] = "--";
								res[rown * 2 + 2][coln * 2 + 1] = "|";
								break;
							case "RT":
								res[rown * 2][coln * 2 + 1] = "|";
								res[rown * 2 + 1][coln * 2 + 2] = "--";
								break;

							case "LB":
								res[rown * 2 + 1][coln * 2] = "--";
								res[rown * 2 + 2][coln * 2 + 1] = "|";
								break;
							case "LT":
								res[rown * 2 + 1][coln * 2] = "--";
								res[rown * 2][coln * 2 + 1] = "|";
								break;
							case "TB":
								res[rown * 2][coln * 2 + 1] = "|";
								res[rown * 2 + 2][coln * 2 + 1] = "|";
								break;
							}
						}
					}
					System.out.println("Solution "+numberOfSolution);
					if(print) {
					
					Print2DArrayString(res);
					}
					
					finder=new Task4();
					VecInt clause=new VecInt();
					for(Integer var:trueVar) 
						clause.push(-var);
					clauses.add(clause);
					
					for(VecInt c:clauses)
						Task2.addClause(finder.solver, c);
					sol=finder.task4(origMap);
					
				}
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return numberOfSolution;
	}

	//print a 2D string array
	public static void Print2DArrayString(String[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if(a[i][j]==null) {
					System.out.print("\t");
				}
				else {
				System.out.print(a[i][j]);
				System.out.print("\t");
				}
			}
			System.out.println();
		}
	}
}
