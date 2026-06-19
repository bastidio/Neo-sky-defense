package skydefense.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Disparo {
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