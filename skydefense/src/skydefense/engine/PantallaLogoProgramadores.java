package skydefense.engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Timer;


public class PantallaLogoProgramadores implements EstadoPantalla {

    private Menu menuContexto;
    private GestorRecursos recursos;
    private GestorAudio audio;

    private float alphaLogoProgramadores = 0f;
    private Timer timerLogoProgramadores;

    public PantallaLogoProgramadores(Menu menuContexto) {
        this.menuContexto = menuContexto;
        this.recursos = GestorRecursos.getInstancia();
        this.audio = GestorAudio.getInstancia();

        iniciarAnimacion();
    }

    private void iniciarAnimacion() {
        alphaLogoProgramadores = 0f;
        audio.reproducirMusicaLogoProgramadores();

        // Respetamos estrictamente el temporizador de 50ms original
        timerLogoProgramadores = new Timer(50, e -> {
            alphaLogoProgramadores += 0.03f;

            if (alphaLogoProgramadores >= 1f) {
                alphaLogoProgramadores = 1f;
                timerLogoProgramadores.stop();

                // Temporizador de espera de 4.5 segundos original
                Timer espera = new Timer(4500, ev -> {
                    ((Timer) ev.getSource()).stop();
                    audio.detenerMusicaLogoProgramadores();
                    
                    // Transición automática hacia el menú principal
                    audio.reproducirMusicaLogo();
                    menuContexto.cambiarPantalla(new PantallaPrincipal(menuContexto));
                });

                espera.setRepeats(false);
                espera.start();
            }
        });

        timerLogoProgramadores.start();
    }

    @Override
    public void actualizar() {
        // La actualización lógica se maneja a través del Timer interno de la clase,
        // tal como estaba diseñado en la arquitectura original.
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        int anchoPantalla = menuContexto.getWidth();
        int altoPantalla = menuContexto.getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, anchoPantalla, altoPantalla);

        Image gifAmigos = recursos.getGifAmigos();
        if (gifAmigos != null) {
            int anchoIntro = 450;
            int altoIntro = 350;

            int x = (anchoPantalla - anchoIntro) / 2;
            int y = (altoPantalla - altoIntro) / 2;

            // Al dibujar un GIF animado en Java, usamos el propio Menu como ImageObserver
            g2d.drawImage(gifAmigos, x, y, anchoIntro, altoIntro, menuContexto);

            int margenFade = 85;

            // Degradado Izquierdo
            GradientPaint fadeIzquierdo = new GradientPaint(
                x, y,
                new Color(0, 0, 0, 255),
                x + margenFade, y,
                new Color(0, 0, 0, 0)
            );
            g2d.setPaint(fadeIzquierdo);
            g2d.fillRect(x, y, margenFade, altoIntro);

            // Degradado Derecho
            GradientPaint fadeDerecho = new GradientPaint(
                x + anchoIntro - margenFade, y,
                new Color(0, 0, 0, 0),
                x + anchoIntro, y,
                new Color(0, 0, 0, 255)
            );
            g2d.setPaint(fadeDerecho);
            g2d.fillRect(x + anchoIntro - margenFade, y, margenFade, altoIntro);

            // Degradado Superior
            GradientPaint fadeSuperior = new GradientPaint(
                x, y,
                new Color(0, 0, 0, 255),
                x, y + margenFade,
                new Color(0, 0, 0, 0)
            );
            g2d.setPaint(fadeSuperior);
            g2d.fillRect(x, y, anchoIntro, margenFade);

            // Degradado Inferior
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

    @Override
    public void teclaPresionada(KeyEvent e) {
        
    }

    @Override
    public void clickRaton(MouseEvent e) {
       
    }

    @Override
    public void mouseMovido(MouseEvent e) {
    }
}