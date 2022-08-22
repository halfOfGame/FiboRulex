package com.fibo.ddp.common.service.strategyx.guiderule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;

import java.util.List;

/**
 * 规则块配置表(RuleBlock)表服务接口
 *
 * @author andy.wang
 * @since 2022-08-05 10:32:22
 */
public interface RuleBlockService extends IService<RuleBlock> {

    List<RuleBlock> listObjectsByVersionId(Long versionId);

    List<RuleBlock> listObjectsByVersionIds(List<Long> versionIds);

    boolean removeObjects(Long ruleId, Long versionId);
}
