package SAT;
import java.util.HashMap;
import java.util.LinkedList;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Task3 {

	public ISolver solver;
	Task2 edgeMethode;
	public HashMap<Integer, VariaEdge> varEdgeMap;// we can get the VariaEdge Object and its information from variable number for solver.

	Task3(int[][] origMap){
		edgeMethode=new Task2(origMap);
		solver = SolverFactory.newDefault();
		
	}
	
	Task3(int[][] adjaMatrix, HashMap<Integer, LinkedList<Integer>> pairM){
		edgeMethode=new Task2(adjaMatrix,pairM);
		solver = SolverFactory.newDefault();
	}
	
	public ISolver task3() {
		solver = edgeMethode.task2();
		varEdgeMap=edgeMethode.varEdgeMap;
		HashMap<Integer,LinkedList<Integer>> checkCycle_vertex;
		HashMap<Integer,LinkedList<Integer>> checkCycle_edge;
		// numberOfEdge=numberOfPath-1
		int[] solution = null;

		boolean doAgain = false;
		// Solve the problem
		try {
			
			do {
				doAgain = false;
				checkCycle_vertex = new HashMap<Integer,LinkedList<Integer>>();
				checkCycle_edge = new HashMap<Integer,LinkedList<Integer>>();
				if (solver.isSatisfiable()) {
					solution = solver.model();
					//Print information
					//System.out.println("Satisfiable problem!");					
					//System.out.println("Number of variables: " + solver.nVars());
					//System.out.println("Number of constraints: " + solver.nConstraints());

					for (int i = 0; i < solution.length; i++) {
						if (solution[i] > 0) {
							// check if there is cycle - begin
							VariaEdge var = edgeMethode.varEdgeMap.get(solution[i]);
							Edge e = var.edge;
							int path = var.i;
							Vertex v1 = e.vertex1;
							Vertex v2 = e.vertex2;
							
							
							
							if (!checkCycle_vertex.containsKey(path)) {
								// add vertex
								LinkedList<Integer> temp_vertex = new LinkedList<Integer>();
								temp_vertex.add(v1.index);
								temp_vertex.add(v2.index);
								checkCycle_vertex.put(path, temp_vertex);
								
								LinkedList<Integer> temp_edge=new LinkedList<Integer>();
								temp_edge.add(solution[i]);
								checkCycle_edge.put(path,temp_edge);
								// number of edge in path i plus one
								

							} else {
								LinkedList<Integer> temp_vertex = checkCycle_vertex.get(path);
								if (!temp_vertex.contains(v1.index))
									temp_vertex.add(v1.index);
								if (!temp_vertex.contains(v2.index))
									temp_vertex.add(v2.index);
								
								LinkedList<Integer> temp_edge = checkCycle_edge.get(path);
								temp_edge.add(solution[i]);
							}
							// check if there is cycle - end
						}
					}

					for (Integer i:checkCycle_vertex.keySet()) {
						
						if (checkCycle_vertex.get(i).size() != checkCycle_edge.get(i).size()+1) {
							doAgain = true;
							VecInt clause=new VecInt();
							for(int j=0;j<checkCycle_edge.get(i).size();j++) {
								clause.push(-checkCycle_edge.get(i).get(j));
							}
							Task2.addClause(solver,clause);
							clause.clear();
						}
						
					}

				} else {
					System.out.println("Unsatisfiable problem without cycle!");
					return solver;
				}

			} while (doAgain);
			
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
		}

		return solver;
	}
}
