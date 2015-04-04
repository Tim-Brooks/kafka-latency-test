package kafka_producer_benchmark;

import org.HdrHistogram.Histogram;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by timbrooks on 4/4/15.
 */
public class ProducerRunnable implements Runnable {

    private final Histogram histogram;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final KafkaProducer<byte[], byte[]> producer;
    private final CountDownLatch latch;
    private final String keyPrefix;
    public ProducerRunnable(KafkaProducer<byte[], byte[]> producer, CountDownLatch latch, int number) {
        this.producer = producer;
        this.latch = latch;
        this.keyPrefix = "key" + number;
        this.histogram = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);
    }

    @Override
    public void run() {
        histogram.reset();
        for (int i = 0; i < 10000; ++i) {
            ProducerRecord<byte[], byte[]> message = generateMessage(i);
            long start = System.nanoTime();
            producer.send(message);
            histogram.recordValue(System.nanoTime() - start);
            LockSupport.parkNanos(1);
        }
        latch.countDown();

    }

    public Histogram getHistogram() {
        return histogram;
    }

    private ProducerRecord<byte[], byte[]> generateMessage(int messageNumber) {
        byte[] bytes = new byte[100];
        random.nextBytes(bytes);
        return new ProducerRecord<>("test", (keyPrefix + messageNumber).getBytes(), bytes);
    }
}
