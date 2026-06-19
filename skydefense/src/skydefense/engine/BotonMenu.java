package skydefense.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Componente reutilizable que representa un botón de texto interactivo en el menú.
 * Encapsula su hitbox, estado de selección y la lógica exacta de renderizado original.
 */
public class BotonMenu {

    private String texto;
    private int x;
    private int y;
    private Rectangle hitbox;
    private boolean isHovered;

    /**
     * Construye un botón de menú con su texto identificador.
     * @param texto Texto que se mostrará en pantalla (ej: "PLAY", "EXIT").
     */
    public BotonMenu(String texto) {
        this.texto = texto;
        this.hitbox = new Rectangle();
        this.isHovered = false;
    }

    /**
     * Actualiza la posición y recalcula las dimensiones de la hitbox del botón.
     * Esto permite que el botón responda correctamente a cambios de resolución o pantalla completa.
     * * @param x Coordenada X base para el renderizado.
     * @param y Coordenada Y (línea de base del texto).
     * @param fm FontMetrics de la fuente normal para calcular el ancho real del texto.
     */
    public void actualizarUbicacion(int x, int y, FontMetrics fm) {
        this.x = x;
        this.y = y;
        
        int anchoTexto = fm.stringWidth(texto);
        int altoTexto = fm.getHeight();
        
        // Conserva el desfase exacto de -45 píxeles vertical utilizado en el bucle original
        this.hitbox.setBounds(x, y - 45, anchoTexto, altoTexto);
    }

    /**
     * Evalúa si las coordenadas actuales del cursor se encuentran dentro de la hitbox.
     * @param mouseX Coordenada X del ratón.
     * @param mouseY Coordenada Y del ratón.
     */
    public void actualizarEstado(int mouseX, int mouseY) {
        this.isHovered = hitbox.contains(mouseX, mouseY);
    }

    /**
     * Dibuja el botón aplicando las reglas estricta de diseño:
     * Efecto hover en naranja con reajuste tipográfico central o texto blanco con resplandor.
     */
    public void dibujar(Graphics2D g2d, Font fuenteNormal, Font fuenteHover, boolean pantallaCompleta) {
        if (isHovered) {
            // Configuración exacta para el estado seleccionado (Hover)
            if (pantallaCompleta) {
                g2d.setFont(fuenteHover.deriveFont(65f));
            } else {
                g2d.setFont(fuenteHover);
            }
            g2d.setColor(Color.ORANGE);

            FontMetrics fmHover = g2d.getFontMetrics();
            int anchoNormal = hitbox.width;
            int anchoHover = fmHover.stringWidth(texto);
            
            // Reajuste horizontal exacto para centrar el escalado de la fuente hover
            int xAjustado = x - ((anchoHover - anchoNormal) / 2);

            g2d.drawString(texto, xAjustado, y);
        } else {
            // Configuración exacta para el estado normal (Con resplandor/sombra)
            if (pantallaCompleta) {
                g2d.setFont(fuenteNormal.deriveFont(55f));
            } else {
                g2d.setFont(fuenteNormal);
            }

            // Capa de resplandor translúcido (Alfa: 60)
            Color blancoResplandor = new Color(255, 255, 255, 60);
            g2d.setColor(blancoResplandor);

            int desfase = 2;
            // Dibujado periférico de 4 puntos para simular el brillo original
            g2d.drawString(texto, x - desfase, y - desfase);
            g2d.drawString(texto, x + desfase, y + desfase);
            g2d.drawString(texto, x - desfase, y + desfase);
            g2d.drawString(texto, x + desfase, y - desfase);

            // Capa de texto frontal sólida
            g2d.setColor(Color.WHITE);
            g2d.drawString(texto, x, y);
        }
    }

    // Getters necesarios para la validación externa de eventos de clics
    public String getTexto() { return texto; }
    public boolean isHovered() { return isHovered; }
    public Rectangle getHitbox() { return hitbox; }
}