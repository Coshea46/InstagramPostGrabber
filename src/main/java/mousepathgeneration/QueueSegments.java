package mousepathgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QueueSegments {
    // State tracker inner class
    private static class SegmentState {
        Segment currentSegment;
        double timePassed = 0;
        int[] hotspotCounts = new int[13]; // Adjust if you have more/less hotspots
        int totalSegments = 0;
    }

    // Main method to generate segment path
    public static ArrayList<SegmentWithTime> queuePost(Segment initialState, List<Segment> segmentList) {
        ArrayList<SegmentWithTime> segmentQueue = new ArrayList<>();
        double postDuration = durationOfPost();
        SegmentState state = new SegmentState();
        state.currentSegment = initialState;
        Random rand = new Random();

        while (!NextButton.clickNextButton(state.currentSegment.segmentPoints)) {
            double timeLeft = postDuration - state.timePassed;
            if (timeLeft <= 0) break;

            if (timeLeft < 0.5) {
                // Handle next post segment
                if (!segmentQueue.isEmpty()) {
                    SegmentWithTime nextPostSeg = NextPostSpecialSegment.nextPostSegment(
                            timeLeft,
                            segmentQueue.get(segmentQueue.size() - 1).segment.segmentPoints.get(
                                    segmentQueue.get(segmentQueue.size() - 1).segment.segmentPoints.size() - 1
                            )
                    );
                    segmentQueue.add(nextPostSeg);
                    state.timePassed += nextPostSeg.time + nextPostSeg.pauseAfter;
                }
                break;
            } else if (rand.nextDouble() < 0.1) {
                // Handle like segment
                if (!segmentQueue.isEmpty()) {
                    SegmentWithTime likeSeg = LikePostSpecialSegment.likePostSpecialSegment(
                            segmentQueue.get(segmentQueue.size() - 1).segment.segmentPoints.get(
                                    segmentQueue.get(segmentQueue.size() - 1).segment.segmentPoints.size() - 1
                            )
                    );
                    segmentQueue.add(likeSeg);
                    state.timePassed += likeSeg.time + likeSeg.pauseAfter;
                }
            } else {
                // Handle normal segments
                int lastHotspot = getSafeHotspotID(segmentQueue);
                addNextSegment(segmentQueue, state, segmentList, lastHotspot, postDuration);

                // Add correction segment if needed
                if (segmentQueue.size() >= 3) {
                    int lastIndex = segmentQueue.size() - 1;
                    SegmentWithTime correction = CorrectionSegment.fixPosition(
                            safeToDoublePrimitive(segmentQueue.get(lastIndex - 2).segment.endPoint),
                            safeToDoublePrimitive(segmentQueue.get(lastIndex).segment.startPoint)
                    );
                    segmentQueue.add(lastIndex, correction);
                    state.timePassed += correction.time + correction.pauseAfter;
                    state.totalSegments++;
                }
            }
        }
        return segmentQueue;
    }

    // Helper methods
    private static int getSafeHotspotID(ArrayList<SegmentWithTime> segmentQueue) {
        if (segmentQueue.isEmpty()) return 0;

        Segment lastSegment = segmentQueue.get(segmentQueue.size() - 1).segment;
        Integer hotspotID = lastSegment.endingHotspotID;

        return (hotspotID == null || hotspotID < 0 || hotspotID >= 13) ? 0 : hotspotID;
    }

    private static void addNextSegment(ArrayList<SegmentWithTime> queue, SegmentState state,
                                       List<Segment> segments, int lastHotspot, double postDuration) {
        // Get valid neighbor IDs with fallback
        Integer[] candidateIDs = state.currentSegment.nearestNeighboursIDs;
        if (candidateIDs == null || candidateIDs.length == 0) {
            candidateIDs = new Integer[segments.size()];
            for (int i = 0; i < segments.size(); i++) candidateIDs[i] = i;
        }

        // Pick and validate next segment
        int nextId = ProbabilityCalculator.pickNextSegment(
                state.currentSegment, candidateIDs, segments,
                state.totalSegments, state.hotspotCounts[lastHotspot],
                state.timePassed, postDuration
        );
        nextId = (nextId < 0 || nextId >= segments.size()) ? new Random().nextInt(segments.size()) : nextId;

        // Create and add segment
        SegmentWithTime next = new SegmentWithTime(
                segments.get(nextId),
                segmentDuration(0, 0), // Your existing params
                pauseAfterSegment()
        );
        queue.add(next);

        // Update state
        state.totalSegments++;
        state.currentSegment = next.segment;
        state.timePassed += next.time + next.pauseAfter;

        // Update hotspot count
        if (next.segment.endingHotspotID != null && next.segment.endingHotspotID >= 0 && next.segment.endingHotspotID < 13) {
            state.hotspotCounts[next.segment.endingHotspotID]++;
        }
    }

    public static double durationOfPost() {
        Random rand = new Random();
        double randNum = rand.nextDouble(0,1);
        if (randNum < 0.20) return rand.nextDouble(0,4);
        else if (randNum < 0.50) return rand.nextDouble(4,14);
        else if (randNum < 0.80) return rand.nextDouble(14,18);
        else return rand.nextDouble(18,85);
    }

    public static double[] idleMovementSplit(double time) {
        return new double[]{new Random().nextDouble(0.05,0.10), 0.90};
    }

    public static double segmentDuration(double timeMovingPost, double timeSoFar) {
        Random rand = new Random();
        return (rand.nextDouble() < 0.05) ? rand.nextDouble(1,1.5) : rand.nextDouble(0.03,0.04);
    }

    public static double pauseAfterSegment() {
        Random rand = new Random();
        return (rand.nextDouble() < 0.20) ? rand.nextDouble(2,5) : rand.nextDouble(0,1);
    }

    public static double[] safeToDoublePrimitive(Double[] input) {
        if (input == null) return new double[]{0.0, 0.0};
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = (input[i] != null) ? input[i] : 0.0;
        }
        return result;
    }

    public static SegmentWithTime queueVeryFirstSegmentInProgram(List<Segment> segmentList) {
        Random rand = new Random();
        int randSegmentID = rand.nextInt(0, segmentList.size());
        return new SegmentWithTime(segmentList.get(randSegmentID), rand.nextDouble(0.03,0.04), 0);
    }
}