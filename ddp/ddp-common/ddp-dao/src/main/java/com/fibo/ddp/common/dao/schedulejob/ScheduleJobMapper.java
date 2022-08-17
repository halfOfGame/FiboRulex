package com.fibo.ddp.common.dao.schedulejob;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fibo.ddp.common.model.schedulejob.ScheduleJobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务
 * @createDate 2022-08-13 12:45:33
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJobEntity> {
    /**
     * 批量更新状态
     */
    int updateBatch(Map<String, Object> map);
}
