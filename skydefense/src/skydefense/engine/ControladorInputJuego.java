package skydefense.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControladorInputJuego extends MouseAdapter {

    private PanelJuego panel;
    private MotorJuego motor;
    private RenderizadorJuego renderizador;

    public ControladorInputJuego(PanelJuego panel, MotorJuego motor, RenderizadorJuego renderizador) {
        this.panel = panel;
        this.motor = motor;
        this.renderizador = renderizador;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        panel.requestFocusInWindow();

        if (motor.isJuegoTerminado() && renderizador.getBotonVolverMenu().contains(e.getPoint())) {
            panel.volverAlMenu();
            return;
        }

        if (motor.isJuegoPausado()) {
            if (motor.isConfirmandoVolverMenu()) {
                if (renderizador.getBotonConfirmYes().contains(e.getPoint())) panel.volverAlMenu();
                else if (renderizador.getBotonConfirmNo().contains(e.getPoint())) motor.cancelarConfirmacionVolverMenu();
            } else {
                if (renderizador.getBotonResume().contains(e.getPoint())) motor.reanudarJuego();
                else if (renderizador.getBotonBackToMenu().contains(e.getPoint())) motor.pedirConfirmacionVolverMenu();
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (motor.isJuegoTerminado() && (tecla == KeyEvent.VK_SPACE || tecla == KeyEvent.VK_ESCAPE)) {
            panel.volverAlMenu();
            return;
        }

        if (tecla == KeyEvent.VK_F11) {
            panel.alternarPantallaCompleta();
            return;
        }

        if (tecla == KeyEvent.VK_ESCAPE || tecla == KeyEvent.VK_P) {
            motor.alternarPausa();
            return;
        }

        if (motor.isJuegoPausado()) {
            manejarTecladoPausa(tecla);
            return;
        }

        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) motor.getNave().setIzquierda(true);
        if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) motor.getNave().setDerecha(true);
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) motor.getNave().setSubir(true);
        if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) motor.getNave().setBajar(true);
    }

    public void keyReleased(KeyEvent e) {
        if (motor.isJuegoPausado()) return;

        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) motor.getNave().setIzquierda(false);
        if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) motor.getNave().setDerecha(false);
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) motor.getNave().setSubir(false);
        if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) motor.getNave().setBajar(false);
    }

    private void manejarTecladoPausa(int tecla) {
        if (motor.isConfirmandoVolverMenu()) {
            if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_DOWN) {
                motor.setOpcionConfirmacionSeleccionada(motor.getOpcionConfirmacionSeleccionada() == 0 ? 1 : 0);
            } else if (tecla == KeyEvent.VK_ENTER) {
                if (motor.getOpcionConfirmacionSeleccionada() == 0) panel.volverAlMenu();
                else motor.cancelarConfirmacionVolverMenu();
            } else if (tecla == KeyEvent.VK_ESCAPE) {
                motor.cancelarConfirmacionVolverMenu();
            }
        } else {
            if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_DOWN) {
                motor.setOpcionPausaSeleccionada(motor.getOpcionPausaSeleccionada() == 0 ? 1 : 0);
            } else if (tecla == KeyEvent.VK_ENTER) {
                if (motor.getOpcionPausaSeleccionada() == 0) motor.reanudarJuego();
                else motor.pedirConfirmacionVolverMenu();
            }
        }
    }
}