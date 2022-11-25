package com.example.assignment2.data;

import com.example.assignment2.QuizApplication;
import com.example.assignment2.entities.Question;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class FileManager {
    private String file_name = "";
    protected Question[] db;

    public FileManager(String _file_name) {
        this.file_name = _file_name;
    }

    protected String readFile() throws IOException {
        new File(this.file_name).createNewFile();
        return Files.readString(Paths.get(this.file_name), StandardCharsets.UTF_8);
    }

    protected String readFile(String _file_name) {
        try {
            new File(_file_name).createNewFile();
            return Files.readString(Paths.get(_file_name), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }

    protected boolean writeFile(String _file_name, String _content) {
        try {
            Files.writeString(Paths.get(_file_name), _content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    protected boolean appendToFile(String _file_name, String _content) {
        //TODO: Test this.
        StringBuilder content = new StringBuilder(readFile(_file_name));
        if(content.isEmpty()) content.append(_content);
        else content.append("\n").append(_content);
        return writeFile(_file_name, content.toString());
    }

    protected boolean createDirectory(String _path) {
        File dir = new File(_path);
        if (!dir.exists()) return new File(_path).mkdirs();
        else return true;
    }

    abstract public void generateDB();

    public Question[] getDB() {
        return this.db;
    }
}
