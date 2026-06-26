package skydefense.engine;

import skydefense.model.Escuadron;
import skydefense.model.Explosion;
import skydefense.model.Jugador;
import skydefense.model.Misil;
import skydefense.model.Nave;
import skydefense.model.Nivel;
import skydefense.model.Puntuaciones;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Rectangle;
import java.util.ArrayList;

public class MotorJuego {

    private Nave nave;
    private Jugador jugador;
    private Nivel nivel;
    private Escuadron escuadron;

    private ArrayList<Misil> misiles = new ArrayList<>();
    private ArrayList<Explosion> explosiones = new ArrayList<>();

    private boolean juegoTerminado = false;
    private boolean victoria = false;
    private boolean scoreGuardado = false;
    private boolean juegoPausado = false;
    private boolean confirmandoVolverMenu = false;

    private int opcionPausaSeleccionada = 0;
    private int opcionConfirmacionSeleccionada = 1;

    private JPanel panelPadre; // Necesario para el ancho de pantalla y JOptionPane

    public MotorJuego(JPanel panelPadre) {
        this.panelPadre = panelPadre;
        init();
    }

    public void init() {
        jugador = new Jugador();
        nivel = new Nivel();

        nave = new Nave(GestorRecursos.getInstancia().getSpriteNave(), 800, 600);
        escuadron = new Escuadron(
            GestorRecursos.getInstancia().getSpriteDron(), 
            GestorRecursos.getInstancia().getSpriteMisil()
        );

        misiles.clear();
        explosiones.clear();

        juegoTerminado = false;
        victoria = false;
        scoreGuardado = false;
        juegoPausado = false;
        confirmandoVolverMenu = false;
    }

    public void update(double delta, int anchoPantalla) {
        if (juegoTerminado || juegoPausado) return;

        nave.update(delta);
        
        // 1. Le pasamos el Ancho y el Alto a la nave para los topes dinámicos
        nave.limitarPantalla(anchoPantalla, panelPadre.getHeight());

        ArrayList<Misil> nuevosMisiles = escuadron.update(delta, nivel, anchoPantalla);
        misiles.addAll(nuevosMisiles);

        for (int i = misiles.size() - 1; i >= 0; i--) {
            Misil misil = misiles.get(i);
            
            // Le pasamos el alto de la ventana en tiempo real
            misil.update(delta, panelPadre.getHeight()); 

            if (!misil.estaActivo()) {
                if (misil.fueDetonado()) {
                    procesarExplosion(misil);
                }
                misiles.remove(i);
            }
        }

        for (int i = explosiones.size() - 1; i >= 0; i--) {
            Explosion explosion = explosiones.get(i);
            explosion.update(delta);

            if (!explosion.estaActiva()) {
                explosiones.remove(i);
            }
        }
        
        if (escuadron.nivelTerminado() && misiles.isEmpty() && explosiones.isEmpty()) {
            if (jugador.sumarPuntos(300)) {
                GestorAudio.getInstancia().reproducirSonidoVidaExtra();
            }

            if (nivel.esUltimoNivel()) {
                terminarJuego(true);
            } else {
                nivel.aplicarIncrementoDificultad();
                escuadron = new Escuadron(
                    GestorRecursos.getInstancia().getSpriteDron(), 
                    GestorRecursos.getInstancia().getSpriteMisil()
                );
            }
        }
    }

    private void procesarExplosion(Misil misil) {
        explosiones.add(new Explosion(misil.getPosicionX(), misil.getPosicionY(), GestorRecursos.getInstancia().getAnimacionExplosion()));
        
        // 2. Geometría 2D exacta: Mide la distancia en píxeles reales de la pantalla
        double distanciaX = misil.getPosicionX() - nave.getPosicionX();
        double distanciaY = misil.getPosicionY() - nave.getPosicionY();
        double distancia = Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);

        if (distancia > 150) {
            if (jugador.sumarPuntos(40)) GestorAudio.getInstancia().reproducirSonidoVidaExtra();
        } else if (distancia >= 80 && distancia <= 150) {
            if (jugador.sumarPuntos(20)) GestorAudio.getInstancia().reproducirSonidoVidaExtra();
            nave.reducirEnergia(20);
        } else if (distancia >= 20 && distancia < 80) {
            nave.reducirEnergia(40);
        } else {
            // Impacto crítico: Resta los 100 de energía directamente
            jugador.perderVida();
        }

        if (nave.energiaAgotada()) {
            jugador.perderVida();
            nave.restaurarEnergia();
        }

        if (jugador.estaMuerto()) {
            terminarJuego(false);
        }
    }

    private void terminarJuego(boolean victoria) {
        this.juegoTerminado = true;
        this.victoria = victoria;
        GestorAudio.getInstancia().detenerMusicaJuego();

        if (!victoria) GestorAudio.getInstancia().reproducirSonidoGameOver();
        guardarScoreSiCorresponde();
    }

    private void guardarScoreSiCorresponde() {
        if (scoreGuardado) return;
        scoreGuardado = true;

        String nombre = JOptionPane.showInputDialog(panelPadre, "ENTER YOUR NAME (3 LETTERS):", "AAA");
        if (nombre == null || nombre.trim().isEmpty()) nombre = "AAA";
        nombre = nombre.trim().toUpperCase();
        if (nombre.length() > 3) nombre = nombre.substring(0, 3);
        while (nombre.length() < 3) nombre += "A";

        Puntuaciones.getInstancia().agregarScore(nombre, jugador.getPuntos());
    }

    // Controles de Pausa
    public void alternarPausa() {
        if (juegoTerminado) return;
        juegoPausado = !juegoPausado;
        confirmandoVolverMenu = false;
        opcionPausaSeleccionada = 0;
        opcionConfirmacionSeleccionada = 1;

        if (juegoPausado) GestorAudio.getInstancia().pausarMusicaJuego();
        else GestorAudio.getInstancia().reanudarMusicaJuego();
    }

    public void reanudarJuego() {
        juegoPausado = false;
        confirmandoVolverMenu = false;
        GestorAudio.getInstancia().reanudarMusicaJuego();
    }

    public void pedirConfirmacionVolverMenu() {
        confirmandoVolverMenu = true;
        opcionConfirmacionSeleccionada = 1;
    }

    public void cancelarConfirmacionVolverMenu() {
        confirmandoVolverMenu = false;
        opcionPausaSeleccionada = 0;
    }

    // Getters y Setters
    public Nave getNave() { return nave; }
    public Jugador getJugador() { return jugador; }
    public Nivel getNivel() { return nivel; }
    public Escuadron getEscuadron() { return escuadron; }
    public ArrayList<Misil> getMisiles() { return misiles; }
    public ArrayList<Explosion> getExplosiones() { return explosiones; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public boolean isVictoria() { return victoria; }
    public boolean isJuegoPausado() { return juegoPausado; }
    public boolean isConfirmandoVolverMenu() { return confirmandoVolverMenu; }
    public int getOpcionPausaSeleccionada() { return opcionPausaSeleccionada; }
    public void setOpcionPausaSeleccionada(int val) { this.opcionPausaSeleccionada = val; }
    public int getOpcionConfirmacionSeleccionada() { return opcionConfirmacionSeleccionada; }
    public void setOpcionConfirmacionSeleccionada(int val) { this.opcionConfirmacionSeleccionada = val; }
}