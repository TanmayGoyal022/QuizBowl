
import java.util.*;
import java.io.*;

// Abstract class for Question
abstract class Question {
    protected String question;
    protected int pointValue;

    public Question(String question, int pointValue) {
        this.question = question;
        this.pointValue = pointValue;
    }

    public int getPointValue() {
        return pointValue;
    }

    public abstract boolean checkAnswer(String answer);
    public abstract void displayQuestion();
}

// True/False Question
class QuestionTF extends Question {
    String correctAnswer;

    public QuestionTF(String question, int pointValue, String correctAnswer) {
        super(question, pointValue);
        this.correctAnswer = correctAnswer.toLowerCase();
    }

    @Override
    public boolean checkAnswer(String answer) {
        return correctAnswer.equals(answer.toLowerCase());
    }

    @Override
    public void displayQuestion() {
        System.out.println("Question: " + question + " (true/false)");
    }
}

// Multiple Choice Question
class QuestionMC extends Question {
    List<String> choices;
    String correctAnswer;

    public QuestionMC(String question, int pointValue, List<String> choices, String correctAnswer) {
        super(question, pointValue);
        this.choices = choices;
        this.correctAnswer = correctAnswer.toUpperCase();
    }

    @Override
    public boolean checkAnswer(String answer) {
        return correctAnswer.equals(answer.toUpperCase());
    }

    @Override
    public void displayQuestion() {
        System.out.println("Question: " + question);
        char option = 'A';
        for (String choice : choices) {
            System.out.println(option + ") " + choice);
            option++;
        }
    }
}

// Short Answer Question
class QuestionSA extends Question {
    String correctAnswer;

    public QuestionSA(String question, int pointValue, String correctAnswer) {
        super(question, pointValue);
        this.correctAnswer = correctAnswer.toLowerCase();
    }

    @Override
    public boolean checkAnswer(String answer) {
        return correctAnswer.equals(answer.toLowerCase());
    }

    @Override
    public void displayQuestion() {
        System.out.println("Question: " + question);
    }
}

// Player class
class Player {
    private String firstName, lastName;
    private int score;

    public Player(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = 0;
    }

    public void updateScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public void displayFinalScore() {
        System.out.println(firstName + " " + lastName + ", your game is over!");
        System.out.println("Your final score is " + score + " points.");
        System.out.println("Better Luck Next Time!");
    }
}

// Main QuizBowl Class
public class QuizBowl {
    private List<Question> questions;
    private Player player;
    private Scanner scanner;

    public QuizBowl() {
        questions = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.print("What is your first name? ");
        String firstName = scanner.nextLine();
        System.out.print("What is your last name? ");
        String lastName = scanner.nextLine();
        player = new Player(firstName, lastName);
        
        System.out.print("What file stores your questions? ");
        String fileName = scanner.nextLine();
        loadQuestions(fileName);

        int maxQuestions = questions.size();
        int numQuestions = getValidQuestionCount(maxQuestions);

        Collections.shuffle(questions);
        playGame(numQuestions);

        player.displayFinalScore();
    }

    private void loadQuestions(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            int numQuestions = Integer.parseInt(fileScanner.nextLine().trim());
            int count = 0; // Track actual questions read

            while (fileScanner.hasNextLine()) {
                String[] meta = fileScanner.nextLine().split(" ");
                String type = meta[0];
                int points = Integer.parseInt(meta[1]);
                String question = fileScanner.nextLine();

                if (type.equals("TF")) {
                    String answer = fileScanner.nextLine();
                    questions.add(new QuestionTF(question, points, answer));
                } else if (type.equals("MC")) {
                    int numChoices = Integer.parseInt(fileScanner.nextLine().trim());
                    List<String> choices = new ArrayList<>();
                    for (int i = 0; i < numChoices; i++) {
                        choices.add(fileScanner.nextLine());
                    }
                    String correctAnswer = fileScanner.nextLine();
                    questions.add(new QuestionMC(question, points, choices, correctAnswer));
                } else if (type.equals("SA")) {
                    String answer = fileScanner.nextLine();
                    questions.add(new QuestionSA(question, points, answer));
                }
                count++;
            }
            if (count != numQuestions) {
                System.out.println("Warning: Expected " + numQuestions + " questions, but found " + count + ".");
            }
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private int getValidQuestionCount(int maxQuestions) {
        while (true) {
            System.out.print("How many questions would you like (out of " + maxQuestions + ")? ");
            String input = scanner.nextLine().trim();
            try {
                int num = Integer.parseInt(input);
                if (num > 0 && num <= maxQuestions) return num;
                System.out.println("Sorry, that is too many.");
            } catch (NumberFormatException e) {
                System.out.println("Sorry, that is not valid.");
            }
        }
    }

    private void playGame(int numQuestions) {
        for (int i = 0; i < numQuestions; i++) {
            Question q = questions.get(i);
            System.out.println("\nPoints: " + q.getPointValue());
            q.displayQuestion();
            System.out.print("Your answer: ");
            String answer = scanner.nextLine().trim();
            
            if (answer.equalsIgnoreCase("SKIP")) {
                System.out.println("You have elected to skip that question.");
                continue;
            }

            if (q.checkAnswer(answer)) {
                System.out.println("Correct! You get " + q.getPointValue() + " points.");
                player.updateScore(q.getPointValue());
            } else {
                System.out.println("Incorrect, the answer was " + ((q instanceof QuestionMC) ? ((QuestionMC) q).correctAnswer : ((q instanceof QuestionTF) ? ((QuestionTF) q).correctAnswer : ((QuestionSA) q).correctAnswer)) + ". You lose " + q.getPointValue() + " points.");
                player.updateScore(-q.getPointValue());
            }
        }
    }

    public static void main(String[] args) {
        new QuizBowl().start();
    }
}
