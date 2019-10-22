package SAT;
public class Varia {

	public int v;
	public int i;
	public int p;
	
	Varia(int a, int b, int c){
		v=a;
		i=b;
		p=c;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Varia))
		      return false;
		if(((Varia)obj).i==i && ((Varia)obj).v==v && ((Varia)obj).p==p)
			return true;
		return false;	
	}
	@Override
	public int hashCode() {
		
		return 100000000*v+10000*i+p;
		
	}
}
