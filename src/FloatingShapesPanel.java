import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class FloatingShapesPanel extends JPanel {
    private Timer animationTimer;
    private List<Shape> shapes;
    private List<Point> positions;
    private List<Color> colors;

    public FloatingShapesPanel() {
        setOpaque(false);
        shapes = new ArrayList<>();
        positions = new ArrayList<>();
        colors = new ArrayList<>();

        // Create floating shapes
        for (int i = 0; i < 6; i++) {
            shapes.add(new Ellipse2D.Double(0, 0, 20 + (i * 5), 20 + (i * 5)));
            positions.add(new Point(
                    (int)(Math.random() * 600),
                    (int)(Math.random() * 400)
            ));
            colors.add(new Color(
                    (int)(Math.random() * 255),
                    (int)(Math.random() * 255),
                    (int)(Math.random() * 255),
                    100
            ));
        }

        animationTimer = new Timer(100, e -> {
            for (Point pos : positions) {
                pos.x += (Math.random() - 0.5) * 4;
                pos.y += (Math.random() - 0.5) * 4;

                // Keep shapes on screen
                if (pos.x < 0) pos.x = getWidth();
                if (pos.x > getWidth()) pos.x = 0;
                if (pos.y < 0) pos.y = getHeight();
                if (pos.y > getHeight()) pos.y = 0;
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < shapes.size(); i++) {
            g2.setColor(colors.get(i));
            Point pos = positions.get(i);
            Shape shape = shapes.get(i);
            g2.translate(pos.x, pos.y);
            g2.fill(shape);
            g2.translate(-pos.x, -pos.y);
        }
        g2.dispose();
    }
}