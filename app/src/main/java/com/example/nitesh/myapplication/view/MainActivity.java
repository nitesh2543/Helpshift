package com.example.nitesh.myapplication.view;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitesh.myapplication.util.Constants;
import com.example.nitesh.myapplication.database.DBHelper;
import com.example.nitesh.myapplication.network.HttpHandler;
import com.example.nitesh.myapplication.network.Network;
import com.example.nitesh.myapplication.util.PreferenceManager;
import com.example.nitesh.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements Constants.QuestionListener,
        Constants.DataDownloadListener, SearchView.OnQueryTextListener {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private String url = Constants.url;
    private ArrayList<HashMap<String, String>> questionList;// used in network operation
    private ArrayList<HashMap<String, String>> dbQuestionList;//used in database
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionList = new ArrayList<>();
        dbQuestionList = new ArrayList();
        lv = (ListView) findViewById(R.id.list);
        tv = (TextView) findViewById(R.id.empty_text);
        if (!Network.isConnected(this) && PreferenceManager.getsInstance(this).isFirstLaunch())
            Toast.makeText(this, R.string.first_launch_err, Toast.LENGTH_LONG).show();
        else if (!PreferenceManager.getsInstance(this).isFirstLaunch())
            getDataFromDB();
        tv.setVisibility(dbQuestionList.size()==0? View.VISIBLE:View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Network.isConnected(this)) {
            dbQuestionList.clear();
            questionList.clear();
            DBHelper dbH = new DBHelper(this);
            SQLiteDatabase db = dbH.getReadableDatabase();
            db.execSQL("delete from " + DBHelper.TABLE_NAME);
            db.close();
            new GetQuestions(this).execute(query);
            return true;
        } else {
            if (PreferenceManager.getsInstance(this).isFirstLaunch())
                Toast.makeText(this, R.string.first_launch_err, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private class GetQuestions extends AsyncTask<String, Void, Void> {

        private Constants.DataDownloadListener listener;

        public GetQuestions(Constants.DataDownloadListener downloadListener) {
            this.listener = downloadListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String query = arg0[0];
            String finalUrl = url.replace("{query}", query.replaceAll(" ", ""));
            URL url = null;
            try {
                url = new URL(finalUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpHandler sh = new HttpHandler(url);
            String jsonStr = sh.makeServiceCall();
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray questions = jsonObj.getJSONArray(getString(R.string.items));
                    for (int i = 0; i < questions.length(); i++) {
                        JSONObject c = questions.getJSONObject(i);
                        String title = c.getString(Constants.Key.TITLE);
                        String upVoteCount = c.getString(Constants.Key.UP_VOTES);
                        HashMap<String, String> question = new HashMap<>();
                        question.put(Constants.Key.TITLE, title);
                        question.put(Constants.Key.UP_VOTES, upVoteCount);
                        questionList.add(question);
                    }
                    listener.onDownloadSuccess();
                } catch (final JSONException e) {
                    listener.onDownloadFailed();
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, questionList,
                    R.layout.list_item, new String[]{Constants.Key.TITLE, Constants.Key.UP_VOTES},
                    new int[]{R.id.title, R.id.up_vote});
            lv.setAdapter(adapter);
            tv.setVisibility(questionList.size()==0? View.VISIBLE:View.GONE);
            if (PreferenceManager.getsInstance(MainActivity.this).isFirstLaunch())
                PreferenceManager.getsInstance(MainActivity.this).setFirstLaunch(false);
        }

    }

    @Override
    public void onDownloadSuccess() {
        Log.e(TAG, "onDownloadSuccess: ");
        dbQuestionList.addAll(questionList);
        insertDataToDB();
    }


    private void getDataFromDB() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String projection[] = {Constants.Key.TITLE, Constants.Key.UP_VOTES};
        Cursor c = db.query(DBHelper.TABLE_NAME, projection, null, null, null, null, null);
        c.moveToFirst();
        try {
            do {
                HashMap<String, String> question = new HashMap<>();
                question.put(Constants.Key.TITLE, c.getString(0));
                question.put(Constants.Key.UP_VOTES, c.getString(1));
                dbQuestionList.add(question);
            } while (c.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
        }
        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, dbQuestionList,
                R.layout.list_item, new String[]{Constants.Key.TITLE, Constants.Key.UP_VOTES},
                new int[]{R.id.title, R.id.up_vote});
        lv.setAdapter(adapter);
    }

    private void insertDataToDB() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < dbQuestionList.size(); i++) {
            values.put(Constants.Key.TITLE, dbQuestionList.get(i).get(Constants.Key.TITLE));
            values.put(Constants.Key.UP_VOTES, dbQuestionList.get(i).get(Constants.Key.UP_VOTES));
            db.insert(DBHelper.TABLE_NAME, null, values);
        }
        db.close();

    }

    @Override
    public void onDownloadFailed() {
        Log.e(TAG, "onDownloadFailed: ");
    }

}