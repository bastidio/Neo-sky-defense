package skydefense.engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PantallaLeaderboard implements EstadoPantalla {

	private List<String> lineasPuntajes = new ArrayList<>();
    private Menu menuContexto;
    private GestorRecursos recursos;

    public PantallaLeaderboard(Menu menuContexto) {
        this.menuContexto = menuContexto;
        this.recursos = GestorRecursos.getInstancia();
        cargarPuntajes(); // <-- AGREGAR ESTA LÍNEA
    }

    @Override
    public void actualizar() { }

    @Override
    public void dibujar(Graphics2D g2d) {
        int anchoPantalla = menuContexto.getWidth();
        int altoPantalla = menuContexto.getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, anchoPantalla, altoPantalla);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(recursos.getFuenteTitulo().deriveFont(48f));
        String titulo = "LEADERBOARD";
        int tituloX = (anchoPantalla - g2d.getFontMetrics().stringWidth(titulo)) / 2;
        g2d.drawString(titulo, tituloX, 100);

        g2d.setColor(Color.WHITE);
        g2d.setFont(recursos.getFuenteNormal().deriveFont(24f));
        String subTitulo = "Press ESC to return";
        int subX = (anchoPantalla - g2d.getFontMetrics().stringWidth(subTitulo)) / 2;
        g2d.drawString(subTitulo, subX, altoPantalla - 50);
        
        
     // --- NUEVO BLOQUE PARA DIBUJAR LOS PUNTAJES ---
        g2d.setFont(recursos.getFuenteNormal().deriveFont(28f));
        int yInicial = 180;  // Altura base debajo del título
        int deltaY = 40;     // Espaciado vertical entre filas

        for (int i = 0; i < lineasPuntajes.size(); i++) {
            String textoPuntaje = (i + 1) + ". " + lineasPuntajes.get(i);
            int puntajeX = (anchoPantalla - g2d.getFontMetrics().stringWidth(textoPuntaje)) / 2;
            
            // Color verde para el récord más alto, gris claro para el resto
            if (i == 0) g2d.setColor(Color.GREEN); 
            else g2d.setColor(Color.LIGHT_GRAY);
            
            g2d.drawString(textoPuntaje, puntajeX, yInicial + (i * deltaY));
        }
        // ----------------------------------------------
    }
    private void cargarPuntajes() {
        String rutaArchivo = "/home/alectro/git/Neo-sky-defense/skydefense/res/json/puntuaciones.json";
        try {
            String contenido = Files.readString(Paths.get(rutaArchivo));
            String[] partes = contenido.split("\\{");
            for (String parte : partes) {
                if (parte.contains("\"nombre\"") && parte.contains("\"puntaje\"")) {
                    String nombre = parte.split("\"nombre\": \"")[1].split("\"")[0];
                    String puntajeStr = parte.split("\"puntaje\": ")[1].split(" ")[0].replaceAll("\\D+", "");
                    lineasPuntajes.add(nombre + " - " + puntajeStr);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer puntuaciones.json: " + e.getMessage());
            lineasPuntajes.add("Error al cargar puntajes");
        }
    }

    @Override
    public void teclaPresionada(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            menuContexto.cambiarPantalla(new PantallaPrincipal(menuContexto));
        }
    }

    @Override
    public void clickRaton(MouseEvent e) { }

    @Override
    public void mouseMovido(MouseEvent e) { }
}