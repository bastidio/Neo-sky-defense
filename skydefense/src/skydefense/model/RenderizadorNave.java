package skydefense.model;

import java.awt.Graphics2D;

public class RenderizadorNave {

    public void draw(Graphics2D g2d, Nave nave) {
        if (nave.getSprite() != null) {
            g2d.drawImage(
                nave.getSprite(),
                (int) nave.getPosicionX() - nave.getAncho() / 2,
                (int) nave.getPosicionY() - nave.getAlto() / 2,
                nave.getAncho(),
                nave.getAlto(),
                null
            );
        }
    }
}