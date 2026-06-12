package skydefense.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Misil extends ObjetoVolador {

    private int altitudActual;
    private int altitudDetonacion;
    private boolean explota;
    private boolean detonado = false;

    private double velocidadCaida;
    private BufferedImage sprite;

    private RenderizadorMisil renderer;

    private final int ancho = 28;
    private final int alto = 58;

    public Misil(double posicionX, int altitudInicial, double velocidadCaida, BufferedImage sprite) {
        this.posicionX = posicionX;
        this.altitudActual = altitudInicial;
        this.velocidadCaida = velocidadCaida;
        this.sprite = sprite;
        this.renderer = new RenderizadorMisil();

        Random random = new Random();
        this.explota = random.nextDouble() < 0.80;
        this.altitudDetonacion = 1200 + random.nextInt(3301);
    }

    @Override
    public void update(double delta) {
        altitudActual -= velocidadCaida * delta;

        if (explota && altitudActual <= altitudDetonacion) {
            detonado = true;
            activo = false;
        }

        if (altitudActual < 500) {
            activo = false;
        }
    }

    public boolean fueDetonado() {
        return detonado;
    }

    public double calcularDistancia(double xNave, int altitudNave) {
        double distanciaHorizontal = Math.abs(posicionX - xNave) * 5;
        double distanciaVertical = Math.abs(altitudActual - altitudNave);

        return Math.sqrt(
            distanciaHorizontal * distanciaHorizontal +
            distanciaVertical * distanciaVertical
        );
    }

    public int getAltitudActual() {
        return altitudActual;
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
        posicionY = convertirAltitudAY(altoPantalla);
        renderer.draw(g2d, this, altoPantalla);
    }

    private double convertirAltitudAY(int altoPantalla) {
        double proporcion = (altitudActual - 500) / 4500.0;
        return altoPantalla - 60 - proporcion * (altoPantalla - 120);
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}