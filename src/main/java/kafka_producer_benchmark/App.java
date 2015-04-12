package kafka_producer_benchmark;

import org.HdrHistogram.Histogram;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by timbrooks on 4/2/15.
 */

public class App {

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Histogram histogram = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);
        histogram.reset();
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", "localhost:9092");
        configs.put("key.serializer", ByteArraySerializer.class);
        configs.put("value.serializer", ByteArraySerializer.class);
        KafkaProducer<byte[], byte[]> producer = new KafkaProducer<>(configs);
        int threadCount = 5;

        for (int j = 0; j < 10; ++j) {


            CountDownLatch latch = new CountDownLatch(threadCount);
            CountDownLatch latch2 = new CountDownLatch(threadCount);

            List<ProducerRunnable> runnables = new ArrayList<>();

            for (int i = 0; i < threadCount; ++i) {
                ProducerRunnable runnable = new ProducerRunnable(producer, latch, i);
                runnables.add(runnable);
                new Thread(runnable).start();
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < threadCount; ++i) {
                ProducerRunnable runnable = new ProducerRunnable(producer, latch2, i);
                runnables.add(runnable);
                new Thread(runnable).start();
            }

            try {
                latch2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (ProducerRunnable r : runnables) {
                histogram.add(r.getHistogram());
            }
        }
        histogram.outputPercentileDistribution(System.out, 1000.0);
    }

}
