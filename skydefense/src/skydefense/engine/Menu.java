package skydefense.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends JPanel {

    private EstadoPantalla estadoActual;
    private Timer timerMenu;
    
    private boolean pantallaCompleta = false;
    private Rectangle boundsVentana;

    public Menu() {
        this(false);
    }

    public Menu(boolean mostrarLogoInicial) {
        setBackground(Color.BLACK);
        setFocusable(true);

        // Define con qué pantalla arranca el juego
        if (mostrarLogoInicial) {
            cambiarPantalla(new PantallaLogoProgramadores(this));
        } else {
            cambiarPantalla(new PantallaPrincipal(this));
        }

        configurarEventosPerifericos();
        iniciarBucleMenu();
    }

    public void cambiarPantalla(EstadoPantalla nuevoEstado) {
        if (nuevoEstado != null) {
            this.estadoActual = nuevoEstado;
            repaint();
        }
    }

    private void iniciarBucleMenu() {
        timerMenu = new Timer(16, e -> {
            if (estadoActual != null) {
                estadoActual.actualizar();
            }
            repaint();
        });
        timerMenu.start();
    }

    private void configurarEventosPerifericos() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (estadoActual != null) estadoActual.teclaPresionada(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (estadoActual != null) estadoActual.clickRaton(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (estadoActual != null) estadoActual.mouseMovido(e);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (estadoActual != null) {
            estadoActual.dibujar(g2d);
        }
    }

    // --- Métodos públicos que usan las pantallas (Fullscreen y cambio al juego) ---

    public boolean isPantallaCompleta() {
        return pantallaCompleta;
    }

    public void alternarPantallaCompleta() {
        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (ventana == null) return;

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

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

    public void iniciarJuego() {
        if (timerMenu != null) timerMenu.stop();
        GestorAudio.getInstancia().detenerTodo();

        JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (ventana != null) {
            PanelJuego panelJuego = new PanelJuego(ventana);
            ventana.setContentPane(panelJuego);
            ventana.revalidate();
            ventana.repaint();
            panelJuego.requestFocusInWindow();
        }
    }
}
