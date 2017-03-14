package net.skyestudios.simon;

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
    Boolean isLocked, isGameLost, hasStarted;
    Integer currentRound;
    GameActivity.GameType gameType;
    LinkedBlockingQueue<Integer> simonQueue;
    Random random;


    public GameLooperTask(GameActivity gameActivity, GameActivity.GameType gameType) {
        this.gameActivity = gameActivity;
        this.gameLoopLock = new Object();
        this.isLocked = false;
        this.isGameLost = false;
        this.hasStarted = false;
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
                        Log.i("INFO", "doInBackground: game has been cancel and user has to retry");
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
                    for (Integer position :
                            simonQueue) {
                        hasStarted = false;
                        showSimonSequence(position);
                        hasStarted = true;
                    }
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

    private void showSimonSequence(Integer position) {
        try {
            switch (gameType) {
                case vanilla:
                    Thread.sleep(1500);
                    break;
                case speed:
                    Thread.sleep(900);
                    break;
                case superSpeed:
                    Thread.sleep(500);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (position) {
            case 0:
                if (!isLocked) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameActivity.upperLeftCorner_ValueAnimator.start();
                            gameActivity.soundPool.play(gameActivity.pingID, 1.0f, 1.0f, 0, 0, 1.0f);
                        }
                    });
                }
                break;
            case 1:
                if (!isLocked) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameActivity.upperRightCorner_ValueAnimator.start();
                            gameActivity.soundPool.play(gameActivity.pingID, 1.0f, 1.0f, 0, 0, 1.0f);
                        }
                    });
                }
                break;
            case 2:
                if (!isLocked) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameActivity.lowerLeftCorner_ValueAnimator.start();
                            gameActivity.soundPool.play(gameActivity.pingID, 1.0f, 1.0f, 0, 0, 1.0f);
                        }
                    });
                }
                break;
            case 3:
                if (!isLocked) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameActivity.lowerRightCorner_ValueAnimator.start();
                            gameActivity.soundPool.play(gameActivity.pingID, 1.0f, 1.0f, 0, 0, 1.0f);
                        }
                    });
                }
                break;
            default:
                Log.i("INFO", "showSimonSequence: Unhandled position!");
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
                hasStarted = false;
            }
            return true;
        } else {
            isGameLost = true;
            setPause(true);
            return false;
        }
    }
}
