package com.hand.push.impl.processors;

import com.hand.push.core.Processor;
import com.hand.push.core.domain.Bundle;
import com.hand.push.core.domain.Output;
import com.hand.push.core.dto.PushJob;
import com.hand.push.core.dto.JobResult;
import com.hand.push.core.dto.PushEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hp
 * Date: 13-12-25
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class DBProcessor implements Processor {

    private SessionFactory sessionFactory;

    public DBProcessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void process(Bundle bundle) {
        // 1. get push source data
        PushJob pushJob = getPushSourceData(bundle);

        // 2. save to db
        Session session = sessionFactory.getCurrentSession();
        session.save(pushJob);
    }

    /*
    *    获得 PushJob 和  <set>JobResult 数据
    *    @Paramater Bundle bundle 数据来源
    * */
    private PushJob getPushSourceData(Bundle bundle){
        PushJob pushJob =bundleToPushJob(bundle);
        Output output = bundle.getOutput();
        Set<JobResult> jobResults =new HashSet<JobResult>();
        for (PushEntry pushEntry : output.getSuccesses()) {
            jobResults.add(JobResult.getJobResult(pushJob, pushEntry).success());
        }

        Set<PushEntry> errorKeyset = output.getErrors().keySet();
        for (PushEntry pushEntry : errorKeyset) {
            jobResults.add(JobResult.getJobResult(pushJob, pushEntry).failure(output.getErrors().get(pushEntry).toString()));
        }

        for (PushEntry pushEntry : bundle.getUnProcessedEntries()) {
            jobResults.add(JobResult.getJobResult(pushJob, pushEntry));
        }

        pushJob.setJobResultSet(jobResults);
        return pushJob;
    }

    /*
  *   Bundle 相关数据转换为 PushJob 的数据
  * */
    private static PushJob bundleToPushJob(Bundle bundle){
        PushJob pushJob = new PushJob();
        pushJob.setJobId(bundle.getJobId());
        pushJob.setAppName(bundle.getPushPacket().getApp().getKey());
        pushJob.setCreateTimeStamp(bundle.createDate());
        pushJob.setPushCount(bundle.getPushPacket().getEntries().size());
        pushJob.setFailureCount(bundle.getOutput().getErrors().size());
        pushJob.setSuccessCount(bundle.getOutput().getSuccesses().size());
        pushJob.setPushEndTimestamp(System.currentTimeMillis());
        return pushJob;
    }
}

