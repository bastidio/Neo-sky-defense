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

    private Font fuenteNormal;
    private Font fuenteHover;
    private Font fuenteTitulo;

    private BufferedImage spriteNave;
    private BufferedImage logo;
    private Image gifAmigos;
    private BufferedImage[] presentaciones = new BufferedImage[4];

    private BufferedImage spriteDron;
    private BufferedImage spriteMisil;
    private AnimacionExplosion animacionExplosion;
    private String baseRes;
    
    private GestorRecursos() {
    	if (new File("res/sprite/nave.png").exists()) {
    	    baseRes = "res/";
    	} else if (new File("skydefense/res/sprite/nave.png").exists()) {
    	    baseRes = "skydefense/res/";
    	} else if (new File("skydefense/skydefense/res/sprite/nave.png").exists()) {
    	    baseRes = "skydefense/skydefense/res/";
    	} else {
    	    baseRes = "res/";
    	}
        cargarFuentes();
        cargarImagenes();
    }
    
    public static GestorRecursos getInstancia() {
        if (instancia == null) {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    public String getBaseRes() {
        return baseRes;
    }

    private void cargarFuentes() {
        try {
            File archivoFuente = new File(baseRes + "font/Arcade.ttf");
            File archivoNightmare = new File(baseRes + "font/Nightmare Codehack.otf");

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
        try { spriteNave = ImageIO.read(new File(baseRes + "sprite/nave.png")); } 
        catch (Exception e) { System.err.println("No se pudo cargar la imagen de la nave."); }

        try { logo = ImageIO.read(new File(baseRes + "sprite/logo.png")); } 
        catch (Exception e) { System.err.println("No se pudo cargar la imagen del logo."); }

        try { gifAmigos = new ImageIcon(baseRes + "sprite/betterLogo.gif").getImage(); } 
        catch (Exception e) { System.err.println("No se pudo cargar el GIF de la intro betterLogo."); }

        try {
            presentaciones[0] = ImageIO.read(new File(baseRes + "sprite/presentacion1.png"));
            presentaciones[1] = ImageIO.read(new File(baseRes + "sprite/presentacion2.png"));
            presentaciones[2] = ImageIO.read(new File(baseRes + "sprite/presentacion3.png"));
            presentaciones[3] = ImageIO.read(new File(baseRes + "sprite/presentacion4.png"));
        } catch (Exception e) { System.err.println("No se pudieron cargar las imágenes de presentación."); }

        try { spriteDron = ImageIO.read(new File(baseRes + "sprite/dron.png")); } 
        catch (Exception e) { System.err.println("No se pudo cargar dron.png"); }

        try { spriteMisil = ImageIO.read(new File(baseRes + "sprite/misil.png")); } 
        catch (Exception e) { System.err.println("No se pudo cargar misil.png"); }

        try {
            animacionExplosion = new AnimacionExplosion(
                baseRes + "sprite/transparent-Photoroom (3) - copia.png",
                baseRes + "sprite/transparent-Photoroom (3) - copia.json"
            );
        } catch (Exception e) { System.err.println("No se pudo cargar la animación de explosión."); }
    }

    public Font getFuenteNormal() { return fuenteNormal; }
    public Font getFuenteHover() { return fuenteHover; }
    public Font getFuenteTitulo() { return fuenteTitulo; }

    public BufferedImage getSpriteNave() { return spriteNave; }
    public BufferedImage getLogo() { return logo; }
    public Image getGifAmigos() { return gifAmigos; }
    
    public BufferedImage getPresentacion(int index) {
        if (index >= 0 && index < presentaciones.length) return presentaciones[index];
        return null;
    }
    
    public int getCantidadPresentaciones() { return presentaciones.length; }

    public BufferedImage getSpriteDron() { return spriteDron; }
    public BufferedImage getSpriteMisil() { return spriteMisil; }
    public AnimacionExplosion getAnimacionExplosion() { return animacionExplosion; }
}