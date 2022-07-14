import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
		    path: '/redirect/:path(.*)',
		    component: () => import('../components/page/redirect/index.vue'),
		},
        {
            path: '/',
            redirect: '/dashboard'
        },
		{
		    path: '/login',
		    component: () => import('../components/page/Login.vue'),
		    meta: { title: '登录' }
		},
        {
            path: '/',
            component: () => import('../components/common/Home.vue'),
            meta: { title: '自述文件' },
            children: [
                {
                    path: '/dashboard',
                    component: () => import('../components/page/Dashboard.vue'),
                    meta: { title: '系统首页' }
                },
                {
                    path: '/dataManage',
                    component: () => import('../components/page/Datamanage.vue'),
                    meta: { title: '基础指标' }
                },
				{
				    path: '/staticManage',
				    component: () => import('../components/page/staticManage.vue'),
				    meta: { title: '常量指标' }
				},
				{
                    path: '/SQLmanage',
                    component: () => import('../components/page/SQLManage.vue'),
                    meta: { title: '数据库指标' }
                },{
                    path: '/realTimeManage',
                    component: () => import('../components/page/realTimeManage.vue'),
                    meta: { title: '实时指标' }
                },
				{ 
                    path: '/derivemanage',
                    component: () => import('../components/page/DeriveManage.vue'),
                    meta: { title: '衍生指标' }
                },
				
				{
                    path: '/portManage',
                    component: () => import('@/components/page/portManage.vue'),
                    meta: {title: '接口指标'}
                },
			
				{
                    path: '/DataSource',
                    component: () => import('@/components/page/DataSource.vue'),
                    meta: {title: '数据库源'}
                },
				{
                    path: '/portSource',
                    component: () => import('@/components/page/portSource.vue'),
                    meta: {title: '接口源'}
                },
				{
				    path: '/MqSource',
				    component: () => import('@/components/page/MqSource.vue'),
				    meta: {title: '消息队列源'}
				},
				{
					path: '/FieldStatistics',
					component: () => import('@/components/page/FieldStatistics.vue'),
					meta: {title: '指标统计'}
				},	

                {
                    path: '/404',
                    component: () => import('../components/page/404.vue'),
                    meta: { title: '404' }
                },
                {
                    path: '/403',
                    component: () => import('../components/page/403.vue'),
                    meta: { title: '403' }
                }
            ]
        },
        
        {
            path: '*',
            redirect: '/404'
        }
    ]
});
