package com.fibo.ddp.common.service.schedulejob.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fibo.ddp.common.dao.schedulejob.ScheduleJobMapper;
import com.fibo.ddp.common.model.schedulejob.ScheduleJobEntity;
import com.fibo.ddp.common.service.schedulejob.ScheduleJobService;
import com.fibo.ddp.common.service.schedulejob.utils.ScheduleUtils;
import com.fibo.ddp.common.utils.constant.Constants;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务
 * @createDate 2022-08-13 12:55:34
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
@Service("scheduleJobService")
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJobEntity> implements ScheduleJobService {
    @Autowired
    private Scheduler scheduler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJob(ScheduleJobEntity scheduleJob) {
        scheduleJob.setCreateTime(new Date());
        scheduleJob.setStatus(Constants.jobScheduleStatus.NORMAL.getValue());
        this.save(scheduleJob);

        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ScheduleJobEntity scheduleJob) {
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        this.updateById(scheduleJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] jobIds) {
        for(Long jobId : jobIds){
            ScheduleUtils.deleteScheduleJob(scheduler, jobId);
        }
        //删除数据
        this.removeByIds(Arrays.asList(jobIds));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(Long[] jobIds) {
        for(Long jobId : jobIds){
            ScheduleUtils.run(scheduler, this.getById(jobId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pause(Long[] jobIds) {
        for(Long jobId : jobIds){
            ScheduleUtils.pauseJob(scheduler, jobId);
        }
        updateBatch(jobIds, Constants.jobScheduleStatus.PAUSE.getValue());
    }

    @Override
    public int updateBatch(Long[] jobIds, int status){
        Map<String, Object> map = new HashMap<>(2);
        map.put("list", Arrays.asList(jobIds));
        map.put("status", status);
        return baseMapper.updateBatch(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resume(Long[] jobIds) {
        for(Long jobId : jobIds){
            ScheduleUtils.resumeJob(scheduler, jobId);
        }

        updateBatch(jobIds, Constants.jobScheduleStatus.NORMAL.getValue());
    }
}
