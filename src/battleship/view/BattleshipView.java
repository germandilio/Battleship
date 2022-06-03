package battleship.view;

import battleship.model.game.AttackReport;
import battleship.model.game.ocean.Point;
import battleship.view.oceanview.OceanView;

import java.util.List;

public class BattleshipView {
    private final OceanView oceanView;

    /**
     * @return horizontal size of the ocean.
     */
    public int getHorizontalSize() {
        return oceanView.getOceanHorizontalLength();
    }

    /**
     * @return vertical size of the ocean.
     */
    public int getVerticalSize() {
        return oceanView.getOceanVerticalLength();
    }

    public BattleshipView(int sizeHorizontal, int sizeVertical) {
        oceanView = new OceanView(sizeHorizontal, sizeVertical);
    }

    /**
     * Print ocean view to console
     *
     * @param additionalInfo additional info to display in the end
     */
    public void printOcean(String additionalInfo) {
        clearConsole();
        System.out.println("=".repeat(oceanView.getOceanHorizontalLength() * 4 + 3));
        System.out.println(oceanView.printOcean());
        System.out.println(additionalInfo);
        System.out.println("-".repeat(oceanView.getOceanHorizontalLength() * 4 + 3));
    }

    /**
     * Update ocean view after the last attack
     *
     * @param report report of attack
     * @return String
     */
    public String updateOcean(AttackReport report) {
        return oceanView.updateOceanView(report);
    }

    /**
     * Print the main menu
     */
    public static void printMainMenu() {
        System.out.println("=========== Battleship ===========");
        System.out.println("| 1. New game                    |");
        System.out.println("| 2. Exit                        |");
        System.out.println("==================================");
    }

    /**
     * Print the Mode choosing menu
     *
     * @param mode mode to chose
     */
    public static void printModeMenu(String mode) {
        System.out.println("=========== Battleship ===========");
        System.out.println("| 1. " + mode + " enabled");
        System.out.println("| 2. " + mode + " disabled");
        System.out.println("==================================");
    }

    /**
     * Clearing console (don't work in IDE)
     */
    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name");

            //Check the current operating system
            if (operatingSystem.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception exception) {
            System.out.println("Cannot clean terminal");
        }
    }

    /**
     * Print help menu.
     */
    public void printHelp() {
        System.out.println("========================================= Help ========================================");
        System.out.println("| Enter your command in format: (optional: \"T\") <horizontal index> <vertical index> |");
        System.out.println("=======================================================================================");
    }

    /**
     * Recover ships cell views
     *
     * @param points list of points to recover
     */
    public void recoverShips(List<Point> points) {
        oceanView.recoverShipCellsByPoints(points);
    }
}
