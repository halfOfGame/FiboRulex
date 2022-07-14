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
export const getIndexInfo = (params) => request.post('Riskmanage/v2/engine/getIndexInfo',params)
// 查询首页统计信息
export const getByKey = (params) => request.post('Riskmanage/dictionary/getByKey',params)
// 查询首页统计信息
export const refreshCache = (params) => request.post('Riskmanage/dictionary/refreshCache',params)


// ================================== 系统管理 ===================================
// ---------------------用户管理
// 获取用户列表
export const getUserList = (params) => request.post('/Riskmanage/v2/sysUser/getUserList',params)
// 创建用户  
export const saveUser = (params) => request.post('/Riskmanage/v2/sysUser/save',params)
// 编辑修改 
export const updateUser = (params) => request.post('/Riskmanage/v2/sysUser/update',params)
// 启用、停用、删除  
export const userUpdateStatus = (params) => request.post('/Riskmanage/v2/sysUser/updateStatus',params)
// 获取组织列表
export const getOrganList = (params) => request.post('/Riskmanage/v2/sysOrganization/getOrganList',params)
// 获取已启用的组织列表
export const getAllValidOrgan = (params) => request.post('/Riskmanage/v2/sysOrganization/getAllValidOrgan',params)
// 获取角色 
export const getAllValidRole = (params) => request.post('/Riskmanage/v2/sysRole/getAllValidRole',params)
// 修改密码 
export const updateUserPassword = (params) => request.post('/Riskmanage/v2/sysUser/updatePassword',params)

//----------------------- 角色管理
// 获取角色列表
export const getRoleList = (params) => request.post('/Riskmanage/v2/sysRole/getRoleList',params)
// 修改批量操作  
export const roleUpdateStatus = (params) => request.post('/Riskmanage/v2/sysRole/updateStatus',params)
// 创建角色
export const saveRole = (params) => request.post('/Riskmanage/v2/sysRole/save',params)
// 修改角色
export const updateRole = (params) => request.post('/Riskmanage/v2/sysRole/update',params)
// 获取资源树  
export const getFindtreeList = (params) => request.post('/Riskmanage/v2/sysMenu/findTreeList',params)
// 获取引擎树  
export const getEngineTree = (params) => request.post('/Riskmanage/v2/sysMenu/getEngineTree',params)
// 权限分配资源树保存 
export const insertRoleMenu = (params) => request.post('/Riskmanage/v2/sysMenu/insertRoleMenu',params)
// 权限分配引擎树保存 
export const insertRoleEngine = (params) => request.post('/Riskmanage/v2/sysMenu/insertRoleEngine',params)
// -----------------------资源管理
// 获取资源列表
export const getRsourceMenuList = (params) => request.post('/Riskmanage/v2/sysMenu/getMenuList',params)
// 删除资源 
export const resourceUpdateStatus = (params) => request.post('/Riskmanage/v2/sysMenu/updateStatus',params)
// 新增/修改资源获取的父节点树 
export const getResourceTreeMenu = (params) => request.post('/Riskmanage/v2/sysMenu/getTreeMenu',params)
// 编辑资源 
export const resourceUpdate = (params) => request.post('/Riskmanage/v2/sysMenu/update',params)
// 新增资源 
export const resourceSave = (params) => request.post('/Riskmanage/v2/sysMenu/save',params)
// -----------------------组织管理
// 创建组织
export const saveOrgan = (params) => request.post('/Riskmanage/v2/sysOrganization/save',params)
// 修改组织  
export const updateOrgan = (params) => request.post('/Riskmanage/v2/sysOrganization/update',params)
// 更新状态  
export const updateStatusOrgan = (params) => request.post('/Riskmanage/v2/sysOrganization/updateStatus',params)




