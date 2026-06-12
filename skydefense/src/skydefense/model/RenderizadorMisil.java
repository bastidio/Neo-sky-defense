package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class RenderizadorMisil {

    public void draw(Graphics2D g2d, Misil misil, int altoPantalla) {
        int ancho = misil.getAncho();
        int alto = misil.getAlto();

        if (misil.getSprite() != null) {
            g2d.drawImage(
                misil.getSprite(),
                (int) misil.getPosicionX() - ancho / 2,
                (int) misil.getPosicionY() - alto / 2,
                ancho,
                alto,
                null
            );
        }
    }
}