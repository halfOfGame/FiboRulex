package com.fibo.ddp.job.controller;

import com.fibo.ddp.common.model.common.ResponseEntityBuilder;
import com.fibo.ddp.common.model.common.ResponseEntityDto;
import com.fibo.ddp.common.model.schedulejob.ScheduleJobEntity;
import com.fibo.ddp.common.service.schedulejob.ScheduleJobService;
import com.fibo.ddp.common.service.schedulejob.utils.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务相关接口
 * @createDate 2022-08-12 18:29:14
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
@RestController
@RequestMapping("/v1/schedule")
public class ScheduleJobController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动，加载定时任务
     */
    @PostConstruct
    public void init(){
        logger.info("*****************开始加载定时任务start*****************");
        List<ScheduleJobEntity> scheduleJobList = scheduleJobService.list();
        for(ScheduleJobEntity scheduleJob : scheduleJobList){
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
        logger.info("*****************开始加载定时任务end*****************");
    }

    /**
     * 保存定时任务
     */
    @RequestMapping("/save")
    public ResponseEntityDto save(@RequestBody ScheduleJobEntity scheduleJob){

        scheduleJobService.saveJob(scheduleJob);

        return ResponseEntityBuilder.buildNormalResponse();
    }

    /**
     * 修改定时任务
     */
    @RequestMapping("/update")
    public ResponseEntityDto update(@RequestBody ScheduleJobEntity scheduleJob){

        scheduleJobService.update(scheduleJob);

        return ResponseEntityBuilder.buildNormalResponse();
    }

    /**
     * 删除定时任务
     */
    @RequestMapping("/delete")
    public ResponseEntityDto delete(@RequestBody Long[] jobIds){
        scheduleJobService.deleteBatch(jobIds);

        return ResponseEntityBuilder.buildNormalResponse();
    }

    /**
     * 立即执行任务
     */
    @RequestMapping("/run")
    public ResponseEntityDto run(@RequestBody Long[] jobIds){
        scheduleJobService.run(jobIds);

        return ResponseEntityBuilder.buildNormalResponse();
    }

    /**
     * 暂停定时任务
     */
    @RequestMapping("/pause")
    public ResponseEntityDto pause(@RequestBody Long[] jobIds){
        scheduleJobService.pause(jobIds);

        return ResponseEntityBuilder.buildNormalResponse();
    }

    /**
     * 恢复定时任务
     */
    @RequestMapping("/resume")
    public ResponseEntityDto resume(@RequestBody Long[] jobIds){
        scheduleJobService.resume(jobIds);
        return ResponseEntityBuilder.buildNormalResponse();
    }
}
