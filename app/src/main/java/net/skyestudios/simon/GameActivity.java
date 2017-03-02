package net.skyestudios.simon;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

public class GameActivity extends AppCompatActivity implements ImageView.OnClickListener {
    GameType gameType;
    String gameTypeString;
    Gson gson;

    ImageView upperLeftCorner_ImageView,
            upperRightCorner_ImageView,
            lowerLeftCorner_ImageView,
            lowerRightCorner_ImageView;

    TextView currentRound_TextView, highestRound_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        upperLeftCorner_ImageView = (ImageView) findViewById(R.id.upperLeftCorner_ImageView);
        upperRightCorner_ImageView = (ImageView) findViewById(R.id.upperRightCorner_ImageView);
        lowerLeftCorner_ImageView = (ImageView) findViewById(R.id.lowerLeftCorner_ImageView);
        lowerRightCorner_ImageView = (ImageView) findViewById(R.id.lowerRightCorner_ImageView);

        currentRound_TextView = (TextView) findViewById(R.id.currentRound_TextView);
        highestRound_TextView = (TextView) findViewById(R.id.highestRound_TextView);

        gson = new Gson();

        loadSettings();

        switch (gameType) {
            case vanilla:
                gameTypeString = "Vanilla Simon";
                break;
            case speed:
                gameTypeString = "Speed Simon";
                break;
            case superSpeed:
                gameTypeString = "Super Speed Simon";
                break;
        }

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Let's Play!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Let's go back...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setTitle("Simon")
                .setMessage("Game Mode: " + gameTypeString)
                .create()
                .show();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gameTypeString = sharedPreferences.getString("gameType", null);
        gameType = gson.fromJson(gameTypeString, GameType.class);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upperLeftCorner_ImageView:
                //blink
                //sound
                //dequeue if next
                //else round ends
                ////if highestRound < currentRound update it & save to settings
                ////else ask user if they'd like to retry
                break;
            case R.id.upperRightCorner_ImageView:
                break;
            case R.id.lowerLeftCorner_ImageView:
                break;
            case R.id.lowerRightCorner_ImageView:
                break;
        }
    }

    public enum GameType {
        vanilla,
        speed,
        superSpeed
    }
}
