package battleship.model.game.user;

import battleship.model.game.AttackReport;
import battleship.model.game.FiringMode;
import battleship.model.game.Game;
import battleship.model.game.ocean.Point;
import battleship.model.ships.Ship;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class User implements StatusChangedListener {
    private int actionsCounter;
    private UserState actualState;
    private Game currentSession;

    /**
     * this list is used only when game mode contains recovery ships mode
     * list stores the last attacking reports, which must be canceled in the event of a miss after a hit.
     */
    private List<AttackReport> lastReports;

    public User() {
        this.actionsCounter = 0;
        actualState = UserState.GENERAL;
        lastReports = new ArrayList<>();
    }

    /**
     * set current game session.
     *
     * @param game game
     */
    public void setCurrentSession(Game game) {
        currentSession = game;
    }

    /**
     * Hit on place in current game session.
     *
     * @param position position to attack
     * @param mode     firing mode
     * @return HitReport
     * @throws IllegalArgumentException if firing mode = torpedo and there is no available torpedo.
     */
    public AttackReport hitOnPlace(Point position, FiringMode mode) throws IllegalArgumentException {
        ++actionsCounter;
        return currentSession.hitOnPlace(position, mode);
    }

    /**
     * @return number of actions.
     */
    public int getActionsCounter() {
        return actionsCounter;
    }

    /**
     * @return current game session.
     */
    public Game getCurrentSession() {
        return currentSession;
    }

    /**
     * (for recovery ship mode only) depends on actual state:
     * if actualState == ATTACKING_SHIP and last attacking ship is not equal to current: undo recent actions.
     * if result of current attack = MISS and user was attacking ship in previous attacks: undo recent actions.
     * when the ship was sunk: clear all history
     * when the ship was firstly attacked remember this attack.
     *
     * @param report report of the current attack.
     */
    @Override
    public void onStatusChanged(AttackReport report) {
        if (report.getResult() == AttackReport.HitResult.MISS && lastReports.size() > 0) {
            undoRecentActions();
            actualState = UserState.GENERAL;
            lastReports.clear();
            return;
        }
        if (actualState == UserState.ATTACKING_SHIP && !lastShipIsEqualToCurrent(report.getShip())) {
            undoRecentActions();
            actualState = UserState.GENERAL;
            lastReports.clear();
            return;
        }

        if (report.getResult() == AttackReport.HitResult.HIT) {
            lastReports.add(report);
            actualState = UserState.ATTACKING_SHIP;
            return;
        }

        if (report.getResult() == AttackReport.HitResult.SUNK) {
            lastReports.clear();
        }
    }

    private boolean lastShipIsEqualToCurrent(Ship ship) {
        if (ship == null) return false;
        if (lastReports.size() < 1) return false;
        return ship.equals(lastReports.get(lastReports.size() - 1).getShip());
    }


    private void undoRecentActions() {
        if (lastReports.size() > 0) {
            // remove duplicated attacks
            lastReports = new ArrayList<>(new LinkedHashSet<>(lastReports));

            // restore health fleet in amount of the lastReports list size
            currentSession.restorePreviousHealthFleet(lastReports.size());

            // restore ship health
            lastReports.get(0).getShip().restoreHealthInRecoveryMode();

            // update points to recover (controller will update view if points not null)
            List<Point> pointsToRecover = lastReports.stream().map(AttackReport::getPosition).collect(Collectors.toList());
            currentSession.updatePointsToRecover(pointsToRecover);
        }
    }
}
