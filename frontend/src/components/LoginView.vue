<script setup>
import { computed, reactive, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { loginWithPassword } from '../utils/auth'

const route = useRoute()
const router = useRouter()

const form = reactive({
  username: 'admin',
  password: '123456',
})

const loading = ref(false)
const errorMessage = ref('')

const redirectPath = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/matter'
})

function fillAccount(username) {
  form.username = username
  form.password = '123456'
  errorMessage.value = ''
}

async function submitLogin() {
  errorMessage.value = ''

  if (!form.username.trim()) {
    errorMessage.value = '请输入账号'
    return
  }

  if (!form.password) {
    errorMessage.value = '请输入密码'
    return
  }

  loading.value = true

  try {
    loginWithPassword(form.username, form.password)
    await router.replace(redirectPath.value)
  } catch (error) {
    errorMessage.value = error.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card login-card">
      <div class="auth-brand">
        <div class="brand-mark">政务</div>
        <div>
          <p class="eyebrow">政务服务管理系统</p>
          <h1>账号登录</h1>
          <p>登录后才能访问事项管理 申请管理 审批流程 统计分析和智能问答模块</p>
        </div>
      </div>

      <form class="auth-form" @submit.prevent="submitLogin">
        <label>
          <span>账号</span>
          <input v-model="form.username" type="text" autocomplete="username" placeholder="请输入账号" />
        </label>

        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="current-password" placeholder="请输入密码" />
        </label>

        <p v-if="errorMessage" class="auth-error">{{ errorMessage }}</p>

        <button class="primary-action full-width" type="submit" :disabled="loading">
          {{ loading ? '登录中' : '登录系统' }}
        </button>
      </form>

      <div class="demo-account-box">
        <span>测试账号</span>
        <button type="button" @click="fillAccount('admin')">管理员 admin / 123456</button>
        <button type="button" @click="fillAccount('user')">普通用户 user / 123456</button>
      </div>

      <p class="auth-switch">
        没有账号
        <RouterLink to="/register">去注册</RouterLink>
      </p>
    </section>

    <section class="auth-side-card">
      <p class="eyebrow">路由守卫已启用</p>
      <h2>未登录访问业务页面会自动跳转登录页</h2>
      <div class="guard-flow">
        <article>
          <strong>访问受保护路由</strong>
          <span>/matter /application /approval /statistics /spring-ai</span>
        </article>
        <article>
          <strong>校验登录状态</strong>
          <span>读取 localStorage 中的登录用户信息</span>
        </article>
        <article>
          <strong>未登录拦截</strong>
          <span>跳转 /login 并保留 redirect 参数</span>
        </article>
      </div>
    </section>
  </main>
</template>
