package skydefense;

import skydefense.engine.Menu;
import javax.swing.*;
import javax.swing.*;
import javax.swing.*;
import java.awt.Image;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Creamos la ventana principal ÚNICA del juego
            JFrame ventana = new JFrame("Sky Defense - Menú Arcade");
            ventana.setSize(800, 600); 
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLocationRelativeTo(null); 
            ventana.setResizable(false); 

            Image iconoVentana = new ImageIcon("skydefense/res/sprite/nave.png").getImage();
            ventana.setIconImage(iconoVentana);

            // Instanciamos el panel del menú (que ahora es un JPanel)
            Menu panelMenu = new Menu();
            
            // Lo añadimos a la ventana
            ventana.add(panelMenu);
            
            // Mostramos todo
            ventana.setVisible(true);
        });
    }
}
