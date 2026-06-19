package skydefense.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanelJuego extends JPanel {

    private JFrame ventana;
    private MotorJuego motor;
    private RenderizadorJuego renderizador;
    private ControladorInputJuego input;
    
    private Timer gameLoop;
    private long ultimoTiempo;

    private boolean pantallaCompleta = false;
    private Rectangle boundsVentana;

    public PanelJuego(JFrame ventana) {
        this.ventana = ventana;

        setBackground(Color.BLACK);
        setFocusable(true);

        motor = new MotorJuego(this);
        renderizador = new RenderizadorJuego();
        input = new ControladorInputJuego(this, motor, renderizador);

        // Agregamos los listeners del ControladorInputJuego
        addMouseListener(input);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { input.keyPressed(e); }
            @Override
            public void keyReleased(KeyEvent e) { input.keyReleased(e); }
        });

        GestorAudio.getInstancia().reproducirMusicaJuego();
        iniciarLoop();
    }

    private void iniciarLoop() {
        ultimoTiempo = System.nanoTime();

        gameLoop = new Timer(16, e -> {
            long ahora = System.nanoTime();
            double delta = (ahora - ultimoTiempo) / 1_000_000_000.0;
            ultimoTiempo = ahora;

            motor.update(delta, getWidth());
            repaint();
        });

        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Delegamos todo el dibujo al renderizador
        renderizador.dibujar(g2d, getWidth(), getHeight(), motor);
    }

    public void volverAlMenu() {
        if (gameLoop != null) gameLoop.stop();
        GestorAudio.getInstancia().detenerMusicaJuego();

        Menu menu = new Menu(false);
        ventana.setContentPane(menu);
        ventana.revalidate();
        ventana.repaint();
        menu.requestFocusInWindow();
    }

    public void alternarPantallaCompleta() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

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
            if (boundsVentana != null) ventana.setBounds(boundsVentana);
            else {
                ventana.setSize(800, 600);
                ventana.setLocationRelativeTo(null);
            }
            ventana.setVisible(true);
            pantallaCompleta = false;
        }
        requestFocusInWindow();
    }
}