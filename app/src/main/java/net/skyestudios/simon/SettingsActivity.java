package net.skyestudios.simon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {

    private GameActivity.GameType gameType;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    private void saveSettings() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gameType", gson.toJson(gameType));
        editor.apply();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        gameType = gson.fromJson(sharedPreferences.getString("", ""), GameActivity.GameType.class);
    }
}
