package com.fibo.ddp.common.service.schedulejob.utils;

import com.fibo.ddp.common.model.common.enums.ErrorCodeEnum;
import com.fibo.ddp.common.model.schedulejob.ScheduleJobEntity;
import com.fibo.ddp.common.utils.constant.Constants;
import com.fibo.ddp.common.utils.exception.ApiException;
import io.netty.util.Constant;
import org.quartz.*;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务操作工具类
 * @createDate 2022-08-13 13:18:33
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
public class ScheduleUtils {
    private final static String JOB_NAME = "TASK_";

    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(JOB_NAME + jobId);
    }

    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(JOB_NAME + jobId);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.GET_JOB_ERROR.getCode(),ErrorCodeEnum.GET_JOB_ERROR.getMessage());
        }
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
        try {
            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleJob.getJobId())).build();

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getJobId())).withSchedule(scheduleBuilder).build();

            //放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

            scheduler.scheduleJob(jobDetail, trigger);

            //暂停任务
            if(scheduleJob.getStatus() == Constants.jobScheduleStatus.PAUSE.getValue()){
                pauseJob(scheduler, scheduleJob.getJobId());
            }
        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.CREATE_JOB_ERROR.getCode(),ErrorCodeEnum.CREATE_JOB_ERROR.getMessage());
        }
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
        try {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getJobId());

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getJobId());

            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            //参数
            trigger.getJobDataMap().put(ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

            scheduler.rescheduleJob(triggerKey, trigger);

            //暂停任务
            if(scheduleJob.getStatus() == Constants.jobScheduleStatus.PAUSE.getValue()){
                pauseJob(scheduler, scheduleJob.getJobId());
            }

        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.MODIFY_JOB_ERROR.getCode(),ErrorCodeEnum.MODIFY_JOB_ERROR.getMessage());
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
        try {
            //参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

            scheduler.triggerJob(getJobKey(scheduleJob.getJobId()), dataMap);
        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.RUN_JOB_ERROR.getCode(),ErrorCodeEnum.RUN_JOB_ERROR.getMessage());
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.PAUSE_JOB_ERROR.getCode(),ErrorCodeEnum.PAUSE_JOB_ERROR.getMessage());
        }
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.RESUME_JOB_ERROR.getCode(),ErrorCodeEnum.RESUME_JOB_ERROR.getMessage());
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new ApiException(ErrorCodeEnum.DELETE_JOB_ERROR.getCode(),ErrorCodeEnum.DELETE_JOB_ERROR.getMessage());
        }
    }
}
