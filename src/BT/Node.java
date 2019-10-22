package BT;

public class Node {

	public int rowIndex;
	public int colIndex;
	public int index;
	public int path;
	//public LinkedList<Node> neibors;
	Node(int row, int col, int i){
		rowIndex=row;
		colIndex=col;
		index=i;
		path=0;
	}

	Node(int row, int col, int i,int p){
		rowIndex=row;
		colIndex=col;
		index=i;
		path=p;
	}

	Node(int i){
		rowIndex=-1;
		colIndex=-1;
		index=i;
		path=0;
	}
	Node(int i,int p){
		rowIndex=-1;
		colIndex=-1;
		index=i;
		path=p;
	}

	
	/*
	public void addNeibor(Node n) {
		neibors.add(n);
	}
	*/
	@Override
	public int hashCode() {
		return index*100;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node))
			return false;
		
		Node n=(Node)obj;
		if(n.index==index)
			return true;
		return false;
		
	}
	@Override
	public String toString() {
		return "[index:"+index+"| col:"+colIndex+"| "+"| row:"+rowIndex+"| path:"+path+"]";
	}
}
