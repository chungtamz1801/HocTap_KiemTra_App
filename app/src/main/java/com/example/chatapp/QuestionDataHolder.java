package com.example.chatapp;
import java.util.ArrayList;

public class QuestionDataHolder {
    private static QuestionDataHolder instance;
    private ArrayList<Question> listQuestions;

    private QuestionDataHolder() {}

    public static QuestionDataHolder getInstance() {
        if (instance == null) instance = new QuestionDataHolder();
        return instance;
    }

    public void setListQuestions(ArrayList<Question> listQuestions) {
        this.listQuestions = listQuestions;
    }

    public ArrayList<Question> getListQuestions() {
        return listQuestions;
    }
}