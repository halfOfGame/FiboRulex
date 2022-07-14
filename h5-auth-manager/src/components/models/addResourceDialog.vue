<template> 
	<div class="user-edit-wrapper">
		<el-dialog title="资源配置" :visible.sync="dialogVisible" width="50%" :before-close="handleClose">
			<el-form ref="myform" :model="form" :rules="rules" label-width="80px" label-position="left">
				<el-row>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="资源编号" prop="code">
								<el-input v-model="form.code" autocomplete="off"></el-input>
							</el-form-item>
						</div>
					</el-col>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="资源名" prop="name">
								<el-input v-model="form.name" autocomplete="off"></el-input>
							</el-form-item>
						</div>
					</el-col>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="icon" prop="icon">
								<el-input v-model="form.icon" autocomplete="off"></el-input>
							</el-form-item>
						</div>
					</el-col>
				</el-row>
				<el-row>
					<el-col :span="12">
						<div class="grid-conten">
							<el-form-item label="资源描述" prop="des">
								<el-input v-model="form.des" autocomplete="off"></el-input>
							</el-form-item>
						</div>
					</el-col>
					<el-col :span="12">
						<div class="grid-conten">
							<el-form-item label="访问路径" prop="url">
								<el-input v-model="form.url" autocomplete="off"></el-input>
							</el-form-item>
						</div>
					</el-col>
				</el-row>

				<el-row>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="菜单排序" prop="sort">
								<el-input v-model="form.sort" autocomplete="off" minlength="1" maxlength="3"></el-input>
							</el-form-item>
						</div>
					</el-col>
				</el-row>
				<el-row>
					<el-col :span="8">
						<div class="grid-conten">
							<el-form-item label="系统分类" prop="sort">
								
								<el-select v-model="form.resourceSystem" placeholder="请选择" @change="gettreeList(parentId)">
									<el-option :label="value.label" :value="value.value" v-for="value in systemList" :key="value.value"></el-option>
								
								</el-select>

							</el-form-item>
						</div>
					</el-col>
				</el-row>

				<el-divider></el-divider>
				<el-row>
					<el-col :span="24">
						<div class="grid-conten">
							<h3 style="margin-bottom:30px">
								父类资源：
							</h3>
						</div>
					</el-col>
				</el-row>

				<el-row>
					<el-col :span="24">
						<div class="grid-conten">
							<el-tree ref="tree" :props="props" node-key="resourceId" :data="systemList.find(x=>x.value==this.form.resourceSystem)?systemList.find(x=>x.value==this.form.resourceSystem).data:[]" :key="num"
								:current-node-key="checkedKeys" :highlight-current="true" @node-click="handleNodeClick">
							</el-tree>
							<div style="padding: 20px;display: flex;justify-content: flex-end;">
								<el-button type="primary" @click="stick" size="mini">置为顶层目录</el-button>
							</div>
						</div>
					</el-col>
				</el-row>

				<el-form-item class="btn-group">
					<el-button type="primary" @click="onSubmit('myform')">确认</el-button>
					<el-button @click="handleClose()">取消</el-button>
				</el-form-item>
			</el-form>
		</el-dialog>
	</div>
</template>
<script>
	import {
		getResourceTreeMenu,
		resourceUpdate,
		resourceSave
	} from '@/api/index.js'
	import mixin from '@/utils/mixin.js'
	export default {
		name: 'resourceDialog',
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
			}
		},
		watch: {
			dataItem(newVal) {
				console.log(newVal)
				
			},
			dialogVisible: function(val, oldVla) {
				if (this.$refs['myform'] != undefined) {
					this.$refs["myform"].resetFields();
				}
				
			}
		},
		created() {
			this.form.resourceSystem =this.systemList[0]?this.systemList[0].value:''
			
			if(this.dataItem.name){
				// this.dataItem = newVal;
				if (JSON.stringify(this.dataItem) !== '{}') {
					this.form = {
						"name": this.dataItem.name,
						"code": this.dataItem.code,
						"url":  this.dataItem.url,
						"parentId":  this.dataItem.parentId,
						"des":  this.dataItem.des,
						"icon":  this.dataItem.icon,
						"sort":  this.dataItem.sort,
						"resourceSystem":  this.dataItem.resourceSystem,
					}
					this.parentId =  this.dataItem.parentId;
					this.checkedKeys =  this.dataItem.parentId;
					console.log(this.dataItem,this.form)
					// this.resourceSystem = this.dataItem.resourceSystem
				}
			}
			
			
			this.gettreeList(this.parentId);
		},
		
		data() {
			let checkSort = (rule, value, callback) => {
				console.log(value)
				if (value === 0) {
					return callback(new Error('超出可选范围（1-100）'));
				}
				if (!value) {
					return callback(new Error('菜单排序不能为空'));
				} else {
					if (value > 0 && value <= 100) {
						callback();
					} else {
						return callback(new Error('超出可选范围（1-100）'));
					}
				}

			};
			return {
				num:0,
				form: {
					name: "",
					code: "",
					url: "",
					parentId: 0,
					des: "",
					icon: "",
					sort: 1,
					resourceSystem: ""
				},
				parentId: 0,
				rules: {
					name: [{
						required: true,
						message: '请输入资源名称',
						trigger: 'blur'
					}],
					code: [{
						required: true,
						message: '请输入资源编号',
						trigger: 'blur'
					}],
					des: [{
						required: true,
						message: '请输入资源描述',
						trigger: 'blur'
					}],
					sort: [{
						validator: checkSort,
						required: true,
						trigger: 'blur'
					}],
					resourceSystem: [{
						required: true,
						message: '请选择系统分类',
						trigger: 'blur'
					}],
				},
				props: {
					label: 'name',
					children: 'children'
				},
				
				// 默认选中节点
				checkedKeys: ""
			}
		},
		methods: {
			stick(){
				this.parentId = 0
				this.form.parentId = 0
				this.checkedKeys = 0
				this.num++
				
				console.log(this.parentId,this.form.parentId,this.checkedKeys)
			},
			handleNodeClick(data) {
				this.form.parentId = data.resourceId;
			},
			getTreeData(data) {
				let treeArr = data;
				let result = []
				for (let i = 0; i < treeArr.length; i++) {
						let item = treeArr[i];
						if (item.parentId === 0) {
							result.push(item);
						}
				}
				this.data2treeDG(treeArr, result);
			},
			data2treeDG(datas, dataArray, type) {
				for (let j = 0; j < dataArray.length; j++) {
					let dataArrayIndex = dataArray[j];
					let childrenArray = [];
					let Id = dataArrayIndex.resourceId;
					if (dataArrayIndex.checked) {
						this.checkedKeys = dataArrayIndex.resourceId
					}
					for (let i = 0; i < datas.length; i++) {
						let data = datas[i];
						let parentId = data.parentId;
						if (parentId == Id) { //判断是否为儿子节点
							childrenArray.push(data);
						}
					}
					dataArrayIndex.children = childrenArray;
					if (childrenArray.length > 0) { //有儿子节点则递归
						this.data2treeDG(datas, childrenArray)
					}
				}
				
				this.systemList.find(x=>x.value==this.form.resourceSystem).data = dataArray;
				// console.log(this.systemList)
			},
			// 新增或编辑
			onSubmit(myForm) {
				let form = this.form;
				this.$refs[myForm].validate((valid) => {
					if (valid) {
						if (JSON.stringify(this.dataItem) == '{}') {
							// 新增
							this.saveResource(form);
						} else {
							// 编辑
							form.resourceId = this.dataItem.resourceId
							this.updateResource(form);
						}
					} else {
						return false;
					}
				});
			},
			async updateResource(form) {
				const data = await resourceUpdate(form)
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
			async saveResource(form) {
				const data = await resourceSave(form)
				if (data.status != "0") {
					if (data.data) {
						this.$message({
							message: '添加成功！',
							type: 'success'
						});
						this.handleClose();
					}
				}
			},
			async gettreeList(id) {
				if(this.systemList.find(x=>x.value==this.form.resourceSystem).data) return
				const data = await getResourceTreeMenu({
					"parentId": id,
					entity:{
						"resourceSystem":this.form.resourceSystem
					}
				})
				if (data.status != "0") {
					this.getTreeData(data.data);
					this.$nextTick(() => {
						if (this.parentId != 0) {
							this.$refs.tree.setCurrentKey(this.checkedKeys)
						}
					})
				}
			},
			handleClose() {
				this.form = {
					"name": "",
					"code": "",
					"url": "",
					"parentId": null,
					"des": "",
					"icon": "",
					"sort": 1,
					"resourceSystem": "EngineX"
				}
				this.systemList.forEach(value=>{
					value.data = null
				})
				
					
				this.$refs["myform"].resetFields();
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

	.user-edit-wrapper .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
		background: #409EFF;
		color: #fff;
	}

	.user-edit-wrapper .is-checked {
		background: #F5F7FA;
	}
</style>
