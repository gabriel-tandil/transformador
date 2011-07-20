package representacion;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjetoTridimensional {

	private double anguloX = 0;
	private double anguloY = 0;
	private double anguloZ = 0;
	public double[][] buf;
	protected double F = -.05;
	protected double z = .5;

	Vector3D light = new Vector3D();

	protected ModeloIluminacion lightmethod;

	protected int p;

	protected Matriz3D persmat = new Matriz3D();

	public double red, gr, bl;

	protected Matriz3D rotX = new Matriz3D();

	protected Matriz3D rotY = new Matriz3D();

	protected Matriz3D rotZ = new Matriz3D();

	public List<Matriz3D> transformaciones = new ArrayList<Matriz3D>();

	Matriz3D translacionPosRotacion = new Matriz3D();

	Matriz3D translacionPreRotacion = new Matriz3D();

	Matriz3D translacionVisualizacion = new Matriz3D();

	int w, h;

	protected boolean perspectiva = true;

	public ObjetoTridimensional() {
		super();
	}

	public ObjetoTridimensional(int w, int h, double red, double gr, double bl,
			Vector3D light, ModeloIluminacion lightmethod, int p) {
		this.w = w;
		this.h = h;
		this.red = red;
		this.gr = gr;
		this.bl = bl;
		this.lightmethod = lightmethod;
		this.light = light;

		this.p = p; // para phong
	}

	protected void computeNormal(double[] P, double[] P1, double[] P2,
			double[] P3, double[] P4) {
		Vector3D tmp1 = new Vector3D();
		Vector3D tmp2 = new Vector3D();

		tmp1.set(P2[0] - P1[0], P2[1] - P1[1], P2[2] - P1[2]);
		tmp2.set(P4[0] - P3[0], P4[1] - P3[1], P4[2] - P3[2]);
		Vector3D tmpnorm = computeNormal(tmp1, tmp2);
		for (int l = 0; l < 3; l++) {
			P[l + 3] = tmpnorm.get(l);
		}
	}

	public Vector3D computeNormal(Vector3D tmp1, Vector3D tmp2) {
		Vector3D tmpnorm = new Vector3D();
		tmpnorm.set(0, tmp1.get(1) * tmp2.get(2) - (tmp1.get(2) * tmp2.get(1)));
		tmpnorm.set(1,
				-((tmp1.get(0) * tmp2.get(2)) - (tmp1.get(2) * tmp2.get(0))));
		tmpnorm.set(2, tmp1.get(0) * tmp2.get(1) - (tmp1.get(1) * tmp2.get(0)));
		tmpnorm.normalize();
		return tmpnorm;
	}

	public double getAnguloX() {
		return anguloX;
	}

	public double getAnguloY() {
		return anguloY;
	}

	public double getAnguloZ() {
		return anguloZ;
	}

	public double getBl() {
		return bl;
	}

	public abstract Vector3D getCentro();

	public double getF() {
		return F;
	}

	public double getGr() {
		return gr;
	}

	public double getPasoZoom() {
		return 0.2;
	}

	public Matriz3D getPersmat() {
		return persmat;
	}

	public double getRed() {
		return red;
	}

	public List<Matriz3D> getTransformaciones() {
		return transformaciones;
	}

	public double getZ() {
		return z;
	}

	protected double gouraud(Vector3D tmp) {
		double C;

		light.normalize();

		C = tmp.dotproduct(light);
		if (C < 0.0)
			return 0.0;
		else
			return C;
	}

	public abstract void recalcularNormales();

	public void reIniciar() {
		representar();
	}

	public void representar() {
		buf = new double[w][h];
		persmat.set(0, 0, F);
		persmat.set(1, 1, F);
		persmat.set(2, 2, 0.0);
		persmat.set(3, 3, 0.0);
		persmat.set(2, 3, 1.0);
		persmat.set(3, 2, 1.0);

		translacionPreRotacion = new Matriz3D();
		
		Vector3D invCentro=new Vector3D();
		invCentro.set(getCentro().invert());
		translacionPreRotacion.translate( invCentro);
		translacionPosRotacion = new Matriz3D();
		translacionPosRotacion.translate(getCentro());
		rotX = new Matriz3D();
		rotY = new Matriz3D();
		rotZ = new Matriz3D();
		rotX.rotateX(anguloX * 2.0 * Math.PI / 360.0);
		rotY.rotateY(anguloY * 2.0 * Math.PI / 360.0); // convierto a radianes
		rotZ.rotateZ(anguloZ * 2.0 * Math.PI / 360.0);

	}

	public void setAnguloX(double anguloX) {
		anguloX %= 360;
		this.anguloX = anguloX;
	}

	public void setAnguloY(double anguloY) {
		anguloY %= 360;
		this.anguloY = anguloY;
	}

	public void setAnguloZ(double anguloZ) {
		anguloZ %= 360;
		this.anguloZ = anguloZ;
	}

	public void setBl(double bl) {
		this.bl = bl;
	}

	public void setF(double f) {
		F = f;
	}

	public void setGr(double gr) {
		this.gr = gr;
	}

	public void setH(int h2) {
		h = h2;
	}

	public void setLigthMethod(ModeloIluminacion modelo) {
		lightmethod = modelo;
	}

	public void setPersmat(Matriz3D persmat) {
		this.persmat = persmat;
	}

	public void setPerspectiva(boolean selection) {
		perspectiva = selection;

	}

	public void setRed(double red) {
		this.red = red;
	}

	public void setTransformaciones(List<Matriz3D> transformaciones) {
		this.transformaciones = transformaciones;
	}

	public void setW(int w2) {
		w = w2;
	}

	public void setZ(double z) {
		if (z >= 0) {
			this.z = z;
		}
	}

	public boolean getPerspectiva() {
		return perspectiva;
	}
}