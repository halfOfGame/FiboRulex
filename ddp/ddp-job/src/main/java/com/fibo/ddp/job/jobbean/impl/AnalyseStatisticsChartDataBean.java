package com.fibo.ddp.job.jobbean.impl;

import com.fibo.ddp.common.service.analyse.StatisticsService;
import com.fibo.ddp.job.jobbean.FiboTaskInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 分析中心图表数据跑批
 * @createDate 2022-08-13 14:08:04
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
@Component("analyseStatisticsChartDataBean")
public class AnalyseStatisticsChartDataBean implements FiboTaskInterface {
    @Resource
    @Qualifier("analyseChartStatisticServiceImpl")
    private StatisticsService statisticsService;


    @Override
    public void run(String params) {
        statisticsService.statisticData();
    }
}
