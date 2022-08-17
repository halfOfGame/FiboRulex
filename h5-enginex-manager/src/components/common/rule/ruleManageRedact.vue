<style>
.rule_com {
	/* height: 51vh; */
	flex: 1;
	overflow: scroll;
	/* overflow-x: hidden; */
	margin: 0px 0px 0px 10px;
	padding-bottom: 100px;

}

.rule_com::-webkit-scrollbar {
	/* display: none; */
	/* Chrome Safari */

}

.Rule_version_buttom {
	/* width: 100px; */
	margin-left: 20px !important;
}

.rule_outcontent_box {
	border: 1px dotted #00000022;
	position: relative;
	margin-top: 10px;
	padding: 10px 0 10px 10px;
}

.rule_outcontent_box>p {
	font-size: 12px;
	position: absolute;
	top: 4px;
	left: 10px;
}
</style>
<template>

	<div class="dataManageRedact" v-loading="loading">

		<div :class="smallHeader ? 'MR_header MR_headerSmall' : 'MR_header'">
			<div>
				<div>
					<el-button type="primary" icon="el-icon-arrow-left" size="small"  circle @click="mixinClose"></el-button>
				</div>
				<div>
					<span v-if="id === 0">新增规则 :</span>
					<span v-else>编辑规则 :</span>
				</div>
			</div>
			<div>
				<el-button :icon="smallHeader ? 'el-icon-bottom' : 'el-icon-top'" size="small"  circle @click="openHeader">
				</el-button>
				<el-button type="success" icon="el-icon-check" size="small"  circle @click="submit" :disabled="addVersionStatus">
				</el-button>
			</div>
		</div>
		<div :class="smallHeader ? 'MR_input MR_inputSmall' : 'MR_input'">
			<div>
				<p>规则代码: </p>
				<el-input placeholder="请输入规则代码" maxlength="30" size="small" v-model="code" clearable :disabled="addVersionStatus">
				</el-input>
			</div>
			<div>
				<p> 规则名称: </p>
				<el-input placeholder="请输入规则名称" maxlength="20" size="small"  v-model="name" clearable :disabled="addVersionStatus">
				</el-input>
			</div>
		</div>
		<div :class="smallHeader ? 'MR_scope MR_scopeSmall' : 'MR_scope'">
			<div>
				<p>规则描述: </p>
				<el-input placeholder="请输入规则描述" maxlength="300"  size="small" v-model="description" clearable
					:disabled="addVersionStatus"></el-input>
			</div>
		</div>

		<version style="margin:5px;" :id="id" :ruleVersionList="ruleVersionList" :version="version"
			:addVersionStatus="addVersionStatus" :addVersionDialog="addVersionDialog"
			:addVersionLoading="addVersionLoading" @addVersion="addVersion" @addVersionClose="addVersionClose"
			@copyVersion="copyVersion" @delectVersion="delectVersion" @updateVersion="updateVersion"
			@addVersionSure="addVersionSure" @Dialog="addVersionDialog = $event" @addVersionExamine="addVersionExamine"
			@versionChange="versionChange" @StatusChange="addVersionStatus = $event" :exportVersion="true"
			@exportVersion="exportVersion" @importNewVersion="importNewVersion">
		</version>


		<div class="rule_com">
			<ruleBlockVo :ruleBlockVoList="ruleBlockVoList"></ruleBlockVo>
			
		</div>
	</div>
</template>

<script>
import mangeRedactMixin from '@/utils/contminxin/MangeRedactMixin.js'
import '@/assets/css/ManageRedact.css'
import version from '@/components/common/Version.vue'
import outcontent from '@/components/models/outcontent.vue'
import rule from '@/components/models/RuleCont.vue'
import ruleBlockVo from './ruleBlockVo.vue'
class ruledata {
	constructor() {
		this.logical = "&&"
		this.fieldId = null
		this.operator = null
		this.fieldValue = null
		this.conditionType = 1
		this.children = []
		this.loopGroupActions = []
	}
}
class ruleBlock {
	constructor() {
		this.score = '1'
		this.failOutputList = []
		this.strategyOutputList = []
		this.resultFieldEn = ''
		this.ruleConditionVo = new ruledata()
		this.scoreFieldEn = ''
	}
}

export default {
	mixins: [mangeRedactMixin],
	components: {
		rule,
		outcontent,
		version,
		ruleBlockVo
	},
	props: {
		fieldTypeId: {
			type: Number,
			default: 0,
		},
		id: {
			type: Number,
			default: 0
		},
		type: {
			type: Number,
			default: 1
		},
		nameId: {
			type: Number,
			default: 0
		},
		getData: {
			type: Object,
			default() {
				return {}
			}
		}
	},

	data() {
		return {
			tempVersion: {
				versionCode: '',
				description: ''
			},
			addVersionLoading: false,
			addVersionDialog: false,
			version: {},
			addVersionStatus: false,
			ruleVersionList: [],
		
			loading: false,
			valueScope: '',
			priority: 0,
			code: '',
			name: '',
			description: '',
			
			parentId: '',
			ruleBlockVoList: []
		}
	},
	created() {
		this.$store.dispatch('getfielduser')

		if (this.id != 0) {
			this.loading = true
			this.getData.getVersion(this.id).then(res => {
				this.parentId = res.data.parentId
				this.ruleVersionList = res.data.ruleVersionList
				this.version = JSON.parse(JSON.stringify(this.ruleVersionList[0]))
				this.code = res.data.code
				this.name = res.data.name
				this.priority = res.data.priority
				this.description = res.data.description
				this.getRuleCont()
			})
		} else {
			this.ruleBlockVoList = [new ruleBlock()]
		}
	},
	computed: {
		FieldUser() {
			if (this.$store.state.FieldUser) {
				console.log(this.$store.state.FieldUser.data.fieldList)
				return this.$store.state.FieldUser.data.fieldList
			} else {
				return []
			}
		}
	},
	mounted() {

	},
	methods: {
		
		importNewVersion(e) {
			this.addVersionLoading = true
			console.log(e)
			let tempisEmpty = {
				is: false,
				msg: ''
			}
			this.ruleBlockVoListVerify(tempisEmpty, e.data.ruleBlockVoList)
			if (tempisEmpty.is) {
				this.$message.error(tempisEmpty.msg)
				return true
			}

			// return
			let obj = {
				versionCode: e.name.versionCode,
				description: e.name.description,
				ruleId: this.id,
				ruleBlockVoList: e.data.ruleBlockVoList,
			}
			this.getData.addVersion(obj).then(res => {
				if (res.status == '1') {
					this.$message({
						message: '添加版本成功',
						type: 'success'
					});
					this.ruleVersionList = res.data
					this.version = JSON.parse(JSON.stringify(this.ruleVersionList[0]))
					this.addVersionDialog = false
					this.addVersionStatus = false
					this.getRuleCont()
					this.$store.dispatch('reGetRuleList')
				}
				this.addVersionLoading = false
			})
		},
		async exportVersion() {
			const res = await this.getData.getInfo({
				id: this.version.id
			})

			if (res.status != '1') {
				return
			}

			this.mixinSaveJSON(res.data, `${this.name}_${res.data.description}.json`)

		},
		addVersionExamine() {
			if (this.verification()) {
				return
			}
			this.addVersionDialog = true
		},
		addVersionSure(tempVersion) {
			this.addVersionLoading = true
			
			// this.deepTypetransition(tempRuleData)
			let obj = {
				versionCode: tempVersion.versionCode,
				description: tempVersion.description,
				ruleId: this.id,
				
				ruleBlockVoList: this.ruleBlockVoList,
				
			}
			this.getData.addVersion(obj).then(res => {
				if (res.status == '1') {
					this.$message({
						message: '添加版本成功',
						type: 'success'
					});
					this.ruleVersionList = res.data
					this.version = JSON.parse(JSON.stringify(this.ruleVersionList[0]))
					this.addVersionDialog = false
					this.addVersionStatus = false
					this.getRuleCont()
					this.$store.dispatch('reGetRuleList')

				}
				this.addVersionLoading = false
			})
		},
		updateVersion(tempVersion) {
			this.addVersionLoading = true
		
			// this.deepTypetransition(tempRuleData)
			let obj = {
				ruleId: this.id,
				id: this.version.id,
				versionCode: tempVersion.versionCode,
				description: tempVersion.description,

				ruleBlockVoList: this.ruleBlockVoList,

			}
			this.getData.updateVersion(obj).then(res => {
				if (res.status == '1') {
					this.$message({
						message: '版本重命名成功',
						type: 'success'
					});
					this.ruleVersionList = res.data
					this.version = JSON.parse(JSON.stringify(this.ruleVersionList[0]))
					this.addVersionDialog = false
					this.addVersionStatus = false
					this.getRuleCont()
					this.$store.dispatch('reGetRuleList')
				}
				this.addVersionLoading = false
			})
		},
		copyVersion(tempVersion) {
			this.addVersionLoading = true
			let obj = {
				ruleId: this.id,
				id: this.version.id,
				versionCode: tempVersion.versionCode,
				description: tempVersion.description
			}
			this.getData.copyVersion(obj).then(res => {
				if (res.status == '1') {
					this.$message({
						message: '复制版本成功',
						type: 'success'
					});
					this.ruleVersionList = res.data
					this.version = JSON.parse(JSON.stringify(this.ruleVersionList[0]))
					this.addVersionDialog = false
					this.addVersionStatus = false
					this.getRuleCont()
					this.$store.dispatch('reGetRuleList')
				}
				this.addVersionLoading = false
			})
		},
		getRuleCont() {
			this.loading = true
			this.getData.getInfo({
				id: this.version.id
			}).then(res => {
				if (res.status == "1") {
					// this.scoreFieldEn = res.data.scoreFieldEn
					// this.resultFieldEn = res.data.resultFieldEn
					this.getType(res.data.ruleConditionVo)
					this.ruleBlockVoList = res.data.ruleBlockVoList
					// this.ruledata = res.data.ruleConditionVo ? res.data.ruleConditionVo : {
					// 	"logical": "&&",
					// 	"fieldId": null,
					// 	"operator": null,
					// 	"fieldValue": null,
					// 	"conditionType": 1,
					// 	"children": [],
					// }
					// this.outcontent = res.data.strategyOutputList
					// this.failOutputList = res.data.failOutputList
					// this.outcontent.forEach(value => {
					// 	if (!value.variableType) {
					// 		value.variableType = 1
					// 	}
					// })
					// // this.redeepverify(this.ruledata)

					// this.SpecialField.score = res.data.score
				}
				this.loading = false
			})
		},
		versionChange() {
			this.ruleVersionList.forEach(value => {
				if (value.id === this.version.id) {
					this.version = JSON.parse(JSON.stringify(value))
					this.getRuleCont()
				}
			})
		},
		addVersion() {
			this.addVersionStatus = true
			this.tempadd = {
				ruleBlockVoList : this.ruleBlockVoList
			}
			this.reset()
		},
		sureAddVersion() {
			if (this.verification()) {
				return
			}
			this.addVersionDialog = true
		},
		verification() {
			let reg = /[\u4e00-\u9fa5]+/g;
			if (this.code.match(reg) != null) {
				this.$message.error('代码不允许出现中文');
				return true
			}
			if (this.verificationCode(this.code) || this.verificationName(this.name)) {
				return true
			}
			if (this.code.trim() === '') {
				this.$message.error('请填入规则代码，并检查空格');
				return true
			}
			if (this.priority === '') {
				this.$message.error('请选择规则优先级');
				return true
			}
			if (this.name.trim() === '') {
				this.$message.error('请填入规则名称，并检查空格');
				return true
			}
			if (this.description === '') {
				this.$message.error('请填入规则描述，并检查空格');
				return true
			}
			// if (this.resultFieldEn === "") {
			// 	this.$message.error('请选择命中时输出变量');
			// 	return true
			// }
			// if (this.scoreFieldEn == "") {
			// 	this.$message.error('请选择得分时输出变量');
			// 	return true
			// }

			let tempisEmpty = {
				is: false,
				msg: ''
			}
			this.ruleBlockVoListVerify(tempisEmpty, this.ruleBlockVoList)
			if (tempisEmpty.is) {
				this.$message.error(tempisEmpty.msg)
				return true
			}

		},
		addVersionClose() {
			this.addVersionStatus = false
		
			this.ruleBlockVoList = this.tempadd.ruleBlockVoList
		},
		reset() {
		
			this.ruleBlockVoList = [new ruleBlock()]
		},
		delectVersion() {
			this.getData.delectVersion({
				status: -1,
				ids: [this.version.id],
				strategyId: this.id
			}).then(res => {
				if (res.status == "1") {
					this.$message({
						message: '删除成功',
						type: 'success'
					});
					this.ruleVersionList = res.data
					this.version = JSON.parse(JSON.stringify(this.ruleVersionList[0]))
					this.getRuleCont()
				}
			})

		},
		getType(obj) {
			// if (obj.conditionType == 2 && !obj.valueType) {
			// 	obj.valueType = this.mixinGetvalueType(obj.fieldId)
			// }
			// if (obj.children.length > 0) {
			// 	obj.children.forEach(value => {
			// 		this.getType(value)
			// 	})
			// }
		},
		
		submit() {


			this.loading = true
			if (this.verification()) {
				this.loading = false
				return
			}
			

			let obj = {
				"code": this.code.trim(),
				"name": this.name.trim(),
				"priority": this.priority,
				"description": String(this.description).trim(),
				difficulty: 2,
				ruleVersionList: [{
					
					ruleBlockVoList: this.ruleBlockVoList
				}],
			}


			if (this.id == 0) {
				obj.ruleVersionList[0].versionCode = 'V:0'
				obj.ruleVersionList[0].description = '初始版本'
				obj.parentId = this.nameId == 99999999 ? 0 : this.nameId,
					this.getData.setsave(obj).then(res => {
						this.loading = false
						if (res.status === "1") {
							this.$message({
								message: '添加成功',
								type: 'success'
							});
							this.$emit('Ok')
							this.$store.dispatch('reGetRuleList')
						}
					}).catch(err => {
						this.loading = false

					})
			} else {
				obj.id = this.id
				obj.parentId = this.parentId
				obj.ruleVersionList[0].id = this.version.id
				this.getData.updatafield(obj).then(res => {
					this.loading = false
					if (res.status === "1") {
						this.$message({
							message: '修改成功',
							type: 'success'
						});
						// this.$emit('Ok')
						this.$store.dispatch('reGetRuleList')
					}
				}).catch(err => {
					this.loading = false

				})
			}
			console.log(obj)
		},
		change(index) {
			this.ruledata[index].operator = ""
			this.ruledata[index].fieldValue = ""
		},
		
		ruleBlockVoListVerify(tempisEmpty, ruleBlockVoList) {
			ruleBlockVoList.forEach(obj => {
				if (!tempisEmpty.is) {
					this.deepverify(tempisEmpty, obj.ruleConditionVo)

					let is = false
					obj.strategyOutputList.forEach(value => {
						if (value.fieldId === "" || String(value.fieldValue).trim() === "" || value.variableType ===
							"") {
							is = true
							this.verifyFlicker(value)
						}
						if (value.variableType == 3 && (String(value.fieldValue).trim() === "" || JSON.parse(value
							.fieldValue).formula.trim() ===
							'')) {
							is = true
							this.verifyFlicker(value)
						}
					})
					obj.failOutputList.forEach(value => {
						if (value.fieldId === "" || String(value.fieldValue).trim() === "" || value.variableType ===
							"") {
							is = true
							this.verifyFlicker(value)
						}
						if (value.variableType == 3 && (String(value.fieldValue).trim() === "" || JSON.parse(value
							.fieldValue).formula.trim() ===
							'')) {
							is = true
							this.verifyFlicker(value)
						}
					})
					if (is === true) {
						tempisEmpty.msg = '请检查自定义输出部分是否有未填项'
						tempisEmpty.is = true
						return true
					}


					if (isNaN(Number(obj.score)) || String(obj.score).trim() === "") {
						tempisEmpty.msg = '得分只能是数字'
						tempisEmpty.is = true
						return
					}


				}





			})
		},
		deepverify(tempisEmpty, obj) {
			if (tempisEmpty.is) {
				return
			}



			if (obj.conditionType == 0) {
				if (obj.fieldId && obj.fieldValue && obj.operator) { } else {
					tempisEmpty.is = true
					return
				}
			}
			if (obj.children.length === 0 && obj.conditionType != 2) {
				tempisEmpty.is = true
				tempisEmpty.msg = '非规则节点后不允许为空'
				this.verifyFlicker(obj)
				return
			}

			if (obj.conditionType == 5) { //如果是条件组
				if (obj.children.length === 0 && obj.conditionType != 5) {
					tempisEmpty.is = true
					tempisEmpty.msg = '条件组后不允许为空'
					return
				}
				obj.condGroupResultCondition.children[0].fieldValue = obj.condGroupResultCondition.children[0]
					.fieldValue.trim()
				obj.condGroupResultCondition.children[1].fieldValue = obj.condGroupResultCondition.children[1]
					.fieldValue.trim()
				if (obj.condGroupResultCondition.children[0].fieldValue.trim() === "" || obj.condGroupResultCondition
					.children[1].fieldValue
					.trim() ===
					"" || isNaN(Number(obj.condGroupResultCondition.children[0].fieldValue)) || isNaN(Number(obj
						.condGroupResultCondition
						.children[1].fieldValue))) {
					if (obj.condGroupResultCondition.children[0].fieldValue !== 0 && obj.condGroupResultCondition
						.children[1].fieldValue !==
						0) {
						tempisEmpty.is = true
						tempisEmpty.msg = '条件组命中条件只能为数字且不为空与空格'
						this.verifyFlicker(obj,'red1')
						return
					}
				}
				if (obj.condGroupResultCondition.children[0].fieldValue < 0) {
					tempisEmpty.is = true
					tempisEmpty.msg = '条件组命中左边不能小于0'
					this.verifyFlicker(obj,'red1')
					return
				}
				if (obj.condGroupResultCondition.children[1].fieldValue > obj.children.length) {
					tempisEmpty.is = true
					tempisEmpty.msg = '条件组命中右边不能大于条件总个数'
					this.verifyFlicker(obj,'red1')
					return
				}
				if (Number(obj.condGroupResultCondition.children[0].fieldValue) > Number(obj.condGroupResultCondition
					.children[1].fieldValue)) {
					tempisEmpty.is = true
					tempisEmpty.msg = '条件组命中左边不能大于右边'
					this.verifyFlicker(obj,'red1')
					return
				}

			}
			if (obj.conditionType == 3 && !obj.fieldEn) { //如果是for节点 且没有选择 被遍历的指标
				tempisEmpty.is = true
				tempisEmpty.msg = 'for后需要确定循环的数组'
				this.verifyFlicker(obj,'red1')
				return
			}
			if (obj.conditionType == 2) {
				obj.fieldValue = obj.fieldValue.trim()
				if (!obj.fieldEn || !obj.operator || !obj.fieldValue) {
					if (obj.fieldValue !== 0) {
						tempisEmpty.is = true
						tempisEmpty.msg = '规则节点不允许有空值'
						this.verifyFlicker(obj)
						return
					}
				}
			}
			if (obj.conditionType == 3 && obj.loopResultCondition.children.length === 0) { //如果是for节点 且没有子节点
				tempisEmpty.is = true
				tempisEmpty.msg = 'for的输出节点后不允许为空'
				this.verifyFlicker(obj,'red2')
				return
			}
			if (obj.conditionType == 3 && obj.loopResultCondition.children.length != 0) { //如果是for节点 且 有输出节点
				obj.loopResultCondition.children.forEach(value => {
					this.deepverify(tempisEmpty, value)
				})
			}
			if (obj.children.length > 0) { //如果有子节点
				obj.children.forEach(value => {
					this.deepverify(tempisEmpty, value)
				})
			}
			if (obj.loopGroupActions.length > 0) { //如果有输出节点
				
				let is = false
				obj.loopGroupActions.forEach(value => {
					if (!value.actionKey || !value.actionType ) {
						this.verifyFlicker(value)
						is = true
						
					}else{
						
						if (!value.actionValue  &&( value.actionType != 1&&value.actionType != 5)) {
							console.log(value)
							this.verifyFlicker(value)
							is = true
						}
					}
				})
				if (is) {
					tempisEmpty.is = true
					tempisEmpty.msg = '输出变量不允许有空值'
					
					return
				}
			}


		},
		async verifyFlicker(obj,str = 'red') {
			this.$set(obj, str, true)

			let MaxFlicker = 6
			flicker(0)
			function flicker(num) {
				setTimeout(() => {
					obj[str] = !obj[str]
					if (num < MaxFlicker) {
						flicker(num + 1)
					}

				}, 300)
			}

		}
		// deepTypetransition(obj) {
		// 	if (Array.isArray(obj.fieldEn)) {
		// 		obj.fieldEn = obj.fieldEn.join('.')
		// 	}
		// 	if (obj.children.length > 0) {
		// 		obj.children.forEach(value => {
		// 			this.deepTypetransition(value)
		// 		})
		// 	}
		// 	if (obj.loopGroupActions.length > 0) {
		// 		obj.loopGroupActions.forEach(value => {
		// 			if (Array.isArray(value.actionValue)) {
		// 				value.actionValue = value.actionValue.join('.')
		// 			}
		// 		})

		// 	}
		// }
	},
	watch: {

	}
}
</script>
