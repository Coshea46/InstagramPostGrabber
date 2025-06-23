package mousepathgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbabilityCalculator {

    private final static int segmentCountThreshold = 110;  // for function 1
    private final static int stayInHotspotThresholdCount = 30; // for function 4

    // Function 1
    // invoke after segmentCountThreshold num of segments have played
    public static double hotSpotOneFactor(int segmentCount) {
        int argument = segmentCount - segmentCountThreshold;
        double result;

        result = (-(5 * (argument - 91) * (argument - 91)) / (91.0 * 91.0)) + 5;

        return result;
    }

    // Function 2
    // invoke after segmentTimeThreshold num of seconds has passed on current post
    public static double timeFactor(double timePassed, double timeForPost){
        double segmentTimeThreshold = timeForPost/2;
        double argument = timePassed - segmentTimeThreshold;
        double result;

        result = (-(5 * (argument - 8) * (argument - 8)) / (64)) + 5;

        return result;
    }


    // Function 3
    // invoke from start
    public static double distanceFactor(double euclideanDistance){
        double result;

        result = 5* (Math.exp(-euclideanDistance));

        return result;
    }

    // Function 4
    // piecewise, always decreasing

    public static double stayingInHotspotFactor(int numOfSegmentsInHotspot){
        double result;

        if(numOfSegmentsInHotspot < stayInHotspotThresholdCount){
            result = (double) (5 * ((numOfSegmentsInHotspot - 30) * (numOfSegmentsInHotspot - 30))) /(30*30);
            return result;
        }
        else{
            result = (5*Math.exp(-numOfSegmentsInHotspot + stayInHotspotThresholdCount)) - 5;
            return result;
        }
    }


    public static double[] softmax(double[] input) {
        double max = Double.NEGATIVE_INFINITY;

        // find max for numerical stability
        for (double val : input) {
            if (val > max) {
                max = val;
            }
        }

        // calculate exponentials and sum
        double sum = 0.0;
        double[] expValues = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            expValues[i] = Math.exp(input[i] - max); // stabilize
            sum += expValues[i];
        }

        // normalize
        for (int i = 0; i < input.length; i++) {
            expValues[i] /= sum;
        }

        return expValues;
    }


    // function to get Euclidean distance between segment end and candidate start

    public static double euclideanDistance(Segment currentSegment, Segment nextSegment) {
        Double[] vector = new Double[2];
        double result;

        vector[0] = currentSegment.endPoint[0] - nextSegment.startPoint[0];
        vector[1] = currentSegment.endPoint[1] - nextSegment.startPoint[1];

        return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
    }

    public static double[] toPrimitiveArray(ArrayList<Double> list) {
        double[] result = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }


    // method to apply all calculation functions

    // since list of segments will be loaded in order of id's, segment list indices match segment id's
    public static double[] calculateProbabilityDistribution(Segment currentSegment, Integer[] candidateIDs, List<Segment> segmentList, int numOfSegmentsSoFar, int numOfSegmentsCurrentHotspot, double timePassed, double timeForPost) {
        ArrayList<Double> probabilitySums = new ArrayList<>();
        for(Integer candidateID : candidateIDs){
            Double probabilitySum = 0.0;
            Segment canditateSegment = segmentList.get(candidateID);

            // functions 1 and 2
            if(canditateSegment.endingHotspotID == 1){
                if(numOfSegmentsSoFar == segmentCountThreshold){
                    probabilitySum += hotSpotOneFactor(numOfSegmentsSoFar);
                }
                if(timePassed >= timeForPost/2){
                    probabilitySum += timeFactor(timePassed, timeForPost);
                }
            }
            // function 3
            probabilitySum += distanceFactor(euclideanDistance(currentSegment, canditateSegment));

            // function 4
            probabilitySum += stayingInHotspotFactor(numOfSegmentsCurrentHotspot);

            probabilitySums.add(probabilitySum);
        }

        return softmax(toPrimitiveArray(probabilitySums));
    }


    // method to pool from 3 most probable segments
    // helper method to get then indices of the 3 most probable segments in the candidate array
    public static int[] topThreeIndices(double[] arr) {
        int first = -1, second = -1, third = -1;
        double max1 = Double.NEGATIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;
        double max3 = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < arr.length; i++) {
            double val = arr[i];
            if (val > max1) {
                max3 = max2;
                third = second;
                max2 = max1;
                second = first;
                max1 = val;
                first = i;
            } else if (val > max2) {
                max3 = max2;
                third = second;
                max2 = val;
                second = i;
            } else if (val > max3) {
                max3 = val;
                third = i;
            }
        }

        return new int[] { first, second, third };
    }

    // returns id of the segment which has
    public static int chosenSegment(double[] probabilities) {
        int[] topThree = topThreeIndices(probabilities);

        Random rand = new Random();
        int randInt = rand.nextInt(0,3);

        return topThree[randInt];
    }


    // master method to pick next segment
    // essentially just applies all the methods within this class
    // returns the id of the next segment to be played
    public static int pickNextSegment(Segment currentSegment, Integer[] candidateIDs, List<Segment> segmentList, int numOfSegmentsSoFar, int numOfSegmentsCurrentHotspot, double timePassed, double timeForPost){
        return chosenSegment((calculateProbabilityDistribution(currentSegment, candidateIDs, segmentList,  numOfSegmentsSoFar,numOfSegmentsCurrentHotspot,timePassed,timeForPost)));
    }

}
