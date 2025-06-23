import mousepathgeneration.*;
import mousepathplayer.*;
import screenshotandsend.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) throws Exception {
        List<Segment> segments = SegmentLoader.loadSegments("SegmentsWithHotspots.json");

        AtomicReference<ArrayList<SegmentWithTime>> sharedPath = new AtomicReference<>(new ArrayList<>());
        Semaphore pathReady = new Semaphore(0);
        Semaphore played = new Semaphore(1);

        Thread mousePathThread = new Thread(new MousePathRunnable(segments, sharedPath, pathReady, played));
        Thread playerThread = new Thread(new PlayerRunnable(sharedPath, pathReady, played));
        mousePathThread.start();
        playerThread.start();

        mousePathThread.join();
        playerThread.join();
    }

}
