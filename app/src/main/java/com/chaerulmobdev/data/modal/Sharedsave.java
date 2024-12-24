package com.chaerulmobdev.data.modal;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class Sharedsave {
    private final SharedPreferences shared;
    private final SharedPreferences.Editor editor;

    public Sharedsave(Context context) {
        shared = context.getSharedPreferences("users", Context.MODE_PRIVATE);
        editor = shared.edit();
    }

    // Menyimpan semua data dari Map
    public void putAll(Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } else if (value instanceof Float) {
                    editor.putFloat(key, (Float) value);
                } else if (value instanceof Long) {
                    editor.putLong(key, (Long) value);
                }
            }
            editor.apply(); // Terapkan perubahan
        }
    }

    // Menyimpan data String
    public void putString(String key, String value) {
        editor.putString(key, value).apply();
    }

    // Menyimpan data Boolean
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    // Mengambil data String
    public String getString(String key) {
        return shared.getString(key, "");
    }

    // Mengambil data Boolean
    public boolean getBoolean(String key) {
        return shared.getBoolean(key, false);
    }

    // Menghapus data berdasarkan kunci
    public void remove(String key) {
        editor.remove(key).apply();
    }

    // Menghapus semua data
    public void clear() {
        editor.clear().apply();
    }

    // Memeriksa apakah kunci tertentu ada
    public boolean contains(String key) {
        return shared.contains(key);
    }
}
