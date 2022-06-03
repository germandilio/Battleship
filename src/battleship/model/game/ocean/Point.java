package battleship.model.game.ocean;

import java.util.Objects;

public record Point(int x, int y) {

    /**
     * Get range of points between start and end points.
     *
     * @param startPos  start point
     * @param endPos    end point
     * @param direction ship direction
     * @return array of points.
     */
    public static Point[] getRange(Point startPos, Point endPos, ShipPosition.Direction direction) {
        var range = new Point[getLength(startPos, endPos)];
        int currentX = startPos.x;
        int currentY = startPos.y;
        range[0] = new Point(currentX, currentY);

        for (int i = 1; i < range.length; i++) {
            switch (direction) {
                case UP -> --currentY;
                case DOWN -> ++currentY;
                case LEFT -> --currentX;
                case RIGHT -> ++currentX;
            }
            range[i] = new Point(currentX, currentY);
        }
        return range;
    }

    private static int getLength(Point startPos, Point endPos) {
        return (int) Math.sqrt(Math.pow(startPos.x() - endPos.x(), 2) + Math.pow(startPos.y() - endPos.y(), 2)) + 1;
    }

    /**
     * @return point which is on left
     */
    public Point getLeft() {
        return new Point(x - 1, y);
    }

    /**
     * @return point which is on right
     */
    public Point getRight() {
        return new Point(x + 1, y);
    }

    /**
     * @return upper point
     */
    public Point getUp() {
        return new Point(x, y - 1);
    }

    /**
     * @return down point
     */
    public Point getDown() {
        return new Point(x, y + 1);
    }

    /**
     * @return up and right point
     */
    public Point getUpAndRight() {
        return new Point(x + 1, y - 1);
    }

    /**
     * @return down and right point
     */
    public Point getDownAndRight() {
        return new Point(x + 1, y + 1);
    }

    /**
     * @return up and left point
     */
    public Point getUpAndLeft() {
        return new Point(x - 1, y - 1);
    }

    /**
     * @return down and left point
     */
    public Point getDownAndLeft() {
        return new Point(x - 1, y + 1);
    }

    /**
     * Overridden equals method
     *
     * @param obj object to compare
     * @return true if it's equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    /**
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
