
package battleship.application;

import battleship.controller.BattleshipController;
import battleship.view.BattleshipView;

public class Main {
    private static final String ARGS_TERMINAL_ERROR = "Args are incorrect!\nRequired integers values: <horizontalSize> <verticalSize>" +
            " <Carriers counter> <Battleship counter> <Cruisers counter> <Destroyer counter> <Submarine counter>.";

    public static void main(String[] args) {
        if (args.length != 0 && args.length != 7) {
            System.out.println(ARGS_TERMINAL_ERROR);
            System.exit(0);
        }
        try {
            // create controller
            BattleshipController controller = new BattleshipController();

            if (args.length == 0) {
                // user input parameters
                BattleshipView.printMainMenu();
                if (controller.requestForUserInput("", 1, 2) == 1) {
                    controller.startGame();

                } else System.exit(0);
            } else {
                // terminal input parameters
                int[] counters = parse(args);
                controller.startGame(counters);
            }

            // print the ocean
            controller.getView().printOcean("\".\" - miss  \"*\" - hit  \"x\" - sunk");
            // execute input player input commands
            controller.executeCommands();

        } catch (NumberFormatException nfe) {
            System.out.println(ARGS_TERMINAL_ERROR);
        } catch (Exception exception) {
            System.out.println("Error has been occurred!");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Parse input arguments from string to int
     *
     * @param args args
     * @return int[]
     * @throws NumberFormatException if string cannot convert to int
     */
    public static int[] parse(String[] args) throws NumberFormatException {
        int[] counters = new int[args.length];
        for (int i = 0; i < counters.length; i++) {
            counters[i] = Integer.parseInt(args[i]);
        }
        return counters;
    }
}