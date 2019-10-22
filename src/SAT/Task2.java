package SAT;
import java.util.LinkedList;
import java.util.HashMap;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

//Variable class: VariaEdge
public class Task2 {
	
	public ISolver solver;
	public HashMap<VariaEdge,Integer> varMap;
	public HashMap<Integer, LinkedList<Integer>> pairMap;
	public LinkedList<Edge> allEdge;
	public LinkedList<Vertex> allVertex;
	public LinkedList<LinkedList<Integer>> neiborTable;
	public HashMap<Integer, VariaEdge> varEdgeMap;// we can get the VariaEdge Object and its information from variable number for solver.


	Task2(int[][] origMap){
		solver = SolverFactory.newDefault();
		varMap=new HashMap<VariaEdge,Integer>();
		pairMap=new HashMap<Integer, LinkedList<Integer>>();
		allEdge=new LinkedList<Edge>();
		allVertex=new LinkedList<Vertex>();
		varEdgeMap=new HashMap<Integer, VariaEdge>();
		
		int n_row=origMap.length;
		int n_col=origMap[0].length;
		int length=n_row*n_col;
		//get a ajascent matrix for graph
		int[][] ajaMap=transformer(origMap,pairMap);
		neiborTable=constructVertexTable(ajaMap);
		
		//Initialize
		
		int count=1;

		for(int i=0;i<length;i++) {
			int i_col=i%n_col;
			int	i_row=(int)(i/n_col);
			
			//construct vertex hashmap
			allVertex.addLast(new Vertex(i_row,i_col,i));
			
			
			for(int j=i;j<length;j++) {
				
				int j_col=j%n_col;
				int	j_row=(int)(j/n_col);
				
				if(ajaMap[i][j]>0) {
					
					for(Integer a:pairMap.keySet()) {
						
						Edge e1=new Edge(new Vertex(i_row,i_col,i),new Vertex(j_row,j_col,j));
						
						allEdge.add(e1);
						VariaEdge var=new VariaEdge(e1,a);
						varMap.put(var, count);
						varEdgeMap.put(count, var);
						count+=1;
						
					}
					
				}
				
			}

		}
		
	}
	
	
	Task2(int[][] adjaMatrix, HashMap<Integer, LinkedList<Integer>> pairM){
		solver = SolverFactory.newDefault();
		varMap=new HashMap<VariaEdge,Integer>();
		pairMap=pairM;
		int length=adjaMatrix.length;
		int[][] ajaMap=adjaMatrix;
		allEdge=new LinkedList<Edge>();
		allVertex=new LinkedList<Vertex>();
		varEdgeMap=new HashMap<Integer, VariaEdge>();
		neiborTable=constructVertexTable(ajaMap);
		
		//Initialize
		
		int count=1;

		for(int i=0;i<length;i++) {

			//construct vertex hashmap
			allVertex.addLast(new Vertex(-1,-1,i));
			
			
			for(int j=i;j<length;j++) {
				
				if(ajaMap[i][j]>0) {
					for(Integer a:pairMap.keySet()) {
						
						Edge e1=new Edge(new Vertex(-1,-1,i),new Vertex(-1,-1,j));
						
						allEdge.add(e1);
						VariaEdge var=new VariaEdge(e1,a);
						varMap.put(var, count);
						varEdgeMap.put(count, var);
						count+=1;
						
					}
					
				}
				
			}

		}
		
	}
	
	public ISolver task2() {
		//Constraint1
		for(Edge e:allEdge) {
			VecInt clause = new VecInt();
			Integer[] temp=new Integer[pairMap.size()];
			//only used to get elements in pairMap's key set
			pairMap.keySet().toArray(temp);
			for(int a=0;a<pairMap.size()-1;a++) {
				for(int b=a+1;b<pairMap.size();b++) {
					int var1=varMap.get(new VariaEdge(e,temp[a]));
					int var2=varMap.get(new VariaEdge(e,temp[b]));
					clause.push(-var1);
					clause.push(-var2);
					addClause(solver,clause);
					clause.clear();
				}
				
			}
		}
		
		//Constraint2
		LinkedList<Vertex> StartEndPointSet =new LinkedList<Vertex>();
		for(Integer i:pairMap.keySet()) {
			LinkedList<Integer> temp=pairMap.get(i);
			//process start point

			int startIndex=temp.getFirst();
			Vertex start=allVertex.get(startIndex);
			StartEndPointSet.add(start);
			LinkedList<Integer> var=new LinkedList<Integer>();
			
			
			LinkedList<Integer> neiborList=neiborTable.get(startIndex);
			
			for(int j=0;j<neiborList.size();j++) {
				int neiborIndex=neiborList.get(j);
				Vertex neibor=allVertex.get(neiborIndex);
				Edge edgeOfNeibor=new Edge(start,neibor);
				for (Integer k:pairMap.keySet()) {
					if (k != i ) {
						// other than the right path
						VecInt clause = new VecInt();
						clause.push(-varMap.get(new VariaEdge(edgeOfNeibor,k)));
						addClause(solver,clause);
						clause.clear();
						
					} else {
						// only one of the right path will exist
						var.add(varMap.get(new VariaEdge(edgeOfNeibor,i)));
					}
				}
			}

			Task1.OnlyOneTrue(solver, toIntArray(var));
			
			//process end Point
			int endIndex=temp.getLast();
			Vertex end=allVertex.get(endIndex);
			
			
			StartEndPointSet.add(end);
			var=new LinkedList<Integer>();
			
			
			neiborList=neiborTable.get(endIndex);
			
			for(int j=0;j<neiborList.size();j++) {
				int neiborIndex=neiborList.get(j);
				Vertex neibor=allVertex.get(neiborIndex);
				Edge edgeOfNeibor=new Edge(end,neibor);
				for (Integer k:pairMap.keySet()) {
					if (k != i ) {
						// other than the right path
						VecInt clause = new VecInt();
						clause.push(-varMap.get(new VariaEdge(edgeOfNeibor,k)));
						addClause(solver,clause);
						clause.clear();
					} else {
						// only one of the right path will exist
						var.add(varMap.get(new VariaEdge(edgeOfNeibor,i)));
					}
				}
			}
			Task1.OnlyOneTrue(solver, toIntArray(var));
			
			
		}
		
		//Constraint3

		for(Vertex v:allVertex) {
			int vIndex=allVertex.indexOf(v);
			if(!StartEndPointSet.contains(v)) {
				//Part1 : only two of
				
				LinkedList<Integer> neiborList=neiborTable.get(vIndex);
				LinkedList<Integer> varList=new LinkedList<Integer>();
				for(int i=0;i<neiborList.size();i++) {
					Vertex neibor=allVertex.get(neiborList.get(i));
					Edge e=new Edge(v,neibor);
					for(Integer j:pairMap.keySet()) {
						
						varList.add(varMap.get(new VariaEdge(e,j)));				
					}

				}

				
				OnlyTwoTrue(solver,toIntArray(varList));
				
				
				// Part2 : in one path
				VecInt clause=new VecInt();
				for (int h = 0; h < neiborList.size() - 1; h++) {

					// System.out.println("neibor"+(i+1)+":"+neiborList.get(i));
					Vertex neibor = allVertex.get(neiborList.get(h));
					Edge theEdge = new Edge(v, neibor);

					for (Integer i:pairMap.keySet()) {
						int var1=varMap.get(new VariaEdge(theEdge,i));// choose one x_(the_edge)_i
						
						
						for (int j = h + 1; j < neiborList.size(); j++) {
							Vertex otherNeibor = allVertex.get(neiborList.get(j));
							Edge otherEdge = new Edge(v, otherNeibor);
							for (Integer k:pairMap.keySet()) {
								if(i!=k) {
									int var2=varMap.get(new VariaEdge(otherEdge,k));
									clause.push(-var1);
									clause.push(-var2);// one edge in path i and other edge in path k can't be true at same time
									addClause(solver,clause);
									clause.clear();

								}
							}

						}
					}
				}
			}
		}
		return solver;
	}
	
	public static LinkedList<LinkedList<Integer>> constructVertexTable(int[][] ajaMap)
	{
		
		LinkedList<LinkedList<Integer>> table=new LinkedList<LinkedList<Integer>> ();
		
		int length=ajaMap.length;
		for(int i=0;i<length;i++) {
			LinkedList<Integer> temp=new LinkedList<Integer>();
			for(int j=0;j<length;j++) {
				if(ajaMap[i][j]>0) {
					//Adjacent
					temp.add(j);
				}
			}
			table.addLast(temp);
			
		}
		return table;
	}

	public static void addClause(ISolver sol,VecInt clause) {
		if(sol!=null) {
			try {
				sol.addClause(clause);
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static int[][] transformer(int [][] origMap, HashMap<Integer, LinkedList<Integer>> pairMap) {
		int n_row=origMap.length;
		int n_col=origMap[0].length;
		int length=n_row*n_col;
		int[][] res=new int[length][length];
		//pairMap = new HashMap<Integer, LinkedList<Integer>>();
		for (int i = 0; i < n_row; i++) {
			for (int j = 0; j < n_col; j++) {
				if (origMap[i][j] > -1) {
					int index = i * n_col + j;

					if (i > 0 && origMap[i-1][j] > -1) {
						res[index][(i - 1) * n_col + j] = 1;
						res[(i - 1) * n_col + j][index] = 1;
					}
					if (i < n_row - 1 && origMap[i+1][j] > -1) {
						res[index][(i + 1) * n_col + j] = 1;
						res[(i + 1) * n_col + j][index] = 1;
					}
					if (j > 0 && origMap[i][j-1] > -1) {
						res[index][i * n_col + j - 1] = 1;
						res[i * n_col + j - 1][index] = 1;
					}
					if (j < n_col - 1 && origMap[i][j+1] > -1) {
						res[index][i * n_col + j + 1] = 1;
						res[i * n_col + j + 1][index] = 1;
					}

					if (origMap[i][j] > 0) {
						LinkedList<Integer> l_temp = null;
						if (!pairMap.containsKey(origMap[i][j])) {
							l_temp = new LinkedList<Integer>();
							l_temp.addFirst(index);
							pairMap.put(origMap[i][j], l_temp);
						} else {
							l_temp = pairMap.get(origMap[i][j]);
							l_temp.addLast(index);
							pairMap.replace(origMap[i][j], l_temp);
						}
					}

			}
			}
		}
		return res;
	}

	public static int[] toIntArray(LinkedList<Integer> list){

		  int[] ret = new int[list.size()];
		  for(int i = 0;i < ret.length;i++)
		    ret[i] = list.get(i);
		  return ret;
		}

	public static ISolver OnlyTwoTrue(ISolver sol, int[] a) {
		
		int n = a.length;
		int[] temp = new int[n - 1];
		try {
			for (int b = 0; b < n; b++) {
				
				for (int i = 0; i < temp.length; i++) {
					if (i < b)
						temp[i] = a[i];
					else if (i >= b)
						temp[i] = a[i + 1];
				}
				sol.addClause(new VecInt(temp));
			}
			
			temp = new int[3];
			for (int i = 0; i < n - 2; i++) {
				for (int j = i + 1; j < n - 1; j++) {
					for (int k = j + 1; k < n; k++) {
						temp[0] = -a[i];
						temp[1] = -a[j];
						temp[2] = -a[k];
						sol.addClause(new VecInt(temp));
						
					}
				}
			}
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sol;
	}

}
