package skydefense.engine;

import skydefense.model.Explosion;
import skydefense.model.Misil;
import java.awt.*;

public class RenderizadorJuego {

    private Rectangle botonVolverMenu = new Rectangle();
    private Rectangle botonResume = new Rectangle();
    private Rectangle botonBackToMenu = new Rectangle();
    private Rectangle botonConfirmYes = new Rectangle();
    private Rectangle botonConfirmNo = new Rectangle();

    public void dibujar(Graphics2D g2d, int width, int height, MotorJuego motor) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        if (motor.isJuegoTerminado()) {
            dibujarFinJuego(g2d, width, height, motor);
            return;
        }

        motor.getNave().draw(g2d, width, height);
        motor.getEscuadron().draw(g2d, width, height);

        for (Misil misil : motor.getMisiles()) misil.draw(g2d, width, height);
        for (Explosion explosion : motor.getExplosiones()) explosion.draw(g2d);

        dibujarHUD(g2d, motor);

        if (motor.isJuegoPausado()) {
            dibujarMenuPausa(g2d, width, height, motor);
        }
    }

    private void dibujarHUD(Graphics2D g2d, MotorJuego motor) {
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);

        g2d.drawString("LIVES: " + motor.getJugador().getVidas(), 20, 30);
        g2d.drawString("SCORE: " + motor.getJugador().getPuntos(), 20, 55);
        g2d.drawString("ENERGY: " + motor.getNave().getEnergia(), 20, 80);
        g2d.drawString("DRONES REMAINING: " + motor.getEscuadron().getDronesRestantes(), 20, 105);
        g2d.drawString("LEVEL: " + motor.getNivel().getNumero(), 20, 130);
    }

    private void dibujarMenuPausa(Graphics2D g2d, int width, int height, MotorJuego motor) {
        g2d.setColor(new Color(0, 0, 0, 190));
        g2d.fillRect(0, 0, width, height);

        if (motor.isConfirmandoVolverMenu()) {
            dibujarConfirmacionVolverMenu(g2d, width, height, motor);
            return;
        }

        int boxW = 470;
        int boxH = 260;
        int boxX = (width - boxW) / 2;
        int boxY = (height - boxH) / 2;

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);
        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);

        g2d.setFont(new Font("Arial", Font.BOLD, 42));
        g2d.setColor(Color.ORANGE);
        String titulo = "PAUSED";
        g2d.drawString(titulo, boxX + (boxW - g2d.getFontMetrics().stringWidth(titulo)) / 2, boxY + 65);

        botonResume.setBounds(boxX + 85, boxY + 105, 300, 50);
        botonBackToMenu.setBounds(boxX + 85, boxY + 170, 300, 50);

        dibujarBoton(g2d, botonResume, "RESUME", motor.getOpcionPausaSeleccionada() == 0);
        dibujarBoton(g2d, botonBackToMenu, "BACK TO MENU", motor.getOpcionPausaSeleccionada() == 1);
    }

    private void dibujarConfirmacionVolverMenu(Graphics2D g2d, int width, int height, MotorJuego motor) {
        int boxW = 560;
        int boxH = 300;
        int boxX = (width - boxW) / 2;
        int boxY = (height - boxH) / 2;

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);
        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);

        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        g2d.setColor(Color.ORANGE);
        String titulo = "WARNING";
        g2d.drawString(titulo, boxX + (boxW - g2d.getFontMetrics().stringWidth(titulo)) / 2, boxY + 60);

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);
        String linea1 = "Current progress will be lost.";
        String linea2 = "Return to menu?";
        g2d.drawString(linea1, boxX + (boxW - g2d.getFontMetrics().stringWidth(linea1)) / 2, boxY + 110);
        g2d.drawString(linea2, boxX + (boxW - g2d.getFontMetrics().stringWidth(linea2)) / 2, boxY + 145);

        botonConfirmYes.setBounds(boxX + 130, boxY + 200, 120, 50);
        botonConfirmNo.setBounds(boxX + 310, boxY + 200, 120, 50);

        dibujarBoton(g2d, botonConfirmYes, "YES", motor.getOpcionConfirmacionSeleccionada() == 0);
        dibujarBoton(g2d, botonConfirmNo, "NO", motor.getOpcionConfirmacionSeleccionada() == 1);
    }

    private void dibujarFinJuego(Graphics2D g2d, int width, int height, MotorJuego motor) {
        if (motor.isVictoria() && GestorRecursos.getInstancia().getLogo() != null) {
            Image logo = GestorRecursos.getInstancia().getLogo();
            int altoLogo = 180;
            int anchoLogo = (int) ((double) logo.getWidth(null) / logo.getHeight(null) * altoLogo);
            g2d.drawImage(logo, (width - anchoLogo) / 2, 80, anchoLogo, altoLogo, null);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.WHITE);
        String texto = motor.isVictoria() ? "CONGRATULATIONS" : "GAME OVER";
        g2d.drawString(texto, (width - g2d.getFontMetrics().stringWidth(texto)) / 2, height / 2 + 20);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreTexto = "FINAL SCORE: " + motor.getJugador().getPuntos();
        g2d.drawString(scoreTexto, (width - g2d.getFontMetrics().stringWidth(scoreTexto)) / 2, height / 2 + 65);

        int bx = width / 2 - 150;
        int by = height / 2 + 110;
        botonVolverMenu.setBounds(bx, by, 300, 55);
        dibujarBoton(g2d, botonVolverMenu, "BACK TO MENU", false);
    }

    private void dibujarBoton(Graphics2D g2d, Rectangle boton, String texto, boolean seleccionado) {
        g2d.setColor(seleccionado ? Color.ORANGE : new Color(35, 35, 35));
        g2d.fillRoundRect(boton.x, boton.y, boton.width, boton.height, 18, 18);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(boton.x, boton.y, boton.width, boton.height, 18, 18);
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(seleccionado ? Color.BLACK : Color.WHITE);
        g2d.drawString(texto, boton.x + (boton.width - g2d.getFontMetrics().stringWidth(texto)) / 2, boton.y + 33);
    }

    // Getters para que el ControladorInputJuego sepa dónde clickeó el usuario
    public Rectangle getBotonVolverMenu() { return botonVolverMenu; }
    public Rectangle getBotonResume() { return botonResume; }
    public Rectangle getBotonBackToMenu() { return botonBackToMenu; }
    public Rectangle getBotonConfirmYes() { return botonConfirmYes; }
    public Rectangle getBotonConfirmNo() { return botonConfirmNo; }
}