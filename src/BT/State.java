package BT;

import java.util.HashMap;
import java.util.Stack;

public class State {

	
	public HashMap<Integer, Stack<Node>> paths;
	public HashMap<Integer,Node> allNodes;
	public int num_freeCases;
	public int lastChangedPath;
	public boolean complete;
	

	State(HashMap<Integer, Stack<Node>> ps, HashMap<Integer,Node> allN,int nfreeCases,int lChangedPathIndex,boolean c){
		
		paths=new HashMap<Integer, Stack<Node>>(ps);
		allNodes=new HashMap<Integer, Node>(allN);
		num_freeCases=nfreeCases;
		lastChangedPath=lChangedPathIndex;
		complete=c;
	}

	State(State s){
		paths=new HashMap<Integer, Stack<Node>>();
		for(Integer p:s.paths.keySet()) {
			Stack<Node> sn=new Stack<Node>();
			Stack<Node> sn1=new Stack<Node>();
			while(!s.paths.get(p).isEmpty()) {
				Node n=s.paths.get(p).pop();
				sn.push(n);
			}
			while(!sn.isEmpty()) {
				Node n=sn.pop();
				Node n1=new Node(n.rowIndex,n.colIndex,n.index,n.path);
				s.paths.get(p).push(n);
				sn1.push(n1);
			}
			
			paths.put(p, sn1);
		}
		
		
		allNodes=new HashMap<Integer, Node>();
		for(Integer p:s.allNodes.keySet()) {
			Node n=new Node(s.allNodes.get(p).rowIndex,
			s.allNodes.get(p).colIndex,
			s.allNodes.get(p).index,
			s.allNodes.get(p).path);
			allNodes.put(p, n);
		}
		num_freeCases=s.num_freeCases;
		lastChangedPath=s.lastChangedPath;
		complete=s.complete;
	
		
		
	}


}
