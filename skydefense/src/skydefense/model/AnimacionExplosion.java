package skydefense.model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class AnimacionExplosion {

    private BufferedImage spritesheet;
    private ArrayList<BufferedImage> frames = new ArrayList<>();

    public AnimacionExplosion(String rutaImagen, String rutaJson) {
        cargar(rutaImagen, rutaJson);
    }

    private void cargar(String rutaImagen, String rutaJson) {

        try {

            spritesheet = ImageIO.read(new File(rutaImagen));

            String contenido =
                Files.readString(new File(rutaJson).toPath());

            Pattern pattern = Pattern.compile(
                "\"frame\"\\s*:\\s*\\{\\s*\"x\"\\s*:\\s*(\\d+)\\s*,\\s*\"y\"\\s*:\\s*(\\d+)\\s*,\\s*\"w\"\\s*:\\s*(\\d+)\\s*,\\s*\"h\"\\s*:\\s*(\\d+)"
            );

            Matcher matcher = pattern.matcher(contenido);

            while (matcher.find()) {

                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int w = Integer.parseInt(matcher.group(3));
                int h = Integer.parseInt(matcher.group(4));

                BufferedImage frame =
                    spritesheet.getSubimage(x, y, w, h);

                frames.add(frame);
            }

        } catch (Exception e) {
            System.err.println("No se pudo cargar la animacion de explosion.");
            e.printStackTrace();
        }
    }

    public BufferedImage getFrame(int indice) {

        if (indice < 0 || indice >= frames.size()) {
            return null;
        }

        return frames.get(indice);
    }

    public int getCantidadFrames() {
        return frames.size();
    }
}