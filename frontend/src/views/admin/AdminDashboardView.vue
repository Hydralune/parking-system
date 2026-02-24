<template>
  <div class="dashboard">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <rect x="2" y="3" width="20" height="18" rx="3" fill="white" fill-opacity="0.3" stroke="white" stroke-width="1.5"/>
            <path d="M7 8h5.5a2.5 2.5 0 010 5H7V8z" fill="white"/>
            <path d="M7 13h3v4H7v-4z" fill="white" opacity="0.7"/>
          </svg>
        </div>
        <span>智停管理</span>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="sidebar-item" :class="{ active: $route.path === '/admin' }">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
          概览
        </router-link>
        <router-link to="/admin/parking-lots" class="sidebar-item" :class="{ active: $route.path === '/admin/parking-lots' }">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          停车场管理
        </router-link>
        <router-link to="/admin/reservations" class="sidebar-item" :class="{ active: $route.path === '/admin/reservations' }">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          预约管理
        </router-link>
        <router-link to="/admin/members" class="sidebar-item" :class="{ active: $route.path === '/admin/members' }">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>
          会员管理
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/" class="sidebar-item">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
          返回前台
        </router-link>
      </div>
    </aside>

    <!-- Main -->
    <main class="main">
      <div class="main-header">
        <div>
          <h1 class="main-title">控制台概览</h1>
          <p class="main-subtitle">欢迎回来，{{ userStore.username }}</p>
        </div>
        <div class="header-date">{{ currentDate }}</div>
      </div>

      <!-- Stat cards -->
      <div class="stat-cards">
        <div class="stat-card" v-for="(s, i) in statCards" :key="s.label" :style="`animation-delay:${i*0.08}s`">
          <div class="stat-card-icon" :style="`background:${s.bg}`">
            <component :is="s.icon" style="width:22px;height:22px;color:white" />
          </div>
          <div class="stat-card-body">
            <div class="stat-card-value">{{ s.value }}</div>
            <div class="stat-card-label">{{ s.label }}</div>
          </div>
          <div class="stat-card-trend" :class="s.trend > 0 ? 'up' : 'down'">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path v-if="s.trend > 0" d="M18 15l-6-6-6 6"/>
              <path v-else d="M6 9l6 6 6-6"/>
            </svg>
            {{ Math.abs(s.trend) }}%
          </div>
        </div>
      </div>

      <!-- Charts row -->
      <div class="charts-row">
        <div class="chart-card wide">
          <div class="chart-card-header">
            <h3>预约趋势</h3>
            <div class="chart-tabs">
              <button v-for="t in ['7天','30天','90天']" :key="t" class="chart-tab" :class="{ active: activeTab === t }" @click="activeTab = t; updateLineChart()">{{ t }}</button>
            </div>
          </div>
          <div ref="lineChartRef" class="chart-container"></div>
        </div>
        <div class="chart-card narrow">
          <div class="chart-card-header"><h3>停车场使用率</h3></div>
          <div ref="pieChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- Second charts row -->
      <div class="charts-row">
        <div class="chart-card half">
          <div class="chart-card-header"><h3>收入统计（月）</h3></div>
          <div ref="barChartRef" class="chart-container"></div>
        </div>
        <div class="chart-card half">
          <div class="chart-card-header"><h3>会员等级分布</h3></div>
          <div ref="memberChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- Quick actions -->
      <div class="quick-actions">
        <h3 class="section-label">快捷操作</h3>
        <div class="action-grid">
          <router-link to="/admin/parking-lots" class="action-card">
            <div class="action-icon" style="background:linear-gradient(135deg,#2563eb,#0ea5e9)">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
            </div>
            <span>停车场管理</span>
            <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </router-link>
          <router-link to="/admin/reservations" class="action-card">
            <div class="action-icon" style="background:linear-gradient(135deg,#059669,#10b981)">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            </div>
            <span>预约管理</span>
            <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </router-link>
          <router-link to="/admin/members" class="action-card">
            <div class="action-icon" style="background:linear-gradient(135deg,#d97706,#f59e0b)">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
            </div>
            <span>会员管理</span>
            <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </router-link>
          <router-link to="/" class="action-card">
            <div class="action-icon" style="background:linear-gradient(135deg,#7c3aed,#a855f7)">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.07 4.93a10 10 0 010 14.14M4.93 4.93a10 10 0 000 14.14"/></svg>
            </div>
            <span>前台首页</span>
            <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'
import { Document, Wallet, User, Place } from '@element-plus/icons-vue'

export default {
  name: 'AdminDashboardView',
  components: { Document, Wallet, User, Place },
  setup() {
    const userStore = useUserStore()
    const lineChartRef = ref(null)
    const pieChartRef = ref(null)
    const barChartRef = ref(null)
    const memberChartRef = ref(null)
    const activeTab = ref('7天')
    let lineChart, pieChart, barChart, memberChart

    const currentDate = computed(() => {
      return new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
    })

    const statCards = ref([
      { label: '总预约数', value: '1,284', trend: 12, icon: 'Document', bg: 'linear-gradient(135deg,#2563eb,#0ea5e9)' },
      { label: '本月收入', value: '¥28,640', trend: 8, icon: 'Wallet', bg: 'linear-gradient(135deg,#059669,#10b981)' },
      { label: '注册用户', value: '3,240', trend: 5, icon: 'User', bg: 'linear-gradient(135deg,#7c3aed,#a855f7)' },
      { label: '停车场数', value: '12', trend: -2, icon: 'Place', bg: 'linear-gradient(135deg,#d97706,#f59e0b)' },
    ])

    const lineData = {
      '7天': { days: ['周一','周二','周三','周四','周五','周六','周日'], vals: [42,58,65,48,72,89,76] },
      '30天': { days: Array.from({length:30},(_,i)=>`${i+1}日`), vals: Array.from({length:30},()=>Math.floor(30+Math.random()*80)) },
      '90天': { days: Array.from({length:12},(_,i)=>`第${i+1}周`), vals: Array.from({length:12},()=>Math.floor(200+Math.random()*400)) },
    }

    const initLineChart = () => {
      lineChart = echarts.init(lineChartRef.value)
      updateLineChart()
    }

    const updateLineChart = () => {
      if (!lineChart) return
      const d = lineData[activeTab.value]
      lineChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'white', borderColor: '#e2e8f0', textStyle: { color: '#0f172a' } },
        grid: { left: 40, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: d.days, axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8', fontSize: 12 }, splitLine: { show: false } },
        yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: '#94a3b8', fontSize: 12 }, splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } } },
        series: [{
          data: d.vals, type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
          lineStyle: { color: '#2563eb', width: 3 },
          itemStyle: { color: '#2563eb', borderColor: 'white', borderWidth: 2 },
          areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(37,99,235,0.15)' }, { offset: 1, color: 'rgba(37,99,235,0)' }] } }
        }]
      })
    }

    const initPieChart = () => {
      pieChart = echarts.init(pieChartRef.value)
      pieChart.setOption({
        tooltip: { trigger: 'item', backgroundColor: 'white', borderColor: '#e2e8f0', textStyle: { color: '#0f172a' } },
        legend: { bottom: 0, textStyle: { color: '#64748b', fontSize: 12 } },
        series: [{
          type: 'pie', radius: ['45%', '70%'], center: ['50%', '45%'],
          data: [
            { value: 78, name: '中央商务区', itemStyle: { color: '#2563eb' } },
            { value: 65, name: '购物中心', itemStyle: { color: '#0ea5e9' } },
            { value: 52, name: '科技园区', itemStyle: { color: '#7c3aed' } },
            { value: 40, name: '其他', itemStyle: { color: '#e2e8f0' } },
          ],
          label: { show: false },
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.1)' } }
        }]
      })
    }

    const initBarChart = () => {
      barChart = echarts.init(barChartRef.value)
      barChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'white', borderColor: '#e2e8f0', textStyle: { color: '#0f172a' } },
        grid: { left: 50, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'], axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8', fontSize: 11 }, splitLine: { show: false } },
        yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: '#94a3b8', fontSize: 11, formatter: v => `¥${v/1000}k` }, splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } } },
        series: [{
          data: [8200,9400,11200,10800,13600,15200,14800,16400,13200,17800,19200,21400],
          type: 'bar', barWidth: '55%', borderRadius: [6,6,0,0],
          itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#2563eb' }, { offset: 1, color: '#0ea5e9' }] } }
        }]
      })
    }

    const initMemberChart = () => {
      memberChart = echarts.init(memberChartRef.value)
      memberChart.setOption({
        tooltip: { trigger: 'item', backgroundColor: 'white', borderColor: '#e2e8f0', textStyle: { color: '#0f172a' } },
        legend: { bottom: 0, textStyle: { color: '#64748b', fontSize: 12 } },
        series: [{
          type: 'pie', radius: ['0%', '65%'], center: ['50%', '45%'],
          roseType: 'area',
          data: [
            { value: 1200, name: '普通会员', itemStyle: { color: '#94a3b8' } },
            { value: 680, name: '银卡会员', itemStyle: { color: '#0ea5e9' } },
            { value: 320, name: '金卡会员', itemStyle: { color: '#f59e0b' } },
            { value: 80, name: '钻石会员', itemStyle: { color: '#7c3aed' } },
          ],
          label: { show: false },
        }]
      })
    }

    const handleResize = () => {
      lineChart?.resize(); pieChart?.resize(); barChart?.resize(); memberChart?.resize()
    }

    onMounted(() => {
      initLineChart(); initPieChart(); initBarChart(); initMemberChart()
      window.addEventListener('resize', handleResize)
    })
    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      lineChart?.dispose(); pieChart?.dispose(); barChart?.dispose(); memberChart?.dispose()
    })

    return { userStore, statCards, lineChartRef, pieChartRef, barChartRef, memberChartRef, activeTab, currentDate, updateLineChart }
  }
}
</script>

<style scoped>
.dashboard { display: flex; min-height: 100vh; background: var(--bg); }

/* Sidebar */
.sidebar {
  width: 240px; flex-shrink: 0;
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  display: flex; flex-direction: column;
  position: sticky; top: 0; height: 100vh;
}
.sidebar-logo { display: flex; align-items: center; gap: 10px; padding: 24px 20px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.logo-icon { width: 36px; height: 36px; background: linear-gradient(135deg, var(--primary), var(--accent)); border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.sidebar-logo span { font-size: 16px; font-weight: 700; color: white; }
.sidebar-nav { flex: 1; padding: 16px 12px; display: flex; flex-direction: column; gap: 4px; }
.sidebar-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px; border-radius: 10px;
  font-size: 14px; font-weight: 500; color: #94a3b8;
  text-decoration: none; transition: var(--transition);
}
.sidebar-item:hover { color: white; background: rgba(255,255,255,0.08); }
.sidebar-item.active { color: white; background: rgba(37,99,235,0.3); }
.sidebar-footer { padding: 12px; border-top: 1px solid rgba(255,255,255,0.08); }

/* Main */
.main { flex: 1; padding: 32px; overflow-y: auto; min-width: 0; }
.main-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 32px; }
.main-title { font-size: 1.6rem; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; }
.main-subtitle { font-size: 14px; color: var(--text-muted); }
.header-date { font-size: 13px; color: var(--text-muted); background: white; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--border); }

/* Stat cards */
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }
.stat-card {
  background: white; border-radius: var(--radius); padding: 20px;
  border: 1px solid var(--border); display: flex; align-items: center; gap: 16px;
  animation: fadeInUp 0.4s ease both; transition: var(--transition);
}
.stat-card:hover { transform: translateY(-3px); box-shadow: var(--shadow); }
.stat-card-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-card-body { flex: 1; }
.stat-card-value { font-size: 1.5rem; font-weight: 800; color: var(--text-primary); }
.stat-card-label { font-size: 13px; color: var(--text-muted); margin-top: 2px; }
.stat-card-trend { display: flex; align-items: center; gap: 3px; font-size: 12px; font-weight: 600; padding: 3px 8px; border-radius: 100px; flex-shrink: 0; }
.stat-card-trend.up { color: var(--success); background: #dcfce7; }
.stat-card-trend.down { color: var(--danger); background: #fee2e2; }

/* Charts */
.charts-row { display: flex; gap: 20px; margin-bottom: 24px; }
.chart-card { background: white; border-radius: var(--radius); border: 1px solid var(--border); padding: 20px; }
.chart-card.wide { flex: 2; }
.chart-card.narrow { flex: 1; }
.chart-card.half { flex: 1; }
.chart-card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.chart-card-header h3 { font-size: 15px; font-weight: 700; color: var(--text-primary); }
.chart-tabs { display: flex; gap: 4px; }
.chart-tab { padding: 4px 12px; border-radius: 6px; font-size: 12px; font-weight: 500; border: 1px solid var(--border); background: none; cursor: pointer; color: var(--text-muted); transition: var(--transition); }
.chart-tab.active { background: var(--primary); color: white; border-color: var(--primary); }
.chart-container { height: 240px; }

/* Quick actions */
.quick-actions { background: white; border-radius: var(--radius); border: 1px solid var(--border); padding: 20px; }
.section-label { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 16px; }
.action-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.action-card {
  display: flex; align-items: center; gap: 12px;
  padding: 16px; border-radius: 12px;
  border: 1px solid var(--border); text-decoration: none;
  color: var(--text-primary); font-size: 14px; font-weight: 600;
  transition: var(--transition);
}
.action-card:hover { border-color: var(--primary); background: var(--primary-bg); transform: translateY(-2px); box-shadow: var(--shadow); }
.action-icon { width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.action-arrow { margin-left: auto; color: var(--text-muted); }

@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .action-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 900px) {
  .sidebar { display: none; }
  .charts-row { flex-direction: column; }
  .main { padding: 20px; }
}
</style>
