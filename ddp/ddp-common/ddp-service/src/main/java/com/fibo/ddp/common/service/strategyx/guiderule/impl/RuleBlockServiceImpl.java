package com.fibo.ddp.common.service.strategyx.guiderule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fibo.ddp.common.dao.canal.TableEnum;
import com.fibo.ddp.common.dao.strategyx.guiderule.RuleBlockMapper;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;
import com.fibo.ddp.common.service.redis.RedisManager;
import com.fibo.ddp.common.service.redis.RedisUtils;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleBlockService;
import com.fibo.ddp.common.utils.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private RedisManager redisManager;
    @Value("${switch.use.cache}")
    private String cacheSwitch;

    @Override
    public List<RuleBlock> listObjectsByVersionId(Long versionId) {
        List<RuleBlock> ruleBlockList = null;
        if(Constants.switchFlag.ON.equals(cacheSwitch)){
            String key = RedisUtils.getForeignKey(TableEnum.T_RULE_BLOCK, versionId);
            ruleBlockList = redisManager.getByForeignKey(key, RuleBlock.class);

        } else {
            LambdaQueryWrapper<RuleBlock> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RuleBlock::getVersionId, versionId);
            ruleBlockList = this.list(queryWrapper);
        }

        return ruleBlockList;
    }

    @Override
    public List<RuleBlock> listObjectsByVersionIds(List<Long> versionIds) {
        List<RuleBlock> ruleBlockList = null;
        if(Constants.switchFlag.ON.equals(cacheSwitch)){
            List<String> keys = RedisUtils.getForeignKey(TableEnum.T_RULE_BLOCK, versionIds);
            ruleBlockList = redisManager.hgetAllBatchByForeignKeys(keys, RuleBlock.class);

        } else {
            LambdaQueryWrapper<RuleBlock> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(RuleBlock::getVersionId, versionIds);
            ruleBlockList = this.list(queryWrapper);
        }

        return ruleBlockList;
    }

    @Override
    public boolean removeObjects(Long ruleId, Long versionId) {
        LambdaQueryWrapper<RuleBlock> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RuleBlock::getRuleId, ruleId);
        queryWrapper.eq(RuleBlock::getVersionId, versionId);
        return this.remove(queryWrapper);
    }
}
