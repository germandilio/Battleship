package battleship.model.game.ocean;

import battleship.model.ships.Ship;

import java.util.ArrayList;
import java.util.List;

public class Ocean {
    private final int sizeHorizontal;
    private final int sizeVertical;
    private final Ship[][] ocean;

    /**
     * @return horizontal size of the ocean
     */
    public int getSizeHorizontal() {
        return sizeHorizontal;
    }

    /**
     * @return vertical size of the ocean
     */
    public int getSizeVertical() {
        return sizeVertical;
    }

    public Ocean(int sizeHorizontal, int sizeVertical) throws IllegalArgumentException {
        if ((sizeHorizontal < 1 || sizeHorizontal > 30) || (sizeVertical < 1 || sizeVertical > 30)) {
            throw new IllegalArgumentException("Cannot create the Ocean with size: %dx%d".formatted(sizeHorizontal, sizeVertical));
        }

        this.sizeHorizontal = sizeHorizontal;
        this.sizeVertical = sizeVertical;
        ocean = new Ship[sizeVertical][sizeHorizontal];
    }

    private static final int MAX_ATTEMPTS = 30;

    /**
     * Random place list of ships
     *
     * @param ships list of ships to place
     * @param ocean ocean on which ships needs to be placed
     * @return Ocean
     */
    public static Ocean randomPlace(List<Ship> ships, Ocean ocean) {
        int attempts = 0;
        while (attempts++ < MAX_ATTEMPTS) {
            Ocean newOcean = getRandomOcean(ships, ocean);
            if (newOcean != null) {
                return newOcean;
            }
        }
        return null;
    }

    private static Ocean getRandomOcean(List<Ship> ships, Ocean ocean) {
        for (Ship ship : ships) {
            ShipPosition pos = ShipPosition.getRandomShipPosition(ocean, ship);
            if (pos == null)
                return null;
        }
        return ocean;
    }

    /**
     * @param pos position to check
     * @return true if it is empty, otherwise false.
     */
    public boolean isEmpty(Point pos) {
        Ship ship = getShipByPosition(pos);
        return ship == null;
    }

    /**
     * @param pos position
     * @return Ship on position, if position is out of ocean or empty return null
     */
    public Ship getShipByPosition(Point pos) {
        if (indexOutOfOcean(pos)) return null;
        return ocean[pos.y()][pos.x()];
    }

    /**
     * if it is impossible to place ship, method returns null;
     * if possible -> place ship on position
     *
     * @param shipToPlace  ship to place
     * @param shipPosition position
     * @return ShipPosition
     */
    public ShipPosition tryPlaceShip(Ship shipToPlace, ShipPosition shipPosition) {
        return switch (shipPosition.getDirection()) {
            case RIGHT -> placeShipRight(shipToPlace, shipPosition);
            case UP -> placeShipUp(shipToPlace, shipPosition);
            case LEFT -> placeShipLeft(shipToPlace, shipPosition);
            case DOWN -> placeShipDown(shipToPlace, shipPosition);
        };
    }

    private boolean indexOutOfOcean(Point pos) {
        return (pos.y() < 0 || pos.y() >= sizeVertical) || (pos.x() < 0 || pos.x() >= sizeHorizontal);
    }

    private boolean badIndexRange(Point startPos, Point endPos, ShipPosition.Direction direction) {
        if (indexOutOfOcean(startPos) || indexOutOfOcean(endPos)) return true;

        Point[] positionRange = Point.getRange(startPos, endPos, direction);
        for (Point point : positionRange) {
            switch (direction) {
                case UP -> {
                    // if this is the beginning of the ship, check below, down and right and down and left
                    if (point.equals(startPos) && (getShipByPosition(point.getDown()) != null ||
                            getShipByPosition(point.getDownAndLeft()) != null ||
                            getShipByPosition(point.getDownAndRight()) != null))
                        return true;

                    // if this is the end of the ship, check up, up and to the right and up and to the left
                    if (point.equals(endPos) && (getShipByPosition(point.getUp()) != null ||
                            getShipByPosition(point.getUpAndRight()) != null ||
                            getShipByPosition(point.getUpAndLeft()) != null))
                        return true;

                    // check left and right
                    if (getShipByPosition(point.getLeft()) != null || getShipByPosition(point.getRight()) != null)
                        return true;
                }
                case DOWN -> {
                    // if this is the beginning of the ship, check up, up and to the right and up and to the left
                    if (point.equals(startPos) && (getShipByPosition(point.getUp()) != null ||
                            getShipByPosition(point.getUpAndRight()) != null ||
                            getShipByPosition(point.getUpAndLeft()) != null))
                        return true;

                    // if this is the end of the ship, check below, down and right and down and left
                    if (point.equals(endPos) && (getShipByPosition(point.getDown()) != null ||
                            getShipByPosition(point.getDownAndLeft()) != null ||
                            getShipByPosition(point.getDownAndRight()) != null))
                        return true;

                    // check left and right
                    if (getShipByPosition(point.getLeft()) != null || getShipByPosition(point.getRight()) != null)
                        return true;
                }
                case LEFT -> {
                    // if this is the beginning of the ship, check right, right and down and right and up
                    if (point.equals(startPos) && (getShipByPosition(point.getRight()) != null ||
                            getShipByPosition(point.getUpAndRight()) != null ||
                            getShipByPosition(point.getDownAndRight()) != null))
                        return true;

                    // if this is the end of the ship, check left, left and down and right and up
                    if (point.equals(endPos) && (getShipByPosition(point.getLeft()) != null ||
                            getShipByPosition(point.getUpAndLeft()) != null ||
                            getShipByPosition(point.getDownAndLeft()) != null))
                        return true;
                    // check from above and below
                    if (getShipByPosition(point.getUp()) != null || getShipByPosition(point.getDown()) != null)
                        return true;
                }
                case RIGHT -> {
                    // if this is the beginning of the ship, check left, left and down and right and up
                    if (point.equals(startPos) && (getShipByPosition(point.getLeft()) != null ||
                            getShipByPosition(point.getUpAndLeft()) != null ||
                            getShipByPosition(point.getDownAndLeft()) != null))
                        return true;

                    // if this is the end of the ship, check right, right and down and right and up
                    if (point.equals(endPos) && (getShipByPosition(point.getRight()) != null ||
                            getShipByPosition(point.getUpAndRight()) != null ||
                            getShipByPosition(point.getDownAndRight()) != null))
                        return true;

                    // check from above and below
                    if (getShipByPosition(point.getUp()) != null || getShipByPosition(point.getDown()) != null)
                        return true;
                }
            }
        }
        return false;
    }

    private ShipPosition placeShipRight(Ship shipToPlace, ShipPosition startPos) {
        Point endPos = new Point(startPos.getX() + shipToPlace.getLength() - 1,
                startPos.getY());

        if (badIndexRange(startPos.getPosition(), endPos, startPos.getDirection()))
            return null;

        for (int i = startPos.getX(); i <= endPos.x(); i++) {
            ocean[startPos.getY()][i] = shipToPlace;
        }
        return startPos;
    }

    private ShipPosition placeShipLeft(Ship shipToPlace, ShipPosition startPos) {
        Point endPos = new Point(startPos.getX() - shipToPlace.getLength() + 1,
                startPos.getY());

        if (badIndexRange(startPos.getPosition(), endPos, startPos.getDirection()))
            return null;

        for (int i = startPos.getX(); i >= endPos.x(); i--) {
            ocean[startPos.getY()][i] = shipToPlace;
        }
        return startPos;
    }

    private ShipPosition placeShipUp(Ship shipToPlace, ShipPosition startPos) {
        Point endPos = new Point(startPos.getX(),
                startPos.getY() - shipToPlace.getLength() + 1);

        if (badIndexRange(startPos.getPosition(), endPos, startPos.getDirection()))
            return null;

        for (int i = endPos.y(); i <= startPos.getY(); i++) {
            ocean[i][startPos.getX()] = shipToPlace;
        }
        return startPos;
    }

    private ShipPosition placeShipDown(Ship shipToPlace, ShipPosition startPos) {
        Point endPos = new Point(startPos.getX(),
                startPos.getY() + shipToPlace.getLength() - 1);

        if (badIndexRange(startPos.getPosition(), endPos, startPos.getDirection()))
            return null;

        for (int i = startPos.getY(); i <= endPos.y(); i++) {
            ocean[i][startPos.getX()] = shipToPlace;
        }
        return startPos;
    }

    /**
     * get list of points which is occupied by ship
     *
     * @param attackingShip ship
     * @return list of points
     */
    public List<Point> getPointsOccupiedByShip(Ship attackingShip) {
        List<Point> shipPoints = new ArrayList<>();
        if (attackingShip != null) {
            for (int i = 0; i < ocean.length; i++) {
                for (int j = 0; j < ocean[0].length; j++) {
                    if (attackingShip.equals(ocean[i][j]))
                        shipPoints.add(new Point(j, i));
                }
            }
        }
        return shipPoints;
    }
}
