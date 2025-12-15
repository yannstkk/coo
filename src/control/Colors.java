package control;

public class Colors {

    private String RESET = "\u001B[0m";
    private String GREEN = "\u001B[32m";
    private String YELLOW = "\u001B[33m";
    private String BLUE = "\u001B[34m";
    private String RED = "\u001B[31m";
    private String PURPLE = "\u001B[35m";
    private String CYAN = "\u001B[36m";
    private String ORANGE = "\u001B[38;5;208m";

    /**
     * @return the reset color code
     */
    public String getReset() {
        return RESET;
    }

    /**
     * @return the green color code
     */
    public String getGreen() {
        return GREEN;
    }

    /**
     * @return the yellow color code
     */
    public String getYellow() {
        return YELLOW;
    }

    /**
     * @return the blue color code
     */
    public String getBlue() {
        return BLUE;
    }

    /**
     * @return the red color code
     */
    public String getRed() {
        return RED;
    }

    /**
     * @return the purple color code
     */
    public String getPurple() {
        return PURPLE;
    }

    /**
     * @return the cyan color code
     */
    public String getCyan() {
        return CYAN;
    }

    /**
     * @return the orange color code
     */
    public String getOrange() {
        return ORANGE;
    }
}