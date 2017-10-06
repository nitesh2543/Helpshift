package com.example.nitesh.myapplication.util;

import java.util.HashMap;

/**
 * Created by nitesh on 5/10/17.
 */

public class Constants {

    public static String url = "https://api.stackexchange.com/2.2/search/advanced?%20pagesize=100&order=desc&sort=relevance&q={query}%20&site=stackoverflow&filter=!.UE46pK5nV.kfAr.";

    public interface OnTaskDoneListener {
        void onTaskDone(String responseData);

        void onError();
    }

    public interface QuestionListener{

    }

    public interface Key {

        String TITLE = "title";
        String UP_VOTES = "up_vote_count";
    }

    public interface DataDownloadListener{
        void onDownloadSuccess();
        void onDownloadFailed();
    }
}
