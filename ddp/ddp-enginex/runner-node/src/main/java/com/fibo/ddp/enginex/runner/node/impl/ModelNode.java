package com.fibo.ddp.enginex.runner.node.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fibo.ddp.common.model.datax.datamanage.Field;
import com.fibo.ddp.common.model.enginex.risk.EngineNode;
import com.fibo.ddp.common.model.strategyx.aimodel.MachineLearningModels;
import com.fibo.ddp.common.model.strategyx.strategyout.StrategyOutput;
import com.fibo.ddp.common.service.datax.datamanage.FieldService;
import com.fibo.ddp.common.service.datax.runner.CommonService;
import com.fibo.ddp.common.service.datax.runner.ExecuteUtils;
import com.fibo.ddp.common.service.strategyx.aimodel.ModelsService;
import com.fibo.ddp.common.service.strategyx.aimodel.PMMLExecutor.PMMLExecutor;
import com.fibo.ddp.common.service.strategyx.strategyout.StrategyOutputService;
import com.fibo.ddp.common.utils.constant.runner.RunnerConstants;
import com.fibo.ddp.common.utils.constant.strategyx.StrategyType;
import com.fibo.ddp.enginex.runner.node.EngineRunnerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模型节点
 */
@Service
public class ModelNode implements EngineRunnerNode {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonService commonService;
    @Resource
    public ModelsService modelsService;
    @Resource(name = "PMMLExecutorRFImpl")
    private PMMLExecutor pmmlExecutor;
    @Resource
    private StrategyOutputService outputService;
    @Autowired
    private FieldService fieldService;

    private List<Long> getExecuteVersionIdList(EngineNode engineNode) {
        return ExecuteUtils.getExecuteIdList(engineNode, "modelId");
    }

    @Override
    public void getNodeField(EngineNode engineNode, Map<String, Object> inputParam) {
        logger.info("start【获取模型节点指标】ModelNode.getNodeField engineNode:{},inputParam:{}", JSONObject.toJSONString(engineNode), JSONObject.toJSONString(inputParam));
        List<Long> modelIds = getExecuteVersionIdList(engineNode);
        List<String> ens = new ArrayList<>();
        for (Long modelId : modelIds) {
            MachineLearningModels models = modelsService.selectById(Integer.valueOf(modelId.toString()));
            List<String> mappingFieldList = Arrays.asList(models.getMappingField().split(","));
            ens.addAll(mappingFieldList);
        }
        List<Field> fieldList = fieldService.listByEns(ens);
        Set<Long> fieldIdSet = fieldList.stream().map(item -> item.getId()).collect(Collectors.toSet());
        List<Long> ids = new ArrayList<>(fieldIdSet);
        try {
            commonService.getFieldByIds(ids, inputParam);
        } catch (Exception e) {
            logger.error("模型中字段未完全找到");
            e.printStackTrace();
        }
    }

    @Override
    public void runNode(EngineNode engineNode, Map<String, Object> inputParam, Map<String, Object> outMap) {
        //监控中心--记录节点快照信息
        if (engineNode != null && engineNode.getSnapshot() != null) {
            outMap.put(RunnerConstants.NODE_SNAPSHOT, engineNode.getSnapshot());
        }
        List<Long> modelIds = getExecuteVersionIdList(engineNode);
        Long modelId = modelIds.get(0);
        MachineLearningModels models = modelsService.selectById(Integer.valueOf(modelId.toString()));
        JSONObject nodeInfo = new JSONObject();
        nodeInfo.put("engineNode", engineNode);
        nodeInfo.put("nodeId", engineNode.getNodeId());
        nodeInfo.put("nodeName", engineNode.getNodeName());
        nodeInfo.put("nodeType", engineNode.getNodeType());
        outMap.put("nodeInfo", nodeInfo);
        // 加载模型文件
        org.jpmml.evaluator.Evaluator evaluator = pmmlExecutor.loadPmml(models.getFilePath());

        Map<String, Object> input = new HashMap<>();
        String[] modelFieldArr = models.getModelField().split(",");
        String[] mappingFieldArr = models.getMappingField().split(",");
        for (int i = 0; i < modelFieldArr.length; i++) {
            input.put(modelFieldArr[i], inputParam.get(mappingFieldArr[i]));
        }
        // 调用模型
        double modelResult = 0d;
        try {
            modelResult = pmmlExecutor.predict(evaluator, input);
        } catch (Exception e) {
            logger.error("模型执行异常", e);
            throw e;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nodeId", engineNode.getNodeId());
        jsonObject.put("nodeName", engineNode.getNodeName());
        jsonObject.put("modelId", models.getId());
        jsonObject.put("modelName", models.getModelName());
        jsonObject.put("result", modelResult);
        List<JSONObject> fieldList = new ArrayList<>();
        JSONObject result = new JSONObject();
        result.put(models.getResultFieldEn(), modelResult);
        fieldList.add(result);
        fieldList.addAll(outputService.setOutput(new StrategyOutput(modelId, StrategyType.MODELS), input));
        jsonObject.put("fieldList", fieldList);
        outMap.put("nodeResult", jsonObject);
        if (outMap.containsKey("modelJson")) {
            JSONArray resultJson = (JSONArray) outMap.get("modelJson");
            resultJson.add(jsonObject);
        } else {
            JSONArray resultJson = new JSONArray();
            resultJson.add(jsonObject);
            outMap.put("modelJson", resultJson);
        }
        inputParam.put("model_" + modelId, modelResult);
        inputParam.put(models.getResultFieldEn(), modelResult);
        outMap.put("model_" + modelId, modelResult);
        terminalCondition(engineNode, inputParam, outMap, modelResult);
        outMap.put(RunnerConstants.NODE_STRATEGYS_SNAPSHOT_PREFIX + engineNode.getNodeId(), JSONObject.toJSONString(models));
    }

    private void terminalCondition(EngineNode engineNode, Map<String, Object> inputParam, Map<String, Object> outMap, Object executeResult) {
        String resultKey = engineNode.getNodeType() + "_" + engineNode.getNodeId() + "_terminal_result";
        Map<String, Object> map = new HashMap<>();
        map.put(resultKey, executeResult);
        ExecuteUtils.terminalCondition(engineNode, inputParam, outMap, map);
    }
}
