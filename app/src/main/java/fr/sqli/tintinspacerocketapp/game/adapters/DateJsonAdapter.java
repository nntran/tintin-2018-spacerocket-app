package fr.sqli.tintinspacerocketapp.game.adapters;

import android.icu.text.SimpleDateFormat;
import android.support.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.util.Date;

public class DateJsonAdapter extends JsonAdapter<java.util.Date> {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:MM:ss");

    @Nullable
    @Override
    public Date fromJson(JsonReader reader) throws IOException {
        String strDate = reader.nextString();
        if (strDate != null && !strDate.equals("")) {
            String tab[] = strDate.split(" ");
            try {
                return dateFormat.parse(tab[0] + " " + tab[1]);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void toJson(JsonWriter writer, @Nullable Date value) throws IOException {
        String strDate = value == null  ? "" : dateFormat.format(value);
        writer.value(strDate);
    }
}
