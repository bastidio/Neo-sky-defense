package skydefense.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Nave extends ObjetoVolador {

    public enum MovimientoHorizontal { IZQUIERDA, DERECHA, REPOSO }
    public enum MovimientoVertical { SUBIR, BAJAR, REPOSO }

    private MovimientoHorizontal estadoHorizontal = MovimientoHorizontal.REPOSO;
    private MovimientoVertical estadoVertical = MovimientoVertical.REPOSO;

    private int energia;
    private int altitud; // Lo dejamos declarado por si algo más lo consulta
    private BufferedImage sprite;

    // Subimos la velocidad un poco para que el vuelo libre se sienta ágil
    private double velocidadMovimiento = 350; 

    private RenderizadorNave renderer;

    public Nave(BufferedImage sprite, int anchoPantalla, int altoPantalla) {
        this.sprite = sprite;
        this.posicionX = anchoPantalla / 2.0;
        this.posicionY = altoPantalla - 100; // Arranca cerca del piso
        this.altitud = 3000;
        this.energia = 100;
        this.renderer = new RenderizadorNave();
        this.ancho = 75;
        this.alto = 75;
    }

    @Override
    public void update(double delta) {
        if (estadoHorizontal == MovimientoHorizontal.IZQUIERDA) posicionX -= velocidadMovimiento * delta;
        if (estadoHorizontal == MovimientoHorizontal.DERECHA) posicionX += velocidadMovimiento * delta;

        // Modificamos la posición Y real para volar por la pantalla
        if (estadoVertical == MovimientoVertical.SUBIR) posicionY -= velocidadMovimiento * delta;
        if (estadoVertical == MovimientoVertical.BAJAR) posicionY += velocidadMovimiento * delta;
    }

    public void limitarPantalla(int anchoPantalla, int altoPantalla) {
        if (posicionX < 50) posicionX = 50;
        if (posicionX > anchoPantalla - 50) posicionX = anchoPantalla - 50;

        // Pared invisible arriba para no pisar a los drones
        if (posicionY < 300) posicionY = 300;
        
        // Tope en el piso (borde inferior de la ventana)
        if (posicionY > altoPantalla - 50) posicionY = altoPantalla - 50;
    }

    public void reducirEnergia(int cantidadDanio) { energia -= cantidadDanio; }
    public boolean energiaAgotada() { return energia <= 0; }
    public void restaurarEnergia() { energia = 100; }
    public int getEnergia() { return energia; }
    public int getAltitud() { return altitud; }

    public void setEstadoHorizontal(MovimientoHorizontal estado) { this.estadoHorizontal = estado; }
    public void setEstadoVertical(MovimientoVertical estado) { this.estadoVertical = estado; }

    @Override
    public void draw(Graphics2D g2d, int anchoPantalla, int altoPantalla) {
        renderer.draw(g2d, this); // Borramos el candado que forzaba la posición Y en pantalla
    }

    public BufferedImage getSprite() { return sprite; }
}