package skydefense.model;

public class Nivel {

    private int numero;
    private double velocidadDrones;
    private double velocidadMisiles;
    private double frecuenciaDisparo;

    public Nivel() {
        this.numero = 1;
        this.velocidadDrones = 150;
        this.velocidadMisiles = 1200;
        this.frecuenciaDisparo = 1.6;
    }
    
    public void aplicarIncrementoDificultad() {
        numero++;
        velocidadDrones *= 1.15;
        velocidadMisiles *= 1.15;
        frecuenciaDisparo = Math.max(0.65, frecuenciaDisparo / 1.15);
    }
    
    public boolean esUltimoNivel() {
        return numero >= 7;
    }

    public int getNumero() {
        return numero;
    }

    public double getVelocidadDrones() {
        return velocidadDrones;
    }

    public double getVelocidadMisiles() {
        return velocidadMisiles;
    }

    public double getFrecuenciaDisparo() {
        return frecuenciaDisparo;
    }
}