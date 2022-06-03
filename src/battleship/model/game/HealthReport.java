package battleship.model.game;

/**
 * Health report (contains ship health remaining, and power of attack).
 */
public record HealthReport(int healthRemaining, int scoreByHit) {
}
