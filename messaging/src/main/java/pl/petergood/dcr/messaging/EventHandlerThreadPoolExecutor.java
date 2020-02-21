package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventHandlerThreadPoolExecutor<T> extends ThreadPoolExecutor {

    private Consumer<String, T> consumer;
    private Logger LOG = LoggerFactory.getLogger(EventHandlerThreadPoolExecutor.class);

    public EventHandlerThreadPoolExecutor(Consumer<String, T> consumer,
                                          int corePoolSize,
                                          int maximumPoolSize,
                                          long keepAliveTime,
                                          TimeUnit unit,
                                          BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.consumer = consumer;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
//
//        consumer.commitSync();
//        consumer.resume(consumer.assignment());
//        LOG.info("Resumed consumer");
    }
}
