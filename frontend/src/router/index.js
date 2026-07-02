import { createRouter, createWebHashHistory } from 'vue-router'
import ApplicationManagement from '../components/ApplicationManagement.vue'
import ApprovalFlowManagement from '../components/ApprovalFlowManagement.vue'
import GovernmentMatterManagement from '../components/GovernmentMatterManagement.vue'
import LoginView from '../components/LoginView.vue'
import RegisterView from '../components/RegisterView.vue'
import SpringAiAssistant from '../components/SpringAiAssistant.vue'
import StatisticsAnalytics from '../components/StatisticsAnalytics.vue'
import { isLoggedIn } from '../utils/auth'

export const moduleRoutes = [
  {
    path: '/matter',
    name: 'matter',
    component: GovernmentMatterManagement,
    meta: {
      requiresAuth: true,
      tag: '模块 1',
      title: '政务事项管理',
      desc: '事项录入 分类管理 发布控制 用例规约',
    },
  },
  {
    path: '/application',
    name: 'application',
    component: ApplicationManagement,
    meta: {
      requiresAuth: true,
      tag: '模块 2',
      title: '办事申请管理',
      desc: '在线申请 状态跟踪 材料补正',
    },
  },
  {
    path: '/approval',
    name: 'approval',
    component: ApprovalFlowManagement,
    meta: {
      requiresAuth: true,
      tag: '模块 3',
      title: '审批流程管理',
      desc: '流程配置 多级审批 记录追踪',
    },
  },
  {
    path: '/statistics',
    name: 'statistics',
    component: StatisticsAnalytics,
    meta: {
      requiresAuth: true,
      tag: '模块 4',
      title: '统计分析',
      desc: '办理统计 效率分析 Excel导出',
    },
  },
  {
    path: '/spring-ai',
    name: 'spring-ai',
    component: SpringAiAssistant,
    meta: {
      requiresAuth: true,
      tag: '模块 5',
      title: 'SpringAI 智能问答',
      desc: '自然语言咨询政务事项',
    },
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      redirect: '/matter',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: '账号登录',
        desc: '登录后进入政务服务管理系统',
        guestOnly: true,
        authPage: true,
      },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: {
        title: '账号注册',
        desc: '创建账号后自动进入系统',
        guestOnly: true,
        authPage: true,
      },
    },
    ...moduleRoutes,
    {
      path: '/:pathMatch(.*)*',
      redirect: '/matter',
    },
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  const loggedIn = isLoggedIn()

  if (to.meta.requiresAuth && !loggedIn) {
    return {
      name: 'login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  if (to.meta.guestOnly && loggedIn) {
    return '/matter'
  }

  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '政务服务管理系统'} - 政务服务管理系统`
})

export default router
