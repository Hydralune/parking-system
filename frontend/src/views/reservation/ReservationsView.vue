<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-content">
      <div class="page-header-bar">
        <div class="page-header-inner">
          <h1>我的预约</h1>
          <p>查看和管理您的所有停车预约</p>
        </div>
        <router-link to="/reservations/create" class="btn-new">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          新建预约
        </router-link>
      </div>

      <div class="content-wrap">
      <div class="member-banner" v-if="memberInfo && memberInfo.validTo && new Date(memberInfo.validTo) > new Date()">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
        <span>{{ { 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }[memberInfo.memberLevel] }}</span>
        <span class="member-discount">享 {{ (memberInfo.discountRate * 10).toFixed(1) }} 折优惠</span>
        <span class="member-points">积分：{{ memberInfo.points }}</span>
        <span class="member-expire">有效期至 {{ memberInfo.validTo }}</span>
      </div>

      <!-- Empty state -->
        <div v-if="!loading && reservations.length === 0" class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" stroke-width="1.5"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          <p>暂无预约记录</p>
          <router-link to="/parking-lots" class="btn-go">去预约停车位</router-link>
        </div>

        <!-- Reservation cards -->
        <div v-else class="reservations-list" v-loading="loading">
          <div v-for="(r, i) in reservations" :key="r.id" class="reservation-card" :style="`animation-delay:${i*0.06}s`">
            <div class="res-card-left">
              <div class="res-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              </div>
              <div class="res-info">
                <div class="res-id">#{{ r.id }}</div>
                <div class="res-lot">{{ r.parkingLotName || '停车场' }}</div>
                <div class="res-spot">车位 {{ r.spotNumber || '-' }}</div>
              </div>
            </div>
            <div class="res-card-mid">
              <div class="res-time-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                <span>{{ formatDateTime(r.startTime) }}</span>
                <span class="time-sep">→</span>
                <span>{{ formatDateTime(r.endTime) }}</span>
              </div>
              <div class="res-plate" v-if="r.licensePlate">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="6" width="22" height="12" rx="2"/><path d="M5 10h2m4 0h2m4 0h2"/></svg>
                {{ r.licensePlate }}
              </div>
            </div>
            <div class="res-card-right">
              <div class="res-fee">¥{{ r.totalFee || '0' }}</div>
              <div class="res-badges">
                <span class="res-badge" :class="getStatusClass(r.reservationStatus)">{{ getStatusText(r.reservationStatus) }}</span>
                <span class="res-badge" :class="getPaymentClass(r.paymentStatus)">{{ getPaymentText(r.paymentStatus) }}</span>
              </div>
              <div class="res-actions">
                <button class="res-btn pay" v-if="r.paymentStatus === 0 && r.reservationStatus === 2" @click="goToPayment(r)">支付</button>
                <button class="res-btn cancel" v-if="r.reservationStatus === 1 || r.reservationStatus === 2" @click="cancelReservation(r)">取消</button>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrap" v-if="pagination.total > pagination.size">
          <el-pagination :current-page="pagination.current" :page-size="pagination.size" :page-sizes="[10,20,50]" :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserReservations, cancelReservation as apiCancel, getMyMember } from '@/api/reservation'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'ReservationsView',
  components: { HeaderNav },
  setup() {
    const router = useRouter()
    const reservations = ref([])
    const pagination = reactive({ current: 1, size: 10, total: 0 })
    const loading = ref(false)
    const memberInfo = ref(null)

    const fetchMember = async () => {
      try {
        const res = await getMyMember()
        if (res.success) memberInfo.value = res.data
      } catch (e) { /* 非会员正常忽略 */ }
    }

    const formatDateTime = (dt) => dt ? new Date(dt).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : '-'
    const getStatusText = (s) => ({ 1: '待确认', 2: '已确认', 3: '已取消', 4: '已完成', 5: '已过期' }[s] || '未知')
    const getStatusClass = (s) => ({ 1: 'badge-info', 2: 'badge-success', 3: 'badge-danger', 4: 'badge-purple', 5: 'badge-muted' }[s] || 'badge-muted')
    const getPaymentText = (s) => ({ 0: '未支付', 1: '已支付', 2: '已退款' }[s] || '未知')
    const getPaymentClass = (s) => ({ 0: 'badge-warning', 1: 'badge-success', 2: 'badge-muted' }[s] || 'badge-muted')

    const fetchReservations = async () => {
      loading.value = true
      try {
        const res = await getUserReservations({ current: pagination.current, size: pagination.size })
        if (res.success) { reservations.value = res.data.records || res.data; pagination.total = res.data.total || 0 }
        else ElMessage.error(res.message || '获取失败')
      } catch (e) { ElMessage.error('获取失败：' + e.message) } finally { loading.value = false }
    }

    const goToPayment = (r) => router.push({ path: '/payment', state: { reservation: JSON.stringify(r) } })

    const cancelReservation = async (r) => {
      try {
        await ElMessageBox.confirm('确定要取消这个预约吗？', '确认取消', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        const res = await apiCancel(r.id)
        if (res.success) { ElMessage.success('预约已取消'); fetchReservations() }
        else ElMessage.error(res.message || '取消失败')
      } catch (e) { if (e !== 'cancel') ElMessage.error('取消失败：' + e.message) }
    }

    const handleSizeChange = (s) => { pagination.size = s; fetchReservations() }
    const handleCurrentChange = (c) => { pagination.current = c; fetchReservations() }
    onMounted(() => { fetchReservations(); fetchMember() })
    return { reservations, pagination, loading, memberInfo, formatDateTime, getStatusText, getStatusClass, getPaymentText, getPaymentClass, goToPayment, cancelReservation, handleSizeChange, handleCurrentChange }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1100px; margin: 0 auto; width: 100%; padding: 40px 24px 60px; }

.page-header-bar { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 32px; }
.page-header-inner h1 { font-size: 1.8rem; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; }
.page-header-inner p { font-size: 14px; color: var(--text-muted); }
.btn-new { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 10px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 14px; font-weight: 600; text-decoration: none; box-shadow: 0 4px 12px rgba(37,99,235,0.3); transition: var(--transition); }
.btn-new:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(37,99,235,0.4); }

.content-wrap { }
.empty-state { text-align: center; padding: 80px 20px; color: var(--text-muted); }
.empty-state p { margin: 16px 0 24px; font-size: 15px; }
.btn-go { display: inline-block; padding: 10px 24px; border-radius: 10px; background: var(--primary); color: white; text-decoration: none; font-size: 14px; font-weight: 600; }

.reservations-list { display: flex; flex-direction: column; gap: 14px; }
.reservation-card {
  background: white; border-radius: var(--radius-lg); border: 1px solid var(--border);
  padding: 20px 24px; display: flex; align-items: center; gap: 24px;
  transition: var(--transition); animation: fadeInUp 0.4s ease both;
}
.reservation-card:hover { box-shadow: var(--shadow); border-color: rgba(37,99,235,0.15); }

.res-card-left { display: flex; align-items: center; gap: 14px; flex: 1; min-width: 0; }
.res-icon { width: 44px; height: 44px; border-radius: 12px; background: linear-gradient(135deg, var(--primary), var(--accent)); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.res-id { font-size: 12px; color: var(--text-muted); margin-bottom: 2px; }
.res-lot { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.res-spot { font-size: 13px; color: var(--text-secondary); }

.res-card-mid { flex: 1.5; min-width: 0; }
.res-time-row { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); margin-bottom: 6px; flex-wrap: wrap; }
.time-sep { color: var(--text-muted); }
.res-plate { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); }

.res-card-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; flex-shrink: 0; }
.res-fee { font-size: 1.4rem; font-weight: 800; color: var(--primary); }
.res-badges { display: flex; gap: 6px; flex-wrap: wrap; justify-content: flex-end; }
.res-badge { padding: 3px 10px; border-radius: 100px; font-size: 11px; font-weight: 600; }
.badge-info { background: #dbeafe; color: #2563eb; }
.badge-success { background: #dcfce7; color: #16a34a; }
.badge-danger { background: #fee2e2; color: #dc2626; }
.badge-warning { background: #fef3c7; color: #d97706; }
.badge-purple { background: #ede9fe; color: #7c3aed; }
.badge-muted { background: #f1f5f9; color: #64748b; }
.res-actions { display: flex; gap: 8px; }
.res-btn { padding: 6px 14px; border-radius: 8px; font-size: 12px; font-weight: 600; border: none; cursor: pointer; transition: var(--transition); }
.res-btn.pay { background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; }
.res-btn.pay:hover { transform: translateY(-1px); box-shadow: 0 4px 10px rgba(37,99,235,0.3); }
.res-btn.cancel { background: #fef2f2; color: #dc2626; }
.res-btn.cancel:hover { background: #fee2e2; }

.member-banner {
  display: flex; align-items: center; gap: 12px; flex-wrap: wrap;
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  border: 1px solid #f59e0b; border-radius: 12px;
  padding: 12px 20px; margin-bottom: 24px;
  font-size: 14px; font-weight: 600; color: #92400e;
}
.member-banner svg { color: #f59e0b; flex-shrink: 0; }
.member-discount { background: #f59e0b; color: white; padding: 2px 10px; border-radius: 100px; font-size: 13px; }
.member-points { color: #b45309; }
.member-expire { margin-left: auto; font-size: 12px; font-weight: 400; color: #b45309; }

.pagination-wrap { display: flex; justify-content: center; margin-top: 32px; }

@media (max-width: 768px) {
  .reservation-card { flex-direction: column; align-items: flex-start; }
  .res-card-right { align-items: flex-start; }
  .page-header-bar { flex-direction: column; align-items: flex-start; gap: 16px; }
}
</style>
