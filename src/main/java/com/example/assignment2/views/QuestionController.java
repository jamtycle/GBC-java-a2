package com.example.assignment2.views;

import com.example.assignment2.data.Examdb;
import com.example.assignment2.entities.Question;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class QuestionController {
    private UUID quizID;
    private Question[] questions_set;
    private Examdb db;

    private final int exam_time = 600;

    @FXML
    private GridPane gp_container;
    @FXML
    private Label lbl_marks;
    @FXML
    private Label lbl_time;
    @FXML
    private ScrollPane sp_quiz;
    @FXML
    private TextField txt_names;

    @FXML
    protected void onQuizStart() {
        if (txt_names.getText().isBlank() || txt_names.getText().isEmpty()) {
            txt_names.setPromptText("Name cannot be empty.");
            return;
        }

        sp_quiz.setDisable(false);
        txt_names.setDisable(true);
        setupTimer(exam_time); // 10 mins
    }

    @FXML
    protected void onSubmit() {
        ButtonType res = ConfirmShow("Confirmation", "Submission", "Are you sure to submit this quiz?");
        if (ButtonType.NO.getText().equals(res.getText())) { // "res == ButtonType.NO" is not working because, java
            return;
        }

        int mark = db.getQuizMark(questions_set);
        if (!db.saveQuiz(txt_names.getText(), mark)) {
            AlertShow("Error", "Submission", "The quiz had a problem and cannot be saved.");
        } else {
            AlertShow("Success", "Submission", "The quiz has been saved.\nThe Application will close after this alert.");
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    protected void onCalculateMark() {
        lbl_marks.setText(db.getQuizMark(questions_set) + "/100");
    }

    @FXML
    protected void initialize() {
        gp_container.prefWidthProperty().bind(((VBox) gp_container.getParent()).widthProperty());
        gp_container.prefHeightProperty().bind(((VBox) gp_container.getParent()).heightProperty());

        db = new Examdb("./exam.txt");
        db.generateDB();
        Pair<UUID, Question[]> quiz = db.getQuestionsSet(5);
        quizID = quiz.getKey();
        questions_set = quiz.getValue();

        VBox box = (VBox) sp_quiz.getContent();
        box.setSpacing(20);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: black");
        box.prefWidthProperty().bind(sp_quiz.widthProperty());
        for (Question q : questions_set) {
            box.getChildren().add(QuestionContainer(q));
        }

        sp_quiz.setFitToWidth(true);
        sp_quiz.setDisable(true);

        lbl_time.setText(String.format("%02d:%02d", exam_time / 60, exam_time % 60));
        lbl_marks.setText("?/100");

        lbl_time.setText(String.format("%02d:%02d", exam_time / 60, exam_time % 60));
        lbl_marks.setText("?/100");

        lbl_marks.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD,18));
        lbl_marks.setStyle("-fx-border-color: black; -fx-background-color: #F2F1E8;");

        lbl_time.setFont(Font.font("Arial", FontWeight.BOLD,16));
        lbl_time.setStyle("-fx-border-color: black; -fx-background-color: #F2F1E8; -fx-text-fill: #B50000;");
    }

    @FXML
    protected void onGetAverage() {
        Double avg = db.getAverage();
        AlertShow("Average", "Average", "The average is: " + avg);
    }

    private void setupTimer(int _seconds) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int i = _seconds;

            public void run() {
                if (i >= 0) {
                    String time = String.format("%02d:%02d", i / 60, i % 60);
                    Platform.runLater(() -> lbl_time.setText(time));
                    i--;
                } else {
                    Platform.runLater(() -> {
                        sp_quiz.setDisable(true);
                        AlertShow("Quiz", "Time out", "You run out of time!\nPress OK to submit your quiz.");
                        onSubmit();
                    });
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void AlertShow(String _title, String _header_text, String _content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(_title);
        alert.setHeaderText(_header_text);
        alert.setContentText(_content);
        alert.showAndWait();
    }

    private ButtonType ConfirmShow(String _title, String _header_text, String _content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(_title);
        alert.setHeaderText(_header_text);
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        alert.setContentText(_content);
        Optional<ButtonType> info = alert.showAndWait();
        if (info.isEmpty()) return ButtonType.NO;
        else return info.get();
    }


    private VBox QuestionContainer(Question _question) {
        VBox box = new VBox();
        box.setSpacing(20);
        box.prefWidthProperty().bind(sp_quiz.widthProperty());
        box.setId(_question.getID());

        Label qtext = new Label();
        qtext.prefWidthProperty().bind(box.widthProperty());
        qtext.setText(_question.getQuestion());
        box.getChildren().add(qtext);
        qtext.setFont(Font.font(" Arial", FontWeight.EXTRA_BOLD, 14.5));
        qtext.setStyle("-fx-text-fill: #53001D");

        int i = 0;
        ToggleGroup group = new ToggleGroup();
        for (String answer : _question.getAnswers()) {
            RadioButton rb = new RadioButton();
            rb.prefWidthProperty().bind(box.widthProperty());
            rb.setText(answer);
            rb.setId(String.valueOf(i++));
            rb.setToggleGroup(group);
            rb.setFont(Font.font("Arial",FontWeight.SEMI_BOLD,14));
            rb.setOnAction(actionEvent -> {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                _question.setSelected(Integer.parseInt(selected.getId()));
            });
            box.getChildren().add(rb);
        }

        return box;
    }
}