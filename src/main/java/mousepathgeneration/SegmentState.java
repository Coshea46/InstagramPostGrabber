package mousepathgeneration;

public class SegmentState {
    Segment currentSegment;
    double timePassed = 0;
    int[] hotspotCounts = new int[13];
    int totalSegments = 0;
}
