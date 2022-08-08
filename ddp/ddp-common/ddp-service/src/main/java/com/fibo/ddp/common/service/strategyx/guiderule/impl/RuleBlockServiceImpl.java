package com.fibo.ddp.common.service.strategyx.guiderule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fibo.ddp.common.dao.strategyx.guiderule.RuleBlockMapper;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleBlockService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 规则块配置表(RuleBlock)表服务实现类
 *
 * @author andy.wang
 * @since 2022-08-05 10:32:25
 */
@Service
public class RuleBlockServiceImpl extends ServiceImpl<RuleBlockMapper, RuleBlock> implements RuleBlockService {

    @Override
    public List<RuleBlock> listObjectsByVersionId(Long versionId) {
        LambdaQueryWrapper<RuleBlock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RuleBlock::getVersionId, versionId);
        return list(queryWrapper);
    }

    @Override
    public boolean removeObjects(Long ruleId, Long versionId) {
        LambdaQueryWrapper<RuleBlock> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RuleBlock::getRuleId, ruleId);
        queryWrapper.eq(RuleBlock::getVersionId, versionId);
        return this.remove(queryWrapper);
    }
}
