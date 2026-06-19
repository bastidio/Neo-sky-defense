package skydefense.engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class NaveMenu {

    private BufferedImage spriteNave;
    private ArrayList<Disparo> disparos;
    private JPanel panel; // Necesitamos el panel para calcular proporciones relativas a la ventana

    public NaveMenu(JPanel panel) {
        this.panel = panel;
        this.disparos = new ArrayList<>();
        
        try {
            spriteNave = ImageIO.read(new File("skydefense/res/sprite/nave.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de la nave en NaveMenu.");
        }
    }

    public void actualizar() {
        for (int i = disparos.size() - 1; i >= 0; i--) {
            Disparo d = disparos.get(i);
            d.mover();

            if (d.estaFuera(panel.getWidth(), panel.getHeight())) {
                disparos.remove(i);
            }
        }
    }

    public void disparar(int mouseX, int mouseY) {
        int anchoNave = getAnchoNaveMenu();
        int altoNave = getAltoNaveMenu();
        int naveX = getNaveXMenu();
        int naveY = getNaveYMenu();

        double centroX = naveX + anchoNave / 2.0;
        double centroY = naveY + altoNave / 2.0;

        double angulo = Math.atan2(mouseY - centroY, mouseX - centroX);

        double puntaX = centroX + Math.cos(angulo) * (anchoNave / 2.0);
        double puntaY = centroY + Math.sin(angulo) * (altoNave / 2.0);

        disparos.add(new Disparo(puntaX, puntaY, angulo));
    }

    public void dibujar(Graphics2D g2d, int mouseX, int mouseY) {
        for (Disparo d : disparos) {
            d.dibujar(g2d);
        }

        if (spriteNave != null) {
            int anchoNave = getAnchoNaveMenu();
            int altoNave = getAltoNaveMenu();
            int naveX = getNaveXMenu();
            int naveY = getNaveYMenu();

            int centroX = naveX + anchoNave / 2;
            int centroY = naveY + altoNave / 2;

            double angulo = Math.atan2(mouseY - centroY, mouseX - centroX);

            AffineTransform old = g2d.getTransform();
            g2d.rotate(angulo + Math.PI / 2, centroX, centroY);
            g2d.drawImage(spriteNave, naveX, naveY, anchoNave, altoNave, null);
            g2d.setTransform(old);
        }
    }

    public void limpiarDisparos() {
        disparos.clear();
    }

    private int getAnchoNaveMenu() {
        return Math.max(180, panel.getWidth() / 6);
    }

    private int getAltoNaveMenu() {
        return getAnchoNaveMenu();
    }

    private int getNaveXMenu() {
        int anchoNave = getAnchoNaveMenu();
        return panel.getWidth() - anchoNave - (panel.getWidth() / 6);
    }

    private int getNaveYMenu() {
        int altoNave = getAltoNaveMenu();

        if (panel.getWidth() <= 900) {
            return panel.getHeight() / 2 - altoNave / 2 + 35;
        }
        return panel.getHeight() / 2 - altoNave / 2 + 70;
    }
}