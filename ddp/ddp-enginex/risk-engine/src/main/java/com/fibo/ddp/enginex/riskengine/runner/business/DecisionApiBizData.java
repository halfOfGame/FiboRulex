package com.fibo.ddp.enginex.riskengine.runner.business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class DecisionApiBizData {

    private String businessId; // 业务id
    private Long organId; // 组织id
    private Long engineId; // 引擎id
    private Map<String, Object> fields; // 指标字段键值对
    private Integer ruleHitRspConfig; // 规则命中返回配置（0：未命中不返回，1：未命中返回）
}
