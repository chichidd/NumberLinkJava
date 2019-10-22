package BT;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

public class NBLK1 extends NBLK{

	static boolean fastforwardMode=true;
	static boolean printDetail=false;
	static boolean printInfo=false;
	static int bottleNeckTestLimit=3;
	static boolean nodeCheckTouch=true;
	static boolean nodePenalizeExploration=true;
	public int[][] print(State s) {
		//if(n_row!=-1) {}
			
		int[][] res=new int[n_row][n_col];
		
		for(Node n:s.allNodes.values()) {
			res[n.rowIndex][n.colIndex]=n.path;
		}
		
		return res;
	}
	
	
	NBLK1(int[][] origMap) {
		
		super(origMap);
		// TODO Auto-generated constructor stub
	}
	
	NBLK1(int[][] matrix, HashMap<Integer, Node> SPoint, HashMap<Integer, Node> EPoint){
		super(matrix, SPoint, EPoint);
		// TODO Auto-generated constructor stub
		
	}

	
	@Override
	public boolean solve() {
		State rootstate = new State(paths, allNodes, length - StartPoint.size() * 2, 0, false);
		optimizeFun1(rootstate);
		StateTreeNode root = new StateTreeNode(rootstate, 0, rootstate.num_freeCases, null);

		String result = "IN_PROGRESS";

		StateTreeNodeComparator stnCom = new StateTreeNodeComparator();
		PriorityQueue<StateTreeNode> stateQueue = new PriorityQueue<StateTreeNode>(stnCom);
		// NEED ADD CHECK FUNCTIONS
		stateQueue.add(root);

		int k = 0;

		while (result.equals("IN_PROGRESS")) {

			int path = 0;
			State nowState = null;
			StateTreeNode n = null;
			Node lastNodeofthePath = null;
			while (path == 0) {
				if (stateQueue.isEmpty()) {
					result = "UNREACHEABLE";
					break;
				}
				n = stateQueue.poll();
				nowState = n.state;
				if (nowState.lastChangedPath > 0 && !EndPoint.get(nowState.lastChangedPath)
						.equals(nowState.paths.get(nowState.lastChangedPath).peek()))
					path = nowState.lastChangedPath;
				else {
					path = leastChoicePath(nowState.paths, nowMap, nowState.allNodes, EndPoint);
				}
			}

			if (path == 0) {
				System.out.println("All the paths are finished but there are non-occupied nodes left.");
				break;
			} else
				lastNodeofthePath = nowState.paths.get(path).peek();

			for (Integer neighborIndex : nowMap.get(lastNodeofthePath.index)) {

				int[] arrayToGetValues = new int[2];
				boolean forced = false;
				if (!fastforwardMode)
					forced = state_find_forced(nowState, arrayToGetValues);
				int nextPath = arrayToGetValues[0];
				int nextNeighborIndex = arrayToGetValues[1];

				if (!forced) {
					nextNeighborIndex = neighborIndex;
					nextPath = path;
				}

				if (state_can_move(nowState, nextPath, nextNeighborIndex)) {

					StateTreeNode child = new StateTreeNode(n, n.state);

					int actionCost = state_make_move(child.state, nextPath, nextNeighborIndex, forced);

					// update costs - begin
					if (child.parent != null)
						child.costToCome = child.parent.costToCome + actionCost;
					else
						child.costToCome = 0;
					child.costToGo--;

					k++;
					if (printInfo) {
						if (k % 1000 == 0)
							System.out.println(k + " times ***********stateQueue length: " + stateQueue.size()
									+ ",*******There are " + child.state.num_freeCases + " free cases.");

					}
					if (printDetail) {
						System.out.println("******************");
						System.out.println("Parent state:");
						Print2DArray(print(child.parent.state));
						System.out.println(k + " times ***********stateQueue length: " + stateQueue.size()
								+ ",*******Path " + nextPath + " is changed.");
						System.out.println("child state:");
						Print2DArray(print(child.state));
						System.out.println("******************");
					}
					child = state_validate(child);

					if (child != null) {

						State childState = child.state;
						if (childState.num_freeCases == 0 && childState.complete) {
							result = "SUCCESS";
							//solutionNode = child;
							break;
						}
						stateQueue.add(child);
					} else {
						child = null;
					}

				}

				if (forced)
					break;
			}

		}

		return result.equals("SUCCESS");

	}
	
	//find if there are forced move in the state and return parameters in array a.
	public static boolean state_find_forced(State s,int[] a) {
		int nfreeNeighbor=0;
		LinkedList<Integer> freeNeighborIndex=new LinkedList<Integer>();
		for (Integer p : s.paths.keySet()) {
			if (!pathComplete(s, p)) {
				freeNeighborIndex=new LinkedList<Integer>();
				nfreeNeighbor=0;
				LinkedList<Integer> pathTopNeighbors=getPathTopNeighbors(s, p);
				for (Integer neighbor_index :pathTopNeighbors ) {
					if(s.allNodes.get(neighbor_index).path==0) {
					nfreeNeighbor++;
					freeNeighborIndex .add(neighbor_index);
					}
				}

				if (nfreeNeighbor == 0) {
					if (pathTopNeighbors.contains(EndPoint.get(p).index)) {
						a[0] = p;
						a[1] = EndPoint.get(p).index;
						return true;
					}
				} else if (nfreeNeighbor == 1) {
					if (!pathTopNeighbors.contains(EndPoint.get(p).index)) {
						a[0] = p;
						a[1] = freeNeighborIndex.getFirst() ;
						return true;
					}
					
				}
			}
		}
		return false;
	}
	
	//check if it's ok for path "path" to be extended in neighborIndex
	public static boolean state_can_move(State s, int path, int neighborIndex) {
		if(!nodeCheckTouch&&EndPoint.get(path).index==neighborIndex)
			return true;
		else if(s.allNodes.get(neighborIndex).path!=0)
			return false;
		  if (nodeCheckTouch) {
			    
			    // All puzzles are designed so that a new path segment is adjacent
			    // to at most one path segment of the same color -- the predecessor
			    // to the new segment. We check this by iterating over the
			    // neighbors.
			 
			    for (int neighborIndex1:nowMap.get(neighborIndex)) {
			      if (s.allNodes.get(neighborIndex1).path==path &&
			          !s.allNodes.get(neighborIndex1).equals(EndPoint.get(path)) && 
			          !s.allNodes.get(neighborIndex1).equals(s.paths.get(path).peek())) {
			        return false;
			      }
			    
			    }

			  }
		return true;
	}
	
	public static int state_make_move(State s,int path, int neighborIndex, boolean forced) {
		
		
		s.allNodes.get(neighborIndex).path=path;
		s.paths.get(path).push(s.allNodes.get(neighborIndex));
		s.lastChangedPath=path;
		int c=0;
		for(Node n:s.allNodes.values())
			if(n.path!=0)
				c++;
		s.num_freeCases=s.allNodes.size()-c;
		
		if(s.num_freeCases==0) {
			
			for(Integer p:s.paths.keySet())
				if(pathComplete(s,p) || getPathTopNeighbors(s,p).contains(EndPoint.get(p).index))
					s.complete=true;
				else {
					s.complete=false;
					break;
				}
			
			}
		if(!nodeCheckTouch && EndPoint.get(path).index==neighborIndex)
			return 0;
		
		int actionCost=1;
		 boolean nearEndPoint=false;
		 if (nodeCheckTouch) {
			    for (int neighborIndex1:nowMap.get(neighborIndex)) {
			      if ( s.allNodes.get(neighborIndex1).equals(EndPoint.get(path))) {
			    	  nearEndPoint=true;
			    	  s.paths.get(path).push(s.allNodes.get(neighborIndex1));//complete the path;
			    	  break;
			      }
			    
			    }
			  }

			  if (!nearEndPoint) {
			  
			    int num_free =0;
			    
			    int pathTopIndex=getPathTopIndex(s,path);
			    for (int neighborIndex1:nowMap.get(pathTopIndex)) {
			    	if(s.allNodes.get(neighborIndex1).path==0)
			    		num_free++;
			    }

			    if (nodePenalizeExploration && num_free == 2) {
			      actionCost = 2;
			    }

			  }

		
		
		
		if(forced)
			return 0;
		
		return actionCost;
		
	}
	
	//check if the path "path" is complete in state s
	public static boolean pathComplete(State s,int path) {
		return EndPoint.get(path).equals(s.paths.get(path).peek());
	}
	
	//return neighbors' index of the top element of the path "path" of state "s"
	public static int getPathTopIndex(State s,int path){
		return s.paths.get(path).peek().index;
	}
	
	//get the top element of the path "path" of state "s"
	public static LinkedList<Integer> getPathTopNeighbors(State s,int path) {
		return nowMap.get(getPathTopIndex(s,path));
	}
	
	//check the state is in dead end
	public static boolean state_is_deadend(State s, int nodeIndex) {

		int num = 0;

		for (Integer neighborIndex : nowMap.get(nodeIndex)) {
				if (s.allNodes.get(neighborIndex).path == 0)
					num++;
				else {
					for (Integer pathIndex : StartPoint.keySet()) {
						if (!s.paths.get(pathIndex).peek().equals(EndPoint.get(pathIndex))) {
							if (neighborIndex == s.paths.get(pathIndex).peek().index
									|| neighborIndex == EndPoint.get(pathIndex).index)
								num++;
						}
					}
				}
		}

		return (num <= 1);

	}
	
	//check if the new extension causes a dead end situation.
	public static boolean state_check_deadends(State s) {
		
		for(Integer path:s.paths.keySet()) {
			if(!pathComplete(s,path)) {
			Node n=s.paths.get(path).peek();
			boolean okForPath = false;
		for (Integer neiborIndex : nowMap.get(n.index)) {
			

			// Basic case check
		
			
			Node neibor = s.allNodes.get(neiborIndex);

			if (neibor.path == 0 || EndPoint.get(path).equals(neibor))
				okForPath = true;
			
			
			if (s.allNodes.get(neiborIndex).path == 0) {
				int num = 0;
			
					// BEGIN check for dead ends

					// except the node we are testing, all the other neighbor nodes from other paths
					// of the neighbor node of the node we are testing are occupied and then a dead
					// end is created.
					
					for(Integer neighborOfNeighborIndex:nowMap.get(neiborIndex)) {
						if(neighborOfNeighborIndex!=n.index) {
							if(s.allNodes.get(neighborOfNeighborIndex).path==0)
								num++;
							else {
								for(Integer pathIndex:StartPoint.keySet()) {
									if(!s.paths.get(pathIndex).peek().equals(EndPoint.get(pathIndex))){
										if(neighborOfNeighborIndex==s.paths.get(pathIndex).peek().index || neighborOfNeighborIndex==EndPoint.get(pathIndex).index)
											num++;
									}
								}
							}
						}
						else
							num++;
					}
					if(num<=1)
						return true;
					// END check for dead ends
					}

			
			}
			if(!okForPath)
			return true;
		}
		}
		return false;
	}
	
	//do union find for non-occupied regions
	public static HashMap<Integer, Integer> state_build_regions(State s) {
		UnionFind u = new UnionFind();
		HashMap<Integer, Integer> rmap=new HashMap<Integer, Integer>();
		// build Unionfind
		for (Integer nodeIndex : s.allNodes.keySet()) {

			LinkedList<Integer> neighborsIndex = nowMap.get(nodeIndex);
			if (s.allNodes.get(nodeIndex).path == 0) {
				for (Integer nIndex : neighborsIndex) {
					if (s.allNodes.get(nIndex).path == 0)
						u.union(s.allNodes.get(nIndex), s.allNodes.get(nodeIndex));
				}
			} else if (s.allNodes.get(nodeIndex).path != 0) {
				for (Integer nIndex : neighborsIndex) {
					if (s.allNodes.get(nIndex).path != 0)
						u.union(s.allNodes.get(nIndex), s.allNodes.get(nodeIndex));
				}
			}
		}

		int rcount = 1;
		LinkedList<Node> count = new LinkedList<Node>();
		// find regions
		for (Integer nIndex : s.allNodes.keySet()) {
			Node n = s.allNodes.get(nIndex);
			Node np = u.find(n);

			if (np.path == 0) {
				if (count.contains(np)) {
					rmap.put(nIndex, count.indexOf(np) + 1);
				} else {
					count.add(np);
					rmap.put(nIndex, count.indexOf(np) + 1);
				}

			} else {
				rmap.put(nIndex, -1);
			}

		}

		return rmap;
	}
	
	//check if the new extension causes stranded regions
	public static int state_regions_stranded( State s, HashMap<Integer, Integer> rmap,int chokePath,int max) {

		HashMap<Integer, LinkedList<Integer>> regions = new HashMap<Integer, LinkedList<Integer>>();
		for (Integer nodeIndex : rmap.keySet()) {
			int region = rmap.get(nodeIndex);
			if (region != -1) {
				if (regions.containsKey(region)) {
					LinkedList<Integer> l = regions.get(region);
					l.add(nodeIndex);
				} else {
					LinkedList<Integer> l = new LinkedList<Integer>();
					l.add(nodeIndex);
					regions.put(region, l);
				}
			}
		}

		HashMap<Integer, LinkedList<Integer>> regionsNeighbor = new HashMap<Integer, LinkedList<Integer>>();

		for (Integer regionIndex : regions.keySet()) {
			LinkedList<Integer> regionNode = regions.get(regionIndex);

			LinkedList<Integer> regionNeighbor = new LinkedList<Integer>();
			for (Integer nodeIndex : regionNode) {
				for (Integer itsNeighbor : nowMap.get(nodeIndex)) {
					if (!regionNode.contains(itsNeighbor))
						if (!regionNeighbor.contains(itsNeighbor))
							regionNeighbor.add(itsNeighbor);
				}
			}
			regionsNeighbor.put(regionIndex, regionNeighbor);
		}

		// Ensure this path is not "stranded" -- at least region must
	    // touch each non-completed color for both current and goal.
		boolean ok = false;
		int path_stranded=0;
		int num_stranded=0;
		for (Integer p : s.paths.keySet()) {
			if (!s.paths.get(p).peek().equals(EndPoint.get(p))&&!nowMap.get(s.paths.get(p).peek().index).contains(EndPoint.get(p).index)&& p!=chokePath) {
				ok = false;
				int topIndex = s.paths.get(p).peek().index;
				for (Integer regionIndex : regionsNeighbor.keySet()) {
					LinkedList<Integer> itsNeighbors = regionsNeighbor.get(regionIndex);

					if (itsNeighbors.contains(topIndex) && itsNeighbors.contains(EndPoint.get(p).index)) {
						ok = true;
						break;
					}

				}
				if (!ok) {
					
						num_stranded++;
						path_stranded = p;
						if(num_stranded>=max)
							return path_stranded;
					
				}
			}
		}
		
		 // For each region, make sure that there is at least one path whose
	    // current and goal positions touch it; otherwise, the region is
	    // stranded.
		for (Integer regionIndex : regionsNeighbor.keySet()) {
			LinkedList<Integer> theRegionNeighbors = regionsNeighbor.get(regionIndex);
			ok=false;
			for(Integer p:s.paths.keySet()) {
				if(theRegionNeighbors.contains(s.paths.get(p).peek().index) && theRegionNeighbors.contains(EndPoint.get(p).index))
				{
					ok=true;
					break;
				}
			}
			if(!ok) {
				return 1;
			}

		}
		return 0;
	}
	
	//check and ameliorate this state
	public static StateTreeNode state_validate(StateTreeNode c) {
		State s=c.state;
		
		if(fastforwardMode) {
		// in fast forward: begin
		int[] arrayToGetValues = new int[2];
		if (state_find_forced(s, arrayToGetValues)) {
			int nextPath = arrayToGetValues[0];
			int nextNeighborIndex = arrayToGetValues[1];

			if (!state_can_move(s, nextPath, nextNeighborIndex)) {
				return null;
			}
			
			StateTreeNode forced_child=new StateTreeNode(c,s);
			
			state_make_move(forced_child.state,nextPath,nextNeighborIndex,true);
			
			//update costs
			if(forced_child.parent!=null)
				forced_child.costToCome=forced_child.parent.costToCome;
			else
				forced_child.costToCome=0;
			forced_child.costToGo=forced_child.state.num_freeCases;
			
			forced_child=state_validate(forced_child);
			if(forced_child==null) {
				return null;
			}
			else
				return forced_child;
		}
		// in fast forward: end
		}
		

		
		//dead end check
		if(state_check_deadends(s))
			return null;
		//stranded region check
		HashMap<Integer,Integer> rmap=state_build_regions(s);
		if(state_regions_stranded(s,rmap,s.paths.size()+1,1)>0)
			return null;
		
		//bottle neck check;
		
		if(!check_bottle_neck(s,s.lastChangedPath,bottleNeckTestLimit))
			return null;
		return c;
	}
	
	public static void main(String[] args) {
		
		int[][]  origMap = new int[7][7];
		  
		  
		  origMap[2][4] = 1; origMap[5][2] = 1;
		  
		  origMap[1][4] = 2; origMap[6][0] = 2;
		 
		  origMap[0][3] = 4; origMap[6][4] = 4; 
		  
		  origMap[1][1] = 3; origMap[2][3] = 3;
		  
		 origMap[1][5] = 5; origMap[3][3] = 5;
		 //for(int i=0;i<10;i++) {
		 //System.out.println("********************************-----");
			 
		 NBLK1 nb=new NBLK1(origMap);
		 
		 System.out.println(nb.solve());
		 //Print2DArray(numlin.print());
	}
	
	//at the begin, sometimes the free neighbor end point is 1 and we can change start and end point.
	public static void optimizeFun1(State root) {
		
		int startfree=0;
		int endfree=0;
		for(int path:StartPoint.keySet()) {
			for(int i:nowMap.get(StartPoint.get(path).index)) {
				if(root.allNodes.get(i).path==0)
					startfree++;
			}
			for(int i:nowMap.get(EndPoint.get(path).index)) {
				if(root.allNodes.get(i).path==0)
					endfree++;
			}
			
			if(endfree<startfree) {
				Node temp=StartPoint.get(path);
				root.paths.get(path).pop();
				root.paths.get(path).push(EndPoint.get(path));
				StartPoint.put(path, EndPoint.get(path));
				EndPoint.put(path, temp);
				
			}
			startfree=0;
			endfree=0;
		}
	}
	
	//Only for 2D array graph
	public static boolean check_bottle_neck(State s,int path,int limit) {
		
		State cs=new State(s);
		int topPathIndex=getPathTopIndex(s,path);
		int i=topPathIndex;
		int t=0;
		int result=0;
		while(nowMap.get(i).contains(i-1) && cs.allNodes.get(i).path==0 &&t<limit) {
			//left;
			state_make_move(cs, path, i-1, true);
			i=i-1;
			t++;
		}
		HashMap<Integer, Integer> rmap=state_build_regions(cs);
		result = state_regions_stranded(cs, rmap, path, t+1);

		if (result>0) {
			return false;
		}
		
		
		cs=new State(s);
		i=topPathIndex;
		t=0;
		while(nowMap.get(i).contains(i+1) && cs.allNodes.get(i).path==0 &&t<limit) {
			//righ;
			state_make_move(cs, path, i+1, true);
			i=i+1;
			t++;
		}
		rmap=state_build_regions(cs);
		result = state_regions_stranded(cs, rmap, path, t+1);

		if (result>0) {
			return false;
		}
		cs=new State(s);
		i=topPathIndex;
		t=0;
		while(nowMap.get(i).contains(i-n_col) && cs.allNodes.get(i).path==0 &&t<limit) {
			//top;
			state_make_move(cs, path, i-n_col, true);
			i=i-n_col;
			t++;
		}
		rmap=state_build_regions(cs);
		result = state_regions_stranded(cs, rmap, path, t+1);

		if (result>0) {
			return false;
		}
		cs=new State(s);
		i=topPathIndex;
		t=0;
		while(nowMap.get(i).contains(i+n_col) && cs.allNodes.get(i).path==0 &&t<limit) {
			//left;
			state_make_move(cs, path, i+n_col, true);
			i=i+n_col;
			t++;
		}
		rmap=state_build_regions(cs);
		result = state_regions_stranded(cs, rmap, path, t+1);

		if (result>0) {
			return false;
		}

		return true;
	}
}
