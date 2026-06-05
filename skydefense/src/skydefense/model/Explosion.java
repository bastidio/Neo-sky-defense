package skydefense.model;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Explosion extends ObjetoVolador {

    private Image gifExplosion;
    private double duracion = 0.8;
    private double tiempoActual = 0;

    public Explosion(double x, double y, String rutaGif) {
        this.posicionX = x;
        this.posicionY = y;
        this.gifExplosion = new ImageIcon(rutaGif).getImage();
    }

    @Override
    public void update(double delta) {
        tiempoActual += delta;

        if (tiempoActual >= duracion) {
            activo = false;
        }
    }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        int ancho = 90;
        int alto = 90;

        if (gifExplosion != null) {
            g2d.drawImage(gifExplosion, (int) posicionX - ancho / 2, (int) posicionY - alto / 2, ancho, alto, null);
        }
    }
}