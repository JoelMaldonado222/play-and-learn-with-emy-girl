import javax.swing.*;
import java.awt.*;

public class AnimatedTitleLabel extends JLabel {
    private Timer pulseTimer;
    private float alpha = 1.0f;
    private boolean increasing = false;

    public AnimatedTitleLabel(String text) {
        super(text, JLabel.CENTER);
        setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        setForeground(new Color(255, 107, 107));

        pulseTimer = new Timer(50, e -> {
            if (increasing) {
                alpha += 0.02f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    increasing = false;
                }
            } else {
                alpha -= 0.02f;
                if (alpha <= 0.7f) {
                    alpha = 0.7f;
                    increasing = true;
                }
            }
            repaint();
        });
        pulseTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2);
        g2.dispose();
    }
}