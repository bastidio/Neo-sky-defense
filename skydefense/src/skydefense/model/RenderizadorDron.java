package skydefense.model;

import java.awt.Graphics2D;

public class RenderizadorDron {

    public void draw(Graphics2D g2d, Dron dron) {
        if (dron.getSpriteDron() != null) {
            g2d.drawImage(
                dron.getSpriteDron(),
                (int) dron.getPosicionX() - dron.getAncho() / 2,
                (int) dron.getPosicionY() - dron.getAlto() / 2,
                dron.getAncho(),
                dron.getAlto(),
                null
            );
        }
    }
}