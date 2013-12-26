package com.hand.push.impl.pushers;

import com.hand.push.core.PushFailureException;
import com.hand.push.core.Pusher;
import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushEntry;
import org.slf4j.Logger;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基础并发推送模块，如果推送器需要并行推送数据，可继承自此类。
 * <p/>
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/24/13
 * Time: 9:01 AM
 */
public abstract class AbstractConcurrentPusher implements Pusher {

    /**
     * 用于管理线程
     */
    protected final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /**
     * 对象销毁时清理
     */
    protected abstract void cleanUp();

    protected abstract Logger getLogger();


    /**
     * 创建一个推送任务
     *
     * <p></p>
     * 注意：如果在<b>创建</b>线程过程中发生错误（非线程执行错误），请将错误捕获并包装为 {@link com.hand.push.core.PushFailureException }，
     * 并且，<b>无需将错误数据写入{@link com.hand.push.core.domain.Output}</b>，系统会自动捕获，否则会出现相同记录
     * @param entry
     * @param output
     * @return
     * @throws PushFailureException
     */
    protected abstract Runnable getTask(final PushEntry entry, final Output output) throws PushFailureException;


    public void push(List<PushEntry> pushRequests, final Output output) {
        getLogger().debug(getClass().getSimpleName() + " called");

        //使用闭锁来达到“所有推送线程都执行完毕，此方法才退出”的效果
        final CountDownLatch endGate = new CountDownLatch(pushRequests.size());

        //并发推送
        for (final PushEntry pushRequest : pushRequests) {

            final Runnable task;

            try {
                //各子类实现如何创建一个推送任务
                task = getTask(pushRequest, output);
            } catch (Throwable e) {
                //创建任务的过程中可能会产生异常，捕获后记录
                e.printStackTrace();
                getLogger().error("Create push thread error, " + e.getCause());

                //计数器递减，否则方法直到超时才会退出
                endGate.countDown();

                //写入错误信息
                output.addErrorEntry(pushRequest,e);

                //继续下一条
                continue;
            }


            //提交一个任务
            EXECUTOR.submit(new Thread() {
                @Override
                public void run() {
                    try {
                        //利用模板模式
                        task.run();
                    } finally {
                        //计数器递减
                        endGate.countDown();
                    }
                }
            });

        }

        try {
            //等待该批次所有推送线程结束
            endGate.await(1, TimeUnit.DAYS);
            getLogger().info(getClass().getSimpleName() + " processes ended");
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO 详细记录
            getLogger().error("Timeout: ");
        }
    }

    /**
     * 当系统即将关闭时，调用此方法释放资源
     * @throws Exception
     */
    @PreDestroy
    public void destroy() throws Exception {
        getLogger().debug("Receive shutdown message, " + getClass().getSimpleName() + " will end when running processor threads end. ");
        this.cleanUp();
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
        }

        getLogger().trace(getClass().getSimpleName() + " has shutdown.");
    }

}
