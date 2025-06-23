package mousepathgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class MousePathRunnable implements Runnable {

    private final List<Segment> segmentList;
    private final AtomicReference<ArrayList<SegmentWithTime>> mousePathSegments;
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
        Segment prevSegment = null;
        boolean firstIteration = true;

        while (true) {
            try {
                played.acquire();  // wait for player to finish last iteration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            ArrayList<SegmentWithTime> newPath;
            if (firstIteration) {
                newPath = veryFirstPost(segmentList);
                firstIteration = false;
            } else {
                // generate next path based on endpoint of previous path
                newPath = QueueSegments.queuePost(prevSegment, segmentList);
            }

            mousePathSegments.set(newPath);

            // update prevSegment for next iteration
            if (!newPath.isEmpty()) {
                prevSegment = newPath.get(newPath.size() - 1).segment;
            }

            pathReady.release();  // signal player to start
        }
    }

    private static ArrayList<SegmentWithTime> veryFirstPost(List<Segment> segmentList) {
        ArrayList<SegmentWithTime> segmentPath = new ArrayList<>();
        // first segment
        SegmentWithTime first = QueueSegments.queueVeryFirstSegmentInProgram(segmentList);
        segmentPath.add(first);

        // rest of path
        ArrayList<SegmentWithTime> rest = QueueSegments.queuePost(first.segment, segmentList);
        segmentPath.addAll(rest);

        return segmentPath;
    }
}
