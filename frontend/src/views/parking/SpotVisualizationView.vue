<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-content">
      <button class="back-btn" @click="goBack">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
        返回
      </button>

      <div class="spot-wrap">
        <!-- Info bar -->
        <div class="lot-info-bar">
          <div class="lot-info-left">
            <div class="lot-info-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
            </div>
            <div>
              <div class="lot-info-name">{{ parkingLotName }}</div>
              <div class="lot-info-stats">
                <span class="stat-avail">{{ availableSpots }} 空位</span>
                <span class="stat-sep">·</span>
                <span class="stat-total">共 {{ totalSpots }} 个</span>
              </div>
            </div>
          </div>
          <div class="spot-legend">
            <div class="legend-item"><span class="legend-dot available"></span>可用</div>
            <div class="legend-item"><span class="legend-dot booked"></span>已预订</div>
            <div class="legend-item"><span class="legend-dot occupied"></span>已占用</div>
            <div class="legend-item"><span class="legend-dot maintenance"></span>维护中</div>
          </div>
        </div>

        <!-- Spots grid -->
        <div class="spots-card">
          <div class="spots-grid">
            <div
              v-for="spot in parkingSpots"
              :key="spot.id"
              class="spot-cell"
              :class="[getSpotClass(spot), { selected: selectedSpot?.id === spot.id }]"
              @click="selectSpot(spot)"
            >
              <div class="spot-number">{{ spot.spotNumber }}</div>
              <div class="spot-type-label">{{ getSpotTypeShort(spot.spotType) }}</div>
            </div>
          </div>
        </div>

        <!-- Selected spot panel -->
        <transition name="slide-up">
          <div class="selected-panel" v-if="selectedSpot">
            <div class="selected-panel-inner">
              <div class="selected-info">
                <div class="selected-badge">已选</div>
                <div class="selected-number">{{ selectedSpot.spotNumber }}</div>
                <div class="selected-meta">{{ getSpotTypeText(selectedSpot.spotType) }} · 可用</div>
              </div>
              <button class="btn-reserve" @click="goToReservation">
                立即预约
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getParkingLotById } from '@/api/parking'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'SpotVisualizationView',
  components: { HeaderNav },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const parkingLotName = ref('停车场')
    const totalSpots = ref(0)
    const availableSpots = ref(0)
    const parkingSpots = ref([])
    const selectedSpot = ref(null)

    const mockSpots = [
      { id:1, spotNumber:'A001', spotType:1, status:1 }, { id:2, spotNumber:'A002', spotType:1, status:1 },
      { id:3, spotNumber:'A003', spotType:1, status:2 }, { id:4, spotNumber:'A004', spotType:1, status:3 },
      { id:5, spotNumber:'A005', spotType:1, status:1 }, { id:6, spotNumber:'B001', spotType:1, status:0 },
      { id:7, spotNumber:'B002', spotType:1, status:1 }, { id:8, spotNumber:'B003', spotType:2, status:1 },
      { id:9, spotNumber:'B004', spotType:1, status:1 }, { id:10, spotNumber:'B005', spotType:3, status:1 },
      { id:11, spotNumber:'C001', spotType:1, status:1 }, { id:12, spotNumber:'C002', spotType:1, status:2 },
      { id:13, spotNumber:'D001', spotType:3, status:1 }, { id:14, spotNumber:'D002', spotType:1, status:1 },
      { id:15, spotNumber:'D003', spotType:1, status:3 },
    ]

    const getSpotClass = (s) => ({ 0:'maintenance', 1:'available', 2:'booked', 3:'occupied' }[s.status] || 'available')
    const getSpotTypeText = (t) => ({ 1:'小型车位', 2:'大型车位', 3:'残疾人专位' }[t] || '标准')
    const getSpotTypeShort = (t) => ({ 1:'小', 2:'大', 3:'♿' }[t] || '标')

    const selectSpot = (spot) => {
      if (spot.status !== 1) { ElMessage.warning('该车位不可用'); return }
      selectedSpot.value = selectedSpot.value?.id === spot.id ? null : spot
    }

    const goToReservation = () => {
      if (!selectedSpot.value) return
      router.push({ name: 'CreateReservation', query: { parkingLotId: route.params.parkingLotId, spotId: selectedSpot.value.id } })
    }

    const goBack = () => router.go(-1)

    onMounted(async () => {
      try {
        const res = await getParkingLotById(route.params.parkingLotId)
        if (res.success) { parkingLotName.value = res.data.name; totalSpots.value = res.data.totalSpots; availableSpots.value = res.data.availableSpots }
      } catch (e) {}
      parkingSpots.value = mockSpots
      availableSpots.value = mockSpots.filter(s => s.status === 1).length
      totalSpots.value = mockSpots.length
    })

    return { parkingLotName, totalSpots, availableSpots, parkingSpots, selectedSpot, getSpotClass, getSpotTypeText, getSpotTypeShort, selectSpot, goToReservation, goBack }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1100px; margin: 0 auto; width: 100%; padding: 32px 24px 80px; }
.back-btn { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--border); background: white; font-size: 14px; color: var(--text-secondary); cursor: pointer; transition: var(--transition); margin-bottom: 24px; }
.back-btn:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-bg); }

.lot-info-bar { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 20px 24px; display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; }
.lot-info-left { display: flex; align-items: center; gap: 14px; }
.lot-info-icon { width: 44px; height: 44px; border-radius: 12px; background: linear-gradient(135deg, var(--primary), var(--accent)); display: flex; align-items: center; justify-content: center; }
.lot-info-name { font-size: 16px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.lot-info-stats { display: flex; align-items: center; gap: 6px; font-size: 13px; }
.stat-avail { color: var(--success); font-weight: 600; }
.stat-sep { color: var(--text-muted); }
.stat-total { color: var(--text-muted); }
.spot-legend { display: flex; gap: 16px; flex-wrap: wrap; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); }
.legend-dot { width: 10px; height: 10px; border-radius: 3px; }
.legend-dot.available { background: #10b981; }
.legend-dot.booked { background: #f59e0b; }
.legend-dot.occupied { background: #ef4444; }
.legend-dot.maintenance { background: #94a3b8; }

.spots-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 24px; }
.spots-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(90px, 1fr)); gap: 10px; }

.spot-cell { height: 80px; border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: var(--transition); border: 2px solid transparent; user-select: none; }
.spot-cell.available { background: #f0fdf4; border-color: #86efac; }
.spot-cell.available:hover { background: #dcfce7; border-color: #4ade80; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(16,185,129,0.2); }
.spot-cell.booked { background: #fffbeb; border-color: #fcd34d; cursor: not-allowed; }
.spot-cell.occupied { background: #fef2f2; border-color: #fca5a5; cursor: not-allowed; }
.spot-cell.maintenance { background: #f8fafc; border-color: #cbd5e1; cursor: not-allowed; opacity: 0.6; }
.spot-cell.selected { border-color: var(--primary) !important; background: var(--primary-bg) !important; box-shadow: 0 0 0 3px rgba(37,99,235,0.15); }
.spot-number { font-size: 13px; font-weight: 700; color: var(--text-primary); }
.spot-type-label { font-size: 11px; color: var(--text-muted); margin-top: 2px; }

.selected-panel { position: fixed; bottom: 0; left: 0; right: 0; z-index: 50; padding: 0 24px 24px; }
.selected-panel-inner { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; border: 1px solid var(--border); box-shadow: var(--shadow-lg); padding: 16px 20px; display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.selected-info { display: flex; align-items: center; gap: 12px; }
.selected-badge { background: var(--primary-bg); color: var(--primary); font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 100px; }
.selected-number { font-size: 18px; font-weight: 800; color: var(--text-primary); }
.selected-meta { font-size: 13px; color: var(--text-muted); }
.btn-reserve { display: flex; align-items: center; gap: 8px; padding: 12px 24px; border-radius: 12px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 14px; font-weight: 700; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(37,99,235,0.3); transition: var(--transition); white-space: nowrap; }
.btn-reserve:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(37,99,235,0.4); }

.slide-up-enter-active, .slide-up-leave-active { transition: all 0.3s cubic-bezier(0.4,0,0.2,1); }
.slide-up-enter-from, .slide-up-leave-to { transform: translateY(100%); opacity: 0; }

@media (max-width: 600px) { .spots-grid { grid-template-columns: repeat(auto-fill, minmax(72px, 1fr)); } .spot-cell { height: 68px; } }
</style>
