<script setup>
import { computed, ref } from 'vue'

const selectedCategory = ref('全部分类')
const selectedMatterId = ref('household-register')
const newMaterialName = ref('')
const newFlowStepName = ref('')

const matterList = ref([
  {
    id: 'household-register',
    name: '户口迁入登记',
    category: '户籍',
    department: '公安户政窗口',
    limit: '5个工作日',
    status: '已上线',
    publishChannel: '政务服务平台 移动端大厅',
    serviceObject: '自然人',
    updateTime: '2026-07-02 09:30',
    conditions: '申请人已取得合法稳定住所或符合亲属投靠政策',
    description: '用于维护户口迁入登记事项的名称 分类 办理流程 材料目录 办理时限和发布状态',
    flow: [
      { id: 'household-flow-1', name: '在线填报申请信息' },
      { id: 'household-flow-2', name: '窗口受理并核验材料' },
      { id: 'household-flow-3', name: '户政科审核迁入条件' },
      { id: 'household-flow-4', name: '办结并同步户籍结果' },
    ],
    materials: [
      { id: 'household-material-1', name: '居民身份证原件及复印件', required: true },
      { id: 'household-material-2', name: '户口簿或集体户证明', required: true },
      { id: 'household-material-3', name: '合法稳定住所证明', required: true },
      { id: 'household-material-4', name: '亲属关系证明', required: false },
    ],
  },
  {
    id: 'social-card',
    name: '社会保障卡申领',
    category: '社保',
    department: '人社服务科',
    limit: '7个工作日',
    status: '已上线',
    publishChannel: '政务服务平台 自助终端',
    serviceObject: '自然人',
    updateTime: '2026-07-01 16:12',
    conditions: '申请人已完成实名登记且未重复申领有效社会保障卡',
    description: '提供社保卡首次申领 信息核验 制卡进度查询和结果领取事项配置',
    flow: [
      { id: 'social-card-flow-1', name: '提交身份信息和照片' },
      { id: 'social-card-flow-2', name: '人社部门受理核验' },
      { id: 'social-card-flow-3', name: '银行制卡并回传状态' },
      { id: 'social-card-flow-4', name: '发卡完成并短信通知' },
    ],
    materials: [
      { id: 'social-card-material-1', name: '居民身份证', required: true },
      { id: 'social-card-material-2', name: '电子证件照片', required: true },
      { id: 'social-card-material-3', name: '监护人身份证明', required: false },
    ],
  },
  {
    id: 'tax-clearance',
    name: '清税证明开具',
    category: '税务',
    department: '税务服务厅',
    limit: '3个工作日',
    status: '草稿',
    publishChannel: '待发布',
    serviceObject: '企业法人',
    updateTime: '2026-06-30 11:45',
    conditions: '纳税人已完成申报并结清应纳税费 滞纳金和罚款',
    description: '用于税务类事项上线前的表单 材料 流程 时限和发布范围维护',
    flow: [
      { id: 'tax-clearance-flow-1', name: '企业提交清税申请' },
      { id: 'tax-clearance-flow-2', name: '系统核验申报和缴税状态' },
      { id: 'tax-clearance-flow-3', name: '税务人员复核异常信息' },
      { id: 'tax-clearance-flow-4', name: '生成清税证明' },
    ],
    materials: [
      { id: 'tax-clearance-material-1', name: '统一社会信用代码证照', required: true },
      { id: 'tax-clearance-material-2', name: '经办人身份证明', required: true },
      { id: 'tax-clearance-material-3', name: '授权委托书', required: false },
    ],
  },
  {
    id: 'business-license',
    name: '营业执照变更登记',
    category: '市场准入',
    department: '市场监管科',
    limit: '2个工作日',
    status: '已上线',
    publishChannel: '政务服务平台 企业服务专区',
    serviceObject: '企业法人',
    updateTime: '2026-07-02 08:50',
    conditions: '企业主体状态正常 变更事项符合法定登记条件',
    description: '维护企业名称 住所 经营范围 法定代表人等变更事项的政务服务配置',
    flow: [
      { id: 'business-license-flow-1', name: '企业在线申报变更信息' },
      { id: 'business-license-flow-2', name: '市场监管科受理材料' },
      { id: 'business-license-flow-3', name: '登记审批科审核' },
      { id: 'business-license-flow-4', name: '核发电子营业执照' },
    ],
    materials: [
      { id: 'business-license-material-1', name: '变更登记申请书', required: true },
      { id: 'business-license-material-2', name: '股东会决议或决定文件', required: true },
      { id: 'business-license-material-3', name: '修改后的章程或章程修正案', required: true },
      { id: 'business-license-material-4', name: '经办人授权委托书', required: false },
    ],
  },
])

const useCaseSpecs = [
  {
    name: '事项录入管理',
    actor: '管理员',
    goal: '录入政务事项名称 办理流程 所需材料 办理时限和受理条件',
    precondition: '管理员已登录后台并拥有事项维护权限',
    mainFlow: ['进入政务事项管理页面', '新增或选择事项', '维护事项基本信息', '配置办理流程和材料清单', '保存草稿'],
    alternative: '必填字段缺失时页面提示补全 不允许发布到平台',
    postcondition: '系统保存事项配置并进入草稿或待发布状态',
  },
  {
    name: '事项分类管理',
    actor: '管理员',
    goal: '按照户籍 社保 税务 市场准入等业务类型归档事项',
    precondition: '系统已存在事项分类字典或允许管理员手动维护分类',
    mainFlow: ['选择事项分类', '查看分类下事项', '调整事项所属类型', '刷新分类统计数据'],
    alternative: '分类下仍有上线事项时禁止直接删除分类',
    postcondition: '事项按照业务类型完成归类 便于用户检索和平台统计',
  },
  {
    name: '事项发布管理',
    actor: '管理员',
    goal: '控制事项上线 下线和统一发布到政务服务平台',
    precondition: '事项基础信息 流程和材料清单已通过完整性校验',
    mainFlow: ['选择待发布事项', '检查发布渠道和服务对象', '点击上线', '同步到政务服务平台'],
    alternative: '事项需要暂停办理时执行下线操作 用户端不再展示该事项',
    postcondition: '事项发布状态更新 平台端按照最新配置展示',
  },
]

const categories = computed(() => ['全部分类', ...new Set(matterList.value.map((item) => item.category))])

const filteredMatters = computed(() => {
  if (selectedCategory.value === '全部分类') return matterList.value
  return matterList.value.filter((item) => item.category === selectedCategory.value)
})

const selectedMatter = computed(() => {
  return matterList.value.find((item) => item.id === selectedMatterId.value) || matterList.value[0]
})

const matterStats = computed(() => {
  const onlineCount = matterList.value.filter((item) => item.status === '已上线').length
  const draftCount = matterList.value.filter((item) => item.status !== '已上线').length
  return {
    total: matterList.value.length,
    categoryCount: categories.value.length - 1,
    onlineCount,
    draftCount,
  }
})


function categoryCount(category) {
  if (category === '全部分类') return matterList.value.length
  return matterList.value.filter((item) => item.category === category).length
}

function chooseCategory(category) {
  selectedCategory.value = category
  const first = filteredMatters.value[0]
  if (first) selectedMatterId.value = first.id
}

function chooseMatter(id) {
  selectedMatterId.value = id
}

function addMatter() {
  const id = `matter-${Date.now()}`
  matterList.value.unshift({
    id,
    name: '新增政务事项',
    category: selectedCategory.value === '全部分类' ? '其他' : selectedCategory.value,
    department: '待配置部门',
    limit: '5个工作日',
    status: '草稿',
    publishChannel: '待发布',
    serviceObject: '自然人',
    updateTime: new Date().toLocaleString('zh-CN', { hour12: false }),
    conditions: '请录入事项受理条件',
    description: '请录入事项说明',
    flow: [{ id: `${id}-flow-1`, name: '申请提交' }],
    materials: [{ id: `${id}-material-1`, name: '申请表', required: true }],
  })
  selectedCategory.value = '全部分类'
  selectedMatterId.value = id
}

function togglePublishStatus() {
  const nextOnline = selectedMatter.value.status !== '已上线'
  selectedMatter.value.status = nextOnline ? '已上线' : '已下线'
  selectedMatter.value.publishChannel = nextOnline ? '政务服务平台' : '已从政务服务平台下线'
  selectedMatter.value.updateTime = new Date().toLocaleString('zh-CN', { hour12: false })
}

function addMaterial() {
  const name = newMaterialName.value.trim()
  if (!name) return
  selectedMatter.value.materials.push({
    id: `${selectedMatter.value.id}-material-${Date.now()}`,
    name,
    required: true,
  })
  newMaterialName.value = ''
}

function removeMaterial(materialId) {
  if (selectedMatter.value.materials.length <= 1) return
  selectedMatter.value.materials = selectedMatter.value.materials.filter((item) => item.id !== materialId)
}

function addFlowStep() {
  const name = newFlowStepName.value.trim()
  if (!name) return
  selectedMatter.value.flow.push({
    id: `${selectedMatter.value.id}-flow-${Date.now()}`,
    name,
  })
  newFlowStepName.value = ''
}

function removeFlowStep(stepId) {
  if (selectedMatter.value.flow.length <= 1) return
  selectedMatter.value.flow = selectedMatter.value.flow.filter((item) => item.id !== stepId)
}

function statusClass(status) {
  return {
    pass: status === '已上线',
    patch: status === '草稿',
    reject: status === '已下线',
  }
}
</script>

<template>
  <section class="admin-page matter-management-page">
    <section class="management-hero matter-hero">
      <div>
        <p class="eyebrow">政务事项管理模块</p>
        <h2>维护事项名称 分类 办理流程 材料清单 办理时限和发布状态</h2>
        <p>
          页面覆盖事项录入管理 事项分类管理 事项发布管理 并补充用例和用例规约说明 适合直接放入前端分支演示后台管理能力
        </p>
      </div>
      <div class="api-card">
        <span>接口预留</span>
        <strong>/api/matters</strong>
        <strong>/api/matters/categories</strong>
        <strong>/api/matters/publish</strong>
      </div>
    </section>

    <section class="overview-grid four-cols">
      <article class="overview-card">
        <span>事项总数</span>
        <strong>{{ matterStats.total }} 项</strong>
        <small>覆盖线上和草稿事项</small>
      </article>
      <article class="overview-card">
        <span>业务分类</span>
        <strong>{{ matterStats.categoryCount }} 类</strong>
        <small>户籍 社保 税务 市场准入等</small>
      </article>
      <article class="overview-card">
        <span>已上线</span>
        <strong>{{ matterStats.onlineCount }} 项</strong>
        <small>已统一发布到政务服务平台</small>
      </article>
      <article class="overview-card">
        <span>草稿或下线</span>
        <strong>{{ matterStats.draftCount }} 项</strong>
        <small>可继续编辑或重新发布</small>
      </article>
    </section>

    <section class="matter-layout">
      <aside class="matter-panel panel-card">
        <div class="panel-heading horizontal">
          <div>
            <p class="eyebrow">事项分类管理</p>
            <h3>业务类型</h3>
          </div>
          <button type="button" class="primary-btn compact-btn" @click="addMatter">新增事项</button>
        </div>

        <div class="category-list">
          <button
            v-for="category in categories"
            :key="category"
            type="button"
            class="category-item"
            :class="{ active: selectedCategory === category }"
            @click="chooseCategory(category)"
          >
            <strong>{{ category }}</strong>
            <span>
              {{ categoryCount(category) }} 项
            </span>
          </button>
        </div>

        <div class="matter-list-block">
          <p class="panel-title">事项列表</p>
          <button
            v-for="matter in filteredMatters"
            :key="matter.id"
            type="button"
            class="matter-item"
            :class="{ active: selectedMatterId === matter.id }"
            @click="chooseMatter(matter.id)"
          >
            <span>{{ matter.name }}</span>
            <small>{{ matter.category }} ｜ {{ matter.department }}</small>
            <em>{{ matter.status }}</em>
          </button>
        </div>
      </aside>

      <section class="panel-card matter-edit-card">
        <div class="panel-heading horizontal">
          <div>
            <p class="eyebrow">事项录入管理</p>
            <h3>{{ selectedMatter.name }}</h3>
          </div>
          <button type="button" class="primary-line-btn" @click="togglePublishStatus">
            {{ selectedMatter.status === '已上线' ? '下线事项' : '上线事项' }}
          </button>
        </div>

        <div class="form-grid two-cols">
          <label>
            <span>事项名称</span>
            <input v-model="selectedMatter.name" type="text" />
          </label>
          <label>
            <span>事项分类</span>
            <select v-model="selectedMatter.category">
              <option>户籍</option>
              <option>社保</option>
              <option>税务</option>
              <option>市场准入</option>
              <option>工程建设</option>
              <option>卫生健康</option>
              <option>其他</option>
            </select>
          </label>
          <label>
            <span>办理部门</span>
            <input v-model="selectedMatter.department" type="text" />
          </label>
          <label>
            <span>办理时限</span>
            <input v-model="selectedMatter.limit" type="text" />
          </label>
          <label>
            <span>服务对象</span>
            <select v-model="selectedMatter.serviceObject">
              <option>自然人</option>
              <option>企业法人</option>
              <option>社会组织</option>
              <option>全部对象</option>
            </select>
          </label>
          <label>
            <span>发布渠道</span>
            <input v-model="selectedMatter.publishChannel" type="text" />
          </label>
          <label class="full-line">
            <span>受理条件</span>
            <textarea v-model="selectedMatter.conditions" rows="3"></textarea>
          </label>
          <label class="full-line">
            <span>事项说明</span>
            <textarea v-model="selectedMatter.description" rows="3"></textarea>
          </label>
        </div>

        <div class="edit-section-grid">
          <section class="inline-panel">
            <div class="panel-heading horizontal small-heading">
              <div>
                <p class="eyebrow">办理流程</p>
                <h3>{{ selectedMatter.flow.length }} 个步骤</h3>
              </div>
            </div>
            <div class="editable-list">
              <article v-for="(step, index) in selectedMatter.flow" :key="step.id" class="editable-row">
                <span>{{ index + 1 }}</span>
                <input v-model="step.name" type="text" />
                <button type="button" class="danger-text" @click="removeFlowStep(step.id)">删除</button>
              </article>
            </div>
            <div class="append-row">
              <input v-model="newFlowStepName" type="text" placeholder="新增流程步骤" @keyup.enter="addFlowStep" />
              <button type="button" class="primary-btn" @click="addFlowStep">添加</button>
            </div>
          </section>

          <section class="inline-panel">
            <div class="panel-heading horizontal small-heading">
              <div>
                <p class="eyebrow">所需材料</p>
                <h3>{{ selectedMatter.materials.length }} 项材料</h3>
              </div>
            </div>
            <div class="editable-list">
              <article v-for="material in selectedMatter.materials" :key="material.id" class="editable-row material-row">
                <label class="checkbox-line">
                  <input v-model="material.required" type="checkbox" />
                  <span>{{ material.required ? '必交' : '选交' }}</span>
                </label>
                <input v-model="material.name" type="text" />
                <button type="button" class="danger-text" @click="removeMaterial(material.id)">删除</button>
              </article>
            </div>
            <div class="append-row">
              <input v-model="newMaterialName" type="text" placeholder="新增材料名称" @keyup.enter="addMaterial" />
              <button type="button" class="primary-btn" @click="addMaterial">添加</button>
            </div>
          </section>
        </div>
      </section>
    </section>

    <section class="panel-card usecase-card">
      <div class="panel-heading horizontal">
        <div>
          <p class="eyebrow">用例与用例规约描述</p>
          <h3>政务事项管理模块用例</h3>
        </div>
        <span class="record-count">{{ useCaseSpecs.length }} 个用例</span>
      </div>

      <div class="usecase-grid">
        <article v-for="spec in useCaseSpecs" :key="spec.name" class="usecase-item">
          <div class="node-title-line">
            <strong>{{ spec.name }}</strong>
            <span class="status-pill normal">{{ spec.actor }}</span>
          </div>
          <p>{{ spec.goal }}</p>
          <dl class="spec-list">
            <div>
              <dt>前置条件</dt>
              <dd>{{ spec.precondition }}</dd>
            </div>
            <div>
              <dt>基本流程</dt>
              <dd>{{ spec.mainFlow.join(' → ') }}</dd>
            </div>
            <div>
              <dt>备选流程</dt>
              <dd>{{ spec.alternative }}</dd>
            </div>
            <div>
              <dt>后置条件</dt>
              <dd>{{ spec.postcondition }}</dd>
            </div>
          </dl>
        </article>
      </div>
    </section>

    <section class="records-card panel-card">
      <div class="panel-heading horizontal">
        <div>
          <p class="eyebrow">发布管理明细</p>
          <h3>事项上线与下线控制</h3>
        </div>
        <span class="record-count">{{ matterList.length }} 条事项</span>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>事项名称</th>
              <th>业务分类</th>
              <th>办理部门</th>
              <th>办理时限</th>
              <th>发布渠道</th>
              <th>更新时间</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="matter in matterList" :key="matter.id">
              <td>{{ matter.name }}</td>
              <td>{{ matter.category }}</td>
              <td>{{ matter.department }}</td>
              <td>{{ matter.limit }}</td>
              <td>{{ matter.publishChannel }}</td>
              <td>{{ matter.updateTime }}</td>
              <td>
                <span class="status-pill" :class="statusClass(matter.status)">{{ matter.status }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>
