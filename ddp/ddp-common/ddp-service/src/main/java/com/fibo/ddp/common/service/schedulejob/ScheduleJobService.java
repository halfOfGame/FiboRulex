package com.fibo.ddp.common.service.schedulejob;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fibo.ddp.common.model.schedulejob.ScheduleJobEntity;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务
 * @createDate 2022-08-13 12:54:03
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
public interface ScheduleJobService extends IService<ScheduleJobEntity> {
    /**
     * 保存定时任务
     */
    void saveJob(ScheduleJobEntity scheduleJob);

    /**
     * 更新定时任务
     */
    void update(ScheduleJobEntity scheduleJob);
    /**
     * 立即执行
     */
    void run(Long[] jobIds);

    /**
     * 暂停运行
     */
    void pause(Long[] jobIds);

    /**
     * 恢复运行
     */
    void resume(Long[] jobIds);

    /**
     * 批量更新定时任务状态
     */
    int updateBatch(Long[] jobIds, int status);

    /**
     * 批量删除定时任务
     */
    void deleteBatch(Long[] jobIds);
}
