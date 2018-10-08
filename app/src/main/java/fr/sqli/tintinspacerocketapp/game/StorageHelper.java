package fr.sqli.tintinspacerocketapp.game;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sqli.tintinspacerocketapp.led.LEDColors;


/**
 * Helper class for read / write Gamer JSON
 */
public class StorageHelper {

    private static final String TAG = StorageHelper.class.getName();

    // Nom du dossier de stockage des données du jeu
    private static final String STORAGE_DIRECTORY_NAME = "simon-game";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:MM:ss");

    // Interdire l'instanciation
    private StorageHelper() {
        //
    }

    /**
     * Read gamers from storage for the giving date or <code>null</code> value for all
     * @param date
     * @return
     * @throws IOException
     */
    public static Map<Integer, Gamer> read(Date date) throws IOException {
        final String today = (date == null ? null : dateFormat.format(date));
        if (today != null)
            Log.i(TAG, "Chargement des fichiers JSON de la journée " + date);
        else
            Log.i(TAG, "Chargement de tous les fichiers JSON");

        File storageDir = getExternalStorageDir(STORAGE_DIRECTORY_NAME);
        if (storageDir == null)
            throw new IOException("Impossible de récupérer le dossier " + STORAGE_DIRECTORY_NAME);

        File files[] = storageDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return today == null || name.startsWith(today);
            }
        });

        Map<Integer, Gamer> gamers = new HashMap<>();
        if (files != null && files.length > 0) {
            // Conversion du flux JSON en gamer
            for (File file : files) {
                Log.i(TAG, "Chargement du fichier " + file.getName());
                Gamer gamer = read(file);
                gamers.put(gamer.gamerId, gamer);
            }
            Log.i(TAG, "Nombre de joueurs chargés: " + gamers.size());
        }

        return gamers;
    }

    /**
     * Read and parse a JSON file to a Gamer object
     * @param file
     * @return
     * @throws IOException
     */
    public static Gamer read(File file) throws IOException {
        JsonReader reader = new JsonReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"));
        try {
            return readGamer(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Enregistrer les informations du joueur dans un fichier JSON
     * @param gamer
     * @throws IOException
     */
    public static void write(Gamer gamer) throws IOException {

        Log.i(TAG, "Sauvegarde du joueur: "
                + gamer.gamerId
                + "("+ gamer.gamerFirstname + " " + gamer.gamerLastname + ")");

        if (!isExternalStorageWritable())
            throw new IOException("Impossible d'écrire sur le média externe");

        File storageDir = getExternalStorageDir(STORAGE_DIRECTORY_NAME);
        if (storageDir == null)
            throw new IOException("Impossible de récupérer le dossier " + STORAGE_DIRECTORY_NAME);

        // TODO : A améliorer
        Date today = new Date();
        gamer.startDateTime = dateFormat.format(today) + " " + timeFormat.format(today);

        // Conversion de l'objet gamer en JSON
        Moshi moshi = new Moshi.Builder()
                //.add(Date.class, new DateJsonAdapter())
                //.add(Date.class, new Rfc3339DateJsonAdapter())
                .build();

        JsonAdapter<Gamer> jsonAdapter = moshi.adapter(Gamer.class);
        String json = jsonAdapter.toJson(gamer);
        Log.d(TAG, "Gamer JSON: " + json);

        // Sauvegarde du flux JSON dans un fichier (format: AAAAMMJJ-<ID JOUEUR>.json)
        //String fileName = dateFormat.format(gamer.startDateTime) + "-" + gamer.gamerId + ".json";
        String fileName = dateFormat.format(today) + "-" + gamer.gamerId + ".json";
        File jsonFile = new File(storageDir, fileName);
        FileOutputStream outputStream  = null;
        try {
            outputStream = new FileOutputStream(jsonFile);
            outputStream.write(json.getBytes());
        } catch (Exception e) {
            throw new IOException("Erreur de sauvegarde du fichier JSON: " + jsonFile.getAbsolutePath());
        }
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    /**
     * Read a gamer JSON
     * @param reader
     * @return
     * @throws IOException
     */
    private static Gamer readGamer(JsonReader reader) throws IOException {

        Gamer gamer = new Gamer();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("gamerId")) {
                gamer.gamerId = reader.nextInt();
            } else if (name.equals("gamerFirstname")) {
                gamer.gamerFirstname = reader.nextString();
            } else if (name.equals("gamerLastname")) {
                gamer.gamerLastname = reader.nextString();
            } else if (name.equals("gamerEmail")) {
                gamer.gamerEmail = reader.nextString();
            } else if (name.equals("gamerCompany")) {
                gamer.gamerCompany = reader.nextString();
            } else if (name.equals("gamerContact")) {
                gamer.gamerContact = reader.nextBoolean();
            } else if (name.equals("gamerResume")) {
                gamer.gamerResume = reader.nextBoolean();
            } else if (name.equals("isDemo")) {
                gamer.isDemo = reader.nextBoolean();
            } else if (name.equals("sequence") && reader.peek() != JsonToken.NULL) {
                gamer.sequence.addAll(readSequenceList(reader));
            } else if (name.equals("score")) {
                gamer.score = reader.nextInt();
            } else if (name.equals("time")) {
                gamer.time = reader.nextLong();
            } else if (name.equals("startDateTime")) {
                gamer.startDateTime = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return gamer;
    }

    /**
     * Lecture de la sequence des couleurs
     * @param reader
     * @return
     * @throws IOException
     */
    private static List<LEDColors> readSequenceList(JsonReader reader) throws IOException {
        List<LEDColors> sequence = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            String colorName = reader.nextString();
            if (colorName.equals("RED")) {
                sequence.add(LEDColors.RED);
            } else if (colorName.equals("BLUE")) {
                sequence.add(LEDColors.BLUE);
            } else if (colorName.equals("YELLOW")) {
                sequence.add(LEDColors.YELLOW);
            } else {
                sequence.add(LEDColors.GREEN);
            }
        }
        reader.endArray();

        return sequence;
    }

    /**
     *  Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     *
     * @param directoryName
     * @return
     */
    public static File getExternalStorageDir(String directoryName) {
        // Get the directory for the user's.
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), directoryName);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Erreur de création du répertoire " + directoryName);
            }
        }
        return dir;
    }
}
