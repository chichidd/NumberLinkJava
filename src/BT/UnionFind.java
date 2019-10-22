package BT;

import java.util.HashMap;
import java.util.LinkedList;

public class UnionFind {
	//parent relation, parent.put(src,dst) indicates that src points to dst
    private HashMap<Node,Node> parent;
    
    public UnionFind( ){
        
    	parent=new HashMap<Node,Node>();
    }
    
    public Node find( Node src ){
        
    	Node p=parent.get(src);
    	Node p0=src;
    	LinkedList<Node> l=new LinkedList<Node>();
    	//-1 represent
    	while(p!=null) {
    		l.push(p0);
    		p0=p;
    		p=parent.get(p);
    	}
    	
    	//compression
		if (!l.isEmpty()) {
			l.pop();// lastone no need to modify
			for (Node p1 : l) {
				parent.replace(p1, p0);
			}
		}
    	return p0;
    }
    
    public void union( Node v0, Node v1 ){
        
    	Node p1=find(v1);
    	Node p0=find(v0);
    	if(!p1.equals(p0)) {
    		parent.put(p0, p1);
    	}
    	return;
    }
    
}