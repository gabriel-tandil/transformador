package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import representacion.ModeloIluminacion;
import representacion.ObjetoTridimensional;
import representacion.Vector3D;

public class TransformadorPaintListener implements PaintListener {
	private Display display;
	private Canvas doubleBufferedCanvas;
	public Vector3D light = new Vector3D();
	ModeloIluminacion lightMethod = ModeloIluminacion.MARCOALAMBRE;
	private double luzAzul = 1;
	private double luzRoja = 1;
	private double luzVerde = 1;
	public ObjetoTridimensional objetoTridimensional;
	public int w = 600, h = 600;

	public void acercar() {
		objetoTridimensional.setZ(objetoTridimensional.getZ()
				+ objetoTridimensional.getPasoZoom());
		objetoTridimensional.representar();
	}

	public void acercarP() {
		objetoTridimensional.setF(objetoTridimensional.getF()
				+ objetoTridimensional.getF() / 2);
		objetoTridimensional.representar();
		System.out.println("F: " + objetoTridimensional.getF());
	}

	public void alejar() {
		objetoTridimensional.setZ(objetoTridimensional.getZ()
				- objetoTridimensional.getPasoZoom());
		objetoTridimensional.representar();
	}

	public void alejarP() {
		objetoTridimensional.setF(objetoTridimensional.getF()
				- objetoTridimensional.getF() / 2);
		objetoTridimensional.representar();
		System.out.println("F: " + objetoTridimensional.getF());
	}

	public ModeloIluminacion getLightMethod() {
		return lightMethod;
	}

	public double getLuzAzul() {
		return luzAzul;
	}

	public double getLuzRoja() {
		return luzRoja;
	}

	public double getLuzVerde() {
		return luzVerde;
	}

	public ObjetoTridimensional getObjetoTridimensional() {
		return objetoTridimensional;
	}

	public void init() {
		objetoTridimensional = new ObjetoTridimensional() { // un objeto
															// tridimensional
															// que no es nada
															// (antes de que se
															// cargue)

			@Override
			public Vector3D getCentro() {
				// TODO Auto-generated method stub
				return new Vector3D();
			}

			@Override
			public void recalcularNormales() {
				// TODO Auto-generated method stub

			}
		};
		light.set(0.0, 0.0, 1.0);
		objetoTridimensional.setRed(1);
	}

	@Override
	public void paintControl(PaintEvent e) {

		Image image =  new Image(display, doubleBufferedCanvas.getBounds());
		
		// Initializes the graphics context of the image.
		GC imageGC = new GC(image);

		// Performs actual drawing here
		int multiplicador = imageGC.getDevice().getSystemColor(SWT.COLOR_WHITE)
				.getBlue();
		double zbufinit;
		Color negro = new Color(imageGC.getDevice(), 0, 0, 0);

		double rojo = objetoTridimensional.red * multiplicador * luzRoja;
		double verde = objetoTridimensional.gr * multiplicador * luzVerde;
		double azul = objetoTridimensional.bl * multiplicador * luzAzul;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				// zbufinit = 0.0;
				// if (zbufinit > (objetoTridimensional.buf[i][j])) {
				zbufinit = (objetoTridimensional.buf[i][j]);

				// }

				if (zbufinit >= 0.0) {

					imageGC.setForeground(negro);

				} else {

					Color pix = new Color(imageGC.getDevice(),
							(int) (-zbufinit * rojo),
							(int) (-zbufinit * verde), (int) (-zbufinit * azul));
					imageGC.setForeground(pix);
				}

				imageGC.drawPoint(i, j);
			}
		}

		// Draws the buffer image onto the canvas.
		e.gc.drawImage(image, 0, 0);
		image.dispose();
		imageGC.dispose();
	}

	public void setCanvas(Canvas canvas1) {
		doubleBufferedCanvas = canvas1;

	}

	public void setDisplay(Display display) {
		this.display = display;

	}

	public void setH(int height) {
		h = height;
		objetoTridimensional.setH(h);
		objetoTridimensional.reIniciar();
	}

	public void setLigthMethod(ModeloIluminacion metodo) {
		lightMethod = metodo;
		objetoTridimensional.setLigthMethod(lightMethod);
		objetoTridimensional.recalcularNormales();
		objetoTridimensional.representar();
	}

	public void setLuzAzul(double luzAzul) {
		this.luzAzul = luzAzul;
	}

	public void setLuzRoja(double luzRoja) {
		this.luzRoja = luzRoja;
	}

	public void setLuzVerde(double luzVerde) {
		this.luzVerde = luzVerde;
	}

	public void setObjeto(ObjetoTridimensional objeto) {
		objetoTridimensional = objeto;
		objetoTridimensional.representar();
	}

	public void setW(int width) {
		w = width;
		objetoTridimensional.setW(w);
		objetoTridimensional.reIniciar();
	}
}
