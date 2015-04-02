package kafka_producer_benchmark;

import org.HdrHistogram.Histogram;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by timbrooks on 4/2/15.
 */

public class App {

    public static void main(String[] args) {
        Histogram histogram = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);
        histogram.reset();

        KafkaProducer<String, String> producer = new KafkaProducer<>(new HashMap<>());
        ProducerRecord<String, String> record = new ProducerRecord<>("hello", "key", "value");

        for (int i = 0; i < 1000; ++i) {
            long start = System.nanoTime();
            producer.send(record);
            histogram.recordValue(System.nanoTime() - start);
        }

        histogram.outputPercentileDistribution(System.out, 1000.0);
    }


}
