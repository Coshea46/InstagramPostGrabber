package mousepathgeneration;

import java.util.ArrayList;
import java.util.Random;

public class NextPostSpecialSegment {

    // next post segment
    // using Quadratic Bézier curve
    public static SegmentWithTime nextPostSegment(double timeLeft, ArrayList<Double> currentPoint) {
        double pauseAfter = 0;

        ArrayList<ArrayList<Double>> resultingPointList = new ArrayList<>();
        double[] endPoint = randomPointInButton();  // picks a target point within the nextPost button
        double[] startPoint = ProbabilityCalculator.toPrimitiveArray(currentPoint);
        double[] controlPoint = getControlPoint(startPoint, endPoint);

        int numOfPoints = (int) (Math.round(euclideanDistance(endPoint,startPoint))*2);
        double stepSize = (double) 1 /numOfPoints;

        for(double t = 0; t<=1; t+=stepSize) {
            ArrayList<Double> newPoint = new ArrayList<>();
            for(int i=0; i<2; i++){
                newPoint.add(calculateXY(t,startPoint,endPoint,controlPoint,i));
            }
            resultingPointList.add(newPoint);
        }

        return new SegmentWithTime(new Segment(resultingPointList),timeLeft, pauseAfter,true, false);
    }


    protected static double euclideanDistance(double[] point1, double[] point2) {
        double[] vector = {point1[0] - point2[0], point1[1] - point2[1]};
        return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
    }

    protected static Double calculateXY(double t, double[] startPoint, double[] endPoint, double[] controlPoint,int index) {
        Double result = 0.0;

        result = ((1-t)*(1-t))*startPoint[index] + 2*(1-t)*t*controlPoint[index] + t*t*endPoint[index];
        return Math.ceil(result);
    }


    protected static double[] getControlPoint(double[] startPoint, double[] endPoint) {
        double[] bigDirectionVector = {endPoint[0] - startPoint[0], endPoint[1] - startPoint[1]};
        double[] bigUnitVector = toUnitVector(bigDirectionVector);
        double[] perpendicularUnitVector = rotateVectorClockwisePerpendicular(bigUnitVector);

        int[] midPoint = {
                (int) Math.round((startPoint[0] + endPoint[0]) / 2.0),
                (int) Math.round((startPoint[1] + endPoint[1]) / 2.0)
        };

        double[] controlPoint = new double[2];

        Random random = new Random();
        double randNum = random.nextDouble(0,1);

        if(randNum <0.5){
            controlPoint[0] = Math.round(midPoint[0]+perpendicularUnitVector[0]);
            controlPoint[1] = Math.round(midPoint[1]+perpendicularUnitVector[1]);
        }
        else{
            controlPoint[0] = Math.round(midPoint[0]-perpendicularUnitVector[0]);
            controlPoint[1] = Math.round(midPoint[1]-perpendicularUnitVector[1]);
        }

        return controlPoint;

    }

    protected static double[] toUnitVector(double[] vector) {
        double[] unitVector = new double[2];
        double magnitude = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
        unitVector[0] = vector[0] / magnitude;
        unitVector[1] = vector[1] / magnitude;
        return unitVector;
    }

    protected static double[] rotateVectorClockwisePerpendicular(double[] vector) {
        double[] rotatedVector = new double[2];
        ArrayList<ArrayList<Double>> rotationMatrix = new ArrayList<>();
        ArrayList<Double> row1 = new ArrayList<>();
        ArrayList<Double> row2 = new ArrayList<>();

        row1.add(0.0);
        row1.add(1.0);
        row2.add(-1.0);
        row2.add(0.0);
        rotationMatrix.add(row1);
        rotationMatrix.add(row2);

        rotatedVector[0] = rotationMatrix.get(0).get(0)*vector[0] + rotationMatrix.get(0).get(1)*vector[1];
        rotatedVector[1] = rotationMatrix.get(1).get(0)*vector[0] + rotationMatrix.get(1).get(1)*vector[1];

        return rotatedVector;

    }

    protected static double[] randomPointInButton() {
        Random rand = new Random();

        double maxDistance = 25;
        double cx = 1853;  // center of button x value
        double cy = 547;  // center of button y value

        double angle = rand.nextDouble() * 2 * Math.PI;           // random angle
        double radius = Math.sqrt(rand.nextDouble()) * maxDistance; // sqrt for uniform distribution in area

        double x = cx + radius * Math.cos(angle);
        double y = cy + radius * Math.sin(angle);

        return new double[] {x, y};
    }
}
