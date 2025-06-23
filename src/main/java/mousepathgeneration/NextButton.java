package mousepathgeneration;

import java.util.ArrayList;
import java.util.Random;

public class NextButton {

    public static boolean overNextButton(ArrayList<ArrayList<Double>> pointList) {
        for (ArrayList<Double> point : pointList) {
            if (checkDistance(point.get(0), point.get(1))) {
                return true;
            }
        }
        return false;
    }


    private static boolean checkDistance(Double x, Double y){
        double result;

        result = Math.sqrt((x-1853)*(x-1853) + (y-547)*(y-547));

        if(result<=25){
            return true;
        }
        else{
            return false;
        }
    }


    public static boolean clickNextButton(ArrayList<ArrayList<Double>> pointList) {
        if(overNextButton(pointList)){
            Random random = new Random();
            double rand = random.nextDouble(0,1);

            if(rand<20){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return false;
        }
    }
}
