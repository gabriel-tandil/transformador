package representacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Comparator;
public class ObjetoSur extends ObjetoTridimensional {
	private double maxX;
	private double maxY;
	private double maxZ;
	private double minX;
	private double minY;
	private double minZ;
	String nombreArchivo;
	Triangulo[] triangulo;
	Triangulo[] trianguloSalvados;

	Triangulo[] trianguloPuntos;
	private Map<Integer, List<Integer>> triangulosPunto;

	public ObjetoSur(boolean perspectiva, String nombreArchivo, int w, int h,
			double red, double gr, double bl, Vector3D light,
			ModeloIluminacion lightmethod, int p) {
		super(w, h, red, gr, bl, light, lightmethod, p);
		this.perspectiva = perspectiva;
		this.nombreArchivo = nombreArchivo;
		cargarDesdeArchivo();
		translacionVisualizacion = new Matriz3D();
		Vector3D v = new Vector3D();
		v.set(0, 0, -maxZ * 4);
		translacionVisualizacion.translate(v);
	}

	public void calcularValoresModIluminacion(Triangulo tria) {

		if (lightmethod == ModeloIluminacion.GOURAUD) {
			tria.setPoint(6, gouraud(tria.t[3]), gouraud(tria.t[4]),
					gouraud(tria.t[5]));
		}
		if (lightmethod == ModeloIluminacion.PLANO) {
			tria.setPoint(6, gouraud(tria.t[3]), 0, 0);
		}
	}

	private Vector3D calcularNormal(Triangulo tria) {
		Vector3D tmp1 = new Vector3D();
		Vector3D tmp2 = new Vector3D();
		tmp1.set(tria.getPoint(0).get(0) - tria.getPoint(1).get(0), tria
				.getPoint(0).get(1) - tria.getPoint(1).get(1), tria.getPoint(0)
				.get(2) - tria.getPoint(1).get(2));
		tmp2.set(tria.getPoint(2).get(0) - tria.getPoint(1).get(0), tria
				.getPoint(2).get(1) - tria.getPoint(1).get(1), tria.getPoint(2)
				.get(2) - tria.getPoint(1).get(2));
		Vector3D tmpnorm = computeNormal(tmp1, tmp2);

		return tmpnorm;
	}

	public void cargarDesdeArchivo() {
		maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
		minX = minY = minZ = Double.POSITIVE_INFINITY;

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			System.out.println(new Date().toString() + " Iniciada la Carga.");
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda
			archivo = new File(nombreArchivo);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// Lectura del archivo
			String linea;
			linea = leerLinea(br);
			if (!linea.equals("*ELEMENT GROUPS"))
				throw new Exception("error al leer el encabezado");
			linea = leerLinea(br);
			int cantidadDeGrupos = Integer.parseInt(linea.trim());
			int tamañoGrupo[] = new int[cantidadDeGrupos];
			int cantidadTriangulos = 0;
			for (int i = 0; i < cantidadDeGrupos; i++) {
				linea = leerLinea(br);
				linea = sacarEspaciosRepetidos(linea.trim());
				String[] s = linea.split(" ");
				tamañoGrupo[i] = Integer.parseInt(s[1]);
				cantidadTriangulos += tamañoGrupo[i];
				if (!"Tri3".equals(s[2]))
					throw new Exception(
							"todos los grupos del archivo deben ser de tipo Tri3");
			}

			triangulo = new Triangulo[cantidadTriangulos];
			trianguloPuntos = new Triangulo[cantidadTriangulos];
			trianguloSalvados = new Triangulo[cantidadTriangulos];
			int numeroTriangulo = 0;
			linea = leerLinea(br); // *INCIDENCE
			for (int i = 0; i < cantidadDeGrupos; i++) {
				for (int ii = 0; ii < tamañoGrupo[i];) {
					linea = leerLinea(br);
					if (!linea.equals("")) {
						ii++;
						linea = sacarEspaciosRepetidos(linea.trim());
						String[] s = linea.split(" ");
						triangulo[numeroTriangulo] = new Triangulo();
						triangulo[numeroTriangulo].setPoint(0,
								Integer.parseInt(s[0]), 0, 0);
						triangulo[numeroTriangulo].setPoint(1,
								Integer.parseInt(s[1]), 0, 0);
						triangulo[numeroTriangulo].setPoint(2,
								Integer.parseInt(s[2]), 0, 0);
						trianguloSalvados[numeroTriangulo] = new Triangulo();
						trianguloSalvados[numeroTriangulo].setPoint(0,
								Integer.parseInt(s[0]), 0, 0);
						trianguloSalvados[numeroTriangulo].setPoint(1,
								Integer.parseInt(s[1]), 0, 0);
						trianguloSalvados[numeroTriangulo].setPoint(2,
								Integer.parseInt(s[2]), 0, 0);

						trianguloPuntos[numeroTriangulo] = new Triangulo();
						trianguloPuntos[numeroTriangulo].setPoint(0,
								Integer.parseInt(s[0]), 0, 0);
						trianguloPuntos[numeroTriangulo].setPoint(1,
								Integer.parseInt(s[1]), 0, 0);
						trianguloPuntos[numeroTriangulo].setPoint(2,
								Integer.parseInt(s[2]), 0, 0);

						numeroTriangulo++;
					}
				}
			}
			linea = leerLinea(br);// *COORDINATES
			linea = leerLinea(br);
			int cantidadPuntos = Integer.parseInt(linea.trim());
			Vector3D[] vector = new Vector3D[cantidadPuntos + 1];
			triangulosPunto = new HashMap<Integer, List<Integer>>();
			for (int i = 0; i < cantidadPuntos; i++) {
				linea = leerLinea(br);
				linea = sacarEspaciosRepetidos(linea.trim());
				String[] s = linea.split(" ");
				if (s.length == 4) {
					double x, y, z;
					x = Double.parseDouble(s[1]);
					y = Double.parseDouble(s[2]);
					z = Double.parseDouble(s[3]);
					verificarExtremos(x, y, z);
					vector[Integer.parseInt(s[0])] = new Vector3D();
					vector[Integer.parseInt(s[0])].set(x, y, z, 1);
					triangulosPunto.put(new Integer(s[0]),
							new ArrayList<Integer>());
					// System.out.println(vector[Integer.parseInt(s[0])]);
				}
			}

			// para hacer la translacion en la carga

			// for (int i = 0; i < vector.length; i++) {
			// Vector3D vector3d = vector[i];
			// if (vector3d != null) {
			// vector3d.set(2, vector3d.get(2) - maxZ * 2);
			// }
			// }
			// maxZ = maxZ - maxZ * 2;
			// minZ = minZ - maxZ * 2;
//Vector3D normalAnterior=null;
			for (int i = 0; i < triangulo.length; i++) {
				Triangulo trianguloActual = triangulo[i];
				Triangulo trianguloSalvado = trianguloSalvados[i];
				List<Integer> lt = triangulosPunto.get(new Integer(
						(int) trianguloActual.getPoint(0).get(0)));
				lt.add(new Integer(i));
				lt = triangulosPunto.get(new Integer((int) trianguloActual
						.getPoint(1).get(0)));
				lt.add(new Integer(i));
				lt = triangulosPunto.get(new Integer((int) trianguloActual
						.getPoint(2).get(0)));
				lt.add(new Integer(i));

				trianguloSalvado.setPoint(0, vector[(int) trianguloSalvado
						.getPoint(0).get(0)]);
				trianguloSalvado.setPoint(1, vector[(int) trianguloSalvado
						.getPoint(1).get(0)]);
				trianguloSalvado.setPoint(2, vector[(int) trianguloSalvado
						.getPoint(2).get(0)]);

				trianguloActual.setPoint(0, vector[(int) trianguloActual
						.getPoint(0).get(0)]);
				trianguloActual.setPoint(1, vector[(int) trianguloActual
						.getPoint(1).get(0)]);
				trianguloActual.setPoint(2, vector[(int) trianguloActual
						.getPoint(2).get(0)]);
//				Vector3D normal=calcularNormal(trianguloActual);
//				if (angulo(trianguloActual.getPoint(0),trianguloActual.getPoint(1),trianguloActual.getPoint(2))>0){
//				
//			//	if (estaAlRevez(trianguloActual.getPoint(0),trianguloActual.getPoint(1),trianguloActual.getPoint(2))){
//				
//			//	if(normalAnterior!=null && estaAlRevez(normalAnterior,normal)){
//				invertir2Vertices(trianguloActual);//TODO: invertir dos puntos (en los 3 vectores)
//					invertir2Vertices(trianguloSalvado);
//					invertir2Vertices(trianguloPuntos[i]);
//				}
//				normalAnterior=normal;
				
			}
			System.out.println(new Date().toString() + " Finalizda la Carga.");
			System.out.println("Maximo X: " + maxX);
			System.out.println("Minimo X: " + minX);
			System.out.println("Maximo Y: " + maxY);
			System.out.println("Minimo Y: " + minY);
			System.out.println("Maximo Z: " + maxZ);
			System.out.println("Minimo Z: " + minZ);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {// al final lo cierro, siempre
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}


	@Override
	public Vector3D getCentro() {
		Vector3D cen = new Vector3D();
		cen.set(((maxX - minX) / 2) + minX, ((maxY - minY) / 2) + minY,
				((maxZ - minZ) / 2) + minZ);
		return cen;
	}

	@Override
	public double getPasoZoom() {
		return maxZ / 20;
	}

	@SuppressWarnings("unused")
	private void imprimirTriangulo(Triangulo tria) {
		imprimirTriangulo(tria, 3);
	}

	private void imprimirTriangulo(Triangulo tria, int puntos) {
		System.out.println("-----------------");
		for (int i = 0; i < 3; i++) {
			Vector3D v = tria.t[i];
			System.out.println(v.toString(puntos));

		}

	}

	private String leerLinea(BufferedReader br) throws IOException {
		String linea = br.readLine();
		while (linea != null && "".equals(linea.trim())) {
			linea = br.readLine();
		}
		return linea;
	}

	@Override
	public void recalcularNormales() {

		for (Iterator<Integer> iterator = triangulosPunto.keySet().iterator(); iterator
				.hasNext();) {
			Integer punto = (Integer) iterator.next();
			calcularNormalesIncidenciaPunto(punto);
		}
		for (Triangulo tria : triangulo) {
			calcularValoresModIluminacion(tria);
		}
	}

	private void calcularNormalesIncidenciaPunto(Integer punto) {
		List<Integer> triangulos = triangulosPunto.get(punto);
		if (lightmethod == ModeloIluminacion.GOURAUD
				|| lightmethod == ModeloIluminacion.PHONG) {
			List<Vector3D> normales = new ArrayList<Vector3D>();
			for (Iterator<Integer> iterator2 = triangulos.iterator(); iterator2
					.hasNext();) {
				Integer numTriangulo = (Integer) iterator2.next();

				normales.add(calcularNormal(triangulo[numTriangulo]));
			}
			Vector3D normalEnPunto = promediar(normales);
			normalEnPunto.normalize();
			for (Iterator<Integer> iterator2 = triangulos.iterator(); iterator2
					.hasNext();) {
				Integer numTriangulo = (Integer) iterator2.next();
				for (int i = 0; i < 3; i++) {
					if (((int) trianguloPuntos[numTriangulo].getPoint(i).get(0)) == punto
							.intValue()) {
						triangulo[numTriangulo].setPoint(i + 3, normalEnPunto);
						break;
					}
				}
			}
		}
		if (lightmethod == ModeloIluminacion.PLANO) {
			for (Iterator<Integer> iterator2 = triangulos.iterator(); iterator2
					.hasNext();) {
				Integer numTriangulo = (Integer) iterator2.next();
				triangulo[numTriangulo].setPoint(3,
						calcularNormal(triangulo[numTriangulo]));
			}
		}
	}

	private Vector3D promediar(List<Vector3D> normales) {
		Vector3D resultado = new Vector3D();
		for (Iterator<Vector3D> iterator = normales.iterator(); iterator
				.hasNext();) {
			Vector3D vector3d = (Vector3D) iterator.next();
			resultado.add(vector3d);
		}
		resultado.divEscalar((double) normales.size());
		return resultado;
	}

	@Override
	public void reIniciar() {
		super.reIniciar();
		cargarDesdeArchivo();

	}


	@Override
	public void representar() {
		super.representar();
		Triangulo tria2 = new Triangulo();
		reescribirTriangulos();

		for (Triangulo tria : triangulo) {
			// Save WC points to pass for Phong illumination
			if (lightmethod == ModeloIluminacion.PHONG) {
				tria2.setPoint(0, tria.t[0]);
				tria2.setPoint(1, tria.t[1]);
				tria2.setPoint(2, tria.t[2]);
			}

			for (Matriz3D matriz : getTransformaciones()) {
				tria.transformPoly(matriz);
			}
			tria.transformPoly(translacionPreRotacion);
			tria.transformPoly(rotX);
			tria.transformPoly(rotY);
			tria.transformPoly(rotZ);
			tria.transformPoly(translacionPosRotacion);
			tria.transformPoly(translacionVisualizacion);
			if (perspectiva) {
				tria.transformPoly(persmat);
				// imprimirTriangulo(tria,4);
			}

			tria.divideByLast();
		}
		recalcularNormales();
		ordenarTriangulos();
		for (Triangulo tria : triangulo) {

			// imprimirTriangulo(tria,4);

			tria.convertToPixel(z, z, w, h);
			tria.zbuffer(buf, lightmethod, light, tria2, p);

		}

	}

	private void ordenarTriangulos() {

		Arrays.sort(triangulo, new Comparator<Triangulo>(){

			@Override
			public int compare(Triangulo arg0, Triangulo arg1) {
				return  (arg0.getPoint(1).getZ()>arg1.getPoint(1).getZ()? 1:arg0.getPoint(1).getZ()<arg1.getPoint(1).getZ()?-1:0);
		
			}});
	}

	private void reescribirTriangulos() {
		for (int i = 0; i < triangulo.length; i++) {
			Triangulo element = triangulo[i];
			element.setPoint(0, trianguloSalvados[i].t[0]);
			element.setPoint(1, trianguloSalvados[i].t[1]);
			element.setPoint(2, trianguloSalvados[i].t[2]);
			element.setPoint(3, trianguloSalvados[i].t[3]);
			element.setPoint(4, trianguloSalvados[i].t[4]);
			element.setPoint(5, trianguloSalvados[i].t[5]);
			element.setPoint(6, trianguloSalvados[i].t[6]);
		}
	}

	private String sacarEspaciosRepetidos(String linea) {
		String lineaResultado = "";
		lineaResultado += linea.charAt(0);
		for (int i = 1; i < linea.length(); i++) {
			char caracter = linea.charAt(i);
			if (caracter == ' '
					&& lineaResultado.charAt(lineaResultado.length() - 1) == ' ') {
				// lo salteo
			} else {
				lineaResultado += linea.charAt(i);
			}
		}
		return lineaResultado;
	}

	private void verificarExtremos(double x, double y, double z) {
		if (x > maxX) {
			maxX = x;
		}
		if (x < minX) {
			minX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (y < minY) {
			minY = y;
		}
		if (z > maxZ) {
			maxZ = z;
		}
		if (z < minZ) {
			minZ = z;
		}
	}
}
