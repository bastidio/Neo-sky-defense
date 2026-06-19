package skydefense.engine;

import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import skydefense.model.AnimacionExplosion;

public class GestorRecursos {

    private static GestorRecursos instancia;

    // --- Fuentes ---
    private Font fuenteNormal;
    private Font fuenteHover;
    private Font fuenteTitulo;

    // --- Recursos del Menú ---
    private BufferedImage spriteNave;
    private BufferedImage logo;
    private Image gifAmigos;
    private BufferedImage[] presentaciones = new BufferedImage[4];

    // --- Recursos del Juego (PanelJuego) ---
    private BufferedImage spriteDron;
    private BufferedImage spriteMisil;
    private AnimacionExplosion animacionExplosion;

    private GestorRecursos() {
        cargarFuentes();
        cargarImagenes();
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    private void cargarFuentes() {
        try {
            File archivoFuente = new File("skydefense/res/font/Arcade.ttf");
            File archivoNightmare = new File("skydefense/res/font/Nightmare Codehack.otf");

            Font fuenteBase = Font.createFont(Font.TRUETYPE_FONT, archivoFuente);
            fuenteNormal = fuenteBase.deriveFont(40f);
            fuenteHover = fuenteBase.deriveFont(50f);

            Font baseNightmare = Font.createFont(Font.TRUETYPE_FONT, archivoNightmare);
            fuenteTitulo = baseNightmare.deriveFont(70f);

        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente personalizada. Cargando Arial de repuesto.");
            fuenteNormal = new Font("Arial", Font.BOLD, 26);
            fuenteHover = new Font("Arial", Font.BOLD, 32);
            fuenteTitulo = new Font("Arial", Font.BOLD, 60);
        }
    }

    private void cargarImagenes() {
        // 1. Cargar imágenes del Menú
        try {
            spriteNave = ImageIO.read(new File("skydefense/res/sprite/nave.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de la nave.");
        }

        try {
            logo = ImageIO.read(new File("skydefense/res/sprite/logo.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del logo.");
        }

        try {
            gifAmigos = new ImageIcon("skydefense/res/sprite/betterLogo.gif").getImage();
        } catch (Exception e) {
            System.err.println("No se pudo cargar el GIF de la intro betterLogo.");
        }

        try {
            presentaciones[0] = ImageIO.read(new File("skydefense/res/sprite/presentacion1.png"));
            presentaciones[1] = ImageIO.read(new File("skydefense/res/sprite/presentacion2.png"));
            presentaciones[2] = ImageIO.read(new File("skydefense/res/sprite/presentacion3.png"));
            presentaciones[3] = ImageIO.read(new File("skydefense/res/sprite/presentacion4.png"));
        } catch (Exception e) {
            System.err.println("No se pudieron cargar las imágenes de presentación.");
        }

        // 2. Cargar imágenes del Juego
        try {
            spriteDron = ImageIO.read(new File("skydefense/res/sprite/dron.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar dron.png");
        }

        try {
            spriteMisil = ImageIO.read(new File("skydefense/res/sprite/misil.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar misil.png");
        }

        try {
            animacionExplosion = new AnimacionExplosion(
                "skydefense/res/sprite/transparent-Photoroom (3) - copia.png",
                "skydefense/res/sprite/transparent-Photoroom (3) - copia.json"
            );
        } catch (Exception e) {
            System.err.println("No se pudo cargar la animación de explosión.");
        }
    }

    // --- Getters de Fuentes ---
    public Font getFuenteNormal() { return fuenteNormal; }
    public Font getFuenteHover() { return fuenteHover; }
    public Font getFuenteTitulo() { return fuenteTitulo; }

    // --- Getters del Menú ---
    public BufferedImage getSpriteNave() { return spriteNave; }
    public BufferedImage getLogo() { return logo; }
    public Image getGifAmigos() { return gifAmigos; }
    
    public BufferedImage getPresentacion(int index) {
        if (index >= 0 && index < presentaciones.length) {
            return presentaciones[index];
        }
        return null;
    }
    
    public int getCantidadPresentaciones() { 
        return presentaciones.length; 
    }

    // --- Getters del Juego ---
    public BufferedImage getSpriteDron() { return spriteDron; }
    public BufferedImage getSpriteMisil() { return spriteMisil; }
    public AnimacionExplosion getAnimacionExplosion() { return animacionExplosion; }
}