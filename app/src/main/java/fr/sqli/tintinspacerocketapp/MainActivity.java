package fr.sqli.tintinspacerocketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import fr.sqli.tintinspacerocketapp.led.LEDManager;
import fr.sqli.tintinspacerocketapp.server.HttpdServer;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private HttpdServer httpdserver;



    private Button exitButton;
    private Button blueLEDButton;
    private Button yellowLEDButton;
    private Button redLEDButton;
    private Button greenLEDButton;
    private LEDManager ledManagerInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initButtons();

        // Starting HTTP server
        httpdserver = new HttpdServer();
        try {
            httpdserver.start();
        } catch (IOException ioe) {
            Log.e(TAG, "Erreur lors du lancement du serveur HTTP", ioe);
        }
        try {
            ledManagerInstance = LEDManager.getInstance();
            ledManagerInstance.turnOnAllLEDs();
        } catch (IOException ioe) {
            Log.e(TAG, "Erreur lors de l'initialisation des LEDS", ioe);
        }

    }

    private void initButtons() {
        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });

        View.OnClickListener buttonsLEDOnclickListenner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.blueLEDButton:
                        try {
                            ledManagerInstance.blueLED.toggle();
                        } catch (IOException ioe) {
                            Log.e(TAG, "", ioe);
                        }
                        break;
                    case R.id.yellowLEDButton:
                        try {
                            ledManagerInstance.yellowLED.toggle();
                        } catch (IOException ioe) {
                            Log.e(TAG, "", ioe);
                        }
                        break;
                    case R.id.redLEDButton:
                        try {
                            ledManagerInstance.redLED.toggle();
                        } catch (IOException ioe) {
                            Log.e(TAG, "", ioe);
                        }
                        break;
                    case R.id.greenLEDButton:
                        try {
                            ledManagerInstance.greenLED.toggle();
                        } catch (IOException ioe) {
                            Log.e(TAG, "", ioe);
                        }
                        break;
                }
            }
        };

        blueLEDButton = findViewById(R.id.blueLEDButton);
        yellowLEDButton = findViewById(R.id.yellowLEDButton);
        redLEDButton = findViewById(R.id.redLEDButton);
        greenLEDButton = findViewById(R.id.greenLEDButton);

        blueLEDButton.setOnClickListener(buttonsLEDOnclickListenner);
        yellowLEDButton.setOnClickListener(buttonsLEDOnclickListenner);
        redLEDButton.setOnClickListener(buttonsLEDOnclickListenner);
        greenLEDButton.setOnClickListener(buttonsLEDOnclickListenner);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        try {
            LEDManager.getInstance().destroy();
        } catch (IOException ioe) {
            Log.e(TAG, "", ioe);
        }

        httpdserver.stop();
    }

}
