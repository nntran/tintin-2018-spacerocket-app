package fr.sqli.tintinspacerocketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.things.device.TimeManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import fr.sqli.tintinspacerocketapp.game.SimonGame;
import fr.sqli.tintinspacerocketapp.led.LEDColors;
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
    private Button launchSequenceButton;
    private LEDManager ledManagerInstance;
    private TextView infosText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initView();

        // Setting time zone
        //String command = "setprop persist.sys.timezone Europe/Paris";
        //try {
        //    Runtime.getRuntime().exec(command);
        //} catch (IOException ioe){
        //    Log.e(TAG, "Erreur de configuration du TimeZone", ioe);
        //}
        // Set time zone
        TimeManager timeManager = TimeManager.getInstance();
        // Use 24-hour time
        timeManager.setTimeFormat(TimeManager.FORMAT_24);
        // Set time zone to Europe/Paris Time
        timeManager.setTimeZone("Europe/Paris");

        try {// Starting HTTP server
            httpdserver = new HttpdServer();
            httpdserver.start();
        } catch (IOException ioe) {
            Log.e(TAG, "Erreur lors du lancement du serveur HTTP", ioe);
        }
        try {
            ledManagerInstance = LEDManager.getInstance();
            ledManagerInstance.startWelcomeSequence();
        } catch (IOException ioe) {
            Log.e(TAG, "Erreur lors de l'initialisation des LEDS", ioe);
        }
    }

    private void initView() {
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

        launchSequenceButton = findViewById(R.id.launchSequenceButton);
        launchSequenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LEDColors[] ledColors = {
                        LEDColors.BLUE,
                        LEDColors.YELLOW,
                        LEDColors.RED,
                        LEDColors.GREEN,
                        LEDColors.GREEN,
                        LEDColors.GREEN
                };
                ledManagerInstance.launchSequence(ledColors, true);
            }
        });

        // Affichage des infos (IP, stats, ...)
        infosText = findViewById(R.id.infosText);
        infosText.setTextSize(14);
        infosText.setText(getInfos());

        // Démarrage du jeu Simon
        try {
            SimonGame.getInstance();
        } catch (IOException ex) {
            //
        }

    }

    /**
     * Récup des infos, stats à afficher
     * @return
     */
    private String getInfos() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("----------------------------------------------------");
        try {
            // getting the list of interfaces in the local machine
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            while (n.hasMoreElements()) { //for each interface

                NetworkInterface e = n.nextElement();
                //name of the interface
                buffer.append("\nInterface Name: " + e.getName());
                //A interface may be binded to many IP addresses like IPv4 and IPv6
                //hence getting the Enumeration of list of IP addresses
                Enumeration<InetAddress> a = e.getInetAddresses();
                while (a.hasMoreElements()) {
                    InetAddress addr = a.nextElement();
                    String add = addr.getHostAddress().toString();
                    if (add.length() < 17)
                        buffer.append("\nIPv4 Address: " + add);
                    else
                        buffer.append("\nIPv6 Address: " + add);
                }
                if (e.getHardwareAddress() != null) {
                    // getting the mac address of the particular network interface
                    byte[] mac = e.getHardwareAddress();
                    // properly formatting the mac address
                    StringBuilder macAddress = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%03X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    buffer.append("\nHardware adrress: " + macAddress.toString());
                }
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "", ex);
        }

        // Autres infos ici


        buffer.append("\n----------------------------------------------------");

        Log.d(TAG, buffer.toString());

        return buffer.toString();
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
