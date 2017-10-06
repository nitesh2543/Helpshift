package com.example.nitesh.myapplication.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.nitesh.myapplication.R;

/**
 * Created by nitesh on 6/10/17.
 */

public class Network {

    /**
     * Check Network is available or not
     *
     * @param context
     * @return True if connected, False otherwise.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null) return networkInfo.isConnected();

        return false;
    }

    /**
     * Display a simple network alert dialog.
     *
     * @param context Android context in which the dialog should be displayed
     */
    public static void showAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setMessage(R.string.no_internet);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}

