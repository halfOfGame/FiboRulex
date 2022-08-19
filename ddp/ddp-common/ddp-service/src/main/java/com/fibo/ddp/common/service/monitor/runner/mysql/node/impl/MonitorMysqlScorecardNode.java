package com.fibo.ddp.common.service.monitor.runner.mysql.node.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fibo.ddp.common.dao.monitor.decisionflow.TMonitorStrategyMapper;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorNode;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorStrategy;
import com.fibo.ddp.common.model.strategyx.scorecard.vo.ScorecardVersionVo;
import com.fibo.ddp.common.service.monitor.runner.mysql.node.MonitorMysqlService;
import com.fibo.ddp.common.utils.constant.runner.RunnerConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MonitorMysqlScorecardNode implements MonitorMysqlService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TMonitorStrategyMapper monitorStrategyMapper;

    @Override
    public void createMonitorStrategy(TMonitorNode monitorNode, Map<String, Object> outMap) {
        logger.info("MonitorMysqlScorecardNode============================「监控中心-策略监控信息」参数:{}",monitorNode);
        //策略快照 根绝评分卡id，去查对应的字段。
        String strategySnopshotKeyId = RunnerConstants.NODE_STRATEGYS_SNAPSHOT_PREFIX+monitorNode.getNodeId();
        if(!outMap.containsKey(strategySnopshotKeyId)){
            return;
        }
        ScorecardVersionVo scorecardVo = JSONObject.parseObject(JSONObject.toJSONString(JSONObject.parseObject(outMap.get(strategySnopshotKeyId)+"")),ScorecardVersionVo.class);
        outMap.remove(strategySnopshotKeyId);
        if(scorecardVo==null){
            return;
        }
        TMonitorStrategy monitorStrategy = new TMonitorStrategy();
//        monitorInfo.setSnapshot(JSONObject.parseObject(JSONObject.toJSONString(scorecardVo.getVersionList().get(0))));
        //全量入参
        monitorStrategy.setInput(monitorNode.getInput());
        //详细得分情况
//        JSONObject scoreDetail = new JSONObject();
//        scoreDetail.put("scoreDetail", JSON.parseObject(monitorNode.getOutput()).get("scoreDetail"));
        monitorStrategy.setOutput(monitorNode.getOutput());
        logger.info("MonitorMysqlScorecardNode============================「监控中心-策略监控信息」monitorInfo:{}",monitorNode);
        //策略ID 即 评分卡版本Id
        monitorStrategy.setStrategyId(scorecardVo.getId());

        //策略类型
        monitorStrategy.setStrategyType(monitorNode.getNodeType());
        //分值
        JSONObject outputMap = new JSONObject();
        if(StringUtils.isNotBlank(monitorNode.getOutput())){
           outputMap = JSON.parseObject(monitorNode.getOutput());
        }
        if(outputMap.containsKey("score")){
            monitorStrategy.setResult(outputMap.getString("score"));
        }
        if(outputMap.containsKey("cardName")){
            //策略名称
            monitorStrategy.setStrategyName(outputMap.getString("cardName"));
        }
        //业务id
        monitorStrategy.setBusinessId(monitorNode.getBusinessId());
        //节点id
        monitorStrategy.setNodeId(monitorNode.getNodeId());
        //节点类型
        monitorStrategy.setNodeType(monitorNode.getNodeType());
        //引擎版本id
        monitorStrategy.setEngineVersionId(monitorNode.getEngineVersionId());
        logger.info("MonitorMysqlScorecardNode============================「监控中心-策略监控信息」baseInfo:{}",monitorNode);
        //快照
        monitorStrategy.setSnapshot(JSON.toJSONString(scorecardVo));
        //版本
        monitorStrategy.setStrategyVersionCode(scorecardVo.getVersionCode());
        //组织id
        monitorStrategy.setOrganId(monitorNode.getOrganId());
        //时间
        monitorStrategy.setCreateTime(LocalDateTime.now());
        monitorStrategy.setMonitorParentId(monitorNode.getId()+"");
        monitorStrategy.setEngineId(monitorNode.getEngineId());
        monitorStrategy.setCreateTime(LocalDateTime.now());
        monitorStrategyMapper.insert(monitorStrategy);
    }
}
