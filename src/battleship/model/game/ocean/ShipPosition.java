package battleship.model.game.ocean;

import battleship.model.ships.Ship;

import java.util.Random;

public class ShipPosition {
    /**
     * All available direction
     */
    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static final Direction[] VALUES = Direction.values();
    private static final int SIZE = VALUES.length;
    private static final Random RANDOM_DIRECTION = new Random();

    /**
     * @return random direction.
     */
    public static Direction randomDirection() {
        return VALUES[(RANDOM_DIRECTION.nextInt(SIZE))];
    }

    /**
     * Places the ship to random position with random direction.
     *
     * @param ocean       ocean on which to place the ship
     * @param shipToPlace ship to place
     * @return null if it's impossible, otherwise ship position.
     */
    public static ShipPosition getRandomShipPosition(Ocean ocean, Ship shipToPlace) {
        Random random = new Random();
        //
        int maxAttempts = ocean.getSizeHorizontal() * ocean.getSizeVertical() * 16;
        //
        int attempts = 0;
        while (attempts++ < maxAttempts) {

            Point point = new Point(random.nextInt(0, ocean.getSizeHorizontal() - 1),
                    random.nextInt(0, ocean.getSizeVertical() - 1));
            Direction dir = randomDirection();

            ShipPosition pos = ocean.tryPlaceShip(shipToPlace, new ShipPosition(point, dir));
            if (pos != null)
                return pos;
        }
        return null;
    }

    private final Point position;
    private final Direction direction;

    /**
     * @return x coordinate
     */
    public int getX() {
        return position.x();
    }

    /**
     * @return y coordinate
     */
    public int getY() {
        return position.y();
    }

    /**
     * @return Point (x, y)
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @return direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @param pos       point (x, y)
     * @param direction direction
     */
    public ShipPosition(Point pos, Direction direction) {
        position = pos;
        this.direction = direction;
    }
}
