package mousepathplayer;
import screenshotandsend.Screenshot;

import mousepathgeneration.SegmentWithTime;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerRunnable implements Runnable {

    public int counter = 0; // tracks num of times the thread has been played
    final public AtomicReference<ArrayList<SegmentWithTime>> segmentPathToPlay;

    private final Semaphore pathReady;
    private final Semaphore played;

    public PlayerRunnable(
            AtomicReference<ArrayList<SegmentWithTime>> segmentPathToPlay,
            Semaphore pathReady,
            Semaphore played
    ) {
        this.segmentPathToPlay = segmentPathToPlay;
        this.pathReady = pathReady;
        this.played = played;
    }

    @Override
    public void run() {
        while (true) {
            try {
                pathReady.acquire(); // wait for path
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            try {
                Screenshot.screenshot(counter);
                SegmentPlayer.playAllSegmentsInList(segmentPathToPlay.get());
            } catch (AWTException | IOException e) {
                e.printStackTrace();
            }

            counter++;
            played.release(); // let path thread go next
        }
    }


}
