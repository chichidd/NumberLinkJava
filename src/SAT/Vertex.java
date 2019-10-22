package SAT;
public class Vertex {

	public int index;
	// except index, other attributes are not necessary when dealing with other
	// graph, we can simply set them 0
	public int row_index;
	public int col_index;

	Vertex(int a, int b, int c) {
		row_index = a;
		col_index = b;
		index = c;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vertex))
			return false;
		else {
			if (((Vertex) obj).index == this.index && ((Vertex) obj).row_index == this.row_index
					&& ((Vertex) obj).col_index == this.col_index) {
				return true;
			} else
				return false;
		}

	}

	@Override
	public int hashCode() {
		return index * 1000 + row_index * 50 + col_index;
	}

	@Override
	public String toString() {
		return "index: " + index + ",row:" + row_index + ",col:" + col_index;
	}
}
