package at.favre.lib.primitives.bytes;

import org.junit.Test;

public class BytesBenchmark {

    @Test
    public void immutableVsMutable() throws Exception {
        int length = 16 * 1024;
        Bytes randomXorOp = Bytes.randomNonSecure(length);
        Bytes immutable = Bytes.allocate(length);
        Bytes mutable = Bytes.allocate(length).mutable();

        for (int i = 0; i < 10; i++) {
            immutable = immutable.xor(randomXorOp);
            mutable = mutable.xor(randomXorOp);
        }

        for (int i = 0; i < 5; i++) {
            Thread.sleep(32);
            long durationImmutable = runBenchmark(randomXorOp, immutable);
            Thread.sleep(120);
            long durationMutable = runBenchmark(randomXorOp, mutable);
            System.out.println("\nRun " + i);
            System.out.println("Immutable: \t" + durationImmutable + " ns");
            System.out.println("Mutable: \t" + durationMutable + " ns");
        }
    }

    private long runBenchmark(Bytes randomXorOp, Bytes bytes) {
        long startImmutable = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            bytes = bytes.xor(randomXorOp);
        }
        return System.nanoTime() - startImmutable;
    }
}
