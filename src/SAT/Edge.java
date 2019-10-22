package SAT;
public class Edge {

	Vertex vertex1;
	Vertex vertex2;
	// int index;

	Edge(Vertex v1, Vertex v2) {
		vertex1 = v1;
		vertex2 = v2;
		// index=v1.index*1000+v2.index;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edge))
			return false;
		else {
			if ((((Edge) o).vertex1.equals(vertex1) && ((Edge) o).vertex2.equals(vertex2))
					|| (((Edge) o).vertex1.equals(vertex2) && ((Edge) o).vertex2.equals(vertex1)))
				// not directed
				return true;
			else
				return false;
		}
	}

	public boolean containsVertex(Vertex v) {
		if (v.equals(vertex1) || v.equals(vertex2))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return vertex1.hashCode() * vertex2.hashCode();
	}

}
