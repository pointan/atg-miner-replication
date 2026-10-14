package at.fhooe.se.atgminer.util;

/**
 * <p>A very simple Random Generator implementation</p>
 */
public class Random {
    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)
    private static final long MASK = (1L << 48) - 1;
    private long currentValue;

    public Random(long seed) {
        currentValue = seed;
    }

    private int next(int bits) {
        currentValue = (currentValue * MULTIPLIER + 1) & MASK;
        return (int)(currentValue >>> (48 - bits));
    }

    public int nextInt(int range) {
        return next(31) % range;
    }

    public double nextDouble() {
        return (((long)next(26) << 27) + next(27)) * DOUBLE_UNIT;
    }

}
