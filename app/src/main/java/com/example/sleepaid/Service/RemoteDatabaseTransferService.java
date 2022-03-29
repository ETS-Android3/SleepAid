package com.example.sleepaid.Service;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.sleepaid.Handler.ServiceHandler;

public class RemoteDatabaseTransferService extends AsyncTask<String, Void, Void> {
    private String url = "https://192.168.0.47/new_testing.php";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... arg) {
        // Preparing post params
        ContentValues params = new ContentValues();
        params.put("userId", arg[0]);
        params.put("questionnaireId", arg[1]);
        params.put("data", arg[2]);

        ServiceHandler serviceClient = new ServiceHandler();

        serviceClient.makeServiceCall(url, ServiceHandler.POST, params);

//            Log.d("Create Prediction Request: ", "> " + json);

//            if (json != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(json);
//                    boolean error = jsonObj.getBoolean("error");
//                    // checking for error node in json
//                    if (!error) {
//                        // new category created successfully
//                        Log.e("Prediction added successfully ",
//                                "> " + jsonObj.getString("message"));
//                    } else {
//                        Log.e("Add Prediction Error: ",
//                                "> " + jsonObj.getString("message"));
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                Log.e("JSON Data", "JSON data error!");
//            }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
