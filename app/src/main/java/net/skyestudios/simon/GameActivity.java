package net.skyestudios.simon;

import android.animation.ValueAnimator;
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
    GameLooperTask gameLooper;

    Integer currentRound;
    Integer highestRound;
    ValueAnimator animator;

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
        animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(300);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(1);
        gson = new Gson();
        currentRound = 0;

        loadSettings();

        highestRound_TextView.setText(highestRound.toString());


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
                        gameLooper.execute();
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
                .setMessage("Game Type: " + gameTypeString)
                .show();

        gameLooper = new GameLooperTask(this, gameType);
    }


    private void saveSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("highestRound", gson.toJson(highestRound));
        editor.commit();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gameTypeString = sharedPreferences.getString("gameType", null);
        highestRound = gson.fromJson(sharedPreferences.getString("highestRound", "\"-1\""), Integer.class);
        if (highestRound == -1) {
            highestRound = 0;
            saveSettings();
        }
        gameType = gson.fromJson(gameTypeString, GameType.class);
    }

    @Override
    public void onBackPressed() {
        gameLooper.setPause(true);//Pauses game
        new AlertDialog.Builder(this)
                .setTitle("Quit Current Game")
                .setMessage("Do you really wish to quit your game?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //show game results on new AlertDialog
                        //save currentRound to highestRound if it is higher
                        //then finally do actual backpress
                        dialogInterface.dismiss();
                        if (currentRound > highestRound) {
                            highestRound = currentRound;
                            highestRound_TextView.setText(highestRound.toString());
                            saveSettings();
                        }
                        new AlertDialog.Builder(GameActivity.this)
                                .setCancelable(false)
                                .setTitle("Game Results")
                                //Set View here instead of message
                                //Only for text alignment^^
                                .setMessage("Current round:  " + currentRound +
                                        "\nHighest round: " + highestRound)
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        GameActivity.super.onBackPressed();
                                    }
                                })
                                .show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        gameLooper.setPause(false);//Resumes Game
                    }
                })
                .setCancelable(false)
                .show();

    }

    @Override
    protected void onPause() {
        gameLooper.setPause(true);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameLooper.setPause(false);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.upperLeftCorner_ImageView:
                //blink
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        upperLeftCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.start();

                //sound

                //check queue inGameLooper
                gameLooper.checkSimonQueue(0);

                //dequeue if next in queue
                //else round ends
                ////if highestRound < currentRound update it & save to settings
                ////else ask user if they'd like to play again
                gameLooper.setPause(false);
                break;
            case R.id.upperRightCorner_ImageView:
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        upperRightCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.start();

                //check queue inGameLooper
                gameLooper.checkSimonQueue(1);

                gameLooper.setPause(false);
                break;
            case R.id.lowerLeftCorner_ImageView:
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        lowerLeftCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.start();

                //check queue inGameLooper
                gameLooper.checkSimonQueue(2);

                gameLooper.setPause(false);
                break;
            case R.id.lowerRightCorner_ImageView:
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        lowerRightCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.start();

                //check queue inGameLooper
                gameLooper.checkSimonQueue(2);

                gameLooper.setPause(false);
                break;
        }
    }

    public enum GameType {
        vanilla,
        speed,
        superSpeed
    }
}
