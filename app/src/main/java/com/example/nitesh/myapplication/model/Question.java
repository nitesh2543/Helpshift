package com.example.nitesh.myapplication.model;

/**
 * Created by nitesh on 6/10/17.
 */

public class Question {

    private String title;
    private String upVoteCount;

    public Question(String title, String upVoteCount) {
        this.title = title;
        this.upVoteCount = upVoteCount;
    }

    public String getTitle() {
        return title;
    }

    public String getUpVoteCount() {
        return upVoteCount;
    }
}
