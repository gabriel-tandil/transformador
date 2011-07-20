package representacion;

public class Vector3D extends VectorN {

	  public Vector3D crossProduct (Vector3D v)
	  {
		  Vector3D vector3D = new Vector3D();
	    
		  vector3D.set(0, get(1) * v.get(2) - get(2) * v.get(1));
		  vector3D.set(1, get(2) * v.get(0) - get(0) * v.get(2));
		  vector3D.set(2, get(0) * v.get(1) - get(1) * v.get(0));

	    return vector3D;
	  }
	
	
	public Vector3D() {
		super(4);
	} // <B>create a new 3D homogeneous vector</B>

	public double getX() {
		return get(0);
	}

	public double getY() {
		return get(1);
	}

	public double getZ() {
		return get(2);
	}

	void normalize() {
		double tmp;
		tmp = get(0) * get(0) + get(1) * get(1) + get(2) * get(2);
		tmp = Math.sqrt(tmp);
		set(get(0) / tmp, get(1) / tmp, get(2) / tmp);
	}

	public void set(double x, double y, double z) {
		set(x, y, z, 1);
	} // <B>set value of a 3D point</B>

	void set(double x, double y, double z, double w) { // <B>set value of
														// vector</B>
		set(0, x);
		set(1, y);
		set(2, z);
		set(3, w);
	}

	void set(Vector3D vec) { // <B>copy from another vector</B>
		for (int j = 0; j < size(); j++) {
			set(j, vec.get(j));
		}
	}

}

class VectorN {
	private double v[];

	VectorN(int n) {
		v = new double[n];
	} // <B>create a new vector</B>

	public VectorN invert() {
		VectorN tmp = new VectorN(size());
		for (int j = 0; j < size(); j++) {
			tmp.set(j, this.get(j) * -1);
		}
		return tmp;
	}	
	
	double distance(VectorN vec) { // <B>euclidean distance</B>
		double x, y, d = 0;
		for (int i = 0; i < size(); i++) {
			x = vec.get(0) - get(0);
			y = vec.get(1) - get(1);
			d += x * x + y * y;
		}
		return Math.sqrt(d);
	}

	public double dotproduct(VectorN vec) { // dot product of this X vec FOR FIRST N-1
										// ELEMENTS, not last 1
		double tmp = 0.0;
		for (int i = 0; i < size() - 1; i++) {
			tmp += get(i) * vec.get(i);
		}
		return tmp;
	}

	double get(int j) {
		return v[j];
	} // <B>get one element</B>

	public boolean isAlmostEqual(VectorN vectorN, double x) {
		boolean diferente = false;
		int j = 0;
		while (j < size() && !diferente) {
			if (Math.abs(get(j) - vectorN.get(j)) > x) {
				diferente = true;
			}
			j++;
		}
		return !diferente;
	}

	void set(int j, double f) {
		v[j] = f;
	} // <B>set one element</B>

	void set(VectorN vec) { // <B>copy from another vector</B>
		for (int j = 0; j < size(); j++) {
			set(j, vec.get(j));
		}
	}

	public void add(VectorN vec) { 
		for (int j = 0; j < size(); j++) {
			set(j, get(j)+vec.get(j));
		}
	}	
	
	public void dif(VectorN vec) { 
		for (int j = 0; j < size(); j++) {
			set(j, get(j)-vec.get(j));
		}
	}	


	public void multEscalar(Double esc) { 
		for (int j = 0; j < size(); j++) {
			set(j, get(j)*esc);
		}
	}	

	public void divEscalar(Double esc) { 
		for (int j = 0; j < size(); j++) {
			set(j, get(j)/esc);
		}
	}	
	
	
	
	int size() {
		return v.length;
	} // <B>return vector size</B>

	@Override
	public String toString() { // <B>convert to string representation</B>
		return toString(size());
	}

	public String toString(int puntos) {
		String s = "{";
		for (int j = 0; j < puntos; j++) {
			s += (j == 0 ? "" : ",") + get(j);
		}
		return s + "}";
	}

	public void transform(MatrizN mat) { // <B>multiply by an N x N matrix</B>
		VectorN tmp = new VectorN(size());
		double f;

		for (int i = 0; i < size(); i++) {
			f = 0.;
			for (int j = 0; j < size(); j++) {
				f += mat.get(i, j) * get(j);
			}
			tmp.set(i, f);
		}
		set(tmp);
	}
}