package com.fibo.ddp.common.service.monitor.runner.mysql.node.impl;

import com.alibaba.fastjson.JSONObject;
import com.fibo.ddp.common.dao.monitor.decisionflow.TMonitorStrategyMapper;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorNode;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorStrategy;
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
public class MonitorMysqlModelNode implements MonitorMysqlService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TMonitorStrategyMapper monitorStrategyMapper;

    @Override
    public void createMonitorStrategy(TMonitorNode monitorNode, Map<String, Object> outMap) {
        String strategyKey = RunnerConstants.NODE_STRATEGYS_SNAPSHOT_PREFIX + monitorNode.getNodeId();
        //模型
        logger.info("MonitorMysqlModelNode============================「监控中心-策略监控信息」参数:{},{}", monitorNode, JSONObject.toJSONString(outMap.get(strategyKey)));
        if (!outMap.containsKey(strategyKey)) {
            return;
        }
        JSONObject object = JSONObject.parseObject(outMap.get(strategyKey).toString());
        outMap.remove(strategyKey);
        if (object == null) {
            return;
        }
        TMonitorStrategy monitorStrategy1 = new TMonitorStrategy();
        //1.节点监控JSONObject信息中拿出策略层面的信息
        JSONObject jsonObject = JSONObject.parseObject(object + "");
        //策略快照
        monitorStrategy1.setSnapshot(jsonObject.toString());
        //全量入参
        monitorStrategy1.setInput(monitorNode.getInput());
        //执行结果全量输出
        monitorStrategy1.setOutput(monitorNode.getOutput());
        logger.info("MonitorMysqlModelNode============================「监控中心-策略监控信息」monitorInfo:{}", monitorStrategy1);
        //策略ID
        monitorStrategy1.setStrategyId(Long.valueOf(jsonObject.getLong("id")));
        //策略名称
        monitorStrategy1.setStrategyName(jsonObject.getString("modelName"));
        //策略结果
        String result = getResult(monitorNode.getOutput());
        monitorStrategy1.setResult(result);
        //策略类型
        monitorStrategy1.setStrategyType(monitorNode.getNodeType());
        //业务id
        monitorStrategy1.setBusinessId(monitorNode.getBusinessId());
        //节点id
        monitorStrategy1.setNodeId(monitorNode.getNodeId());
        //节点类型
        monitorStrategy1.setNodeType(monitorNode.getNodeType());
        //引擎版本id
        monitorStrategy1.setEngineVersionId(monitorNode.getEngineVersionId());
        //策略版本
        monitorStrategy1.setStrategyVersionCode(jsonObject.getString("versionCode"));
        logger.info("MonitorMysqlModelNode============================「监控中心-策略监控信息」baseInfo:{}", monitorStrategy1);
        //组织id
        monitorStrategy1.setOrganId(monitorNode.getOrganId());
        //时间
        monitorStrategy1.setCreateTime(LocalDateTime.now());
        monitorStrategy1.setMonitorParentId(monitorNode.getId() + "");
        monitorStrategy1.setEngineId(monitorNode.getEngineId());
        monitorStrategyMapper.insert(monitorStrategy1);
    }

    private static String getResult(String output) {
        if (StringUtils.isNotBlank(output)) {
            JSONObject jsonObject = JSONObject.parseObject(output);
            return jsonObject.getString("result");
        }
        return null;
    }
}
