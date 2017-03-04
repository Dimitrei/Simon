package net.skyestudios.simon;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
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
    Boolean isLocked, isGameLost;
    Integer currentRound;
    GameActivity.GameType gameType;
    LinkedBlockingQueue<Integer> simonQueue;
    Random random;


    public GameLooperTask(GameActivity gameActivity, GameActivity.GameType gameType) {
        this.gameActivity = gameActivity;
        this.gameLoopLock = new Object();
        this.isLocked = false;
        this.isGameLost = false;
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
            simonQueue.add(random.nextInt(4));
        }
    }

    private void showSimonSequence() {
        for (Integer position :
                simonQueue) {
            //blink image
            //make sound
            synchronized (gameLoopLock) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                switch (position) {
                    case 0:
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameActivity.animator.removeAllUpdateListeners();
                                gameActivity.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        gameActivity.upperLeftCorner_ImageView.setAlpha((Float) valueAnimator.getAnimatedValue());
                                    }
                                });
                                gameActivity.animator.start();
                            }
                        });
                        break;
                    case 1:
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameActivity.animator.removeAllUpdateListeners();
                                gameActivity.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        gameActivity.upperRightCorner_ImageView.setAlpha((Float) valueAnimator.getAnimatedValue());
                                    }
                                });
                                gameActivity.animator.start();
                            }
                        });
                        break;
                    case 2:
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameActivity.animator.removeAllUpdateListeners();
                                gameActivity.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        gameActivity.lowerLeftCorner_ImageView.setAlpha((Float) valueAnimator.getAnimatedValue());
                                    }
                                });
                                gameActivity.animator.start();
                            }
                        });
                        break;
                    case 3:
//                    use handler to do this
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameActivity.animator.removeAllUpdateListeners();
                                gameActivity.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        gameActivity.lowerRightCorner_ImageView.setAlpha((Float) valueAnimator.getAnimatedValue());
                                    }
                                });
                                gameActivity.animator.start();
                            }
                        });
                        break;
                    default:
                        Log.i("INFO", "showSimonSequence: Unhandled position!");
                }
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

    public Boolean checkSimonQueue(Integer cornerIndex) {
        if (simonQueue.peek() == cornerIndex) {
            simonQueue.remove();
            if (simonQueue.isEmpty()) {
                currentRound++;
            }
            return true;
        } else {
            isGameLost = true;
            setPause(true);
            return false;
        }
    }
}
