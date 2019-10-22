package BT;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack; 
public class NBLK {
	// each node has a unique index.
	public static int length; // number of nodes.
	public static int n_col;// for non 2D array, equals to -1.
	public int n_row;// for non 2D array, equals to -1.
	public int[][] adjaMatrix;// 0 represents no edge, 1 represents there is a edge,
	public HashMap<Integer,Node> allNodes;
	public static HashMap<Integer,LinkedList<Integer>> nowMap;// a easy way to get neighbors of the node
							// vertices are in the same path x
	public static HashMap<Integer, Node> StartPoint;
	public static HashMap<Integer, Node> EndPoint;

	public HashMap<Integer, Stack<Node>> paths;// all paths

	
	int lastPath;//last changed path
	public int count;// how many times solve() is called.

	NBLK(int[][] origMap) {

		
		// suppose it's a matrix for numberlink
		n_row = origMap.length;
		n_col = origMap[0].length;
		length = n_row * n_col;
		adjaMatrix=new int[length][length];
		StartPoint=new HashMap<Integer, Node>();
		EndPoint=new HashMap<Integer, Node>();
		nowMap= new HashMap<Integer,LinkedList<Integer>>();
		
		paths=new HashMap<Integer, Stack<Node>>();
		allNodes= new HashMap<Integer,Node>();
		
		
		lastPath=0;
		
		count=0;
		for (int i = 0; i < n_row; i++) {
			for (int j = 0; j < n_col; j++) {
				if (origMap[i][j] > -1) {
					//we can use that point to represent a blocked point (i,j) as matrix[i][j]=-1
					int index = i * n_col + j;
					Node n = new Node(i, j, index, origMap[i][j]);
					allNodes.put(index, n);
					LinkedList<Integer> neibors = new LinkedList<Integer>();

					if (i > 0 && origMap[i-1][j] > -1) {
						//left
						adjaMatrix[index][(i - 1) * n_col + j] = 1;
						adjaMatrix[(i - 1) * n_col + j][index] = 1;
						neibors.addLast((i - 1) * n_col + j);
					}
					if (i < n_row - 1 && origMap[i+1][j] > -1) {
						//right
						adjaMatrix[index][(i + 1) * n_col + j] = 1;
						adjaMatrix[(i + 1) * n_col + j][index] = 1;
						neibors.addLast((i + 1) * n_col + j);
					}
					if (j > 0 && origMap[i][j-1] > -1) {
						//top
						adjaMatrix[index][i * n_col + j - 1] = 1;
						adjaMatrix[i * n_col + j - 1][index] = 1;
						neibors.addLast(i * n_col + j - 1);
					}
					if (j < n_col - 1 && origMap[i][j+1] > -1) {
						//bottom
						adjaMatrix[index][i * n_col + j + 1] = 1;
						adjaMatrix[i * n_col + j + 1][index] = 1;
						neibors.addLast(i * n_col + j + 1);
					}

					nowMap.put(index, neibors);
					if (origMap[i][j] > 0) {

						if (!StartPoint.containsKey(origMap[i][j])) {
							StartPoint.put(origMap[i][j], n);
							Stack<Node> tp = new Stack<Node>();
							tp.push(n);
							paths.put(origMap[i][j], tp);
						} else {
							EndPoint.put(origMap[i][j], n);
						}

					}
				}
			}
		}

	}

	NBLK(int[][] matrix, HashMap<Integer, Node> SPoint, HashMap<Integer, Node> EPoint) {
		lastPath=0;
		count=0;
		adjaMatrix=matrix;
		StartPoint=SPoint;
		EndPoint=EPoint;
		length=matrix.length;

		n_col=-1;
		n_row=-1;
		nowMap= new HashMap<Integer,LinkedList<Integer>>();
		paths=new HashMap<Integer, Stack<Node>>();
		allNodes= new HashMap<Integer,Node>();
		for(int i=0;i!=length;i++)
		{
			allNodes.put(i, new Node(n_row,n_col,i,0));
			for(int j=0;j!=length;j++) {
				if(adjaMatrix[i][j]>0) {
					if(nowMap.containsKey(i)) {
						LinkedList<Integer> ltemp=nowMap.get(i);
						ltemp.add(j);
						
					}else {
						LinkedList<Integer> ltemp=new LinkedList<Integer>();
						ltemp.add(j);
						nowMap.put(i, ltemp);
					}
				}
			}
		}
		
		for(Integer path:StartPoint.keySet()) {
			
			Node start=StartPoint.get(path);
			Node end=EndPoint.get(path);
			int startIndex=start.index;
			int endIndex=end.index;
			allNodes.put(startIndex,start);
			allNodes.put(endIndex,end);
			Stack<Node> thisPath=new Stack<Node>();
			thisPath.push(start);
			paths.put(path, thisPath);
		}
		
	}

	//check if the path can be extended into node n.
	public boolean check(Node n, int path) {

		// Basic case check.
		if (EndPoint.get(path).equals(n))
			return true;

		// Basic case check
		boolean okForPath = false;
		for (Integer neiborIndex : nowMap.get(n.index)) {
			Node neibor = allNodes.get(neiborIndex);

			if (neibor.path == 0 || EndPoint.get(path).equals(neibor))
				okForPath = true;
		}

		
		// Dead end check

		for (Integer neiborIndex : nowMap.get(n.index)) {
			if (allNodes.get(neiborIndex).path == 0) {
				int num = 0;
			
					// BEGIN check for dead ends

					// except the node we are testing, all the other neighbor nodes from other paths
					// of the neighbor node of the node we are testing are occupied and then a dead
					// end is created.
					
					for(Integer neighborOfNeighborIndex:nowMap.get(neiborIndex)) {
						if(neighborOfNeighborIndex!=n.index) {
							if(allNodes.get(neighborOfNeighborIndex).path==0)
								num++;
							else {
								for(Integer pathIndex:StartPoint.keySet()) {
									if(!paths.get(pathIndex).peek().equals(EndPoint.get(pathIndex))){
										if(neighborOfNeighborIndex==paths.get(pathIndex).peek().index || neighborOfNeighborIndex==EndPoint.get(pathIndex).index)
											num++;
									}
								}
							}
						}
						else
							num++;
					}
					if(num<=1)
						return false;
					// END check for dead ends
					}

			}

		LinkedList<Integer> topPathsIndex=new LinkedList<Integer>();
		
		for(Integer p:paths.keySet())
			topPathsIndex.add(paths.get(p).peek().index);
		boolean deadEnd=true;
		for(Integer nodeIndex:allNodes.keySet()) {
			Node theNode=allNodes.get(nodeIndex);
			if(theNode.path==0) {
				for(Integer itsNeighborIndex:nowMap.get(nodeIndex)) {
					if(allNodes.get(itsNeighborIndex).path==0) {
						deadEnd=false;
						break;
					}
				}
				if(deadEnd) {
					for(Integer itsNeighborIndex:nowMap.get(nodeIndex)) {
						if(topPathsIndex.contains(itsNeighborIndex)) {
							int p0=allNodes.get(itsNeighborIndex).path;
							int itsEndIndex=EndPoint.get(p0).index;
							if(nowMap.get(nodeIndex).contains(itsEndIndex))
							{
								deadEnd=false;
								break;
							}
						}
						
					}
				}
				
			}else
			{
				for(Integer itsNeighborIndex:nowMap.get(nodeIndex)) {
					if(allNodes.get(itsNeighborIndex).path==0 ||allNodes.get(itsNeighborIndex).path==theNode.path) {
						deadEnd=false;
						break;
					}
				}
				
			}
			
		}
		if(deadEnd)
			return !deadEnd;
		
		HashMap<Integer,Integer> rmap=new HashMap<Integer,Integer>();
		int rcount=state_build_regions(rmap);
		if(state_regions_stranded(rcount,rmap,paths.size()+1,1)>0)
			return false;

		return okForPath;
	}
	
	//build a union find structure of regions
	public int state_build_regions(HashMap<Integer, Integer> rmap) {
		UnionFind u=new UnionFind();
		
		//build Unionfind
		for(Integer nodeIndex:allNodes.keySet()) {
		
			LinkedList<Integer> neighborsIndex=nowMap.get(nodeIndex);
			if(allNodes.get(nodeIndex).path==0) {
				for(Integer nIndex:neighborsIndex) {
					if(allNodes.get(nIndex).path==0)
						u.union(allNodes.get(nIndex),allNodes.get(nodeIndex));
				}
			} else if(allNodes.get(nodeIndex).path!=0) {
				for(Integer nIndex:neighborsIndex) {
					if(allNodes.get(nIndex).path!=0)
						u.union(allNodes.get(nIndex),allNodes.get(nodeIndex));
				}
			}
		}
		
		int rcount=1;
		LinkedList<Node> count=new LinkedList<Node>();
		//find regions
		for(Integer nIndex:allNodes.keySet()) {
			Node n=allNodes.get(nIndex);
			Node np=u.find(n);
			
			
			if(np.path==0) {
				if(count.contains(np)) {
					rmap.put(nIndex, count.indexOf(np)+1);
				}
				else {
					count.add(np);
					rmap.put(nIndex, count.indexOf(np)+1);
				}
				
			}else {
				rmap.put(nIndex, -1);
			}
				
		}
	
		return rcount;
	}

	//check if there are stranded regions
	public int state_regions_stranded(int rcount,HashMap<Integer,Integer> rmap,int chokepoint_path,int max_stranded) {
		
		HashMap<Integer,LinkedList<Integer>> regions=new HashMap<Integer,LinkedList<Integer>> ();
		for(Integer nodeIndex:rmap.keySet()) {
			int region=rmap.get(nodeIndex);
			if(region!=-1) {
			if(regions.containsKey(region)) {
				LinkedList<Integer> l=regions.get(region);
				l.add(nodeIndex);
			}
			else {
				LinkedList<Integer> l=new LinkedList<Integer>();
				l.add(nodeIndex);
				regions.put(region, l);
			}
			}
		}
		
		HashMap<Integer,LinkedList<Integer>> regionsNeighbor=new HashMap<Integer,LinkedList<Integer>> ();
		
		for(Integer regionIndex:regions.keySet()) {
			LinkedList<Integer> regionNode=regions.get(regionIndex);
			
			LinkedList<Integer> regionNeighbor=new LinkedList<Integer>();
			for(Integer nodeIndex:regionNode) {
				for(Integer itsNeighbor:nowMap.get(nodeIndex)) {
					if(!regionNode.contains(itsNeighbor))
					if(!regionNeighbor.contains(itsNeighbor))
						regionNeighbor.add(itsNeighbor);
				}
			}
			regionsNeighbor.put(regionIndex, regionNeighbor);
		}
		
		boolean ok=false;;

		for (Integer p : paths.keySet()) {
			if(!paths.get(p).peek().equals(EndPoint.get(p))) {
			ok=false;
			int topIndex = paths.get(p).peek().index;
			for (Integer regionIndex : regionsNeighbor.keySet()) {
				LinkedList<Integer> itsNeighbors = regionsNeighbor.get(regionIndex);

				if (itsNeighbors.contains(topIndex)&&itsNeighbors.contains(EndPoint.get(p).index)) {
					ok=true;
					break;
				}

			}
			if(!ok) {
				
				if(!nowMap.get(paths.get(p).peek().index).contains(EndPoint.get(p).index))
				return p;
			}
		}
		}
		
		
		return 0;
	}
	
	//return one of paths with least neighbors of its top node
	public static int leastChoicePath(HashMap<Integer, Stack<Node>> ps,HashMap<Integer,LinkedList<Integer>> nowM ,HashMap<Integer,Node> allN,HashMap<Integer, Node> EndP) {
		
		int res = 0;
		LinkedList<Integer> possiPath= allleastChoicePath( ps, nowM ,allN,EndP);
		//in case where there are several possible path.
		if(possiPath.size()==1)
			return possiPath.getFirst();
		else {
			//find randomly,
			//int index=(int)(Math.random()*possiPath.size());
			//or find the path with less length.
			int min=Integer.MAX_VALUE;
			for(Integer p:possiPath)
			{
				if(ps.get(p).size()<min) {
					res=p;
					min=ps.get(p).size();
				}
			}
			return res;
		}
	}

	//return all the paths with least neighbors of its top node
	public static LinkedList<Integer> allleastChoicePath(HashMap<Integer, Stack<Node>> ps,HashMap<Integer,LinkedList<Integer>> nowM ,HashMap<Integer,Node> allN,HashMap<Integer, Node> EndP) {
		
		
		LinkedList<Integer> possiPath=new LinkedList<Integer>();
		int mincount = Integer.MAX_VALUE;
		int count = 0;
		for (Integer path : ps.keySet()) {

			Node n = ps.get(path).peek();

			if (EndP.containsKey(path) &&!EndP.get(path).equals(n)) {
				
				for (Integer neiborIndex : nowM.get(n.index)) {
					Node neibor=allN.get(neiborIndex);
					if (neibor.path == 0 || EndP.get(path).equals(neibor))
						count++;
				}

				if (count < mincount) {
					
					possiPath.clear();
					possiPath.add(path);	
					mincount = count;
				}else if(count==mincount) {
					possiPath.addFirst(path);
				}
						
				count = 0;
			}

		}

		return possiPath;
	}
	
	//check if the puzzle is finished
	public boolean finished() {
		boolean allOccupied = true;
		for (Node n : allNodes.values()) {
			if (n.path == 0) {
				allOccupied = false;
			}
		}
		if (allOccupied) {

			for (Integer path : paths.keySet()) {
				Stack<Node> sn=paths.get(path);
				Node n = sn.peek();
				if (!EndPoint.containsValue(n)) {
					return false;
				}
				
				if (sn.size() > 1) {
					Stack<Node> snBK = (Stack<Node>) sn.clone();
					
					Node n1 = snBK.pop();
					while (!snBK.isEmpty()) {
						if(n1.path!=path)
							return false;
						Node n2= snBK.pop();
						if(adjaMatrix[n1.index][n2.index]==0)
							return false;
						else {
							n1=n2;
							
						}
					}
				}
				
			}
			return true;
		} else
			return false;
	}
	
	//Task6
	public boolean solve() {
		

		if (finished())
			return true;
		count++;
		
		//System.out.println("***************"+count);
		//Print2DArray(print());
		int path=0;
		
		
		path = leastChoicePath(paths, nowMap, allNodes, EndPoint);
		if(path==0)
			return false;
		Stack<Node> thePath = paths.get(path);
		Node n = thePath.peek();
		if (!n.equals(EndPoint.get(path))) {

			LinkedList<Integer> neiborsIndex = nowMap.get(n.index);
			for (Integer theNeiborIndex : neiborsIndex) {
				Node theNeibor = allNodes.get(theNeiborIndex);
				if (theNeibor.path == 0) {
					theNeibor.path = path;
					thePath.push(theNeibor);
					if (check(theNeibor, path) && solve())
						return true;

					theNeibor.path = 0;
					thePath.pop();
				}
				else if( theNeibor.equals(EndPoint.get(path))) {
					thePath.push(theNeibor);
					if (check(theNeibor, path) && solve())
						return true;
					thePath.pop();
				}
			}
			lastPath=0;
			return false;

		}
		return true;
	}
	
	//Task7
	public void count(int k,boolean print){
		
		if (finished()) {
			
			count++;
			if(count>=k) {
				System.out.println("*********A result************");
				Print2DArray(print());
			}
			return ;
		}	
		if(count>=k)
		return;
		LinkedList<Integer> pathList = allleastChoicePath( paths, nowMap ,allNodes,EndPoint);
		if(pathList.size()!=0) {
			
		int path=pathList.getFirst();
		Stack<Node> thePath = paths.get(path);
		Node n = thePath.peek();
		if (!n.equals(EndPoint.get(path))) {

			LinkedList<Integer> neiborsIndex = nowMap.get(n.index);
			for (Integer theNeiborIndex : neiborsIndex) {

				Node theNeibor = allNodes.get(theNeiborIndex);
				if (theNeibor.path == 0) {
					theNeibor.path = path;
					thePath.push(theNeibor);
					if (check(theNeibor, path))
						count(k,print);

					theNeibor.path = 0;
					thePath.pop();
				}
				if( theNeibor.equals(EndPoint.get(path))) {
					thePath.push(theNeibor);
					if (check(theNeibor, path))
						count(k,print);
					thePath.pop();
				}
			}
			return ;

		}
		count++;
		
		}
	
	}
	
	
	//generate the adjacent matrix of complete graph of "length" nodes
	public static int[][] completeGraph(int length) {
		int[][] res= new int[length][length];
		for(int i=0;i!=length;i++)
			for(int j=0;j!=length;j++) {
				if(i!=j)
					res[i][j]=1;
			}
		return res;
	}

	//if the input is grid graph, then transform it into a 2d array.
	public int[][] print() {
		if(n_row!=-1) {
			
		int[][] res=new int[n_row][n_col];
		
		for(Node n:allNodes.values()) {
			
			res[n.rowIndex][n.colIndex]=n.path;
		}
		
		return res;
	}
		else {
			int[][] res=new int[length][length];
			System.out.println(paths.size());
			for(Integer pi:paths.keySet()) {
				
				Stack<Node> p=paths.get(pi);
				System.out.println(p.toString());
				Node n1=p.pop();
				while(!p.isEmpty()) {
					Node n2=p.pop();
					res[n1.index][n2.index]=pi;
					res[n2.index][n1.index]=pi;
					n1=n2;
				}
				
			}
			return res;
		}
	}
	
	//print function
	public static void Print2DArray(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				System.out.print(a[i][j]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}

	
public static void main(String[] args) {
		
		
		//test normal matrix
		int[][]  origMap = new int[7][7];
		  
		  
		  origMap[2][4] = 1; origMap[5][2] = 1;
		  
		  origMap[1][4] = 2; origMap[6][0] = 2;
		 
		  origMap[0][3] = 4; origMap[6][6] = 4; 
		  
		  origMap[1][1] = 3; origMap[2][3] = 3;
		  
		 origMap[1][5] = 5; origMap[3][3] = 5;
		 //for(int i=0;i<10;i++) {
		 //System.out.println("********************************-----");
			 
		 NBLK numlin=new NBLK(origMap);
		 
		 System.out.println(numlin.solve());
		 Print2DArray(numlin.print());
		 
		 //numlin=new NBLK(origMap);
		 //numlin.count(5,true);
		 //System.out.println("Total Results:"+numlin.count);
		 
		 //}
		  
		
		/*
		//test normal matrix
		int[][]  origMap = new int[3][3];
		  
		  
		  origMap[0][0] = 1; origMap[1][2] = 1;
		  origMap[1][1] = 2; origMap[2][2] = 2;
		 
		 
		 NBLK numlin=new NBLK(origMap);
		 
		 //System.out.println(numlin.solve());
		 //Print2DArray(numlin.print());
		 numlin=new NBLK(origMap);
		 numlin.count(false);
		 System.out.println(numlin.count);
		 
		 //}
		  */
		 
		 
		 
		 
		 
		 /*
		//test graph adjacent matrix
		int[][] adjaM=completeGraph(9);
		HashMap<Integer, Node> sps=pointList(new int[] {0,3,6});
		HashMap<Integer, Node> eps=pointList(new int[] {2,5,8});
		NBLK numlin=new NBLK(adjaM,sps,eps);
		 
		 
		 //numlin.count();
		 System.out.println(numlin.solve());
		 Print2DArray(numlin.print());
		 
		  */
	}
	
}
