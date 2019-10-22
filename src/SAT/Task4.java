package SAT;
import BT.*;
import java.util.HashMap;
import java.util.LinkedList;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

//Variable class: VariaVertex
public class Task4 {
	// R:Right
	// L:Left
	// T:Top
	// B:Bottom
	// If we are only interested in grid graphs
	public static String[] shape = { "LR", "TB", "LT", "RT", "RB", "LB" };
	public HashMap<VariaVertex, Integer> varMap;
	public HashMap<Integer, LinkedList<Integer>> pairMap;
	public ISolver solver;

	public static HashMap<Integer, VariaVertex> varMapInverse;
	// we can get the VariaVertex Object and its information from variable number for solver.
	Task4() {
		
		varMap = new HashMap<VariaVertex, Integer>();
		pairMap = new HashMap<Integer, LinkedList<Integer>>();
		solver = SolverFactory.newDefault();
		varMapInverse = new HashMap<Integer, VariaVertex>();
	}

	public ISolver task4(int[][] origMap) {
		HashMap<Integer,Vertex> allVertex = new HashMap<Integer,Vertex>();
		int n_row = origMap.length;
		int n_col = origMap[0].length;
		int[][] ajaMap = Task2.transformer(origMap, pairMap);
		// get a adjacent matrix for graph

		HashMap<Vertex, HashMap<String, Vertex>> neiborList = createNeiborList(origMap);
		varAndVerDealer(origMap, varMap, allVertex, pairMap);
		LinkedList<Integer> StartEnd = setofStartorEnd();
		// Constraint1: each vertices has one i and shape.
		for (Vertex v : allVertex.values()) {
			LinkedList<Integer> varList = new LinkedList<Integer>();
			for (Integer i : pairMap.keySet()) {
				for (int j = 0; j < shape.length; j++) {
					varList.add(VAR(v, i, shape[j]));
				}

				// Constraint4 possible: the direction must correspond to the existing neighbor
				if (!StartEnd.contains(v.index)) {
					VecInt clause = new VecInt();
					if (!neiborList.get(v).containsKey("L")) {
						// on the left
						clause.push(-VAR(v, i, "LR"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "LT"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "LB"));
						Task2.addClause(solver, clause);
						clause.pop();

					}
					if (!neiborList.get(v).containsKey("R")) {
						// on the right
						clause.push(-VAR(v, i, "LR"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "RT"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "RB"));
						Task2.addClause(solver, clause);
						clause.pop();
					}
					if (!neiborList.get(v).containsKey("T")) {
						// on the top
						clause.push(-VAR(v, i, "TB"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "RT"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "LT"));
						Task2.addClause(solver, clause);
						clause.pop();
					}
					if (!neiborList.get(v).containsKey("B")) {
						// on the bottom
						clause.push(-VAR(v, i, "TB"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "RB"));
						Task2.addClause(solver, clause);
						clause.pop();
						clause.push(-VAR(v, i, "LB"));
						Task2.addClause(solver, clause);
						clause.pop();
					}
				}

			}
			Task1.OnlyOneTrue(solver, Task2.toIntArray(varList));
		}
		// Constraint2: for a path i, for a point except start and end, only two
		// direction can go.

		for (Vertex vertice : allVertex.values()) {
			if (!StartEnd.contains(vertice.index)) {
				// get all neibors of this vertice---------------------
				HashMap<String, Vertex> neibors = neiborList.get(vertice);

				for (Integer path : pairMap.keySet()) {
					// get other paths------------------
					LinkedList<Integer> otherPath = getOtherPath(path);

					// For each shape, we consider its neibors'shape
					for (int i = 0; i < shape.length; i++) {

						HashMap<String, LinkedList<String>> neiborsPossiShape = possibleShape(shape[i]);
						HashMap<String, LinkedList<String>> neiborsNonPossiShape = nonPossibleShape(shape[i]);
						VecInt clause = new VecInt();
						// L'astuce, c'est que:
						// if the vertice have shape[i] in the path P,
						// 1.on the two possible direction only path P can go through with limited
						// shapes,

						// 1.
						// possible direction
						for (String sens : neibors.keySet()) {
							Vertex theNeibor = neibors.get(sens);
							if (neiborsPossiShape.containsKey(sens)) {

								////// in the same path
								// possible and impossible shapes
								LinkedList<String> possiShape = neiborsPossiShape.get(sens);
								LinkedList<String> nonpossiShape = neiborsNonPossiShape.get(sens);

								// when neibors are in the same path and have possible shapes
								clause.clear();
								clause.push(-VAR(vertice, path, shape[i]));
								for (String thePossiShape : possiShape)
									clause.push(VAR(theNeibor, path, thePossiShape));
								Task2.addClause(solver, clause);
								clause.clear();

								// when neibors are in the same path and have impossible shapes

								clause.push(-VAR(vertice, path, shape[i]));
								for (String npshape : nonpossiShape) {
									clause.push(-VAR(theNeibor, path, npshape));
									Task2.addClause(solver, clause);
									clause.pop();
								}
								/////// in the possible direction, other path can't be possible for all shape
								clause.clear();
								clause.push(-VAR(vertice, path, shape[i]));
								for (Integer otherp : otherPath) {
									for (int c = 0; c < shape.length; c++) {
										clause.push(-VAR(theNeibor, otherp, shape[c]));
										Task2.addClause(solver, clause);
										clause.pop();
									}
								}

							} else {

								// 2.and on the other direction, path P can't be true for all shape and for this
								// path, and certains shape for other path except for the start or end neibor

								if (!StartEnd.contains(theNeibor.index)) {
									clause.clear();
									clause.push(-VAR(vertice, path, shape[i]));

									LinkedList<String> nonPossiShape = possibleShapeforOneDirection(sens);
									for (String str : nonPossiShape) {
										for (Integer op : pairMap.keySet()) {
											clause.push(-VAR(theNeibor, op, str));
											Task2.addClause(solver, clause);
											clause.pop();
										}
									}

								}
							}

						}

					}

				}
			}
		}

		// Constraint3: for end and start point, only shapes TB and LR are possible, for
		// each shape, only one of two possible direction goes and its path number is
		// unique.
		for (Integer pathNumber : pairMap.keySet()) {

			LinkedList<Integer> liste = pairMap.get(pathNumber);
			int startIndex = liste.getFirst();
			int endIndex = liste.getLast();
			Vertex startVertice = allVertex.get(startIndex);
			Vertex endVertice = allVertex.get(endIndex);
			VecInt clause = new VecInt();
			// its path number is unique.
			
			// its path number is unique and only shapes TB and LR are possible

			clause.push(VAR(startVertice, pathNumber, "LR"));
			clause.push(VAR(startVertice, pathNumber, "TB"));
			Task2.addClause(solver, clause);
			clause.clear();
			clause.push(VAR(endVertice, pathNumber, "LR"));
			clause.push(VAR(endVertice, pathNumber, "TB"));
			Task2.addClause(solver, clause);
			clause.clear();

			// LR
			int condiofStart = -VAR(startVertice, pathNumber, "LR");
			int condiofEnd = -VAR(endVertice, pathNumber, "LR");
			LinkedList<Integer> varsofStart = new LinkedList<Integer>();
			LinkedList<Integer> varsofEnd = new LinkedList<Integer>();
			HashMap<String, Vertex> startNeibors = neiborList.get(startVertice);
			HashMap<String, Vertex> endNeibors = neiborList.get(endVertice);

			// delete all the vertices that are other start or end point

			if (startNeibors.containsKey("L")) {
				for (String shape : possibleShapeforOneDirection("L"))
					varsofStart.add(VAR(startNeibors.get("L"), pathNumber, shape));
			}
			if (startNeibors.containsKey("R")) {
				for (String shape : possibleShapeforOneDirection("R"))
					varsofStart.add(VAR(startNeibors.get("R"), pathNumber, shape));
			}
			if (endNeibors.containsKey("L")) {
				for (String shape : possibleShapeforOneDirection("L"))
					varsofEnd.add(VAR(endNeibors.get("L"), pathNumber, shape));
			}
			if (endNeibors.containsKey("R")) {
				for (String shape : possibleShapeforOneDirection("R"))
					varsofEnd.add(VAR(endNeibors.get("R"), pathNumber, shape));
			}
			Task1.OnlyOneTrue1(solver, Task2.toIntArray(varsofStart), condiofStart);
			Task1.OnlyOneTrue1(solver, Task2.toIntArray(varsofEnd), condiofEnd);
			// TB
			condiofStart = -VAR(startVertice, pathNumber, "TB");
			condiofEnd = -VAR(endVertice, pathNumber, "TB");
			varsofStart = new LinkedList<Integer>();
			varsofEnd = new LinkedList<Integer>();

			if (startNeibors.containsKey("T")) {
				for (String shape : possibleShapeforOneDirection("T"))
					varsofStart.add(VAR(startNeibors.get("T"), pathNumber, shape));
			}
			if (startNeibors.containsKey("B")) {
				for (String shape : possibleShapeforOneDirection("B"))
					varsofStart.add(VAR(startNeibors.get("B"), pathNumber, shape));
			}
			if (endNeibors.containsKey("T")) {
				for (String shape : possibleShapeforOneDirection("T"))
					varsofEnd.add(VAR(endNeibors.get("T"), pathNumber, shape));
			}
			if (endNeibors.containsKey("B")) {
				for (String shape : possibleShapeforOneDirection("B"))
					varsofEnd.add(VAR(endNeibors.get("B"), pathNumber, shape));
			}
			Task1.OnlyOneTrue1(solver, Task2.toIntArray(varsofStart), condiofStart);
			Task1.OnlyOneTrue1(solver, Task2.toIntArray(varsofEnd), condiofEnd);

		}

		// Check for closed cycles

		return solver;
	}

	public int VAR(Vertex v, int path, String sh) {
		return varMap.get(new VariaVertex(v, path, sh));
	}

	public static void addClause(ISolver sol, LinkedList<Integer> a) {
		if (sol != null) {
			try {
				sol.addClause(new VecInt(Task2.toIntArray(a)));
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void addClause(ISolver sol, int[] a) {
		if (sol != null) {
			try {
				sol.addClause(new VecInt(a));
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void varAndVerDealer(int[][] origMap, HashMap<VariaVertex, Integer> varMap,
			HashMap<Integer,Vertex> allVertex, HashMap<Integer, LinkedList<Integer>> pairMap) {
		int n_row = origMap.length;
		int n_col = origMap[0].length;
		for (int i = 0; i < n_row; i++) {
			for (int j = 0; j < n_col; j++) {
				if(origMap[i][j]>-1)
					allVertex.put(i * n_col + j,new Vertex(i, j, i * n_col + j));
			}
		}
		int count = 1;
		for (Vertex v : allVertex.values()) {
			for (Integer i : pairMap.keySet()) {
				for (int j = 0; j != shape.length; j++) {
					VariaVertex varVer = new VariaVertex(v, i, shape[j]);
					varMap.put(varVer, count);
					varMapInverse.put(count, varVer);
					count++;
				}
			}
		}

	}

	public LinkedList<Integer> getOtherPath(int path) {
		LinkedList<Integer> otherPath = new LinkedList<Integer>();
		for (Integer op : pairMap.keySet())
			if (op != path)
				otherPath.add(op);
		return otherPath;
	}

	public HashMap<String, LinkedList<String>> possibleShape(String str) {

		HashMap<String, LinkedList<String>> res = new HashMap<String, LinkedList<String>>();
		for (int i = 0; i < str.length(); i++) {
			String letter = str.substring(i, i + 1);
			res.put(letter, possibleShapeforOneDirection(letter));
		}
		return res;
	}

	public HashMap<String, LinkedList<String>> nonPossibleShape(String str) {

		HashMap<String, LinkedList<String>> res = new HashMap<String, LinkedList<String>>();
		for (int i = 0; i < str.length(); i++) {
			String letter = str.substring(i, i + 1);
			res.put(letter, nonPossibleShapeforOneDirection(letter));
		}
		return res;
	}

	public LinkedList<String> possibleShapeforOneDirection(String str) {

		LinkedList<String> res = new LinkedList<String>();
		switch (str) {
		case "L":
			res.add("LR");
			res.add("RT");
			res.add("RB");
			break;
		case "R":
			res.add("LR");
			res.add("LT");
			res.add("LB");
			break;
		case "T":
			res.add("TB");
			res.add("LB");
			res.add("RB");
			break;
		case "B":
			res.add("TB");
			res.add("LT");
			res.add("RT");
			break;

		}
		return res;
	}

	public LinkedList<String> nonPossibleShapeforOneDirection(String str) {

		LinkedList<String> res = new LinkedList<String>();
		switch (str) {
		case "L":
			res.add("TB");
			res.add("LT");
			res.add("LB");
			break;
		case "R":
			res.add("TB");
			res.add("RT");
			res.add("RB");
			break;
		case "T":
			res.add("LR");
			res.add("LT");
			res.add("RT");
			break;
		case "B":
			res.add("LR");
			res.add("LB");
			res.add("RB");
			break;

		}
		return res;
	}

	public LinkedList<Integer> setofStartorEnd() {
		LinkedList<Integer> res = new LinkedList<Integer>();

		for (LinkedList<Integer> l : pairMap.values())
			res.addAll(l);
		return res;
	}

	public HashMap<Vertex, HashMap<String, Vertex>> createNeiborList(int[][] origMap) {
		HashMap<Vertex, HashMap<String, Vertex>> neiborList = new HashMap<Vertex, HashMap<String, Vertex>>();
		int n_col = origMap[0].length;
		int n_row = origMap.length;
		for (int i = 0; i != n_row; i++) {
			for (int j = 0; j != n_col; j++) {
				if(origMap[i][j]>-1) {
				Vertex mainVertex = new Vertex(i, j, i * n_col + j);
				HashMap<String, Vertex> map = new HashMap<String, Vertex>();
				if (i > 0 && origMap[i-1][j]>-1) {
					// there is a neibor above
					map.put("T", new Vertex(i - 1, j, (i - 1) * n_col + j));
				}
				if (i < n_row - 1 && origMap[i+1][j]>-1) {
					// there is a neibor below
					map.put("B", new Vertex(i + 1, j, (i + 1) * n_col + j));
				}
				if (j > 0 && origMap[i][j-1]>-1) {
					// there is a neibor on the left
					map.put("L", new Vertex(i, j - 1, i * n_col + j - 1));
				}
				if (j < n_col - 1 && origMap[i][j+1]>-1) {
					// there is a neibor on the right
					map.put("R", new Vertex(i, j + 1, i * n_col + j + 1));
				}
				neiborList.put(mainVertex, map);
			}
			}
		}
		return neiborList;
	}
	
}
