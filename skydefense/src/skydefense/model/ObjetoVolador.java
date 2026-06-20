package skydefense.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class ObjetoVolador {

    protected double posicionX;
    protected double posicionY;
    protected boolean activo = true;
    protected int ancho;
    protected int alto;

    public abstract void update(double delta);

    public abstract void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla);

    public Rectangle getHitbox() {
        return new Rectangle(
            (int) posicionX - ancho / 2,
            (int) posicionY - alto / 2,
            ancho,
            alto
        );
    }

    public boolean estaActivo() {
        return activo;
    }

    public double getPosicionX() {
        return posicionX;
    }

    public double getPosicionY() {
        return posicionY;
    }

    public void desactivar() {
        activo = false;
    }
}