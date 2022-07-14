<template>
	<div>
		<div style="padding-top: 20px;">
			<el-button type="primary" @click="add">添加队列</el-button>

			<el-select v-model="search.type" placeholder="请选择队列类型" style="margin-left: 20px;">
				<el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value">
				</el-option>
			</el-select>

		</div>

		<el-table :data="list" border style="width: 100%;margin-top: 20px;" v-loading="loading">
			<el-table-column prop="name" label="name" align="center">
			</el-table-column>
			<el-table-column prop="type" label="类型" align="center">
			</el-table-column>
			<el-table-column prop="serverAddrs" label="serverAddrs" align="center">
			</el-table-column>
			<el-table-column prop="groupId" label="groupId" align="center">
			</el-table-column>
			<el-table-column prop="topic" label="topic" align="center">
			</el-table-column>

			<el-table-column prop="createTime" label="创建时间" align="center">
				<template slot-scope="scope">
					{{new Date(scope.row.createTime).format('yyyy-MM-dd hh:mm:ss')}}
				</template>
			</el-table-column>
			<el-table-column prop="" label="操作" align="center">
				<template slot-scope="scope">
					<div style="display: flex;justify-content: center;">

						<el-button type="primary" icon="el-icon-edit" circle size="mini" @click="update(scope.row)">
						</el-button>
						<el-button type="danger" icon="el-icon-delete" circle size="mini"
							@click="deleteMq(scope.row.id)"></el-button>
					</div>
				</template>
			</el-table-column>
		</el-table>

		<div style="display: flex;justify-content: flex-end;padding-top: 20px;padding-right: 20px;">
			<el-pagination background layout="prev, pager, next" :current-page.sync="pageNum" :total="total"
				@current-change="getList">
			</el-pagination>
		</div>





		<el-dialog title="添加/修改" :visible.sync="dialogVisible" v-if="dialogVisible" width="800px" @close="close">
			<div v-loading="dialogLoading">
				<el-form ref="form" :model="form" label-width="120px" :rules="rules">
					<el-form-item label="name" prop="name">
						<el-input v-model="form.name"></el-input>
					</el-form-item>
					<el-form-item label="serverAddrs" prop="serverAddrs">
						<el-input v-model="form.serverAddrs"></el-input>
					</el-form-item>
					<el-form-item label="groupId" prop="groupId">
						<el-input v-model="form.groupId"></el-input>
					</el-form-item>
					<el-form-item label="topic" prop="topic">
						<el-input v-model="form.topic"></el-input>
					</el-form-item>
					<el-form-item label="messageBody" prop="messageBody">
						<div style="overflow: hidden;position: relative;line-height: 16px;">

							<codemirror v-model="form.messageBody" MYname="json" mime="text/javascript"
								:autocomplete="false">
							</codemirror>

						</div>
					</el-form-item>

				</el-form>

				<div style="display: flex;justify-content: flex-end;">
					<el-button @click="dialogVisible = false">取 消</el-button>
					<el-button type="primary" @click="dialogCallBack">确 定</el-button>
				</div>
			</div>
		</el-dialog>

	</div>
</template>

<script>
	import codemirror from '@/components/common/codemirror.vue'
	import {
		getMqSourceList,
		addMqSource,
		updateMqSource,
		MqupdateStatus,
	} from '@/api/index.js'

	const formOr = {
		id: '',
		name: '',
		type: 'kafka',
		serverAddrs: '',
		groupId: '',
		topic: '',
		messageBody: ''
	}
	export default {
		components: {
			codemirror
		},
		data() {
			return {
				list: [],
				pageNum: 1,
				total: 0,
				dialogVisible: false,
				loading: false,
				dialogLoading: false,
				typeList: [{
					label: 'kafka',
					value: 'kafka'
				}],
				codeKey: 0,
				search: {
					type: 'kafka'
				},
				rules: {
					name: [{
						required: true,
						message: '请输入name',
						trigger: 'blur'
					}, ],
					serverAddrs: [{
						required: true,
						message: '请输入serverAddrs',
						trigger: 'blur'
					}, ],
					groupId: [{
						required: true,
						message: '请输入groupId',
						trigger: 'blur'
					}, ],
					topic: [{
						required: true,
						message: '请输入topic',
						trigger: 'blur'
					}, ],
					messageBody: [{
						required: true,
						message: '请输入messageBody',
						trigger: 'blur'
					}, ],
				},
				form: JSON.parse(JSON.stringify(formOr)),
				dialogCallBack: () => {}
			}
		},
		created() {
			this.getList()
		},
		methods: {
			deleteMq(id) {
				this.$confirm('确定删除？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'error'
				}).then(() => {
					MqupdateStatus({
						ids: id,
						status: -1
					}).then(res => {
						if (res.status == '1') {
							this.getList()
						}
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					});
				});
			},
			close() {
				this.form = JSON.parse(JSON.stringify(formOr))
				this.dialogCallBack = () => {}

			},
			update(item) {

				this.form = {
					id: item.id,
					name: item.name,
					type: item.type,
					serverAddrs: item.serverAddrs,
					groupId: item.groupId,
					topic: item.topic,
					messageBody: JSON.stringify(JSON.parse(item.messageBody),null,4),
				}
				this.dialogVisible = true

				this.dialogCallBack = () => {
					if (this.verify()) {
						return
					}
					this.dialogLoading = true
					updateMqSource(this.form).then(res => {
						if (res.status == '1') {
							this.dialogVisible = false
							this.getList()
						}
						this.dialogLoading = false
					})

				}


			},
			add() {
				this.dialogVisible = true
				this.form.type = this.search.type
				this.dialogCallBack = () => {
					if (this.verify()) {
						return
					}
					delete this.form.id
					this.dialogLoading = true
					addMqSource(this.form).then(res => {
						if (res.status == '1') {
							this.dialogVisible = false
							this.getList()
						}
						this.dialogLoading = false
					})




				}
			},
			verify() {
				let is = {
					is: false,
					msg: ''
				}

				if(!this.isJSON(this.form.messageBody)){
					is.is = true
					is.msg = 'messageBody必须为JSON对象'
				}
				if (!this.form.messageBody || this.form.messageBody.trim() === '') {
					is.is = true
					is.msg = '请输入messageBody'
				}
				if (!this.form.topic || this.form.topic.trim() === '') {
					is.is = true
					is.msg = '请输入topic'
				}
				if (!this.form.groupId || this.form.groupId.trim() === '') {
					is.is = true
					is.msg = '请输入groupId'
				}
				if (!this.form.serverAddrs || this.form.serverAddrs.trim() === '') {
					is.is = true
					is.msg = '请输入serverAddrs'
				}
				if (!this.form.type || this.form.type.trim() === '') {
					is.is = true
					is.msg = '请输入队列类型'
				}
				if (!this.form.name || this.form.name.trim() === '') {
					is.is = true
					is.msg = '请输入name'
				}


				if (is.is) {
					this.$message.error(is.msg)
				}
				return is.is
			},
			getList() {
				this.loading = true
				getMqSourceList({
					pageNum: this.pageNum,
					pageSize: 10
				}).then(res => {
					if (res.status == 1) {
						this.list = res.data.list
						this.total = res.data.total
					}
					this.loading = false
				})
			}
		}
	}
</script>

<style>
</style>
