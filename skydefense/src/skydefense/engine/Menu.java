package skydefense.engine;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Menu extends JPanel {

    private String[] opciones = {"PLAY", "SCORES", "OPTIONS", "EXIT"};
    private Rectangle[] hitboxes = new Rectangle[4];
    private int hoveredIndex = -1;

    private int[] posicionesY = {200, 275, 350, 425};
    private int posicionXBase = 75;

    private Font fuenteNormal;
    private Font fuenteHover;
    private Font fuenteTitulo;
    
    private int mouseX = 500;
    private int mouseY = 280;

    private BufferedImage spriteNave;
    private BufferedImage logo;

    private ArrayList<Disparo> disparos = new ArrayList<>();

    public Menu() {
        System.out.println("Working dir: " + new File(".").getAbsolutePath());
        setBackground(Color.BLACK);

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

        try {
            spriteNave = ImageIO.read(new File("skydefense/res/sprite/nave.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de la nave. Verificá la ruta y el nombre.");
        }

        try {
            logo = ImageIO.read(new File("skydefense/res/sprite/logo.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del logo. Verificá la ruta y el nombre.");
        }

        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(fuenteNormal);

        for (int i = 0; i < opciones.length; i++) {
            int anchoTexto = fm.stringWidth(opciones[i]);
            int altoTexto = fm.getHeight();
            int ascent = fm.getAscent();

            hitboxes[i] = new Rectangle(posicionXBase, posicionesY[i] - ascent, anchoTexto, altoTexto);
        }

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
        	@Override
        	public void mouseMoved(java.awt.event.MouseEvent e) {
        	    int previousHover = hoveredIndex;
        	    hoveredIndex = -1;

        	    mouseY = e.getY();
        	    mouseX = e.getX();
        	    
        	    for (int i = 0; i < hitboxes.length; i++) {
        	        if (hitboxes[i].contains(e.getPoint())) {
        	            hoveredIndex = i;
        	            break;
        	        }
        	    }

        	    if (previousHover != hoveredIndex) {
        	        repaint();
        	    }

        	    repaint();
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                disparar();
            }
        });

        Timer timer = new Timer(16, e -> {
            for (int i = disparos.size() - 1; i >= 0; i--) {
                Disparo d = disparos.get(i);
                d.mover();

                if (d.estaFuera(getWidth(), getHeight())) {
                    disparos.remove(i);
                }
            }

            repaint();
        });

        timer.start();
    }

    private void disparar() {
        int anchoNave = 180;
        int altoNave = 180;
        int naveX = 500;
        int naveY = 250;

        double centroX = naveX + anchoNave / 2.0;
        double centroY = naveY + altoNave / 2.0;

        double angulo = Math.atan2(mouseY - centroY, mouseX - centroX);

        double puntaX = centroX + Math.cos(angulo) * (altoNave / 2.0);
        double puntaY = centroY + Math.sin(angulo) * (altoNave / 2.0);

        disparos.add(new Disparo(puntaX, puntaY, angulo));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (logo != null) {
            int altoLogo = 150;
            int anchoLogo = (int) ((double) logo.getWidth() / logo.getHeight() * altoLogo);

            int logoX = 450;
            int logoY = 5;

            g2d.drawImage(logo, logoX, logoY, anchoLogo, altoLogo, null);
        }

        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];

            if (i == hoveredIndex) {
                g2d.setFont(fuenteHover);
                g2d.setColor(Color.ORANGE);

                FontMetrics fmHover = g2d.getFontMetrics();
                int anchoNormal = hitboxes[i].width;
                int anchoHover = fmHover.stringWidth(texto);
                int xAjustado = posicionXBase - ((anchoHover - anchoNormal) / 2);

                g2d.drawString(texto, xAjustado, posicionesY[i]);

            } else {
                g2d.setFont(fuenteNormal);

                Color blancoResplandor = new Color(255, 255, 255, 60);
                g2d.setColor(blancoResplandor);

                int desfase = 2;
                g2d.drawString(texto, posicionXBase - desfase, posicionesY[i] - desfase);
                g2d.drawString(texto, posicionXBase + desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, posicionXBase - desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, posicionXBase + desfase, posicionesY[i] - desfase);

                g2d.setColor(Color.WHITE);
                g2d.drawString(texto, posicionXBase, posicionesY[i]);
            }
        }

        for (Disparo d : disparos) {
            d.dibujar(g2d);
        }

        if (spriteNave != null) {
            int anchoNave = 180;
            int altoNave = 180;
            int naveX = 500;
            int naveY = 250;

            int centroX = naveX + anchoNave / 2;
            int centroY = naveY + altoNave / 2;

            double angulo = Math.atan2(mouseY - centroY, mouseX - centroX);

            AffineTransform old = g2d.getTransform();

            g2d.rotate(angulo + Math.PI / 2, centroX, centroY);

            g2d.drawImage(spriteNave, naveX, naveY, anchoNave, altoNave, null);

            g2d.setTransform(old);
        }
    }

    private class Disparo {
        private double x;
        private double y;
        private double velocidadX;
        private double velocidadY;

        public Disparo(double x, double y, double angulo) {
            this.x = x;
            this.y = y;

            double velocidad = 12;
            this.velocidadX = Math.cos(angulo) * velocidad;
            this.velocidadY = Math.sin(angulo) * velocidad;
        }

        public void mover() {
            x += velocidadX;
            y += velocidadY;
        }

        public void dibujar(Graphics2D g2d) {

            g2d.setColor(Color.ORANGE);

            double angulo = Math.atan2(velocidadY, velocidadX);

            int largo = 16;

            int x2 = (int) (x - Math.cos(angulo) * largo);
            int y2 = (int) (y - Math.sin(angulo) * largo);

            Stroke oldStroke = g2d.getStroke();

            g2d.setStroke(new BasicStroke(
                6,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
            ));

            g2d.drawLine((int) x, (int) y, x2, y2);

            g2d.setStroke(oldStroke);
        }

        public boolean estaFuera(int anchoPanel, int altoPanel) {
            return x < 0 || x > anchoPanel || y < 0 || y > altoPanel;
        }
    }
}