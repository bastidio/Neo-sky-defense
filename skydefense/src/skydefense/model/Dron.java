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
    private FabricaMisil fabricaMisil;

    private Random random = new Random();

    private RenderizadorDron renderizador;

    private final int ancho = 75;
    private final int alto = 75;

    public Dron(int anchoPantalla, BufferedImage spriteDron, FabricaMisil fabricaMisil, double velocidadBase) {
        this.spriteDron = spriteDron;
        this.fabricaMisil = fabricaMisil;
        this.renderizador = new RenderizadorDron();

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
        if (misilesRestantes <= 0) {
            return null;
        }

        tiempoHastaProximoDisparo -= delta;

        if (tiempoHastaProximoDisparo <= 0) {
            misilesRestantes--;
            tiempoHastaProximoDisparo = frecuenciaBase * (0.65 + random.nextDouble() * 1.10);
            return lanzarMisil(velocidadMisil);
        }

        return null;
    }

    private Misil lanzarMisil(double velocidadMisil) {
        return fabricaMisil.crearMisil(posicionX, altitud - 250, velocidadMisil);
    }

    public void verificarSalida(int anchoPantalla) {
        if (misilesRestantes <= 0) {
            if (direccion == 1 && posicionX > anchoPantalla + 100) {
                activo = false;
            }

            if (direccion == -1 && posicionX < -100) {
                activo = false;
            }
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(
            (int) posicionX - ancho / 2,
            (int) posicionY - alto / 2,
            ancho,
            alto
        );
    }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        posicionY = 75;
        renderizador.draw(g2d, this);
    }

    public BufferedImage getSpriteDron() {
        return spriteDron;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}