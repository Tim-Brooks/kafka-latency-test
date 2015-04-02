package kafka_producer_benchmark;

import org.HdrHistogram.Histogram;

import java.util.concurrent.TimeUnit;

/**
 * Created by timbrooks on 4/2/15.
 */

public class App {

    public static void main(String[] args) {
        Histogram histogram = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);
        histogram.reset();

        for (int i = 0; i < 1000; ++i) {
            long start = System.nanoTime();
            int x = 3 * 3;
            histogram.recordValue(System.nanoTime() - start);
        }

        histogram.outputPercentileDistribution(System.out, 1000.0);
    }


}
