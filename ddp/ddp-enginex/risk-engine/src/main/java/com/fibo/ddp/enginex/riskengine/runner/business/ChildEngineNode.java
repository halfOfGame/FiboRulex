package com.fibo.ddp.enginex.riskengine.runner.business;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fibo.ddp.common.model.enginex.risk.Engine;
import com.fibo.ddp.common.model.enginex.risk.EngineNode;
import com.fibo.ddp.common.service.common.runner.RunnerSessionManager;
import com.fibo.ddp.common.service.common.runner.SessionData;
import com.fibo.ddp.common.service.enginex.risk.EngineService;
import com.fibo.ddp.common.utils.constant.runner.RunnerConstants;
import com.fibo.ddp.enginex.runner.node.EngineRunnerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChildEngineNode implements EngineRunnerNode {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RiskEngineBusiness riskEngineBusiness;
    @Autowired
    public EngineService engineService;

    @Override
    public void getNodeField(EngineNode engineNode, Map<String, Object> inputParam) {

    }

    @Override
    public void runNode(EngineNode engineNode, Map<String, Object> inputParam, Map<String, Object> outMap) {
        //监控中心-节点信息快照记录
        if (engineNode != null && engineNode.getNodeJson() != null) {
            JSONObject nodeSnapshot = new JSONObject();
            nodeSnapshot.put(RunnerConstants.NODE_SNAPSHOT, engineNode.getNodeJson());
            outMap.put(RunnerConstants.NODE_SNAPSHOT, nodeSnapshot);
        }
        Long childEngineId = Long.valueOf(engineNode.getNodeJson());
        SessionData sessionData = RunnerSessionManager.getSession();
        // 封装请求参数
        DecisionApiBizData bizData = new DecisionApiBizData();
        bizData.setBusinessId(sessionData.getBusinessId());
        bizData.setOrganId(sessionData.getOrganId());
        bizData.setEngineId(childEngineId);
        bizData.setFields(inputParam);
        // 决策流执行
        Map<String, Object> paramJson = JSONObject.parseObject(JSONObject.toJSONString(bizData), Map.class);
        String engineResult = "";
        Integer resultId = null;
        try {
            String result = riskEngineBusiness.engineApi(paramJson);
            JSONObject resultObj = JSONObject.parseObject(result);
            if ("0x0000".equals(resultObj.getString("status"))) {
                engineResult = resultObj.getString("result");
                resultId = resultObj.getInteger("resultId");
            }
        } catch (Exception e) {
            logger.error("子引擎节点执行异常", e);
            throw e;
        }

        Engine engine = engineService.getEngineById(childEngineId);
        JSONObject nodeInfo = new JSONObject();
        nodeInfo.put("engineNode", engineNode);
        nodeInfo.put("nodeId", engineNode.getNodeId());
        nodeInfo.put("nodeName", engineNode.getNodeName());
        nodeInfo.put("nodeType", engineNode.getNodeType());
        outMap.put("nodeInfo", nodeInfo);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nodeId", engineNode.getNodeId());
        jsonObject.put("nodeName", engineNode.getNodeName());
        jsonObject.put("engineId", engine.getId());
        jsonObject.put("engineName", engine.getName());
        jsonObject.put("result", engineResult);
        outMap.put("nodeResult", jsonObject);
        if (outMap.containsKey("childEngineJson")) {
            JSONArray resultJson = (JSONArray) outMap.get("childEngineJson");
            resultJson.add(jsonObject);
        } else {
            JSONArray resultJson = new JSONArray();
            resultJson.add(jsonObject);
            outMap.put("childEngineJson", resultJson);
        }
        String key = engineNode.getNodeType() + "_" + engineNode.getNodeId() + "_" + engineNode.getNodeJson() + "_result";
        inputParam.put(key, engineResult);

        engine.setResultId(resultId);
        outMap.put(RunnerConstants.NODE_STRATEGYS_SNAPSHOT_PREFIX + engineNode.getNodeId(), JSONObject.toJSONString(engine));
    }
}
