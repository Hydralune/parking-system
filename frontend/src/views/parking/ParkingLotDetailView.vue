<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-content">
      <button class="back-btn" @click="goBack">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
        返回列表
      </button>

      <div v-if="parkingLot.id" class="detail-wrap">
        <div class="detail-hero">
          <div class="detail-hero-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.5"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          </div>
          <div class="detail-hero-info">
            <div class="detail-hero-top">
              <h1>{{ parkingLot.name }}</h1>
              <span class="status-badge" :class="parkingLot.status === 1 ? 'success' : 'danger'">
                {{ parkingLot.status === 1 ? '营业中' : '已关闭' }}
              </span>
            </div>
            <p class="detail-address">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/><circle cx="12" cy="10" r="3"/></svg>
              {{ parkingLot.address }}
            </p>
          </div>
          <div class="detail-hero-actions">
            <button class="btn-select-spot" @click="goToSpots" :disabled="parkingLot.status !== 1">
              选择车位
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </button>
          </div>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <h3 class="card-section-title">基本信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <div class="info-icon" style="background:#eff6ff">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div>
                  <div class="info-label">营业时间</div>
                  <div class="info-value">{{ parkingLot.openingTime }} - {{ parkingLot.closingTime }}</div>
                </div>
              </div>
              <div class="info-item">
                <div class="info-icon" style="background:#f0fdf4">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#16a34a" stroke-width="2"><rect x="1" y="6" width="22" height="12" rx="2"/><path d="M1 10h22"/></svg>
                </div>
                <div>
                  <div class="info-label">联系电话</div>
                  <div class="info-value">{{ parkingLot.phone || '暂无' }}</div>
                </div>
              </div>
              <div class="info-item">
                <div class="info-icon" style="background:#fef3c7">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#d97706" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
                </div>
                <div>
                  <div class="info-label">每小时费用</div>
                  <div class="info-value price">¥{{ parkingLot.hourlyRate }}</div>
                </div>
              </div>
              <div class="info-item">
                <div class="info-icon" style="background:#ede9fe">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#7c3aed" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M9 21V9"/></svg>
                </div>
                <div>
                  <div class="info-label">车位情况</div>
                  <div class="info-value"><span style="color:#10b981;font-weight:700">{{ parkingLot.availableSpots }}</span> / {{ parkingLot.totalSpots }} 可用</div>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-card">
            <h3 class="card-section-title">车位占用率</h3>
            <div class="occupancy-display">
              <div class="occupancy-ring">
                <svg viewBox="0 0 100 100" class="ring-svg">
                  <circle cx="50" cy="50" r="40" fill="none" stroke="#f1f5f9" stroke-width="10"/>
                  <circle cx="50" cy="50" r="40" fill="none" stroke="url(#grad)" stroke-width="10"
                    stroke-dasharray="251.2"
                    :stroke-dashoffset="251.2 * (1 - occupancyRate)"
                    stroke-linecap="round"
                    transform="rotate(-90 50 50)"/>
                  <defs>
                    <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="0%">
                      <stop offset="0%" stop-color="#2563eb"/>
                      <stop offset="100%" stop-color="#0ea5e9"/>
                    </linearGradient>
                  </defs>
                </svg>
                <div class="ring-label">
                  <span class="ring-pct">{{ Math.round(occupancyRate * 100) }}%</span>
                  <span class="ring-sub">占用率</span>
                </div>
              </div>
              <div class="occupancy-legend">
                <div class="occ-item"><span class="occ-dot occupied"></span>已占用 {{ parkingLot.totalSpots - parkingLot.availableSpots }}</div>
                <div class="occ-item"><span class="occ-dot available"></span>空闲 {{ parkingLot.availableSpots }}</div>
              </div>
            </div>
          </div>

          <div class="detail-card full-width" v-if="parkingLot.description">
            <h3 class="card-section-title">停车场描述</h3>
            <p class="desc-text">{{ parkingLot.description }}</p>
          </div>
        </div>
      </div>

      <div v-else class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getParkingLotById } from '@/api/parking'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'ParkingLotDetailView',
  components: { HeaderNav },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const parkingLot = ref({ id: null, name: '', address: '', openingTime: '', closingTime: '', phone: '', totalSpots: 0, availableSpots: 0, hourlyRate: 0, description: '', status: 0 })

    const occupancyRate = computed(() => {
      if (!parkingLot.value.totalSpots) return 0
      return (parkingLot.value.totalSpots - parkingLot.value.availableSpots) / parkingLot.value.totalSpots
    })

    const fetchParkingLotDetail = async () => {
      try {
        const res = await getParkingLotById(route.params.id)
        if (res.success) parkingLot.value = res.data
        else { ElMessage.error(res.message || '获取详情失败'); router.push('/parking-lots') }
      } catch (e) { ElMessage.error('获取详情失败：' + e.message); router.push('/parking-lots') }
    }

    const goToSpots = () => router.push(`/spots/${route.params.id}`)
    const goBack = () => router.go(-1)
    onMounted(fetchParkingLotDetail)
    return { parkingLot, occupancyRate, goToSpots, goBack }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1100px; margin: 0 auto; width: 100%; padding: 32px 24px 60px; }

.back-btn { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--border); background: white; font-size: 14px; color: var(--text-secondary); cursor: pointer; transition: var(--transition); margin-bottom: 28px; }
.back-btn:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-bg); }

.detail-hero { background: linear-gradient(135deg, var(--primary-dark), var(--primary)); border-radius: var(--radius-lg); padding: 32px; display: flex; align-items: center; gap: 24px; margin-bottom: 24px; }
.detail-hero-icon { width: 72px; height: 72px; background: rgba(255,255,255,0.15); border-radius: 18px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.detail-hero-info { flex: 1; }
.detail-hero-top { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.detail-hero-top h1 { font-size: 1.6rem; font-weight: 800; color: white; }
.status-badge { display: inline-block; padding: 4px 12px; border-radius: 100px; font-size: 12px; font-weight: 600; }
.status-badge.success { background: rgba(16,185,129,0.2); color: #6ee7b7; }
.status-badge.danger { background: rgba(239,68,68,0.2); color: #fca5a5; }
.detail-address { display: flex; align-items: center; gap: 6px; font-size: 14px; color: rgba(255,255,255,0.75); }
.btn-select-spot { display: flex; align-items: center; gap: 8px; padding: 12px 24px; border-radius: 12px; background: white; color: var(--primary); font-size: 14px; font-weight: 700; border: none; cursor: pointer; transition: var(--transition); flex-shrink: 0; }
.btn-select-spot:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(0,0,0,0.15); }
.btn-select-spot:disabled { opacity: 0.5; cursor: not-allowed; }

.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.detail-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 24px; }
.detail-card.full-width { grid-column: 1 / -1; }
.card-section-title { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border); }

.info-grid { display: flex; flex-direction: column; gap: 16px; }
.info-item { display: flex; align-items: center; gap: 14px; }
.info-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.info-label { font-size: 12px; color: var(--text-muted); margin-bottom: 2px; }
.info-value { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.info-value.price { font-size: 18px; color: var(--primary); }

.occupancy-display { display: flex; align-items: center; gap: 32px; }
.occupancy-ring { position: relative; width: 120px; height: 120px; flex-shrink: 0; }
.ring-svg { width: 100%; height: 100%; }
.ring-label { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.ring-pct { font-size: 22px; font-weight: 800; color: var(--primary); }
.ring-sub { font-size: 11px; color: var(--text-muted); }
.occupancy-legend { display: flex; flex-direction: column; gap: 12px; }
.occ-item { display: flex; align-items: center; gap: 8px; font-size: 14px; color: var(--text-secondary); }
.occ-dot { width: 10px; height: 10px; border-radius: 50%; }
.occ-dot.occupied { background: var(--primary); }
.occ-dot.available { background: #10b981; }

.desc-text { font-size: 14px; color: var(--text-secondary); line-height: 1.8; }

.loading-state { text-align: center; padding: 80px; color: var(--text-muted); }
.loading-spinner { width: 40px; height: 40px; border: 3px solid var(--border); border-top-color: var(--primary); border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 768px) {
  .detail-hero { flex-direction: column; align-items: flex-start; }
  .detail-grid { grid-template-columns: 1fr; }
}
</style>
