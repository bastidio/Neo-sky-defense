package skydefense.engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import skydefense.model.Puntuaciones;

public class PantallaLeaderboard implements EstadoPantalla {

    private Menu menuContexto;
    private GestorRecursos recursos;

    public PantallaLeaderboard(Menu menuContexto) {
        this.menuContexto = menuContexto;
        this.recursos = GestorRecursos.getInstancia();
    }

    @Override
    public void actualizar() { }

    @Override
    public void dibujar(Graphics2D g2d) {
        int anchoPantalla = menuContexto.getWidth();
        int altoPantalla = menuContexto.getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, anchoPantalla, altoPantalla);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(recursos.getFuenteTitulo().deriveFont(48f));
        String titulo = "LEADERBOARD";
        int tituloX = (anchoPantalla - g2d.getFontMetrics().stringWidth(titulo)) / 2;
        g2d.drawString(titulo, tituloX, 100);

        g2d.setColor(Color.WHITE);
        g2d.setFont(recursos.getFuenteNormal().deriveFont(24f));
        String subTitulo = "Press ESC to return";
        int subX = (anchoPantalla - g2d.getFontMetrics().stringWidth(subTitulo)) / 2;
        g2d.drawString(subTitulo, subX, altoPantalla - 50);
        
        
        g2d.setFont(recursos.getFuenteNormal().deriveFont(28f));
        int yInicial = 180;  // Altura base debajo del título
        int deltaY = 40;     // Espaciado vertical entre filas

        int posicion = 1;

        for (Puntuaciones.Puntaje p : Puntuaciones.getInstancia().getPuntajes()) {

            String texto = posicion + ". " + p.name + "   " + p.score;

            int puntajeX =
                    (anchoPantalla - g2d.getFontMetrics().stringWidth(texto)) / 2;

            if (posicion == 1)
                g2d.setColor(Color.GREEN);
            else
                g2d.setColor(Color.LIGHT_GRAY);

            g2d.drawString(texto, puntajeX, yInicial);

            yInicial += deltaY;
            posicion++;
        }
    }

    @Override
    public void teclaPresionada(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            menuContexto.cambiarPantalla(new PantallaPrincipal(menuContexto));
        }
    }

    @Override
    public void clickRaton(MouseEvent e) { }

    @Override
    public void mouseMovido(MouseEvent e) { }
}