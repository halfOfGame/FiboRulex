import Vue from 'vue'
import Vuex from 'vuex'
// import request from '../utils/request.js'


Vue.use(Vuex)
export default new Vuex.Store({
	state: {
		barShrink: false,
		systemList:[]
	},
	mutations: {

		setbarShrink(state, res) {
			state.barShrink = res
		},
		setsystemList(state, res) {
			state.systemList = res
		},

	},
	actions: {},
	modules: {},
	getters: {


	}
})
