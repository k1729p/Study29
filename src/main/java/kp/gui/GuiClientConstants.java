package kp.gui;

import java.awt.*;
import java.util.List;

/**
 * GUI client constants.
 */
public class GuiClientConstants {
    public static final String TITLE = "GUI Client";
    public static final String APP_ICON = "/images/icon.gif";
    public static final String GET_CUSTOMERS_TITLE = "Get Customers";
    public static final String GET_ORDERS_TITLE = "Get Orders";
    public static final java.util.List<String> MENU_LIST =
            List.of(GET_CUSTOMERS_TITLE, GET_ORDERS_TITLE, "Exit");
    public static final Dimension DIMENSION = new Dimension(600, 900);
    /*
    Java's Color.GREEN is #00FF00 (Lime)
    Color.YELLOW predefined in Swing
    Color.LIGHT_GRAY Predefined in Swing
     */
    public static final Color ALICE_BLUE = Color.decode("#F0F8FF");
    public static final Color BISQUE = Color.decode("#FFE4C4");
    public static final Color BROWN = Color.decode("#A52A2A"); // 'redBrown'
    public static final Color CHOCOLATE = Color.decode("#D2691E");
    public static final Color CORNSILK = Color.decode("#FFF8DC");
    public static final Color GOLD = Color.decode("#FFD700");
    public static final Color HONEYDEW = Color.decode("#F0FFF0");
    public static final Color LAVENDER = Color.decode("#E6E6FA");
    public static final Color LIGHT_CYAN = Color.decode("#E0FFFF");
    public static final Color LIGHT_YELLOW = Color.decode("#FFFFE0");
    public static final Color MISTY_ROSE = Color.decode("#FFE4E1");
    public static final Color OLIVE = Color.decode("#808000");
    public static final Color ORANGE_DARK = Color.decode("#FF5C00");
    public static final Color SADDLE_BROWN = Color.decode("#8B4513");
    public static final Color SALMON = Color.decode("#FA8072");
    public static final Color SIENNA = Color.decode("#A0522D");
    public static final Color VIOLET = Color.decode("#EE82EE");

    /**
     * Private constructor to prevent instantiation.
     */
    private GuiClientConstants() {
        throw new IllegalStateException("Utility class");
    }
}
