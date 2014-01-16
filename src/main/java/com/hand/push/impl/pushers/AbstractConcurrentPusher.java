package com.hand.push.impl.pushers;

import com.hand.push.core.PushFailureException;
import com.hand.push.core.Pusher;
import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.*;

/**
 * 基础并发推送模块，如果推送器需要并行推送数据，可继承自此类。
 * <p/>
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/24/13
 * Time: 9:01 AM
 */
public abstract class AbstractConcurrentPusher implements Pusher {

    protected static final int CORE_WORKERS = Runtime.getRuntime().availableProcessors()*4;
    protected static final int MAX_WORKERS = CORE_WORKERS * 2;
    protected static final int QUEUE_CAPACITY = 10000;
    /**
     * 用于管理线程
     */
    private final ExecutorService EXECUTOR ;



    public AbstractConcurrentPusher(int coreWorkers,int maxWorkers,int queueCapacity) {
        check(coreWorkers, maxWorkers, queueCapacity);
        EXECUTOR = new ThreadPoolExecutor(coreWorkers, maxWorkers,30,TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(queueCapacity));

    }

    private void check(int coreWorkers,int maxWorkers,int queueCapacity) {
        String className = getClass().getName();
        if (coreWorkers <= 0) throw new IllegalArgumentException(className+" 的 coreWorkers 属性必须>0");
        if (maxWorkers <=0 ) throw new IllegalArgumentException(className+" 的 maxWorkers 属性必须>0");
        if (maxWorkers <coreWorkers ) throw new IllegalArgumentException(className+" 的 maxWorkers 属性不能小于coreWorkers");
        if (queueCapacity <= 0) throw new IllegalArgumentException(className+" 的 queueCapacity 属性必须>0");
    }



    /**
     * 对象销毁时清理
     */
    protected abstract void cleanUp();




    /**
     * 创建一个推送任务
     *
     * <p></p>
     * 注意：如果在<b>创建</b>{@code Runnable}对象过程中发生错误（非线程执行错误），如果你愿意将其处理，
     * 可以将错误捕获并包装为 {@link com.hand.push.core.PushFailureException }抛出.
     * 并且，<b>此时无需将产生错误的数据写入{@code output}</b>，系统会自动捕获
     * @param entry
     * @param output
     * @return
     * @throws PushFailureException
     */
    protected abstract Runnable getTask(final PushEntry entry, final Output output) throws PushFailureException;


    public void push(List<PushEntry> pushRequests, final Output output) {
        Logger logger = getLogger();

        logger.debug(tellMeYourDeviceType() + " pusher called");

        //使用闭锁来达到“所有推送线程都执行完毕，此方法才退出”的效果
        final CountDownLatch endGate = new CountDownLatch(pushRequests.size());

        //并发推送
        for (final PushEntry pushRequest : pushRequests) {



            try {
                //各子类实现如何创建一个推送任务
                final Runnable task = getTask(pushRequest, output);

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
            catch (Throwable e) {
                //创建任务的过程中可能会产生异常，捕获后记录
                e.printStackTrace();
                logger.error("Create push thread error, " + e);

                //计数器递减，否则方法直到超时才会退出
                endGate.countDown();

                //写入错误信息
                output.addErrorEntry(pushRequest,e);

                //继续下一条
                continue;
            }


        }

        try {
            //等待该批次所有推送线程结束
            endGate.await(1, TimeUnit.DAYS);
            logger.info(tellMeYourDeviceType() + " pusher processes ended");
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO 详细记录
            logger.error("Timeout: ");
        }
    }

    /**
     * 当系统即将关闭时，调用此方法释放资源
     * @throws Exception
     */
    @PreDestroy
    public void destroy() throws Exception {
        Logger logger = getLogger();
        logger.debug("Receive shutdown message, " + tellMeYourDeviceType() + " will end when running processor threads end. ");
        this.cleanUp();
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
        }

        logger.trace(getClass().getSimpleName() + " has shutdown.");
    }

    private  Logger getLogger(){
        return LoggerFactory.getLogger(getClass());
    }

}
