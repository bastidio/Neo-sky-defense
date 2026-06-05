package skydefense.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class Explosion extends ObjetoVolador {

    private BufferedImage spritesheet;
    private List<Rectangle> frames;
    private int frameActual = 0;
    
    private double duracionFrame = 0.05; // Ajusta la velocidad de la explosión
    private double tiempoAcumulado = 0; 

    public Explosion(double x, double y, String rutaSprite, String rutaJson) {
        this.posicionX = x;
        this.posicionY = y;
        this.frames = new ArrayList<>();
        this.activo = true;

        cargarSpritesheet(rutaSprite);
        cargarFramesJson(rutaJson);
    }

    private void cargarSpritesheet(String ruta) {
        try {
            spritesheet = ImageIO.read(new File(ruta));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el spritesheet: " + ruta);
        }
    }

    // Lector JSON Nativo
    private void cargarFramesJson(String rutaJson) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaJson)));
            
            // Busca exactamente el bloque "frame":{"x":..., "y":..., "w":..., "h":...}
            Pattern pattern = Pattern.compile("\"frame\"\\s*:\\s*\\{\\s*\"x\"\\s*:\\s*(\\d+)\\s*,\\s*\"y\"\\s*:\\s*(\\d+)\\s*,\\s*\"w\"\\s*:\\s*(\\d+)\\s*,\\s*\"h\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher matcher = pattern.matcher(contenido);

            while (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int w = Integer.parseInt(matcher.group(3));
                int h = Integer.parseInt(matcher.group(4));
                
                frames.add(new Rectangle(x, y, w, h));
            }
        } catch (Exception e) {
            System.err.println("Error al leer el archivo JSON: " + rutaJson);
        }
    }
    @Override
    public void update(double delta) {
        if (!activo) return;

        tiempoAcumulado += delta;

        if (tiempoAcumulado >= duracionFrame) {
            tiempoAcumulado = 0;
            frameActual++;

            if (frameActual >= frames.size()) {
                activo = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        if (!activo || spritesheet == null || frames.isEmpty() || frameActual >= frames.size()) {
            return;
        }

        Rectangle rect = frames.get(frameActual);

        // Coordenadas de destino (dónde se dibuja en pantalla, centrado)
        int destX1 = (int) posicionX - (rect.width / 2);
        int destY1 = (int) posicionY - (rect.height / 2);
        int destX2 = destX1 + rect.width;
        int destY2 = destY1 + rect.height;

        // Coordenadas de origen (qué recorte del PNG se extrae)
        int srcX1 = rect.x;
        int srcY1 = rect.y;
        int srcX2 = rect.x + rect.width;
        int srcY2 = rect.y + rect.height;

        g2d.drawImage(spritesheet, destX1, destY1, destX2, destY2, srcX1, srcY1, srcX2, srcY2, null);
    }
}
