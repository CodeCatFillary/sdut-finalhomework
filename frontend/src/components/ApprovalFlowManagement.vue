<script setup>
import { computed, ref } from 'vue'

const selectedMatterId = ref('business-change')
const selectedNodeId = ref('business-change-accept')

const flowMatters = ref([
  {
    id: 'business-change',
    name: '营业执照变更',
    department: '市场监管科',
    status: '已启用',
    averageHours: 18,
    applicantCount: 126,
    nodes: [
      {
        id: 'business-change-submit',
        name: '申请提交',
        level: '申请端',
        department: '网上办事大厅',
        role: '申请人',
        handler: '系统自动接收',
        limit: '即时',
        desc: '校验申请表单和附件完整性 生成事项申请编号',
        condition: '申请信息完整且附件格式合规',
      },
      {
        id: 'business-change-accept',
        name: '窗口受理',
        level: '科室级',
        department: '市场监管科',
        role: '受理员',
        handler: '王晓敏',
        limit: '4小时',
        desc: '核对申请主体资格 材料目录和签章内容 不符合要求时发起补正',
        condition: '材料齐全 符合法定形式',
      },
      {
        id: 'business-change-office',
        name: '科室初审',
        level: '科室级',
        department: '登记审批科',
        role: '科室审核员',
        handler: '刘志强',
        limit: '8小时',
        desc: '检查变更事项 企业登记信息和历史办件记录',
        condition: '受理节点通过后自动流转',
      },
      {
        id: 'business-change-dept',
        name: '部门复核',
        level: '部门级',
        department: '行政审批局',
        role: '部门负责人',
        handler: '陈立',
        limit: '12小时',
        desc: '复核科室意见 决定准予变更 退回修改或不予通过',
        condition: '涉及登记事项实质变更时进入部门复核',
      },
      {
        id: 'business-change-finish',
        name: '办结送达',
        level: '系统归档',
        department: '证照管理中心',
        role: '制证员',
        handler: '系统归档',
        limit: '2小时',
        desc: '生成办结结果 同步电子证照并推送办结通知',
        condition: '部门复核通过',
      },
    ],
  },
  {
    id: 'social-payment',
    name: '社保补缴申请',
    department: '人社服务科',
    status: '已启用',
    averageHours: 26,
    applicantCount: 84,
    nodes: [
      {
        id: 'social-payment-submit',
        name: '申请提交',
        level: '申请端',
        department: '网上办事大厅',
        role: '申请人',
        handler: '系统自动接收',
        limit: '即时',
        desc: '接收补缴申请和劳动关系证明材料',
        condition: '申请人完成实名校验',
      },
      {
        id: 'social-payment-accept',
        name: '材料受理',
        level: '科室级',
        department: '人社服务科',
        role: '经办人',
        handler: '赵宁',
        limit: '6小时',
        desc: '核验申请人身份 用工关系和补缴月份范围',
        condition: '补缴月份不超过系统允许范围',
      },
      {
        id: 'social-payment-office',
        name: '科室复核',
        level: '科室级',
        department: '社会保险科',
        role: '复核员',
        handler: '马洁',
        limit: '12小时',
        desc: '确认缴费基数 补缴金额和单位信息',
        condition: '经办人初审通过',
      },
      {
        id: 'social-payment-dept',
        name: '部门审批',
        level: '部门级',
        department: '人社局',
        role: '审批负责人',
        handler: '高峰',
        limit: '18小时',
        desc: '审批补缴结论 并推送缴费通知',
        condition: '存在跨年度补缴或异常核验时进入部门级审批',
      },
    ],
  },
  {
    id: 'construction-license',
    name: '施工许可备案',
    department: '住建审批科',
    status: '草稿',
    averageHours: 34,
    applicantCount: 45,
    nodes: [
      {
        id: 'construction-license-submit',
        name: '企业申报',
        level: '申请端',
        department: '工程项目审批平台',
        role: '企业经办人',
        handler: '系统自动接收',
        limit: '即时',
        desc: '提交施工许可备案信息和工程材料',
        condition: '项目编号有效',
      },
      {
        id: 'construction-license-office',
        name: '科室审查',
        level: '科室级',
        department: '住建审批科',
        role: '审查员',
        handler: '孙浩',
        limit: '16小时',
        desc: '审查施工单位资质 安全文明施工材料和项目备案数据',
        condition: '材料完整且项目状态正常',
      },
      {
        id: 'construction-license-dept',
        name: '部门签批',
        level: '部门级',
        department: '住房城乡建设局',
        role: '分管负责人',
        handler: '周明',
        limit: '24小时',
        desc: '对科室审查结论进行签批并形成备案结果',
        condition: '科室审查通过',
      },
    ],
  },
])

const approvalRecords = [
  {
    caseNo: 'ZW202607020001',
    matter: '营业执照变更',
    node: '窗口受理',
    approver: '王晓敏',
    department: '市场监管科',
    time: '2026-07-02 09:18',
    opinion: '材料齐全 予以受理',
    status: '通过',
  },
  {
    caseNo: 'ZW202607020001',
    matter: '营业执照变更',
    node: '科室初审',
    approver: '刘志强',
    department: '登记审批科',
    time: '2026-07-02 10:42',
    opinion: '企业名称和变更事项一致 建议通过',
    status: '通过',
  },
  {
    caseNo: 'ZW202607020016',
    matter: '营业执照变更',
    node: '窗口受理',
    approver: '王晓敏',
    department: '市场监管科',
    time: '2026-07-02 14:11',
    opinion: '授权委托书缺少签章 已发起补正',
    status: '补正',
  },
  {
    caseNo: 'ZW202607010082',
    matter: '社保补缴申请',
    node: '材料受理',
    approver: '赵宁',
    department: '人社服务科',
    time: '2026-07-01 16:25',
    opinion: '劳动合同和工资流水已核验',
    status: '通过',
  },
  {
    caseNo: 'ZW202607010082',
    matter: '社保补缴申请',
    node: '部门审批',
    approver: '高峰',
    department: '人社局',
    time: '2026-07-02 11:04',
    opinion: '跨年度补缴情形属实 准予办理',
    status: '通过',
  },
  {
    caseNo: 'ZW202607010109',
    matter: '施工许可备案',
    node: '科室审查',
    approver: '孙浩',
    department: '住建审批科',
    time: '2026-07-02 13:36',
    opinion: '安全文明施工材料缺少承诺书',
    status: '补正',
  },
]

const selectedMatter = computed(() => {
  return flowMatters.value.find((item) => item.id === selectedMatterId.value) || flowMatters.value[0]
})

const selectedNode = computed(() => {
  return selectedMatter.value.nodes.find((node) => node.id === selectedNodeId.value) || selectedMatter.value.nodes[0]
})

const filteredRecords = computed(() => {
  return approvalRecords.filter((record) => record.matter === selectedMatter.value.name)
})

const flowSummary = computed(() => {
  const nodes = selectedMatter.value.nodes
  return {
    total: nodes.length,
    office: nodes.filter((node) => node.level === '科室级').length,
    department: nodes.filter((node) => node.level === '部门级').length,
  }
})

function chooseMatter(id) {
  selectedMatterId.value = id
  const matter = flowMatters.value.find((item) => item.id === id)
  selectedNodeId.value = matter?.nodes[0]?.id || ''
}

function chooseNode(id) {
  selectedNodeId.value = id
}

function addNode() {
  const index = selectedMatter.value.nodes.length + 1
  const id = `${selectedMatter.value.id}-custom-${Date.now()}`
  selectedMatter.value.nodes.push({
    id,
    name: `新增审批节点${index}`,
    level: '科室级',
    department: selectedMatter.value.department,
    role: '审批人员',
    handler: '待分配',
    limit: '8小时',
    desc: '用于演示管理员自定义配置审批流程节点',
    condition: '上一节点审批通过后自动流转',
  })
  selectedNodeId.value = id
}

function removeNode(nodeId) {
  if (selectedMatter.value.nodes.length <= 1) return
  const index = selectedMatter.value.nodes.findIndex((node) => node.id === nodeId)
  if (index < 0) return
  selectedMatter.value.nodes.splice(index, 1)
  selectedNodeId.value = selectedMatter.value.nodes[Math.max(0, index - 1)].id
}

function moveNode(index, direction) {
  const targetIndex = index + direction
  const nodes = selectedMatter.value.nodes
  if (targetIndex < 0 || targetIndex >= nodes.length) return
  const current = nodes[index]
  nodes.splice(index, 1)
  nodes.splice(targetIndex, 0, current)
}

function statusClass(status) {
  return {
    pass: status === '通过',
    patch: status === '补正',
    reject: status === '退回',
  }
}

function toggleStatus() {
  selectedMatter.value.status = selectedMatter.value.status === '已启用' ? '草稿' : '已启用'
}
</script>

<template>
  <section class="admin-page approval-page">
    <section class="management-hero">
      <div>
        <p class="eyebrow">审批流程管理模块</p>
        <h2>按事项配置审批节点 支持科室级和部门级逐级流转</h2>
        <p>
          管理员可以选择不同政务事项 调整审批节点顺序 配置办理科室 审批角色 办理时限和流转条件 页面下方同步展示每一级审批人员 时间和意见记录
        </p>
      </div>
      <div class="api-card">
        <span>接口预留</span>
        <strong>/api/approval/flows</strong>
        <strong>/api/approval/records</strong>
      </div>
    </section>

    <section class="overview-grid four-cols">
      <article class="overview-card">
        <span>当前事项</span>
        <strong>{{ selectedMatter.name }}</strong>
        <small>{{ selectedMatter.department }}</small>
      </article>
      <article class="overview-card">
        <span>流程节点</span>
        <strong>{{ flowSummary.total }} 个</strong>
        <small>按顺序逐级审批</small>
      </article>
      <article class="overview-card">
        <span>多级审批</span>
        <strong>{{ flowSummary.office }} 科室级 {{ flowSummary.department }} 部门级</strong>
        <small>支持科室和部门分层控制</small>
      </article>
      <article class="overview-card">
        <span>流程状态</span>
        <strong>{{ selectedMatter.status }}</strong>
        <small>草稿可继续调整 已启用可进入办件流转</small>
      </article>
    </section>

    <section class="approval-layout">
      <aside class="matter-panel panel-card">
        <div class="panel-heading">
          <p class="eyebrow">事项列表</p>
          <h3>流程配置管理</h3>
        </div>
        <button
          v-for="matter in flowMatters"
          :key="matter.id"
          type="button"
          class="matter-item"
          :class="{ active: matter.id === selectedMatterId }"
          @click="chooseMatter(matter.id)"
        >
          <span>{{ matter.name }}</span>
          <small>{{ matter.department }} · {{ matter.nodes.length }} 个节点</small>
          <em>{{ matter.status }}</em>
        </button>
        <button type="button" class="primary-line-btn" @click="toggleStatus">
          切换当前流程状态
        </button>
      </aside>

      <section class="flow-designer panel-card">
        <div class="panel-heading horizontal">
          <div>
            <p class="eyebrow">流程设计器</p>
            <h3>{{ selectedMatter.name }} 审批流</h3>
          </div>
          <button type="button" class="primary-btn" @click="addNode">新增节点</button>
        </div>

        <div class="flow-track">
          <article
            v-for="(node, index) in selectedMatter.nodes"
            :key="node.id"
            class="flow-node"
            :class="{ active: node.id === selectedNodeId }"
            @click="chooseNode(node.id)"
          >
            <div class="node-index">{{ index + 1 }}</div>
            <div class="node-body">
              <div class="node-title-line">
                <strong>{{ node.name }}</strong>
                <span :class="['level-pill', node.level === '部门级' ? 'department' : 'office']">
                  {{ node.level }}
                </span>
              </div>
              <p>{{ node.desc }}</p>
              <div class="node-meta">
                <span>{{ node.department }}</span>
                <span>{{ node.role }}</span>
                <span>{{ node.limit }}</span>
              </div>
              <div class="node-actions">
                <button type="button" @click.stop="moveNode(index, -1)">上移</button>
                <button type="button" @click.stop="moveNode(index, 1)">下移</button>
                <button type="button" class="danger" @click.stop="removeNode(node.id)">删除</button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <aside class="config-panel panel-card">
        <div class="panel-heading">
          <p class="eyebrow">节点配置</p>
          <h3>{{ selectedNode.name }}</h3>
        </div>

        <div class="form-grid">
          <label>
            <span>节点名称</span>
            <input v-model="selectedNode.name" type="text" />
          </label>
          <label>
            <span>审批层级</span>
            <select v-model="selectedNode.level">
              <option>申请端</option>
              <option>科室级</option>
              <option>部门级</option>
              <option>系统归档</option>
            </select>
          </label>
          <label>
            <span>办理科室</span>
            <input v-model="selectedNode.department" type="text" />
          </label>
          <label>
            <span>审批角色</span>
            <input v-model="selectedNode.role" type="text" />
          </label>
          <label>
            <span>默认审批人</span>
            <input v-model="selectedNode.handler" type="text" />
          </label>
          <label>
            <span>节点时限</span>
            <input v-model="selectedNode.limit" type="text" />
          </label>
          <label class="full-line">
            <span>流转条件</span>
            <textarea v-model="selectedNode.condition" rows="3"></textarea>
          </label>
          <label class="full-line">
            <span>节点说明</span>
            <textarea v-model="selectedNode.desc" rows="4"></textarea>
          </label>
        </div>
      </aside>
    </section>

    <section class="records-card panel-card">
      <div class="panel-heading horizontal">
        <div>
          <p class="eyebrow">审批记录追踪</p>
          <h3>{{ selectedMatter.name }} 审批记录</h3>
        </div>
        <span class="record-count">{{ filteredRecords.length }} 条记录</span>
      </div>

      <div class="data-table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>办件编号</th>
              <th>审批节点</th>
              <th>审批人员</th>
              <th>所属部门</th>
              <th>审批时间</th>
              <th>审批意见</th>
              <th>结果</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in filteredRecords" :key="`${record.caseNo}-${record.node}`">
              <td>{{ record.caseNo }}</td>
              <td>{{ record.node }}</td>
              <td>{{ record.approver }}</td>
              <td>{{ record.department }}</td>
              <td>{{ record.time }}</td>
              <td>{{ record.opinion }}</td>
              <td>
                <span class="status-pill" :class="statusClass(record.status)">{{ record.status }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>
