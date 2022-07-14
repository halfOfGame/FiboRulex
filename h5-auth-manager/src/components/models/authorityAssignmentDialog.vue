<template> 
	<div class="user-edit-wrapper">
		<el-dialog title="角色配置" :visible.sync="dialogVisible" width="50%" :before-close="handleClose">
			<el-form ref="myform" :model="form" label-width="80px" label-position="left">
				<el-row>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="角色名">
								<el-input v-model="form.roleName" autocomplete="off" :disabled="disabled"></el-input>
							</el-form-item>
						</div>
					</el-col>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="所属组织">
								<el-select v-model="form.organId" :disabled="disabled">
									<el-option v-for="item in listOrganArr" :key="item.organId" :label="item.name" :value="item.organId"></el-option>
								</el-select>
							</el-form-item>
						</div>
					</el-col>
				</el-row>

				<el-divider></el-divider>
				<el-row>
					<el-col :span="12">
						<div class="grid-conten">
							<h3 style="margin-bottom:30px">
								功能权限：
							</h3>
						</div>
					</el-col>
					<el-col :span="12">
						<div class="grid-conten">
							<!-- <h3 style="margin-bottom:30px">
                                    搜索引擎子权限：
                                </h3> -->
						</div>
					</el-col>
				</el-row>

				<el-row>
					<el-col :span="12">
						 <el-tabs v-model="tabs" >
						    <el-tab-pane :label="value.value" :name="value.value" v-for="value in systemList"></el-tab-pane>
						   
						  </el-tabs>
						
						
						<div class="grid-conten">
							<el-tree ref="tree" :props="props" :data="treeList[tabs]" show-checkbox node-key="resourceId" :default-checked-keys="checkedKeys[tabs]"
							 @check-change="findTreeChange">
							</el-tree>
						</div>
						
						
						
					</el-col>
					<el-col :span="12">
						<div class="grid-conten">
							<!-- <el-tree
                                    ref="engineTree"
                                    :props="props"
                                    :data="engineList"
                                    show-checkbox
                                    node-key="id"
                                    :default-checked-keys="engineCheckedKeys"
                                    @check-change="engineTreeChange"
                                >
                                </el-tree> -->
						</div>
					</el-col>

				</el-row>

				<el-form-item class="btn-group">

					<el-button @click="handleClose()">取消</el-button>
					<el-button type="primary" @click="onSubmit()">确认</el-button>
				</el-form-item>
			</el-form>
		</el-dialog>
	</div>
</template>
<script>
	import {
		getFindtreeList,
		getEngineTree,
		insertRoleMenu,
		insertRoleEngine
	} from '@/api/index.js'
	import mixin from '@/utils/mixin.js'
	export default {
		name: 'authorityAssignmentDialog',
		mixins:[mixin],
		props: { 
			dialogVisible: {
				type: Boolean,
				default: false
			},
			dataItem: {
				type: Object,
				default () {
					return {}
				}
			},
			listOrganArr: {
				type: Array,
				default () {
					return []
				}
			}
		},
		watch: {
			dataItem(newVal) {
				this.dataItem = newVal;
				if (JSON.stringify(newVal) !== '{}') {
					
					this.tabs = this.systemList[0].value
					// console.log(this.systemList[0].value)
					this.systemList.forEach(value=>{
						
						this.$set(this.treeList,value.value,null)
						this.$set(this.checkedKeys,value.value,[])
						
						this.gettreeList(newVal.roleId,value.value);
					})
					// this.getEnginetreeList(newVal.id);
					this.form = {
						roleId: newVal.roleId,
						organId: newVal.organId,
						roleName: newVal.roleName
					}
				}
			}
		},
		data() {
			return {
				form: {
					roleId: '',
					organId: '',
					roleName: ''
				},
				tabs:"",
				disabled: true,
				props: {
					label: 'name',
					children: 'children'
				},
				count: 1,
				treeList: {
					
				},
				engineList: [],
				// 默认选中节点
				checkedKeys: {
					
				},
				engineCheckedKeys: []
			}
		},
		methods: {
			findTreeChange(data, checked, indeterminate) {
				let nodes = this.$refs.tree.getCheckedNodes()
				this.checkedKeys[this.tabs] = nodes.map(item => {
					return item.resourceId
				})

			},
			// 搜索引擎子权限：
			// engineTreeChange(data,checked,indeterminate){
			//     let nodes = this.$refs.engineTree.getCheckedNodes()
			//     this.engineCheckedKeys = nodes.map(item => {
			//         return item.id
			//     })
			// },
			getTreeData(data, type,str) {

				let treeArr = data;
				let result = []
				for (let i = 0; i < treeArr.length; i++) {
					let item = treeArr[i];
					if (item.parentId == 0) {
						result.push(item);
					}
				}
				this.data2treeDG(treeArr, result, type,str);
			},
			data2treeDG(datas, dataArray, type,str) {
				// console.log(datas, dataArray, type)


				for (let j = 0; j < dataArray.length; j++) {
					let dataArrayIndex = dataArray[j];
					let childrenArray = [];
					let Id = dataArrayIndex.resourceId;
					if (dataArrayIndex.checked) {
						(type == 1) ? this.checkedKeys[str].push(dataArrayIndex.resourceId): this.engineCheckedKeys.push(dataArrayIndex.resourceId)
					}
					for (let i = 0; i < datas.length; i++) {
						let data = datas[i];
						let parentId = data.parentId;
						if (parentId == Id) { //判断是否为儿子节点
							childrenArray.push(data);
							if (data.checked) {
								(type == 1) ? this.checkedKeys[str].push(data.resourceId): this.engineCheckedKeys.push(data.resourceId)
							}
						}
					}
					dataArrayIndex.children = childrenArray;
					if (childrenArray.length > 0) { //有儿子节点则递归
						this.data2treeDG(datas, childrenArray)
					}
				}
				if (type == 1) {
					this.treeList[str] = dataArray;
					console.log(this.treeList)
				} else {
					this.engineList = dataArray;
				}
			},
			// 新增或编辑
			onSubmit() {

				console.log(this.checkedKeys)
					
				this.checkedKeys.EngineX.forEach(value => {
					if (this.deepparentId(value,'EngineX')) {
						this.checkedKeys.EngineX.push(this.deepparentId(value,'EngineX'))
					}

				})
				this.checkedKeys.DataX.forEach(value => {
					if (this.deepparentId(value,'DataX')) {
						this.checkedKeys.DataX.push(this.deepparentId(value,'DataX'))
					}
				
				})
				
				let arr = []
				this.systemList.forEach(value=>{
					arr.push(...this.checkedKeys[value.value]) 
				})
				console.log(arr)
				arr = this.unique(arr)
				this.updateInsertRoleMenu({
					roleId: this.dataItem.roleId,
					ids: arr.join(',')
				})
				// 搜索引擎子权限：
				// this.updateInsertRoleEngine({
				//     roleId:this.dataItem.id,
				//     ids:this.engineCheckedKeys.join(',')
				// })
			},
			unique(arr) {
				return Array.from(new Set(arr))
			},
			deepparentId(id,str) {
				let num
				this.treeList[str].forEach(value => {
					if (value.resourceId == id) {
						num = value.parentId
					}
					if (value.children.length > 0) {
						value.children.forEach(item => {
							if (item.resourceId == id) {
								num = item.parentId
							}
						})
					}

				})
				return num



			},
			async updateInsertRoleMenu(form) {
				const data = await insertRoleMenu(form)
				if (data.status != "0") {
					if (data.data) {
						this.$message({
							message: '修改成功！',
							type: 'success'
						});
						this.handleClose();
					}
				}
			},
			async updateInsertRoleEngine(form) {
				const data = await insertRoleEngine(form)
				if (data.status != "0") {
					if (data.data) {
						this.$message({
							message: '修改成功！',
							type: 'success'
						});
						this.handleClose();
					}
				}
			},
			async gettreeList(id,str) {
				if(this.treeList[str]) return
				const data = await getFindtreeList({
					"roleId": id,
					"entity":{
						"resourceSystem":str
					}
				})
				if (data.status != "0") {
					data.data.forEach(value=>{
						if(!value.checked){
							data.data.forEach(item=>{
								if(item.resourceId==value.parentId){
									item.checked = false
								}
							})
						}
					})
					this.getTreeData(data.data, 1,str);
				}
			},
			async getEnginetreeList(id) {
				const data = await getEngineTree({
					"roleId": id,
					
				})
				if (data.status != "0") {

					this.getTreeData(data.data, 2);
				}
			},
			handleClose() {
				this.form = {
					roleId: '',
					organId: '',
					roleName: ''
				}
				this.checkedKeys.EngineX = []
				this.checkedKeys.DataX = []
			
				this.treeList.EngineX=null
				this.treeList.DataX=null
				
				this.tabs = 'EngineX'
				this.$emit('closeEvent');
			}
		}
	}
</script>

<style>
	.el-dialog__body {
		padding: 20px 40px;
	}

	.user-edit-wrapper .el-input {
		width: 80%;
	}

	.user-edit-wrapper .btn-group {
		margin-top: 30px;
	}
</style>
