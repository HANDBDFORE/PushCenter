package com.hand.push.core.dto;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: hp
 * Date: 13-12-25
 * Time: 下午4:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class JobResult {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "jobId")
    private PushJob pushJob;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JobResultS")
    @SequenceGenerator(name = "JobResultS", sequenceName = "JobResult_S", allocationSize = 20)
    private int jobResultId;

    private Type status;
    //----- 以下是推送原始数据
    private String platform;
    private String token;
    private int count;
    private String message;
    private String errormsg;

    public JobResult() {
    }

    public static JobResult getJobResult(PushJob pushJob,PushEntry pushEntry) {
        JobResult result = new JobResult();
        result.pushJob = pushJob;
        result.platform = pushEntry.getPlatform();
        result.token = pushEntry.getToken();
        result.count = pushEntry.getCount();
        result.message = pushEntry.getMessage();
        return result;
    }

    public  JobResult success() {
        this.errormsg = null;
        this.status = Type.Success;
        return this;
    }

    public  JobResult failure(String errormsg) {
        this.errormsg = errormsg;
        this.status = Type.Failure;
        return this;
    }

    public  JobResult unProcess() {
        this.errormsg = null;
        this.status = Type.UnProcess;
        return this;
    }

    public Type getStatus() {
        return status;
    }

    public void setStatus(Type status) {
        this.status = status;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public PushJob getPushJob() {
        return pushJob;
    }

    public void setPushJob(PushJob pushJob) {
        this.pushJob = pushJob;
    }

    public int getJobResultId() {
        return jobResultId;
    }

    public void setJobResultId(int jobResultId) {
        this.jobResultId = jobResultId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static enum Type {
        Success("SUCCESS"), Failure("FAILURE"), UnProcess("UNPROCESS");
        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
