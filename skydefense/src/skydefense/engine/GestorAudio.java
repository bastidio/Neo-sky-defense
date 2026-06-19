package skydefense.engine;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GestorAudio {

    private static GestorAudio instancia;

    // --- Clips de Música ---
    private Clip musicaPresentacion;
    private Clip musicaLogo;
    private Clip musicaJuego;
    private Clip musicaLogoProgramadores;

    // --- Clips de Efectos de Sonido (SFX) ---
    private Clip sonidoDisparo;
    private Clip sonidoVidaExtra;
    private Clip sonidoGameOver;

    private GestorAudio() {
        // Constructor privado para el patrón Singleton
    }

    public static GestorAudio getInstancia() {
        if (instancia == null) {
            instancia = new GestorAudio();
        }
        return instancia;
    }

    private Clip cargarClip(String ruta) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(ruta));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (Exception e) {
            System.err.println("No se pudo cargar el audio: " + ruta);
            return null;
        }
    }

    public void setVolumen(Clip clip, float volumen) {
        if (clip == null) {
            return;
        }

        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float min = control.getMinimum();
        float max = control.getMaximum();

        if (volumen <= 0f) {
            volumen = 0.0001f;
        }

        float ganancia = (float) (20.0 * Math.log10(volumen));

        if (ganancia < min) {
            ganancia = min;
        }

        if (ganancia > max) {
            ganancia = max;
        }

        control.setValue(ganancia);
    }

    // --- Efectos de Sonido (SFX) ---

    public void reproducirSonidoDisparo() {
        if (!ConfigJuego.sfxActivo) return;
        try {
            if (sonidoDisparo != null) {
                sonidoDisparo.stop();
                sonidoDisparo.close();
            }
            sonidoDisparo = cargarClip("skydefense/res/sfx/disparos.wav");
            if (sonidoDisparo != null) sonidoDisparo.start();
        } catch (Exception e) {
            System.err.println("No se pudo reproducir disparos.wav");
        }
    }

    public void reproducirSonidoVidaExtra() {
        if (!ConfigJuego.sfxActivo) return;
        try {
            if (sonidoVidaExtra != null) {
                sonidoVidaExtra.stop();
                sonidoVidaExtra.close();
            }
            sonidoVidaExtra = cargarClip("skydefense/res/sfx/ganarVida.wav");
            if (sonidoVidaExtra != null) sonidoVidaExtra.start();
        } catch (Exception e) {
            System.err.println("No se pudo reproducir ganarVida.wav");
        }
    }

    public void reproducirSonidoGameOver() {
        if (!ConfigJuego.sfxActivo) return;
        try {
            if (sonidoGameOver != null) {
                sonidoGameOver.stop();
                sonidoGameOver.close();
            }
            sonidoGameOver = cargarClip("skydefense/res/sfx/juegoPerdido.wav");
            if (sonidoGameOver != null) sonidoGameOver.start();
        } catch (Exception e) {
            System.err.println("No se pudo reproducir juegoPerdido.wav");
        }
    }

    // --- Música de Fondo ---

    public void reproducirMusicaPresentacion() {
        if (!ConfigJuego.musicaActiva) return;
        detenerMusicaPresentacion();
        musicaPresentacion = cargarClip("skydefense/res/sfx/introPresentacion.wav");
        if (musicaPresentacion != null) {
            setVolumen(musicaPresentacion, 1f);
            musicaPresentacion.start();
        }
    }

    public void reproducirMusicaLogo() {
        if (!ConfigJuego.musicaActiva) return;
        detenerMusicaLogo();
        musicaLogo = cargarClip("skydefense/res/sfx/musicaLogo.wav");
        if (musicaLogo != null) {
            setVolumen(musicaLogo, 1f);
            musicaLogo.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void reproducirMusicaLogoProgramadores() {
        if (!ConfigJuego.musicaActiva) return;
        detenerMusicaLogoProgramadores();
        musicaLogoProgramadores = cargarClip("skydefense/res/sfx/musicaLogoProgramadores.wav");
        if (musicaLogoProgramadores != null) {
            setVolumen(musicaLogoProgramadores, 1f);
            musicaLogoProgramadores.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void reproducirMusicaJuego() {
        if (!ConfigJuego.musicaActiva) return;
        detenerMusicaJuego();
        musicaJuego = cargarClip("skydefense/res/sfx/musicaJuego.wav");
        if (musicaJuego != null) {
            setVolumen(musicaJuego, 1f);
            musicaJuego.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // --- Controles de Música (Pausa/Detener) ---

    public void pausarMusicaJuego() {
        if (musicaJuego != null && musicaJuego.isRunning()) {
            musicaJuego.stop();
        }
    }

    public void reanudarMusicaJuego() {
        if (musicaJuego != null && !musicaJuego.isRunning() && ConfigJuego.musicaActiva) {
            musicaJuego.start();
            musicaJuego.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void detenerMusicaPresentacion() {
        if (musicaPresentacion != null) {
            musicaPresentacion.stop();
            musicaPresentacion.close();
            musicaPresentacion = null;
        }
    }

    public void detenerMusicaLogo() {
        if (musicaLogo != null) {
            musicaLogo.stop();
            musicaLogo.close();
            musicaLogo = null;
        }
    }

    public void detenerMusicaLogoProgramadores() {
        if (musicaLogoProgramadores != null) {
            musicaLogoProgramadores.stop();
            musicaLogoProgramadores.close();
            musicaLogoProgramadores = null;
        }
    }

    public void detenerMusicaJuego() {
        if (musicaJuego != null) {
            musicaJuego.stop();
            musicaJuego.close();
            musicaJuego = null;
        }
    }
    
    public void detenerTodo() {
        detenerMusicaPresentacion();
        detenerMusicaLogo();
        detenerMusicaLogoProgramadores();
        detenerMusicaJuego();
    }
}