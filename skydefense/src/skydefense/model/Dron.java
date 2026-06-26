package skydefense.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Dron extends ObjetoVolador {

    private int direccion;
    private int misilesRestantes = 3;
    private int altitud = 5000;

    private double tiempoHastaProximoDisparo;
    private double velocidadPropia;

    private BufferedImage spriteDron;
    private FabricaEntidades fabricaEntidades; // Fábrica nueva

    private Random random = new Random();
    private RenderizadorDron renderizador;

    public Dron(int anchoPantalla, BufferedImage spriteDron, FabricaEntidades fabricaEntidades, double velocidadBase) {
        this.spriteDron = spriteDron;
        this.fabricaEntidades = fabricaEntidades;
        this.renderizador = new RenderizadorDron();
        this.alto = 75;
        this.ancho = 75;

        this.velocidadPropia = velocidadBase * (0.80 + random.nextDouble() * 0.60);

        if (random.nextBoolean()) {
            posicionX = -80;
            direccion = 1;
        } else {
            posicionX = anchoPantalla + 80;
            direccion = -1;
        }

        this.tiempoHastaProximoDisparo = 0.6 + random.nextDouble() * 2.2;
    }

    @Override
    public void update(double delta) {
        posicionX += direccion * velocidadPropia * delta;
    }

    public Misil intentarDisparar(double delta, double frecuenciaBase, double velocidadMisil) {
        if (misilesRestantes <= 0) return null;

        tiempoHastaProximoDisparo -= delta;

        if (tiempoHastaProximoDisparo <= 0) {
            misilesRestantes--;
            tiempoHastaProximoDisparo = frecuenciaBase * (0.65 + random.nextDouble() * 1.10);
            return lanzarMisil(velocidadMisil);
        }
        return null;
    }

    private Misil lanzarMisil(double velocidadMisil) {
        return fabricaEntidades.crearMisil(posicionX, altitud - 250, velocidadMisil);
    }

    public void verificarSalida(int anchoPantalla) {
        if (misilesRestantes <= 0) {
            if (direccion == 1 && posicionX > anchoPantalla + 100) activo = false;
            if (direccion == -1 && posicionX < -100) activo = false;
        }
    }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        posicionY = 75;
        renderizador.draw(g2d, this);
    }

    public BufferedImage getSpriteDron() { return spriteDron; }
}