package com.fibo.ddp.common.service.schedulejob.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fibo.ddp.common.dao.schedulejob.ScheduleJobLogMapper;
import com.fibo.ddp.common.model.schedulejob.ScheduleJobLogEntity;
import com.fibo.ddp.common.service.schedulejob.ScheduleJobLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务日志
 * @createDate 2022-08-13 13:35:06
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/

@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLogEntity> implements ScheduleJobLogService {

}
