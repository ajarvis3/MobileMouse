import java.awt.*;
import java.awt.event.InputEvent;

public class MouseProtocol {
    private Robot robot;

    /**
     * Default constructor
     * @param robot the Robot used by this process
     */
    public MouseProtocol(Robot robot) {
        this.robot = robot;
    }

    /**
     * Handles a button click
     * @param button the String with the number of the button that was clicked
     */
    public void handleButton(String button) {
        try {
            int buttonClick = InputEvent.getMaskForButton(Integer.parseInt(button));
            robot.mousePress(buttonClick);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles release of button
     * @param button the String representing the button to release
     */
    public void handleButtonRelease(String button) {
        try {
            int buttonClick = InputEvent.getMaskForButton(Integer.parseInt(button));
            robot.mouseRelease(buttonClick);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the movement of the cursor
     * @param x the String representing change in x coordinate
     * @param y the String representing change in y coordinate
     */
    public void handleMoveCursor(String x, String y) {
        Point coor = MouseInfo.getPointerInfo().getLocation();
        int newX = (int)(coor.getX() + Double.parseDouble(x));
        int newY = (int)(coor.getY() + Double.parseDouble(y));
        robot.mouseMove(newX, newY);
    }

    /**
     * Handles scroll motion
     * @param num the String representing number of scroll movements
     */
    public void handleScroll(String num) {
        try {
            int wheelAmt = Integer.parseInt(num);
            robot.mouseWheel(wheelAmt);
        }
        catch (NumberFormatException e) {}
    }

    /**
     * Takes input and performs the appropriate action.
     * @param input
     */
    public void handleInput(String input) {
        String[] tokens = input.split("[^A-Z0-9-]");
        switch (tokens[0]) {
            case("BUTTON"):
                handleButton(tokens[1]);
                break;
            case("RELEASE"):
                handleButtonRelease(tokens[1]);
                break;
            case("SCROLL"):
                handleScroll(tokens[1]);
                break;
            case("SWIPE"):
                handleMoveCursor(tokens[1], tokens[2]);
                break;
        }
    }
}
