<template>
	<div style="height: 100%;overflow: scroll;">
		<div style="background-color: #fff;border-radius: 10px;padding: 10px;">
			<span style="font-size: 20px;font-weight: bold;">
				计算指标统计:
			</span>
			<div>
				<div v-for="value in from" style="border-top:1px dotted #ddd;margin-top: 20px;" v-loading="value.loading">
					{{value.name}}
					<div style="display: flex;justify-content: flex-end;">

						<el-input v-model="value.searchKey" placeholder="请输入搜索" size="mini"
							style="width: 200px;margin-right: 20px;"></el-input>

						<el-date-picker v-model="value.time" type="daterange" range-separator="至"
							start-placeholder="开始日期" end-placeholder="结束日期" size="mini" style="margin-right: 20px;">
						</el-date-picker>

						<el-button icon="el-icon-search" circle size="mini" @click="getData(value)"></el-button>

					</div>


					<div style="display: flex;flex-wrap: wrap;margin: 10px;">
						<div v-for="item in value.data">
							<div
								style="display: flex;font-size: 14px;border-right: 1px solid #ddd;align-items: center;">
								<div style="width: 200px;text-align: center;">
									<el-button type="text" @click="getlogData(value,item.id)" :disabled="!item.callCount">{{item.fieldCn}}</el-button>
									<!-- <el-button type="text" @click="getlogData(value)" :disabled="!item.callCount">{{item.fieldCn}}</el-button> -->
								</div>
								:
								<div style="width: 150px;text-align: center;" >{{item.callCount}}次</div>
							</div>
						</div>

					</div>



					<div style="display: flex;justify-content: flex-end;padding-right: 20px;padding-top: 20px;">
						<el-pagination @size-change="getData(value)" @current-change="getData(value)"
							:current-page.sync="value.pageNum" :page-sizes="[10,100, 200, 300, 400]"
							:page-size.sync="value.pageSize" layout="sizes, prev, pager, next" :total="value.total">
						</el-pagination>
					</div>

				</div>
			</div>
		</div>

		<el-dialog title="调用日志" :visible.sync="dialogVisible" width="40%" :close-on-click-modal="false"
			@close="resetlogfrom">

			<div v-loading="logfrom.loading">
				<el-table :data="logfrom.data" style="width: 100%">
					<el-table-column prop="duration" label="耗时/ms" align="center">
					</el-table-column>
					<el-table-column prop="createTime" label="调用时间" align="center">
						<template slot-scope="scope">
							{{new Date(scope.row.createTime).format('yyyy-MM-dd hh:mm:ss')}}
						</template>
					</el-table-column>
					<el-table-column prop="fieldValue" label="调用结果" align="center">
					</el-table-column>
					<el-table-column prop="address" label="请求参数" align="center">
						<template slot-scope="scope">
							<el-button type="text" @click="showinputParam(scope.row.inputParam)">查看入参</el-button>
						</template>
					</el-table-column>
				</el-table>
				<div style="display: flex;justify-content: flex-end;padding-right: 20px;">
					<el-pagination layout="prev, pager, next" :current-page.sync="logfrom.pageNum" @current-change="getlogData(logfrom)" :total="logfrom.total">
					</el-pagination>
				</div>

				<el-dialog width="60%" title="查看入参" :visible.sync="innerVisible" append-to-body @close="inputParam=''">
					<div>
						<el-input type="textarea" :rows="20" placeholder="" v-model="inputParam" disabled>
						</el-input>
					</div>

				</el-dialog>



			</div>




			<span slot="footer" class="dialog-footer">
				<el-button type="primary" @click="dialogVisible = false">确 定</el-button>
			</span>
		</el-dialog>






	</div>
</template>

<script>
	import {
		getFieldCallList,
		getFieldCallLogList
	} from '@/api/index.js'
	const logfromOr = {
		"loading":false,
		"data": [],
		"pageSize": 10,
		"pageNum": 1,
		"total": 0,
		time:[],
		fieldType:'',
		fieldCn:'',
		fieldId:0
	}
	export default {
		data() {
			return {
				from: [{
						"loading":false,
						"name": '数据源指标统计:',
						"time": [new Date().getTomorrow(-1), new Date()],
						"fieldType": 2,
						"searchKey": "",
						"data": [],
						"pageSize": 10,
						"pageNum": 1,
						"total": 0
					},
					{
						"loading":false,
						"name": '接口指标统计:',
						"time": [new Date().getTomorrow(-1), new Date()],
						"fieldType": 4,
						"searchKey": "",
						"data": [],
						"pageSize": 10,
						"pageNum": 1,
						"total": 0
					},
					{
						"loading":false,
						"name": '衍生指标统计:',
						"time": [new Date().getTomorrow(-1), new Date()],
						"fieldType": 3,
						"searchKey": "",
						"data": [],
						"pageSize": 10,
						"pageNum": 1,
						"total": 0
					},
				],
				logfrom: Object.assign({}, JSON.parse(JSON.stringify(logfromOr))),
				dialogVisible: false,
				inputParam: '',
				innerVisible: false
			}
		},
		created() {
			this.from.forEach(value => {
				this.getData(value)
			})

		},
		methods: {
			resetlogfrom() {
				this.logfrom = Object.assign({}, JSON.parse(JSON.stringify(logfromOr)))
			},
			getData(param) {
				param.loading = true
				getFieldCallList({
					"entity": {
						"queryTimeStart": new Date(param.time[0]),
						"queryTimeEnd": new Date(param.time[1]),
						"fieldType": param.fieldType,
						"searchKey": param.searchKey
					},
					"pageSize": param.pageSize,
					"pageNum": param.pageNum

				}).then(res => {
					if (res.status == '1') {
						param.total = res.data.total
						param.data = res.data.list
					}
					param.loading = false
				})


			},
			showinputParam(inputParam) {
				// console.log(inputParam)
				this.inputParam = JSON.stringify(JSON.parse(inputParam), null, 4)
				// console.log(this.inputParam)
				this.innerVisible = true
			},
			getlogData(param,id) {
				console.log(param)
				this.dialogVisible = true
				this.logfrom.loading=true
				getFieldCallLogList({
					"entity": {
						"queryTimeStart": new Date(param.time[0]),
						"queryTimeEnd": new Date(param.time[1]),
						"fieldType": param.fieldType,
						"fieldId":id
					},
					"pageSize": this.logfrom.pageSize,
					"pageNum": this.logfrom.pageNum

				}).then(res => {
					if (res.status == '1') {
						this.logfrom.total = res.data.total
						this.logfrom.data = res.data.list
						this.logfrom.time = JSON.parse(JSON.stringify(param.time))
						this.logfrom.fieldType = param.fieldType
						this.logfrom.fieldCn = param.fieldCn
						this.logfrom.fieldId = id



					}
					this.logfrom.loading=false
				})
			}
		}











	}
</script>

<style>
</style>
