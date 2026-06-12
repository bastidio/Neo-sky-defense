package skydefense.model;

import java.awt.image.BufferedImage;

public class FabricaMisil {

    private BufferedImage spriteMisil;

    public FabricaMisil(BufferedImage spriteMisil) {
        this.spriteMisil = spriteMisil;
    }

    public Misil crearMisil(double posicionX, int altitudInicial, double velocidadMisil) {
        return new Misil(posicionX, altitudInicial, velocidadMisil, spriteMisil);
    }
}