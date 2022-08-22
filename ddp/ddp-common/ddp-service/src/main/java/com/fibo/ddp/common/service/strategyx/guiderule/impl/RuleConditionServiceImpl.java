package com.fibo.ddp.common.service.strategyx.guiderule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fibo.ddp.common.dao.canal.TableEnum;
import com.fibo.ddp.common.dao.strategyx.guiderule.RuleConditionInfoMapper;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleConditionInfo;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleLoopGroupAction;
import com.fibo.ddp.common.model.strategyx.guiderule.vo.RuleConditionVo;
import com.fibo.ddp.common.service.redis.RedisManager;
import com.fibo.ddp.common.service.redis.RedisUtils;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleBlockService;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleConditionService;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleLoopGroupActionService;
import com.fibo.ddp.common.utils.constant.Constants;
import com.fibo.ddp.common.utils.constant.strategyx.RuleConditionConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (RuleConditionInfo)表服务实现类
 */
@Service("ruleConditionService2")
public class RuleConditionServiceImpl extends ServiceImpl<RuleConditionInfoMapper, RuleConditionInfo> implements RuleConditionService {
    @Resource
    private RuleConditionInfoMapper ruleConditionInfoMapper;

    @Resource
    private RuleLoopGroupActionService loopGroupActionService;

    @Autowired
    private RedisManager redisManager;

    @Value("${switch.use.cache}")
    private String cacheSwitch;

    @Autowired
    private RuleBlockService ruleBlockService;

    @Override
    public RuleConditionVo queryByBlockId(Long blockId) {
        if (blockId == null) {
            return null;
        }
        List<RuleConditionInfo> ruleConditionInfoList = listObjectsByBlockId(blockId);
        //组装为需要的树形结构
        RuleConditionVo result = null;
        if (ruleConditionInfoList != null) {
            result = this.assemble(ruleConditionInfoList);
        }
        return result;
    }

    /**
     * 新增数据
     *
     * @param ruleConditionVo 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public RuleConditionVo insertRuleCondition(RuleConditionVo ruleConditionVo, Long ruleId, Long blockId) {
        if (ruleConditionVo == null || ruleId == null) {
            return null;
        }
        Long versionId = ruleConditionVo.getVersionId();
        if (versionId == null) {
            versionId = 0L;
        }
        Long parentId = RuleConditionConst.DEFAULT_CONDITION_PARENT_ID;
        //将插入的条件树拆解
        List<RuleConditionInfo> list = this.disassemble(ruleConditionVo, ruleId, true);
        for (RuleConditionInfo info : list) {
            info.setVersionId(versionId);
            info.setBlockId(blockId);
        }
        //找出唯一根节点
        RuleConditionInfo root = null;
        for (RuleConditionInfo info : list) {
            if (info.getParentId() == parentId) {
                root = info;
                break;
            }
        }
        //递归插入所有数据
        boolean saveResult = this.save(list, root);
        if (!saveResult) {
            return null;
        }
        return null;
    }

    /**
     * 修改规则条件
     *
     * @param ruleConditionVo
     * @return
     */
    @Override
    @Transactional
    public RuleConditionVo updateRuleCondition(Long ruleId, Long blockId, RuleConditionVo ruleConditionVo) {
        if (ruleId == null) {
            return null;
        }
        //插入条件树
        if (ruleConditionVo != null) {
            RuleConditionVo insertResult = this.insertRuleCondition(ruleConditionVo, ruleId, blockId);
            return insertResult;
        }
        return null;
    }

    /**
     * 删除根据规则id规则条件
     *
     * @param ruleId
     * @return
     */
    @Override
    @Transactional
    public boolean deleteRuleCondition(Long ruleId, Long versionId) {
        if (ruleId == null) {
            return false;
        }
        //删除循环动作子表中的内容
        RuleConditionInfo info = new RuleConditionInfo();
        if (ruleId != null) {
            info.setRuleId(ruleId);
        }
        info.setVersionId(versionId);
        info.setLogical(RuleConditionConst.LOOP_RULE_LOGICAL);
        List<RuleConditionInfo> ruleConditionInfoList = ruleConditionInfoMapper.selectList(new QueryWrapper<>(info));
        for (RuleConditionInfo ruleConditionInfo : ruleConditionInfoList) {
            loopGroupActionService.deleteLoopGroupByForId(ruleConditionInfo.getId());
        }
        //删除条件表内容
        LambdaQueryWrapper<RuleConditionInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RuleConditionInfo::getVersionId, versionId);
        if (ruleId != null) {
            queryWrapper.eq(RuleConditionInfo::getRuleId, ruleId);
        }
        int delete = ruleConditionInfoMapper.delete(queryWrapper);
        return delete > 0 ? true : false;
    }


    //装配方法，将规则条件List装配成一个规则树并返回
    @Override
    public RuleConditionVo assemble(List<RuleConditionInfo> list) {
        RuleConditionVo root = null;
        //转换为Vo
        List<RuleConditionVo> rcVoList = transferToVoList(list);
        //获取根节点，根节点只有一个的时候进行操作，并且返回拼装好的规则树，否则返回null
        List<RuleConditionVo> collect = rcVoList.stream().filter(rc -> {
            return rc.getParentId() == RuleConditionConst.DEFAULT_CONDITION_PARENT_ID;
        }).collect(Collectors.toList());
        if (collect.size() == 1) {
            root = collect.get(0);
            RuleConditionVo ruleTree = coupling(rcVoList, root);
            return ruleTree;
        }
        return null;
    }

    // 拆解方法，将规则条件Vo转换未规则条件list
    @Override
    public List<RuleConditionInfo> disassemble(RuleConditionVo vo, Long ruleId, boolean needTempId) {
        if (vo == null) {
            return null;
        }
        if (needTempId) {
            vo.setParentId(RuleConditionConst.DEFAULT_CONDITION_PARENT_ID);
            vo.setInsertTempId(UUID.randomUUID().toString().replace("-", ""));
        }
        List<RuleConditionVo> voList = decoupling(vo, ruleId, needTempId);
        List<RuleConditionInfo> ruleConditionInfoList = transferToInfoList(voList);
        return ruleConditionInfoList;
    }

    //存储
    @Transactional
    public boolean save(List<RuleConditionInfo> list, RuleConditionInfo root) {
        String tempId = root.getInsertTempId();
        //保存根节点
        int insert = ruleConditionInfoMapper.insert(root);
        if (insert <= 0) {
            return false;
        }
        Long id = root.getId();
        for (int i = 0; i < list.size(); i++) {
            RuleConditionInfo info = list.get(i);
            //保存根节点的子节点
            if (tempId.equals(info.getTempParentId())) {
                info.setParentId(id);
                //递归保存每个节点和子节点
                save(list, info);
                //对有动作的循环条件保存循环动作表内容
                if (root.getLogical() != null && RuleConditionConst.LOOP_RULE_LOGICAL.equals(root.getLogical()) && info.getLoopGroupActions() != null) {
                    loopGroupActionService.addLoopGroupList(id, info.getId(), info.getLoopGroupActions());
                }
            }
        }
        return true;
    }

    //耦合方法：将规则节点列表耦合规则树(),循环规则的子节点需要去查循环表获取
    private RuleConditionVo coupling(List<RuleConditionVo> list, RuleConditionVo root) {
        List<RuleConditionVo> children = new ArrayList<>();
        for (RuleConditionVo rc : list) {
            //处理root的子节点
            if (root.getId().equals(rc.getParentId())) {
                RuleConditionVo rcVo = coupling(list, rc);
                String logical = root.getLogical();

                if (logical != null && !"".equals(logical)) {
                    switch (logical) {
                        //当root为for节点，则此子节点需要拼上循环动作
                        case RuleConditionConst.LOOP_RULE_LOGICAL:
                            List<RuleLoopGroupAction> loopList = loopGroupActionService.getRuleLoopList(root.getId(), rc.getId());
                            rcVo.setLoopGroupActions(loopList);
                            if (rc.getConditionType() == RuleConditionConst.LOOP_RULE_RESULT_CONDITION) {
                                root.setLoopResultCondition(rcVo);
                                continue;
                            }
                            break;
                        //当root为条件组节点，则此子节点需要拼上条件组结果
                        case RuleConditionConst.CONDITION_GROUP_LOGICAL:
                            if (rc.getConditionType() == RuleConditionConst.CONDITION_GROUP_RESULT_CONDITION) {
                                root.setCondGroupResultCondition(rcVo);
                                continue;
                            }
                            break;


                    }

                }
//                if (root.getLogical() != null && RuleConditionConst.LOOP_RULE_LOGICAL.equals(root.getLogical())) {
//                    List<RuleLoopGroupAction> loopList = loopGroupActionService.getRuleLoopList(root.getUserId(),rc.getUserId());
//                    rcVo.setLoopGroupActions(loopList);
//                    if (rc.getConditionType()==RuleConditionConst.LOOP_RULE_RESULT_CONDITION){
//                        root.setLoopResultCondition(rcVo);
//                        continue;
//                    }
//                }
                //
                children.add(rcVo);
            }
        }
        root.setChildren(children);
        return root;
    }

    //解耦方法：将规则树解耦为节点列表
    private List<RuleConditionVo> decoupling(RuleConditionVo vo, Long ruleId, boolean needTempId) {
        List<RuleConditionVo> list = new ArrayList<>();
        List<RuleConditionVo> children = vo.getChildren();

        //处理条件树根节点的子条件
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                RuleConditionVo child = children.get(i);
                if (needTempId) {
                    child.setInsertTempId(UUID.randomUUID().toString().replace("-", ""));
                    child.setTempParentId(vo.getInsertTempId());
                }
                List<RuleConditionVo> childList = decoupling(child, ruleId, needTempId);
                list.addAll(childList);
            }
        }
        vo.setRuleId(ruleId);
        list.add(vo);
        //处理for条件的结果条件
        Integer conditionType = vo.getConditionType();
        if (conditionType != null) {
            switch (conditionType) {
                case RuleConditionConst.LOOP_RULE_ROOT:
                    RuleConditionVo loopResult = vo.getLoopResultCondition();
                    loopResult.setRuleId(ruleId);
                    loopResult.setTempParentId(vo.getInsertTempId());
                    loopResult.setInsertTempId(UUID.randomUUID().toString().replace("-", ""));
                    List<RuleConditionVo> loopResultList = decoupling(loopResult, ruleId, needTempId);
                    list.addAll(loopResultList);
                    break;
                case RuleConditionConst.CONDITION_GROUP_ROOT:
                    RuleConditionVo condGroupResult = vo.getCondGroupResultCondition();
                    condGroupResult.setRuleId(ruleId);
                    condGroupResult.setTempParentId(vo.getInsertTempId());
                    condGroupResult.setInsertTempId(UUID.randomUUID().toString().replace("-", ""));
                    List<RuleConditionVo> condGroupResultList = decoupling(condGroupResult, ruleId, needTempId);
                    list.addAll(condGroupResultList);
                    break;
            }
        }
//        if (vo.getConditionType()==RuleConditionConst.LOOP_RULE_ROOT){
//            RuleConditionVo loopResult = vo.getLoopResultCondition();
//            loopResult.setRuleId(ruleId);
//            loopResult.setTempParentId(vo.getInsertTempId());
//            loopResult.setInsertTempId(UUID.randomUUID().toString().replace("-", ""));
//            List<RuleConditionVo> loopResultList = decoupling(loopResult, ruleId, needTempId);
//            list.addAll(loopResultList);
//        }
        return list;
    }

    //List<RuleConditionInfo>转换为List<RuleConditionVo>
    private List<RuleConditionVo> transferToVoList(List<RuleConditionInfo> list) {
        List<RuleConditionVo> rcVoList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RuleConditionVo rcVo = new RuleConditionVo();
            BeanUtils.copyProperties(list.get(i), rcVo);
            rcVoList.add(rcVo);
        }
        return rcVoList;
    }

    //List<RuleConditionVo>转换为List<RuleConditionInfo>
    private List<RuleConditionInfo> transferToInfoList(List<RuleConditionVo> list) {
        List<RuleConditionInfo> rcList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RuleConditionInfo rc = new RuleConditionInfo();
            BeanUtils.copyProperties(list.get(i), rc);
            rcList.add(rc);
        }
        return rcList;
    }

    @Override
    public List<String> queryFieldEnByVersionIds(List<Long> versionIds) {
        List<RuleBlock> ruleBlockList = ruleBlockService.listObjectsByVersionIds(versionIds);
        List<Long> blockIds = ruleBlockList.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<RuleConditionInfo> ruleConditions = listObjectsByBlockIds(blockIds);

        ruleConditions = ruleConditions.stream()
                .filter(item -> StringUtils.isNotBlank(item.getFieldEn()) && !"1".equals(item.getFieldType()))
                .collect(Collectors.toList());

        Set<String> result = new HashSet<>();
        if (ruleConditions != null) {
            for (RuleConditionInfo condition : ruleConditions) {
                result.add(condition.getFieldEn());
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public List<RuleConditionInfo> listObjectsByBlockId(Long blockId) {
        List<RuleConditionInfo> ruleConditionInfoList = null;
        if (Constants.switchFlag.ON.equals(cacheSwitch)) {
            String key = RedisUtils.getForeignKey(TableEnum.T_RULE_CONDITION, blockId);
            ruleConditionInfoList = redisManager.getByForeignKey(key, RuleConditionInfo.class);
        } else {
            LambdaQueryWrapper<RuleConditionInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RuleConditionInfo::getBlockId, blockId);
            ruleConditionInfoList = this.list(queryWrapper);
        }
        return ruleConditionInfoList;
    }

    @Override
    public List<RuleConditionInfo> listObjectsByBlockIds(List<Long> blockIds) {
        List<RuleConditionInfo> ruleConditionInfoList = null;
        if (Constants.switchFlag.ON.equals(cacheSwitch)) {
            List<String> keys = RedisUtils.getForeignKey(TableEnum.T_RULE_CONDITION, blockIds);
            ruleConditionInfoList = redisManager.hgetAllBatchByForeignKeys(keys, RuleConditionInfo.class);
        } else {
            LambdaQueryWrapper<RuleConditionInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(RuleConditionInfo::getBlockId, blockIds);
            ruleConditionInfoList = this.list(queryWrapper);
        }
        return ruleConditionInfoList;
    }
}
