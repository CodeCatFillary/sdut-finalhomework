<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { moduleRoutes } from './router'
import { getCurrentUser, logout } from './utils/auth'

const route = useRoute()
const router = useRouter()

const modules = moduleRoutes.map((item) => ({
  path: item.path,
  key: item.name,
  title: item.meta.title,
  desc: item.meta.desc,
  tag: item.meta.tag,
}))

const currentModule = computed(() => {
  return modules.find((item) => item.path === route.path) || {
    title: route.meta.title || '政务服务管理系统',
    desc: route.meta.desc || '统一政务服务前端页面',
  }
})

const isAuthPage = computed(() => Boolean(route.meta.authPage))
const currentUser = computed(() => getCurrentUser())

function logoutSystem() {
  logout()
  router.replace('/login')
}
</script>

<template>
  <RouterView v-if="isAuthPage" />

  <main v-else class="app-shell">
    <header class="shell-header">
      <div class="shell-title-area">
        <p class="eyebrow">政务服务管理系统前端分支</p>
        <h1>{{ currentModule.title }}</h1>
        <p class="shell-desc">{{ currentModule.desc }}</p>

        <div class="shell-account" v-if="currentUser">
          <div>
            <span>当前账号</span>
            <strong>{{ currentUser.name }} / {{ currentUser.role }}</strong>
            <small>{{ currentUser.department }}  登录时间 {{ currentUser.loginAt }}</small>
          </div>
          <button type="button" @click="logoutSystem">退出登录</button>
        </div>
      </div>

      <nav class="shell-nav" aria-label="系统模块导航">
        <RouterLink
          v-for="module in modules"
          :key="module.key"
          :to="module.path"
          :class="{ active: route.path === module.path }"
        >
          <span>{{ module.tag }}</span>
          <strong>{{ module.title }}</strong>
        </RouterLink>
      </nav>
    </header>

    <section class="app-body">
      <RouterView />
    </section>
  </main>
</template>
