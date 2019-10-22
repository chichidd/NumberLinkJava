package SAT;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.ContradictionException;

public class Task1 {

	//Variable class : Varia
	
	ISolver solver;//it's solver
	HashMap<Varia, Integer> solmap; // we can get variable number for solver from a Varia object
	HashMap<Integer, Varia> inverse_solmap; // we can get the Varia Object and its information from variable number for solver.

	Task1() {
		solver = SolverFactory.newDefault();
		solmap = new HashMap<Varia, Integer>();
		inverse_solmap = new HashMap<Integer, Varia>();
	}

	public ISolver task1(int[][] map, List<LinkedList<Integer>> l) {
		int n = map.length;// number of vertex
		int k = l.size();// number of paths
		// totally, there will be (n+1)*k*n +variables, for the length of path will not
		// exceed n.
		// x_v_i_p will be represented by :

		// use hashmap to make it easier to get variable number to use ISolver.
		int count=1;
		for (int i = 0; i < n; i++) {
			// when point index equals to n, it represents the symbol phantom.
			for (int j = 1; j <= k; j++) {
				for (int p = 1; p <= n; p++) {
					solmap.put(new Varia(i, j, p), count);
					inverse_solmap.put(count, new Varia(i, j, p));
					count++;
				}
			}
		}
		for (int j = 1; j <= k; j++) {
			for (int p = 1; p <= n + 1; p++) {
				solmap.put(new Varia(n, j, p), count);
				inverse_solmap.put(count, new Varia(n, j, p));
				count++;
			}
		}


		Constraint1(solver, map, l, solmap);

		for (int i = 1; i <= k; i++)
			for (int p = 1; p <= n + 1; p++)
				Constraint2(solver, i, p, map, l, solmap);

		Constraint3(solver, map, l, solmap);

		Constraint4(solver, map, l, solmap);

		Constraint5(solver, map, l, solmap);

		return solver;
	}

	public ISolver Constraint1(ISolver sol,  int[][] map, List<LinkedList<Integer>> l,
			HashMap<Varia, Integer> solmap) {
		// index of the vertex in the vertex set is pointIndex
		// Neighborhood information is in map[pointIndex]

		int n = map.length;// number of vertex
		int k = l.size();// number of paths
		VecInt clause=new VecInt();
		int[] allvar=new int[k*n];
		for(int pointIndex=0;pointIndex<n;pointIndex++) {
		for (int i = 1; i <= k; i++)
			for (int p = 1; p <= n; p++) {
				clause.push(solmap.get(new Varia(pointIndex, i, p)));
				allvar[(i-1)*n+p-1]=solmap.get(new Varia(pointIndex, i, p));

			}
		try {
			
			sol.addClause(clause);
			clause=new VecInt();
			for(int a=0;a<allvar.length-1;a++)
				for(int b=a+1;b<allvar.length;b++) {
				clause.push(-allvar[a]);
				clause.push(-allvar[b]);
				

				sol.addClause(clause);
				clause = new VecInt();
				}
				
		} catch (ContradictionException e1) {
			e1.printStackTrace();
		}
		}
		return sol;
	}

	public ISolver Constraint2(ISolver sol, int i, int p, int[][] map, List<LinkedList<Integer>> l,
			HashMap<Varia, Integer> solmap) {

		int n = map.length;// number of vertice
		int[] l_part1 = new int[n + 1];
		if (p != n + 1) {
			for (int m = 0; m <= n; m++) {
				// for both vertex and symbol phantom
				l_part1[m] = solmap.get(new Varia(m, i, p));
			}
		}
		try {
			if (p == n + 1) {
				// for each path i, at step n+1, only x_*_i_n+1 can be true;
				VecInt clause = new VecInt();
				clause.push(solmap.get(new Varia(n, i, p)));
				sol.addClause(clause);
			} else {
				sol.addClause(new VecInt(l_part1));
				int[] temp = new int[2];
				for (int m = 0; m < n; m++) {
					for (int q = m + 1; q <= n; q++) {
						temp[0] = -solmap.get(new Varia(m, i, p));
						temp[1] = -solmap.get(new Varia(q, i, p));
						sol.addClause(new VecInt(temp));
					}
				}
			}
		} catch (ContradictionException e1) {

			e1.printStackTrace();
		}

		return sol;

	};

	public ISolver Constraint3(ISolver sol, int[][] map, List<LinkedList<Integer>> l, HashMap<Varia, Integer> solmap) {

		int n = map.length;// number of vertex
		int k = l.size();
		try {
			int[] temp = null;
			for (int i = 1; i <= k; i++) {
				// FOR A path i
				for (int p = 1; p <= n; p++) {
					temp = new int[2];
					temp[0] = -solmap.get(new Varia(n, i, p));
					// if the path is finished before posision p
					// the path is finished before posision p+1 to n
					temp[1] = solmap.get(new Varia(n, i, p + 1));
					sol.addClause(new VecInt(temp));
				}
				// x_*_i_1=false and x_*_i_2=false;
				VecInt clause = new VecInt();
				clause.push(-solmap.get(new Varia(n, i, 1)));
				sol.addClause(clause);
				clause = new VecInt();
				clause.push(-solmap.get(new Varia(n, i, 2)));
				sol.addClause(clause);
			}

		} catch (ContradictionException e1) {
			e1.printStackTrace();
		}

		return sol;

	};

	public ISolver Constraint5(ISolver sol, int[][] map, List<LinkedList<Integer>> l, HashMap<Varia, Integer> solmap) {
		int n = map.length;
		int k = l.size();
		int i = 0;
		for (LinkedList<Integer> c : l) {

			i = l.indexOf(c) + 1;
			int startPointIndex = c.get(0);
			int endPointIndex = c.get(1);

			try {
				// x_start_startIndex_1=true x_end_endIndex_1=false
				int[] temp = new int[1];
				temp[0] = solmap.get(new Varia(startPointIndex, i, 1));

				sol.addClause(new VecInt(temp));
				temp[0] = -solmap.get(new Varia(endPointIndex, i, 1));
				sol.addClause(new VecInt(temp));
				// x_end_i_k=false for i!=endIndex and k from 1 to n
				VecInt clause = new VecInt();
				for (int a = 1; a <= n; a++) {
					for (int i0 = 1; i0 <= k; i0++) {
						if (i0 != i) {
							clause.push(-solmap.get(new Varia(endPointIndex, i0, a)));
							sol.addClause(clause);
						}
					}
				}
				// only one of x_end_endpathIndex_k can be true among k in [1,n]
				temp = new int[n - 1];
				for (int i0 = 2; i0 <= n; i0++) {
					temp[i0 - 2] = solmap.get(new Varia(endPointIndex, i, i0));
				}
				sol.addClause(new VecInt(temp));
				temp = new int[2];
				for (int a = 2; a <= n - 1; a++) {
					for (int b = a + 1; b <= n; b++) {
						temp[0] = -solmap.get(new Varia(endPointIndex, i, a));
						temp[1] = -solmap.get(new Varia(endPointIndex, i, b));
						sol.addClause(new VecInt(temp));
					}
				}
				// and the sink vertex t i is followed by 鈯� in the path i.
				for (int p = 2; p <= n; p++) {
					clause = new VecInt();
					clause.push(-solmap.get(new Varia(endPointIndex, i, p)));// if the path i ends at endPointIndex with
																				// // position p
					clause.push(solmap.get(new Varia(n, i, p + 1)));// if the path i ends at endPointIndex with position
																	// p
					sol.addClause(clause);
				}

			} catch (ContradictionException e1) {
				e1.printStackTrace();
			}

		}
		return sol;
	}

	
	public ISolver Constraint4(ISolver sol, int[][] map, List<LinkedList<Integer>> l,
			HashMap<Varia, Integer> solmap) {
		// find neibors
		int n = map.length;// number of vertex
		
		int[] allvarToConsider=null;

		
		
		HashMap<Integer,Integer> starts=new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> ends=new HashMap<Integer,Integer>();
		for (LinkedList<Integer> l0 : l) {
			starts.put(l0.get(0),l.indexOf(l0)+1);
			ends.put(l0.get(1),l.indexOf(l0)+1);
		}
		
		
		
		for (int pointIndex = 0; pointIndex < n; pointIndex++) {
			LinkedList<Integer> neibor = new LinkedList<Integer>();
			for (int a = 0; a < n; a++) {
				if (map[a][pointIndex] > 0) {
					// ajacent
					neibor.add(a);
				}
			}
			
			if(starts.containsKey(pointIndex)) {
				allvarToConsider = new int[neibor.size()];
				for (int a = 0; a < neibor.size(); a++) {
					allvarToConsider[a] = solmap.get(new Varia(neibor.get(a), starts.get(pointIndex), 2));
				}
				OnlyOneTrue(sol, allvarToConsider);
				
			} else if(ends.containsKey(pointIndex)){
				allvarToConsider = new int[neibor.size()];
				for(int p=2;p<=n;p++) {
					for (int a = 0; a < neibor.size(); a++) {
						allvarToConsider[a] = solmap.get(new Varia(neibor.get(a), ends.get(pointIndex), p-1));
					}
					OnlyOneTrue1(sol,allvarToConsider,-solmap.get(new Varia(pointIndex, ends.get(pointIndex), p)));
				}
				
			}else{
				
				for (LinkedList<Integer> l0 : l) {
					int i = l.indexOf(l0) + 1;

					for (int p = 2; p <= n - 1; p++) {

						allvarToConsider = new int[neibor.size()];
						for (int a = 0; a < neibor.size(); a++) {
							allvarToConsider[a] = solmap.get(new Varia(neibor.get(a), i, p + 1));
						}
						OnlyOneTrue1(sol, allvarToConsider, -solmap.get(new Varia(pointIndex, i, p)));
						for (int a = 0; a < neibor.size(); a++) {
							allvarToConsider[a] = solmap.get(new Varia(neibor.get(a), i, p - 1));
						}
						OnlyOneTrue1(sol, allvarToConsider, -solmap.get(new Varia(pointIndex, i, p)));

					}

				}
			}
		}
		return sol;

	}
	
	//given an array of variables for ISolver and a condition variable "condi", put into the solver "sol" a formula that ensures only one of 
	//the variables can be true if "condi" is true;
	public static ISolver OnlyOneTrue1(ISolver sol,int[] a,int condi) {

		int n=a.length;
		int[] temp=new int[n+1];
		for(int i=0;i<n;i++) {
			temp[i]=a[i];
		}
		temp[n]=condi;
		try {
			sol.addClause(new VecInt(temp));
			temp=new int[3];
			for(int i=0;i<n-1;i++) {
				for(int j=i+1;j<n;j++) {
				temp[0]=condi;
				temp[1]=-a[i];
				temp[2]=-a[j];
				sol.addClause(new VecInt(temp));
				}
				
			}
			
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sol;
	}
	
	//given an array of variables for ISolver, put into the solver "sol" a formula that ensures only one of 
	//the variables can be true;
	public static ISolver OnlyOneTrue(ISolver sol,int[] a) {

		int n=a.length;
		
		try {
			sol.addClause(new VecInt(a));
			int[] temp=new int[2];
			for(int i=0;i<n-1;i++) {
				for(int j=i+1;j<n;j++) {
				
				temp[0]=-a[i];
				temp[1]=-a[j];
				sol.addClause(new VecInt(temp));
				}
				
			}
			
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sol;
	}

}
