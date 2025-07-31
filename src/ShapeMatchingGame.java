import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;

public class ShapeMatchingGame extends JFrame {
    private DraggableShape draggedShape = null;
    private SoundPlayer soundPlayer;
    private boolean gameCompleted = false;

    public ShapeMatchingGame() {
        soundPlayer = new SoundPlayer();
        setupWindow();
        add(new GamePanel());
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("🟢 Shape Matching Game - Easy Mode");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Add return to menu button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(255, 248, 220));

        JButton backButton = new JButton("🏠 Back to Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 107, 107));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.addActionListener(e -> {
            dispose();
            new GameFrame();
        });

        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);
    }

    class GamePanel extends JPanel {
        private DraggableShape[] shapes;
        private TargetZone[] targets;
        private int correctMatches = 0;
        private final int TOTAL_SHAPES = 6;

        public GamePanel() {
            setBackground(new Color(255, 248, 220)); // Cream background
            initializeShapesAndTargets();
            setupMouseListeners();
        }

        private void initializeShapesAndTargets() {
            // Create 6 different shapes with different colors and types
            shapes = new DraggableShape[] {
                    new DraggableShape(80, 80, ShapeType.CIRCLE, new Color(255, 107, 107)), // Red circle
                    new DraggableShape(80, 180, ShapeType.SQUARE, new Color(76, 175, 80)), // Green square
                    new DraggableShape(80, 280, ShapeType.TRIANGLE, new Color(33, 150, 243)), // Blue triangle
                    new DraggableShape(80, 380, ShapeType.STAR, new Color(255, 152, 0)), // Orange star
                    new DraggableShape(80, 480, ShapeType.HEART, new Color(156, 39, 176)), // Purple heart
                    new DraggableShape(80, 580, ShapeType.DIAMOND, new Color(255, 193, 7)) // Yellow diamond
            };

            // Create corresponding target zones (scrambled order for challenge)
            ArrayList<TargetZone> targetList = new ArrayList<>();
            targetList.add(new TargetZone(650, 80, ShapeType.CIRCLE, new Color(255, 107, 107)));
            targetList.add(new TargetZone(650, 180, ShapeType.SQUARE, new Color(76, 175, 80)));
            targetList.add(new TargetZone(650, 280, ShapeType.TRIANGLE, new Color(33, 150, 243)));
            targetList.add(new TargetZone(650, 380, ShapeType.STAR, new Color(255, 152, 0)));
            targetList.add(new TargetZone(650, 480, ShapeType.HEART, new Color(156, 39, 176)));
            targetList.add(new TargetZone(650, 580, ShapeType.DIAMOND, new Color(255, 193, 7)));

            // Shuffle for randomness
            Collections.shuffle(targetList);
            targets = targetList.toArray(new TargetZone[0]);
        }

        private void setupMouseListeners() {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (gameCompleted) return;

                    for (DraggableShape shape : shapes) {
                        if (shape.contains(e.getPoint()) && !shape.isMatched()) {
                            draggedShape = shape;
                            break;
                        }
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (draggedShape != null && !gameCompleted) {
                        boolean matched = false;

                        for (TargetZone target : targets) {
                            if (target.contains(e.getPoint()) && !target.isOccupied() &&
                                    draggedShape.getShapeType() == target.getShapeType() &&
                                    draggedShape.getColor().equals(target.getColor())) {

                                // Successful match!
                                draggedShape.snapTo(target.getBounds());
                                draggedShape.setMatched(true);
                                target.setOccupied(true);
                                correctMatches++;
                                matched = true;

                                // Play success sound
                                playSuccessSound();

                                // Check if game is complete
                                if (correctMatches >= TOTAL_SHAPES) {
                                    gameCompleted = true;
                                    Timer delay = new Timer(500, evt -> showVictoryMessage());
                                    delay.setRepeats(false);
                                    delay.start();
                                }
                                break;
                            }
                        }

                        if (!matched) {
                            // Wrong match - play error sound
                            playErrorSound();

                            // Animate shape back to original position
                            animateShapeBack(draggedShape);
                        }

                        draggedShape = null;
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (draggedShape != null && !gameCompleted) {
                        draggedShape.setPosition(e.getX(), e.getY());
                        repaint();
                    }
                }
            });
        }

        private void animateShapeBack(DraggableShape shape) {
            Point originalPos = shape.getOriginalPosition();
            Point currentPos = new Point(shape.getX(), shape.getY());

            Timer animTimer = new Timer(20, null);
            animTimer.addActionListener(e -> {
                double progress = 0.1; // Speed of animation
                int newX = (int)(currentPos.x + (originalPos.x - currentPos.x) * progress);
                int newY = (int)(currentPos.y + (originalPos.y - currentPos.y) * progress);

                shape.setPosition(newX + shape.getSize()/2, newY + shape.getSize()/2);
                repaint();

                if (Math.abs(newX - originalPos.x) < 5 && Math.abs(newY - originalPos.y) < 5) {
                    shape.resetToOriginal();
                    animTimer.stop();
                    repaint();
                }
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw title and instructions
            drawInstructions(g2d);

            // Draw target zones with labels
            drawTargetZones(g2d);

            // Draw shapes
            drawShapes(g2d);

            // Draw progress
            drawProgress(g2d);
        }

        private void drawInstructions(Graphics2D g2d) {
            g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            g2d.setColor(new Color(102, 51, 153));
            g2d.drawString("🎯 Drag shapes to matching targets!", 250, 40);

            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.setColor(new Color(85, 85, 85));
            g2d.drawString("Match the color AND shape type!", 320, 65);
        }

        private void drawTargetZones(Graphics2D g2d) {
            for (TargetZone target : targets) {
                target.draw(g2d);
            }
        }

        private void drawShapes(Graphics2D g2d) {
            for (DraggableShape shape : shapes) {
                if (!shape.isMatched()) {
                    shape.draw(g2d);
                }
            }
        }

        private void drawProgress(Graphics2D g2d) {
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.setColor(new Color(76, 175, 80));
            String progress = "Progress: " + correctMatches + "/" + TOTAL_SHAPES + " ⭐";
            g2d.drawString(progress, 20, 30);

            // Progress bar
            int barWidth = 200;
            int barHeight = 15;
            int barX = 20;
            int barY = 40;

            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);

            g2d.setColor(new Color(76, 175, 80));
            int progressWidth = (int)((double)correctMatches / TOTAL_SHAPES * barWidth);
            g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 10, 10);
        }

        private void playSuccessSound() {
            // Play a success beep
            Toolkit.getDefaultToolkit().beep();
        }

        private void playErrorSound() {
            // Different tone for error (you can enhance this with actual sound files)
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    Toolkit.getDefaultToolkit().beep();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        private void showVictoryMessage() {
            String message = "🎉 Congratulations! 🎉\n\n" +
                    "You matched all the shapes perfectly!\n" +
                    "⭐ Amazing job! ⭐\n\n" +
                    "Ready for the next challenge?";

            String[] options = {"🎮 Play Again", "🏠 Main Menu", "🟡 Try Medium Mode"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Victory!",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0: // Play Again
                    dispose();
                    new ShapeMatchingGame();
                    break;
                case 1: // Main Menu
                    dispose();
                    new GameFrame();
                    break;
                case 2: // Medium Mode
                    dispose();
                    try {
                        new NumberMatchingGame();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Medium mode coming soon!");
                        new GameFrame();
                    }
                    break;
            }
        }
    }

    // Enum for different shape types
    enum ShapeType {
        CIRCLE, SQUARE, TRIANGLE, STAR, HEART, DIAMOND
    }

    class DraggableShape {
        private int x, y, originalX, originalY;
        private final int SIZE = 70;
        private Color color;
        private ShapeType shapeType;
        private boolean matched = false;

        public DraggableShape(int x, int y, ShapeType shapeType, Color color) {
            this.x = this.originalX = x;
            this.y = this.originalY = y;
            this.shapeType = shapeType;
            this.color = color;
        }

        public void draw(Graphics2D g2d) {
            // Add shadow effect
            g2d.setColor(new Color(0, 0, 0, 30));
            drawShape(g2d, x + 3, y + 3);

            // Draw main shape
            g2d.setColor(color);
            drawShape(g2d, x, y);

            // Add highlight
            g2d.setColor(new Color(255, 255, 255, 80));
            drawShape(g2d, x - 1, y - 1);
        }

        private void drawShape(Graphics2D g2d, int drawX, int drawY) {
            switch (shapeType) {
                case CIRCLE:
                    g2d.fillOval(drawX, drawY, SIZE, SIZE);
                    break;
                case SQUARE:
                    g2d.fillRect(drawX, drawY, SIZE, SIZE);
                    break;
                case TRIANGLE:
                    int[] xPoints = {drawX + SIZE/2, drawX, drawX + SIZE};
                    int[] yPoints = {drawY, drawY + SIZE, drawY + SIZE};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                    break;
                case STAR:
                    drawStar(g2d, drawX + SIZE/2, drawY + SIZE/2, SIZE/2);
                    break;
                case HEART:
                    drawHeart(g2d, drawX, drawY, SIZE);
                    break;
                case DIAMOND:
                    int[] diamondX = {drawX + SIZE/2, drawX, drawX + SIZE/2, drawX + SIZE};
                    int[] diamondY = {drawY, drawY + SIZE/2, drawY + SIZE, drawY + SIZE/2};
                    g2d.fillPolygon(diamondX, diamondY, 4);
                    break;
            }
        }

        private void drawStar(Graphics2D g2d, int centerX, int centerY, int radius) {
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];

            for (int i = 0; i < 10; i++) {
                double angle = Math.PI * i / 5;
                int r = (i % 2 == 0) ? radius : radius / 2;
                xPoints[i] = (int)(centerX + r * Math.cos(angle - Math.PI/2));
                yPoints[i] = (int)(centerY + r * Math.sin(angle - Math.PI/2));
            }
            g2d.fillPolygon(xPoints, yPoints, 10);
        }

        private void drawHeart(Graphics2D g2d, int x, int y, int size) {
            // Create heart shape using curves
            GeneralPath heart = new GeneralPath();
            heart.moveTo(x + size/2, y + size/4);
            heart.curveTo(x + size/2, y, x, y, x, y + size/4);
            heart.curveTo(x, y + size/2, x + size/2, y + 3*size/4, x + size/2, y + size);
            heart.curveTo(x + size/2, y + 3*size/4, x + size, y + size/2, x + size, y + size/4);
            heart.curveTo(x + size, y, x + size/2, y, x + size/2, y + size/4);
            heart.closePath();
            g2d.fill(heart);
        }

        public boolean contains(Point p) {
            return new Rectangle(x, y, SIZE, SIZE).contains(p);
        }

        public void setPosition(int mx, int my) {
            this.x = mx - SIZE / 2;
            this.y = my - SIZE / 2;
        }

        public void snapTo(Rectangle target) {
            this.x = target.x;
            this.y = target.y;
        }

        public void resetToOriginal() {
            this.x = originalX;
            this.y = originalY;
        }

        // Getters and setters
        public Color getColor() { return color; }
        public ShapeType getShapeType() { return shapeType; }
        public boolean isMatched() { return matched; }
        public void setMatched(boolean matched) { this.matched = matched; }
        public Point getOriginalPosition() { return new Point(originalX, originalY); }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getSize() { return SIZE; }
    }

    class TargetZone {
        private Rectangle bounds;
        private ShapeType shapeType;
        private Color color;
        private boolean occupied = false;

        public TargetZone(int x, int y, ShapeType shapeType, Color color) {
            this.bounds = new Rectangle(x, y, 70, 70);
            this.shapeType = shapeType;
            this.color = color;
        }

        public void draw(Graphics2D g2d) {
            // Draw target zone background
            g2d.setColor(new Color(220, 220, 220, 100));
            g2d.fillRoundRect(bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 10, 15, 15);

            // Draw dashed border
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g2d.setColor(color);
            g2d.drawRoundRect(bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 10, 15, 15);

            // Draw shape outline as hint
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            drawShapeOutline(g2d);

            // Label
            g2d.setColor(new Color(85, 85, 85));
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String label = shapeType.toString();
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = bounds.x + (bounds.width - fm.stringWidth(label)) / 2;
            g2d.drawString(label, labelX, bounds.y - 10);
        }

        private void drawShapeOutline(Graphics2D g2d) {
            int x = bounds.x;
            int y = bounds.y;
            int size = bounds.width;

            switch (shapeType) {
                case CIRCLE:
                    g2d.drawOval(x, y, size, size);
                    break;
                case SQUARE:
                    g2d.drawRect(x, y, size, size);
                    break;
                case TRIANGLE:
                    int[] xPoints = {x + size/2, x, x + size};
                    int[] yPoints = {y, y + size, y + size};
                    g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case DIAMOND:
                    int[] diamondX = {x + size/2, x, x + size/2, x + size};
                    int[] diamondY = {y, y + size/2, y + size, y + size/2};
                    g2d.drawPolygon(diamondX, diamondY, 4);
                    break;
                // Add other shape outlines as needed
            }
        }

        public boolean contains(Point p) { return bounds.contains(p); }
        public Rectangle getBounds() { return bounds; }
        public ShapeType getShapeType() { return shapeType; }
        public Color getColor() { return color; }
        public boolean isOccupied() { return occupied; }
        public void setOccupied(boolean occupied) { this.occupied = occupied; }
    }
}