package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Escuadron {

    private int totalDrones = 10;
    private int dronesGenerados = 0;
    private int dronesMuertos = 0;
    private int topeDrones = 4;

    private double tiempoParaProximoDron = 0;

    private Random random = new Random();

    private ArrayList<Dron> dronesActivos = new ArrayList<>();

    private FabricaDron fabricaDron;

    public Escuadron(BufferedImage spriteDron, BufferedImage spriteMisil) {
    	FabricaMisil fabricaMisil = new FabricaMisil(spriteMisil);
        this.fabricaDron = new FabricaDron(spriteDron, fabricaMisil);

        this.tiempoParaProximoDron = 0.4 + random.nextDouble() * 1.3;
    }

    public void generarDronSiCorresponde(double delta, int anchoPantalla, Nivel nivel) {
        if (dronesGenerados >= totalDrones) {
            return;
        }

        if (dronesActivos.size() >= topeDrones) {
            return;
        }

        tiempoParaProximoDron -= delta;

        if (tiempoParaProximoDron <= 0) {
            Dron dron = fabricaDron.crearDron(anchoPantalla, nivel.getVelocidadDrones());

            dronesActivos.add(dron);
            dronesGenerados++;

            tiempoParaProximoDron = 0.7 + random.nextDouble() * 2.0;
        }
    }

    public ArrayList<Misil> update(double delta, Nivel nivel, int anchoPantalla) {
        ArrayList<Misil> misilesNuevos = new ArrayList<>();

        generarDronSiCorresponde(delta, anchoPantalla, nivel);

        for (int i = dronesActivos.size() - 1; i >= 0; i--) {
            Dron dron = dronesActivos.get(i);

            dron.update(delta);

            Misil misil = dron.intentarDisparar(
                delta,
                nivel.getFrecuenciaDisparo(),
                nivel.getVelocidadMisiles()
            );

            if (misil != null) {
                misilesNuevos.add(misil);
            }

            dron.verificarSalida(anchoPantalla);

            if (!dron.estaActivo()) {
                dronesActivos.remove(i);
                dronesMuertos++;
            }
        }

        return misilesNuevos;
    }

    public boolean nivelTerminado() {
        return dronesGenerados >= totalDrones && dronesActivos.isEmpty();
    }

    public int getDronesRestantes() {
        return totalDrones - dronesMuertos;
    }

    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        for (Dron dron : dronesActivos) {
            dron.draw(g2d, anchoPantalla, altoPantalla);
        }
    }
}