import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private boolean isHovered = false;

    public RoundedButton(String text, Color bgColor, Color hoverColor) {
        super(text);
        this.backgroundColor = bgColor;
        this.hoverColor = hoverColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color currentColor = isHovered ? hoverColor : backgroundColor;
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        // Add subtle shadow when hovered
        if (isHovered) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 2, getWidth(), getHeight(), 25, 25);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}