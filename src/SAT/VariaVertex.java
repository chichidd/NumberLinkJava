package SAT;
public class VariaVertex {

	public Vertex vertice;
	public int path;
	public String shape;

	
	VariaVertex(Vertex v,int p,String str) {
		vertice=v;
		path=p;
		shape=str;

	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VariaVertex))
			return false;
		else {
			if (((VariaVertex)o).vertice.equals(vertice) && (( VariaVertex) o).path == path && (( VariaVertex) o).shape == shape)
				return true;
			else
				return false;
		}
	}
	
	@Override
	public int hashCode() {
		return vertice.hashCode()+path*6666+shape.hashCode();
	}
}
