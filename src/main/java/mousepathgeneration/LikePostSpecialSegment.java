package mousepathgeneration;

import java.util.ArrayList;
import java.util.Random;

public class LikePostSpecialSegment{

    public static SegmentWithTime likePostSpecialSegment(ArrayList<Double> currentPoint){
        double pauseAfter = segmentTime();  // meant to be this way dw

        ArrayList<ArrayList<Double>> resultingPointList = new ArrayList<>();
        double[] endPoint = randomPointInLikeButton();  // picks a target point within the nextPost button
        double[] startPoint = ProbabilityCalculator.toPrimitiveArray(currentPoint);
        double[] controlPoint = NextPostSpecialSegment.getControlPoint(startPoint, endPoint);

        int numOfPoints = (int) (Math.round(NextPostSpecialSegment.euclideanDistance(endPoint,startPoint))*2);
        double stepSize = (double) 1 /numOfPoints;

        for(double t = 0; t<=1; t+=stepSize) {
            ArrayList<Double> newPoint = new ArrayList<>();
            for(int i=0; i<2; i++){
                newPoint.add(NextPostSpecialSegment.calculateXY(t,startPoint,endPoint,controlPoint,i));
            }
            resultingPointList.add(newPoint);
        }

        return new SegmentWithTime(new Segment(resultingPointList), segmentTime(), pauseAfter,false,true);
    }

    // Replace with actual values on desktop
    private static double[] randomPointInLikeButton() {
        Random rand = new Random();

        double maxDistance = 1;
        double cx = 2;  // center of button x value
        double cy = 3 ;  // center of button y value

        double angle = rand.nextDouble() * 2 * Math.PI;           // random angle
        double radius = Math.sqrt(rand.nextDouble()) * maxDistance; // sqrt for uniform distribution in area

        double x = cx + radius * Math.cos(angle);
        double y = cy + radius * Math.sin(angle);

        return new double[] {x, y};
    }

    private static double segmentTime(){
        Random rand = new Random();

        return rand.nextDouble(0.03,0.04);
    }
}
