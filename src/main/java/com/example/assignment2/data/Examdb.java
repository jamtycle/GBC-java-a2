package com.example.assignment2.data;

import com.example.assignment2.entities.Question;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class Examdb extends FileManager {
    java.util.UUID uuid = java.util.UUID.randomUUID();

    public Examdb(String _file_name) {
        super(_file_name);
    }

    @Override
    public void generateDB() {
        try {
            String info = this.readFile();
            String[] lines = info.split("\n");
            this.db = new Question[lines.length];
            int i = 0;
            for (String line: lines) {
                this.db[i++] = Question.questionFromLine(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getQuizMark(Question[] _question_set) {
        int mark = 0;
        for (Question q: _question_set) {
            mark += switch (q.checkAnswer()) {
                case "No Answer" -> 0;
                case "Correct"   -> 20;
                case "Wrong"     -> -5;
                default          -> -9999;
            };
        }
        return mark;
    }

    public boolean saveQuiz(String _id, Question[] _question_set) {
        ArrayList<String> content = new ArrayList<>();
//        content.add("Name:" + );
//        for (Question q : _question_set) {
//            content.add(q.getResult());
//        }
        if(!this.createDirectory("./quizzes")) {
            System.out.println("./quizzes/ folder could not be created!");
            return false;
        }
        return this.writeFile("./quizzes/quiz-" + _id + ".txt", String.join("\n", content));
    }

    public boolean saveQuiz(String _name, int _score) {
        if(!this.createDirectory("./quizzes")) {
            System.out.println("./quizzes/ folder could not be created!");
            return false;
        }
        return this.appendToFile("./quizzes/record.txt", _name + "," + _score);
    }

    public Double getAverage()
    {
        double avg = 0d;
        int i = 0;
        String content = this.readFile("./quizzes/record.txt");
        String[] lines = content.split("\n");
        for (String line: lines) {
            if(!line.isEmpty() && !line.isBlank()) {
                String[] info = line.split(",");
                // TODO: in case need the name as well
                avg += Double.parseDouble(info[1]);
                i++;
            }
        }
        return avg / i;
    }

    public Pair<UUID, Question[]> getQuestionsSet(int _quantity) {

        if(_quantity >= this.getDB().length) _quantity = this.getDB().length - 1;

        Random rand = new Random();
        int[] indexes = new int[_quantity];
        int i = 0;
        while (i < _quantity)
        {
            int gen = rand.nextInt(0, this.getDB().length - 1);
            if (Arrays.stream(indexes).noneMatch(x -> x == gen)) { // maybe make indexes a stream from its declaration?
                indexes[i++] = gen;
            }
        }

        Question[] qs = new Question[_quantity];
        for (int j = 0; j < _quantity; j++) {
            qs[j] = this.getDB()[indexes[j]];
        }

        // The use of shuffle here could simplify this process considerably. Thanks to Prakash to show me tha way.

        return new Pair<>(UUID.randomUUID(), qs);
    }
}
