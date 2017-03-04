package net.skyestudios.simon;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout screen;
    TextView preStartMessage_TextView, title_TextView;
    Boolean preStartMessage_TextView_Visibility;
    Button startGame_Button, settings_Button;
    Gson gson;
    GameActivity.GameType gameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        gson = new Gson();

        title_TextView = (TextView) findViewById(R.id.title_TextView);
        screen = (RelativeLayout) findViewById(R.id.activity_menu);
        preStartMessage_TextView = (TextView) findViewById(R.id.preStartMessage_TextView);
        startGame_Button = (Button) findViewById(R.id.startGame_Button);
        settings_Button = (Button) findViewById(R.id.settings_Button);

        final ObjectAnimator OA = ObjectAnimator.ofInt(preStartMessage_TextView, "textColor",
                Color.WHITE, Color.TRANSPARENT);
        OA.setDuration(1200);
        OA.setEvaluator(new ArgbEvaluator());
        OA.setRepeatCount(ValueAnimator.INFINITE);
        OA.setRepeatMode(ValueAnimator.REVERSE);
        OA.start();

        if (savedInstanceState != null) {
            if (preStartMessage_TextView_Visibility) {
                preStartMessage_TextView.setVisibility(View.VISIBLE);

                startGame_Button.setVisibility(View.GONE);
                settings_Button.setVisibility(View.GONE);
            } else {
                preStartMessage_TextView.setVisibility(View.GONE);

                startGame_Button.setVisibility(View.VISIBLE);
                settings_Button.setVisibility(View.VISIBLE);
            }
        } else {
            preStartMessage_TextView_Visibility = true;
            loadSettings();
            if (gameType == null) {
                gameType = GameActivity.GameType.vanilla;
                saveSettings();
            }
        }
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gameType", gson.toJson(gameType));
        editor.commit();
    }

    private void loadSettings() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gameType = gson.fromJson(sharedPreferences.getString("gameType", null), GameActivity.GameType.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Simon")
                .setMessage("Do you really wish to exit Simon?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MenuActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("preStartMessage_TextView_Visibility", preStartMessage_TextView_Visibility);
        outState.putSerializable("gameType", gameType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        preStartMessage_TextView_Visibility = savedInstanceState.getBoolean("preStartMessage_TextView_Visibility");
        gameType = (GameActivity.GameType) savedInstanceState.getSerializable("gameType");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_menu:
                preStartMessage_TextView.setVisibility(View.GONE);

                startGame_Button.setVisibility(View.VISIBLE);
                settings_Button.setVisibility(View.VISIBLE);
                findViewById(R.id.activity_menu).setOnClickListener(null);
                break;
            case R.id.startGame_Button:
                Intent gameActivity_Intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(gameActivity_Intent);
                break;
            case R.id.settings_Button:
                Intent settingsActivity_Intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivity_Intent);
                break;
            default:
                Log.d("DEBUG", "onClick: View not handled");
                break;
        }
    }
}