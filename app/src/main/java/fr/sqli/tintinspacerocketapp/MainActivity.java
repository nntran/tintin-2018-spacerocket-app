package fr.sqli.tintinspacerocketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import fr.sqli.tintinspacerocketapp.server.HttpdServer;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private HttpdServer httpdserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Starting HTTP server
        httpdserver = new HttpdServer();
        try {
            httpdserver.start();
        } catch (IOException ioe) {
            Log.e(TAG, "Erreur lors du lancement du serveur HTTP", ioe);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpdserver.stop();
    }

}
