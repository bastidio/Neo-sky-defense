package skydefense.engine;

import skydefense.model.Escuadron;
import skydefense.model.Explosion;
import skydefense.model.Jugador;
import skydefense.model.Leaderboard;
import skydefense.model.Misil;
import skydefense.model.Nave;
import skydefense.model.Nivel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private JFrame ventana;

    private Nave nave;
    private Jugador jugador;
    private Nivel nivel;
    private Escuadron escuadron;

    private ArrayList<Misil> misiles = new ArrayList<>();
    private ArrayList<Explosion> explosiones = new ArrayList<>();

    private BufferedImage spriteNave;
    private BufferedImage spriteDron;
    private BufferedImage spriteMisil;
    private BufferedImage logo;

    private Timer gameLoop;
    private long ultimoTiempo;

    private boolean juegoTerminado = false;
    private boolean victoria = false;
    private boolean scoreGuardado = false;

    private boolean pantallaCompleta = false;
    private Rectangle boundsVentana;

    private Rectangle botonVolverMenu = new Rectangle(250, 420, 300, 55);

    public GamePanel(JFrame ventana) {
        this.ventana = ventana;

        setBackground(Color.BLACK);
        setFocusable(true);

        cargarImagenes();
        init();
        configurarTeclado();
        configurarMouse();
        iniciarLoop();
    }

    private void cargarImagenes() {
        try {
            spriteNave = ImageIO.read(new File("skydefense/res/sprite/nave.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar nave.png");
        }

        try {
            spriteDron = ImageIO.read(new File("skydefense/res/sprite/dron.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar dron.png");
        }

        try {
            spriteMisil = ImageIO.read(new File("skydefense/res/sprite/misil.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar misil.png");
        }

        try {
            logo = ImageIO.read(new File("skydefense/res/sprite/logo.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar logo.png");
        }
    }

    private void init() {
        jugador = new Jugador();
        nivel = new Nivel();

        nave = new Nave(spriteNave, 800, 600);
        escuadron = new Escuadron(spriteDron, spriteMisil);

        misiles.clear();
        explosiones.clear();

        juegoTerminado = false;
        victoria = false;
        scoreGuardado = false;
    }

    private void iniciarLoop() {
        ultimoTiempo = System.nanoTime();

        gameLoop = new Timer(16, e -> {
            long ahora = System.nanoTime();
            double delta = (ahora - ultimoTiempo) / 1_000_000_000.0;
            ultimoTiempo = ahora;

            update(delta);
            repaint();
        });

        gameLoop.start();
    }

    private void update(double delta) {
        if (juegoTerminado) {
            return;
        }

        nave.update(delta);
        nave.limitarPantalla(getWidth());

        ArrayList<Misil> nuevosMisiles = escuadron.update(delta, nivel, getWidth());
        misiles.addAll(nuevosMisiles);

        for (int i = misiles.size() - 1; i >= 0; i--) {
            Misil misil = misiles.get(i);
            misil.update(delta);

            if (!misil.estaActivo()) {
                if (misil.fueDetonado()) {
                    procesarExplosion(misil);
                }

                misiles.remove(i);
            }
        }

        for (int i = explosiones.size() - 1; i >= 0; i--) {
            Explosion explosion = explosiones.get(i);
            explosion.update(delta);

            if (!explosion.estaActivo()) {
                explosiones.remove(i);
            }
        }
        Rectangle hitboxNave = nave.getHitbox();

        // 1. Colisiones con Misiles
        for (int i = misiles.size() - 1; i >= 0; i--) {
            Misil misil = misiles.get(i);
            
            // Verificamos si la caja del misil se superpone con la caja de la nave
            if (misil.estaActivo() && hitboxNave.intersects(misil.getHitbox())) {
                // Choque directo: Daño grave y destrucción del proyectil
                nave.reducirEnergia(40); 
                explosiones.add(new Explosion(misil.getPosicionX(), misil.getPosicionY(), "skydefense/res/sprite/transparent-Photoroom (3) - copia.png", "skydefense/res/sprite/transparent-Photoroom (3) - copia.json"));
                misiles.remove(i);

                // Evaluar estado del jugador tras el impacto directo
                if (nave.energiaAgotada()) {
                    jugador.perderVida();
                    nave.restaurarEnergia();
                }
                if (jugador.estaMuerto()) {
                    terminarJuego(false);
                }
            }
        }

        if (escuadron.nivelTerminado() && misiles.isEmpty() && explosiones.isEmpty()) {
            jugador.sumarPuntos(300);

            if (nivel.esUltimoNivel()) {
                terminarJuego(true);
            } else {
                nivel.aplicarIncrementoDificultad();
                escuadron = new Escuadron(spriteDron, spriteMisil);
            }
        }
    }

    private void procesarExplosion(Misil misil) {
        double xExplosion = misil.getPosicionX();
        double yExplosion = misil.getPosicionY();

        explosiones.add(new Explosion(xExplosion, yExplosion, "skydefense/res/sprite/transparent-Photoroom (3) - copia.png", "skydefense/res/sprite/transparent-Photoroom (3) - copia.json"));

        double distancia = misil.calcularDistancia(nave.getPosicionX(), nave.getAltitud());

        if (distancia > 150) {
            jugador.sumarPuntos(40);
        } else if (distancia >= 80 && distancia <= 150) {
            jugador.sumarPuntos(20);
            nave.reducirEnergia(20);
        } else if (distancia >= 20 && distancia < 80) {
            nave.reducirEnergia(40);
        } else {
            jugador.perderVida();
        }

        if (nave.energiaAgotada()) {
            jugador.perderVida();
            nave.restaurarEnergia();
        }

        if (jugador.estaMuerto()) {
            terminarJuego(false);
        }
    }

    private void terminarJuego(boolean victoria) {
        this.juegoTerminado = true;
        this.victoria = victoria;

        if (gameLoop != null) {
            gameLoop.stop();
        }

        guardarScoreSiCorresponde();

        repaint();
    }

    private void guardarScoreSiCorresponde() {
        if (scoreGuardado) {
            return;
        }

        scoreGuardado = true;

        String nombre = JOptionPane.showInputDialog(this, "ENTER YOUR NAME (3 LETTERS):", "AAA");

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "AAA";
        }

        nombre = nombre.trim().toUpperCase();

        if (nombre.length() > 3) {
            nombre = nombre.substring(0, 3);
        }

        while (nombre.length() < 3) {
            nombre += "A";
        }

        Leaderboard.getInstancia().agregarScore(nombre, jugador.getPuntos());
    }

    private void volverAlMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        Menu menu = new Menu(false);
        ventana.setContentPane(menu);
        ventana.revalidate();
        ventana.repaint();
        menu.requestFocusInWindow();
    }

    private void configurarMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();

                if (juegoTerminado && botonVolverMenu.contains(e.getPoint())) {
                    volverAlMenu();
                }
            }
        });
    }

    private void configurarTeclado() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int tecla = e.getKeyCode();

                if (juegoTerminado && tecla == KeyEvent.VK_SPACE) {
                    volverAlMenu();
                    return;
                }

                if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) {
                    nave.setIzquierda(true);
                }

                if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) {
                    nave.setDerecha(true);
                }

                if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) {
                    nave.setSubir(true);
                }

                if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) {
                    nave.setBajar(true);
                }

                if (tecla == KeyEvent.VK_F11) {
                    alternarPantallaCompleta();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int tecla = e.getKeyCode();

                if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) {
                    nave.setIzquierda(false);
                }

                if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) {
                    nave.setDerecha(false);
                }

                if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) {
                    nave.setSubir(false);
                }

                if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) {
                    nave.setBajar(false);
                }
            }
        });
    }

    private void alternarPantallaCompleta() {
        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();

        if (!pantallaCompleta) {
            boundsVentana = ventana.getBounds();

            ventana.dispose();
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

            ventana.setVisible(true);
            pantallaCompleta = false;
        }

        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        draw(g2d);
    }

    private void draw(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (juegoTerminado) {
            dibujarFinJuego(g2d);
            return;
        }

        dibujarFondo(g2d);

        nave.draw(g2d, getWidth(), getHeight());
        escuadron.draw(g2d, getWidth(), getHeight());

        for (Misil misil : misiles) {
            misil.draw(g2d, getWidth(), getHeight());
        }

        for (Explosion explosion : explosiones) {
            explosion.draw(g2d, getWidth(), getHeight());
        }

        dibujarHUD(g2d);
    }

    private void dibujarFondo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(20, 20, 35));
        for (int i = 0; i < getHeight(); i += 40) {
            g2d.drawLine(0, i, getWidth(), i);
        }
    }

    private void dibujarHUD(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);

        g2d.drawString("LIVES: " + jugador.getVidas(), 20, 30);
        g2d.drawString("SCORE: " + jugador.getPuntos(), 20, 55);
        g2d.drawString("ENERGY: " + nave.getEnergia(), 20, 80);
        g2d.drawString("DRONES REMAINING: " + escuadron.getDronesRestantes(), 20, 105);
        g2d.drawString("LEVEL: " + nivel.getNumero(), 20, 130);
    }

    private void dibujarFinJuego(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (victoria && logo != null) {
            int altoLogo = 180;
            int anchoLogo = (int) ((double) logo.getWidth() / logo.getHeight() * altoLogo);

            int x = (getWidth() - anchoLogo) / 2;
            int y = 80;

            g2d.drawImage(logo, x, y, anchoLogo, altoLogo, null);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.WHITE);

        String texto = victoria ? "CONGRATULATIONS" : "GAME OVER";
        int anchoTexto = g2d.getFontMetrics().stringWidth(texto);
        g2d.drawString(texto, (getWidth() - anchoTexto) / 2, getHeight() / 2 + 20);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreTexto = "FINAL SCORE: " + jugador.getPuntos();
        int anchoScore = g2d.getFontMetrics().stringWidth(scoreTexto);
        g2d.drawString(scoreTexto, (getWidth() - anchoScore) / 2, getHeight() / 2 + 65);

        dibujarBotonVolverMenu(g2d);
    }

    private void dibujarBotonVolverMenu(Graphics2D g2d) {
        int x = getWidth() / 2 - 150;
        int y = getHeight() / 2 + 110;
        int ancho = 300;
        int alto = 55;

        botonVolverMenu.setBounds(x, y, ancho, alto);

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(x, y, ancho, alto, 20, 20);

        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, ancho, alto, 20, 20);

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);

        String texto = "BACK TO MENU";
        int anchoTexto = g2d.getFontMetrics().stringWidth(texto);
        g2d.drawString(texto, x + (ancho - anchoTexto) / 2, y + 36);
    }
}
