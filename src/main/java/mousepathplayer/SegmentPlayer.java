package mousepathplayer;
import mousepathgeneration.*;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.InputEvent;

public class SegmentPlayer {
    public static void playSegment(SegmentWithTime segment) throws AWTException {
        Robot robot = new Robot();

        double timeForEachPoint = segment.time / segment.segment.segmentPoints.size();

        for(int i = 0; i < segment.segment.segmentPoints.size(); i++) {
            robot.mouseMove((int) segment.segment.segmentPoints.get(i).get(0).intValue(),(int) segment.segment.segmentPoints.get(i).get(1).intValue());
            robot.setAutoDelay((int) (segment.pauseAfter*1000));
        }
    }

    public static void playAllSegmentsInList(ArrayList<SegmentWithTime> segments) throws AWTException {
        for(SegmentWithTime segment : segments) {
            playSegment(segment);
            if(segment.likePostSpecialSegment) {
                Robot robot = new Robot();
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);  // left mouse click
            }
            else if(segment.nextPostSpecialSegment) {
                Robot robot = new Robot();
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            }
        }
    }

}
