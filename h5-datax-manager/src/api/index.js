import request from '../utils/request';
// console.log(request)

// export const getV = (params) => request.get(`${window.location.origin}/index.html?time=${new Date().getTime()}`)



export const fetchData = (params) => request.get('./table.json',{params})
// 登录接口
export const getLogin = (params) => request.post('Riskmanage/v2/login/login',params)
// 登出接口
export const getLogout = (params) => request.post('Riskmanage/v2/login/logout',params)
// 查询菜单权限接口
export const getMenus = (params) => request.post('Riskmanage/v2/sysMenu/getMenus',params)
// 查询首页统计信息
export const getIndexInfo = (params) => request.post('Riskmanage/DataX/home/getIndexInfo',params)
// 获取调用最多的10条
export const getFieldCallCountList = (params) => request.post('Riskmanage/DataX/statistics/getFieldCallCountList',params)



// 数据源 数据库列表
export const getDataSourcelist = (params) => request.post('/Riskmanage/datasource/getDataSourceList',params)
// 数据源 修改以及创建数据库
export const setDataSource = (params) => request.post('/Riskmanage/datasource/save',params)
// 数据源 删除
export const deleteDataSource = (id) => request.delete('/Riskmanage/datasource/'+id)
// 数据源 修改数据源
export const updataDataSource = (params) => request.post('/Riskmanage/datasource/update',params)


// 指标管理 listTree 获取
export const getfieldListTree = (params) => request.post('/Riskmanage/v2/datamanage/field/newListTree',params)

// 指标管理 指标列表 获取
export const getfieldList = (params) => request.post('/Riskmanage/v2/datamanage/field/list',params)

// 指标管理 指标列表 获取
export const addfieldList = (params) => request.post('/Riskmanage/v2/datamanage/field/addTree',params)
// 指标管理 更新类型 名称
export const updatafieldList = (params) => request.post('/Riskmanage/v2/datamanage/field/updateTree',params)
// 指标管理 更新类型 名称
export const getFieldUser = (params) => request.post('/Riskmanage/v2/datamanage/field/findFieldByUser',params)

// 保存 增加属性
export const getfieldsave = (params) => request.post('/Riskmanage/v2/datamanage/field/save',params)
// 编辑保存
export const updatafield = (params) => request.post('/Riskmanage/v2/datamanage/field/update',params)
// 启用等
export const fieldusing = (params) => request.post('/Riskmanage/v2/datamanage/field/updateStatus',params)
// 指标导入模板下载
export const fielddownTemplate = (params) => request.post('/Riskmanage/v2/datamanage/field/downTemplate',params)
// 指标批量模板上传
export const fieldupdata = (params) => request.post('/Riskmanage/v2/datamanage/field/upload',params)
// 获取指标管理
export const getfieldInfo = (id,params) => request.post('/Riskmanage/v2/datamanage/field/getFieldInfo/'+id,{params})

// 指标文件夹移动
export const updateFieldFolder = (params) => request.post('/Riskmanage/v2/datamanage/field/updateFieldFolder',params)


// =====================接口管理==============================
// 获取list
export const getInterfaceList = (params) => request.post('/Riskmanage/v3/interface/getInterfaceList',params)
// 新增list
export const addInterface = (params) => request.post('/Riskmanage/v3/interface/addInterface',params)
// 修改list
export const updateInterface = (params) => request.post('/Riskmanage/v3/interface/updateInterface',params)
// 删除list
export const deleteInterface = (params) => request.post('/Riskmanage/v3/interface/deleteInterface',params)
// 测试接口
export const getHttpResponse = (params) => request.post('/Riskmanage/v3/interface/getHttpResponse',params)

// =====================消息队列管理==============================
// 获取list
export const getMqSourceList = (params) => request.post('/Riskmanage/mqSource/getMqSourceList',params)
// 新增list
export const addMqSource = (params) => request.post('/Riskmanage/mqSource/addMqSource',params)
// 修改list
export const updateMqSource = (params) => request.post('/Riskmanage/mqSource/updateMqSource',params)
// 删除list
export const MqupdateStatus = (params) => request.post('/Riskmanage/mqSource/updateStatus',params)






// ======================接口管理 ==============================

export const getengineSummaryList = (params) => request.post('/Riskmanage/v3/1',params)


// =====================指标统计========================
// 获取指标调用次数
export const getFieldCallList = (params) => request.post('/Riskmanage/DataX/statistics/getFieldCallList',params)

// 获取指标日志
export const getFieldCallLogList = (params) => request.post('/Riskmanage/DataX/statistics/getFieldCallLogList',params)








export const getV = (params) => request.post('/list/product',params)



