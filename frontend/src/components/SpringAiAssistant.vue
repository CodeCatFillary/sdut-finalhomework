<script setup>
import { computed, nextTick, ref } from 'vue'

const apiEndpoint = '/api/spring-ai/chat'

const inputMessage = ref('')
const isSending = ref(false)
const selectedScene = ref('事项咨询')
const chatViewport = ref(null)

const scenes = [
  {
    name: '事项咨询',
    title: '办理流程问答',
    desc: '查询事项办理流程 办理时限 受理条件',
    icon: 'flow',
  },
  {
    name: '材料清单',
    title: '所需材料核对',
    desc: '按事项类型生成材料清单和注意事项',
    icon: 'folder',
  },
  {
    name: '进度查询',
    title: '审批进度追踪',
    desc: '说明受理 审核 补正 办结节点含义',
    icon: 'clock',
  },
  {
    name: '补正操作',
    title: '材料补正指导',
    desc: '解释补正原因和重新提交步骤',
    icon: 'repair',
  },
  {
    name: '报表查询',
    title: '统计报表助手',
    desc: '查询办件量 办结率 平均时长和导出方式',
    icon: 'chart',
  },
]

const quickQuestions = [
  '我想办理营业执照变更 需要哪些材料',
  '社保补缴申请一般要经过哪些审批流程',
  '申请状态显示补正待提交是什么意思',
  '怎么查询我的审批进度和办理编号',
  '本月事项办理统计报表在哪里导出',
  '材料上传失败后还能重新提交吗',
]

const serviceCards = [
  { label: '覆盖场景', value: '5类', text: '流程 材料 进度 补正 报表' },
  { label: '响应方式', value: '自然语言', text: '输入问题即可获取结构化解答' },
  { label: '接入接口', value: apiEndpoint, text: '后端 SpringAI 完成后可直接对接' },
]

const messages = ref([
  {
    role: 'assistant',
    time: '09:30',
    content:
      '我是政务智能问答助手 可以咨询办理流程 所需材料 审批进度 材料补正和报表查询 请直接输入问题',
    suggestions: ['查询办理流程', '生成材料清单', '说明补正操作'],
  },
])

const currentScene = computed(() => scenes.find((item) => item.name === selectedScene.value) || scenes[0])

function getTimeText() {
  const now = new Date()
  const hour = `${now.getHours()}`.padStart(2, '0')
  const minute = `${now.getMinutes()}`.padStart(2, '0')
  return `${hour}:${minute}`
}

function normalizeText(text) {
  return text.replace(/\s+/g, '').toLowerCase()
}

function createLocalAnswer(question) {
  const text = normalizeText(question)

  if (text.includes('材料') || text.includes('清单') || text.includes('上传')) {
    return {
      content:
        '办理材料通常包含申请表 身份证明 主体资格证明 业务证明材料 电子附件以及授权委托书 如果是企业事项 还需要营业执照或统一社会信用代码证明 上传时建议使用 PDF 或清晰图片 并保证材料名称和系统要求一致',
      suggestions: ['材料命名有什么要求', '上传失败怎么处理', '材料能否后补'],
    }
  }

  if (text.includes('进度') || text.includes('状态') || text.includes('编号') || text.includes('审批')) {
    return {
      content:
        '审批进度一般分为已提交 已受理 审核中 待补正 已办结 退回等节点 你可以通过申请编号 身份信息或统一社会信用代码查询进度 如果停留在审核中 说明事项仍在部门流转 如果显示待补正 需要先处理补正通知',
      suggestions: ['待补正是什么意思', '已受理后多久办结', '退回后能否重新提交'],
    }
  }

  if (text.includes('补正') || text.includes('退回') || text.includes('重新提交')) {
    return {
      content:
        '补正操作建议按四步处理 第一查看补正通知和原因 第二下载或展开问题清单 第三替换不合格材料并补充说明 第四重新提交等待复核 注意不要重复提交同一份错误材料 否则会再次退回',
      suggestions: ['补正有时间限制吗', '补正材料怎么上传', '补正后进度会重置吗'],
    }
  }

  if (text.includes('报表') || text.includes('统计') || text.includes('导出') || text.includes('excel')) {
    return {
      content:
        '报表查询可以按事项类型 时间范围 办理状态和部门维度筛选 常用指标包含申请数量 受理数量 办结数量 办结率 平均办理时长和超期数量 查询后可导出 Excel 报表 用于统计分析模块归档',
      suggestions: ['怎么筛选本月数据', '报表包含哪些字段', '导出失败怎么处理'],
    }
  }

  if (text.includes('流程') || text.includes('办理') || text.includes('怎么')) {
    return {
      content:
        '标准办理流程为选择政务事项 阅读办事指南 填写申请信息 上传材料 提交申请 窗口受理 部门审核 办结送达 如果事项需要多级审批 系统会按配置的科室级和部门级流程依次流转',
      suggestions: ['办理时限怎么算', '受理条件有哪些', '能否线上办结'],
    }
  }

  return {
    content:
      '这个问题可以继续补充事项名称 申请编号或办理状态 我会按政务事项办理流程 所需材料 审批进度 补正操作或报表查询进行解释 如果后端 SpringAI 接口已启用 页面会优先使用接口返回内容',
    suggestions: ['查询事项办理流程', '查询所需材料', '查询审批进度'],
  }
}

async function scrollToBottom() {
  await nextTick()
  if (chatViewport.value) {
    chatViewport.value.scrollTop = chatViewport.value.scrollHeight
  }
}

async function requestSpringAi(question) {
  const controller = new AbortController()
  const timeout = window.setTimeout(() => controller.abort(), 1800)

  try {
    const response = await fetch(apiEndpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        question,
        scene: selectedScene.value,
      }),
      signal: controller.signal,
    })

    if (!response.ok) {
      throw new Error('接口暂不可用')
    }

    const data = await response.json()
    return {
      content: data.answer || data.content || data.message || '',
      suggestions: data.suggestions || [],
    }
  } finally {
    window.clearTimeout(timeout)
  }
}

async function sendMessage(text) {
  const question = (text || inputMessage.value).trim()
  if (!question || isSending.value) return

  inputMessage.value = ''
  messages.value.push({
    role: 'user',
    time: getTimeText(),
    content: question,
  })
  await scrollToBottom()

  isSending.value = true

  try {
    let answer
    try {
      answer = await requestSpringAi(question)
      if (!answer.content) {
        answer = createLocalAnswer(question)
      }
    } catch (error) {
      answer = createLocalAnswer(question)
    }

    messages.value.push({
      role: 'assistant',
      time: getTimeText(),
      content: answer.content,
      suggestions: answer.suggestions,
    })
  } finally {
    isSending.value = false
    await scrollToBottom()
  }
}

function chooseScene(sceneName) {
  selectedScene.value = sceneName
}
</script>

<template>
  <section class="gov-ai-page">
    <aside class="sidebar">
      <div class="brand-card">
        <div class="brand-mark">AI</div>
        <div>
          <p class="eyebrow">政务服务管理系统</p>
          <h1>SpringAI 智能问答</h1>
        </div>
      </div>

      <nav class="module-list" aria-label="智能问答场景">
        <button
          v-for="scene in scenes"
          :key="scene.name"
          type="button"
          class="module-item"
          :class="{ active: selectedScene === scene.name }"
          @click="chooseScene(scene.name)"
        >
          <span class="module-icon" :data-icon="scene.icon"></span>
          <span>
            <strong>{{ scene.title }}</strong>
            <small>{{ scene.desc }}</small>
          </span>
        </button>
      </nav>

      <section class="side-panel">
        <p class="panel-title">页面定位</p>
        <p>
          面向办事用户和工作人员 提供政务事项办理咨询 材料核对 进度解释 补正指导和统计报表查询入口
        </p>
      </section>
    </aside>

    <section class="workspace">
      <header class="topbar">
        <div>
          <p class="eyebrow">当前场景 {{ currentScene.name }}</p>
          <h2>{{ currentScene.title }}</h2>
        </div>
        <div class="topbar-actions">
          <span class="status-dot"></span>
          <span>AI 服务在线</span>
        </div>
      </header>

      <section class="hero-grid">
        <div class="hero-copy">
          <p class="eyebrow">自然语言政务咨询</p>
          <h3>一句话查询流程 材料 进度 补正和报表</h3>
          <p>
            页面已预留 SpringAI 接口请求 如果接口未完成 会使用前端兜底知识库返回演示答案 方便前端独立验收
          </p>
          <div class="hero-actions">
            <button type="button" @click="sendMessage('我想办理营业执照变更 需要哪些材料')">
              试问材料清单
            </button>
            <button type="button" class="ghost" @click="sendMessage('申请状态显示补正待提交是什么意思')">
              试问补正状态
            </button>
          </div>
        </div>

        <div class="metrics-card">
          <div v-for="card in serviceCards" :key="card.label" class="metric-item">
            <span>{{ card.label }}</span>
            <strong>{{ card.value }}</strong>
            <small>{{ card.text }}</small>
          </div>
        </div>
      </section>

      <section class="content-grid">
        <section class="chat-card">
          <div class="chat-header">
            <div>
              <p class="eyebrow">智能咨询问答</p>
              <h3>政务 AI 助手</h3>
            </div>
            <span class="chat-badge">SpringAI</span>
          </div>

          <div ref="chatViewport" class="chat-window">
            <article
              v-for="(message, index) in messages"
              :key="`${message.role}-${index}`"
              class="message-row"
              :class="message.role"
            >
              <div class="avatar">{{ message.role === 'assistant' ? 'AI' : '我' }}</div>
              <div class="message-body">
                <div class="message-meta">
                  <span>{{ message.role === 'assistant' ? '智能助手' : '用户' }}</span>
                  <time>{{ message.time }}</time>
                </div>
                <p>{{ message.content }}</p>
                <div v-if="message.suggestions?.length" class="suggestion-line">
                  <button
                    v-for="suggestion in message.suggestions"
                    :key="suggestion"
                    type="button"
                    @click="sendMessage(suggestion)"
                  >
                    {{ suggestion }}
                  </button>
                </div>
              </div>
            </article>

            <div v-if="isSending" class="message-row assistant loading-row">
              <div class="avatar">AI</div>
              <div class="message-body">
                <div class="typing">
                  <i></i>
                  <i></i>
                  <i></i>
                </div>
              </div>
            </div>
          </div>

          <form class="chat-input" @submit.prevent="sendMessage()">
            <textarea
              v-model="inputMessage"
              rows="3"
              placeholder="请输入咨询内容 例如 申请状态显示补正待提交是什么意思"
              @keydown.enter.exact.prevent="sendMessage()"
            ></textarea>
            <button type="submit" :disabled="isSending || !inputMessage.trim()">发送</button>
          </form>
        </section>

        <aside class="assistant-panel">
          <section class="quick-card">
            <div class="section-title">
              <p class="eyebrow">快速提问</p>
              <h3>常见咨询</h3>
            </div>
            <button
              v-for="question in quickQuestions"
              :key="question"
              type="button"
              @click="sendMessage(question)"
            >
              {{ question }}
            </button>
          </section>

          <section class="flow-card">
            <div class="section-title">
              <p class="eyebrow">回答结构</p>
              <h3>AI 输出格式</h3>
            </div>
            <ol>
              <li>识别咨询事项和办理场景</li>
              <li>匹配流程 材料 进度或报表知识</li>
              <li>输出步骤化回答和下一步建议</li>
              <li>必要时提示补充申请编号或事项名称</li>
            </ol>
          </section>
        </aside>
      </section>
    </section>
  </section>
</template>
