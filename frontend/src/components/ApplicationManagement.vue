<script setup>
import { computed, ref } from 'vue'

const selectedApplicationId = ref('ZW202607020001')
const submitForm = ref({
  applicant: '张明',
  phone: '13800000000',
  matter: '营业执照变更登记',
  idNo: '370100199901010011',
  description: '企业经营范围和注册地址需要同步变更',
})
const uploadFiles = ref([
  { id: 'upload-demo-1', name: '变更登记申请书.pdf', size: '1.2MB', status: '待核验' },
  { id: 'upload-demo-2', name: '股东会决议.pdf', size: '836KB', status: '待核验' },
])

const matterOptions = ['户口迁入登记', '社会保障卡申领', '清税证明开具', '营业执照变更登记', '施工许可备案']

const applicationList = ref([
  {
    id: 'ZW202607020001',
    no: 'ZW202607020001',
    applicant: '张明',
    phone: '13800000000',
    matter: '营业执照变更登记',
    category: '市场准入',
    submitTime: '2026-07-02 09:16',
    status: '审核中',
    currentNode: '登记审批科审核',
    description: '企业经营范围和注册地址需要同步变更',
    files: [
      { id: 'file-1', name: '变更登记申请书.pdf', status: '合格', opinion: '内容完整' },
      { id: 'file-2', name: '股东会决议.pdf', status: '合格', opinion: '签章清晰' },
      { id: 'file-3', name: '授权委托书.pdf', status: '合格', opinion: '已核验' },
    ],
    timeline: [
      { node: '申请提交', time: '2026-07-02 09:16', handler: '申请人', status: '已完成', opinion: '申请提交成功' },
      { node: '窗口受理', time: '2026-07-02 09:42', handler: '王晓敏', status: '已完成', opinion: '材料齐全 予以受理' },
      { node: '登记审批科审核', time: '2026-07-02 10:28', handler: '刘志强', status: '办理中', opinion: '正在核对登记事项' },
      { node: '办结送达', time: '待处理', handler: '系统归档', status: '待处理', opinion: '等待上一节点完成' },
    ],
  },
  {
    id: 'ZW202607020016',
    no: 'ZW202607020016',
    applicant: '李娜',
    phone: '13900000000',
    matter: '户口迁入登记',
    category: '户籍',
    submitTime: '2026-07-02 10:40',
    status: '补正待提交',
    currentNode: '材料补正',
    description: '因工作调动申请户口迁入',
    files: [
      { id: 'file-4', name: '居民身份证.pdf', status: '合格', opinion: '身份信息有效' },
      { id: 'file-5', name: '住所证明.pdf', status: '需补正', opinion: '租赁合同缺少出租方签字 请重新上传完整扫描件' },
      { id: 'file-6', name: '劳动合同.pdf', status: '需补正', opinion: '合同页码不完整 缺少末页签字盖章' },
    ],
    timeline: [
      { node: '申请提交', time: '2026-07-02 10:40', handler: '申请人', status: '已完成', opinion: '申请提交成功' },
      { node: '窗口受理', time: '2026-07-02 11:08', handler: '周雪', status: '已完成', opinion: '已受理并进入材料核验' },
      { node: '材料补正', time: '2026-07-02 13:20', handler: '周雪', status: '办理中', opinion: '住所证明和劳动合同需要补正' },
      { node: '户政科审核', time: '待处理', handler: '户政科', status: '待处理', opinion: '等待补正材料重新提交' },
    ],
  },
  {
    id: 'ZW202607010082',
    no: 'ZW202607010082',
    applicant: '山东明远科技有限公司',
    phone: '0531-88000000',
    matter: '清税证明开具',
    category: '税务',
    submitTime: '2026-07-01 15:34',
    status: '已办结',
    currentNode: '办结送达',
    description: '企业注销前申请开具清税证明',
    files: [
      { id: 'file-7', name: '统一社会信用代码证照.pdf', status: '合格', opinion: '证照有效' },
      { id: 'file-8', name: '经办人身份证明.pdf', status: '合格', opinion: '已通过实名核验' },
    ],
    timeline: [
      { node: '申请提交', time: '2026-07-01 15:34', handler: '企业经办人', status: '已完成', opinion: '申请提交成功' },
      { node: '税务受理', time: '2026-07-01 16:10', handler: '赵宁', status: '已完成', opinion: '申报和缴税状态正常' },
      { node: '税务复核', time: '2026-07-02 09:20', handler: '王磊', status: '已完成', opinion: '无欠税记录 准予开具' },
      { node: '办结送达', time: '2026-07-02 09:45', handler: '系统归档', status: '已完成', opinion: '电子证明已生成' },
    ],
  },
  {
    id: 'ZW202607020021',
    no: 'ZW202607020021',
    applicant: '王强',
    phone: '13700000000',
    matter: '社会保障卡申领',
    category: '社保',
    submitTime: '2026-07-02 14:05',
    status: '待受理',
    currentNode: '窗口受理',
    description: '首次申领社会保障卡',
    files: [
      { id: 'file-9', name: '居民身份证.pdf', status: '待核验', opinion: '等待窗口受理' },
      { id: 'file-10', name: '电子证件照片.jpg', status: '待核验', opinion: '等待窗口受理' },
    ],
    timeline: [
      { node: '申请提交', time: '2026-07-02 14:05', handler: '申请人', status: '已完成', opinion: '申请提交成功' },
      { node: '窗口受理', time: '待处理', handler: '人社服务科', status: '待处理', opinion: '等待工作人员受理' },
      { node: '制卡处理', time: '待处理', handler: '制卡中心', status: '待处理', opinion: '等待受理完成' },
      { node: '办结送达', time: '待处理', handler: '系统归档', status: '待处理', opinion: '等待制卡完成' },
    ],
  },
])

const selectedApplication = computed(() => {
  return applicationList.value.find((item) => item.id === selectedApplicationId.value) || applicationList.value[0]
})

const applicationStats = computed(() => {
  return {
    total: applicationList.value.length,
    accepted: applicationList.value.filter((item) => ['审核中', '已办结', '补正待提交'].includes(item.status)).length,
    finished: applicationList.value.filter((item) => item.status === '已办结').length,
    supplement: applicationList.value.filter((item) => item.status === '补正待提交').length,
  }
})

const supplementFiles = computed(() => {
  return selectedApplication.value.files.filter((file) => file.status === '需补正')
})

const currentStepIndex = computed(() => {
  const index = selectedApplication.value.timeline.findIndex((item) => item.status === '办理中' || item.status === '待处理')
  return index < 0 ? selectedApplication.value.timeline.length - 1 : index
})

function chooseApplication(id) {
  selectedApplicationId.value = id
}

function formatFileSize(size) {
  if (size >= 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)}MB`
  return `${Math.max(1, Math.round(size / 1024))}KB`
}

function handleFileUpload(event) {
  const files = Array.from(event.target.files || [])
  uploadFiles.value = files.map((file) => ({
    id: `upload-${file.name}-${Date.now()}`,
    name: file.name,
    size: formatFileSize(file.size),
    status: '待核验',
  }))
}

function submitApplication() {
  if (!submitForm.value.applicant.trim() || !submitForm.value.matter.trim()) return
  const now = new Date()
  const no = `ZW${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(now.getTime()).slice(-4)}`
  const newApplication = {
    id: no,
    no,
    applicant: submitForm.value.applicant,
    phone: submitForm.value.phone,
    matter: submitForm.value.matter,
    category: resolveCategory(submitForm.value.matter),
    submitTime: now.toLocaleString('zh-CN', { hour12: false }),
    status: '待受理',
    currentNode: '窗口受理',
    description: submitForm.value.description,
    files: uploadFiles.value.map((file) => ({
      id: `${file.id}-saved`,
      name: file.name,
      status: '待核验',
      opinion: '等待窗口受理',
    })),
    timeline: [
      { node: '申请提交', time: now.toLocaleString('zh-CN', { hour12: false }), handler: submitForm.value.applicant, status: '已完成', opinion: '申请提交成功' },
      { node: '窗口受理', time: '待处理', handler: '业务窗口', status: '待处理', opinion: '等待工作人员受理' },
      { node: '业务审核', time: '待处理', handler: '审批人员', status: '待处理', opinion: '等待受理完成' },
      { node: '办结送达', time: '待处理', handler: '系统归档', status: '待处理', opinion: '等待审核完成' },
    ],
  }
  applicationList.value.unshift(newApplication)
  selectedApplicationId.value = no
}

function resolveCategory(matter) {
  if (matter.includes('户口')) return '户籍'
  if (matter.includes('社保') || matter.includes('社会保障')) return '社保'
  if (matter.includes('税')) return '税务'
  if (matter.includes('营业执照')) return '市场准入'
  if (matter.includes('施工')) return '工程建设'
  return '其他'
}

function statusClass(status) {
  return {
    pass: status === '已办结' || status === '已完成' || status === '合格',
    normal: status === '审核中' || status === '办理中' || status === '待核验' || status === '待处理',
    patch: status === '补正待提交' || status === '需补正',
    warning: status === '待受理',
  }
}

function markSupplementSubmitted(file) {
  file.status = '待核验'
  file.opinion = '申请人已重新提交补正材料 等待工作人员复核'
  const stillNeedPatch = selectedApplication.value.files.some((item) => item.status === '需补正')
  if (!stillNeedPatch) {
    selectedApplication.value.status = '审核中'
    selectedApplication.value.currentNode = '业务审核'
    const patchNode = selectedApplication.value.timeline.find((item) => item.node === '材料补正')
    if (patchNode) {
      patchNode.status = '已完成'
      patchNode.opinion = '补正材料已重新提交'
    }
  }
}

function acceptApplication() {
  if (selectedApplication.value.status !== '待受理') return
  selectedApplication.value.status = '审核中'
  selectedApplication.value.currentNode = '业务审核'
  selectedApplication.value.files.forEach((file) => {
    if (file.status === '待核验') {
      file.status = '合格'
      file.opinion = '受理通过'
    }
  })
  const acceptNode = selectedApplication.value.timeline.find((item) => item.node === '窗口受理')
  if (acceptNode) {
    acceptNode.time = new Date().toLocaleString('zh-CN', { hour12: false })
    acceptNode.status = '已完成'
    acceptNode.opinion = '材料齐全 予以受理'
  }
  const reviewNode = selectedApplication.value.timeline.find((item) => item.node === '业务审核')
  if (reviewNode) {
    reviewNode.status = '办理中'
    reviewNode.time = new Date().toLocaleString('zh-CN', { hour12: false })
    reviewNode.opinion = '进入业务审核'
  }
}
</script>

<template>
  <section class="admin-page application-management-page">
    <section class="management-hero application-hero">
      <div>
        <p class="eyebrow">办事申请管理模块</p>
        <h2>支持用户在线提交申请 上传材料 跟踪进度并处理材料补正</h2>
        <p>
          页面模拟用户提交政务办理申请 管理端可查看受理 审核 办结状态 并对不合格材料展示退回补正意见
        </p>
      </div>
      <div class="api-card">
        <span>接口预留</span>
        <strong>/api/applications</strong>
        <strong>/api/applications/status</strong>
        <strong>/api/applications/supplements</strong>
      </div>
    </section>

    <section class="overview-grid four-cols">
      <article class="overview-card">
        <span>申请总数</span>
        <strong>{{ applicationStats.total }} 件</strong>
        <small>当前演示数据和新提交申请</small>
      </article>
      <article class="overview-card">
        <span>已受理</span>
        <strong>{{ applicationStats.accepted }} 件</strong>
        <small>进入受理 审核或办结环节</small>
      </article>
      <article class="overview-card">
        <span>已办结</span>
        <strong>{{ applicationStats.finished }} 件</strong>
        <small>审批完成并生成办理结果</small>
      </article>
      <article class="overview-card">
        <span>待补正</span>
        <strong>{{ applicationStats.supplement }} 件</strong>
        <small>材料不合格 等待重新提交</small>
      </article>
    </section>

    <section class="application-layout">
      <section class="panel-card submit-card">
        <div class="panel-heading">
          <p class="eyebrow">在线申请提交</p>
          <h3>申请表单</h3>
        </div>

        <div class="form-grid two-cols">
          <label>
            <span>申请人或单位</span>
            <input v-model="submitForm.applicant" type="text" />
          </label>
          <label>
            <span>联系电话</span>
            <input v-model="submitForm.phone" type="text" />
          </label>
          <label>
            <span>证件号码</span>
            <input v-model="submitForm.idNo" type="text" />
          </label>
          <label>
            <span>办理事项</span>
            <select v-model="submitForm.matter">
              <option v-for="matter in matterOptions" :key="matter">{{ matter }}</option>
            </select>
          </label>
          <label class="full-line">
            <span>申请说明</span>
            <textarea v-model="submitForm.description" rows="3"></textarea>
          </label>
          <label class="full-line upload-box">
            <span>上传相关材料</span>
            <input type="file" multiple @change="handleFileUpload" />
          </label>
        </div>

        <div class="upload-list">
          <article v-for="file in uploadFiles" :key="file.id" class="upload-item">
            <strong>{{ file.name }}</strong>
            <span>{{ file.size }} ｜ {{ file.status }}</span>
          </article>
        </div>

        <button type="button" class="primary-btn full-width-btn" @click="submitApplication">提交申请</button>
      </section>

      <aside class="matter-panel panel-card application-list-panel">
        <div class="panel-heading">
          <p class="eyebrow">申请列表</p>
          <h3>申请状态跟踪</h3>
        </div>
        <button
          v-for="application in applicationList"
          :key="application.id"
          type="button"
          class="matter-item"
          :class="{ active: selectedApplicationId === application.id }"
          @click="chooseApplication(application.id)"
        >
          <span>{{ application.matter }}</span>
          <small>{{ application.no }} ｜ {{ application.applicant }}</small>
          <em>{{ application.status }}</em>
        </button>
      </aside>
    </section>

    <section class="application-detail-layout">
      <section class="panel-card progress-card">
        <div class="panel-heading horizontal">
          <div>
            <p class="eyebrow">办理进度</p>
            <h3>{{ selectedApplication.no }} ｜ {{ selectedApplication.matter }}</h3>
          </div>
          <button type="button" class="primary-line-btn" :disabled="selectedApplication.status !== '待受理'" @click="acceptApplication">
            模拟受理
          </button>
        </div>

        <div class="case-info-grid">
          <article>
            <span>申请人</span>
            <strong>{{ selectedApplication.applicant }}</strong>
          </article>
          <article>
            <span>事项分类</span>
            <strong>{{ selectedApplication.category }}</strong>
          </article>
          <article>
            <span>当前状态</span>
            <strong>{{ selectedApplication.status }}</strong>
          </article>
          <article>
            <span>当前节点</span>
            <strong>{{ selectedApplication.currentNode }}</strong>
          </article>
        </div>

        <div class="progress-track">
          <article
            v-for="(step, index) in selectedApplication.timeline"
            :key="`${selectedApplication.id}-${step.node}`"
            class="progress-step"
            :class="{ current: index === currentStepIndex }"
          >
            <div class="node-index">{{ index + 1 }}</div>
            <div>
              <div class="node-title-line">
                <strong>{{ step.node }}</strong>
                <span class="status-pill" :class="statusClass(step.status)">{{ step.status }}</span>
              </div>
              <p>{{ step.opinion }}</p>
              <small>{{ step.handler }} ｜ {{ step.time }}</small>
            </div>
          </article>
        </div>
      </section>

      <section class="panel-card supplement-card">
        <div class="panel-heading horizontal">
          <div>
            <p class="eyebrow">材料补正管理</p>
            <h3>材料核验结果</h3>
          </div>
          <span class="record-count">{{ supplementFiles.length }} 项需补正</span>
        </div>

        <div class="material-check-list">
          <article v-for="file in selectedApplication.files" :key="file.id" class="material-check-item">
            <div>
              <div class="node-title-line">
                <strong>{{ file.name }}</strong>
                <span class="status-pill" :class="statusClass(file.status)">{{ file.status }}</span>
              </div>
              <p>{{ file.opinion }}</p>
            </div>
            <button
              v-if="file.status === '需补正'"
              type="button"
              class="primary-line-btn"
              @click="markSupplementSubmitted(file)"
            >
              标记已补正
            </button>
          </article>
        </div>
      </section>
    </section>

    <section class="records-card panel-card">
      <div class="panel-heading horizontal">
        <div>
          <p class="eyebrow">申请状态明细</p>
          <h3>实时办理进度列表</h3>
        </div>
        <span class="record-count">{{ applicationList.length }} 条申请</span>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>申请编号</th>
              <th>申请人</th>
              <th>办理事项</th>
              <th>提交时间</th>
              <th>当前节点</th>
              <th>状态</th>
              <th>申请说明</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="application in applicationList" :key="application.id">
              <td>{{ application.no }}</td>
              <td>{{ application.applicant }}</td>
              <td>{{ application.matter }}</td>
              <td>{{ application.submitTime }}</td>
              <td>{{ application.currentNode }}</td>
              <td>
                <span class="status-pill" :class="statusClass(application.status)">{{ application.status }}</span>
              </td>
              <td>{{ application.description }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>
