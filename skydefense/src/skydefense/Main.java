package skydefense;

import skydefense.engine.Menu;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Creamos la ventana principal
            JFrame ventana = new JFrame("Sky Defense - Menú Arcade");
            ventana.setSize(800, 600); 
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLocationRelativeTo(null); 
            ventana.setResizable(false); 

            // Instanciamos el panel del menú desde el paquete engine
            Menu panelMenu = new Menu();
            ventana.add(panelMenu);
            
            // Mostramos la ventana
            ventana.setVisible(true);
        });
    }
}
