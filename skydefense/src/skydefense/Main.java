package skydefense;

import skydefense.engine.Menu;
import javax.swing.*;
import javax.swing.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Creamos la ventana principal ÚNICA del juego
            JFrame ventana = new JFrame("Sky Defense - Menú Arcade");
            ventana.setSize(800, 600); 
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLocationRelativeTo(null); 
            ventana.setResizable(false); 

            // Instanciamos el panel del menú (que ahora es un JPanel)
            Menu panelMenu = new Menu();
            
            // Lo añadimos a la ventana
            ventana.add(panelMenu);
            
            // Mostramos todo
            ventana.setVisible(true);
        });
    }
}
