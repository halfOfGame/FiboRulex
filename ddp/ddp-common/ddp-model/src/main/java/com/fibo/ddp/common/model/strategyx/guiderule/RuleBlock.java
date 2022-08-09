package com.fibo.ddp.common.model.strategyx.guiderule;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.io.Serializable;

/**
 * 规则块配置表(RuleBlock)实体类
 *
 * @author andy.wang
 * @since 2022-08-05 10:32:12
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_rule_block")
public class RuleBlock implements Serializable {
    private static final long serialVersionUID = 153524790651348062L;
    
    @TableId(type = IdType.AUTO)
    /**
     * 主键：规则块id
     */
    private Long id;
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 规则版本的id
     */
    private Long versionId;
    /**
     * 存放执行结果的变量
     */
    private String resultFieldEn;
    /**
     * 规则分数
     */
    private Integer score;
    /**
     * 存放得分的变量
     */
    private String scoreFieldEn;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}

