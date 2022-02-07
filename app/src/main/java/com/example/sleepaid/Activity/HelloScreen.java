package com.example.sleepaid.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.sleepaid.AppDatabase;
import com.example.sleepaid.HelloScreenFragment;
import com.example.sleepaid.Questionnaire;
import com.example.sleepaid.R;
import com.example.sleepaid.SleepDataScreen;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HelloScreen extends AppCompatActivity {
    //private String url = "https://192.168.0.47/new_testing.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //new AddNewTesting().execute("hello");

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        db.answerDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answers -> {
                            if (!answers.isEmpty()) {
                                goToSleepDataScreen();
                            }
                            else {
                                setContentView(R.layout.activity_hello_screen_host);

                                if (savedInstanceState == null) {
                                    HelloScreenFragment fragment = new HelloScreenFragment();

                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .add(R.id.content, fragment)
                                            .commit();
                                }
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    public void startQuestionnaire(View view) {
        Intent questionnaire = new Intent(this, Questionnaire.class);
        startActivity(questionnaire);
    }

    private void goToSleepDataScreen() {
        Intent sleepDataScreen = new Intent(this, SleepDataScreen.class);
        startActivity(sleepDataScreen);
    }

//    private class AddNewTesting extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... arg) {
//            // TODO Auto-generated method stub
//            String text = arg[0];
//
//            // Preparing post params
//            ContentValues params = new ContentValues();
//            params.put("text", text);
//
//            ServiceHandler serviceClient = new ServiceHandler();
//
//            serviceClient.makeServiceCall(url,
//                    ServiceHandler.POST, params);
//
////            Log.d("Create Prediction Request: ", "> " + json);
//
////            if (json != null) {
////                try {
////                    JSONObject jsonObj = new JSONObject(json);
////                    boolean error = jsonObj.getBoolean("error");
////                    // checking for error node in json
////                    if (!error) {
////                        // new category created successfully
////                        Log.e("Prediction added successfully ",
////                                "> " + jsonObj.getString("message"));
////                    } else {
////                        Log.e("Add Prediction Error: ",
////                                "> " + jsonObj.getString("message"));
////                    }
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////
////            } else {
////                Log.e("JSON Data", "JSON data error!");
////            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//        }
//    }
}
