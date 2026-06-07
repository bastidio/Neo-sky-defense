package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;


public class Nave extends ObjetoVolador {

    private int energia;
    private int altitud;
    private BufferedImage sprite;

    private boolean izquierda;
    private boolean derecha;
    private boolean subir;
    private boolean bajar;

    private double velocidadMovimiento = 280;
    private double tiempoCambioAltitud = 0;

    public Nave(BufferedImage sprite, int anchoPantalla, int altoPantalla) {
        this.sprite = sprite;
        this.posicionX = anchoPantalla / 2.0;
        this.posicionY = altoPantalla / 2.0 + 80;
        this.altitud = 3000;
        this.energia = 100;
    }

    @Override
    public void update(double delta) {
        if (izquierda) {
            posicionX -= velocidadMovimiento * delta;
        }

        if (derecha) {
            posicionX += velocidadMovimiento * delta;
        }

        tiempoCambioAltitud += delta;

        if (tiempoCambioAltitud >= 0.12) {
            if (subir) {
                cambiarAltitud(altitud + 500);
                tiempoCambioAltitud = 0;
            } else if (bajar) {
                cambiarAltitud(altitud - 500);
                tiempoCambioAltitud = 0;
            }
        }
    }

    public void limitarPantalla(int anchoPantalla) {
        if (posicionX < 50) {
            posicionX = 50;
        }

        if (posicionX > anchoPantalla - 50) {
            posicionX = anchoPantalla - 50;
        }
    }

    public void cambiarAltitud(int nuevaAltitud) {
        if (nuevaAltitud >= 1000 && nuevaAltitud <= 5000) {
            this.altitud = nuevaAltitud;
        }
    }

    public void reducirEnergia(int cantidadDanio) {
        energia -= cantidadDanio;
    }

    public boolean energiaAgotada() {
        return energia <= 0;
    }

    public void restaurarEnergia() {
        energia = 100;
    }

    public int getEnergia() {
        return energia;
    }

    public int getAltitud() {
        return altitud;
    }
    public Rectangle getHitbox() {
        int ancho = 90;
        int alto = 90;
        return new Rectangle((int) posicionX - ancho / 2, (int) posicionY - alto / 2, ancho, alto);
    }

    public void setIzquierda(boolean izquierda) {
        this.izquierda = izquierda;
    }

    public void setDerecha(boolean derecha) {
        this.derecha = derecha;
    }

    public void setSubir(boolean subir) {
        this.subir = subir;
    }

    public void setBajar(boolean bajar) {
        this.bajar = bajar;
    }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        int ancho = 90;
        int alto = 90;

        posicionY = altoPantalla / 2.0 + 80;

        if (sprite != null) {
            g2d.drawImage(sprite, (int) posicionX - ancho / 2, (int) posicionY - alto / 2, ancho, alto, null);
        }
    }
}
