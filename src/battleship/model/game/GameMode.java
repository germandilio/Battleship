package battleship.model.game;

import java.util.EnumSet;

/**
 * Game mode (NO_OPTIONS, TORPEDO_MODE_ENABLE, SHIP_RECOVERY_MODE_ENABLE, All_OPTIONS)
 * .
 */
public enum GameMode {
    NO_OPTIONS,
    TORPEDO_MODE_ENABLE,
    SHIP_RECOVERY_MODE_ENABLE;
    /**
     * Enum set of all flags.
     */
    public static final EnumSet<GameMode> All_OPTIONS = EnumSet.allOf(GameMode.class);
}
