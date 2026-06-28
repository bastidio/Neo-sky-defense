package skydefense.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import skydefense.engine.GestorRecursos;

public class ArchivoPuntuaciones {

	private final String rutaArchivo =
	        GestorRecursos.getInstancia().getBaseRes() + "json/puntuaciones.json";

    public ArrayList<Puntuaciones.Puntaje> cargar() {
        ArrayList<Puntuaciones.Puntaje> puntajes = new ArrayList<>();

        try {
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                puntajes = precargarPuntajes();
                guardar(puntajes);
                return puntajes;
            }

            String contenido = Files.readString(archivo.toPath());
            

            Pattern pattern = Pattern.compile("\\{\\s*\"nombre\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"puntaje\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher matcher = pattern.matcher(contenido);

            while (matcher.find()) {
                String nombre = matcher.group(1);
                int score = Integer.parseInt(matcher.group(2));
                puntajes.add(new Puntuaciones.Puntaje(nombre, score));
            }

            puntajes.sort((a, b) -> Integer.compare(b.score, a.score));

        } catch (Exception e) {
            System.err.println("No se pudo cargar el archivo de puntuaciones.");
            e.printStackTrace();
        }

        return puntajes;
    }

    public void guardar(ArrayList<Puntuaciones.Puntaje> puntajes) {
        try {
            File archivo = new File(rutaArchivo);
            archivo.getParentFile().mkdirs();

            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"puntajes\": [\n");

            for (int i = 0; i < puntajes.size(); i++) {
                Puntuaciones.Puntaje p = puntajes.get(i);

                json.append("    { \"nombre\": \"")
	                .append(p.name)
	                .append("\", \"puntaje\": ")
	                .append(p.score)
	                .append(" }");

                if (i < puntajes.size() - 1) {
                    json.append(",");
                }

                json.append("\n");
            }

            json.append("  ]\n");
            json.append("}");

            Files.writeString(Path.of(rutaArchivo), json.toString());

        } catch (Exception e) {
            System.err.println("No se pudo guardar el archivo de puntuaciones.");
            e.printStackTrace();
        }
    }

    private ArrayList<Puntuaciones.Puntaje> precargarPuntajes() {
        ArrayList<Puntuaciones.Puntaje> puntajes = new ArrayList<>();

        puntajes.add(new Puntuaciones.Puntaje("BRU", 9340));
        puntajes.add(new Puntuaciones.Puntaje("JOA", 4590));
        puntajes.add(new Puntuaciones.Puntaje("FTG", 2420));
        puntajes.add(new Puntuaciones.Puntaje("ALE", 800));

        return puntajes;
    }
}
