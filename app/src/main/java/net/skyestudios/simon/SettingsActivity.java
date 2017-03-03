package net.skyestudios.simon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {

    private GameActivity.GameType gameType;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        gson = new Gson();
        loadSettings();
        final Spinner spinner = (Spinner) findViewById(R.id.gameTypeOptions_Spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.game_types, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        switch (gameType) {
            case vanilla:
                spinner.setSelection(adapter.getPosition("Vanilla Simon"));
                break;
            case speed:
                spinner.setSelection(adapter.getPosition("Speed Simon"));
                break;
            case superSpeed:
                spinner.setSelection(adapter.getPosition("Super Speed Simon"));
                break;
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) spinner.getSelectedItem();
                switch (item) {
                    case "Vanilla Simon":
                        gameType = GameActivity.GameType.vanilla;
                        break;
                    case "Speed Simon":
                        gameType = GameActivity.GameType.speed;
                        break;
                    case "Super Speed Simon":
                        gameType = GameActivity.GameType.superSpeed;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gameType", gson.toJson(gameType));
        editor.commit();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = sharedPreferences.getString("gameType", null);
        gameType = gson.fromJson(json, GameActivity.GameType.class);
    }
}
