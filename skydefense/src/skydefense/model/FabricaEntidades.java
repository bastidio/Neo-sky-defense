package skydefense.model;

import java.awt.image.BufferedImage;

public class FabricaEntidades {

    private BufferedImage spriteDron;
    private BufferedImage spriteMisil;

    public FabricaEntidades(BufferedImage spriteDron, BufferedImage spriteMisil) {
        this.spriteDron = spriteDron;
        this.spriteMisil = spriteMisil;
    }

    public Dron crearDron(int anchoPantalla, double velocidadBase) {
        return new Dron(anchoPantalla, spriteDron, this, velocidadBase);
    }

    public Misil crearMisil(double posicionX, int altitudInicial, double velocidadMisil) {
        return new Misil(posicionX, altitudInicial, velocidadMisil, spriteMisil);
    }
}