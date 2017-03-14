package net.skyestudios.simon;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class GameActivity extends AppCompatActivity implements ImageView.OnClickListener {
    GameType gameType;
    String gameTypeString;
    GameLooperTask gameLooper;

    Integer currentRound;
    Integer highestRound;
    ValueAnimator upperLeftCorner_ValueAnimator,
            upperRightCorner_ValueAnimator,
            lowerLeftCorner_ValueAnimator,
            lowerRightCorner_ValueAnimator;

    Gson gson;

    SoundPool soundPool;
    Set<Integer> sounds;

    ImageView upperLeftCorner_ImageView,
            upperRightCorner_ImageView,
            lowerLeftCorner_ImageView,
            lowerRightCorner_ImageView;

    TextView currentRound_TextView, highestRound_TextView;
    Integer pingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gson = new Gson();
        currentRound = 0;

        loadSettings();

        upperLeftCorner_ImageView = (ImageView) findViewById(R.id.upperLeftCorner_ImageView);
        upperRightCorner_ImageView = (ImageView) findViewById(R.id.upperRightCorner_ImageView);
        lowerLeftCorner_ImageView = (ImageView) findViewById(R.id.lowerLeftCorner_ImageView);
        lowerRightCorner_ImageView = (ImageView) findViewById(R.id.lowerRightCorner_ImageView);

        currentRound_TextView = (TextView) findViewById(R.id.currentRound_TextView);
        highestRound_TextView = (TextView) findViewById(R.id.highestRound_TextView);

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

        upperLeftCorner_ValueAnimator = ValueAnimator.ofFloat(1f, 0f);
        upperLeftCorner_ValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        upperLeftCorner_ValueAnimator.setRepeatCount(1);
        upperLeftCorner_ValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upperLeftCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        upperRightCorner_ValueAnimator = ValueAnimator.ofFloat(1f, 0f);
        upperRightCorner_ValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        upperRightCorner_ValueAnimator.setRepeatCount(1);
        upperRightCorner_ValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upperRightCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        lowerLeftCorner_ValueAnimator = ValueAnimator.ofFloat(1f, 0f);
        lowerLeftCorner_ValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        lowerLeftCorner_ValueAnimator.setRepeatCount(1);
        lowerLeftCorner_ValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lowerLeftCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        lowerRightCorner_ValueAnimator = ValueAnimator.ofFloat(1f, 0f);
        lowerRightCorner_ValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        lowerRightCorner_ValueAnimator.setRepeatCount(1);
        lowerRightCorner_ValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lowerRightCorner_ImageView.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        switch (gameType) {
            case vanilla:
                lowerRightCorner_ValueAnimator.setDuration(700);
                lowerLeftCorner_ValueAnimator.setDuration(700);
                upperRightCorner_ValueAnimator.setDuration(700);
                upperLeftCorner_ValueAnimator.setDuration(700);
                break;
            case speed:
                lowerRightCorner_ValueAnimator.setDuration(300);
                lowerLeftCorner_ValueAnimator.setDuration(300);
                upperRightCorner_ValueAnimator.setDuration(300);
                upperLeftCorner_ValueAnimator.setDuration(300);
                break;
            case superSpeed:
                lowerRightCorner_ValueAnimator.setDuration(100);
                lowerLeftCorner_ValueAnimator.setDuration(100);
                upperRightCorner_ValueAnimator.setDuration(100);
                upperLeftCorner_ValueAnimator.setDuration(100);
                break;
        }

        gameLooper = new GameLooperTask(this, gameType);

        sounds = new HashSet<>();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(GameActivity.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false)
                        .setPositiveButton("Let's Play!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                gameLooper.hasStarted = true;
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
            }
        }, 1000);
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

        final AlertDialog alertDialogResults = new AlertDialog.Builder(GameActivity.this)
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
                .create();
        if (!gameLooper.isGameLost) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
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
                            alertDialogResults.show();
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
        } else {
            alertDialogResults.show();
        }

    }

    @Override
    protected void onPause() {
        gameLooper.setPause(true);
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            sounds.clear();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameLooper.setPause(false);

        AudioAttributes.Builder attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder soundPoolBuilder = new SoundPool.Builder()
                .setAudioAttributes(attributes.build())
                .setMaxStreams(8);

        soundPool = soundPoolBuilder.build();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {
                switch (status) {
                    case 0:
                        sounds.add(sampleID);
                        break;
                    default:
                        Log.i("SOUND", "Sound could not be loaded");
                        break;
                }
            }
        });

        pingID = soundPool.load(this, R.raw.ping, 1);
    }

    @Override
    protected void onDestroy() {
        gameLooper.cancel(true);
        super.onStop();
    }

    public void onClick(View view) {
        if (gameLooper.hasStarted) {
            switch (view.getId()) {
                case R.id.upperLeftCorner_ImageView:
                    //blink
                    upperLeftCorner_ValueAnimator.start();
                    //sound
                    soundPool.play(pingID, 1.0f, 1.0f, 0, 0, 1.0f);

                    //check queue inGameLooper
                    if (!gameLooper.isGameLost) {
                        executeSimonCheck(0);
                    }
                    break;
                case R.id.upperRightCorner_ImageView:
                    upperRightCorner_ValueAnimator.start();
                    soundPool.play(pingID, 1.0f, 1.0f, 0, 0, 1.0f);

                    //check queue inGameLooper
                    if (!gameLooper.isGameLost) {
                        executeSimonCheck(1);
                    }

                    break;
                case R.id.lowerLeftCorner_ImageView:
                    lowerLeftCorner_ValueAnimator.start();
                    soundPool.play(pingID, 1.0f, 1.0f, 0, 0, 1.0f);

                    //check queue inGameLooper
                    if (!gameLooper.isGameLost) {
                        executeSimonCheck(2);
                    }

                    break;
                case R.id.lowerRightCorner_ImageView:
                    lowerRightCorner_ValueAnimator.start();
                    soundPool.play(pingID, 1.0f, 1.0f, 0, 0, 1.0f);
                    if (!gameLooper.isGameLost) {
                        executeSimonCheck(3);
                    }


                    break;
            }
        }
    }

    private void executeSimonCheck(int cornerIndex) {
        //check queue inGameLooper
        if (gameLooper.checkSimonQueue(cornerIndex) && gameLooper.simonQueue.isEmpty()) {
            gameLooper.setPause(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(GameActivity.this)
                            .setCancelable(false)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("Round Results")
                            .setMessage("Successful!\n" +
                                    "You moved on to the next round...")
                            .setNegativeButton("I quit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    GameActivity.this.gameLooper.setPause(true);
                                    GameActivity.this.onBackPressed();
                                }
                            })
                            .setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    GameActivity.this.currentRound = ++currentRound;
                                    ((TextView) GameActivity.this.findViewById(R.id.currentRound_TextView)).setText(currentRound.toString());
                                    gameLooper.setPause(false);
                                }
                            })
                            .show();
                }
            }, 700);

        } else {
            if (gameLooper.isGameLost) {
                gameLooper.setPause(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(GameActivity.this)
                                .setCancelable(false)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle("Round Results")
                                .setMessage("Unsuccessful!\n" +
                                        "You failed the round...")
                                .setNegativeButton("Results", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        if (currentRound > highestRound) {
                                            highestRound = currentRound;
                                            highestRound_TextView.setText(highestRound.toString());
                                            saveSettings();
                                        }
                                        GameActivity.this.onBackPressed();
                                    }
                                })
                                .show();
                    }

                }, 700);
            }
        }
    }

    public enum GameType {
        vanilla,
        speed,
        superSpeed
    }
}
