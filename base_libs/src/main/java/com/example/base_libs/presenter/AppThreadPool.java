package com.example.base_libs.presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Snail
 * @Since 2021/2/27
 */
public class AppThreadPool {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int BIGGER_CORE_POOL_SIZE = CPU_COUNT * 4;
    private static final int MAXIMUM_CORE_POOL_SIZE = CPU_COUNT * 8;
    private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;
    private static final long KEEP_ALIVE_SECONDS = 30L;
    private static final int MAX_QUEUE_SIZE = 10;

    private static ThreadPoolExecutor THREAD_POOL_EXECUTOR;
    private static ExecutorService sDefaultExecutor = new BufferExecutor();
    private static Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE), newThreadFactory("AppThread"));
    }

    private static ThreadFactory newThreadFactory(final String threadName) {
        return new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, threadName + " #" + mCount.getAndIncrement());
            }
        };
    }

    /**
     * 二级缓存线程池内部执行逻辑
     *
     * <pre>
     * 每个任务首先被加入二级缓存队列中
     * if (一级队列没有满 && 当前正在执行的线程不超过核心线程数) {
     *     取二级队列中的任务
     *     if (核心线程全部处于工作中) {
     *         if (一级队列没有满){
     *             将任务加入一级队列
     *         } else {
     *             新开临时线程执行任务
     *         }
     *     } else {
     *         将任务交给核心线程执行
     *     }
     * }
     * </pre>
     */
    private static class BufferExecutor implements ExecutorService, Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                @Override
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            //mActive 不为空，表示一级队列已经满了，此刻任务应该被停留到二级队列等待调度
            if (mActive == null) {
                scheduleNext();
            }
        }

        /**
         * 从二级队列调度任务，将任务放入一级队列，或者交由线程池执行
         */
        private synchronized void scheduleNext() {
            //线程池中正在执行的任务数
            int activeCount = THREAD_POOL_EXECUTOR.getActiveCount();
            //一级队列任务数
            int queueSize = THREAD_POOL_EXECUTOR.getQueue().size();

            //动态修改核心线程数，以适应不同场景的任务量
            if (mTasks.size() > MAX_QUEUE_SIZE * 100) {
                THREAD_POOL_EXECUTOR.setCorePoolSize(MAXIMUM_CORE_POOL_SIZE);
            } else if (mTasks.size() > MAX_QUEUE_SIZE * 10) {
                THREAD_POOL_EXECUTOR.setCorePoolSize(BIGGER_CORE_POOL_SIZE);
            } else {
                THREAD_POOL_EXECUTOR.setCorePoolSize(CORE_POOL_SIZE);
            }
            //如果一级队列没有满，且当前正在执行的线程不超过核心线程数
            if (queueSize < MAX_QUEUE_SIZE && activeCount <= THREAD_POOL_EXECUTOR.getCorePoolSize()) {
                //从二级队列中取任务
                if ((mActive = mTasks.poll()) != null) {
                    //将任务加入一级队列，或者有可能直接被线程池执行
                    THREAD_POOL_EXECUTOR.execute(mActive);
                    mActive = null;
                }
            }

        }


        @Override
        public void shutdown() {
            THREAD_POOL_EXECUTOR.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return THREAD_POOL_EXECUTOR.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return THREAD_POOL_EXECUTOR.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return THREAD_POOL_EXECUTOR.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return THREAD_POOL_EXECUTOR.awaitTermination(timeout, unit);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return THREAD_POOL_EXECUTOR.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return THREAD_POOL_EXECUTOR.submit(task, result);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return THREAD_POOL_EXECUTOR.submit(task);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return THREAD_POOL_EXECUTOR.invokeAll(tasks);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return THREAD_POOL_EXECUTOR.invokeAll(tasks, timeout, unit);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
            return THREAD_POOL_EXECUTOR.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            return THREAD_POOL_EXECUTOR.invokeAny(tasks, timeout, unit);
        }
    }

    /**
     * Executors the given command at some time in the future,The command may execute in a new trhed,
     * in a pooled thread,or in the calling thread,at the discretion of the {@code Executor} implementation
     *
     * @param command the runnable task
     */
    public static void execute(Runnable command) {
        if (command == null) {
            return;
        }
        try {
            sDefaultExecutor.execute(command);
        } catch (Exception e) {
            //RejectedExecutionException if this task cannot be accepted for execution
            e.printStackTrace();
        }
    }

}
