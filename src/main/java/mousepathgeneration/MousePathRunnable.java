package mousepathgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class MousePathRunnable implements Runnable {

    public boolean firstThreadIteration = true; // for first time thread is run
    public List<Segment> segmentList;
    public final AtomicReference<ArrayList<SegmentWithTime>> mousePathSegments;


    // var for storing last segment in thread
    public AtomicReference<SegmentWithTime> lastSegmentOfPost;

    private final Semaphore pathReady;
    private final Semaphore played;

    public MousePathRunnable(
            List<Segment> segmentList,
            AtomicReference<ArrayList<SegmentWithTime>> mousePathSegments,
            Semaphore pathReady,
            Semaphore played
    ) {
        this.segmentList = segmentList;
        this.mousePathSegments = mousePathSegments;
        this.pathReady = pathReady;
        this.played = played;
    }

    @Override
    public void run() {
        while (true) {
            try {
                played.acquire(); // wait until player is done
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Generate path
            if (firstThreadIteration) {
                mousePathSegments.set(veryFirstPost(segmentList));
                firstThreadIteration = false;
            } else {
                mousePathSegments.set(
                        QueueSegments.queuePost(mousePathSegments.get().getLast().segment, segmentList)
                );
            }

            pathReady.release(); // let PlayerRunnable go
        }
    }

    private static ArrayList<SegmentWithTime> veryFirstPost(List<Segment> segmentList){
        ArrayList<SegmentWithTime> segmentPath = new ArrayList<SegmentWithTime>();
        segmentPath.add(QueueSegments.queueVeryFirstSegmentInProgram(segmentList));

        // add rest of segments for post
        ArrayList<SegmentWithTime> restOfSegmentsInPath = QueueSegments.queuePost(segmentPath.getFirst().segment, segmentList);

        segmentPath.addAll(restOfSegmentsInPath);

        return segmentPath;
    }
}
