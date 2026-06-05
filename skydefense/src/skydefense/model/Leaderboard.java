package skydefense.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Leaderboard {

    private static Leaderboard instancia;

    private final String rutaArchivo = "skydefense/res/json/leaderboard.json";

    private ArrayList<ScoreEntry> scores = new ArrayList<>();

    private Leaderboard() {
        cargar();
    }

    public static Leaderboard getInstancia() {
        if (instancia == null) {
            instancia = new Leaderboard();
        }

        return instancia;
    }

    public void agregarScore(String nombre, int score) {
        scores.add(new ScoreEntry(nombre, score));

        scores.sort((a, b) -> Integer.compare(b.score, a.score));

        while (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }

        guardar();
    }

    public ArrayList<ScoreEntry> getScores() {
        return scores;
    }

    private void cargar() {
        try {
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                precargarScores();
                guardar();
                return;
            }

            String contenido = Files.readString(Path.of(rutaArchivo));

            Pattern pattern = Pattern.compile("\\{\\s*\"name\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"score\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher matcher = pattern.matcher(contenido);

            while (matcher.find()) {
                String nombre = matcher.group(1);
                int score = Integer.parseInt(matcher.group(2));
                scores.add(new ScoreEntry(nombre, score));
            }

            scores.sort((a, b) -> Integer.compare(b.score, a.score));

        } catch (Exception e) {
            System.err.println("No se pudo cargar el leaderboard.");
        }
    }

    private void precargarScores() {
        scores.add(new ScoreEntry("BRU", 9340));
        scores.add(new ScoreEntry("JOA", 4590));
        scores.add(new ScoreEntry("FTG", 2420));
        scores.add(new ScoreEntry("ALE", 800));
    }

    private void guardar() {
        try {
            File archivo = new File(rutaArchivo);
            archivo.getParentFile().mkdirs();

            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"scores\": [\n");

            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry s = scores.get(i);

                json.append("    { \"name\": \"")
                    .append(s.name)
                    .append("\", \"score\": ")
                    .append(s.score)
                    .append(" }");

                if (i < scores.size() - 1) {
                    json.append(",");
                }

                json.append("\n");
            }

            json.append("  ]\n");
            json.append("}");

            Files.writeString(Path.of(rutaArchivo), json.toString());

        } catch (Exception e) {
            System.err.println("No se pudo guardar el leaderboard.");
        }
    }

    public static class ScoreEntry {
        public String name;
        public int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}