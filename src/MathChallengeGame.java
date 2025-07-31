import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MathChallengeGame extends JFrame {
    private int correctAnswer;
    private int score = 0;
    private JLabel questionLabel;
    private JLabel feedbackLabel;
    private final Random rand = new Random();

    public MathChallengeGame() {
        setTitle("🔴 Hard Mode - Math Challenge with Emy Girl");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top: Question label
        questionLabel = new JLabel("", JLabel.CENTER);
        questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        add(questionLabel, BorderLayout.NORTH);

        // Center: Panel with 3 buttons (answer choices)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        add(buttonPanel, BorderLayout.CENTER);

        // Answer buttons
        JButton[] buttons = new JButton[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 22));
            buttonPanel.add(buttons[i]);
        }

        // Bottom: Feedback label
        feedbackLabel = new JLabel(" ", JLabel.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        add(feedbackLabel, BorderLayout.SOUTH);

        // First question
        generateNewQuestion(buttons);

        setVisible(true);
    }

    private void generateNewQuestion(JButton[] buttons) {
        int num1 = rand.nextInt(20) + 1; // 1–20
        int num2 = rand.nextInt(20) + 1;
        boolean isAddition = rand.nextBoolean();

        String operation = isAddition ? "+" : "-";
        correctAnswer = isAddition ? num1 + num2 : num1 - num2;
        questionLabel.setText("What is " + num1 + " " + operation + " " + num2 + "?");

        // Avoid negative answers in subtraction for now
        if (!isAddition && num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
            correctAnswer = num1 - num2;
            questionLabel.setText("What is " + num1 + " - " + num2 + "?");
        }

        // Generate answer choices
        int correctPos = rand.nextInt(3);
        int wrong1 = correctAnswer + rand.nextInt(5) + 1;
        int wrong2 = correctAnswer - (rand.nextInt(5) + 1);
        if (wrong2 == correctAnswer || wrong2 < -10) wrong2 = correctAnswer + 7;

        for (int i = 0; i < 3; i++) {
            int answer;
            if (i == correctPos) {
                answer = correctAnswer;
            } else if (i == (correctPos + 1) % 3) {
                answer = wrong1;
            } else {
                answer = wrong2;
            }

            buttons[i].setText(String.valueOf(answer));
            // Clear previous listeners
            for (ActionListener al : buttons[i].getActionListeners()) {
                buttons[i].removeActionListener(al);
            }

            final int selected = answer;
            buttons[i].addActionListener(e -> checkAnswer(selected, buttons));
        }
    }

    private void checkAnswer(int selected, JButton[] buttons) {
        if (selected == correctAnswer) {
            score++;
            feedbackLabel.setText("✅ Great job! Score: " + score);
        } else {
            feedbackLabel.setText("❌ Try again! That was " + selected);
        }

        // Delay next question
        Timer timer = new Timer(1500, e -> {
            feedbackLabel.setText(" ");
            generateNewQuestion(buttons);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
