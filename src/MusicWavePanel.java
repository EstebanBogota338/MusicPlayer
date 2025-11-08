import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MusicWavePanel extends JPanel {
    private float[] bars;
    private float time = 0;
    private double currentVolume = 0.3;
    private Timer animationTimer;
    private boolean isPlaying = false;

    // Colores del tema negro y dorado
    private final Color GOLD = new Color(212, 175, 55);
    private final Color LIGHT_GOLD = new Color(230, 190, 80);
    private final Color DARK_GOLD = new Color(180, 150, 45);
    private final Color PURPLE = new Color(139, 92, 246);

    public MusicWavePanel() {
        bars = new float[48]; // Más barras = más detallado
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(600, 120));
        setMaximumSize(new Dimension(600, 120));
        startAnimation();
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
        if (playing) {
            currentVolume = 0.6; // Más energía cuando está reproduciendo
        } else {
            currentVolume = 0.3; // Más calmado cuando está pausado/parado
        }
    }

    public void setVolume(double volume) {
        this.currentVolume = Math.max(0.1, Math.min(1.0, volume));
    }

    private void startAnimation() {
        animationTimer = new Timer(40, e -> { // 25 FPS suave
            updateBars();
            repaint();
        });
        animationTimer.start();
    }

    private void updateBars() {
        time += 0.08F;

        // Base del movimiento - más energía si está reproduciendo
        double energy = isPlaying ? 0.7 : 0.3;
        double baseAmplitude = energy + 0.2 * Math.sin(time * 0.3);

        for (int i = 0; i < bars.length; i++) {
            // Fórmula que crea movimiento orgánico y musical
            double position = i / (double) bars.length;
            float barValue = (float) (
                    currentVolume * baseAmplitude *
                            (0.6 + 0.4 * Math.sin(time * 2 + i * 0.2)) * // Frecuencia base
                            (0.8 + 0.2 * Math.sin(time * 5 + i * 0.5)) * // Armónicos
                            (0.9 + 0.1 * Math.sin(time * 0.7 + position * Math.PI * 2)) // Movimiento global
            );

            // Suavizar transiciones
            bars[i] = bars[i] * 0.7f + barValue * 0.3f;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;
        int barWidth = Math.max(2, width / bars.length - 1);

        // Sombra de fondo sutil
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillRect(0, 0, width, height);

        // Dibujar cada barra con efecto 3D
        for (int i = 0; i < bars.length; i++) {
            int x = i * (barWidth + 1);
            int barHeight = (int) (bars[i] * height * 0.45); // 90% del alto total

            if (barHeight > 2) { // Solo dibujar si tiene altura significativa
                // Gradiente vertical dorado
                GradientPaint gradient = new GradientPaint(
                        x, centerY - barHeight, LIGHT_GOLD,
                        x, centerY + barHeight, isPlaying ? PURPLE : DARK_GOLD
                );

                g2d.setPaint(gradient);

                // Barra redondeada
                RoundRectangle2D bar = new RoundRectangle2D.Float(
                        x, centerY - barHeight, barWidth, barHeight * 2, 6, 6
                );
                g2d.fill(bar);

                // Highlight sutil en la parte superior
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.drawRoundRect(x, centerY - barHeight, barWidth, barHeight * 2, 6, 6);
            }
        }

        // Línea central sutil
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(0, centerY, width, centerY);
    }

    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}
