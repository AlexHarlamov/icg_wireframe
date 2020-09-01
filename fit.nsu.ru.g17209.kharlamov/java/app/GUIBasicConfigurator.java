package app;

import java.awt.*;

/**
 * Class BasicConfigurator
 *
 * Simple container of the pre-defined default GUI settings
 *
 */
public final class GUIBasicConfigurator {

    /**
     * Empty private constructor to make class abstract
     */

    private GUIBasicConfigurator(){}

    /**
     * Main frame size props
     */

    public static final int W_MIN_HEIGHT = 800;
    public static final int W_MIN_WIDTH = 800;

    /**
     * Naming pattern:
     * PARENT_COMPONENT_STATE_ELEMENT_PROPERTY,
     * where STATE - state of the ELEMENT
     *
     * available shortcuts:
     * P - panel
     * N - normal
     * S - selected
     * BTN - button
     */

    public static final Color P_TOOLS_N_BTN_COLOR = Color.gray;
    public static final Color P_TOOLS_S_BTN_COLOR = Color.darkGray;

    public static final Color GUI_N_IMAGE_P_COLOR = Color.gray;

    public static final int P_TOOLS_N_BTN_HEIGHT = 30;
    public static final int P_TOOLS_N_BTN_WIDTH = 30;

    public static final int GUI_N_P_TOOLS_HEIGHT = 35;

    public static final int GUI_N_P_HEIGHT = 320;
    public static final int GUI_N_P_WIDTH = 640;

    public static final int GUI_FULL_WORKING_AREA_DISPLAY_MODE = 1;
    public static final int GUI_ADAPTIVE_WORKING_AREA_DISPLAY_MODE = 2;

}
