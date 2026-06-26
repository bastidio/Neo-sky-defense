package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Misil extends ObjetoVolador {

    // Ahora guardamos un porcentaje (0.0 a 1.0) en vez de un píxel fijo
    private double factorDetonacion; 
    private boolean detonado = false;

    private double velocidadCaida;
    private BufferedImage sprite;

    private RenderizadorMisil renderer;

    public Misil(double posicionX, int altitudInicial, double velocidadCaida, BufferedImage sprite) {
        this.posicionX = posicionX;
        this.posicionY = 75; 
        
        this.velocidadCaida = velocidadCaida * 0.15; 
        this.sprite = sprite;
        this.renderer = new RenderizadorMisil();

        Random random = new Random();
        // Sorteamos en qué porcentaje de la zona de juego va a explotar (ej: 0.5 es la mitad)
        this.factorDetonacion = random.nextDouble(); 

        this.ancho = 50;
        this.alto = 50;
    }

    @Override
    public void update(double delta) {
        // Lo dejamos vacío para cumplir con la herencia de ObjetoVolador, 
        // pero usaremos el método sobrecargado de abajo.
    }

    // NUEVO MÉTODO: Recibe el altoPantalla en tiempo real
    public void update(double delta, int altoPantalla) {
        posicionY += velocidadCaida * delta;

        // Calculamos los límites reales de la pantalla AHORA MISMO
        double limiteSuperior = 300; // Tu pared invisible
        double limiteInferior = altoPantalla - 50; // El piso de la pantalla
        
        // Traducimos el porcentaje sorteado al píxel exacto de esta pantalla
        double yDetonacion = limiteSuperior + (factorDetonacion * (limiteInferior - limiteSuperior));

        // Todos explotan sí o sí al llegar a su marca
        if (posicionY >= yDetonacion) {
            detonado = true;
            activo = false;
        }

        // Limpieza de memoria dinámica (se borra solo si sale de TU pantalla actual)
        if (posicionY > altoPantalla + 100) {
            activo = false;
        }
    }

    public boolean fueDetonado() {
        return detonado;
    }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        renderer.draw(g2d, this, altoPantalla);
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}