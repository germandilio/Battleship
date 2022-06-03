package battleship.controller;

import battleship.application.Main;
import battleship.model.game.AttackReport;
import battleship.model.game.FiringMode;
import battleship.model.game.Game;
import battleship.model.game.GameMode;
import battleship.model.game.ocean.Point;
import battleship.model.game.user.User;
import battleship.view.BattleshipView;

import java.util.*;

public class BattleshipController {
    private final User currentUser;
    private BattleshipView view;
    private final Scanner scanner;

    public BattleshipController() {
        currentUser = new User();
        scanner = new Scanner(System.in);
    }

    public BattleshipView getView() {
        return view;
    }

    /**
     * Start new game from user input parameters
     */
    public void startGame() {
        int horizontal = -1;
        while (horizontal == -1) {
            horizontal = requestForUserInput("Enter the horizontal size of the ocean (from 1 to 30):",
                    1, 30);
        }
        int vertical = -1;
        while (vertical == -1) {
            vertical = requestForUserInput("Enter the vertical size of the ocean (from 1 to 30):",
                    1, 30);
        }
        view = new BattleshipView(horizontal, vertical);

        int[] counters = null;
        while (counters == null) {
            counters = getShipsCounters();
        }

        boolean isSucceed = createNewGame(view.getHorizontalSize(), view.getVerticalSize(), counters);
        if (isSucceed) {
            int torpedoCounts = -1;
            while (torpedoCounts == -1) {
                torpedoCounts = requestForTorpedoMode(counters);
            }
            boolean shipRecoveryMode = requestForRecoveryMode();
            setGameMode(torpedoCounts, shipRecoveryMode);
        }
    }

    private void setGameMode(int torpedoCounts, boolean shipRecoveryMode) {
        try {
            EnumSet<GameMode> mode = Game.recognizeMode(torpedoCounts, shipRecoveryMode);
            currentUser.getCurrentSession().setGameMode(mode, torpedoCounts);

        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("FATAL ERROR:");
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Start new game with arguments from terminal.
     *
     * @param args arguments
     */
    public void startGame(int[] args) {
        int horizontal = args[0];
        int vertical = args[1];
        int[] counters = Arrays.copyOfRange(args, 2, 7);

        // set view ocean
        view = new BattleshipView(horizontal, vertical);

        boolean isSucceed = createNewGame(horizontal, vertical, counters);
        if (isSucceed) {
            int torpedoCounts = -1;
            while (torpedoCounts == -1) {
                torpedoCounts = requestForTorpedoMode(counters);
            }
            boolean shipRecoveryMode = requestForRecoveryMode();

            setGameMode(torpedoCounts, shipRecoveryMode);
        }
    }

    private int[] getShipsCounters() {
        int[] counters = new int[5];
        String[] typeOfShips = {"Carrier", "Battleship", "Cruiser", "Destroyer", "Submarine"};
        try {
            for (int i = 0; i < counters.length; i++) {
                System.out.print("Enter number of " + typeOfShips[i] + "s:");
                while (scanner.hasNext()) {
                    if (scanner.hasNextInt()) {
                        counters[i] = scanner.nextInt();
                        if (counters[i] >= 0)
                            break;
                    }
                    System.out.println("Enter integer number no less than 0: ");
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("Your value was so big");
            return null;
        } catch (Exception ex) {
            System.out.println("FATAL ERROR:");
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return null;
        }
        return counters;
    }

    private boolean requestForRecoveryMode() {
        BattleshipView.printModeMenu("Ship recovery mode");
        int result = -1;
        while (result == -1) {
            result = requestForUserInput("", 1, 2);
        }
        return result == 1;
    }

    private boolean createNewGame(int horizontal, int vertical, int[] counters) {
        try {
            Game currentGame = new Game(horizontal, vertical);
            currentGame.placeShipsOnOcean(counters);

            currentGame.addListener(currentUser);
            currentUser.setCurrentSession(currentGame);
            return true;

        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            BattleshipView.printMainMenu();
            if (requestForUserInput("", 1, 2) == 1) {
                startGame();
            } else
                System.exit(0);
            return false;
        } catch (Exception ex) {
            System.out.println("FATAL ERROR:");
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    /**
     * Request for user input
     *
     * @param helpString help string to print first
     * @param minBound   minimum correct integer number
     * @param maxBound   maximum correct integer number
     * @return option (int)
     */
    public int requestForUserInput(String helpString, int minBound, int maxBound) {
        int result = -1;
        boolean inputValid = false;
        while (!inputValid) {
            System.out.print(helpString);
            String input = scanner.next();
            try {
                result = Integer.parseInt(input);
                if (result >= minBound && result <= maxBound) {
                    inputValid = true;
                } else {
                    System.out.printf("Enter integer number from %d to %d%n", minBound, maxBound);
                }
            } catch (NumberFormatException e) {
                System.out.printf("Enter integer number from %d to %d%n", minBound, maxBound);
            }
        }
        return result;
    }

    private int requestForTorpedoMode(int[] counters) {
        int torpedoCounts = 0;
        BattleshipView.printModeMenu("Torpedo firing mode");
        int result = requestForUserInput("", 1, 2);
        if (result == 1) {
            torpedoCounts = requestForUserInput("Enter the number of available torpedo:",
                    Math.min(1, Arrays.stream(counters).sum()), Arrays.stream(counters).sum());
        }
        return torpedoCounts;
    }

    /**
     * Execute all user input commands.
     */
    public void executeCommands() {
        try {
            scanner.nextLine();
            while (true) {
                System.out.println("Enter command (coordinates to shot or \"exit\"/ \"help\"):");
                String inputCommand = scanner.nextLine();
                if (inputCommand.contains("exit")) return;
                if (inputCommand.contains("help")) {
                    view.printHelp();
                }

                String[] atoms = inputCommand.split("\\s+");
                if (correctInput(atoms)) {
                    if (executeCurrentCommand(atoms) <= 0) {
                        System.out.println("  YOU ARE WIN THE GAME!!!");
                        System.out.println("""
                                ＜￣｀ヽ、　　　　　　　／ ￣ ＞
                                　ゝ、　　＼　／⌒ヽ,ノ 　 /´
                                　　　ゝ、 （ ( ͡◉ ͜> ͡◉) ／
                                　　 　　>　 　 　,ノ
                                　　　　　∠_,,,/´""");
                        System.out.println("     Total shots: " + currentUser.getActionsCounter());
                        break;
                    }
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalStateException ex) {
            System.out.println("FATAL ERROR:");
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    private int executeCurrentCommand(String[] atoms) {
        try {
            int[] parameters;
            FiringMode mode;
            if (atoms.length == 3) {
                parameters = Main.parse(Arrays.copyOfRange(atoms, 1, 3));
                mode = FiringMode.TORPEDO_FIRING_MODE;
            } else {
                // length == 2
                parameters = Main.parse(Arrays.copyOfRange(atoms, 0, 2));
                mode = FiringMode.GENERAL_FIRING_MODE;
            }
            var point = new Point(parameters[0], parameters[1]);

            AttackReport report = currentUser.hitOnPlace(point, mode);
            String info = view.updateOcean(report);

            // Recovery ship mode
            var pointsToUpdate = currentUser.getCurrentSession().getPointsToRecover();
            if (pointsToUpdate != null) {
                view.recoverShips(pointsToUpdate);
                // clear list
                currentUser.getCurrentSession().clearPointsToRecover();
            }

            view.printOcean("\".\" - miss  \"*\" - hit  \"x\" - sunk\n" + info);
            return report.getFleetHealthRemaining();

        } catch (NumberFormatException ex) {
            System.out.println("Incorrect command format. Enter \"help\" to see details");
            return 1;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return 1;
        } catch (Exception ex) {
            System.out.println("FATAL ERROR:");
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return 0;
        }
    }

    private boolean correctInput(String[] atoms) {
        if (atoms.length < 2 || atoms.length > 3) return false;
        if (atoms.length == 3) {
            if (!Objects.equals(atoms[0], "T") && !Objects.equals(atoms[0], "t"))
                return false;
            try {
                Main.parse(Arrays.copyOfRange(atoms, 1, 2));
            } catch (NumberFormatException ex) {
                return false;
            }
        } else {
            try {
                Main.parse(Arrays.copyOfRange(atoms, 0, 1));
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return true;
    }
}
