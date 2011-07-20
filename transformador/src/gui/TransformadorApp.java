package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import representacion.Matriz3D;
import representacion.ModeloIluminacion;
import representacion.ObjetoSur;
import representacion.ObjetoTridimensional;
import representacion.Vector3D;

public class TransformadorApp extends org.eclipse.swt.widgets.Composite {

	/**
	 * @author gabriel.alvarez
	 * 
	 */
	public enum Accion {
		RotarXY, RotarXZ

	}

	private MenuItem aboutMenuItem;

	protected Accion accion;

	private Shell acercaDe;
	private Shell bending;
	private MenuItem bendingMenuItem;
	private Canvas canvas1;
	private Shell colorLuz;
	private MenuItem colorLuzMenuItem;
	private Menu colorMenu;
	private MenuItem colorMenuItem;
	private Shell colorObjeto;
	private MenuItem colorObjetoMenuItem;
	protected int comienzoX;
	protected int comienzoY;
	private Shell escala;
	private MenuItem escalaMenuItem;
	private Shell espejo;
	private MenuItem espejoMenuItem;
	private MenuItem exitMenuItem;
	private Menu fileMenu;

	private MenuItem fileMenuItem;
	private MenuItem gouraudMenuItem;
	private Menu helpMenu;
	private MenuItem helpMenuItem;
	private Menu menu1;
	private Menu modeloIluminacionMenu;

	private MenuItem modeloIluminacionMenuItem;
	private MenuItem openFileMenuItem;
	private MenuItem phongMenuItem;
	private MenuItem planoMenuItem;
	private MenuItem reiniciaMenuItem;
	private Shell rotacion;
	private MenuItem rotacionMenuItem;
	private Shell shear;
	private MenuItem shearMenuItem;
	private Shell tappering;
	private MenuItem tapperingMenuItem;
	private Menu transformacionMenu;
	private MenuItem transformacionMenuItem;
	private MenuItem transformacionperspectivaMenuItem;
	private TransformadorPaintListener transformadorPaintListener;
	private Shell translacion;
	private MenuItem translacionMenuItem;
	private Shell twisting;
	private MenuItem twistingMenuItem;
	private MenuItem splashAyudaMenuItem;
	private MenuItem wireframeMenuItem;

	public TransformadorApp(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	public static void main(String[] args) {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		TransformadorApp inst = new TransformadorApp(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if (size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		Rectangle winRect = shell.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - winRect.width) / 2;
		int y = (displayRect.height - winRect.height) / 2;
		shell.setLocation(x, y);

		splashAyuda();
		try {
			Thread.sleep(3000);
		} catch (Throwable e) {
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void splashAyuda() {
		Display display = Display.getDefault();
		Rectangle displayRect = display.getBounds();
		Rectangle winRect;
		int x;
		int y;
		final Image image = new Image(display, 300, 200);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(image.getBounds());
		gc.drawText(
				"Transformador\n\nRueda Mouse: Zoom acercar/alejar\nArrastrar: Rotar x,y\nCtrl. + Arrastrar : Rotar x,z\nCtrl. + Rueda Mouse: modificar perspectiva\nq,a,w,s,e,d: modificar posición luz (x,y,z)",
				10, 10);
		gc.dispose();
		final Shell splash = new Shell(SWT.ON_TOP);
		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
		FormLayout layout = new FormLayout();
		splash.setLayout(layout);
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100, 0);
		labelData.bottom = new FormAttachment(100, 0);
		label.setLayoutData(labelData);
		splash.pack();
		winRect = splash.getBounds();
		x = (displayRect.width - winRect.width) / 2;
		y = (displayRect.height - winRect.height) / 2;
		splash.setLocation(x, y);
		splash.open();

		display.asyncExec(new Runnable() {
			@Override
			public void run() {

				try {
					Thread.sleep(3000);
				} catch (Throwable e) {
				}

				splash.close();
				image.dispose();
			}
		});
	}

	private void dialogoAcercaDe() {
		{
			acercaDe = new Shell(getShell(), SWT.APPLICATION_MODAL
					| SWT.DIALOG_TRIM);
			acercaDe.setText("Acerca de...");
			acercaDe.setSize(450, 150);

			Button buttonOK = new Button(acercaDe, SWT.PUSH);
			buttonOK.setText("Aceptar");
			buttonOK.setBounds(300, 55, 90, 25);

			Label label = new Label(acercaDe, SWT.NONE);
			label.setText("Transformador. Trabajo final de Visualización I. Gabriel Alvarez");
			label.setBounds(20, 15, 400, 20);

			Listener listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					acercaDe.close();
				}
			};
			buttonOK.addListener(SWT.Selection, listener);
		}
		acercaDe.open();
	}

	private void dialogoColorLuz() {
		{
			colorLuz = new Shell(getShell(), SWT.APPLICATION_MODAL
					| SWT.DIALOG_TRIM);
			colorLuz.setLayout(new GridLayout());
			colorLuz.setText("Color de la Luz");
			colorLuz.setSize(250, 240);

			Label label = new Label(colorLuz, SWT.NONE);
			label.setText("Seleccione el color de la luz");
			Label labelR = new Label(colorLuz, SWT.NONE);
			labelR.setText("Rojo (valor entre 0 y 1)");
			final Spinner rojo = new Spinner(colorLuz, SWT.NONE);
			rojo.setDigits(3);
			rojo.setMinimum(0);
			rojo.setMaximum(1000);
			rojo.setIncrement(100);
			rojo.setSelection((int) (transformadorPaintListener.getLuzRoja() * 1000.0));
			rojo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = rojo.getSelection();
					int digits = rojo.getDigits();
					System.out.println("Rojo is "
							+ (selection / Math.pow(10, digits)));
					transformadorPaintListener.setLuzRoja(selection
							/ Math.pow(10, digits));
					canvas1.redraw();
				}

			});
			Label labelV = new Label(colorLuz, SWT.NONE);
			labelV.setText("Verde (valor entre 0 y 1)");
			final Spinner verde = new Spinner(colorLuz, SWT.NONE);
			verde.setDigits(3);
			verde.setMinimum(0);
			verde.setMaximum(1000);
			verde.setIncrement(100);
			verde.setSelection((int) (transformadorPaintListener.getLuzVerde() * 1000.0));
			verde.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = verde.getSelection();
					int digits = verde.getDigits();
					System.out.println("Verde is "
							+ (selection / Math.pow(10, digits)));
					transformadorPaintListener.setLuzVerde(selection
							/ Math.pow(10, digits));
					canvas1.redraw();
				}

			});
			Label labelA = new Label(colorLuz, SWT.NONE);
			labelA.setText("Azul (valor entre 0 y 1)");
			final Spinner azul = new Spinner(colorLuz, SWT.NONE);
			azul.setDigits(3);
			azul.setMinimum(0);
			azul.setMaximum(1000);
			azul.setIncrement(100);
			azul.setSelection((int) (transformadorPaintListener.getLuzAzul() * 1000.0));
			azul.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = azul.getSelection();
					int digits = azul.getDigits();
					System.out.println("Azul is "
							+ (selection / Math.pow(10, digits)));
					transformadorPaintListener.setLuzAzul(selection
							/ Math.pow(10, digits));
					canvas1.redraw();
				}

			});

			Button buttonOK = new Button(colorLuz, SWT.PUSH);
			buttonOK.setText("Aceptar");
			Listener listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					colorLuz.close();
				}
			};
			buttonOK.addListener(SWT.Selection, listener);
		}
		colorLuz.open();
	}

	private void dialogoColorObjeto() {
		{
			colorObjeto = new Shell(getShell(), SWT.APPLICATION_MODAL
					| SWT.DIALOG_TRIM);
			colorObjeto.setLayout(new GridLayout());
			colorObjeto.setText("Color del Objeto");
			colorObjeto.setSize(250, 240);

			Label label = new Label(colorObjeto, SWT.NONE);
			label.setText("Seleccione el color del objeto");
			Label labelR = new Label(colorObjeto, SWT.NONE);
			labelR.setText("Rojo (valor entre 0 y 1)");
			final Spinner rojo = new Spinner(colorObjeto, SWT.NONE);
			rojo.setDigits(3);
			rojo.setMinimum(0);
			rojo.setMaximum(1000);
			rojo.setIncrement(100);
			rojo.setSelection((int) (transformadorPaintListener
					.getObjetoTridimensional().getRed() * 1000.0));
			rojo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = rojo.getSelection();
					int digits = rojo.getDigits();
					System.out.println("Rojo is "
							+ (selection / Math.pow(10, digits)));
					transformadorPaintListener.getObjetoTridimensional()
							.setRed(selection / Math.pow(10, digits));
					canvas1.redraw();
				}

			});
			Label labelV = new Label(colorObjeto, SWT.NONE);
			labelV.setText("Verde (valor entre 0 y 1)");
			final Spinner verde = new Spinner(colorObjeto, SWT.NONE);
			verde.setDigits(3);
			verde.setMinimum(0);
			verde.setMaximum(1000);
			verde.setIncrement(100);

			verde.setSelection((int) (transformadorPaintListener
					.getObjetoTridimensional().getGr() * 1000.0));
			verde.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = verde.getSelection();
					int digits = verde.getDigits();
					System.out.println("Verde is "
							+ (selection / Math.pow(10, digits)));
					transformadorPaintListener.getObjetoTridimensional().setGr(
							selection / Math.pow(10, digits));
					canvas1.redraw();
				}

			});
			Label labelA = new Label(colorObjeto, SWT.NONE);
			labelA.setText("Azul (valor entre 0 y 1)");
			final Spinner azul = new Spinner(colorObjeto, SWT.NONE);
			azul.setDigits(3);
			azul.setMinimum(0);
			azul.setMaximum(1000);
			azul.setIncrement(100);
			azul.setSelection((int) (transformadorPaintListener
					.getObjetoTridimensional().getBl() * 1000.0));
			azul.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = azul.getSelection();
					int digits = azul.getDigits();
					System.out.println("Azul is "
							+ (selection / Math.pow(10, digits)));
					transformadorPaintListener.getObjetoTridimensional().setBl(
							selection / Math.pow(10, digits));
					canvas1.redraw();
				}

			});

			Button buttonOK = new Button(colorObjeto, SWT.PUSH);
			buttonOK.setText("Aceptar");
			Listener listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					colorObjeto.close();
				}
			};
			buttonOK.addListener(SWT.Selection, listener);
		}
		colorObjeto.open();
	}

	private void dialogoEspejado() {
		{
			espejo = new Shell(getShell(), SWT.APPLICATION_MODAL
					| SWT.DIALOG_TRIM);
			espejo.setText("Espejado");
			espejo.setSize(450, 150);

			Button buttonOK = new Button(espejo, SWT.PUSH);
			buttonOK.setText("Aceptar");
			buttonOK.setBounds(300, 55, 90, 25);

			Label label = new Label(espejo, SWT.NONE);
			label.setText("Aplicar transformacion de espejado en eje");
			label.setBounds(20, 15, 400, 20);
			final Combo combo = new Combo(espejo, SWT.DROP_DOWN);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			combo.setItems(new String[] { "X", "Y", "Z" });
			combo.select(0);
			combo.setBounds(20, 32, 50, 25);
			Listener listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					combo.getSelectionIndex();
					espejo.close();
				}
			};
			buttonOK.addListener(SWT.Selection, listener);
		}
		espejo.open();
	}

	private void initGUI() {
		try {
			transformadorPaintListener = new TransformadorPaintListener();
			transformadorPaintListener.init();

			getShell().setText("Transformador");
			this.setSize(new org.eclipse.swt.graphics.Point(600, 600));
			this.setBackground(new Color(this.getDisplay(), 192, 192, 192));

			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);

			{

				canvas1 = new Canvas(this, SWT.NONE);
				// FillLayout canvas1Layout = new FillLayout(
				// org.eclipse.swt.SWT.HORIZONTAL);
				FormData canvas1LData = new FormData();
				canvas1LData.left = new FormAttachment(0);
				canvas1LData.top = new FormAttachment(0);
				// canvas1LData.width = 400;
				// canvas1LData.height = 300;
				canvas1LData.right = new FormAttachment(90);
				canvas1LData.bottom = new FormAttachment(90);
				canvas1.setLayoutData(canvas1LData);
				// canvas1.setLayout(canvas1Layout);
				transformadorPaintListener.setCanvas(canvas1);
				transformadorPaintListener.setDisplay(this.getDisplay());
				getShell().addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent arg0) {
						Vector3D v = new Vector3D();
						v.set(0, 0, 0);
						Matriz3D traslacion = new Matriz3D();

						if (arg0.character == 'q') {
							v.set(1, 0, 0);
						}
						if (arg0.character == 'a') {
							v.set(-1, 0, 0);
						}
						if (arg0.character == 'w') {
							v.set(0, 1, 0);
						}
						if (arg0.character == 's') {
							v.set(0, -1, 0);
						}
						if (arg0.character == 'e') {
							v.set(0, 0, 1);
						}
						if (arg0.character == 'd') {
							v.set(0, 0, -1);
						}
						traslacion.translate(v);
						transformadorPaintListener.light.transform(traslacion);
						transformadorPaintListener.objetoTridimensional
								.recalcularNormales();
						transformadorPaintListener.objetoTridimensional
								.representar();
						canvas1.redraw();

					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub

					}
				});
				canvas1.addPaintListener(transformadorPaintListener);
				canvas1.addMouseWheelListener(new MouseWheelListener() {
					@Override
					public void mouseScrolled(MouseEvent arg0) {
						if (arg0.stateMask == 0) {
							if (arg0.count > 0) {
								transformadorPaintListener.acercar();
								canvas1.redraw();
							}
							if (arg0.count < 0) {
								transformadorPaintListener.alejar();
								canvas1.redraw();
							}
						} else {
							if (arg0.count > 0) {
								transformadorPaintListener.acercarP();
								canvas1.redraw();
							}
							if (arg0.count < 0) {
								transformadorPaintListener.alejarP();
								canvas1.redraw();
							}

						}

					}
				});
				canvas1.addListener(SWT.MouseDown, new Listener() {

					@Override
					public void handleEvent(Event arg0) {
						comienzoX = arg0.x;
						comienzoY = arg0.y;
						if (arg0.stateMask == 0) {
							if (arg0.button == 1) {
								accion = Accion.RotarXY;
							}
						} else {
							if (arg0.button == 1) {
								accion = Accion.RotarXZ;
							}
						}
					}
				});
				canvas1.addListener(SWT.MouseUp, new Listener() {

					@Override
					public void handleEvent(Event arg0) {
						transformadorPaintListener.getObjetoTridimensional()
								.getTransformaciones().clear();
						int distanciaX = comienzoX - arg0.x;
						int distanciaY = comienzoY - arg0.y;
						Rectangle rect = getShell().getClientArea();
						int maxX = rect.width / 2;
						int maxY = rect.height / 2;
						double anguloY = (distanciaX / 180.0) * maxX;
						double anguloX = (distanciaY / 180.0) * maxY;
						System.out.println(anguloX + " : " + anguloY);

						transformadorPaintListener.getObjetoTridimensional()
								.setAnguloX(
										transformadorPaintListener
												.getObjetoTridimensional()
												.getAnguloX()
												+ anguloX);
						System.out
								.println("Rotacion X: "
										+ transformadorPaintListener
												.getObjetoTridimensional()
												.getAnguloX());

						if (accion == Accion.RotarXY) {
							transformadorPaintListener
									.getObjetoTridimensional().setAnguloY(
											transformadorPaintListener
													.getObjetoTridimensional()
													.getAnguloY()
													+ anguloY);
							System.out.println("Rotacion Y: "
									+ transformadorPaintListener
											.getObjetoTridimensional()
											.getAnguloY());
						}

						if (accion == Accion.RotarXZ) {
							transformadorPaintListener
									.getObjetoTridimensional().setAnguloZ(
											transformadorPaintListener
													.getObjetoTridimensional()
													.getAnguloZ()
													+ anguloY);
							System.out.println("Rotacion Z: "
									+ transformadorPaintListener
											.getObjetoTridimensional()
											.getAnguloZ());
						}

						transformadorPaintListener.getObjetoTridimensional()
								.representar();
						canvas1.redraw();

					}
				});

				canvas1.addListener(SWT.Resize, new Listener() {
					@Override
					public void handleEvent(Event e) {
						Rectangle rect = getShell().getClientArea();
						int x = rect.width;
						int y = rect.height;
						if (x > y) {
							y = x;
						}
						if (y > x) {
							x = y;
						}
						canvas1.setSize(new org.eclipse.swt.graphics.Point(x, y));
						transformadorPaintListener.setW(x);
						transformadorPaintListener.setH(y);

					}
				});
			}

			{
				menu1 = new Menu(getShell(), SWT.BAR);
				getShell().setMenuBar(menu1);
				{
					fileMenuItem = new MenuItem(menu1, SWT.CASCADE);
					fileMenuItem.setText("Archivo");
					{
						fileMenu = new Menu(fileMenuItem);
						{
							openFileMenuItem = new MenuItem(fileMenu,
									SWT.CASCADE);
							openFileMenuItem.setText("Abrir");
							openFileMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent event) {
											FileDialog fd = new FileDialog(
													getShell(), SWT.OPEN);
											fd.setText("Abrir");
											String[] filterExt = { "*.sur",
													"*.SUR", "*.*" };
											fd.setFilterExtensions(filterExt);
											String selected = fd.open();
											ObjetoTridimensional objeto = new ObjetoSur(transformadorPaintListener
													.getObjetoTridimensional().getPerspectiva(),
													selected,
													transformadorPaintListener.h,
													transformadorPaintListener.w,
													transformadorPaintListener
															.getObjetoTridimensional().red,
													transformadorPaintListener
															.getObjetoTridimensional().gr,
													transformadorPaintListener
															.getObjetoTridimensional().bl,
													transformadorPaintListener.light,
													transformadorPaintListener
															.getLightMethod(),
													5);
											transformadorPaintListener
													.setObjeto(objeto);
											transformacionMenuItem
													.setEnabled(true);
											canvas1.redraw();
										}
									});

						}
						{
							exitMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
							exitMenuItem.setText("Salir");
							exitMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											getShell().close();
										}
									});
						}
						fileMenuItem.setMenu(fileMenu);
					}
				}
				{
					modeloIluminacionMenuItem = new MenuItem(menu1, SWT.CASCADE);
					modeloIluminacionMenuItem.setText("Modelo Iluminación");
					{

						modeloIluminacionMenu = new Menu(
								modeloIluminacionMenuItem);
						{
							wireframeMenuItem = new MenuItem(
									modeloIluminacionMenu, SWT.RADIO);
							wireframeMenuItem.setText("Marco de Alambre");
							wireframeMenuItem.setSelection(true);
							wireframeMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											transformadorPaintListener
													.setLigthMethod(ModeloIluminacion.MARCOALAMBRE);
											canvas1.redraw();
										}
									});
						}
						{
							planoMenuItem = new MenuItem(modeloIluminacionMenu,
									SWT.RADIO);
							planoMenuItem.setText("Plano");
							planoMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											transformadorPaintListener
													.setLigthMethod(ModeloIluminacion.PLANO);
											canvas1.redraw();
										}
									});
						}
						{
							gouraudMenuItem = new MenuItem(
									modeloIluminacionMenu, SWT.RADIO);
							gouraudMenuItem.setText("Gouraud");
							gouraudMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											transformadorPaintListener
													.setLigthMethod(ModeloIluminacion.GOURAUD);
											canvas1.redraw();
										}
									});
						}
						{
							phongMenuItem = new MenuItem(modeloIluminacionMenu,
									SWT.RADIO);
							phongMenuItem.setText("Phong");
							phongMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											transformadorPaintListener
													.setLigthMethod(ModeloIluminacion.PHONG);
											canvas1.redraw();
										}
									});
						}
						modeloIluminacionMenuItem
								.setMenu(modeloIluminacionMenu);
					}
				}
				{
					transformacionMenuItem = new MenuItem(menu1, SWT.CASCADE);
					transformacionMenuItem.setEnabled(false);
					transformacionMenuItem.setText("Transformación");
					{

						transformacionMenu = new Menu(transformacionMenuItem);
						{
							transformacionperspectivaMenuItem = new MenuItem(
									transformacionMenu, SWT.CHECK);
							transformacionperspectivaMenuItem
									.setText("Perspectiva");
							transformacionperspectivaMenuItem
									.setSelection(true);
							transformacionperspectivaMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											transformadorPaintListener.objetoTridimensional
													.setPerspectiva(transformacionperspectivaMenuItem
															.getSelection());
											transformadorPaintListener
													.getObjetoTridimensional()
													.representar();
											canvas1.redraw();
										}
									});
						}
						{
							escalaMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							escalaMenuItem.setEnabled(false);
							escalaMenuItem.setText("Escala");
							escalaMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											escala.open();
										}
									});
						}
						{
							translacionMenuItem = new MenuItem(
									transformacionMenu, SWT.CASCADE);
							translacionMenuItem.setText("Translación");
							translacionMenuItem.setEnabled(false);
							translacionMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											translacion.open();
										}
									});
						}
						{
							rotacionMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							rotacionMenuItem.setText("Rotación");
							rotacionMenuItem.setEnabled(false);
							rotacionMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											rotacion.open();
										}
									});
						}
						{
							espejoMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							espejoMenuItem.setText("Espejo");
							espejoMenuItem.setEnabled(false);
							espejoMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											dialogoEspejado();
										}
									});
						}
						{
							shearMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							shearMenuItem.setText("Shear");
							shearMenuItem.setEnabled(false);
							shearMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											shear.open();
										}
									});
						}
						{
							twistingMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							twistingMenuItem.setText("Twisting");
							twistingMenuItem.setEnabled(false);
							twistingMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											twisting.open();
										}
									});
						}
						{
							tapperingMenuItem = new MenuItem(
									transformacionMenu, SWT.CASCADE);
							tapperingMenuItem.setText("Tappering");
							tapperingMenuItem.setEnabled(false);
							tapperingMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											tappering.open();
										}
									});
						}
						{
							bendingMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							bendingMenuItem.setText("Bending");
							bendingMenuItem.setEnabled(false);
							bendingMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											bending.open();
										}
									});
						}
						new MenuItem(transformacionMenu, SWT.SEPARATOR);
						{
							reiniciaMenuItem = new MenuItem(transformacionMenu,
									SWT.CASCADE);
							reiniciaMenuItem.setText("Reinicia");
							reiniciaMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											((ObjetoSur) transformadorPaintListener
													.getObjetoTridimensional())
													.cargarDesdeArchivo();
										}
									});
						}
						transformacionMenuItem.setMenu(transformacionMenu);
					}
				}
				{
					colorMenuItem = new MenuItem(menu1, SWT.CASCADE);
					colorMenuItem.setText("Color");
					{
						colorMenu = new Menu(colorMenuItem);
						{
							colorObjetoMenuItem = new MenuItem(colorMenu,
									SWT.CASCADE);
							colorObjetoMenuItem.setText("Color del Objeto");
							colorObjetoMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											dialogoColorObjeto();
										}
									});
						}
						{
							colorLuzMenuItem = new MenuItem(colorMenu,
									SWT.CASCADE);
							colorLuzMenuItem.setText("Color de la Luz");
							colorLuzMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											dialogoColorLuz();
										}
									});
						}
						colorMenuItem.setMenu(colorMenu);
					}
				}

				{
					helpMenuItem = new MenuItem(menu1, SWT.CASCADE);
					helpMenuItem.setText("?");
					{
						helpMenu = new Menu(helpMenuItem);
						{
							splashAyudaMenuItem = new MenuItem(helpMenu,
									SWT.CASCADE);
							splashAyudaMenuItem.setText("Ayuda");
							splashAyudaMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											splashAyuda();
										}
									});
						}
						{
							aboutMenuItem = new MenuItem(helpMenu, SWT.CASCADE);
							aboutMenuItem.setText("Acerca de...");
							aboutMenuItem
									.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(
												SelectionEvent evt) {
											dialogoAcercaDe();
										}
									});
						}

						helpMenuItem.setMenu(helpMenu);
					}
				}
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
