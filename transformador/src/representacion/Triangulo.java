package representacion;

public class Triangulo {
	public Vector3D[] t;// para triangulos, n = 3

	Triangulo() {
		// tres vectores para los puntos, tres para las normales (para el
		// sobreado) y el ultimo para cuelquier cosa, como intensidad, etc.
		t = new Vector3D[7];

		for (int i = 0; i < 7; i++) {
			t[i] = new Vector3D();

		}
	}

	// convierte los puntos del triangulo en puntos de pixel para poder ser
	// representado
	// se asume una superficie cuadrada
	void convertToPixel(double xmaxfilm, double ymaxfilm, int xmaxpix,
			int ymaxpix) {
		int x, y;
		for (int i = 0; i < 3; i++) {
			x = (int) ((0.5 + t[i].get(0) * xmaxfilm) * xmaxpix);
			y = (int) ((0.5 - t[i].get(1) * ymaxfilm) * ymaxpix);
			t[i].set(0, x);
			t[i].set(1, y);
		}
	}

	// divide cada punto por el ultimo elemento del vector (usualmente 1 salvo
	// con transformacion de perspectiva, etc)
	void divideByLast() {
		Matriz3D tmpmat = new Matriz3D();
		for (int i = 0; i < 3; i++) {
			tmpmat.identity();
			tmpmat.scale(1.0 / (t[i].get(3)));
			t[i].transform(tmpmat);
			// reset last element to 1
			t[i].set(3, 1.0);
		}
	}

	// encuentra la fla de mas abajo
	private int findBottomRow() {
		double num = t[0].get(1);
		int tmp = 0;
		for (int i = 1; i < 3; i++) {
			if (t[i].get(1) > num) {
				num = t[i].get(1);
				tmp = i;
			} else if (t[i].get(1) == num) {
				if (t[i].get(0) < t[tmp].get(0)) {
					tmp = i;
				}
			}
		}
		return tmp;
	}

	// encuentra el medio (conociendo el de mas arriba y el de mas abajo)
	private int findMiddle(int n, int m) {
		if (n == 0) {
			if (m == 1)
				return 2;
			if (m == 2)
				return 1;
		}
		if (n == 1) {
			if (m == 0)
				return 2;
			if (m == 2)
				return 0;
		}
		if (n == 2) {
			if (m == 0)
				return 1;
			if (m == 1)
				return 0;
		}
		return n;
	}

	// encuentra la fila de mas arriba
	private int findTopRow() {
		double num = t[0].get(1);
		int tmp = 0;
		for (int i = 1; i < 3; i++) {
			if (t[i].get(1) < num) {
				num = t[i].get(1);
				tmp = i;
			} else if (t[i].get(1) == num) {
				if (t[i].get(0) < t[tmp].get(0)) {
					tmp = i;
				}
			}
		}
		return tmp;
	}

	public Vector3D getPoint(int i) {
		return t[i];
	}

	private Vector3D getRefVec(Vector3D view, Vector3D norm) {
		Vector3D ref = new Vector3D();
		double tmp;
		norm.normalize();
		view.normalize();
		tmp = 2 * norm.dotproduct(view);
		ref.set(tmp * norm.get(0) - view.get(0),
				tmp * norm.get(1) - view.get(1),
				tmp * norm.get(2) - view.get(2));
		return ref;
	}

	private double gouraud(Vector3D tmp, Vector3D light) {
		double C;
		// don't forget to normalize light vector!
		light.normalize();
		// make sure tmp is normalized
		tmp.normalize();
		// Find value of C by dotting normal with light
		C = tmp.dotproduct(light);
		if (C < 0.0)
			return 0.0;
		else
			return C;
	}

	// verifica que dos vertices esten en la misma scanline
	private int is2SameLine(int num) {
		int switcher;
		int tmp = 0;
		if (num == 0) {
			switcher = 1;
		} else {
			switcher = 0;
		}
		for (int i = switcher; i < 3; i++) {
			if (t[i].get(1) == t[num].get(1)) {
				tmp = i;
			}
		}
		return tmp;
	}

	private boolean is3SepPoints() {
		boolean test;
		double x = 0.1;
		// uses Almost Equal function I wrote in Vector3D classes - uses a limit
		// tester
		// to check that all points of the
		if (t[0].isAlmostEqual(t[1], x) || t[0].isAlmostEqual(t[2], x)
				|| t[1].isAlmostEqual(t[2], x)) {
			test = false;
		} else {
			test = true;
		}
		return test;
	}

	private double phong(Vector3D view, Vector3D norm, Vector3D light, int p) {
		Vector3D reflected = new Vector3D();
		reflected = getRefVec(view, norm);
		double C;
		// don't forget to normalize light vector!
		light.normalize();
		// make sure tmp is normalized
		reflected.normalize();
		// Find value of C by dotting normal with light
		C = reflected.dotproduct(light);

		if (C < 0.0)
			return 0.0;
		else
			return Math.pow(C, p);

	}

	// Setea el iesimo punto
	void setPoint(int i, double x, double y, double z) {
		t[i].set(x, y, z);
	}

	// Setea el iesimo punto con un Vector3D
	void setPoint(int i, Vector3D vec) {
		t[i].set(vec);
	}

	// Transforma todos los puntos con la matriz dada
	void transformPoly(Matriz3D mat) {
		for (int i = 0; i < 3; i++) {
			t[i].transform(mat);
		}
	}

	void zbuffer(double[][] buf, ModeloIluminacion lightmethod, Vector3D light,
			Triangulo tmptria, int n) {
		if (lightmethod == ModeloIluminacion.GOURAUD) {
			zbuffergouraud(buf);
		} else if (lightmethod == ModeloIluminacion.PHONG) {
			zbufferPhong(buf, light, tmptria, n);
		} else if (lightmethod == ModeloIluminacion.PLANO) {
			zbufferPlano(buf);
		} else if (lightmethod == ModeloIluminacion.MARCOALAMBRE) {
			zbufferWireFrame(buf);
		}
	}

	// zbuffer function loops through triangles, and sets zbuf to proper values
	// takes a double double array and returns a double double array
	// this is for gouraud shading via light intensity interpolation
	private void zbuffergouraud(double[][] buf) {
		Vector3D top = new Vector3D();
		Vector3D bottom = new Vector3D();
		Vector3D middle = new Vector3D();
		int topnum, bottomnum, middlenum, tmpnum;
		double left = 0.0, right = 0.0, tmp1, tmp2, y1, y2, y3, Ia = 0.0, Ib = 0.0, Ip, I1, I2, I3;
		double perspZ, zleft = 0.0, zright = 0.0, tmp4 = 0.0;
		double ydiv1 = 0.0, ydiv2 = 0.0, ydiv3 = 0.0, xsub1 = 0.0, xsub2 = 0.0, xsub3 = 0.0;
		double zsub1 = 0.0, zsub2 = 0.0, zsub3 = 0.0, isub1 = 0.0, isub2 = 0.0, isub3 = 0.0;

		topnum = findTopRow();
		top = t[topnum];
		bottomnum = findBottomRow();
		bottom = t[bottomnum];
		middlenum = findMiddle(topnum, bottomnum);
		middle = t[middlenum];
		// COMPUTE LEFT AND RIGHT COLUMN FOR THIS ROW
		// Check to make sure that there are three points that are different
		if (is3SepPoints()) {
			y1 = top.get(1);
			y2 = middle.get(1);
			y3 = bottom.get(1);
			I1 = t[6].get(topnum);
			I3 = t[6].get(bottomnum);
			I2 = t[6].get(middlenum);
			for (int row = (int) top.get(1); row <= (int) bottom.get(1); row++) {
				if ((row >= 0) && (row < buf.length)) {
					if (y1 != y2) {
						ydiv1 = ((y1 - row) / (y1 - y2));
						xsub1 = top.get(0) - (top.get(0) - middle.get(0))
								* ydiv1;
						zsub1 = top.get(2) - (top.get(2) - middle.get(2))
								* ydiv1;
						isub1 = I1 - (I1 - I2) * ydiv1;
					}
					if (y1 != y3) {
						ydiv2 = ((y1 - row) / (y1 - y3));
						xsub2 = top.get(0) - (top.get(0) - bottom.get(0))
								* ydiv2;
						zsub2 = top.get(2) - (top.get(2) - bottom.get(2))
								* ydiv2;
						isub2 = I1 - (I1 - I3) * ydiv2;
					}
					if (y2 != y3) {
						ydiv3 = ((y2 - row) / (y2 - y3));
						xsub3 = middle.get(0) - (middle.get(0) - bottom.get(0))
								* ydiv3;
						zsub3 = middle.get(2) - (middle.get(2) - bottom.get(2))
								* ydiv3;
						isub3 = I2 - (I2 - I3) * ydiv3;
					}
					// System.out.println(ydiv1+" "+ydiv2+" "+ydiv3);
					if (row == (int) top.get(1)) {
						left = top.get(0);
						zleft = top.get(2);
						Ia = t[6].get(topnum);
						if ((tmpnum = is2SameLine(topnum)) == topnum) {
							right = left;
							zright = top.get(2);
							Ib = Ia;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2);
							if (tmp1 < left) {
								right = left;
								left = tmp1;
								zright = zleft;
								zleft = tmp2;
								Ib = Ia;
								Ia = t[6].get(tmpnum);
							} else {
								right = tmp1;
								zright = tmp2;
								Ib = t[6].get(tmpnum);
							}
						}
					} else if (row == (int) bottom.get(1)) {
						left = bottom.get(0);
						zleft = bottom.get(2);
						Ia = t[6].get(bottomnum);
						if ((tmpnum = is2SameLine(bottomnum)) == bottomnum) {
							right = left;
							zright = bottom.get(2);
							Ib = Ia;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2);
							if (tmp1 < left) {
								right = left;
								left = tmp1;
								zright = zleft;
								zleft = tmp2;
								Ib = Ia;
								Ia = t[6].get(tmpnum);
							} else {
								right = tmp1;
								zright = tmp2;
								Ib = t[6].get(tmpnum);
							}
						}
					} else {
						if (middle.get(1) != top.get(1)
								&& middle.get(1) != bottom.get(1)) {
							// Check to make sure middle y value not same as
							// bottom
							// or top
							if (row < middle.get(1)) {
								// if current scan line between top and middle
								// points
								tmp1 = xsub1;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
									zleft = zsub1;
									zright = zsub2;
									Ia = isub1;
									Ib = isub2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
									zleft = zsub2;
									zright = zsub1;
									Ia = isub2;
									Ib = isub1;
								}
							} else if (row > middle.get(1)) {
								// current scan line between midle and bottom
								// points
								tmp1 = xsub3;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
									zleft = zsub3;
									zright = zsub2;
									Ia = isub3;
									Ib = isub2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
									zleft = zsub2;
									zright = zsub3;
									Ia = isub2;
									Ib = isub3;
								}
							} else if (row == middle.get(1)) {
								// current scan line equals middle's y value
								tmp1 = middle.get(0);
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
									zleft = middle.get(2);
									zright = zsub2;
									Ia = t[6].get(middlenum);
									Ib = isub2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
									zleft = zsub2;
									zright = middle.get(2);
									Ia = isub2;
									Ib = t[6].get(middlenum);
								}
							}
						} else if (middle.get(1) == top.get(1)) {
							// middle point at same level as top point
							tmp1 = xsub3;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
								zleft = zsub3;
								zright = zsub2;
								Ia = isub3;
								Ib = isub2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
								zleft = zsub2;
								zright = zsub3;
								Ia = isub2;
								Ib = isub3;
							}
						} else if (middle.get(1) == bottom.get(1)) {
							// middle point at same level as bottom point
							tmp1 = xsub1;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
								zleft = zsub1;
								zright = zsub2;
								Ia = isub1;
								Ib = isub2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
								zleft = zsub2;
								zright = zsub1;
								Ia = isub2;
								Ib = isub1;
							}
						}
					}
					for (int col = (int) left; col <= (int) right; col++) {
						if ((col >= 0) && (col < buf.length)) {

							if (col == (int) left) {
								perspZ = zleft;
								Ip = Ia;
								if (Math.abs(Ip) >= 0.0) {
									tmp4 = Math.abs((0.2 + 0.8 * Ip)) - perspZ;
								} else {
									tmp4 = perspZ;
								}
							} else if (col == (int) right) {
								perspZ = zright;
								Ip = Ib;
								if (Math.abs(Ip) >= 0.0) {
									tmp4 = Math.abs((0.2 + 0.8 * Ip)) - perspZ;
								} else {
									tmp4 = perspZ;
								}
							} else {
								perspZ = zright
										- ((zright - zleft) * ((right - col) / (right - left)));
								Ip = Ib - (Ib - Ia)
										* ((right - col) / (right - left));
								if (Math.abs(Ip) >= 0.0) {
									tmp4 = Math.abs((0.2 + 0.8 * Ip)) - perspZ;
								} else {
									tmp4 = perspZ;
								}
							}

							{
								if (Math.abs(tmp4) > 1.0) {
									tmp4 = 1.0;
								}

								buf[col][row] = -(Math.abs(tmp4));
							}
						}
					}
				}
			}
		}
	}

	// zbuffer para phong
	private void zbufferPhong(double[][] buf, Vector3D light, Triangulo World,
			int p) {
		Vector3D top = new Vector3D();
		Vector3D bottom = new Vector3D();
		Vector3D middle = new Vector3D();
		Vector3D tmpnorm = new Vector3D();
		Vector3D view = new Vector3D();
		int topnum, bottomnum, middlenum, tmpnum;
		double left = 0.0, right = 0.0, tmp1, tmp2, y1, y2, y3;
		// variables for normals and Gouraud and Phong
		double Xa = 0.0, Xb = 0.0, Xp, G, P;
		double Ya = 0.0, Yb = 0.0, Yp;
		double Za = 0.0, Zb = 0.0, Zp;
		double XN1 = 0.0, YN1 = 0.0, ZN1 = 0.0;
		double XN2 = 0.0, YN2 = 0.0, ZN2 = 0.0;
		double XN3 = 0.0, YN3 = 0.0, ZN3 = 0.0;
		double xNsub1 = 0.0, xNsub2 = 0.0, xNsub3 = 0.0;
		double yNsub1 = 0.0, yNsub2 = 0.0, yNsub3 = 0.0;
		double zNsub1 = 0.0, zNsub2 = 0.0, zNsub3 = 0.0;
		// For storing world-coordinates - needed for finding view vector later
		double XWC1 = 0.0, YWC1 = 0.0, ZWC1 = 0.0;
		double XWC2 = 0.0, YWC2 = 0.0, ZWC2 = 0.0;
		double XWC3 = 0.0, YWC3 = 0.0, ZWC3 = 0.0;
		double xWCsub1 = 0.0, xWCsub2 = 0.0, xWCsub3 = 0.0;
		double yWCsub1 = 0.0, yWCsub2 = 0.0, yWCsub3 = 0.0;
		double zWCsub1 = 0.0, zWCsub2 = 0.0, zWCsub3 = 0.0;
		double XWCa = 0.0, XWCb = 0.0, XWCp;
		double YWCa = 0.0, YWCb = 0.0, YWCp;
		double ZWCa = 0.0, ZWCb = 0.0, ZWCp;
		double tmp4 = 0.0;
		double ydiv1 = 0.0, ydiv2 = 0.0, ydiv3 = 0.0, xsub1 = 0.0, xsub2 = 0.0, xsub3 = 0.0;
		topnum = findTopRow();
		top = t[topnum];
		bottomnum = findBottomRow();
		bottom = t[bottomnum];
		middlenum = findMiddle(topnum, bottomnum);
		middle = t[middlenum];
		view.set(0.0, 0.0, -1.0);
		// COMPUTE LEFT AND RIGHT COLUMN FOR THIS ROW
		// Check to make sure that there are three points that are different
		if (is3SepPoints()) {
			y1 = top.get(1);
			y2 = middle.get(1);
			y3 = bottom.get(1);
			XN1 = t[topnum + 3].get(0);
			YN1 = t[topnum + 3].get(1);
			ZN1 = t[topnum + 3].get(2);
			XN2 = t[middlenum + 3].get(0);
			YN2 = t[middlenum + 3].get(1);
			ZN2 = t[middlenum + 3].get(2);
			XN3 = t[bottomnum + 3].get(0);
			YN3 = t[bottomnum + 3].get(1);
			ZN3 = t[bottomnum + 3].get(2);
			XWC1 = World.t[topnum].get(0);
			YWC1 = World.t[topnum].get(1);
			ZWC1 = World.t[topnum].get(2);
			XWC2 = World.t[middlenum].get(0);
			YWC2 = World.t[middlenum].get(1);
			ZWC2 = World.t[middlenum].get(2);
			XWC3 = World.t[bottomnum].get(0);
			YWC3 = World.t[bottomnum].get(1);
			ZWC3 = World.t[bottomnum].get(2);
			for (int row = (int) top.get(1); row <= (int) bottom.get(1); row++) {
				if ((row >= 0) && (row < buf.length)) {
					if (y1 != y2) {
						ydiv1 = ((y1 - row) / (y1 - y2));
						xsub1 = top.get(0) - (top.get(0) - middle.get(0))
								* ydiv1;
						xNsub1 = XN1 - (XN1 - XN2) * ydiv1;
						yNsub1 = YN1 - (YN1 - YN2) * ydiv1;
						zNsub1 = ZN1 - (ZN1 - ZN2) * ydiv1;
						xWCsub1 = XWC1 - (XWC1 - XWC2) * ydiv1;
						yWCsub1 = YWC1 - (YWC1 - YWC2) * ydiv1;
						zWCsub1 = ZWC1 - (ZWC1 - ZWC2) * ydiv1;
					}
					if (y1 != y3) {
						ydiv2 = ((y1 - row) / (y1 - y3));
						xsub2 = top.get(0) - (top.get(0) - bottom.get(0))
								* ydiv2;
						xNsub2 = XN1 - (XN1 - XN3) * ydiv2;
						yNsub2 = YN1 - (YN1 - YN3) * ydiv2;
						zNsub2 = ZN1 - (ZN1 - ZN3) * ydiv2;
						xWCsub2 = XWC1 - (XWC1 - XWC3) * ydiv2;
						yWCsub2 = YWC1 - (YWC1 - YWC3) * ydiv2;
						zWCsub2 = ZWC1 - (ZWC1 - ZWC3) * ydiv2;
					}
					if (y2 != y3) {
						ydiv3 = ((y2 - row) / (y2 - y3));
						xsub3 = middle.get(0) - (middle.get(0) - bottom.get(0))
								* ydiv3;
						xNsub3 = XN2 - (XN2 - XN3) * ydiv3;
						yNsub3 = YN2 - (YN2 - YN3) * ydiv3;
						zNsub3 = ZN2 - (ZN2 - ZN3) * ydiv3;
						xWCsub3 = XWC2 - (XWC2 - XWC3) * ydiv3;
						yWCsub3 = YWC2 - (YWC2 - YWC3) * ydiv3;
						zWCsub3 = ZWC2 - (ZWC2 - ZWC3) * ydiv3;
					}
					// System.out.println(ydiv1+" "+ydiv2+" "+ydiv3);
					if (row == (int) top.get(1)) {
						left = top.get(0);
						Xa = t[topnum + 3].get(0);
						Ya = t[topnum + 3].get(1);
						Za = t[topnum + 3].get(2);
						XWCa = World.t[topnum].get(0);
						YWCa = World.t[topnum].get(1);
						ZWCa = World.t[topnum].get(2);
						if ((tmpnum = is2SameLine(topnum)) == topnum) {
							right = left;
							Xb = Xa;
							Yb = Ya;
							Zb = Za;
							XWCb = XWCa;
							YWCb = YWCa;
							ZWCb = ZWCa;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2);
							if (tmp1 < left) {
								right = left;
								left = tmp1;
								Xb = Xa;
								Yb = Ya;
								Zb = Za;
								Xa = t[tmpnum + 3].get(0);
								Ya = t[tmpnum + 3].get(1);
								Za = t[tmpnum + 3].get(2);
								XWCb = XWCa;
								YWCb = YWCa;
								ZWCb = ZWCa;
								XWCa = World.t[tmpnum].get(0);
								YWCa = World.t[tmpnum].get(1);
								ZWCa = World.t[tmpnum].get(2);
							} else {
								right = tmp1;
								Xb = t[tmpnum + 3].get(0);
								Yb = t[tmpnum + 3].get(1);
								Zb = t[tmpnum + 3].get(2);
								XWCb = World.t[tmpnum].get(0);
								YWCb = World.t[tmpnum].get(1);
								ZWCb = World.t[tmpnum].get(2);
							}
						}
					} else if (row == (int) bottom.get(1)) {
						left = bottom.get(0);
						Xa = t[bottomnum + 3].get(0);
						Ya = t[bottomnum + 3].get(1);
						Za = t[bottomnum + 3].get(2);
						XWCa = World.t[bottomnum].get(0);
						YWCa = World.t[bottomnum].get(1);
						ZWCa = World.t[bottomnum].get(2);
						if ((tmpnum = is2SameLine(bottomnum)) == bottomnum) {
							right = left;
							Xb = Xa;
							Yb = Ya;
							Zb = Za;
							XWCb = XWCa;
							YWCb = YWCa;
							ZWCb = ZWCa;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2);
							if (tmp1 < left) {
								right = left;
								left = tmp1;
								Xb = Xa;
								Yb = Ya;
								Zb = Za;
								Xa = t[tmpnum + 3].get(0);
								Ya = t[tmpnum + 3].get(1);
								Za = t[tmpnum + 3].get(2);
								XWCb = XWCa;
								YWCb = YWCa;
								ZWCb = ZWCa;
								XWCa = World.t[tmpnum].get(0);
								YWCa = World.t[tmpnum].get(1);
								ZWCa = World.t[tmpnum].get(2);

							} else {
								right = tmp1;
								Xb = t[tmpnum + 3].get(0);
								Yb = t[tmpnum + 3].get(1);
								Zb = t[tmpnum + 3].get(2);
								XWCb = World.t[tmpnum].get(0);
								YWCb = World.t[tmpnum].get(1);
								ZWCb = World.t[tmpnum].get(2);
							}
						}
					} else {
						if (middle.get(1) != top.get(1)
								&& middle.get(1) != bottom.get(1)) {
							// Check to make sure middle y value not same as
							// bottom
							// or top
							if (row < middle.get(1)) {
								// if current scan line between top and middle
								// points
								tmp1 = xsub1;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
									Xa = xNsub1;
									Ya = yNsub1;
									Za = zNsub1;
									Xb = xNsub2;
									Yb = yNsub2;
									Zb = zNsub2;
									XWCa = xWCsub1;
									YWCa = yWCsub1;
									ZWCa = zWCsub1;
									XWCb = xWCsub2;
									YWCb = yWCsub2;
									ZWCb = zWCsub2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
									Xa = xNsub2;
									Ya = yNsub2;
									Za = zNsub2;
									Xb = xNsub1;
									Yb = yNsub1;
									Zb = zNsub1;
									XWCa = xWCsub2;
									YWCa = yWCsub2;
									ZWCa = zWCsub2;
									XWCb = xWCsub1;
									YWCb = yWCsub1;
									ZWCb = zWCsub1;
								}
							} else if (row > middle.get(1)) {
								// current scan line between midle and bottom
								// points
								tmp1 = xsub3;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
									Xa = xNsub3;
									Ya = yNsub3;
									Za = zNsub3;
									Xb = xNsub2;
									Yb = yNsub2;
									Zb = zNsub2;
									XWCa = xWCsub3;
									YWCa = yWCsub3;
									ZWCa = zWCsub3;
									XWCb = xWCsub2;
									YWCb = yWCsub2;
									ZWCb = zWCsub2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
									Xa = xNsub2;
									Ya = yNsub2;
									Za = zNsub2;
									Xb = xNsub3;
									Yb = yNsub3;
									Zb = zNsub3;
									XWCa = xWCsub2;
									YWCa = yWCsub2;
									ZWCa = zWCsub2;
									XWCb = xWCsub3;
									YWCb = yWCsub3;
									ZWCb = zWCsub3;
								}
							} else if (row == middle.get(1)) {
								// current scan line equals middle's y value
								tmp1 = middle.get(0);
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
									Xa = t[middlenum + 3].get(0);
									Ya = t[middlenum + 3].get(1);
									Za = t[middlenum + 3].get(2);
									Xb = xNsub2;
									Yb = yNsub2;
									Zb = zNsub2;
									XWCa = World.t[middlenum].get(0);
									YWCa = World.t[middlenum].get(1);
									ZWCa = World.t[middlenum].get(2);
									XWCb = xWCsub2;
									YWCb = yWCsub2;
									ZWCb = zWCsub2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
									Xa = xNsub2;
									Ya = yNsub2;
									Za = zNsub2;
									Xb = t[middlenum + 3].get(0);
									Yb = t[middlenum + 3].get(1);
									Zb = t[middlenum + 3].get(2);
									XWCa = xWCsub2;
									YWCa = yWCsub2;
									ZWCa = zWCsub2;
									XWCb = World.t[middlenum].get(0);
									YWCb = World.t[middlenum].get(1);
									ZWCb = World.t[middlenum].get(2);

								}
							}
						} else if (middle.get(1) == top.get(1)) {
							// middle point at same level as top point
							tmp1 = xsub3;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
								Xa = xNsub3;
								Ya = yNsub3;
								Za = zNsub3;
								Xb = xNsub2;
								Yb = yNsub2;
								Zb = zNsub2;
								XWCa = xWCsub3;
								YWCa = yWCsub3;
								ZWCa = zWCsub3;
								XWCb = xWCsub2;
								YWCb = yWCsub2;
								ZWCb = zWCsub2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
								Xa = xNsub2;
								Ya = yNsub2;
								Za = zNsub2;
								Xb = xNsub3;
								Yb = yNsub3;
								Zb = zNsub3;
								XWCa = xWCsub2;
								YWCa = yWCsub2;
								ZWCa = zWCsub2;
								XWCb = xWCsub3;
								YWCb = yWCsub3;
								ZWCb = zWCsub3;
							}
						} else if (middle.get(1) == bottom.get(1)) {
							// middle point at same level as bottom point
							tmp1 = xsub1;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
								Xa = xNsub1;
								Ya = yNsub1;
								Za = zNsub1;
								Xb = xNsub2;
								Yb = yNsub2;
								Zb = zNsub2;
								XWCa = xWCsub1;
								YWCa = yWCsub1;
								ZWCa = zWCsub1;
								XWCb = xWCsub2;
								YWCb = yWCsub2;
								ZWCb = zWCsub2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
								Xa = xNsub2;
								Ya = yNsub2;
								Za = zNsub2;
								Xb = xNsub1;
								Yb = yNsub1;
								Zb = zNsub1;
								XWCa = xWCsub2;
								YWCa = yWCsub2;
								ZWCa = zWCsub2;
								XWCb = xWCsub1;
								YWCb = yWCsub1;
								ZWCb = zWCsub1;
							}
						}
					}
					for (int col = (int) left; col <= (int) right; col++) {

						if ((col >= 0) && (col < buf.length)) {
							if (col == (int) left) {
								Xp = Xa;
								Yp = Ya;
								Zp = Za;
								XWCp = XWCa;
								YWCp = YWCa;
								ZWCp = ZWCa;
							} else if (col == (int) right) {
								Xp = Xb;
								Yp = Yb;
								Zp = Zb;
								XWCp = XWCb;
								YWCp = YWCb;
								ZWCp = ZWCb;
							} else {
								Xp = Xb - (Xb - Xa)
										* ((right - col) / (right - left));
								Yp = Yb - (Yb - Ya)
										* ((right - col) / (right - left));
								Zp = Zb - (Zb - Za)
										* ((right - col) / (right - left));
								XWCp = XWCb - (XWCb - XWCa)
										* ((right - col) / (right - left));
								YWCp = YWCb - (YWCb - YWCa)
										* ((right - col) / (right - left));
								ZWCp = ZWCb - (ZWCb - ZWCa)
										* ((right - col) / (right - left));
							}
							tmpnorm.set(Xp, Yp, Zp);
							tmpnorm.normalize();
							// Camera assumed to be at origin
							view.set(-XWCp, -YWCp, -ZWCp);
							view.normalize();
							// System.out.println(XWCp+", "+YWCp+", "+ZWCp);
							G = gouraud(tmpnorm, light);
							P = phong(view, tmpnorm, light, p);
							tmp4 = P + G;

							{
								if (Math.abs(tmp4) > 1.0) {
									tmp4 = 1.0;
								}

								buf[col][row] = -(Math.abs(tmp4));
							}
						}
					}
				}
			}
		}
	}

	private void zbufferPlano(double[][] buf) {
		Vector3D top = new Vector3D();
		Vector3D bottom = new Vector3D();
		Vector3D middle = new Vector3D();
		int topnum, bottomnum, middlenum, tmpnum;
		double left = 0.0, right = 0.0, tmp1, tmp2, y1, y2, y3;
		double ydiv1 = 0.0, ydiv2 = 0.0, ydiv3 = 0.0, xsub1 = 0.0, xsub2 = 0.0, xsub3 = 0.0;
		topnum = findTopRow();
		top = t[topnum];
		bottomnum = findBottomRow();
		bottom = t[bottomnum];
		middlenum = findMiddle(topnum, bottomnum);
		middle = t[middlenum];
		// si no estan todos los puntos juntos
		if (is3SepPoints()) {
			y1 = top.get(1);
			y2 = middle.get(1);
			y3 = bottom.get(1);

			for (int row = (int) top.get(1); row <= (int) bottom.get(1); row++) {
				if ((row >= 0) && (row < buf.length)) {
					if (y1 != y2) {
						ydiv1 = ((y1 - row) / (y1 - y2));
						xsub1 = top.get(0) - (top.get(0) - middle.get(0))
								* ydiv1;
					}
					if (y1 != y3) {
						ydiv2 = ((y1 - row) / (y1 - y3));
						xsub2 = top.get(0) - (top.get(0) - bottom.get(0))
								* ydiv2;
					}
					if (y2 != y3) {
						ydiv3 = ((y2 - row) / (y2 - y3));
						xsub3 = middle.get(0) - (middle.get(0) - bottom.get(0))
								* ydiv3;
					}

					if (row == (int) top.get(1)) {
						left = top.get(0);
						if ((tmpnum = is2SameLine(topnum)) == topnum) {
							right = left;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2);
							if (tmp1 < left) {
								right = left;
								left = tmp1;
							} else {
								right = tmp1;
							}
						}
					} else if (row == (int) bottom.get(1)) {
						left = bottom.get(0);
						if ((tmpnum = is2SameLine(bottomnum)) == bottomnum) {
							right = left;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2);
							if (tmp1 < left) {
								right = left;
								left = tmp1;
							} else {
								right = tmp1;
							}
						}
					} else {
						if (middle.get(1) != top.get(1)
								&& middle.get(1) != bottom.get(1)) {
							// veo que la linea del medio no sea igual a la de
							// arriba o la de abajo
							if (row < middle.get(1)) {
								// mitad superior
								tmp1 = xsub1;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
								}
							} else if (row > middle.get(1)) {
								// mitad inferior
								tmp1 = xsub3;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
								}
							} else if (row == middle.get(1)) {
								// linea del medio
								tmp1 = middle.get(0);
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
								}
							}
						} else if (middle.get(1) == top.get(1)) {
							// medio = superior
							tmp1 = xsub3;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
							}
						} else if (middle.get(1) == bottom.get(1)) {
							// medio = inferior
							tmp1 = xsub1;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
							}
						}
					}

					for (int col = (int) left; col <= (int) right; col++) {
						if ((col >= 0) && (col < buf.length)) {

							buf[col][row] = -(Math.abs(t[6].get(0))); // en t[6]
																		// tengo
							// la normal al
							// triangulo
							// este

						}
					}
				}
			}
		}

	}

	private void zbufferWireFrame(double[][] buf) {
		Vector3D top = new Vector3D();
		Vector3D bottom = new Vector3D();
		Vector3D middle = new Vector3D();
		int topnum, bottomnum, middlenum, tmpnum;
		double left = 0.0, right = 0.0, tmp1, tmp2, y1, y2, y3;
		double ydiv1 = 0.0, ydiv2 = 0.0, ydiv3 = 0.0, xsub1 = 0.0, xsub2 = 0.0, xsub3 = 0.0;
		topnum = findTopRow();
		top = t[topnum];
		bottomnum = findBottomRow();
		bottom = t[bottomnum];
		middlenum = findMiddle(topnum, bottomnum);
		middle = t[middlenum];

		// si no estan todos los puntos juntos
		if (is3SepPoints()) {
			y1 = top.get(1);
			y2 = middle.get(1);
			y3 = bottom.get(1);

			for (int row = (int) top.get(1); row <= (int) bottom.get(1); row++) {
				if ((row >= 0) && (row < buf.length)) {
					if (y1 != y2) {
						ydiv1 = ((y1 - row) / (y1 - y2));
						xsub1 = top.get(0) - (top.get(0) - middle.get(0))
								* ydiv1;
					}
					if (y1 != y3) {
						ydiv2 = ((y1 - row) / (y1 - y3));
						xsub2 = top.get(0) - (top.get(0) - bottom.get(0))
								* ydiv2;
					}
					if (y2 != y3) {
						ydiv3 = ((y2 - row) / (y2 - y3));
						xsub3 = middle.get(0) - (middle.get(0) - bottom.get(0))
								* ydiv3;
					}

					if (row == (int) top.get(1)) {
						left = top.get(0);
						if ((tmpnum = is2SameLine(topnum)) == topnum) {
							right = left;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2); // ?
							if (tmp1 < left) {
								right = left;
								left = tmp1;
							} else {
								right = tmp1;
							}
						}
					} else if (row == (int) bottom.get(1)) {
						left = bottom.get(0);
						if ((tmpnum = is2SameLine(bottomnum)) == bottomnum) {
							right = left;
						} else {
							tmp1 = t[tmpnum].get(0);
							tmp2 = t[tmpnum].get(2); // ?
							if (tmp1 < left) {
								right = left;
								left = tmp1;
							} else {
								right = tmp1;
							}
						}
					} else {
						if (middle.get(1) != top.get(1)
								&& middle.get(1) != bottom.get(1)) {
							// veo que el medio no sea igual al superior o
							// inferior
							if (row < middle.get(1)) {
								// si la linea actual esta en la mitad
								// superior...
								tmp1 = xsub1;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
								}
							} else if (row > middle.get(1)) {
								// linea actual en la mitad inferior
								tmp1 = xsub3;
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
								}
							} else if (row == middle.get(1)) {
								// linea del medio
								tmp1 = middle.get(0);
								tmp2 = xsub2;
								if (tmp1 < tmp2) {
									left = tmp1;
									right = tmp2;
								} else if (tmp2 < tmp1) {
									left = tmp2;
									right = tmp1;
								}
							}
						} else if (middle.get(1) == top.get(1)) {
							// medio y superior en la misma linea
							tmp1 = xsub3;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
							}
						} else if (middle.get(1) == bottom.get(1)) {
							// medio e inferior en la misma linea
							tmp1 = xsub1;
							tmp2 = xsub2;
							if (tmp1 < tmp2) {
								left = tmp1;
								right = tmp2;
							} else if (tmp2 < tmp1) {
								left = tmp2;
								right = tmp1;
							}
						}
					}
					if ((int) left >= 0 && (int) left < buf.length) {
						buf[(int) left][row] = -1;

						// System.out.println(left+" - "+row);
					}
					if ((int) right >= 0 && (int) right < buf.length) {
						// System.out.println(right+" - "+row);

						buf[(int) right][row] = -1;
					}
					if (row == (int) top.get(1) || row == (int) bottom.get(1)) {
						for (int col = (int) left; col <= (int) right; col++) {
							if ((col >= 0) && (col < buf.length)) {
								buf[col][row] = -1;
							}
						}
					}
				}
			}
		}

	}

}
