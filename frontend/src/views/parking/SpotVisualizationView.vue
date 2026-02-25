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
                <span class="stat-avail">{{ parkingSpots.length }} 个空位</span>
                <span class="stat-sep">·</span>
                <span class="stat-total">共 {{ totalSpots }} 个</span>
              </div>
            </div>
          </div>
          <div class="spot-legend">
            <div class="legend-item"><span class="legend-dot available"></span>可用（点击选择）</div>
            <div class="legend-item"><span class="legend-dot selected-dot"></span>已选中</div>
          </div>
        </div>

        <!-- Zone filter -->
        <div class="zone-filter-bar">
          <span class="zone-label">选择区域：</span>
          <button
            class="zone-btn"
            :class="{ active: selectedZone === '' }"
            @click="filterZone('')"
          >全部</button>
          <button
            v-for="z in zones"
            :key="z"
            class="zone-btn"
            :class="{ active: selectedZone === z }"
            @click="filterZone(z)"
          >{{ z }} 区</button>
        </div>

        <!-- Spots grid -->
        <div class="spots-card" v-loading="loading">
          <div v-if="!loading && parkingSpots.length === 0" class="empty-spots">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
            <p>该区域暂无可用车位</p>
          </div>
          <div v-else class="spots-grid">
            <div
              v-for="spot in parkingSpots"
              :key="spot.id"
              class="spot-cell available"
              :class="{ selected: selectedSpot?.id === spot.id }"
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
import { getParkingLotById, getAvailableSpots, getParkingZones } from '@/api/parking'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'SpotVisualizationView',
  components: { HeaderNav },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const parkingLotName = ref('停车场')
    const totalSpots = ref(0)
    const parkingSpots = ref([])
    const zones = ref([])
    const selectedZone = ref('')
    const selectedSpot = ref(null)
    const loading = ref(false)

    const getSpotTypeText = (t) => ({ 1: '小型车位', 2: '大型车位', 3: '残疾人专位' }[t] || '标准')
    const getSpotTypeShort = (t) => ({ 1: '小', 2: '大', 3: '♿' }[t] || '标')

    const fetchSpots = async (zone) => {
      loading.value = true
      selectedSpot.value = null
      try {
        const res = await getAvailableSpots(route.params.parkingLotId, zone)
        if (res.success) parkingSpots.value = res.data
      } finally { loading.value = false }
    }

    const filterZone = (zone) => {
      selectedZone.value = zone
      fetchSpots(zone)
    }

    const selectSpot = (spot) => {
      selectedSpot.value = selectedSpot.value?.id === spot.id ? null : spot
    }

    const goToReservation = () => {
      if (!selectedSpot.value) return
      router.push({ name: 'CreateReservation', query: { parkingLotId: route.params.parkingLotId, spotId: selectedSpot.value.id, spotNumber: selectedSpot.value.spotNumber } })
    }

    const goBack = () => router.go(-1)

    onMounted(async () => {
      try {
        const [lotRes, zonesRes] = await Promise.all([
          getParkingLotById(route.params.parkingLotId),
          getParkingZones(route.params.parkingLotId)
        ])
        if (lotRes.success) { parkingLotName.value = lotRes.data.name; totalSpots.value = lotRes.data.totalSpots }
        if (zonesRes.success) zones.value = zonesRes.data
      } catch (e) {}
      fetchSpots('')
    })

    return { parkingLotName, totalSpots, parkingSpots, zones, selectedZone, selectedSpot, loading, getSpotTypeText, getSpotTypeShort, filterZone, selectSpot, goToReservation, goBack }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1100px; margin: 0 auto; width: 100%; padding: 32px 24px 80px; }
.back-btn { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--border); background: white; font-size: 14px; color: var(--text-secondary); cursor: pointer; transition: var(--transition); margin-bottom: 24px; }
.back-btn:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-bg); }

.lot-info-bar { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 20px 24px; display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 16px; }
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
.legend-dot.selected-dot { background: var(--primary); }

.zone-filter-bar { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 14px 20px; margin-bottom: 16px; }
.zone-label { font-size: 13px; color: var(--text-muted); font-weight: 500; }
.zone-btn { padding: 6px 16px; border-radius: 8px; border: 1.5px solid var(--border); background: white; font-size: 13px; font-weight: 600; color: var(--text-secondary); cursor: pointer; transition: var(--transition); }
.zone-btn:hover { border-color: var(--primary); color: var(--primary); background: var(--primary-bg); }
.zone-btn.active { border-color: var(--primary); background: var(--primary); color: white; }

.spots-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 24px; min-height: 200px; }
.spots-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(90px, 1fr)); gap: 10px; }
.empty-spots { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; color: var(--text-muted); gap: 12px; font-size: 14px; }

.spot-cell { height: 80px; border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: var(--transition); border: 2px solid transparent; user-select: none; }
.spot-cell.available { background: #f0fdf4; border-color: #86efac; }
.spot-cell.available:hover { background: #dcfce7; border-color: #4ade80; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(16,185,129,0.2); }
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
