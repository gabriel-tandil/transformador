package representacion;

public class Matriz3D extends MatrizN {

	public Matriz3D() {
		super(4);
		identity();
	}

	// ROTA EN EL EJE X
	public void rotateX(double theta) {

		Matriz3D tmp = new Matriz3D();
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		tmp.set(1, 1, c);
		tmp.set(1, 2, -s);
		tmp.set(2, 1, s);
		tmp.set(2, 2, c);

		preMultiply(tmp);
	}

	// ROTA LA matriz en el eje y
	public void rotateY(double theta) {

		Matriz3D tmp = new Matriz3D();
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		tmp.set(2, 2, c);
		tmp.set(2, 0, -s);
		tmp.set(0, 2, s);
		tmp.set(0, 0, c);

		preMultiply(tmp);
	}

	// rota la matriz en el eje z
	public void rotateZ(double theta) {

		Matriz3D tmp = new Matriz3D();
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		tmp.set(0, 0, c);
		tmp.set(0, 1, -s);
		tmp.set(1, 0, s);
		tmp.set(1, 1, c);

		preMultiply(tmp);
	}

	// escala uniformemente
	public void scale(double s) {

		Matriz3D tmp = new Matriz3D();

		tmp.set(0, 0, s);
		tmp.set(1, 1, s);
		tmp.set(2, 2, s);

		preMultiply(tmp);
	}

	// escala en cada eje el valor parametro
	public void scale(double r, double s, double t) {

		Matriz3D tmp = new Matriz3D();

		tmp.set(0, 0, r);
		tmp.set(1, 1, s);
		tmp.set(2, 2, t);

		preMultiply(tmp);
	}

	// escala en cada eje el vector parametro
	public void scale(Vector3D v) {
		scale(v.get(0), v.get(1), v.get(2));
	}

	// translada segun las coordenadas
	public void translate(double a, double b, double c) {

		Matriz3D tmp = new Matriz3D();

		tmp.set(0, 3, a);
		tmp.set(1, 3, b);
		tmp.set(2, 3, c);

		preMultiply(tmp);
	}

	// transalada segun el vector
	public void translate(Vector3D v) {
		translate(v.get(0), v.get(1), v.get(2));
	}

	// transpone
	public void transpose3() {
		Matriz3D tmp = new Matriz3D();

		tmp.set(this);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				set(i, j, tmp.get(j, i));
			}
		}
	}
}

class MatrizN {
	public VectorN v[];

	// contructor matriz cuadrada
	MatrizN(int n) {
		v = new VectorN[n];
		for (int i = 0; i < n; i++) {
			v[i] = new VectorN(n);
		}
	}

	VectorN get(int i) {
		return v[i];
	}

	double get(int i, int j) {
		return get(i).get(j);
	}

	void identity() {
		for (int j = 0; j < size(); j++) {
			for (int i = 0; i < size(); i++) {
				set(i, j, (i == j ? 1 : 0));
			}
		}
	}

	void postMultiply(MatrizN mat) {
		MatrizN tmp = new MatrizN(size());
		double f;

		for (int j = 0; j < size(); j++) {
			for (int i = 0; i < size(); i++) {
				f = 0.;
				for (int k = 0; k < size(); k++) {
					f += get(i, k) * mat.get(k, j);
				}
				tmp.set(i, j, f);
			}
		}
		set(tmp);
	}

	void preMultiply(MatrizN mat) {
		MatrizN tmp = new MatrizN(size());
		double f;

		for (int j = 0; j < size(); j++) {
			for (int i = 0; i < size(); i++) {
				f = 0.;
				for (int k = 0; k < size(); k++) {
					f += mat.get(i, k) * get(k, j);
				}
				tmp.set(i, j, f);
			}
		}
		set(tmp);
	}

	void set(int i, int j, double f) {
		v[i].set(j, f);
	}

	void set(int i, VectorN vec) {
		v[i].set(vec);
	}

	void set(MatrizN mat) {
		for (int i = 0; i < size(); i++) {
			set(i, mat.get(i));
		}
	}

	int size() {
		return v.length;
	}

	@Override
	public String toString() {
		String s = "{";
		for (int i = 0; i < size(); i++) {
			s += (i == 0 ? "" : "\n,") + get(i);
		}
		return s + "}";
	}
}
