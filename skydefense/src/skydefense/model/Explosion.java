package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Explosion {

    private double x;
    private double y;

    private AnimacionExplosion animacion;

    private int frameActual = 0;

    private double tiempoFrame = 0.05;
    private double acumulador = 0;

    private boolean activa = true;

    public Explosion(
            double x,
            double y,
            AnimacionExplosion animacion) {

        this.x = x;
        this.y = y;
        this.animacion = animacion;
    }

    public void update(double delta) {

        if (!activa) {
            return;
        }

        acumulador += delta;

        if (acumulador >= tiempoFrame) {

            acumulador = 0;
            frameActual++;

            if (frameActual >= animacion.getCantidadFrames()) {
                activa = false;
            }
        }
    }

    public void draw(Graphics2D g2d) {

        if (!activa) {
            return;
        }

        BufferedImage frame =
            animacion.getFrame(frameActual);

        if (frame == null) {
            return;
        }

        int ancho = frame.getWidth();
        int alto = frame.getHeight();

        g2d.drawImage(
            frame,
            (int) x - ancho / 2,
            (int) y - alto / 2,
            null
        );
    }

    public boolean estaActiva() {
        return activa;
    }
}