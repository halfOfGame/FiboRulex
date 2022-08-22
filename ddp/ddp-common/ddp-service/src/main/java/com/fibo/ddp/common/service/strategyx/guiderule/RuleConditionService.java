package com.fibo.ddp.common.service.strategyx.guiderule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleConditionInfo;
import com.fibo.ddp.common.model.strategyx.guiderule.vo.RuleConditionVo;

import java.util.List;

/**
 * (RuleConditionInfo)表服务接口
 */
public interface RuleConditionService extends IService<RuleConditionInfo> {

    /**
     * 新增数据
     * @param ruleConditionVo 实例对象
     * @return 实例对象
     */
    RuleConditionVo insertRuleCondition(RuleConditionVo ruleConditionVo, Long ruleId, Long blockId);

    RuleConditionVo updateRuleCondition(Long ruleId, Long blockId, RuleConditionVo ruleConditionVo);

    boolean deleteRuleCondition(Long ruleId, Long versionId);


    RuleConditionVo assemble(List<RuleConditionInfo> list);

    List<RuleConditionInfo> disassemble(RuleConditionVo vo, Long ruleId, boolean needTempId);

    // runner
    RuleConditionVo queryByBlockId(Long blockId);

    List<String> queryFieldEnByVersionIds(List<Long> versionIds);

    List<RuleConditionInfo> listObjectsByBlockId(Long blockId);

    List<RuleConditionInfo> listObjectsByBlockIds(List<Long> blockIds);
}
