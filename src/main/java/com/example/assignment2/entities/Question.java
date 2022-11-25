package com.example.assignment2.entities;

import java.util.UUID;

public class Question {
    private final String id = UUID.randomUUID().toString();

    private final String question;
    private final String[] answers; // answers[0] is the correct answer.
    private int selected;
    private final int correct; // 0

    public Question(String _question, String[] _answers, int _selected, int _correct) {
        this.question = _question;
        this.answers = _answers;
        this.selected = _selected;
        this.correct = _correct;
    }

    public char indexToLetter(int _index) {
        return switch (_index) {
            case 0 -> 'a';
            case 1 -> 'b';
            case 2 -> 'c';
            case 3 -> 'd';
            default -> '~'; // 😔
        };
    }

    public String getID() {
        return this.id;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public int getCorrect() {
        return correct;
    }

    public void setSelected(int _selected) {
        this.selected = _selected;
    }

    public int getSelected() {
        return selected;
    }

    public String checkAnswer() {
        if (this.getSelected() == -1) return "No Answer";
        return this.getSelected() == this.getCorrect() ? "Correct" : "Wrong";
    }

    public String getResult() {
        return this.getQuestion() + ":" + checkAnswer();
    }

    public static Question questionFromLine(String _line) {
        String[] data = _line.split(",");
        String question = data[0];
        String[] answers = new String[]{data[1], data[2], data[3], data[4]};
        int selected = -1;
        int correct = Integer.parseInt(data[5]);
        return new Question(question, answers, selected, correct);
    }
}
