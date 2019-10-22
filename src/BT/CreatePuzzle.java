package BT;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import SAT.*;

public class CreatePuzzle{
	public int n_row;
	public int n_col;
	public int length;
	public int[][] origMap;
	public int[][] adjaMatrix;
	
	public HashMap<Integer,Node> allNodes;
	public HashMap<Integer,LinkedList<Integer>> nowMap;
	
	public HashMap<Integer,LinkedList<Integer>> paths;
	//path number can be set as its index plus one.
	public LinkedList<Integer> restNodes;
	
	public void init() {}
	
	CreatePuzzle(int[][] origMap,int nrow,int ncol){
		//used for 2D numberlink
		n_row=nrow;
		n_col=ncol;
		length=n_row*n_col;
		this.origMap=origMap;
		adjaMatrix=new int[length][length];
		allNodes=new HashMap<Integer,Node>();
		nowMap=new HashMap<Integer,LinkedList<Integer>>();
		paths=new HashMap<Integer,LinkedList<Integer>>();
		for(int i=0;i!=n_row;i++) {
			for(int j=0;j!=n_col;j++) {
				if (origMap[i][j] > -1) {
					//we can use that point to represent a blocked point (i,j) as origMap[i][j]=-1
					int index = i * n_col + j;
					Node n = new Node(i, j, index, 0);
					allNodes.put(index, n);
					LinkedList<Integer> neibors = new LinkedList<Integer>();

					if (i > 0 && origMap[i-1][j] > -1) {
						adjaMatrix[index][(i - 1) * n_col + j] = 1;
						adjaMatrix[(i - 1) * n_col + j][index] = 1;
						neibors.add((i - 1) * n_col + j);
					}
					if (i < n_row - 1 && origMap[i+1][j] > -1) {

						adjaMatrix[index][(i + 1) * n_col + j] = 1;
						adjaMatrix[(i + 1) * n_col + j][index] = 1;
						neibors.add((i + 1) * n_col + j);
					}
					if (j > 0 && origMap[i][j-1] > -1) {
						adjaMatrix[index][i * n_col + j - 1] = 1;
						adjaMatrix[i * n_col + j - 1][index] = 1;
						neibors.add(i * n_col + j - 1);
					}
					if (j < n_col - 1 && origMap[i][j+1] > -1) {
						adjaMatrix[index][i * n_col + j + 1] = 1;
						adjaMatrix[i * n_col + j + 1][index] = 1;
						neibors.add(i * n_col + j + 1);
					}

					nowMap.put(index, neibors);

				}
			}
		}
	}
	

	CreatePuzzle(int[][] adjaMatrix,int l){
		//used for general graph
		n_col=-1;
		n_row=-1;
		origMap=null;
		length=l;
		this.adjaMatrix=adjaMatrix;
		allNodes=new HashMap<Integer,Node>();
		nowMap=new HashMap<Integer,LinkedList<Integer>>();
		paths=new HashMap<Integer,LinkedList<Integer>>();
		for(int i=0;i!=length;i++) {
			allNodes.put(i, new Node(-1,-1,i,0));
			LinkedList<Integer> neibor=new LinkedList<Integer>();
			for(int j=0;j!=length;j++) {
				if(adjaMatrix[i][j]>0) {
					//point of index i and the point of index j are connected
					neibor.add(j);
				}
			}
			nowMap.put(i, neibor);
		}
	}
	
	public void createPuzzle() {
		//begin initialize restNodes
		restNodes=new LinkedList<Integer>();
		if(n_col==-1)
		for(int i=0;i!=length;i++) 
			restNodes.add(i);
		else {
			for(int i=0;i!=n_row;i++) 
				for(int j=0;j!=n_col;j++)
					if(origMap[i][j]>-1)
						restNodes.add(i*n_col+j);
		}
		//end initialize restNodes
		
		int pathIndex=1;
		while(restNodes.size()!=0) {	
			LinkedList<Integer> thePath=new LinkedList<Integer>();
			int departIndex=chooseAndDelete(restNodes);
			
			thePath.addLast(departIndex);
			paths.put(pathIndex,thePath);
			allNodes.get(departIndex).path=pathIndex;
			/*
			LinkedList<Integer> isolatePoints=causeIsolatePoint(departIndex,pathIndex);
			if(!isolatePoints.isEmpty()) {
				//we may create an isolate point because of random selection
				for(Integer i:isolatePoints)
				{
					thePath.addFirst(i);
					allNodes.get(i).path=pathIndex;
					restNodes.remove(restNodes.indexOf(i));
				}
			}
			*/
			finishPath(thePath,pathIndex);
			pathIndex++;
		}
		
		
		minimizePathsNumber();
		//System.out.println("processed:");
		//print();
	}
	
	//choose randomly an element of list and return its index
	public int chooseIndexRandomly(LinkedList<Integer> list) {
		double ran=Math.random();
		
		int res= (int)(ran*list.size());
		//!!!attention, (int) will turn the first variable into integer, so we need to add a pair of parentheses to ran*list.size()
		return res;	
	}
	
	//choose randomly and delete an element of list
	public int chooseAndDelete(LinkedList<Integer> list) {
		return list.remove(chooseIndexRandomly(list));
	}
	
	//choose randomly an element of list
	public int chooseWithoutDelete(LinkedList<Integer> list) {
		return list.get(chooseIndexRandomly(list));
	}
	
	//return all the non-occupied neighbors of lastPointIndex.
	public LinkedList<Integer> nonOccupiedNeighbors(int lastPointIndex){
		//find all the neighbor points that haven't been occupied by a path; 
		LinkedList<Integer> res=new LinkedList<Integer>();
		for(Integer neighborIndex:nowMap.get(lastPointIndex)) {
			if(allNodes.get(neighborIndex).path==0)
				res.add(neighborIndex);
		}
		return res;
	}
	
	//extend the path as far as we can.
	public void finishPath(LinkedList<Integer> thePath,int pathIndex) {
	
		int times=0;
		//we may set the maximum long of a path as length/3 if we replace true by times<length/4, then add times++; in while loop.

		while (times<=length/3) {
			times++;
			//print();
			int lastPointIndex = thePath.getLast();

			LinkedList<Integer> neighbors = nonOccupiedNeighbors(lastPointIndex);
			LinkedList<Integer> neighborsOktoChoose = new LinkedList<Integer>();

			for (Integer theNeighbor : neighbors) {
				if (!thePath.contains(theNeighbor)) {
					if (ableToChoose(theNeighbor, pathIndex)) {
						neighborsOktoChoose.add(theNeighbor);
					}
				}
			}
			// choose randomly a neighbor and add it to the end of path.
			if (!neighborsOktoChoose.isEmpty()) {
				
				int i=chooseWithoutDelete(neighborsOktoChoose);
				
				/*if(neighborsOktoChoose.size()>1)
				{
					System.out.println("all choices:"+neighborsOktoChoose.toString()+", the choice:"+i);
				}*/
				thePath.addLast(i);
				allNodes.get(i).path=pathIndex;
				restNodes.remove(restNodes.indexOf(i));
			}
			else
				break;
		}
	}
	
	//try to merge some small paths.
	public void minimizePathsNumber() {
		
		//System.out.println("Before miniziation:________________");
		//print();
		//System.out.println("___________________________________");
		//Part 1:
		//deal with the paths with one element. Those paths are mainly caused due to defaults of "causeIsolatePoint"
		LinkedList<Integer> toDelete=new LinkedList<Integer>();//used for delete key of a hashmap
		for(Integer path:paths.keySet()) {
			LinkedList<Integer> thePath=paths.get(path);
			if(thePath.size()==1) {
				int pointIndex=thePath.getFirst();
				
				for (Integer neighborIndex : nowMap.get(pointIndex)) {
					// find the neighbor which is head or tail of the path which only has one point
					// near the point.
					// It's ok to form a cycle path because we will deal with it later.

					int neighborPathIndex = allNodes.get(neighborIndex).path;
					// detect the case(the 1 below 8 is the head of path 1):
					// 5 5 1
					// 4 8 1
					// 4 1 1
					boolean trueEnd = true;
					for (Integer neighborIndex1 : nowMap.get(pointIndex)) {
						if (neighborIndex1 != neighborIndex) {
							if (allNodes.get(neighborIndex1).path == neighborPathIndex) {
								trueEnd = false;
								break;
							}
						}
					}
					if (trueEnd) {
						LinkedList<Integer> neighborPath = paths.get(neighborPathIndex);
						if (neighborPath.getFirst() == neighborIndex) {
							neighborPath.addFirst(pointIndex);
							allNodes.get(pointIndex).path = neighborPathIndex;
							toDelete.add(path);
							break;
						} else if (neighborPath.getLast() == neighborIndex) {
							neighborPath.addLast(pointIndex);
							allNodes.get(pointIndex).path = neighborPathIndex;
							toDelete.add(path);
							break;
						}
					}
				}
				
			}
		}
		for(Integer i:toDelete)
			paths.remove(i);
		
		//Part 2:
		//Connect small passes
		for(Integer path:paths.keySet()) {
			LinkedList<Integer> thePath=paths.get(path);
			if (!thePath.isEmpty() && thePath.size()<4) {
				int headIndex = thePath.getFirst();
				int lastIndex = thePath.getLast();
				//the path number of a checkable neighbor must appear only once
				HashMap<Integer,Integer> checkableNeighborsofHead=new HashMap<Integer,Integer>();
				HashMap<Integer,Integer> checkableNeighborsofLast=new HashMap<Integer,Integer>();
				//count it
				for(Integer neighborOfHeadIndex:nowMap.get(headIndex)) {
					
					int p=allNodes.get(neighborOfHeadIndex).path;
					if(p!=path && !paths.get(p).isEmpty()) {
					if(checkableNeighborsofHead.containsKey(p))
						checkableNeighborsofHead.put(p, -1);
					else
						checkableNeighborsofHead.put(p, neighborOfHeadIndex);
					}
				}
				for(Integer neighborOfLastIndex:nowMap.get(lastIndex)) {
					
					int p=allNodes.get(neighborOfLastIndex).path;
					if(p!=path && !paths.get(p).isEmpty()) {
					if(checkableNeighborsofLast.containsKey(p))
						checkableNeighborsofLast.put(p, -1);
					else
						checkableNeighborsofLast.put(p, neighborOfLastIndex);
					}
				}
				
				//the intersection of two keysets must be cut off , 
				toDelete=new LinkedList<Integer>();
				for(Integer i:checkableNeighborsofHead.keySet()) {
					if(checkableNeighborsofLast.containsKey(i))
					{	
						checkableNeighborsofLast.remove(i);
						toDelete.add(i);
					}
					
				}
				for(Integer i:toDelete)
					checkableNeighborsofHead.remove(i);
				
				//then find a right path to extend
				for(Integer i:checkableNeighborsofHead.keySet()) {
					int neighborIndex=checkableNeighborsofHead.get(i);
					if(neighborIndex!=-1) {
						if(paths.get(i).getFirst()==neighborIndex) {
							while(!paths.get(i).isEmpty()) {
								int j=paths.get(i).poll();
								thePath.addFirst(j);
								allNodes.get(j).path=path;
							}
							paths.get(i).clear();
							break;
						}
						else if(paths.get(i).getLast()==neighborIndex) {
							while(!paths.get(i).isEmpty()) {
								int j=paths.get(i).removeLast();
								thePath.addFirst(j);
								allNodes.get(j).path=path;
							}
							paths.get(i).clear();
							break;
						}
					}
				}

				for(Integer i:checkableNeighborsofLast.keySet()) {
					int neighborIndex=checkableNeighborsofLast.get(i);
					if(neighborIndex!=-1) {
						if(paths.get(i).getFirst()==neighborIndex) {
							while(!paths.get(i).isEmpty()) {
								int j=paths.get(i).poll();
								thePath.addLast(j);
								allNodes.get(j).path=path;
							}
							paths.get(i).clear();
							break;
						}
						else if(paths.get(i).getLast()==neighborIndex) {
							while(!paths.get(i).isEmpty()) {
								int j=paths.get(i).removeLast();
								thePath.addLast(j);
								allNodes.get(j).path=path;
							}
							paths.get(i).clear();
							break;
						}
					}
				}
			}
		}
		toDelete=new LinkedList<Integer>();
		for(Integer i:paths.keySet())
			if(paths.get(i).isEmpty())
				toDelete.add(i);
		for(int i:toDelete) {
			paths.remove(i);
		}
		//System.out.println("After miniziation:________________");
		//print();
		//System.out.println("___________________________________");
		
	}
	
	//check if the path of pathIndex can be extended into the node whose index is testNeighborIndex.
	public boolean ableToChoose(int testNeighborIndex,int pathIndex) {
		int count=0;
		for(Integer neighborIndex:nowMap.get(testNeighborIndex)) {
			if(allNodes.get(neighborIndex).path==pathIndex)
				count++;
			if(!causeIsolatePoint(testNeighborIndex,pathIndex).isEmpty()) {
				return false;
			}
		}
		if(count>1)
			return false;//prevent the case where testPoint have two neighbors in the same path.
		return true;
	}

	//check if the path of pathIndex occupies node whose index is testNeighborIndex may produce an isolate node.
	public LinkedList<Integer> causeIsolatePoint(int testNeighborIndex,int pathIndex) {
		//an isolate point is a non-occupied point whose neighbors are occupied by a path
		LinkedList<Integer> res=new LinkedList<Integer>();
		boolean ok=false;
		for(Integer neighborOftestNeiborIndex:nowMap.get(testNeighborIndex)) {
			
			if(allNodes.get(neighborOftestNeiborIndex).path==0) {
				//get rid of occupied points, including the last point of the path we are considering
				//test if we use this neighbor point, one of neighbor points of this neighbor point will become isolate
				
				for(Integer i:nowMap.get(neighborOftestNeiborIndex)) {
					if(i!=testNeighborIndex) 
					{
						
						if( allNodes.get(i).path==0)
							ok=true;
					}
				}
				
				//will cause isolate point, so the test neighbor can't become the next point of the path we are considering.
				if(!ok) {
						res.add(neighborOftestNeiborIndex);
				}
				ok=false;
			}
				
		}
		return res;
	}
	
	//print the map for 2D grid graph.
	public void print() {
		if(n_col!=-1) {
			int[][] a=new int[n_row][n_col];
			for(Integer path:paths.keySet()) {
				LinkedList<Integer> thePath=paths.get(path);
					for(Integer index:thePath) {
						int i=index/n_col;
						int j=index%n_col;
						a[i][j]=path;
					}
			}
			System.out.println("---------------------");
			NBLK.Print2DArray(a);
		}
	}
	
	//return a hashmap whose key is path number and whose value is path list.
	public HashMap<Integer, LinkedList<Integer>> result(){
		//the number of each path can be changed if we want
		HashMap<Integer, LinkedList<Integer>> res=new HashMap<Integer, LinkedList<Integer>>();
		for(Integer i:paths.keySet()) {
			LinkedList<Integer> temp=new LinkedList<Integer>();
			temp.add(paths.get(i).getFirst());
			temp.add(paths.get(i).getLast());
			res.put(i, temp);
		}
		return res;
	}
	
	public int[][] resultFor2D(){
		int[][] res= new int[n_row][n_col];
		for(int i=0;i!=n_row;i++)
			for(int j=0;j!=n_col;j++) {
				res[i][j]=origMap[i][j];
			}
		HashMap<Integer, LinkedList<Integer>> l=result();
		for(int p:l.keySet()) {
			res[l.get(p).getFirst()/n_col][l.get(p).getFirst()%n_col]=p;
			res[l.get(p).getLast()/n_col][l.get(p).getLast()%n_col]=p;
		}
		System.out.println("*************RESULT*2D*NUMBERLINK*************");
		NBLK.Print2DArray(res);
		System.out.println("**********************************************");
		return res;
	}
	
	public static void main(String[] args) {
		
		
		int[][] t= ReadImage("./src/christmasTree.png");
		t=makeImageSmall(t,10,10);
		
		testSAT1(t);
		
	}

	//Method 1 : as long as there exists more than one solutions, we recalculate.
	public static void testBT1(int[][] t) {
		int[][] test;
		NBLK solver;
		int times=1;
		long t1=System.currentTimeMillis();
		do {
			System.out.println("Try "+times+" :");
			CreatePuzzle cp = new CreatePuzzle(t, t.length, t[0].length);
			cp.createPuzzle();
			test = cp.resultFor2D();
			System.out.println("Its solution:");
			cp.print();
			System.out.print("number of solution by backtracking algo:");
			solver = new NBLK(test);
			solver.count(2,false);
			System.out.println(solver.count);
			if(solver.count==1)
			{
				
				System.out.println("****Test solution****:");
				solver=new NBLK(test);
				solver.solve();
				NBLK.Print2DArray(solver.print());
				
				break;
			}
			times++;
		} while (solver.count != 1);
		Long t2=System.currentTimeMillis();
		System.out.println("****Final result****:");
		NBLK.Print2DArray(test);
		
		System.out.println("Finished in "+(t2-t1)+"ms.\n");
	}
	
	public static void testSAT1(int[][] t) {

		int[][] test;
		int numberOfSolution=0;
		int times=1;
		long t1=System.currentTimeMillis();
		do {
			System.out.println("Try "+times+" :");
			CreatePuzzle cp = new CreatePuzzle(t, t.length, t[0].length);
			cp.createPuzzle();
			test = cp.resultFor2D();
			System.out.println("Its solution:");
			cp.print();
			System.out.print("number of solution by SAT solver:");
			numberOfSolution=Task5.allSolution(test,false,2);
			System.out.println(numberOfSolution);
			times++;
			if(numberOfSolution==0) {
				System.out.println("Attention:");
				System.out.println(cp.paths.toString());
				
			}
		} while (numberOfSolution != 1);
		long t2=System.currentTimeMillis();
		System.out.println("****Final result****:");
		NBLK.Print2DArray(test);
		System.out.println("Finished in "+(t2-t1)+"ms.\n");
	}

	//Methode 2 : we cut the longest path into two.
	public static void testBT2(int i,int j) {
		int[][] t=new int[i][j];

		int[][] test;
		NBLK solver;
		int times=1;
		CreatePuzzle cp = new CreatePuzzle(t, t.length, t[0].length);
		cp.createPuzzle();
		test = cp.resultFor2D();
		System.out.println("Its solution:");
		cp.print();
		long t1=System.currentTimeMillis();
		do {
			
			System.out.println("Try "+times+" :");
			System.out.print("number of solution by backtracking algo:");
			
			solver = new NBLK(test);
			solver.count(2,false);;
			System.out.println(solver.count);
			times++;
			if(solver.count>1) {
				LinkedList<Integer> longestPath=new LinkedList<Integer>();
				int maxlong=0;
				int itsPnumber=0;
				int maxPathNumber=0;
				for(Integer pathNumber:cp.paths.keySet()) {
					LinkedList<Integer> thePath=cp.paths.get(pathNumber);
					if(thePath.size()>maxlong) {
						maxlong=thePath.size();
						longestPath=thePath;
						itsPnumber=pathNumber;
					}
					if(pathNumber>maxPathNumber)
						maxPathNumber=pathNumber;
				}
				if (!longestPath.isEmpty()) {
					int cutIndex = (int)(longestPath.size() / 2);
					// or we can use random function to decide where to cut.
					LinkedList<Integer> subPath1=new LinkedList<Integer>();
					LinkedList<Integer> subPath2=new LinkedList<Integer>();
					
					//finish the two subpath
					for(int a=0;a<cutIndex;a++) 
						subPath1.addLast(longestPath.get(a));
					
					for(int a=cutIndex;a<longestPath.size();a++) 
						subPath2.addLast(longestPath.get(a));
					
					cp.paths.put(itsPnumber, subPath1);
					cp.paths.put(maxPathNumber+1, subPath2);
					for(Integer n:subPath2) {
						cp.allNodes.get(n).path=maxPathNumber+1;
					}
				}
			}
			test = cp.resultFor2D();
		} while (solver.count != 1);
		long t2=System.currentTimeMillis();
		System.out.println("****Final result****:");
		NBLK.Print2DArray(test);
		System.out.println("Finished in "+(t2-t1)+"ms.\n");
	}
	
	//read an image 
    public static int[][] ReadImage(String imgfile){  
        File file = new File(imgfile);  
        BufferedImage bi = null;  
        try {  
            bi = ImageIO.read(file);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  

        int width = bi.getWidth();  
        int height = bi.getHeight();  
        int minx = bi.getMinX();  
        int miny = bi.getMinY();  
        int[][] allcolor=new int[height-miny][width-minx];
        for (int j = miny; j < height; j++) { 
            for (int i = minx; i < width; i++) {  
                int pixel = bi.getRGB(i, j); // �������д��뽫һ������ת��ΪRGB����  
                if(pixel<-1)
                	allcolor[j-miny][i-minx] =0; 
                else 
                	allcolor[j-miny][i-minx] =-1; 
            }
        }  
        return allcolor;
    }  

    //turn image into an 2D array of "newRow" rows and "newCol" columns
    public static int[][] makeImageSmall(int[][] t,int newRow,int newCol){
    	int row=t.length;
    	int col=t[0].length;
    	int a=(int)(row/newRow);
    	int b=(int)(col/newCol);
    	int[][] res=new int[newRow][newCol];
    	for(int i=0;i!=newRow;i++)
    		for(int j=0;j!=newCol;j++) {
    			res[i][j]=t[i*a][j*b];
    		}
    	return res;
    }
}
