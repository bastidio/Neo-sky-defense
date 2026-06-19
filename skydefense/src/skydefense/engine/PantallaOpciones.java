package skydefense.engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Estado que representa el menú de opciones.
 * Permite configurar el audio, los efectos de sonido y la pantalla completa.
 */
public class PantallaOpciones implements EstadoPantalla {

    private Menu menuContexto;
    private GestorRecursos recursos;
    private GestorAudio audio;

    private int opcionSeleccionada = 0;

    // Hitboxes de los botones de opciones
    private Rectangle botonMusic = new Rectangle();
    private Rectangle botonSfx = new Rectangle();
    private Rectangle botonFullscreen = new Rectangle();
    private Rectangle botonBackOptions = new Rectangle();

    public PantallaOpciones(Menu menuContexto) {
        this.menuContexto = menuContexto;
        this.recursos = GestorRecursos.getInstancia();
        this.audio = GestorAudio.getInstancia();
    }

    @Override
    public void actualizar() {
        // En la pantalla de opciones original no hay animaciones de físicas que actualizar por frame
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        int anchoPantalla = menuContexto.getWidth();
        int altoPantalla = menuContexto.getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, anchoPantalla, altoPantalla);

        // 1. Dibujar Título
        g2d.setFont(recursos.getFuenteTitulo().deriveFont(52f));
        g2d.setColor(Color.ORANGE);

        String titulo = "OPTIONS";
        int tituloX = (anchoPantalla - g2d.getFontMetrics().stringWidth(titulo)) / 2;
        g2d.drawString(titulo, tituloX, altoPantalla / 6);

        // 2. Configurar dimensiones y posiciones de los botones
        int anchoBoton = 420;
        int altoBoton = 55;
        int x = (anchoPantalla - anchoBoton) / 2;
        int y = altoPantalla / 4;
        int separacion = 75;

        botonMusic.setBounds(x, y, anchoBoton, altoBoton);
        botonSfx.setBounds(x, y + separacion, anchoBoton, altoBoton);
        botonFullscreen.setBounds(x, y + separacion * 2, anchoBoton, altoBoton);
        botonBackOptions.setBounds(x, y + separacion * 3, anchoBoton, altoBoton);

        // 3. Dibujar cada botón con su estado actual extraído de ConfigJuego y Menu
        dibujarBoton(g2d, botonMusic, "MUSIC: " + (ConfigJuego.musicaActiva ? "ON" : "OFF"), opcionSeleccionada == 0);
        dibujarBoton(g2d, botonSfx, "SFX: " + (ConfigJuego.sfxActivo ? "ON" : "OFF"), opcionSeleccionada == 1);
        dibujarBoton(g2d, botonFullscreen, menuContexto.isPantallaCompleta() ? "WINDOWED" : "FULLSCREEN", opcionSeleccionada == 2);
        dibujarBoton(g2d, botonBackOptions, "BACK", opcionSeleccionada == 3);
    }

    private void dibujarBoton(Graphics2D g2d, Rectangle boton, String texto, boolean seleccionado) {
        // Fondo del botón
        if (seleccionado) {
            g2d.setColor(Color.ORANGE);
        } else {
            g2d.setColor(new Color(25, 25, 25));
        }
        g2d.fillRoundRect(boton.x, boton.y, boton.width, boton.height, 20, 20);

        // Borde del botón
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(boton.x, boton.y, boton.width, boton.height, 20, 20);

        // Texto del botón
        g2d.setFont(recursos.getFuenteNormal().deriveFont(24f));
        if (seleccionado) {
            g2d.setColor(Color.BLACK);
        } else {
            g2d.setColor(Color.WHITE);
        }

        int textoX = boton.x + (boton.width - g2d.getFontMetrics().stringWidth(texto)) / 2;
        int textoY = boton.y + 37;
        g2d.drawString(texto, textoX, textoY);
    }

    @Override
    public void teclaPresionada(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP) {
            opcionSeleccionada--;
            if (opcionSeleccionada < 0) {
                opcionSeleccionada = 3;
            }
            menuContexto.repaint();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            opcionSeleccionada++;
            if (opcionSeleccionada > 3) {
                opcionSeleccionada = 0;
            }
            menuContexto.repaint();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            ejecutarOpcion(opcionSeleccionada);
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            volverAlMenu();
        } else if (keyCode == KeyEvent.VK_F11) {
            menuContexto.alternarPantallaCompleta();
        }
    }

    @Override
    public void clickRaton(MouseEvent e) {
        Point click = e.getPoint();

        if (botonMusic.contains(click)) {
            ejecutarOpcion(0);
        } else if (botonSfx.contains(click)) {
            ejecutarOpcion(1);
        } else if (botonFullscreen.contains(click)) {
            ejecutarOpcion(2);
        } else if (botonBackOptions.contains(click)) {
            ejecutarOpcion(3);
        }
    }

    @Override
    public void mouseMovido(MouseEvent e) {
        Point raton = e.getPoint();
        int hoverAnterior = opcionSeleccionada;

        if (botonMusic.contains(raton)) opcionSeleccionada = 0;
        else if (botonSfx.contains(raton)) opcionSeleccionada = 1;
        else if (botonFullscreen.contains(raton)) opcionSeleccionada = 2;
        else if (botonBackOptions.contains(raton)) opcionSeleccionada = 3;

        // Solo repintamos si el ratón cambió de botón para ahorrar recursos
        if (hoverAnterior != opcionSeleccionada) {
            menuContexto.repaint();
        }
    }

    private void ejecutarOpcion(int index) {
        switch (index) {
            case 0: // MUSIC
                // Invertimos el estado de la música
                ConfigJuego.musicaActiva = !ConfigJuego.musicaActiva;
                
                // Le decimos al GestorAudio que actúe en consecuencia
                if (ConfigJuego.musicaActiva) {
                    audio.reproducirMusicaLogo();
                } else {
                    audio.detenerMusicaLogo();
                }
                menuContexto.repaint();
                break;
                
            case 1: // SFX
                // Invertimos el estado de los efectos de sonido
                ConfigJuego.sfxActivo = !ConfigJuego.sfxActivo;
                menuContexto.repaint();
                break;
                
            case 2: // FULLSCREEN
                // Llamamos al método que ya tienes en Menu.java
                menuContexto.alternarPantallaCompleta();
                menuContexto.repaint();
                break;
                
            case 3: // BACK
                // Usamos el método que ya tienes definido abajo
                volverAlMenu();
                break;
        }
    }
    private void volverAlMenu() {
        menuContexto.cambiarPantalla(new PantallaPrincipal(menuContexto));
    }
}