package mousepathgeneration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

public class SegmentLoader {
    // read segments json
    public static List<Segment> loadSegments(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return List.of(mapper.readValue(new File(filePath), Segment[].class));
    }


}
