package battleship.model.game;

import battleship.model.game.ocean.Point;
import battleship.model.ships.Ship;

import java.util.List;

public class AttackReport {
    /**
     * Hit result (Miss, nit or sunk).
     */
    public enum HitResult {
        MISS, HIT, SUNK
    }

    private final Ship ship;
    private final HitResult result;
    private final Point position;
    private final int fleetHealthRemaining;

    /**
     * @return List of points occupied by ship
     */
    public List<Point> getPointsOccupiedByShip() {
        return pointsOccupiedByShip;
    }

    private final List<Point> pointsOccupiedByShip;

    /**
     * Ship which was under attack
     *
     * @return null if missed, ship type if sunk
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * @return HitResult
     */
    public HitResult getResult() {
        return result;
    }

    /**
     * Get the position being attacked.
     *
     * @return Point (x, y)
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @return total health remaining.
     */
    public int getFleetHealthRemaining() {
        return fleetHealthRemaining;
    }

    public AttackReport(Ship ship, HitResult result, Point point, int fleetHealthRemaining, List<Point> points) {
        this.result = result;
        if (result == HitResult.MISS) {
            this.ship = null;
        } else {
            this.ship = ship;
        }
        position = point;
        this.fleetHealthRemaining = fleetHealthRemaining;
        pointsOccupiedByShip = points;
    }

    /**
     * @param health health remaining of the ship.
     * @return HitResult
     */
    public static HitResult getResult(int health) {
        if (health == 0) return HitResult.SUNK;
        else return HitResult.HIT;
    }

    /**
     * @return HitResult.MISS
     */
    public static HitResult getMissedResult() {
        return HitResult.MISS;
    }
}
