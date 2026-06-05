package skydefense.model;

public class Jugador {

    private int puntos;
    private int vidas;
    private int proximaVidaExtra;

    public Jugador() {
        this.puntos = 0;
        this.vidas = 3;
        this.proximaVidaExtra = 1000;
    }

    public void sumarPuntos(int cantidad) {
        puntos += cantidad;
        verificarVidaExtra();
    }

    private void verificarVidaExtra() {
        while (puntos >= proximaVidaExtra) {
            agregarVida();
            proximaVidaExtra += 1000;
        }
    }

    public void perderVida() {
        vidas--;
    }

    public void agregarVida() {
        vidas++;
    }

    public boolean estaMuerto() {
        return vidas < 0;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getVidas() {
        return vidas;
    }
}