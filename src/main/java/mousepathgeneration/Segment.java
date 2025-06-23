package mousepathgeneration;

import java.util.ArrayList;

public class Segment {
    // attributes for json read
    public ArrayList<ArrayList<Double>> segmentPoints = new ArrayList<>();
    public Double segmentDuration;
    public Integer numOfPoints;
    public Double displacement;
    public double[] directionVector = new double[2];
    public Integer segmentID;
    public Double[] startPoint = new Double[2];
    public Double[] endPoint = new Double[2];
    public Integer[] nearestNeighboursIDs = new Integer[10];
    public Integer startingHotspotID;
    public Integer endingHotspotID;


    // constructor for json
    public Segment() {

    }

    // constructor for special segment
    public Segment(ArrayList<ArrayList<Double>> segmentPoints) {
        this.segmentPoints = segmentPoints;
    }

}
