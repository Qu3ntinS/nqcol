package de.nq.nqcol.logic;

import java.util.Random;

public class HumanDelay {
    private static final Random random = new Random();

    /**
     * Calculates a gaussian delay with jitter, matching the AutoHotkey behavior.
     * Uses Box-Muller transform for gaussian distribution.
     * 
     * @param min Minimum delay in milliseconds
     * @param max Maximum delay in milliseconds
     * @param jitter Jitter range in milliseconds (±jitter)
     * @return Calculated delay in milliseconds
     */
    public static long calculateGaussianDelay(int min, int max, int jitter) {
        // Calculate mean and standard deviation
        double mean = (min + max) / 2.0;
        double stdDev = (max - min) / 6.0;

        // Box-Muller transform to generate gaussian random variable
        double u1 = 1.0 - random.nextDouble(); // Avoid 0
        double u2 = 1.0 - random.nextDouble(); // Avoid 0
        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);

        // Calculate delay
        long delay = Math.round(mean + z * stdDev);

        // Clamp to [min, max]
        delay = Math.max(min, Math.min(max, delay));

        // Add jitter (uniform random in [-jitter, +jitter])
        delay += random.nextInt(2 * jitter + 1) - jitter;

        return delay;
    }

    /**
     * Calculates a uniform random delay between min and max (inclusive).
     * 
     * @param min Minimum delay in milliseconds
     * @param max Maximum delay in milliseconds
     * @return Calculated delay in milliseconds
     */
    public static long calculateUniformDelay(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}

