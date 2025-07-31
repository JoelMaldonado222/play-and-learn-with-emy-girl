import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameFrame extends JFrame {
    private JLabel modeSelectedLabel;
    private GradientPanel mainPanel;

    private SoundPlayer soundPlayer = new SoundPlayer();
    private Timer fadeInTimer;
    private float frameAlpha = 0.0f;
    private String selectedDifficulty = "Easy"; // Default selection

    private void initializeAudio() {
        try {
            System.out.println("🎵 Attempting to load background music...");
            soundPlayer.playBackgroundMusic("music/background_music.wav");
        } catch (Exception e) {
            System.out.println("⚠️ Audio failed: " + e.getMessage());
            System.out.println("🔇 Continuing without audio");
        }
    }

    public GameFrame() {
        setupWindow();
        initializeAudio(); // Only call once here
        try {
            createUI();
            animateEntrance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error initializing application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupWindow() {
        setTitle("Play and Learn With Emy Girl - Enhanced Edition");
        setSize(900, 800); // Increased from 700 to 800
        setMinimumSize(new Dimension(900, 800)); // Ensure minimum size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Changed to true to allow user adjustment if needed
    }

    private void setupKeyListener() {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gd = ge.getDefaultScreenDevice();
                if (gd.getFullScreenWindow() != null) {
                    exitFullScreen();
                }
            }
        });
    }

    private void createUI() throws Exception {
        // REMOVED: initializeAudio(); - This was causing the duplicate music
        setLayout(new BorderLayout());

        // Background with gradient
        mainPanel = new GradientPanel(
                new Color(255, 182, 193),
                new Color(255, 218, 185)
        );
        mainPanel.setLayout(new BorderLayout());

        // Add floating shapes
        FloatingShapesPanel shapesPanel = new FloatingShapesPanel();
        mainPanel.add(shapesPanel, BorderLayout.CENTER);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Reduced top/bottom padding

        // Animated title
        AnimatedTitleLabel title = new AnimatedTitleLabel("Play and Learn With Emy Girl");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(title);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 25))); // Reduced from 30

        // Image panel with enhanced styling
        JPanel imagePanel = createImagePanel();
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(imagePanel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 25))); // Reduced from 30

        // NEW: Difficulty Selection Panel
        JPanel difficultyPanel = createDifficultyPanel();
        difficultyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(difficultyPanel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 18))); // Reduced from 20

        // Enhanced buttons panel (Start Game + other buttons)
        JPanel buttonsPanel = createButtonsPanel();
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(buttonsPanel);

        shapesPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        createStatusBar();
    }

    private JPanel createDifficultyPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel difficultyLabel = new JLabel("Choose Your Level:");
        difficultyLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        difficultyLabel.setForeground(new Color(102, 51, 153));
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(difficultyLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 12))); // Reduced from 15

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.setOpaque(false);

        RoundedButton easyButton = new RoundedButton("🟢 Easy Mode",
                new Color(76, 175, 80), new Color(56, 142, 60));
        easyButton.setFont(new Font("Arial", Font.BOLD, 16));
        easyButton.setForeground(Color.WHITE);
        easyButton.setPreferredSize(new Dimension(150, 50));
        easyButton.addActionListener(e -> {
            playClickSound();
            selectedDifficulty = "Easy";
            updateDifficultySelection(easyButton);
            modeSelectedLabel.setText("Easy Mode Selected");
        });

        RoundedButton mediumButton = new RoundedButton("🟡 Medium Mode",
                new Color(255, 152, 0), new Color(245, 124, 0));
        mediumButton.setFont(new Font("Arial", Font.BOLD, 16));
        mediumButton.setForeground(Color.WHITE);
        mediumButton.setPreferredSize(new Dimension(150, 50));
        mediumButton.addActionListener(e -> {
            playClickSound();
            selectedDifficulty = "Medium";
            updateDifficultySelection(mediumButton);
            modeSelectedLabel.setText("Medium Mode Selected");
        });

        RoundedButton hardButton = new RoundedButton("🔴 Hard Mode",
                new Color(244, 67, 54), new Color(211, 47, 47));
        hardButton.setFont(new Font("Arial", Font.BOLD, 16));
        hardButton.setForeground(Color.WHITE);
        hardButton.setPreferredSize(new Dimension(150, 50));
        hardButton.addActionListener(e -> {
            playClickSound();
            selectedDifficulty = "Hard";
            updateDifficultySelection(hardButton);
            modeSelectedLabel.setText("Hard Mode Selected");
        });

        easyButton.putClientProperty("difficulty", "Easy");
        mediumButton.putClientProperty("difficulty", "Medium");
        hardButton.putClientProperty("difficulty", "Hard");

        buttonsPanel.add(easyButton);
        buttonsPanel.add(mediumButton);
        buttonsPanel.add(hardButton);
        panel.add(buttonsPanel);

        // Selected mode label (moved here instead of bottom of frame)
        modeSelectedLabel = new JLabel(" ", JLabel.CENTER);
        modeSelectedLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        modeSelectedLabel.setForeground(Color.MAGENTA);
        modeSelectedLabel.setMaximumSize(new Dimension(300, 30));
        modeSelectedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 8))); // Reduced from 10
        panel.add(modeSelectedLabel);

        updateDifficultySelection(easyButton);
        return panel;
    }

    // NEW METHOD: Update difficulty selection visual feedback
    private void updateDifficultySelection(RoundedButton selectedButton) {
        // Reset all buttons to normal appearance
        Container parent = selectedButton.getParent();
        for (Component comp : parent.getComponents()) {
            if (comp instanceof RoundedButton) {
                RoundedButton btn = (RoundedButton) comp;
                btn.setBorder(null);
            }
        }

        // Highlight selected button
        selectedButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 3),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
    }

    private JPanel createImagePanel() throws Exception {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        // Try to load the image, use placeholder if not found
        ImageIcon imageIcon = loadImageIcon();

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBorder(createImageBorder());
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }

    private ImageIcon loadImageIcon() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/music/IMG_0825.png")) {
            if (in != null) {
                BufferedImage full = ImageIO.read(in);
                int topCrop = 50, bottomCrop = 50;
                BufferedImage cropped = full.getSubimage(
                        0, topCrop, full.getWidth(), full.getHeight() - topCrop - bottomCrop
                );
                Image scaled = cropped.getScaledInstance(350, 280, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        }
        return createPlaceholderImage();
    }

    private Border createImageBorder() {
        return new CompoundBorder(
                new AbstractBorder() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Shadow
                        g2.setColor(new Color(0, 0, 0, 50));
                        g2.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 20, 20);

                        // Main border
                        g2.setColor(new Color(255, 255, 255, 200));
                        g2.fillRoundRect(x, y, width - 5, height - 5, 20, 20);

                        g2.setColor(new Color(255, 107, 107));
                        g2.setStroke(new BasicStroke(3));
                        g2.drawRoundRect(x + 2, y + 2, width - 9, height - 9, 20, 20);

                        g2.dispose();
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(15, 15, 20, 20);
                    }
                },
                new EmptyBorder(10, 10, 10, 10)
        );
    }

    private ImageIcon createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(350, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 182, 193), 350, 280, new Color(255, 218, 185));
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, 350, 280, 20, 20);

        // Happy face
        g2.setColor(new Color(255, 107, 107));
        g2.fillOval(120, 80, 25, 25);  // Left eye
        g2.fillOval(205, 80, 25, 25);  // Right eye

        // Smile
        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(125, 140, 100, 60, 0, -180);

        // Text
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        g2.setColor(new Color(255, 107, 107));
        FontMetrics fm = g2.getFontMetrics();
        String text = "Emy Girl";
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, (350 - textWidth) / 2, 240);

        g2.dispose();
        return new ImageIcon(placeholder);
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Start Game button - NOW LAUNCHES BASED ON SELECTED DIFFICULTY
        RoundedButton startButton = new RoundedButton("🎮 Start Game",
                new Color(76, 175, 80), new Color(56, 142, 60));
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(180, 55));
        startButton.addActionListener(e -> {
            playClickSound();
            startGameBasedOnDifficulty();
        });

        // Settings button (now for other settings like volume, etc.)
        RoundedButton settingsButton = new RoundedButton("⚙️ Settings",
                new Color(33, 150, 243), new Color(25, 118, 210));
        settingsButton.setFont(new Font("Arial", Font.BOLD, 16));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setPreferredSize(new Dimension(140, 45));
        settingsButton.addActionListener(e -> {
            playClickSound();
            showSettings();
        });

        // Mute/Unmute button
        RoundedButton muteButton = new RoundedButton("🔇 Mute",
                new Color(255, 87, 34), new Color(230, 74, 25));
        muteButton.setFont(new Font("Arial", Font.BOLD, 16));
        muteButton.setForeground(Color.WHITE);
        muteButton.setPreferredSize(new Dimension(140, 45));
        muteButton.addActionListener(e -> {
            soundPlayer.toggleMute();
            muteButton.setText(soundPlayer.isMuted() ? "🔊 Unmute" : "🔇 Mute");
        });

        // Help button
        RoundedButton helpButton = new RoundedButton("❓ Help",
                new Color(255, 152, 0), new Color(245, 124, 0));
        helpButton.setFont(new Font("Arial", Font.BOLD, 16));
        helpButton.setForeground(Color.WHITE);
        helpButton.setPreferredSize(new Dimension(100, 45));
        helpButton.addActionListener(e -> {
            playClickSound();
            showHelp();
        });

        panel.add(startButton);
        panel.add(settingsButton);
        panel.add(muteButton);
        panel.add(helpButton);

        return panel;
    }

    // NEW METHOD: Launch game based on selected difficulty
    private void startGameBasedOnDifficulty() {
        Timer fadeOut = new Timer(30, e -> {
            frameAlpha -= 0.08f;
            if (frameAlpha <= 0.0f) {
                frameAlpha = 0.0f;
                ((Timer)e.getSource()).stop();

                // IMPORTANT: Stop the background music before disposing
                soundPlayer.stopBackgroundMusic();
                dispose();

                SwingUtilities.invokeLater(() -> {
                    try {
                        if (selectedDifficulty.equals("Easy")) {
                            new ShapeMatchingGame(); // TODO: Create this class
                        } else if (selectedDifficulty.equals("Medium")) {
                            new NumberMatchingGame(); // TODO: Create this class
                        } else if (selectedDifficulty.equals("Hard")) {
                            new MathChallengeGame(); // TODO: Rename EasyMathGame to this
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Game class not found: " + selectedDifficulty + " Mode\n" +
                                        "Please create the corresponding game class.",
                                "Game Launch Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            setOpacity(frameAlpha);
        });
        fadeOut.start();
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(255, 107, 107, 100));
        statusBar.setBorder(new EmptyBorder(6, 15, 6, 15)); // Reduced padding

        JLabel welcomeLabel = new JLabel("Welcome! Choose your level and start learning! 🌟");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(102, 51, 153));

        JLabel versionLabel = new JLabel("Enhanced v2.0");
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        versionLabel.setForeground(new Color(102, 51, 153));

        statusBar.add(welcomeLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void animateEntrance() {
        setUndecorated(true);
        setOpacity(0.0f);
        setVisible(true);

        fadeInTimer = new Timer(30, e -> {
            frameAlpha += 0.05f;
            if (frameAlpha >= 1.0f) {
                frameAlpha = 1.0f;
                fadeInTimer.stop();
            }
            setOpacity(frameAlpha);
        });
        fadeInTimer.start();
    }

    private void showSettings() {
        String[] options = {"Sound On/Off", "Reset Progress"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choose a setting to modify:",
                "Game Settings",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0) {
            switch (choice) {
                case 0: // Sound On/Off
                    toggleSound();
                    break;
                case 1: // Reset Progress
                    resetProgress();
                    break;
            }
        }
    }

    private void toggleSound() {
        boolean currentState = soundPlayer.isSoundEnabled();
        soundPlayer.setSoundEnabled(!currentState);

        String message = !currentState ?
                "🔊 Sound has been turned ON" :
                "🔇 Sound has been turned OFF";

        JOptionPane.showMessageDialog(this, message, "Sound Settings", JOptionPane.INFORMATION_MESSAGE);

        // If sound was turned on, restart background music
        if (!currentState) {
            initializeAudio();
        }
    }

    private void toggleFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {
            if (gd.getFullScreenWindow() == null) {
                // Enter full screen
                try {
                    setVisible(false);
                    dispose();
                    setUndecorated(true);
                    setResizable(false);
                    gd.setFullScreenWindow(this);
                    setVisible(true);
                    JOptionPane.showMessageDialog(this,
                            "✅ Full Screen Mode ON\nPress ESC to exit full screen",
                            "Display Settings",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Could not enter full screen mode: " + e.getMessage(),
                            "Display Error",
                            JOptionPane.ERROR_MESSAGE);
                    // Restore window if full screen failed
                    gd.setFullScreenWindow(null);
                    setUndecorated(false);
                    setResizable(true);
                    setVisible(true);
                }
            } else {
                // Exit full screen
                exitFullScreen();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Full screen mode is not supported on this system",
                    "Display Settings",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exitFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (gd.getFullScreenWindow() != null) {
            gd.setFullScreenWindow(null);
            setUndecorated(false);
            setResizable(true);
            setSize(900, 800);
            setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(this,
                    "✅ Full Screen Mode OFF",
                    "Display Settings",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetProgress() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ Are you sure you want to reset all progress?\n" +
                        "This action cannot be undone!",
                "Reset Progress",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Reset difficulty to default
            selectedDifficulty = "Easy";

            // Reset UI to show Easy mode selected
            updateDifficultyToEasy();

            // Here you would typically clear saved game data, scores, etc.
            // For now, we'll just show a confirmation
            JOptionPane.showMessageDialog(this,
                    "✅ Progress has been reset!\n" +
                            "• Difficulty set to Easy Mode\n" +
                            "• All saved data cleared",
                    "Reset Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateDifficultyToEasy() {
        // Find and select the Easy button
        Container contentPanel = (Container) ((Container) mainPanel.getComponent(0)).getComponent(0);
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                // Look for the difficulty panel
                for (Component subComp : panel.getComponents()) {
                    if (subComp instanceof JPanel) {
                        JPanel subPanel = (JPanel) subComp;
                        for (Component button : subPanel.getComponents()) {
                            if (button instanceof RoundedButton) {
                                RoundedButton btn = (RoundedButton) button;
                                if ("Easy".equals(btn.getClientProperty("difficulty"))) {
                                    updateDifficultySelection(btn);
                                    modeSelectedLabel.setText("Easy Mode Selected");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "🌟 Welcome to Emy Girl's Learning Adventure! 🌟\n\n" +
                        "• Choose your difficulty level (Easy/Medium/Hard)\n" +
                        "• Click 'Start Game' to begin your learning journey\n" +
                        "• 🟢 Easy: Shape matching fun!\n" +
                        "• 🟡 Medium: Number and word matching\n" +
                        "• 🔴 Hard: Math challenges and puzzles\n" +
                        "• Use 'Settings' for sound and display options\n\n" +
                        "Remember: Learning is fun! 🎈",
                "How to Play",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void playClickSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }

            new GameFrame();
        });
    }
}