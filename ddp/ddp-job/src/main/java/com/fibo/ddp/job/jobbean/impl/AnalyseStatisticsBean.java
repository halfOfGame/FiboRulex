package com.fibo.ddp.job.jobbean.impl;

import com.fibo.ddp.common.service.analyse.StatisticsService;
import com.fibo.ddp.job.jobbean.FiboTaskInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 分析中心引擎概况数据跑批
 * @createDate 2022-08-13 14:05:29
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
@Component("analyseStatisticsBean")
public class AnalyseStatisticsBean implements FiboTaskInterface {

    @Resource
    @Qualifier("statisticsServiceImpl")
    private StatisticsService statisticsServiceImpl;

    @Override
    public void run(String params) {
        statisticsServiceImpl.statisticData();
    }
}
