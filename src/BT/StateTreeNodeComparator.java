package BT;

import java.util.Comparator;

public class StateTreeNodeComparator  implements Comparator<StateTreeNode>{


	@Override
	public int compare(StateTreeNode o1, StateTreeNode o2) {
		// TODO Auto-generated method stub
		int a=o1.costToCome+o1.costToGo;
		int b=o2.costToCome+o2.costToGo;
		if(a>b)
			return 1;
		else if(a<b)
			return -1;
		else
			return 0;
	}
}
