package battleship.view.oceanview;

import battleship.model.game.AttackReport;
import battleship.model.game.ocean.Point;
import battleship.view.matrixprinter.PrettyMatrixPrinter;

import java.util.List;

public class OceanView {
    private final ShipCellView[][] oceanView;

    /**
     * @return OceanView
     */
    public ShipCellView[][] getOceanView() {
        return oceanView;
    }

    public OceanView(int sizeHorizontal, int sizeVertical) {
        oceanView = new ShipCellView[sizeVertical][sizeHorizontal];
    }

    /**
     * update ocean view after last attack
     *
     * @param report report of the last attack
     * @return String with additional info
     */
    public String updateOceanView(AttackReport report) {
        if (report.getPosition().x() < 0 || report.getPosition().x() >= getOceanHorizontalLength() ||
                report.getPosition().y() < 0 || report.getPosition().y() >= getOceanVerticalLength()) {
            return "Attacking position was out of the ocean.";
        }

        if (oceanView[report.getPosition().y()][report.getPosition().x()] == null) {
            oceanView[report.getPosition().y()][report.getPosition().x()] = new ShipCellView();
        }
        // if ship was sunk
        if (report.getPointsOccupiedByShip() != null) {
            for (Point point : report.getPointsOccupiedByShip()) {
                if (oceanView[point.y()][point.x()] == null)
                    oceanView[point.y()][point.x()] = new ShipCellView();
                oceanView[point.y()][point.x()].shipWasAttackByUser(report);
            }
            return "You just have sunk a " + report.getShip().toString();
        } else {
            // ship was hit
            oceanView[report.getPosition().y()][report.getPosition().x()].shipWasAttackByUser(report);
        }
        return "";
    }

    /**
     * print Ocean
     *
     * @return String
     */
    public String printOcean() {
        return PrettyMatrixPrinter.print(oceanView);
    }

    /**
     * get horizontal size of Ocean view
     *
     * @return int size > 0
     */
    public int getOceanHorizontalLength() {
        return oceanView[0].length;
    }

    /**
     * get vertical size of Ocean view
     *
     * @return int size > 0
     */
    public int getOceanVerticalLength() {
        return oceanView.length;
    }

    /**
     * restore ships cell views after cancelling the last attacks
     *
     * @param points points to restore
     */
    public void recoverShipCellsByPoints(List<Point> points) {
        for (Point point : points) {
            oceanView[point.y()][point.x()] = null;
        }
    }
}
