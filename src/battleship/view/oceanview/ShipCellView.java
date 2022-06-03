package battleship.view.oceanview;

import battleship.model.game.AttackReport;

public class ShipCellView {
    private boolean wasHit;
    private boolean isSunk;

    public ShipCellView() {
        wasHit = false;
        isSunk = false;
    }

    /**
     * update cell status if it was attacked by user
     *
     * @param result attack report
     */
    public void shipWasAttackByUser(AttackReport result) {
        if (result.getResult() != AttackReport.HitResult.MISS)
            wasHit = true;
        if (result.getResult() == AttackReport.HitResult.SUNK) {
            isSunk = true;
        }
    }

    /**
     * convert ship cell view to string, depends on it status
     *
     * @return String
     */
    @Override
    public String toString() {
        // ship on this cell was sunk
        if (isSunk) return " x ";
        // ship on this cell was hit
        if (wasHit) return " * ";
            // there is no ship on this cell
        else return " . ";
    }
}
