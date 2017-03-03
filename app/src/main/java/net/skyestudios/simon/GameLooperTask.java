package net.skyestudios.simon;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by arkeonet64 on 3/2/2017.
 */

public class GameLooperTask extends AsyncTask<Void, Void, Void> {
    GameActivity gameActivity;
    Object gameLoopLock;
    Boolean isLocked;
    Integer currentRound;
    GameActivity.GameType gameType;
    LinkedBlockingQueue<Integer> simonQueue;
    Random random;


    public GameLooperTask(GameActivity gameActivity, GameActivity.GameType gameType) {
        this.gameActivity = gameActivity;
        this.gameLoopLock = new Object();
        this.isLocked = false;
        this.currentRound = 1;
        gameActivity.currentRound = currentRound;
        this.gameType = gameType;
        this.random = new Random(System.currentTimeMillis());
        this.simonQueue = new LinkedBlockingQueue<>();
    }

    @Override
    protected Void doInBackground(Void... Voids) {
        while (!isCancelled()) {
            synchronized (gameLoopLock) {
                while (isLocked && !isCancelled()) {
                    try {
                        Log.i("INFO", "doInBackground: game is paused");
                        gameLoopLock.wait();
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(gameActivity)
                                        .setCancelable(false)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setTitle("Round Results")
                                        .setMessage("Successful!\n" +
                                                "You moved on to the next round...")
                                        .setNegativeButton("I quit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                gameActivity.gameLooper.setPause(true);
                                                gameActivity.onBackPressed();
                                            }
                                        })
                                        .setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                gameActivity.currentRound = ++currentRound;
                                                ((TextView) gameActivity.findViewById(R.id.currentRound_TextView)).setText(currentRound.toString());
                                            }
                                        })
                                        .show();
                            }
                        });
                        Log.i("INFO", "doInBackground: game is unpaused");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (!isLocked && !isCancelled()) {
                    Log.i("INFO", "doInBackground: game is running");
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) gameActivity.findViewById(R.id.currentRound_TextView)).setText(currentRound.toString());
                        }
                    });
                    addSimonSequence();
                    showSimonSequence();
                    setPause(true);
                }
            }
        }
        return null;
    }

    private void addSimonSequence() {
        simonQueue.clear();
        for (int i = 0; i < currentRound; i++) {
            simonQueue.add(random.nextInt(3 + 1));
        }
    }

    private void showSimonSequence() {
        for (Integer position :
                simonQueue) {
            final ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
            animator.setDuration(300);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(1);
            //blink image
            //make sound
            switch (position) {
                case 0:
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animator.setTarget(gameActivity.upperLeftCorner_ImageView);
                            animator.start();
                        }
                    });
                    break;
                case 1:
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animator.setTarget(gameActivity.upperRightCorner_ImageView);
                            animator.start();
                        }
                    });
                    break;
                case 2:
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animator.setTarget(gameActivity.lowerLeftCorner_ImageView);
                            animator.start();
                        }
                    });
                    break;
                case 3:
//                    use handler to do this
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animator.setTarget(gameActivity.lowerRightCorner_ImageView);
                            animator.start();
                        }
                    });
                    break;
                default:
                    Log.i("INFO", "showSimonSequence: Unhandled position!");
            }
        }
    }

    public Boolean getGameLoopLock() {
        return isLocked;
    }

    public void setPause(Boolean gameLock) {
        synchronized (gameLoopLock) {
            isLocked = gameLock;
            if (!isLocked) {
                gameLoopLock.notifyAll();
            }
        }
    }

    public void checkSimonQueue(Integer cornerIndex) {
        if (simonQueue.peek() == cornerIndex) {
            simonQueue.remove();
        } else {
            setPause(true);
        }
    }
}
