package com.fibo.ddp.job.jobbean;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 定时任务执行器接口
 * @createDate 2022-08-13 14:02:56
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
public interface FiboTaskInterface {
    /**
     * 执行定时任务接口
     *
     * @param params   参数，多参数使用JSON数据
     */
    void run(String params);
}
