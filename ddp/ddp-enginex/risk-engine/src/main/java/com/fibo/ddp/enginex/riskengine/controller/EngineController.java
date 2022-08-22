package com.fibo.ddp.enginex.riskengine.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fibo.ddp.common.model.authx.system.SysUser;
import com.fibo.ddp.common.model.common.ResponseEntityBuilder;
import com.fibo.ddp.common.model.common.ResponseEntityDto;
import com.fibo.ddp.common.model.common.enums.ErrorCodeEnum;
import com.fibo.ddp.common.model.enginex.risk.Engine;
import com.fibo.ddp.common.model.enginex.risk.EngineVersion;
import com.fibo.ddp.common.model.enginex.risk.EngineVersionVo;
import com.fibo.ddp.common.model.enginex.risk.IndexEngineReportVo;
import com.fibo.ddp.common.model.enginex.risk.response.TestResponse;
import com.fibo.ddp.common.service.common.SessionManager;
import com.fibo.ddp.common.service.enginex.risk.EngineService;
import com.fibo.ddp.common.service.enginex.risk.EngineVersionService;
import com.fibo.ddp.common.service.monitor.logger.ArchivesLog;
import com.fibo.ddp.common.service.monitor.logger.LogService;
import com.fibo.ddp.common.utils.constant.OpTypeConst;
import com.fibo.ddp.common.utils.util.CollectionUtil;
import com.fibo.ddp.common.utils.util.DataHelp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  引擎
 */
@Controller("engineControllerV2")
@RequestMapping("/v2/engine")

public class EngineController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EngineService engineService;
    @Autowired
    private EngineVersionService engineVersionService;
    @Autowired
    private LogService loggerService;

    /**
     * 获取引擎列表
     *
     * @param searchString
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "getEngineList")
    @ResponseBody
    public ResponseEntityDto<Object> enginIndex(@RequestParam(required = false) String searchString,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Integer> list = new ArrayList<>();
        SysUser sysUser = SessionManager.getLoginAccount();
        Long organId = sysUser.getOrganId();
        PageHelper.startPage(pageNo, pageSize);
        //查询对应的引擎
        List<Engine> enginelist = engineService.getEngineListNotVersion(organId, searchString, list);
        // 引擎列表添加运行状态字段
//        for (Engine engine : enginelist) {
//            for (EngineVersion engineVersion : engine.getEngineVersionList()) {
//                if (engineVersion.getBootState() == 1) {
//                    engine.setRunState(engineVersion.getBootState());
//                    break;
//                }
//            }
//            engine.setEngineVersionList(null);
//        }
        PageInfo<Engine> pageInfo = new PageInfo<>(enginelist);
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("pager", pageInfo);
        hashMap.put("enginelist", pageInfo.getList());
        return ResponseEntityBuilder.buildNormalResponse(hashMap);
    }

    /**
     * 获取UUID
     *
     * @return
     */
    @RequestMapping("getUUID")
    @ResponseBody
    public ResponseEntityDto<Object> getUUID() {
        return ResponseEntityBuilder.buildNormalResponse(UUID.randomUUID());
    }

    /**
     * 引擎信息数据回显
     *
     * @param id
     * @return
     */
    @RequestMapping("initupdate/{id}")
    @ResponseBody
    public ResponseEntityDto<Engine> initupdate(@PathVariable String id) {
        try {
            Engine engineVo = new Engine();
            engineVo.setId(Long.parseLong(id));
            SysUser sysUser = SessionManager.getLoginAccount();
            engineVo.setOrganId(sysUser.getOrganId());
            engineVo = engineService.getEngineById(engineVo);
            return ResponseEntityBuilder.buildNormalResponse(engineVo);
        } catch (Exception e) {
            logger.error("获取引擎信息失败", e);
            return ResponseEntityBuilder.buildErrorResponse(ErrorCodeEnum.SERVER_ERROR);
        }

    }

    /**
     * 保存及更新引擎信息
     *
     * @param engine
     * @param
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ArchivesLog(operationType = OpTypeConst.UPDATE_ENGINE)
    @ResponseBody
    public ResponseEntityDto<Object> update(@RequestBody Engine engine) {
        SysUser sysUser = SessionManager.getLoginAccount();
        engine.setOrganId(sysUser.getOrganId());
        try {
            if (engine.getId() == null) {
                engine.setCreator(sysUser.getUserId());
                engine.setStatus(1);
                boolean flag = engineService.saveEngine(engine);
            } else {
                int count = engineService.updateEngine(engine);
                engine.setUserId(sysUser.getUserId());
            }
            return ResponseEntityBuilder.buildNormalResponse(1);
        } catch (Exception e) {
            logger.error("保存引擎信息失败", e);
            return ResponseEntityBuilder.buildErrorResponse(ErrorCodeEnum.SERVER_ERROR);
        }
    }

    /**
     * @api {POST} /v2/engine/version/{engineId} 7.01. 根据engineId获取擎下所有版本
     * @apiGroup zzzzz01
     * @apiVersion 2.0.0
     * @apiParam {Number} engineId 引擎id（路径参数）例如211
     * @apiSuccess {String} status 状态: 1成功, 0失败
     * @apiSuccess {JSON} engineVersion 左边的（主版本、选出的）
     * @apiSuccess {JSONArray} subEngineVersionList 右边的（子版本）
     * @apiParamExample {json} 请求示例：
     * {无请求体}
     * @apiSuccessExample {json} 成功返回数据示例：
     * {"status":"1","error":"00000000","msg":null,"data":[{"engineVersion":{"versionId":462,"engineId":223,"version":0,"subVersion":0,"bootState":0,"status":1,"layout":0,"userId":145,"createTime":"Fri Feb 05 10:45:20 CST 2021","latestUser":145,"latestTime":"Fri Feb 05 10:45:20 CST 2021","engineNodeList":null,"engineName":null,"engineDesc":null},"subEngineVersionList":[{"versionId":465,"engineId":223,"version":0,"subVersion":1,"bootState":0,"status":1,"layout":0,"userId":148,"createTime":"Mon Feb 22 13:55:07 CST 2021","latestUser":148,"latestTime":"Mon Feb 22 13:55:07 CST 2021","engineNodeList":null,"engineName":null,"engineDesc":null}]},{"engineVersion":{"versionId":464,"engineId":223,"version":1,"subVersion":0,"bootState":0,"status":1,"layout":0,"userId":145,"createTime":"Fri Feb 05 10:59:48 CST 2021","latestUser":145,"latestTime":"Fri Feb 05 10:59:48 CST 2021","engineNodeList":null,"engineName":null,"engineDesc":null},"subEngineVersionList":[]}]}
     */
    @ResponseBody
    @RequestMapping(value = "/version/{engineId}", method = RequestMethod.POST)
    public ResponseEntityDto<Object> getEngineVersionList(@PathVariable long engineId) {
        // 查出引擎下所有版本
        List<EngineVersion> versions0 = engineVersionService.getEngineVersionListByEngineIdV2(engineId);  // 调用 V2

        // 过滤：status为1的
        List<EngineVersion> versions = new ArrayList<>();
        for (EngineVersion engineVersion : versions0) {
            if (engineVersion.getStatus() == 1) {
                versions.add(engineVersion);
            }
        }

        Map<Integer, EngineVersionVo> map = new TreeMap<>(
                new Comparator<Integer>() {
                    public int compare(Integer obj1, Integer obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        // 如果不是空的
        if (CollectionUtil.isNotNullOrEmpty(versions)) {
            EngineVersionVo engineVersionVo = null;
            int key = 0;
            // 对版本进行排序
            Collections.sort(versions);
            for (EngineVersion engineVersion : versions) {
                key = engineVersion.getVersion();
                // 首先把subversion是0的保存起来
                if (map.containsKey(engineVersion.getVersion())) {
                    if (engineVersion.getSubVersion() != 0) {
                        map.get(engineVersion.getVersion())
                                .getSubEngineVersionList().add(engineVersion);
                    }
                } else {
                    engineVersionVo = new EngineVersionVo();
                    if (engineVersion.getSubVersion() == 0) {
                        engineVersionVo.setEngineVersion(engineVersion);
                        engineVersionVo.setSubEngineVersionList(
                                new ArrayList<EngineVersion>());
                    } else {
                        List<EngineVersion> engineVersions = new ArrayList<>();
                        engineVersions.add(engineVersion);
                        engineVersionVo.setSubEngineVersionList(engineVersions);
                    }
                }
                map.put(key, engineVersionVo);
            }
        } else {
            // TODO 如果是空的,则代表模型创建有逻辑错误
        }

        List<EngineVersionVo> engineVersionVos = new ArrayList<>();
        for (EngineVersionVo v : map.values()) {
            engineVersionVos.add(v);
        }

        return ResponseEntityBuilder.buildNormalResponse(engineVersionVos);
    }

    /**
     * @api {POST} /v2/engine/pageCheck 7.54. pageCheck页面填写
     * @apiGroup zzzzz01
     * @apiVersion 2.0.0
     * @apiParam {String} valueScope xxxxx
     * @apiParam {String} complex xxxxx
     * @apiParam {String} versionId xxxxx
     * @apiSuccess {String} status 状态：1成功，0失败
     * @apiParamExample {json} 请求示例：
     * {无请求体}
     * @apiSuccessExample {json} 成功返回数据示例：
     * {待完善}
     */
    @RequestMapping(value = "/pageCheck", method = RequestMethod.POST)
    @ArchivesLog(operationType = OpTypeConst.FILL_DATA)
    @ResponseBody
    public ResponseEntityDto<Object> pageCheck(@RequestParam String valueScope, @RequestParam String complex, @RequestParam String versionId) {
        Map<String, Object> inputMap = new HashMap<>();
        if (null != valueScope && !"".equals(valueScope)) {
            JSONArray array = JSONArray.parseArray(valueScope);
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = JSONObject.parseObject(array.get(i).toString());
                for (Map.Entry<String, Object> entry : json.entrySet()) {
                    inputMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        inputMap.put("complexfield", complex);
        Map<String, Object> resultMap = engineService.getEngineVersionExecute(inputMap, versionId);
        return ResponseEntityBuilder.buildNormalResponse(resultMap);
    }

    /**
     * @api {POST} /v2/engine/engineTestList 7.55. 引擎批量测试版本列表页
     * @apiGroup zzzzz01
     * @apiVersion 2.0.0
     * @apiParam {Number} engineId engineId
     * @apiParam {Number} [pageNo=1]
     * @apiParam {Number} [pageSize=10]
     * @apiSuccess {String} status 状态：1成功，0失败
     * @apiParamExample {json} 请求示例：
     * {"engineId":229,"pageNo":1,"pageSize":10}
     * @apiSuccessExample {json} 成功返回数据示例：
     * {待完善}
     */
    @ResponseBody
    @RequestMapping(value = "/engineTestList", method = RequestMethod.POST)
    public ResponseEntityDto<Object> engineTestList(@RequestBody HashMap<String, Object> paramMap) {
        Integer pageNo = paramMap.get("pageNo") == null ? 1 : Integer.valueOf(paramMap.get("pageNo").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? 10 : Integer.valueOf(paramMap.get("pageSize").toString());

        Long userId = SessionManager.getLoginAccount().getUserId();
        paramMap.put("userId", userId);

        PageHelper.startPage(pageNo, pageSize);

        List<EngineVersion> engineVersionList = engineVersionService.getEngineVersionByEngineId(paramMap);

        // 对表t_engine_version中的latest_time字段的日期（Thu Mar 16 16:56:52 CST 2017）进行回显处理
        for (Iterator iterator = engineVersionList.iterator(); iterator.hasNext(); ) {
            EngineVersion engineVersion = (EngineVersion) iterator.next();
            String latestTime = engineVersion.getLatestTime();
            if (latestTime != null && !latestTime.equals("")) {
                DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'CST' yyyy", Locale.US);
                try {
                    Date date = sdf2.parse(latestTime);
                    engineVersion.setLatestTime(sdf1.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        PageInfo<EngineVersion> pageInfo = new PageInfo<>(engineVersionList);

        HashMap<String, Object> modelMap = new HashMap<>();

        modelMap.put("pager", pageInfo);
        // modelMap.put("engineVersionList", pageInfo.getList());
        modelMap.put("engineId", paramMap.get("engineId"));
        modelMap.put("searchString", paramMap.get("searchString"));
        return ResponseEntityBuilder.buildNormalResponse(modelMap);
    }



    /**
     * @api {POST} /v2/engine/createSampleData 7.66.10. 批量生成引擎测试样本数据（模板下载）
     * @apiGroup zzzzz01
     * @apiVersion 2.0.0
     * @apiParam {Number} engineId 引擎id
     * @apiParam {Number} versionId versionId
     * @apiParam {Number} nullCtRatio nullCtRatio
     * @apiParam {Number} elseCtRatio elseCtRatio
     * @apiParam {Number} rowCt 样本数
     * @apiSuccess {String} status 状态：1成功，0失败
     * @apiParamExample {json} 请求示例
     * {"engineId":210,"versionId":419,"nullCtRatio":0,"elseCtRatio":100,"rowCt":2}
     * @apiSuccessExample {json} 成功返回数据示例
     * {待完善}
     */
    @RequestMapping(value = "/createSampleData", method = RequestMethod.POST)
    public void getFieldExcelTemplate(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, Object> paramMap) {
        //构造引擎版本
        EngineVersion engineVersion = new EngineVersion();
        Long versionId = Long.valueOf(String.valueOf(paramMap.get("versionId")));
        engineVersion.setVersionId(versionId);
        boolean result = engineService.getFieldExcelTemplate(response, engineVersion);
    }

    @PostMapping(value = "/downloadJsonField")
    @ResponseBody
    public  ResponseEntityDto downloadJsonField(@RequestBody EngineVersion engineVersion){
        String jsonField = engineService.getJsonField(engineVersion);
        return ResponseEntityBuilder.buildNormalResponse(jsonField);
    }

    /**
     * @api {POST} /v2/engine/getIndexInfo 7.66.22. 查询首页统计信息
     * @apiGroup zzzzz01
     * @apiVersion 2.0.0
     * @apiSuccess {String} status 状态：1成功，0失败
     * @apiParamExample {json} 请求示例
     * {}
     * @apiSuccessExample {json} 成功返回数据示例
     * {"status":"1","error":"00000000","msg":null,"data":{"lastLoginTime":1613801892000,"recentDayMap":{"datasets":[],"labels":[]},"logList":[{"userId":7966,"opType":"logout","organName":"rik","opName":"登出","opUserId":1,"opUserName":"超级管理员","organId":1,"method":"logout","requestPath":"http://localhost:8080/Riskmanage/v2/login/logout","requestParam":"}","responseParam":"ResponseEntityDto(super=com.risk.riskmanage.common.model.ResponseEntityDto@8bc7d658, status=1, error=00000000, msg=null, data=null)","ip":"0:0:0:0:0:0:0:1","startTime":1617088732000,"endTime":1617088738000},{"userId":7964,"opType":"logout","organName":"rik","opName":"登出","opUserId":1,"opUserName":"超级管理员","organId":1,"method":"logout","requestPath":"http://localhost:8080/Riskmanage/v2/login/logout","requestParam":"}","responseParam":"ResponseEntityDto(super=com.risk.riskmanage.common.model.ResponseEntityDto@8bc7d658, status=1, error=00000000, msg=null, data=null)","ip":"0:0:0:0:0:0:0:1","startTime":1617088616000,"endTime":1617088624000},{"userId":7731,"opType":"saveOrUpdateMenuRole","organName":"rik","opName":"保存、修改资源树","opUserId":1,"opUserName":"超级管理员","organId":1,"method":"insertRoleMenu","requestPath":"http://47.102.125.25:8080/Riskmanage/v2/sysMenu/insertRoleMenu","requestParam":"}","responseParam":"ResponseEntityDto(super=com.risk.riskmanage.common.model.ResponseEntityDto@8bc7d645, status=1, error=00000000, msg=null, data=24)","ip":"127.0.0.1","startTime":1617073017000,"endTime":1617073017000},{"userId":7730,"opType":"saveOrUpdateMenuRole","organName":"rik","opName":"保存、修改资源树","opUserId":1,"opUserName":"超级管理员","organId":1,"method":"insertRoleMenu","requestPath":"http://47.102.125.25:8080/Riskmanage/v2/sysMenu/insertRoleMenu","requestParam":"}","responseParam":"ResponseEntityDto(super=com.risk.riskmanage.common.model.ResponseEntityDto@8bc7d645, status=1, error=00000000, msg=null, data=24)","ip":"127.0.0.1","startTime":1617072896000,"endTime":1617072896000},{"userId":7729,"opType":"saveOrUpdateMenuRole","organName":"rik","opName":"保存、修改资源树","opUserId":1,"opUserName":"超级管理员","organId":1,"method":"insertRoleMenu","requestPath":"http://47.102.125.25:8080/Riskmanage/v2/sysMenu/insertRoleMenu","requestParam":"}","responseParam":"ResponseEntityDto(super=com.risk.riskmanage.common.model.ResponseEntityDto@8bc7d633, status=1, error=00000000, msg=null, data=6)","ip":"127.0.0.1","startTime":1617072804000,"endTime":1617072804000},{"userId":7728,"opType":"saveOrUpdateMenuRole","organName":"rik","opName":"保存、修改资源树","opUserId":1,"opUserName":"超级管理员","organId":1,"method":"insertRoleMenu","requestPath":"http://47.102.125.25:8080/Riskmanage/v2/sysMenu/insertRoleMenu","requestParam":"}","responseParam":"ResponseEntityDto(super=com.risk.riskmanage.common.model.ResponseEntityDto@8bc7d63f, status=1, error=00000000, msg=null, data=18)","ip":"127.0.0.1","startTime":1617072574000,"endTime":1617072574000}],"engineUseRatio":[],"engineBaseInfo":{"engineNum":1,"engineResultNum":0,"engineNodeNum":2},"recentMonthMap":{"datasets":[],"labels":[]}}}
     */
    @ResponseBody
    @RequestMapping(value = "/getIndexInfo", method = RequestMethod.POST)
    public ResponseEntityDto getIndexInfo(){
        Map<String, Object> map = new HashMap<>();
        SysUser sysUser = SessionManager.getLoginAccount();
        Long organId = sysUser.getOrganId();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("organId", organId);
        // 查询首页引擎基本信息
        Map<String,Object> engineBaseInfo = engineService.getIndexEngineBaseInfo(paramMap);

        // 查询首页引擎使用占比
        List<Map<String,Object>> engineUseRatio = engineService.getIndexEngineUseRatio(paramMap);

        // 查询首页最近7天引擎使用情况
        List<IndexEngineReportVo> recentDayEngineUseInfo = engineService.getIndexRecentDayEngineUseInfo(paramMap);
        Map<String, Object> recentDayMap = parseRecentDayEngineUseInfo(recentDayEngineUseInfo);

        // 查询首页最近6个月引擎使用情况
        List<IndexEngineReportVo> recentMonthEngineUseInfo = engineService.getIndexRecentMonthEngineUseInfo(paramMap);
        Map<String, Object> recentMonthMap = parseRecentMonthEngineUseInfo(recentMonthEngineUseInfo);

        // 查询首页上次登录时间
        List<Date> lastLoginTimeList = loggerService.getLastLoginInfo(sysUser.getUserId());
        Date lastLoginTime = lastLoginTimeList.size() > 1 ? lastLoginTimeList.get(1) : lastLoginTimeList.get(0);

        // 查询首页当天活动日志
        Map<String,Object> param = new HashMap<>();
        param.put("organId", organId);
        param.put("startDate", DataHelp.getDay());
        param.put("endDate", DataHelp.getNowDate());
        PageHelper.startPage(1,6);
        List<com.fibo.ddp.common.model.monitor.logger.Logger> logList = loggerService.getLogList(param);

        map.put("engineBaseInfo", engineBaseInfo);
        map.put("engineUseRatio", engineUseRatio);
        map.put("recentDayMap", recentDayMap);
        map.put("recentMonthMap", recentMonthMap);
        map.put("lastLoginTime", lastLoginTime);
        map.put("logList", logList);
        return ResponseEntityBuilder.buildNormalResponse(map);
    }

    @ResponseBody
    @RequestMapping(value = "/batchTest", method = RequestMethod.POST)
    public ResponseEntityDto batchTest(HttpServletRequest request){
        TestResponse result = result = engineService.batchTest(request);
        if (result==null){
            return ResponseEntityBuilder.buildErrorResponse(ErrorCodeEnum.SERVER_ERROR.getCode(),"执行失败");
        }
        return ResponseEntityBuilder.buildNormalResponse(result);
    }

    private Map<String, Object> parseRecentDayEngineUseInfo(List<IndexEngineReportVo> recentDayEngineUseInfo){
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> datasetList = new ArrayList<>();
        Set<String> dayTimeSet = new TreeSet<>();
        Set<String> engineNameSet = new HashSet<>();
        for(IndexEngineReportVo indexEngineReportVo : recentDayEngineUseInfo){
            dayTimeSet.add(indexEngineReportVo.getDayTime());
            engineNameSet.add(indexEngineReportVo.getEngineName());
        }

        for(String engineName : engineNameSet){
            Map<String, Object> dataMap = new HashMap<>();
            List<Integer> dataList = new ArrayList<>();
            for(String dayTime : dayTimeSet){
                IndexEngineReportVo engineReportVo = null;
                for(IndexEngineReportVo indexEngineReportVo : recentDayEngineUseInfo){
                    if(dayTime.equals(indexEngineReportVo.getDayTime()) && engineName.equals(indexEngineReportVo.getEngineName())){
                        engineReportVo = indexEngineReportVo;
                        break;
                    }
                }
                if(engineReportVo != null){
                    dataList.add(engineReportVo.getUseNum());
                } else {
                    dataList.add(0);
                }
            }
            dataMap.put("label", engineName);
            dataMap.put("data", dataList);
            datasetList.add(dataMap);
        }
        map.put("labels", dayTimeSet);
        map.put("datasets", datasetList);
        return map;
    }

    private Map<String, Object> parseRecentMonthEngineUseInfo(List<IndexEngineReportVo> recentMonthEngineUseInfo){
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> datasetList = new ArrayList<>();
        TreeSet<String> monthTimeSet = new TreeSet<>();
        Set<String> engineNameSet = new HashSet<>();
        for(IndexEngineReportVo indexEngineReportVo : recentMonthEngineUseInfo){
            monthTimeSet.add(indexEngineReportVo.getMonthTime());
            engineNameSet.add(indexEngineReportVo.getEngineName());
        }

        for(String engineName : engineNameSet){
            Map<String, Object> dataMap = new HashMap<>();
            List<Integer> dataList = new ArrayList<>();
            for(String monthTime : monthTimeSet){
                IndexEngineReportVo engineReportVo = null;
                for(IndexEngineReportVo indexEngineReportVo : recentMonthEngineUseInfo){
                    if(monthTime.equals(indexEngineReportVo.getMonthTime()) && engineName.equals(indexEngineReportVo.getEngineName())){
                        engineReportVo = indexEngineReportVo;
                        break;
                    }
                }
                if(engineReportVo != null){
                    dataList.add(engineReportVo.getUseNum());
                } else {
                    dataList.add(0);
                }
            }
            dataMap.put("label", engineName);
            dataMap.put("data", dataList);
            datasetList.add(dataMap);
        }
        map.put("labels", monthTimeSet);
        map.put("datasets", datasetList);
        return map;
    }

}
