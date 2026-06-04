package skydefense.engine;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

public class Menu extends JPanel { 

    private String[] opciones = {"PLAY", "LEADERBOARD", "OPTIONS", "EXIT"};
    private Rectangle[] hitboxes = new Rectangle[4];
    private int hoveredIndex = -1;

    private int[] posicionesY = {200, 275, 350, 425};
    private int posicionXBase = 65; 
    
    private Font fuenteNormal;
    private Font fuenteHover; 
    private Font fuenteTitulo; 
    
    private Image spriteNave; 

    public Menu() {
        setBackground(Color.BLACK);

        // ==========================================
        // RUTA INTELIGENTE (Detecta la arquitectura)
        // ==========================================
        String baseRes = "skydefense/res/";
        if (!new File(baseRes).exists()) {
            baseRes = "res/"; // Fallback por si Eclipse ajusta el directorio de trabajo
        }

        // ==========================================
        // 1. CARGA DE FUENTES
        // ==========================================
        try {
            File archivoArcade = new File(baseRes + "font/Arcade.ttf");
            Font baseArcade = Font.createFont(Font.TRUETYPE_FONT, archivoArcade);
            fuenteNormal = baseArcade.deriveFont(35f); 
            fuenteHover = baseArcade.deriveFont(45f); 

            File archivoNightmare = new File(baseRes + "font/Nightmare Codehack.otf");
            Font baseNightmare = Font.createFont(Font.TRUETYPE_FONT, archivoNightmare);
            fuenteTitulo = baseNightmare.deriveFont(70f); 
            
        } catch (Exception e) {
            System.err.println("⚠ AVISO: No se encontraron las fuentes en " + baseRes + "font/");
            fuenteNormal = new Font("Arial", Font.BOLD, 26);
            fuenteHover = new Font("Arial", Font.BOLD, 32);
            fuenteTitulo = new Font("Arial", Font.BOLD, 50);
        }

        // ==========================================
        // 2. CARGA DE LA IMAGEN DE LA NAVE
        // ==========================================
        try {
            File archivoNave = new File(baseRes + "sprite/nave.png");
            spriteNave = ImageIO.read(archivoNave);
            System.out.println("✅ Imagen cargada con éxito desde: " + archivoNave.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("⚠ AVISO: No se pudo cargar nave.png en " + baseRes + "sprite/");
            spriteNave = null; 
        }

        // ==========================================
        // CREACIÓN DE HITBOXES Y EVENTOS
        // ==========================================
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(fuenteNormal);

        for (int i = 0; i < opciones.length; i++) {
            int anchoTexto = fm.stringWidth(opciones[i]);
            int altoTexto = fm.getHeight();
            int ascent = fm.getAscent(); 
            hitboxes[i] = new Rectangle(posicionXBase, posicionesY[i] - ascent, anchoTexto, altoTexto);
        }

        setFocusable(true); 
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int tecla = e.getKeyCode();
                if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) {
                    if (hoveredIndex > 0) hoveredIndex--;
                    else if (hoveredIndex == -1) hoveredIndex = 0;
                    repaint();
                } else if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) {
                    if (hoveredIndex < opciones.length - 1) hoveredIndex++;
                    else if (hoveredIndex == -1) hoveredIndex = 0;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int previousHover = hoveredIndex;
                hoveredIndex = -1; 
                for (int i = 0; i < hitboxes.length; i++) {
                    if (hitboxes[i].contains(e.getPoint())) {
                        hoveredIndex = i;
                        break;
                    }
                }
                if (previousHover != hoveredIndex) repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. TÍTULO
        g2d.setFont(fuenteTitulo); 
        int tituloY = 100;
        g2d.setColor(Color.RED);
        g2d.drawString("S", 400, tituloY);
        g2d.setColor(Color.WHITE);
        g2d.drawString("KY", 430, tituloY); 
        g2d.setColor(Color.BLUE);
        g2d.drawString("D", 510, tituloY);
        g2d.setColor(Color.WHITE);
        g2d.drawString("EFENSE", 545, tituloY);

        // 2. TEXTOS INTERACTIVOS
        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];
            
            if (i == hoveredIndex) {
                g2d.setFont(fuenteHover);
                FontMetrics fmHover = g2d.getFontMetrics();
                int anchoNormal = hitboxes[i].width;
                int anchoHover = fmHover.stringWidth(texto);
                int xAjustado = posicionXBase - ((anchoHover - anchoNormal) / 2);
                
                Color colorResplandor = new Color(255, 255, 0, 80); 
                g2d.setColor(colorResplandor);
                int desfase = 3; 
                g2d.drawString(texto, xAjustado - desfase, posicionesY[i] - desfase);
                g2d.drawString(texto, xAjustado + desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, xAjustado - desfase, posicionesY[i] + desfase);
                g2d.drawString(texto, xAjustado + desfase, posicionesY[i] - desfase);

                g2d.setColor(Color.YELLOW); 
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

        // 3. NAVE ESPACIAL
        int anchoNave = 100;
        int altoNave = 100;
        int naveX = 500; 
        int naveY = (hoveredIndex != -1) ? (posicionesY[hoveredIndex] - (altoNave / 2) - 10) : 280;
        
        if (spriteNave != null) {
            g2d.drawImage(spriteNave, naveX, naveY, anchoNave, altoNave, null);
        } else {
            g2d.setColor(Color.MAGENTA);
            g2d.fillRect(naveX, naveY, anchoNave, altoNave);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("IMG FAIL", naveX + 25, naveY + 50);
        }
    }
}
