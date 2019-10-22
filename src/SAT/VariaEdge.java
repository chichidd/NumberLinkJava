package SAT;
public class VariaEdge {

	public Edge edge;
	public int i;

	VariaEdge(Edge e, int a) {
		i = a;
		edge = e;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VariaEdge))
			return false;
		else {
			if (((VariaEdge) o).edge.equals(edge) && ((VariaEdge) o).i == i)
				return true;
			else
				return false;
		}
	}
	
	@Override
	public int hashCode() {
		return i+edge.hashCode();
	}
}
