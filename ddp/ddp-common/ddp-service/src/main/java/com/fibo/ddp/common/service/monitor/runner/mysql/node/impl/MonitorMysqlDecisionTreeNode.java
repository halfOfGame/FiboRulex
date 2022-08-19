package com.fibo.ddp.common.service.monitor.runner.mysql.node.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fibo.ddp.common.dao.monitor.decisionflow.TMonitorStrategyMapper;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorNode;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorStrategy;
import com.fibo.ddp.common.model.strategyx.decisiontree.DecisionTree;
import com.fibo.ddp.common.service.monitor.runner.mysql.node.MonitorMysqlService;
import com.fibo.ddp.common.service.strategyx.decisiontree.DecisionTreeService;
import com.fibo.ddp.common.utils.constant.runner.RunnerConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MonitorMysqlDecisionTreeNode implements MonitorMysqlService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorMysqlDecisionTreeNode.class);

    @Autowired
    private TMonitorStrategyMapper monitorStrategyMapper;
    @Autowired
    private DecisionTreeService decisionTreeService;

    @Override
    public void createMonitorStrategy(TMonitorNode monitorNode, Map<String, Object> outMap) {
        //根据快照中数据的个数进行确定存取条数。目前决策表只能选择一个，此处数组是预留防止以后多个的情况
        String decisionTreeStrategyIdKey = RunnerConstants.NODE_STRATEGYS_SNAPSHOT_PREFIX+monitorNode.getNodeId();
        //决策表
        logger.info("MonitorMysqlDecisionTreeNode============================「监控中心-策略监控信息」参数:{},{}",monitorNode, JSONObject.toJSONString(outMap.get(decisionTreeStrategyIdKey)));
        if(!outMap.containsKey(decisionTreeStrategyIdKey)){
            return;
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.parseObject(outMap.get(decisionTreeStrategyIdKey)+"").get("snapshot")+"");
        outMap.remove(decisionTreeStrategyIdKey);
        for (Object object : jsonArray) {
            if (object == null) {
                continue;
            }
            TMonitorStrategy monitorStrategy1 = new TMonitorStrategy();
            //1.节点监控JSONObject信息中拿出策略层面的信息
            JSONObject jsonObject = JSONObject.parseObject(object + "");
            //策略快照
            monitorStrategy1.setSnapshot(jsonObject.toString());
            //全量入参
            monitorStrategy1.setInput(monitorNode.getInput());
            //决策表结果
            monitorStrategy1.setOutput(monitorNode.getOutput());
            logger.info("MonitorMysqlDecisionTreeNode============================「监控中心-策略监控信息」monitorInfo:{}", monitorStrategy1);
            //策略ID 即 决策树版本id
            String ruleVersionId = jsonObject.getString("id");
            String decisionTreeId = jsonObject.getString("decisionTreeId");
            monitorStrategy1.setStrategyId(Long.valueOf(ruleVersionId));
            String result = getResult(monitorNode.getOutput(), decisionTreeId);
            //决策表执行结果
            monitorStrategy1.setResult(result);
            //获取对应决策表的名称
            String name = getStrategyName(decisionTreeId);
            //策略名称 决策树名称
            monitorStrategy1.setStrategyName(name);
            //策略类型
            monitorStrategy1.setStrategyType(monitorNode.getNodeType());
            //策略版本
            monitorStrategy1.setStrategyVersionCode(jsonObject.getString("versionCode"));
            //业务id
            monitorStrategy1.setBusinessId(monitorNode.getBusinessId());
            //节点id
            monitorStrategy1.setNodeId(monitorNode.getNodeId());
            //节点类型
            monitorStrategy1.setNodeType(monitorNode.getNodeType());
            //引擎版本id
            monitorStrategy1.setEngineVersionId(monitorNode.getEngineVersionId());
            logger.info("MonitorMysqlDecisionTreeNode============================「监控中心-策略监控信息」baseInfo:{}", monitorStrategy1);
            //组织id
            monitorStrategy1.setOrganId(monitorNode.getOrganId());
            //时间
            monitorStrategy1.setCreateTime(LocalDateTime.now());
            monitorStrategy1.setMonitorParentId(monitorNode.getId() + "");
            monitorStrategy1.setEngineId(monitorNode.getEngineId());
            monitorStrategyMapper.insert(monitorStrategy1);
        }
    }

    private String getStrategyName(String decisionTreeId) {
        DecisionTree decisionTree = decisionTreeService.getById(decisionTreeId);
        return decisionTree == null ? null : decisionTree.getName();
    }

    private static String getResult(String output, String ruleVersionId) {
        if (!StringUtils.isEmpty(output) && !StringUtils.isEmpty(ruleVersionId)) {
            JSONObject resultJsonObject = JSONObject.parseObject(output);
            JSONArray allResults = JSONArray.parseArray(resultJsonObject.getString("result"));
            List<Object> one = allResults.stream().filter(
                    object -> ruleVersionId.equals(JSONObject.parseObject(JSON.toJSONString(object)).getString("decisionTreeId"))).collect(Collectors.toList());

            if (one.size() == 1) {
                Object strategyResultJson = one.get(0);
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(strategyResultJson));
                return jsonObject.getString("result");
            }
        }
        return null;
    }
}
