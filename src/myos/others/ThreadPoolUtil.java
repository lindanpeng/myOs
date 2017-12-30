package myos.others;

import java.util.concurrent.*;

/**
 * Created by lindanpeng on 2017/10/20.
 */
public class ThreadPoolUtil {

        private final ExecutorService executor;
        private final ScheduledExecutorService scheduleExecutor;

        private static ThreadPoolUtil instance = new ThreadPoolUtil();

        private ThreadPoolUtil() {
            this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            this.scheduleExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        }

        public static ThreadPoolUtil getInstance() {
            return instance;
        }

        public static <T> Future<T> execute(final Callable<T> runnable) {
            return getInstance().executor.submit(runnable);
        }
        public static  void shutdown(){
            instance.executor.shutdown();
        }

        public static Future<?> execute(final Runnable runnable) {
            return getInstance().executor.submit(runnable);
        }

        public static ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final int initDelay, final int delay){
            return getInstance().scheduleExecutor.scheduleWithFixedDelay(runnable, initDelay, delay, TimeUnit.SECONDS);
        }

}
