package skydefense.engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

/**
 * Estado que maneja la cinemática de introducción al iniciar una partida.
 * Reproduce las diapositivas de la historia, el logo final y permite saltar al juego.
 */
public class PantallaPresentacion implements EstadoPantalla {

    private Menu menuContexto;
    private GestorRecursos recursos;

    // Control de flujo de la cinemática
    private boolean mostrandoTituloFinal = false;

    // Variables de las Diapositivas (Historia)
    private int presentacionActual = 0;
    private float alphaPresentacion = 1f;
    private boolean cambiandoDiapositiva = false;
    private Timer timerPresentacion;

    // Variables del Título Final
    private float alphaLogoFinal = 0f;
    private Timer timerLogo;
    private Timer esperaLogo;

    public PantallaPresentacion(Menu menuContexto) {
        this.menuContexto = menuContexto;
        this.recursos = GestorRecursos.getInstancia();

        iniciarPresentacion();
    }

    private void iniciarPresentacion() {
        presentacionActual = 0;
        alphaPresentacion = 1f;
        cambiandoDiapositiva = false;
        mostrandoTituloFinal = false;

        GestorAudio.getInstancia().reproducirMusicaPresentacion();

        // Temporizador original de 5000ms (5 segundos por imagen)
        timerPresentacion = new Timer(5000, e -> iniciarCambioDiapositiva());
        timerPresentacion.start();
    }

    private void iniciarCambioDiapositiva() {
        if (cambiandoDiapositiva) return;
        cambiandoDiapositiva = true;

        // Fade-Out original: 25ms, restando 0.12f
        Timer fadeOut = new Timer(25, null);
        fadeOut.addActionListener(e -> {
            alphaPresentacion -= 0.12f;

            if (alphaPresentacion <= 0f) {
                alphaPresentacion = 0f;
                fadeOut.stop();

                presentacionActual++;

                // Si ya pasamos las 4 imágenes, pasamos al título final
                if (presentacionActual >= recursos.getCantidadPresentaciones()) {
                    if (timerPresentacion != null) {
                        timerPresentacion.stop();
                    }
                    iniciarTituloFinal();
                    return;
                }

                // Fade-In original: 25ms, sumando 0.12f
                Timer fadeIn = new Timer(25, null);
                fadeIn.addActionListener(ev -> {
                    alphaPresentacion += 0.12f;

                    if (alphaPresentacion >= 1f) {
                        alphaPresentacion = 1f;
                        cambiandoDiapositiva = false;
                        fadeIn.stop();
                    }
                    menuContexto.repaint();
                });
                fadeIn.start();
            }
            menuContexto.repaint();
        });

        fadeOut.start();
    }

    private void iniciarTituloFinal() {
        mostrandoTituloFinal = true;
        alphaLogoFinal = 0f;

        // Fade-In del Logo original: 50ms, sumando 0.03f
        timerLogo = new Timer(50, e -> {
            alphaLogoFinal += 0.03f;

            if (alphaLogoFinal >= 1f) {
                alphaLogoFinal = 1f;
                timerLogo.stop();

                // Espera final original de 1.5 segundos
                esperaLogo = new Timer(1500, ev -> {
                    esperaLogo.stop();
                    saltarAlJuego();
                });
                esperaLogo.setRepeats(false);
                esperaLogo.start();
            }
            menuContexto.repaint();
        });

        timerLogo.start();
    }

    private void saltarAlJuego() {
        // Detener limpiamente todos los hilos y temporizadores para evitar fugas de memoria
        if (timerPresentacion != null) timerPresentacion.stop();
        if (timerLogo != null) timerLogo.stop();
        if (esperaLogo != null) esperaLogo.stop();

        // El Contexto se encarga de instanciar el PanelJuego y apagar la música
        menuContexto.iniciarJuego();
    }

    @Override
    public void actualizar() {
        // La actualización de físicas es manejada asíncronamente por los Timers internos
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, menuContexto.getWidth(), menuContexto.getHeight());

        if (!mostrandoTituloFinal) {
            BufferedImage imagen = recursos.getPresentacion(presentacionActual);
            if (imagen != null) {
                Composite oldComposite = g2d.getComposite();

                // Aseguramos que el alpha no rompa los límites de Float (0.0 a 1.0)
                float alphaSeguro = Math.max(0f, Math.min(1f, alphaPresentacion));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaSeguro));
                
                g2d.drawImage(imagen, 0, 0, menuContexto.getWidth(), menuContexto.getHeight(), null);
                g2d.setComposite(oldComposite);
            }
        } else {
            BufferedImage logo = recursos.getLogo();
            if (logo != null) {
                Composite oldComposite = g2d.getComposite();
                
                float alphaSeguro = Math.max(0f, Math.min(1f, alphaLogoFinal));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaSeguro));

                int altoLogo = 180;
                int anchoLogo = (int) ((double) logo.getWidth() / logo.getHeight() * altoLogo);
                int logoX = (menuContexto.getWidth() - anchoLogo) / 2;
                int logoY = (menuContexto.getHeight() - altoLogo) / 2;

                g2d.drawImage(logo, logoX, logoY, anchoLogo, altoLogo, null);
                g2d.setComposite(oldComposite);
            }
        }
    }

    @Override
    public void teclaPresionada(KeyEvent e) {
        // Atajo para saltar la cinemática
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            saltarAlJuego();
        }
    }

    @Override
    public void clickRaton(MouseEvent e) {
        // La cinemática original no interactúa con clics de ratón
    }

    @Override
    public void mouseMovido(MouseEvent e) {
        // No se rastrea hover en esta pantalla
    }
}