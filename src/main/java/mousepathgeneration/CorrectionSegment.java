package mousepathgeneration;

import java.util.ArrayList;

// since segments use nearest neighbour, will be gaps between endpoint of current segment and start point of next segment
public class CorrectionSegment {

    final static double correctionSegmentTime = 0.03;
    final static double pauseAfterCorrection = 0.0;
    public static SegmentWithTime fixPosition(double[] previousEndPoint,double[] newStartPoint) {

        ArrayList<ArrayList<Double>> pointList = new ArrayList<ArrayList<Double>>();
        double[] controlPoint = NextPostSpecialSegment.getControlPoint(previousEndPoint, newStartPoint);

        int numOfPoints = (int) (Math.round(NextPostSpecialSegment.euclideanDistance(previousEndPoint, newStartPoint))*2);
        double stepSize = (double) 1 /numOfPoints;

        for(double t = 0; t<=1; t+=stepSize) {
            ArrayList<Double> newPoint = new ArrayList<>();
            for(int i=0; i<2; i++){
                newPoint.add(NextPostSpecialSegment.calculateXY(t,previousEndPoint, newStartPoint,controlPoint,i));
            }
            pointList.add(newPoint);
        }



        return new SegmentWithTime(new Segment(pointList), correctionSegmentTime, pauseAfterCorrection,false,false);
    }


}
