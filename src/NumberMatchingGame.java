import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class NumberMatchingGame extends JFrame {
    private int currentNumber;
    private final String[] numberWords = {
            "Zero", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine"
    };
    private JLabel numberLabel;
    private JButton[] wordButtons;
    private JLabel scoreLabel;
    private int score = 0, totalQuestions = 0;

    public NumberMatchingGame() {
        setTitle("🟡 Number Matching Game - Medium Mode");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setupUI();
        generateNewQuestion();
        setVisible(true);
    }

    private void setupUI() {
        // Top Panel (Back Button + Score)
        JPanel topPanel = new JPanel(new BorderLayout());
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

        scoreLabel = new JLabel("Score: 0/0", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(scoreLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Game Panel (Instructions + Number + Buttons)
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBackground(new Color(255, 248, 220));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel instructions = new JLabel("🔢 Match the number to its word!", JLabel.CENTER);
        instructions.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);

        numberLabel = new JLabel("", JLabel.CENTER);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 120));
        numberLabel.setForeground(new Color(33, 150, 243));
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setOpaque(false);
        wordButtons = new JButton[3];

        for (int i = 0; i < 3; i++) {
            wordButtons[i] = new JButton();
            wordButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
            wordButtons[i].setPreferredSize(new Dimension(120, 50));
            int index = i;
            wordButtons[i].addActionListener(e -> checkAnswer(index));
            buttonsPanel.add(wordButtons[i]);
        }

        gamePanel.add(instructions);
        gamePanel.add(Box.createVerticalStrut(30));
        gamePanel.add(numberLabel);
        gamePanel.add(Box.createVerticalStrut(30));
        gamePanel.add(buttonsPanel);
        add(gamePanel, BorderLayout.CENTER);
    }

    private void generateNewQuestion() {
        currentNumber = new Random().nextInt(10); // 0 to 9
        numberLabel.setText(String.valueOf(currentNumber));

        Set<String> options = new LinkedHashSet<>();
        options.add(numberWords[currentNumber]);

        while (options.size() < 3) {
            int rand = new Random().nextInt(10);
            options.add(numberWords[rand]);
        }

        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);

        for (int i = 0; i < 3; i++) {
            wordButtons[i].setText(shuffled.get(i));
            wordButtons[i].setEnabled(true);
            wordButtons[i].setBackground(new Color(220, 220, 220));
        }
    }

    private void checkAnswer(int index) {
        totalQuestions++;
        String selected = wordButtons[index].getText();
        boolean isCorrect = selected.equals(numberWords[currentNumber]);

        if (isCorrect) {
            score++;
            wordButtons[index].setBackground(new Color(76, 175, 80)); // Green
        } else {
            wordButtons[index].setBackground(new Color(244, 67, 54)); // Red
            for (JButton btn : wordButtons) {
                if (btn.getText().equals(numberWords[currentNumber])) {
                    btn.setBackground(new Color(76, 175, 80));
                }
            }
        }

        scoreLabel.setText("Score: " + score + "/" + totalQuestions);
        for (JButton btn : wordButtons) btn.setEnabled(false);

        Timer delay = new Timer(1200, e -> {
            if (totalQuestions >= 10) {
                showFinalScore();
            } else {
                generateNewQuestion();
            }
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void showFinalScore() {
        String msg = "🎉 Game Complete! 🎉\n\n" +
                "Final Score: " + score + "/10\n" +
                (score >= 8 ? "⭐ Excellent!" : score >= 6 ? "👍 Good job!" : "💪 Keep practicing!");

        String[] options = {"🎮 Play Again", "🏠 Main Menu", "🔴 Try Hard Mode"};
        int choice = JOptionPane.showOptionDialog(this, msg, "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0 -> {
                dispose();
                new NumberMatchingGame();
            }
            case 1 -> {
                dispose();
                new GameFrame();
            }
            case 2 -> {
                dispose();
                try {
                    new MathChallengeGame();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Hard mode coming soon!");
                    new GameFrame();
                }
            }
        }
    }
}
