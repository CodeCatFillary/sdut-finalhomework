<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { registerAccount } from '../utils/auth'

const router = useRouter()

const form = reactive({
  name: '',
  username: '',
  password: '',
  confirmPassword: '',
  role: '普通用户',
  department: '个人办事端',
  phone: '',
})

const roles = ['普通用户', '窗口工作人员', '科室审批员', '部门管理员', '系统管理员']
const loading = ref(false)
const errorMessage = ref('')

function validateForm() {
  if (!form.name.trim()) {
    return '请输入姓名'
  }

  if (!form.username.trim()) {
    return '请输入账号'
  }

  if (form.username.trim().length < 4) {
    return '账号长度不能少于 4 位'
  }

  if (!form.password) {
    return '请输入密码'
  }

  if (form.password.length < 6) {
    return '密码长度不能少于 6 位'
  }

  if (form.password !== form.confirmPassword) {
    return '两次输入的密码不一致'
  }

  if (!form.department.trim()) {
    return '请输入所属部门'
  }

  if (!form.phone.trim()) {
    return '请输入联系电话'
  }

  return ''
}

async function submitRegister() {
  errorMessage.value = validateForm()

  if (errorMessage.value) {
    return
  }

  loading.value = true

  try {
    registerAccount(form)
    await router.replace('/matter')
  } catch (error) {
    errorMessage.value = error.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card register-card">
      <div class="auth-brand compact">
        <div class="brand-mark">政务</div>
        <div>
          <p class="eyebrow">政务服务管理系统</p>
          <h1>账号注册</h1>
          <p>注册成功后自动登录并进入系统首页</p>
        </div>
      </div>

      <form class="auth-form register-form" @submit.prevent="submitRegister">
        <label>
          <span>姓名</span>
          <input v-model="form.name" type="text" autocomplete="name" placeholder="请输入姓名" />
        </label>

        <label>
          <span>账号</span>
          <input v-model="form.username" type="text" autocomplete="username" placeholder="至少 4 位" />
        </label>

        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="new-password" placeholder="至少 6 位" />
        </label>

        <label>
          <span>确认密码</span>
          <input v-model="form.confirmPassword" type="password" autocomplete="new-password" placeholder="再次输入密码" />
        </label>

        <label>
          <span>角色</span>
          <select v-model="form.role">
            <option v-for="role in roles" :key="role" :value="role">{{ role }}</option>
          </select>
        </label>

        <label>
          <span>所属部门</span>
          <input v-model="form.department" type="text" placeholder="请输入部门或办事端名称" />
        </label>

        <label class="full-line">
          <span>联系电话</span>
          <input v-model="form.phone" type="tel" autocomplete="tel" placeholder="请输入联系电话" />
        </label>

        <p v-if="errorMessage" class="auth-error full-line">{{ errorMessage }}</p>

        <button class="primary-action full-width full-line" type="submit" :disabled="loading">
          {{ loading ? '注册中' : '注册并进入系统' }}
        </button>
      </form>

      <p class="auth-switch">
        已有账号
        <RouterLink to="/login">返回登录</RouterLink>
      </p>
    </section>

    <section class="auth-side-card">
      <p class="eyebrow">账号信息说明</p>
      <h2>当前为前端演示登录态</h2>
      <div class="guard-flow">
        <article>
          <strong>注册数据</strong>
          <span>保存到浏览器 localStorage 便于前端演示</span>
        </article>
        <article>
          <strong>后端接入</strong>
          <span>后续可替换为 POST /api/auth/register</span>
        </article>
        <article>
          <strong>权限扩展</strong>
          <span>可按角色控制模块菜单和按钮权限</span>
        </article>
      </div>
    </section>
  </main>
</template>
