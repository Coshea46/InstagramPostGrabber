package screenshotandsend;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {
    public static void screenshot(int screenshotNumber) throws IOException, AWTException {
        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);

        // Capture the screen
        Robot robot = new Robot();
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);

        // Save to file
        ImageIO.write(screenFullImage, "png", new File("output\\screenshot"+screenshotNumber+".png"));

    }
}
