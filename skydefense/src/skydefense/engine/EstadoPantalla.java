package skydefense.engine;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface EstadoPantalla {

    void actualizar();

    void dibujar(Graphics2D g2d);

    void teclaPresionada(KeyEvent e);

    void clickRaton(MouseEvent e);

    void mouseMovido(MouseEvent e);
}