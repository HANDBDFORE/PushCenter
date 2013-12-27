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
import java.util.HashSet;
import java.util.Set;

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
        bundle.getPushPacket().getEntries().size()  ;

        Output output = bundle.getOutput();

        Session session = sessionFactory.getCurrentSession();
        PushJob pushJob = new PushJob(bundle.getJobId(),bundle.createDate(),System.currentTimeMillis(),bundle.getPushPacket().getApp().getKey(),bundle.getPushPacket().getEntries().size(),output.getSuccesses().size(),output.getErrors().size(),bundle.getUnProcessedEntries().size());
//        session.save(pushJob);
        Set<JobResult> jobResults =new HashSet<JobResult>();

        for (PushEntry pushEntry : output.getSuccesses()) {
            jobResults.add(JobResult.success(pushJob, pushEntry));
        }

        Set<PushEntry> errorKeyset = output.getErrors().keySet();
        for (PushEntry pushEntry : errorKeyset) {
            jobResults.add(JobResult.failure(pushJob,pushEntry,output.getErrors().get(pushEntry).toString()));
        }

        for (PushEntry pushEntry : bundle.getUnProcessedEntries()) {
            jobResults.add(JobResult.unProcess(pushJob, pushEntry));
        }

        pushJob.setJobResultSet(jobResults);
        session.save(pushJob);

    }

//    @Transactional(isolation = Isolation.READ_COMMITTED)
//    public static void main(String[] args) {
//        Session session = sessionFactory.getCurrentSession();
//        PushJob pushJob = new PushJob("debug",123,"debugApp",3,2,1,0);
//        session.save(pushJob);
//    }
}
