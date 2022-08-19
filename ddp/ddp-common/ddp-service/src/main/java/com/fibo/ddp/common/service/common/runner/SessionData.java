package com.fibo.ddp.common.service.common.runner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class SessionData {

    private Long organId; // 组织id
    private Long engineId; // 引擎id
    private String businessId; // 业务id
    private Integer ruleHitRspConfig; // 规则命中返回配置，默认1（0：未命中不返回，1：未命中返回）
}
