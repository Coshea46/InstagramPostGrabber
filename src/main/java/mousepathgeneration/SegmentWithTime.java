package mousepathgeneration;

public class SegmentWithTime {
    public Segment segment;  // the segment being played
    public double time;  // time taken to complete segment (i.e. how fast the mouse will move)
    public double pauseAfter; // how long to pause after playing segment (can be 0s)
    public boolean nextPostSpecialSegment = false;
    public boolean likePostSpecialSegment = false;

    public Boolean click;

    public SegmentWithTime(Segment segment, double time, double pauseAfter) {
        this.segment = segment;
        this.time = time;
        this.pauseAfter = pauseAfter;
    }

    public SegmentWithTime(Segment segment, double time ,double pauseAfter, boolean nextPostSpecialSegment, boolean likePostSpecialSegment) {
        this.segment = segment;
        this.time = time;
        this.pauseAfter = pauseAfter;
        this.nextPostSpecialSegment = nextPostSpecialSegment;
        this.likePostSpecialSegment = likePostSpecialSegment;
    }


}
