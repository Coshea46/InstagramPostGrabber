package mousepathgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class which queues the mouse path for the next post
 */

public class QueueSegments {
    ArrayList<SegmentWithTime> pathList;  // arrayList of tuple objects with segments, their durations and pauses after words

    // method to decide duration of post viewing
    public static double durationOfPost(){
        Random rand = new Random();
        double randNum = rand.nextDouble(0,1);  // random number in interval [0,1)
        double postDuration;

        if(randNum<0.20){  // q1
            postDuration = rand.nextDouble(0,4); // 20% chance of viewing duration being between 0 and 4 seconds
        }
        else if(randNum >= 0.20 && randNum < 0.50){
            postDuration = rand.nextDouble(4,14);
        }
        else if(randNum >= 0.50 && randNum < 0.80){
            postDuration = rand.nextDouble(14,18);
        }
        else {
            postDuration = rand.nextDouble(18,85);
        }

        return postDuration;
    }

    // method to decide time split of idle and mouse movement per post
    // return percentage as decimal
    private static double[] idleMovementSplit(double time){
        Random rand = new Random();
        double timeMoving = rand.nextDouble(0.05,0.10);

        // elem 1 is time percentage moving
        // elem 2 is time percentage idle
        return new double[]{timeMoving,(1-timeMoving)};
    }

    // method to decide duration of segment
    public static double segmentDuration(double timeMovingPost, double timeSoFar){
        Random rand = new Random();

        double randNum = rand.nextDouble(0,1);
        double randNum2 = 0;

        if(randNum<0.05){
            randNum2 = rand.nextDouble(1,1.5);

        }
        else{
            randNum2 = rand.nextDouble(0.03,0.04);
        }

        return randNum2;
    }


    // queue segments action method FOR QUEUEING ANOTHER POST, SHOULD NOT BE USED FOR VIEWING FIRST POST WHEN PROGRAM RAN
    public static ArrayList<SegmentWithTime> queuePost(Segment previousSegment, List<Segment> segmentList){
        ArrayList<SegmentWithTime> segmentQueue = new ArrayList<>();
        double postDuration = durationOfPost();
        double[] idleMovementSplit = idleMovementSplit(postDuration);
        final double initialTimeMoving = idleMovementSplit[0]*postDuration;
        final double initialTimeIdle = idleMovementSplit[1]*postDuration;

        int numOfSegments = 0;
        double timeLeftMoving = initialTimeIdle; // possibly don't need anymore (the idle and moving stuff)
        double timeLeftIdle = initialTimeIdle;
        double timePassed = 0;
        double timeLeft = postDuration;
        int[] numOfSegmentsCurrentHotspot = new int[13];
        boolean updateHotSpotArray = true;

        while((!NextButton.clickNextButton(previousSegment.segmentPoints)) && timeLeft != 0){
            if((timeLeft)<0.5){
                // invoke special next post segment here
                segmentQueue.add(NextPostSpecialSegment.nextPostSegment(timeLeft, segmentQueue.get(numOfSegments).segment.segmentPoints.getLast()));
                // need to also break while loop after invoking this specific segment
                timeLeft = 0;
            }
            else if((new Random()).nextDouble()<0.1){
                // invoke like post segment here
                segmentQueue.add(LikePostSpecialSegment.likePostSpecialSegment(segmentQueue.get(numOfSegments).segment.segmentPoints.getLast()));
                updateHotSpotArray = false;
            }
            else{
                if(segmentQueue.isEmpty()){
                    addSegmentToQueue(segmentQueue, previousSegment,segmentList,numOfSegments, 0, timePassed, postDuration);
                }
                else{
                    addSegmentToQueue(segmentQueue, previousSegment,segmentList,numOfSegments, numOfSegmentsCurrentHotspot[segmentQueue.get(numOfSegments).segment.endingHotspotID], timePassed, postDuration);

                }
                if(segmentQueue.size()>=3){
                    segmentQueue.add(
                            segmentQueue.size() - 1,
                            CorrectionSegment.fixPosition(
                                    toDoublePrimitive(segmentQueue.get(segmentQueue.size() - 3).segment.endPoint),
                                    toDoublePrimitive(segmentQueue.getLast().segment.startPoint)
                            )
                    );
                    timeLeft -= segmentQueue.get(segmentQueue.size()-2).time - segmentQueue.get(segmentQueue.size()-2).pauseAfter;
                    numOfSegments++;
                }

            }
            numOfSegments = numOfSegments + 1;

            if(updateHotSpotArray){
                numOfSegmentsCurrentHotspot[segmentQueue.get(numOfSegments).segment.endingHotspotID] += 1;
            }

            if(timeLeft != 0) {
                timeLeft = timeLeft - (segmentQueue.get(numOfSegments).time) - (segmentQueue.get(numOfSegments).pauseAfter);
            }
            numOfSegments++;
            updateHotSpotArray = true;
        }
        return segmentQueue;
    }

    private static void addSegmentToQueue(ArrayList<SegmentWithTime> segmentQueue, Segment previousSegment, List<Segment> segmentList, int numOfSegmentsSoFar, int numOfSegmentsCurrentHotspot, double timePassed, double timeForPost){
        int nextSegmentID = ProbabilityCalculator.pickNextSegment(previousSegment, previousSegment.nearestNeighboursIDs, segmentList, numOfSegmentsSoFar, numOfSegmentsCurrentHotspot, timePassed, timeForPost);
        double newSegmentDuration = segmentDuration(timePassed, timeForPost);
        double pauseAfterSegment = pauseAfterSegment();

        SegmentWithTime nextSegment = new SegmentWithTime(segmentList.get(nextSegmentID), newSegmentDuration, pauseAfterSegment);
        segmentQueue.add(nextSegment);

    }

    // method to determine how long to pause after a segment (if any pause)
    private static double pauseAfterSegment(){
        Random rand = new Random();

        double randNum = rand.nextDouble(0,1);
        if(randNum<0.20){
            randNum = rand.nextDouble(2,5);
        }
        else{
            randNum = rand.nextDouble(0,1);
        }

        return randNum;
    }

    public static double[] toDoublePrimitive(Double[] input) {
        if (input == null) {
            return null; // Or throw an IllegalArgumentException if you prefer
        }
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i]; // Auto-unboxing
        }
        return result;
    }

    // Method for queueing very first segment of every post
    // picks a random segment to start at, moves to the start point of that segment and plays it

    public static SegmentWithTime queueVeryFirstSegmentInProgram(List<Segment> segmentList){
        Random rand = new Random();
        int randSegmentID = rand.nextInt(0,segmentList.size());

        double segmentTimeLength = rand.nextDouble(0.03,0.04);
        double segmentPauseAfter = 0;

        return new SegmentWithTime(segmentList.get(randSegmentID), segmentTimeLength, segmentPauseAfter);
    }

}
