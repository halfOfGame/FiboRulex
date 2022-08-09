package com.fibo.ddp.common.dao.strategyx.guiderule;

import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 规则块配置表(RuleBlock)表数据库访问层
 *
 * @author andy.wang
 * @since 2022-08-05 10:32:11
 */
@Mapper
public interface RuleBlockMapper extends BaseMapper<RuleBlock>{

}
