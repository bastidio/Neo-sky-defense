package skydefense.engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Estado que representa el menú principal del juego.
 * Controla la lista de opciones, el minijuego de la nave y el cuadro de confirmación de salida.
 */
public class PantallaPrincipal implements EstadoPantalla {

    private Menu menuContexto;
    private GestorRecursos recursos;
    private GestorAudio audio;

    // Componentes visuales encapsulados
    private List<BotonMenu> botones;
    private NaveMenu naveMenu;

    // Variables de estado interno
    private int hoveredIndex = -1;
    private int mouseX = 500;
    private int mouseY = 280;

    // Lógica del cuadro de confirmación de salida
    private boolean mostrandoConfirmacionExit = false;
    private Rectangle botonYes = new Rectangle();
    private Rectangle botonNo = new Rectangle();

    public PantallaPrincipal(Menu menuContexto) {
        this.menuContexto = menuContexto;
        this.recursos = GestorRecursos.getInstancia();
        this.audio = GestorAudio.getInstancia();
        
        this.naveMenu = new NaveMenu(menuContexto);

        // Inicializamos los botones de forma limpia
        this.botones = new ArrayList<>();
        botones.add(new BotonMenu("PLAY"));
        botones.add(new BotonMenu("SCORES"));
        botones.add(new BotonMenu("OPTIONS"));
        botones.add(new BotonMenu("EXIT"));
    }

    @Override
    public void actualizar() {
        // Se encarga de mover los disparos en cada frame (16ms)
        if (!mostrandoConfirmacionExit) {
            naveMenu.actualizar();
        }
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        int anchoPantalla = menuContexto.getWidth();
        int altoPantalla = menuContexto.getHeight();

        // 1. Dibujar el Logo original
        BufferedImage logo = recursos.getLogo();
        if (logo != null) {
            int altoLogo = Math.max(140, altoPantalla / 4);
            int anchoLogo = (int) ((double) logo.getWidth() / logo.getHeight() * altoLogo);
            int logoX = anchoPantalla - anchoLogo - (anchoPantalla / 10);
            int logoY = altoPantalla / 12;
            g2d.drawImage(logo, logoX, logoY, anchoLogo, altoLogo, null);
        }

        // 2. Dibujar los Botones
        int menuX = anchoPantalla / 8;
        int primerY = altoPantalla / 3;
        int separacionY = altoPantalla / 9;

        FontMetrics fm = g2d.getFontMetrics(recursos.getFuenteNormal());

        for (int i = 0; i < botones.size(); i++) {
            BotonMenu boton = botones.get(i);
            int posicionY = primerY + (i * separacionY);
            
            // Actualizamos la hitbox por si la ventana cambió de tamaño
            boton.actualizarUbicacion(menuX, posicionY, fm);
            
            // Forzamos el estado visual si estamos navegando con las flechas del teclado
            boolean estaSeleccionado = (i == hoveredIndex) || boton.isHovered();

            // Usamos un pequeño "hack" temporal pasando el estado para respetar el teclado
            if (estaSeleccionado) {
                // Estado Hover (Naranja)
                if (menuContexto.isPantallaCompleta()) {
                    g2d.setFont(recursos.getFuenteHover().deriveFont(65f));
                } else {
                    g2d.setFont(recursos.getFuenteHover());
                }
                g2d.setColor(Color.ORANGE);
                int anchoNormal = boton.getHitbox().width;
                int anchoHover = g2d.getFontMetrics().stringWidth(boton.getTexto());
                int xAjustado = menuX - ((anchoHover - anchoNormal) / 2);
                g2d.drawString(boton.getTexto(), xAjustado, posicionY);
            } else {
                // Estado Normal (Blanco con resplandor)
                if (menuContexto.isPantallaCompleta()) {
                    g2d.setFont(recursos.getFuenteNormal().deriveFont(55f));
                } else {
                    g2d.setFont(recursos.getFuenteNormal());
                }
                Color blancoResplandor = new Color(255, 255, 255, 60);
                g2d.setColor(blancoResplandor);
                int desfase = 2;
                g2d.drawString(boton.getTexto(), menuX - desfase, posicionY - desfase);
                g2d.drawString(boton.getTexto(), menuX + desfase, posicionY + desfase);
                g2d.drawString(boton.getTexto(), menuX - desfase, posicionY + desfase);
                g2d.drawString(boton.getTexto(), menuX + desfase, posicionY - desfase);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(boton.getTexto(), menuX, posicionY);
            }
        }

        // 3. Dibujar la Nave apuntando al cursor
        naveMenu.dibujar(g2d, mouseX, mouseY);

        // 4. Dibujar Cuadro de Salida (Superpuesto)
        if (mostrandoConfirmacionExit) {
            dibujarConfirmacionExit(g2d);
        }
    }

    @Override
    public void mouseMovido(MouseEvent e) {
        if (mostrandoConfirmacionExit) return;

        mouseX = e.getX();
        mouseY = e.getY();

        int previousHover = hoveredIndex;
        hoveredIndex = -1;

        for (int i = 0; i < botones.size(); i++) {
            botones.get(i).actualizarEstado(mouseX, mouseY);
            if (botones.get(i).isHovered()) {
                hoveredIndex = i;
            }
        }

        if (previousHover != hoveredIndex) {
            menuContexto.repaint();
        }
    }

    @Override
    public void clickRaton(MouseEvent e) {
        if (mostrandoConfirmacionExit) {
            if (botonYes.contains(e.getPoint())) {
                System.exit(0);
            }
            if (botonNo.contains(e.getPoint())) {
                mostrandoConfirmacionExit = false;
                menuContexto.repaint();
            }
            return;
        }

        boolean clickEnBoton = false;
        for (int i = 0; i < botones.size(); i++) {
            if (botones.get(i).isHovered()) {
                ejecutarOpcion(i);
                clickEnBoton = true;
                break;
            }
        }

        // Si no se hizo clic en ningún botón, la nave dispara
        if (!clickEnBoton) {
            naveMenu.disparar(mouseX, mouseY);
        }
    }

    @Override
    public void teclaPresionada(KeyEvent e) {
        if (mostrandoConfirmacionExit) {
            if (e.getKeyCode() == KeyEvent.VK_Y || e.getKeyCode() == KeyEvent.VK_ENTER) {
                System.exit(0);
            }
            if (e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                mostrandoConfirmacionExit = false;
                menuContexto.repaint();
            }
            return;
        }

        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP) {
            hoveredIndex = (hoveredIndex <= 0) ? botones.size() - 1 : hoveredIndex - 1;
            menuContexto.repaint();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            hoveredIndex = (hoveredIndex >= botones.size() - 1) ? 0 : hoveredIndex + 1;
            menuContexto.repaint();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (hoveredIndex != -1) {
                ejecutarOpcion(hoveredIndex);
            }
        } else if (keyCode == KeyEvent.VK_F11) {
            menuContexto.alternarPantallaCompleta();
        }
    }

    private void ejecutarOpcion(int index) {
        switch (index) {
            case 0: // PLAY
                audio.detenerMusicaLogo();
                menuContexto.cambiarPantalla(new PantallaPresentacion(menuContexto));
                break;
            case 1: // SCORES
                menuContexto.cambiarPantalla(new PantallaLeaderboard(menuContexto));
                break;
            case 2: // OPTIONS
                menuContexto.cambiarPantalla(new PantallaOpciones(menuContexto));
                break;
            case 3: // EXIT
                mostrandoConfirmacionExit = true;
                menuContexto.repaint();
                break;
        }
    }

    private void dibujarConfirmacionExit(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 190));
        g2d.fillRect(0, 0, menuContexto.getWidth(), menuContexto.getHeight());

        int boxW = 520;
        int boxH = 210;
        int boxX = (menuContexto.getWidth() - boxW) / 2;
        int boxY = (menuContexto.getHeight() - boxH) / 2;

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);

        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);

        g2d.setFont(recursos.getFuenteNormal().deriveFont(28f));
        g2d.setColor(Color.WHITE);

        String linea1 = "Are you sure you want";
        String linea2 = "to exit the game?";

        int linea1X = boxX + (boxW - g2d.getFontMetrics().stringWidth(linea1)) / 2;
        int linea2X = boxX + (boxW - g2d.getFontMetrics().stringWidth(linea2)) / 2;

        g2d.drawString(linea1, linea1X, boxY + 75);
        g2d.drawString(linea2, linea2X, boxY + 115);

        botonYes.setBounds(boxX + 95, boxY + 140, 120, 45);
        botonNo.setBounds(boxX + 305, boxY + 140, 120, 45);

        g2d.setFont(recursos.getFuenteNormal().deriveFont(24f));

        g2d.setColor(Color.ORANGE);
        String yes = "YES";
        int yesX = botonYes.x + (botonYes.width - g2d.getFontMetrics().stringWidth(yes)) / 2;
        g2d.drawString(yes, yesX, botonYes.y + 32);

        g2d.setColor(Color.WHITE);
        String no = "NO";
        int noX = botonNo.x + (botonNo.width - g2d.getFontMetrics().stringWidth(no)) / 2;
        g2d.drawString(no, noX, botonNo.y + 32);
    }
}