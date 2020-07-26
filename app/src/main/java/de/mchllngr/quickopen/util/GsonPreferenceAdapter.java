package de.mchllngr.quickopen.util;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.f2prateek.rx.preferences.Preference;
import com.google.gson.Gson;

/**
 * {@link com.f2prateek.rx.preferences.Preference.Adapter} to allow automatic conversion between
 * a class T and a JSON-{@link String} via {@link Gson}.
 */
public class GsonPreferenceAdapter<T> implements Preference.Adapter<T> {
    private final Gson gson;
    private Class<T> clazz;

    /**
     * Constructor for initialising.
     *
     * @param gson  {@link Gson}
     * @param clazz {@link Class}
     */
    public GsonPreferenceAdapter(Gson gson, Class<T> clazz) {
        this.gson = gson;
        this.clazz = clazz;
    }

    @Override
    public T get(@NonNull String key, @NonNull SharedPreferences preferences) {
        return gson.fromJson(preferences.getString(key, ""), clazz);
    }

    @Override
    public void set(@NonNull String key,
                    @NonNull T value,
                    @NonNull SharedPreferences.Editor editor) {
        editor.putString(key, gson.toJson(value));
    }
}
