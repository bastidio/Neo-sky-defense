package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.Rectangle;

public class Dron extends ObjetoVolador {

    private int direccion;
    private int misilesRestantes = 3;
    private int altitud = 5000;

    private double tiempoHastaProximoDisparo;
    private double velocidadPropia;

    private BufferedImage spriteDron;
    private BufferedImage spriteMisil;

    private Random random = new Random();

    public Dron(int anchoPantalla, BufferedImage spriteDron, BufferedImage spriteMisil, double velocidadBase) {
        this.spriteDron = spriteDron;
        this.spriteMisil = spriteMisil;

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
    public Rectangle getHitbox() {
        int ancho = 75;
        int alto = 75;
        return new Rectangle((int) posicionX - ancho / 2, (int) posicionY - alto / 2, ancho, alto);
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
        return new Misil(posicionX, altitud - 250, velocidadMisil, spriteMisil);
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

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        int ancho = 75;
        int alto = 75;

        posicionY = 75;

        if (spriteDron != null) {
            g2d.drawImage(spriteDron, (int) posicionX - ancho / 2, (int) posicionY - alto / 2, ancho, alto, null);
        }
    }
}
