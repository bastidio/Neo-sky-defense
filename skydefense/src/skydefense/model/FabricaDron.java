package skydefense.model;

import java.awt.image.BufferedImage;

public class FabricaDron {

    private BufferedImage spriteDron;
    private FabricaMisil fabricaMisil;

    public FabricaDron(BufferedImage spriteDron, FabricaMisil fabricaMisil) {
        this.spriteDron = spriteDron;
        this.fabricaMisil = fabricaMisil;
    }

    public Dron crearDron(int anchoPantalla, double velocidadBase) {
        return new Dron(anchoPantalla, spriteDron, fabricaMisil, velocidadBase);
    }
}