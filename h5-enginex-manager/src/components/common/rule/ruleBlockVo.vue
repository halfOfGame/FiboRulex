<template>
    <div>
        <el-button icon="el-icon-plus" type="success" size="mini" circle @click="addRuleBlock()"></el-button>
        <template v-for="(item, index) in ruleBlockVoList ">

            <div style="border: 1px solid  #9cb1c7 ;padding: 10px;margin: 10px;position: relative;">
                <div style="position: absolute;top:10px;right: 10px;z-index: 2;">
                    <el-button icon="el-icon-top" :disabled="!index" type="info" size="mini" circle
                        @click="goTop(index)"></el-button>
                    <el-button icon="el-icon-bottom" type="info" :disabled="index == ruleBlockVoList.length - 1"
                        size="mini" circle @click="goBootom(index)">
                    </el-button>
                    <el-button icon="el-icon-close" type="danger" :disabled="!index" size="mini" circle
                        @click="deleteRuleBlock(index)"></el-button>
                </div>
                <rule :data="item.ruleConditionVo" :ZIndex="1"></rule>

                <div class="rule_outcontent_box">
                    <p>命中输出：</p>
                    <div class="rule_home" style="margin-top: 10px;">
                        <div class="rule_fa">
                            <el-button icon="el-icon-plus" size="mini" circle @click="outAdd(0)" disabled>
                            </el-button>
                            <el-button icon="el-icon-close" size="mini" circle disabled='disabled'
                                style="margin-right: 10px">
                            </el-button>
                        </div>
                        <el-select v-model="item.resultFieldEn" size="mini" filterable placeholder="请选择"
                            style="width: 200px;" clearable>
                            <el-option v-for="item in FieldUser" :key="item.id" :label="item.fieldCn"
                                :value="item.fieldEn">
                            </el-option>
                        </el-select>
                        <p style="margin: 10px;">
                            =
                        </p>
                        <el-select filterable value="是否命中" size="mini" disabled style="width: 255px;">
                        </el-select>
                    </div>

                    <outcontent :outcontent="item.strategyOutputList" size='mini' :ruleOut="true" type="complex_rule"
                        :outType="outTypeSuccess">
                        <div style="display: flex;align-items: center;">
                            <el-select v-model="item.scoreFieldEn" size="mini" filterable placeholder="请选择"
                                style="width: 200px;" clearable>
                                <el-option v-for="item in FieldUser" :key="item.id" :label="item.fieldCn"
                                    :value="item.fieldEn">
                                </el-option>
                            </el-select>
                            <p style="margin: 10px;">=</p>
                            <div>
                                <el-input v-model="item.score" size="mini" maxlength="30" style="width: 255px;">
                                    <template slot="prepend">得分</template>
                                </el-input>
                            </div>
                        </div>
                    </outcontent>
                </div>
                <div class="rule_outcontent_box">
                    <p style="font-size: 12px;">未命中输出:</p>
                    <outcontent :outcontent="item.failOutputList" size='mini' :unone="true" :ruleOut="true"
                        type="complex_rule" :outType="outTypeFail">

                    </outcontent>
                    <div>
                    </div>
                </div>


            </div>

        </template>



    </div>


</template>   
<script>
import outcontent from '@/components/models/outcontent.vue'
import rule from '@/components/models/RuleCont.vue'

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
    components: {
        rule,
        outcontent,
    },

    props: {
        ruleBlockVoList: {
            type: Array,
            default: ()=>[]
        }
    },
    data() {
        return {
            outTypeSuccess: {
                outType: 'success'
            },
            outTypeFail: {
                outType: 'fail'
            },
        }
    },
    methods: {
        addRuleBlock() {
            this.ruleBlockVoList.push(new ruleBlock())
        },
        goTop(index) {
            let obj = this.ruleBlockVoList[index]
            this.ruleBlockVoList.splice(index, 1)
            this.ruleBlockVoList.splice(index - 1, 0, obj)
        },
        goBootom(index) {
            let obj = this.ruleBlockVoList[index]
            this.ruleBlockVoList.splice(index, 1)
            this.ruleBlockVoList.splice(index + 1, 0, obj)
        },
        deleteRuleBlock(index) {
            this.ruleBlockVoList.splice(index, 1)
        },
        outAdd(index) {
            this.outcontent.splice(index, 0, {
                "fieldId": "",
                strategyType: 'base_rule',
                "fieldValue": "",
                variableType: 1
            })
        },
    }



}




</script>