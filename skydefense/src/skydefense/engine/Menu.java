package skydefense.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import skydefense.model.Leaderboard;

public class Menu extends JPanel {

    private String[] opciones = {"PLAY", "SCORES", "OPTIONS", "EXIT"};
    private Rectangle[] hitboxes = new Rectangle[4];
    private int hoveredIndex = -1;

    private int[] posicionesY = {200, 275, 350, 425};
    private int posicionXBase = 75;

    private Font fuenteNormal;
    private Font fuenteHover;
    private Font fuenteTitulo;

    private int mouseX = 500;
    private int mouseY = 280;

    private BufferedImage spriteNave;
    private BufferedImage logo;
    private Image gifAmigos;

    private BufferedImage[] presentaciones = new BufferedImage[4];
    private int presentacionActual = 0;
    private Timer timerPresentacion;
    private float alphaLogoFinal = 0f;
    private float alphaLogoProgramadores = 0f;

    private Clip musicaPresentacion;
    private Clip musicaLogo;
    private Clip musicaJuego;
    private Clip musicaLogoProgramadores;

    private float alphaPresentacion = 1f;
    private boolean cambiandoDiapositiva = false;

    private boolean mostrandoConfirmacionExit = false;
    private boolean mostrarLogoInicial;

    private boolean pantallaCompleta = false;
    private Rectangle boundsVentana;

    private Rectangle botonYes = new Rectangle();
    private Rectangle botonNo = new Rectangle();

    private enum EstadoPantalla {
        LOGO_PROGRAMADORES,
        MENU,
        SCORES,
        PRESENTACION,
        TITULO_FINAL,
        JUEGO
    }

    private EstadoPantalla estadoPantalla;

    private ArrayList<Disparo> disparos = new ArrayList<>();

    public Menu() {
        this(true);
    }

    public Menu(boolean mostrarLogoInicial) {
        this.mostrarLogoInicial = mostrarLogoInicial;

        System.out.println("Working dir: " + new File(".").getAbsolutePath());
        setBackground(Color.BLACK);
        setFocusable(true);

        estadoPantalla = mostrarLogoInicial ? EstadoPantalla.LOGO_PROGRAMADORES : EstadoPantalla.MENU;

        cargarRecursos();
        crearHitboxes();
        configurarMouse();
        configurarTeclado();
        iniciarTimerMenu();

        if (mostrarLogoInicial) {
            iniciarLogoProgramadores();
        } else {
            reproducirMusicaLogo();
        }
    }

    private void cargarRecursos() {
        try {
            File archivoFuente = new File("skydefense/res/font/Arcade.ttf");
            File archivoNightmare = new File("skydefense/res/font/Nightmare Codehack.otf");

            Font fuenteBase = Font.createFont(Font.TRUETYPE_FONT, archivoFuente);
            fuenteNormal = fuenteBase.deriveFont(40f);
            fuenteHover = fuenteBase.deriveFont(50f);

            Font baseNightmare = Font.createFont(Font.TRUETYPE_FONT, archivoNightmare);
            fuenteTitulo = baseNightmare.deriveFont(70f);

        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente personalizada. Cargando Arial de repuesto.");
            fuenteNormal = new Font("Arial", Font.BOLD, 26);
            fuenteHover = new Font("Arial", Font.BOLD, 32);
            fuenteTitulo = new Font("Arial", Font.BOLD, 60);
        }

        try {
            spriteNave = ImageIO.read(new File("skydefense/res/sprite/nave.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de la nave. Verificá la ruta y el nombre.");
        }

        try {
            logo = ImageIO.read(new File("skydefense/res/sprite/logo.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del logo. Verificá la ruta y el nombre.");
        }

        try {
            gifAmigos = new ImageIcon("skydefense/res/sprite/betterLogo.gif").getImage();
        } catch (Exception e) {
            System.err.println("No se pudo cargar el GIF de la intro betterLogo.");
        }

        try {
            presentaciones[0] = ImageIO.read(new File("skydefense/res/sprite/presentacion1.png"));
            presentaciones[1] = ImageIO.read(new File("skydefense/res/sprite/presentacion2.png"));
            presentaciones[2] = ImageIO.read(new File("skydefense/res/sprite/presentacion3.png"));
            presentaciones[3] = ImageIO.read(new File("skydefense/res/sprite/presentacion4.png"));
        } catch (Exception e) {
            System.err.println("No se pudieron cargar las imágenes de presentación.");
        }
    }

    private void crearHitboxes() {
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(fuenteNormal);

        for (int i = 0; i < opciones.length; i++) {
            int anchoTexto = fm.stringWidth(opciones[i]);
            int altoTexto = fm.getHeight();
            int ascent = fm.getAscent();

            hitboxes[i] = new Rectangle(posicionXBase, posicionesY[i] - ascent, anchoTexto, altoTexto);
        }
    }

    private void configurarMouse() {
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (mostrandoConfirmacionExit) {
                    return;
                }

                mouseY = e.getY();
                mouseX = e.getX();

                if (estadoPantalla != EstadoPantalla.MENU) {
                    repaint();
                    return;
                }

                int previousHover = hoveredIndex;
                hoveredIndex = -1;

                for (int i = 0; i < hitboxes.length; i++) {
                    if (hitboxes[i].contains(e.getPoint())) {
                        hoveredIndex = i;
                        break;
                    }
                }

                if (previousHover != hoveredIndex) {
                    repaint();
                }

                repaint();
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();

                if (mostrandoConfirmacionExit) {
                    if (botonYes.contains(e.getPoint())) {
                        System.exit(0);
                    }

                    if (botonNo.contains(e.getPoint())) {
                        mostrandoConfirmacionExit = false;
                        repaint();
                    }

                    return;
                }

                if (estadoPantalla == EstadoPantalla.SCORES) {
                    estadoPantalla = EstadoPantalla.MENU;
                    repaint();
                    return;
                }

                if (estadoPantalla == EstadoPantalla.MENU && hoveredIndex == 0) {
                    ejecutarPlay();
                } else if (estadoPantalla == EstadoPantalla.MENU && hoveredIndex == 1) {
                    estadoPantalla = EstadoPantalla.SCORES;
                    repaint();
                } else if (estadoPantalla == EstadoPantalla.MENU && hoveredIndex == 2) {
                    alternarPantallaCompleta();
                } else if (estadoPantalla == EstadoPantalla.MENU && hoveredIndex == 3) {
                    mostrarConfirmacionExit();
                } else if (estadoPantalla == EstadoPantalla.MENU) {
                    disparar();
                }
            }
        });
    }

    private void configurarTeclado() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (mostrandoConfirmacionExit) {
                    if (e.getKeyCode() == KeyEvent.VK_Y || e.getKeyCode() == KeyEvent.VK_ENTER) {
                        System.exit(0);
                    }

                    if (e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        mostrandoConfirmacionExit = false;
                        repaint();
                    }

                    return;
                }

                if (estadoPantalla == EstadoPantalla.SCORES) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE ||
                        e.getKeyCode() == KeyEvent.VK_ESCAPE ||
                        e.getKeyCode() == KeyEvent.VK_ENTER) {
                        estadoPantalla = EstadoPantalla.MENU;
                        repaint();
                    }

                    return;
                }

                if (estadoPantalla == EstadoPantalla.MENU) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        moverSeleccionArriba();
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        moverSeleccionAbajo();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        ejecutarOpcionSeleccionada();
                    } else if (e.getKeyCode() == KeyEvent.VK_F11) {
                        alternarPantallaCompleta();
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE &&
                    (estadoPantalla == EstadoPantalla.PRESENTACION || estadoPantalla == EstadoPantalla.TITULO_FINAL)) {
                    saltarAlJuego();
                }
            }
        });
    }

    private void iniciarTimerMenu() {
        Timer timer = new Timer(16, e -> {
            if (estadoPantalla == EstadoPantalla.MENU) {
                for (int i = disparos.size() - 1; i >= 0; i--) {
                    Disparo d = disparos.get(i);
                    d.mover();

                    if (d.estaFuera(getWidth(), getHeight())) {
                        disparos.remove(i);
                    }
                }
            }

            repaint();
        });

        timer.start();
    }

    private void moverSeleccionArriba() {
        if (hoveredIndex == -1) {
            hoveredIndex = 0;
        } else {
            hoveredIndex--;

            if (hoveredIndex < 0) {
                hoveredIndex = opciones.length - 1;
            }
        }

        repaint();
    }

    private void moverSeleccionAbajo() {
        if (hoveredIndex == -1) {
            hoveredIndex = 0;
        } else {
            hoveredIndex++;

            if (hoveredIndex >= opciones.length) {
                hoveredIndex = 0;
            }
        }

        repaint();
    }

    private void ejecutarOpcionSeleccionada() {
        if (hoveredIndex == 0) {
            ejecutarPlay();
        } else if (hoveredIndex == 1) {
            estadoPantalla = EstadoPantalla.SCORES;
            repaint();
        } else if (hoveredIndex == 2) {
            alternarPantallaCompleta();
        } else if (hoveredIndex == 3) {
            mostrarConfirmacionExit();
        }
    }

    private void ejecutarPlay() {
        detenerMusicaLogo();
        iniciarPresentacion();
    }

    private void mostrarConfirmacionExit() {
        mostrandoConfirmacionExit = true;
        repaint();
    }

    private void iniciarLogoProgramadores() {
        estadoPantalla = EstadoPantalla.LOGO_PROGRAMADORES;
        alphaLogoProgramadores = 0f;

        reproducirMusicaLogoProgramadores();

        Timer timerLogoProgramadores = new Timer(50, e -> {
            alphaLogoProgramadores += 0.03f;

            if (alphaLogoProgramadores >= 1f) {
                alphaLogoProgramadores = 1f;
                ((Timer) e.getSource()).stop();

                Timer espera = new Timer(4500, ev -> {
                    ((Timer) ev.getSource()).stop();
                    detenerMusicaLogoProgramadores();
                    estadoPantalla = EstadoPantalla.MENU;
                    reproducirMusicaLogo();
                    repaint();
                });

                espera.setRepeats(false);
                espera.start();
            }

            repaint();
        });

        timerLogoProgramadores.start();
    }

    private void iniciarPresentacion() {
        estadoPantalla = EstadoPantalla.PRESENTACION;
        presentacionActual = 0;
        alphaPresentacion = 1f;
        cambiandoDiapositiva = false;
        disparos.clear();
        reproducirMusicaPresentacion();
        repaint();

        timerPresentacion = new Timer(5000, e -> {
            iniciarCambioDiapositiva();
        });

        timerPresentacion.start();
    }

    private void iniciarCambioDiapositiva() {
        if (cambiandoDiapositiva) {
            return;
        }

        cambiandoDiapositiva = true;

        Timer fadeOut = new Timer(25, null);
        fadeOut.addActionListener(e -> {
            alphaPresentacion -= 0.12f;

            if (alphaPresentacion <= 0f) {
                alphaPresentacion = 0f;
                fadeOut.stop();

                presentacionActual++;

                if (presentacionActual >= presentaciones.length) {
                    if (timerPresentacion != null) {
                        timerPresentacion.stop();
                    }

                    iniciarTituloFinal();
                    return;
                }

                Timer fadeIn = new Timer(25, null);
                fadeIn.addActionListener(ev -> {
                    alphaPresentacion += 0.12f;

                    if (alphaPresentacion >= 1f) {
                        alphaPresentacion = 1f;
                        cambiandoDiapositiva = false;
                        fadeIn.stop();
                    }

                    repaint();
                });

                fadeIn.start();
            }

            repaint();
        });

        fadeOut.start();
    }

    private void iniciarTituloFinal() {
        estadoPantalla = EstadoPantalla.TITULO_FINAL;
        alphaLogoFinal = 0f;

        Timer timerLogo = new Timer(50, e -> {
            alphaLogoFinal += 0.03f;

            if (alphaLogoFinal >= 1f) {
                alphaLogoFinal = 1f;
                ((Timer) e.getSource()).stop();

                Timer espera = new Timer(1500, ev -> {
                    ((Timer) ev.getSource()).stop();
                    saltarAlJuego();
                });

                espera.setRepeats(false);
                espera.start();
            }

            repaint();
        });

        timerLogo.start();
    }

    private void saltarAlJuego() {
        if (timerPresentacion != null) {
            timerPresentacion.stop();
        }

        detenerMusicaPresentacion();
        detenerMusicaLogo();
        detenerMusicaLogoProgramadores();

        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (ventana != null) {
            GamePanel gamePanel = new GamePanel(ventana);
            ventana.setContentPane(gamePanel);
            ventana.revalidate();
            ventana.repaint();
            gamePanel.requestFocusInWindow();
        }

        System.out.println("Iniciando juego.");
    }

    private void alternarPantallaCompleta() {
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (ventana == null) {
            return;
        }

        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();

        if (!pantallaCompleta) {
            boundsVentana = ventana.getBounds();

            ventana.dispose();
            ventana.setResizable(true);
            ventana.setUndecorated(true);
            ventana.setVisible(true);

            device.setFullScreenWindow(ventana);
            pantallaCompleta = true;
        } else {
            device.setFullScreenWindow(null);

            ventana.dispose();
            ventana.setUndecorated(false);

            if (boundsVentana != null) {
                ventana.setBounds(boundsVentana);
            } else {
                ventana.setSize(800, 600);
                ventana.setLocationRelativeTo(null);
            }

            ventana.setResizable(false);
            ventana.setVisible(true);
            pantallaCompleta = false;
        }

        requestFocusInWindow();
    }

    private Clip cargarClip(String ruta) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(ruta));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (Exception e) {
            System.err.println("No se pudo cargar el audio: " + ruta);
            return null;
        }
    }

    private void reproducirMusicaPresentacion() {
        detenerMusicaPresentacion();

        musicaPresentacion = cargarClip("skydefense/res/sfx/introPresentacion.wav");

        if (musicaPresentacion != null) {
            setVolumen(musicaPresentacion, 1f);
            musicaPresentacion.start();
        }
    }

    private void reproducirMusicaLogo() {
        detenerMusicaLogo();

        musicaLogo = cargarClip("skydefense/res/sfx/musicaLogo.wav");

        if (musicaLogo != null) {
            setVolumen(musicaLogo, 1f);
            musicaLogo.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void reproducirMusicaLogoProgramadores() {
        detenerMusicaLogoProgramadores();

        musicaLogoProgramadores = cargarClip("skydefense/res/sfx/musicaLogoProgramadores.wav");

        if (musicaLogoProgramadores != null) {
            setVolumen(musicaLogoProgramadores, 1f);
            musicaLogoProgramadores.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void reproducirMusicaJuego() {
        detenerMusicaJuego();

        musicaJuego = cargarClip("skydefense/res/sfx/musicaJuego.wav");

        if (musicaJuego != null) {
            setVolumen(musicaJuego, 1f);
            musicaJuego.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void setVolumen(Clip clip, float volumen) {
        if (clip == null) {
            return;
        }

        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float min = control.getMinimum();
        float max = control.getMaximum();

        if (volumen <= 0f) {
            volumen = 0.0001f;
        }

        float ganancia = (float) (20.0 * Math.log10(volumen));

        if (ganancia < min) {
            ganancia = min;
        }

        if (ganancia > max) {
            ganancia = max;
        }

        control.setValue(ganancia);
    }

    private void detenerMusicaPresentacion() {
        if (musicaPresentacion != null) {
            musicaPresentacion.stop();
            musicaPresentacion.close();
            musicaPresentacion = null;
        }
    }

    private void detenerMusicaLogo() {
        if (musicaLogo != null) {
            musicaLogo.stop();
            musicaLogo.close();
            musicaLogo = null;
        }
    }

    private void detenerMusicaLogoProgramadores() {
        if (musicaLogoProgramadores != null) {
            musicaLogoProgramadores.stop();
            musicaLogoProgramadores.close();
            musicaLogoProgramadores = null;
        }
    }

    private void detenerMusicaJuego() {
        if (musicaJuego != null) {
            musicaJuego.stop();
            musicaJuego.close();
            musicaJuego = null;
        }
    }

    private void disparar() {
        int anchoNave = 180;
        int altoNave = 180;
        int naveX = 500;
        int naveY = 250;

        double centroX = naveX + anchoNave / 2.0;
        double centroY = naveY + altoNave / 2.0;

        double angulo = Math.atan2(mouseY - centroY, mouseX - centroX);

        double puntaX = centroX + Math.cos(angulo) * (altoNave / 2.0);
        double puntaY = centroY + Math.sin(angulo) * (altoNave / 2.0);

        disparos.add(new Disparo(puntaX, puntaY, angulo));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (estadoPantalla == EstadoPantalla.LOGO_PROGRAMADORES) {
            dibujarLogoProgramadores(g2d);
            return;
        }

        if (estadoPantalla == EstadoPantalla.SCORES) {
            dibujarScores(g2d);
            return;
        }

        if (estadoPantalla == EstadoPantalla.PRESENTACION) {
            dibujarPresentacion(g2d);
            return;
        }

        if (estadoPantalla == EstadoPantalla.TITULO_FINAL) {
            dibujarTituloFinal(g2d);
            return;
        }

        dibujarMenu(g2d);
    }

    private void dibujarMenu(Graphics2D g2d) {
        if (logo != null) {
            int altoLogo = 150;
            int anchoLogo = (int) ((double) logo.getWidth() / logo.getHeight() * altoLogo);

            int logoX = 450;
            int logoY = 5;

            g2d.drawImage(logo, logoX, logoY, anchoLogo, altoLogo, null);
        }

        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];

            if (i == hoveredIndex) {
                g2d.setFont(fuenteHover);
                g2d.setColor(Color.ORANGE);

                FontMetrics fmHover = g2d.getFontMetrics();
                int anchoNormal = hitboxes[i].width;
                int anchoHover = fmHover.stringWidth(texto);
                int xAjustado = posicionXBase - ((anchoHover - anchoNormal) / 2);

                g2d.drawString(texto, xAjustado, posicionesY[i]);
            } else {
                g2d.setFont(fuenteNormal);

                Color blancoResplandor = new Color(255, 255, 255, 60);
                g2d.setColor(blancoResplandor);

                int desfase = 2;
                g2d.drawString(texto, posicionXBase - desfase, posicionesY[i] - desfase);
                g2d.drawString(texto, posicionXBase + desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, posicionXBase - desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, posicionXBase + desfase, posicionesY[i] - desfase);

                g2d.setColor(Color.WHITE);
                g2d.drawString(texto, posicionXBase, posicionesY[i]);
            }
        }

        for (Disparo d : disparos) {
            d.dibujar(g2d);
        }

        if (spriteNave != null) {
            int anchoNave = 180;
            int altoNave = 180;
            int naveX = 500;
            int naveY = 250;

            int centroX = naveX + anchoNave / 2;
            int centroY = naveY + altoNave / 2;

            double angulo = Math.atan2(mouseY - centroY, mouseX - centroX);

            AffineTransform old = g2d.getTransform();

            g2d.rotate(angulo + Math.PI / 2, centroX, centroY);

            g2d.drawImage(spriteNave, naveX, naveY, anchoNave, altoNave, null);

            g2d.setTransform(old);
        }

        if (mostrandoConfirmacionExit) {
            dibujarConfirmacionExit(g2d);
        }
    }

    private void dibujarScores(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(fuenteTitulo.deriveFont(52f));
        g2d.setColor(Color.ORANGE);

        String titulo = "SCORES";
        int tituloX = (getWidth() - g2d.getFontMetrics().stringWidth(titulo)) / 2;
        g2d.drawString(titulo, tituloX, 90);

        g2d.setFont(fuenteNormal.deriveFont(26f));
        g2d.setColor(Color.WHITE);

        int y = 145;
        int pos = 1;

        for (Leaderboard.ScoreEntry score : Leaderboard.getInstancia().getScores()) {
            String linea = pos + ". " + score.name + "   " + score.score;
            int lineaX = (getWidth() - g2d.getFontMetrics().stringWidth(linea)) / 2;
            g2d.drawString(linea, lineaX, y);
            y += 38;
            pos++;
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.ORANGE);

        String volver = "PRESS SPACE / ENTER / ESC OR CLICK TO RETURN";
        int volverX = (getWidth() - g2d.getFontMetrics().stringWidth(volver)) / 2;
        g2d.drawString(volver, volverX, getHeight() - 25);
    }

    private void dibujarConfirmacionExit(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 190));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int boxW = 520;
        int boxH = 210;
        int boxX = (getWidth() - boxW) / 2;
        int boxY = (getHeight() - boxH) / 2;

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);

        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);

        g2d.setFont(fuenteNormal.deriveFont(28f));
        g2d.setColor(Color.WHITE);

        String linea1 = "Are you sure you want";
        String linea2 = "to exit the game?";

        int linea1X = boxX + (boxW - g2d.getFontMetrics().stringWidth(linea1)) / 2;
        int linea2X = boxX + (boxW - g2d.getFontMetrics().stringWidth(linea2)) / 2;

        g2d.drawString(linea1, linea1X, boxY + 75);
        g2d.drawString(linea2, linea2X, boxY + 115);

        botonYes.setBounds(boxX + 95, boxY + 140, 120, 45);
        botonNo.setBounds(boxX + 305, boxY + 140, 120, 45);

        g2d.setFont(fuenteNormal.deriveFont(24f));

        g2d.setColor(Color.ORANGE);
        String yes = "YES";
        int yesX = botonYes.x + (botonYes.width - g2d.getFontMetrics().stringWidth(yes)) / 2;
        g2d.drawString(yes, yesX, botonYes.y + 32);

        g2d.setColor(Color.WHITE);
        String no = "NO";
        int noX = botonNo.x + (botonNo.width - g2d.getFontMetrics().stringWidth(no)) / 2;
        g2d.drawString(no, noX, botonNo.y + 32);
    }

    private void dibujarLogoProgramadores(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (gifAmigos != null) {
            int anchoIntro = 450;
            int altoIntro = 350;

            int x = (getWidth() - anchoIntro) / 2;
            int y = (getHeight() - altoIntro) / 2;

            g2d.drawImage(gifAmigos, x, y, anchoIntro, altoIntro, this);

            int margenFade = 85;

            GradientPaint fadeIzquierdo = new GradientPaint(
                x, y,
                new Color(0, 0, 0, 255),
                x + margenFade, y,
                new Color(0, 0, 0, 0)
            );
            g2d.setPaint(fadeIzquierdo);
            g2d.fillRect(x, y, margenFade, altoIntro);

            GradientPaint fadeDerecho = new GradientPaint(
                x + anchoIntro - margenFade, y,
                new Color(0, 0, 0, 0),
                x + anchoIntro, y,
                new Color(0, 0, 0, 255)
            );
            g2d.setPaint(fadeDerecho);
            g2d.fillRect(x + anchoIntro - margenFade, y, margenFade, altoIntro);

            GradientPaint fadeSuperior = new GradientPaint(
                x, y,
                new Color(0, 0, 0, 255),
                x, y + margenFade,
                new Color(0, 0, 0, 0)
            );
            g2d.setPaint(fadeSuperior);
            g2d.fillRect(x, y, anchoIntro, margenFade);

            GradientPaint fadeInferior = new GradientPaint(
                x, y + altoIntro - margenFade,
                new Color(0, 0, 0, 0),
                x, y + altoIntro,
                new Color(0, 0, 0, 255)
            );
            g2d.setPaint(fadeInferior);
            g2d.fillRect(x, y + altoIntro - margenFade, anchoIntro, margenFade);
        }
    }

    private void dibujarPresentacion(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        BufferedImage imagen = presentaciones[presentacionActual];

        if (imagen != null) {
            Composite oldComposite = g2d.getComposite();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaPresentacion));
            g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), null);

            g2d.setComposite(oldComposite);
        }
    }

    private void dibujarTituloFinal(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (logo != null) {
            Composite oldComposite = g2d.getComposite();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaLogoFinal));

            int altoLogo = 180;
            int anchoLogo = (int) ((double) logo.getWidth() / logo.getHeight() * altoLogo);

            int logoX = (getWidth() - anchoLogo) / 2;
            int logoY = (getHeight() - altoLogo) / 2;

            g2d.drawImage(logo, logoX, logoY, anchoLogo, altoLogo, null);

            g2d.setComposite(oldComposite);
        }
    }

    private class Disparo {
        private double x;
        private double y;
        private double velocidadX;
        private double velocidadY;

        public Disparo(double x, double y, double angulo) {
            this.x = x;
            this.y = y;

            double velocidad = 12;
            this.velocidadX = Math.cos(angulo) * velocidad;
            this.velocidadY = Math.sin(angulo) * velocidad;
        }

        public void mover() {
            x += velocidadX;
            y += velocidadY;
        }

        public void dibujar(Graphics2D g2d) {
            g2d.setColor(Color.ORANGE);

            double angulo = Math.atan2(velocidadY, velocidadX);

            int largo = 16;

            int x2 = (int) (x - Math.cos(angulo) * largo);
            int y2 = (int) (y - Math.sin(angulo) * largo);

            Stroke oldStroke = g2d.getStroke();

            g2d.setStroke(new BasicStroke(
                6,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
            ));

            g2d.drawLine((int) x, (int) y, x2, y2);

            g2d.setStroke(oldStroke);
        }

        public boolean estaFuera(int anchoPanel, int altoPanel) {
            return x < 0 || x > anchoPanel || y < 0 || y > altoPanel;
        }
    }
}