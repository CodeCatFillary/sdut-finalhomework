<script setup>
import { computed, ref } from 'vue'

const selectedMatterType = ref('全部事项')
const selectedDepartment = ref('全部部门')
const startDate = ref('2026-07-01')
const endDate = ref('2026-07-31')

const statisticRows = ref([
  {
    matter: '营业执照变更',
    type: '市场准入',
    department: '市场监管科',
    applyCount: 126,
    acceptCount: 118,
    finishCount: 109,
    supplementCount: 12,
    overtimeCount: 2,
    avgHours: 18.5,
    legalHours: 48,
  },
  {
    matter: '社保补缴申请',
    type: '人社服务',
    department: '人社服务科',
    applyCount: 84,
    acceptCount: 79,
    finishCount: 68,
    supplementCount: 9,
    overtimeCount: 4,
    avgHours: 26.2,
    legalHours: 72,
  },
  {
    matter: '施工许可备案',
    type: '工程建设',
    department: '住建审批科',
    applyCount: 45,
    acceptCount: 42,
    finishCount: 33,
    supplementCount: 7,
    overtimeCount: 3,
    avgHours: 34.8,
    legalHours: 96,
  },
  {
    matter: '食品经营许可证延续',
    type: '市场准入',
    department: '市场监管科',
    applyCount: 97,
    acceptCount: 91,
    finishCount: 86,
    supplementCount: 6,
    overtimeCount: 1,
    avgHours: 21.4,
    legalHours: 72,
  },
  {
    matter: '公共场所卫生许可',
    type: '卫生健康',
    department: '卫健审批科',
    applyCount: 62,
    acceptCount: 56,
    finishCount: 49,
    supplementCount: 5,
    overtimeCount: 2,
    avgHours: 29.7,
    legalHours: 72,
  },
  {
    matter: '道路运输经营备案',
    type: '交通运输',
    department: '交通运输科',
    applyCount: 38,
    acceptCount: 35,
    finishCount: 31,
    supplementCount: 3,
    overtimeCount: 1,
    avgHours: 24.6,
    legalHours: 72,
  },
])

const matterTypes = computed(() => ['全部事项', ...new Set(statisticRows.value.map((row) => row.type))])
const departments = computed(() => ['全部部门', ...new Set(statisticRows.value.map((row) => row.department))])

const filteredRows = computed(() => {
  return statisticRows.value.filter((row) => {
    const matchType = selectedMatterType.value === '全部事项' || row.type === selectedMatterType.value
    const matchDept = selectedDepartment.value === '全部部门' || row.department === selectedDepartment.value
    return matchType && matchDept
  })
})

const totals = computed(() => {
  const base = filteredRows.value.reduce(
    (result, row) => {
      result.applyCount += row.applyCount
      result.acceptCount += row.acceptCount
      result.finishCount += row.finishCount
      result.supplementCount += row.supplementCount
      result.overtimeCount += row.overtimeCount
      result.weightedHours += row.avgHours * row.finishCount
      return result
    },
    {
      applyCount: 0,
      acceptCount: 0,
      finishCount: 0,
      supplementCount: 0,
      overtimeCount: 0,
      weightedHours: 0,
    },
  )

  const averageHours = base.finishCount ? base.weightedHours / base.finishCount : 0
  const finishRate = base.acceptCount ? (base.finishCount / base.acceptCount) * 100 : 0
  const acceptRate = base.applyCount ? (base.acceptCount / base.applyCount) * 100 : 0

  return {
    ...base,
    averageHours,
    finishRate,
    acceptRate,
  }
})

const maxApplyCount = computed(() => Math.max(...filteredRows.value.map((row) => row.applyCount), 1))
const maxAverageHours = computed(() => Math.max(...filteredRows.value.map((row) => row.avgHours), 1))

function formatRate(value) {
  return `${value.toFixed(1)}%`
}

function formatHours(value) {
  return `${value.toFixed(1)}小时`
}

function barStyle(value, maxValue) {
  return {
    width: `${Math.max(8, (value / maxValue) * 100)}%`,
  }
}

function efficiencyStatus(row) {
  if (row.avgHours <= row.legalHours * 0.35) return '优秀'
  if (row.avgHours <= row.legalHours * 0.65) return '正常'
  return '需关注'
}

function efficiencyClass(row) {
  return {
    excellent: efficiencyStatus(row) === '优秀',
    normal: efficiencyStatus(row) === '正常',
    warning: efficiencyStatus(row) === '需关注',
  }
}

function escapeXml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;')
}

function buildWorksheetXml(sheetName, headers, rows) {
  const headerXml = headers
    .map((header) => `<Cell><Data ss:Type="String">${escapeXml(header)}</Data></Cell>`)
    .join('')

  const rowsXml = rows
    .map((row) => {
      const cellXml = row
        .map((cell) => {
          const isNumber = typeof cell === 'number' && Number.isFinite(cell)
          const type = isNumber ? 'Number' : 'String'
          return `<Cell><Data ss:Type="${type}">${escapeXml(cell)}</Data></Cell>`
        })
        .join('')
      return `<Row>${cellXml}</Row>`
    })
    .join('')

  return `<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
 <Worksheet ss:Name="${escapeXml(sheetName)}">
  <Table>
   <Row>${headerXml}</Row>
   ${rowsXml}
  </Table>
 </Worksheet>
</Workbook>`
}

function downloadExcel(filename, xml) {
  const blob = new Blob(['\ufeff', xml], {
    type: 'application/vnd.ms-excel;charset=utf-8;',
  })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

function exportExcel(type) {
  const dateText = `${startDate.value}_${endDate.value}`

  if (type === 'efficiency') {
    const headers = ['事项名称', '事项类型', '办理部门', '平均办理时长', '法定办理时限', '超期数量', '效率状态']
    const rows = filteredRows.value.map((row) => [
      row.matter,
      row.type,
      row.department,
      row.avgHours,
      row.legalHours,
      row.overtimeCount,
      efficiencyStatus(row),
    ])
    downloadExcel(`办事效率分析_${dateText}.xls`, buildWorksheetXml('办事效率分析', headers, rows))
    return
  }

  const headers = ['事项名称', '事项类型', '办理部门', '申请数量', '受理数量', '办结数量', '补正数量', '办结率']
  const rows = filteredRows.value.map((row) => [
    row.matter,
    row.type,
    row.department,
    row.applyCount,
    row.acceptCount,
    row.finishCount,
    row.supplementCount,
    row.acceptCount ? `${((row.finishCount / row.acceptCount) * 100).toFixed(1)}%` : '0%',
  ])
  downloadExcel(`事项办理统计_${dateText}.xls`, buildWorksheetXml('事项办理统计', headers, rows))
}
</script>

<template>
  <section class="admin-page statistics-page">
    <section class="management-hero statistics-hero">
      <div>
        <p class="eyebrow">统计分析模块</p>
        <h2>自动汇总事项办理数据 分析办事效率并导出 Excel 报表</h2>
        <p>
          支持按事项类型 办理部门和统计周期筛选 统计申请数量 受理数量 办结数量 平均办理时长 超期数量等指标 用于管理端运营分析和报表归档
        </p>
      </div>
      <div class="api-card">
        <span>接口预留</span>
        <strong>/api/statistics/summary</strong>
        <strong>/api/statistics/export</strong>
      </div>
    </section>

    <section class="filter-card panel-card">
      <div class="filter-grid">
        <label>
          <span>开始日期</span>
          <input v-model="startDate" type="date" />
        </label>
        <label>
          <span>结束日期</span>
          <input v-model="endDate" type="date" />
        </label>
        <label>
          <span>事项类型</span>
          <select v-model="selectedMatterType">
            <option v-for="type in matterTypes" :key="type">{{ type }}</option>
          </select>
        </label>
        <label>
          <span>办理部门</span>
          <select v-model="selectedDepartment">
            <option v-for="department in departments" :key="department">{{ department }}</option>
          </select>
        </label>
      </div>
      <div class="export-actions">
        <button type="button" class="primary-btn" @click="exportExcel('summary')">导出办理统计 Excel</button>
        <button type="button" class="primary-line-btn" @click="exportExcel('efficiency')">导出效率分析 Excel</button>
      </div>
    </section>

    <section class="overview-grid five-cols">
      <article class="overview-card">
        <span>申请数量</span>
        <strong>{{ totals.applyCount }}</strong>
        <small>统计周期内提交申请总量</small>
      </article>
      <article class="overview-card">
        <span>受理数量</span>
        <strong>{{ totals.acceptCount }}</strong>
        <small>受理率 {{ formatRate(totals.acceptRate) }}</small>
      </article>
      <article class="overview-card">
        <span>办结数量</span>
        <strong>{{ totals.finishCount }}</strong>
        <small>办结率 {{ formatRate(totals.finishRate) }}</small>
      </article>
      <article class="overview-card">
        <span>平均时效</span>
        <strong>{{ formatHours(totals.averageHours) }}</strong>
        <small>按办结量加权计算</small>
      </article>
      <article class="overview-card">
        <span>超期数量</span>
        <strong>{{ totals.overtimeCount }}</strong>
        <small>用于效率风险提醒</small>
      </article>
    </section>

    <section class="analysis-layout">
      <section class="panel-card chart-panel">
        <div class="panel-heading">
          <p class="eyebrow">事项办理统计</p>
          <h3>申请 受理 办结数量对比</h3>
        </div>
        <div class="bar-list">
          <article v-for="row in filteredRows" :key="row.matter" class="bar-row">
            <div class="bar-label">
              <strong>{{ row.matter }}</strong>
              <span>{{ row.department }}</span>
            </div>
            <div class="triple-bars">
              <div class="bar-line apply">
                <span :style="barStyle(row.applyCount, maxApplyCount)"></span>
                <em>申请 {{ row.applyCount }}</em>
              </div>
              <div class="bar-line accept">
                <span :style="barStyle(row.acceptCount, maxApplyCount)"></span>
                <em>受理 {{ row.acceptCount }}</em>
              </div>
              <div class="bar-line finish">
                <span :style="barStyle(row.finishCount, maxApplyCount)"></span>
                <em>办结 {{ row.finishCount }}</em>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section class="panel-card chart-panel">
        <div class="panel-heading">
          <p class="eyebrow">办事效率分析</p>
          <h3>平均办理时长对比</h3>
        </div>
        <div class="duration-list">
          <article v-for="row in filteredRows" :key="`${row.matter}-duration`" class="duration-row">
            <div class="duration-title">
              <strong>{{ row.matter }}</strong>
              <span class="status-pill" :class="efficiencyClass(row)">{{ efficiencyStatus(row) }}</span>
            </div>
            <div class="duration-track">
              <span :style="barStyle(row.avgHours, maxAverageHours)"></span>
            </div>
            <p>平均 {{ formatHours(row.avgHours) }} 法定 {{ formatHours(row.legalHours) }} 超期 {{ row.overtimeCount }} 件</p>
          </article>
        </div>
      </section>
    </section>

    <section class="panel-card records-card">
      <div class="panel-heading horizontal">
        <div>
          <p class="eyebrow">数据明细</p>
          <h3>政务办理统计与效率明细</h3>
        </div>
        <span class="record-count">{{ filteredRows.length }} 类事项</span>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>事项名称</th>
              <th>类型</th>
              <th>部门</th>
              <th>申请</th>
              <th>受理</th>
              <th>办结</th>
              <th>补正</th>
              <th>平均时长</th>
              <th>办结率</th>
              <th>效率状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in filteredRows" :key="`${row.matter}-table`">
              <td>{{ row.matter }}</td>
              <td>{{ row.type }}</td>
              <td>{{ row.department }}</td>
              <td>{{ row.applyCount }}</td>
              <td>{{ row.acceptCount }}</td>
              <td>{{ row.finishCount }}</td>
              <td>{{ row.supplementCount }}</td>
              <td>{{ formatHours(row.avgHours) }}</td>
              <td>{{ row.acceptCount ? formatRate((row.finishCount / row.acceptCount) * 100) : '0%' }}</td>
              <td>
                <span class="status-pill" :class="efficiencyClass(row)">{{ efficiencyStatus(row) }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>
