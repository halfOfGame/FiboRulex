package com.fibo.ddp.common.model.strategyx.guiderule.vo;

import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;
import com.fibo.ddp.common.model.strategyx.strategyout.StrategyOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors
public class RuleBlockVo extends RuleBlock {

    private RuleConditionVo ruleConditionVo;//规则对应的结点树

    private List<StrategyOutput> strategyOutputList;//输出字段

    private List<StrategyOutput> failOutputList;//失败输出字段
}
