package com.hand.push.core.dto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: hp
 * Date: 13-12-25
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
@Entity

public class PushJob {
    @Id
    @Column(name = "job_id")
    private  String jobId ;

    @Column(name = "create_timestamp")
    private  long createTimeStamp;

    @Column(name = "push_end_Timestamp")
    private long pushEndTimestamp;

    @Column(name = "app_name")
    private String appName;

    @OneToMany(mappedBy = "pushJob" ,cascade = CascadeType.ALL)
    private Set<JobResult> jobResultSet = new HashSet<JobResult>();
    /**
     *  推送项总大小
     */

    @Column(name = "push_count")
    private int pushCount;
    /**
     * 成功推送数量
     */

    @Column(name = "success_count")
    private int successCount;

    @Column(name = "failure_count")
    private int failureCount;

    @Column(name = "un_process_count")
    private int unProcessCount;

    public PushJob() {
    }

    public PushJob(String jobId, long createTimeStamp, long pushEndTimestamp, String appName, int size, int successCount, int failureCount, int unProcessCount) {
        this.jobId = jobId;
        this.createTimeStamp = createTimeStamp;
        this.pushEndTimestamp = pushEndTimestamp;
        this.appName = appName;
        this.pushCount = size;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.unProcessCount = unProcessCount;
    }

   public static PushJob job(String jobId){
       PushJob pushJob = new PushJob();
       pushJob.jobId = jobId;
       return pushJob;
   }

    public PushJob when(long timestamp){
        this.createTimeStamp = timestamp;
        return this;
    }
;
    public PushJob app(String appName){
        this.appName = appName;
        return this;
    }

    public PushJob total(int size){
        this.pushCount = size;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public String getAppName() {
        return appName;
    }


    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getUnProcessCount() {
        return unProcessCount;
    }

    public void setJobResultSet(Set<JobResult> jobResultSet) {
        this.jobResultSet = jobResultSet;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setCreateTimeStamp(long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Set<JobResult> getJobResultSet() {
        return jobResultSet;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void setPushCount(int pushCount) {
        this.pushCount = pushCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public void setUnProcessCount(int unProcessCount) {
        this.unProcessCount = unProcessCount;
    }
}
