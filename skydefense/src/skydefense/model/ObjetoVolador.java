package skydefense.model;

import java.awt.Graphics2D;

public abstract class ObjetoVolador {

    protected double posicionX;
    protected double posicionY;
    protected boolean activo = true;

    public abstract void update(double delta);

    public abstract void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla);

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