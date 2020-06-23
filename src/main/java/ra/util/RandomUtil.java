package ra.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static long nextRandomLong() {
        return ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }
    public static long nextRandomLong(long upperBound) {
        return ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, upperBound);
    }
    public static long nextRandomLong(long lowerBound, long upperbound) {
        return ThreadLocalRandom.current().nextLong(lowerBound, upperbound);
    }
    public static int nextRandomInteger() {
        return ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    public static int nextRandomInteger(int upperBound) {
        return ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, upperBound);
    }
    public static int nextRandomInteger(int lowerBound, int upperBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound);
    }
    public static String randomAlphanumeric(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
