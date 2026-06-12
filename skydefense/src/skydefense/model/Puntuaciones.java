package skydefense.model;

import java.util.ArrayList;

public class Puntuaciones {

    private static Puntuaciones instancia;

    private ArrayList<Puntaje> puntajes = new ArrayList<>();
    private ArchivoPuntuaciones archivoPuntuaciones = new ArchivoPuntuaciones();

    private Puntuaciones() {
        puntajes = archivoPuntuaciones.cargar();
    }

    public static Puntuaciones getInstancia() {
        if (instancia == null) {
            instancia = new Puntuaciones();
        }

        return instancia;
    }

    public void agregarScore(String nombre, int score) {
        puntajes.add(new Puntaje(nombre, score));

        puntajes.sort((a, b) -> Integer.compare(b.score, a.score));

        while (puntajes.size() > 10) {
            puntajes.remove(puntajes.size() - 1);
        }

        archivoPuntuaciones.guardar(puntajes);
    }

    public ArrayList<Puntaje> getPuntajes() {
        return puntajes;
    }

    public static class Puntaje {
        public String name;
        public int score;

        public Puntaje(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}