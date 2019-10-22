package BT;

public class StateTreeNode {

	public State state;
	public int costToCome;
	public int costToGo;
	public StateTreeNode parent;
	StateTreeNode(State s,int a,int b,StateTreeNode p){
		state=s;
		costToCome=a;
		costToGo=b;
		parent=p;
	}
	StateTreeNode(StateTreeNode st,State s) {
		//create child
		// TODO Auto-generated constructor stub
		state=new State(s);
		
		
		costToCome=st.costToCome;
		costToGo=st.costToGo;
		parent=st;
	}
}
