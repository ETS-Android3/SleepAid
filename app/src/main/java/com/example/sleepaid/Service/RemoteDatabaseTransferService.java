package com.example.sleepaid.Service;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.sleepaid.Handler.ServiceHandler;

public class RemoteDatabaseTransferService extends AsyncTask<String, Void, Void> {
    private String url = "https://192.168.0.47/sleepaid.php";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... arg) {
        // Preparing post params
        ContentValues params = new ContentValues();
        params.put("userId", arg[0]);
        params.put("questionnaireId", Integer.valueOf(arg[1]));
        params.put("data", arg[2]);

        ServiceHandler serviceClient = new ServiceHandler();

        serviceClient.makeServiceCall(url, ServiceHandler.POST, params);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
