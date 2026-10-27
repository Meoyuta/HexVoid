package mys.hexvoid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class HexvoidUtils {

    /**
     *
     * @param player       a player that need award advancement
     * @param level        the ServerLevel that player stayed
     * @param advancement  target advancement
     * @param advCriterion the criterion of target advancement
     * @return status code:
     * <table>
     *     <tr><td><code>-1</code></td><td><code>not found</code></td></tr>
     *     <tr><td><code>0</code></td><td><code>no error</code></td></tr>
     *     <tr><td><code>1</code></td><td><code>has awarded</code></td></tr>
     * </table>
     */
    public static int awardAdvancement(@NotNull ServerPlayer player, @NotNull ServerLevel level, ResourceLocation advancement, String advCriterion) {
        var adv = level.getServer().getAdvancements().get(advancement);
        if (adv != null) return player.getAdvancements().award(adv, advCriterion) ? 0 : 1;
        else Hexvoid.LOGGER.error("couldn't get advancement", new NullPointerException());
        return -1;
    }

    /**
     * Computes the value of the function k(x) which satisfies:
     * - As x approaches 0+, k(x) approaches 0 but never equals 0.
     * - As x approaches a constant P, k(x) approaches K but never equals K.
     * - As x approaches infinity, k(x) approaches P.
     * The function is designed using exponential terms to ensure asymptotic behavior.
     * Note: P should not be zero to avoid division by zero.
     *
     * @param x The input value, should be positive.
     * @param P The constant value that k(x) approaches as x approaches infinity, and the point where k(x) approaches K.
     * @param K The constant value that k(x) approaches as x approaches P.
     * @return The value of k(x) at x.
     * @throws IllegalArgumentException if P is zero.
     */
    public static double k(double x, double P, double K) {
        if (P == 0) {
            throw new IllegalArgumentException("P should not be zero.");
        }

        double alpha = 1.0;  // Controls the rate of approach for the limits at 0 and infinity
        double beta = 1.0;   // Controls the rate of approach for the limit at P
        double tolerance = 1e-10;  // Tolerance for x close to P
        double epsilon = 1e-10;    // Small offset to avoid equality at x = P

        // Compute A(x) = P * (1 - exp(-alpha * x)), which handles limits at 0 and infinity
        double A_x = P * (1 - Math.exp(-alpha * x));

        // Compute A(P), the value of A(x) at x = P
        double A_P = P * (1 - Math.exp(-alpha * P));

        // If x is very close to P, return A(P) + epsilon to avoid k(x) = K
        if (Math.abs(x - P) < tolerance) {
            return A_P + epsilon;
        }

        // Compute g(x) = (x / P) * exp(-beta * (x - P)^2), which peaks near x = P
        double g_x = (x / P) * Math.exp(-beta * (x - P) * (x - P));

        // Compute B(x) = (K - A(P)) * g(x), which handles the limit at P
        double B_x = (K - A_P) * g_x;

        // k(x) = A(x) + B(x)
        return A_x + B_x;
    }
}
