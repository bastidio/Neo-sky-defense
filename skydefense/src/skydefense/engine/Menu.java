package skydefense.engine;

import javax.swing.*;
import java.awt.*;
import java.io.File;


public class Menu extends JPanel {

    private String[] opciones = {"PLAY", "LEADERBOARD", "OPTIONS", "EXIT"};
    private Rectangle[] hitboxes = new Rectangle[4];
    private int hoveredIndex = -1;

    private int[] posicionesY = {200, 275, 350, 425};
    private int posicionXBase = 65; 
    
    private Font fuenteNormal;
    private Font fuenteHover; 
    private Font fuenteTitulo;

    public Menu() {
        setBackground(Color.BLACK);

        // ==========================================
        // CARGA DE FUENTE PERSONALIZADA: STARLIGHT
        // ==========================================
        try {
            // Ajustado para buscar dentro de la carpeta 'res/fonts' de tu proyecto
            File archivoFuente = new File("res/font/Arcade.ttf");
            
            Font fuenteBase = Font.createFont(Font.TRUETYPE_FONT, archivoFuente);
            
            fuenteNormal = fuenteBase.deriveFont(40f);
            fuenteHover = fuenteBase.deriveFont(50f); 
            
            File archivoNightmare = new File("res/font/Nightmare Codehack.otf");
            Font baseNightmare = Font.createFont(Font.TRUETYPE_FONT, archivoNightmare);
            fuenteTitulo = baseNightmare.deriveFont(70f);
            
        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente Starlight. Cargando Arial de repuesto.");
            fuenteNormal = new Font("Arial", Font.BOLD, 26);
            fuenteHover = new Font("Arial", Font.BOLD, 32);
        }

        // ==========================================
        // CREACIÓN DE HITBOXES (Ajustados a la fuente)
        // ==========================================
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(fuenteNormal);

        for (int i = 0; i < opciones.length; i++) {
            int anchoTexto = fm.stringWidth(opciones[i]);
            int altoTexto = fm.getHeight();
            int ascent = fm.getAscent(); 

            // Creamos la caja invisible envolviendo exactamente la palabra
            hitboxes[i] = new Rectangle(posicionXBase, posicionesY[i] - ascent, anchoTexto, altoTexto);
        }

        // Evento de movimiento del mouse
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int previousHover = hoveredIndex;
                hoveredIndex = -1; 

                for (int i = 0; i < hitboxes.length; i++) {
                    if (hitboxes[i].contains(e.getPoint())) {
                        hoveredIndex = i;
                        break;
                    }
                }

                if (previousHover != hoveredIndex) {
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ==========================================
        // 1. TÍTULO: "SKY DEFENSE" 
        // ==========================================
        g2d.setFont(fuenteTitulo); // Aplicamos la fuente Nightmare Codehack
        g2d.setColor(Color.RED);
        g2d.drawString("S", 420, 90);
        g2d.setColor(Color.WHITE);
        g2d.drawString("KY", 450, 90);
        g2d.setColor(Color.BLUE);
        g2d.drawString("D", 520, 90);
        g2d.setColor(Color.WHITE);
        g2d.drawString("EFENSE", 550, 90);

        // ==========================================
        // 2. TEXTOS INTERACTIVOS (Con fuente Starlight)
        // ==========================================
        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];
            
            if (i == hoveredIndex) {
                g2d.setFont(fuenteHover);
                g2d.setColor(Color.YELLOW); 
                
                FontMetrics fmHover = g2d.getFontMetrics();
                int anchoNormal = hitboxes[i].width;
                int anchoHover = fmHover.stringWidth(texto);
                int xAjustado = posicionXBase - ((anchoHover - anchoNormal) / 2);
                
                g2d.drawString(texto, xAjustado, posicionesY[i]);
                
            } else {
                g2d.setFont(fuenteNormal);
                
                // ==========================================
                // EFECTO GLOW CONSTANTE (Blanco)
                // ==========================================
                Color blancoResplandor = new Color(255, 255, 255, 60); 
                g2d.setColor(blancoResplandor);
                
                int desfase = 2;
                g2d.drawString(texto, posicionXBase - desfase, posicionesY[i] - desfase);
                g2d.drawString(texto, posicionXBase + desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, posicionXBase - desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, posicionXBase + desfase, posicionesY[i] - desfase);

                // ==========================================
                // TEXTO PRINCIPAL
                // ==========================================
                g2d.setColor(Color.WHITE);
                g2d.drawString(texto, posicionXBase, posicionesY[i]);
            }
        }

        // ==========================================
        // 3. NAVE ESPACIAL (Área reservada)
        // ==========================================
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(450, 180, 280, 300);
        
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        g2d.setColor(Color.GRAY);
        g2d.drawString("[ Espacio para Sprite de Nave ]", 495, 320);

        g2d.setColor(Color.RED);
        int[] xPuntos = {560, 620, 560};
        int[] yPuntos = {290, 320, 350};
        g2d.fillPolygon(xPuntos, yPuntos, 3);
    }
}
