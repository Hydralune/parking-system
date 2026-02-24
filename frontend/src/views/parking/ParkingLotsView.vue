<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-body">
      <!-- Page header -->
      <div class="page-hero">
        <div class="page-hero-inner">
          <h1 class="page-hero-title">停车场列表</h1>
          <p class="page-hero-desc">查找附近停车场，实时了解车位状态</p>
          <div class="search-bar">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
            <input v-model="searchForm.keyword" placeholder="搜索停车场名称或地址..." @keyup.enter="handleSearch" class="search-input" />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>

      <div class="content-area">
        <!-- Loading skeleton -->
        <div v-if="loading" class="lots-grid">
          <div v-for="i in 6" :key="i" class="lot-card skeleton-card">
            <div class="skeleton-header"></div>
            <div class="skeleton-body">
              <div class="skeleton-line"></div>
              <div class="skeleton-line short"></div>
              <div class="skeleton-line"></div>
            </div>
          </div>
        </div>

        <!-- Lots grid -->
        <div v-else-if="parkingLots.length" class="lots-grid">
          <div
            v-for="(lot, i) in parkingLots"
            :key="lot.id"
            class="lot-card"
            :style="`animation-delay:${i*0.06}s`"
          >
            <div class="lot-card-header">
              <div class="lot-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
              </div>
              <el-tag :type="lot.status === 1 ? 'success' : 'danger'" size="small" round>
                {{ lot.status === 1 ? '营业中' : '已关闭' }}
              </el-tag>
            </div>
            <h3 class="lot-name">{{ lot.name }}</h3>
            <div class="lot-meta">
              <div class="lot-meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/><circle cx="12" cy="10" r="3"/></svg>
                {{ lot.address }}
              </div>
              <div class="lot-meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                {{ lot.openingTime }} - {{ lot.closingTime }}
              </div>
            </div>
            <div class="lot-spots">
              <div class="spots-bar">
                <div class="spots-fill" :style="`width:${lot.totalSpots ? (lot.availableSpots/lot.totalSpots*100) : 0}%`"></div>
              </div>
              <div class="spots-text">
                <span class="spots-available">{{ lot.availableSpots }} 空位</span>
                <span class="spots-total">共 {{ lot.totalSpots }}</span>
              </div>
            </div>
            <div class="lot-price">
              <span class="price-value">¥{{ lot.hourlyRate }}</span>
              <span class="price-unit">/小时</span>
            </div>
            <div class="lot-actions">
              <button class="btn-outline" @click="goToDetail(lot.id)">查看详情</button>
              <button class="btn-primary-sm" @click="goToSpots(lot.id)" :disabled="lot.status !== 1">选择车位</button>
            </div>
          </div>
        </div>

        <!-- Empty -->
        <div v-else class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" stroke-width="1.5"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
          <p>暂无停车场数据</p>
        </div>

        <!-- Pagination -->
        <div class="pagination-wrap" v-if="pagination.total > pagination.size">
          <el-pagination
            :current-page="pagination.current"
            :page-size="pagination.size"
            :page-sizes="[9, 18, 36]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getParkingLots } from '@/api/parking'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'ParkingLotsView',
  components: { HeaderNav },
  setup() {
    const router = useRouter()
    const searchForm = reactive({ keyword: '' })
    const parkingLots = ref([])
    const pagination = reactive({ current: 1, size: 9, total: 0 })
    const loading = ref(false)

    const fetchParkingLots = async () => {
      loading.value = true
      try {
        const response = await getParkingLots({ current: pagination.current, size: pagination.size, keyword: searchForm.keyword })
        if (response.success) {
          parkingLots.value = response.data.records || response.data
          pagination.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取停车场列表失败')
        }
      } catch (e) {
        ElMessage.error('获取停车场列表失败：' + e.message)
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => { pagination.current = 1; fetchParkingLots() }
    const handleSizeChange = (s) => { pagination.size = s; fetchParkingLots() }
    const handleCurrentChange = (c) => { pagination.current = c; fetchParkingLots() }
    const goToDetail = (id) => router.push(`/parking-lots/${id}`)
    const goToSpots = (id) => router.push(`/spots/${id}`)

    onMounted(fetchParkingLots)
    return { searchForm, parkingLots, pagination, loading, handleSearch, handleSizeChange, handleCurrentChange, goToDetail, goToSpots }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-body { flex: 1; }

.page-hero {
  background: linear-gradient(135deg, var(--primary-dark) 0%, var(--primary) 60%, var(--accent) 100%);
  padding: 60px 24px 80px;
}
.page-hero-inner { max-width: 800px; margin: 0 auto; text-align: center; }
.page-hero-title { font-size: 2.2rem; font-weight: 800; color: white; margin-bottom: 10px; }
.page-hero-desc { font-size: 16px; color: rgba(255,255,255,0.8); margin-bottom: 32px; }

.search-bar {
  display: flex; align-items: center; gap: 12px;
  background: white; border-radius: 14px; padding: 10px 10px 10px 18px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15); max-width: 560px; margin: 0 auto;
}
.search-input { flex: 1; border: none; outline: none; font-size: 15px; color: var(--text-primary); background: transparent; }
.search-input::placeholder { color: var(--text-muted); }
.search-btn { padding: 10px 24px; border-radius: 10px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 14px; font-weight: 600; border: none; cursor: pointer; transition: var(--transition); white-space: nowrap; }
.search-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(37,99,235,0.4); }

.content-area { max-width: 1280px; margin: -32px auto 0; padding: 0 24px 60px; }

.lots-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }

.lot-card {
  background: white; border-radius: var(--radius-lg); padding: 24px;
  border: 1px solid var(--border); transition: var(--transition);
  animation: fadeInUp 0.4s ease both;
}
.lot-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); border-color: rgba(37,99,235,0.2); }

.lot-card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.lot-icon { width: 44px; height: 44px; border-radius: 12px; background: linear-gradient(135deg, var(--primary), var(--accent)); display: flex; align-items: center; justify-content: center; }
.lot-name { font-size: 17px; font-weight: 700; color: var(--text-primary); margin-bottom: 12px; }
.lot-meta { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.lot-meta-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); }

.lot-spots { margin-bottom: 16px; }
.spots-bar { height: 6px; background: #f1f5f9; border-radius: 3px; overflow: hidden; margin-bottom: 6px; }
.spots-fill { height: 100%; background: linear-gradient(90deg, var(--primary), var(--accent)); border-radius: 3px; transition: width 0.6s ease; }
.spots-text { display: flex; justify-content: space-between; font-size: 12px; }
.spots-available { color: var(--success); font-weight: 600; }
.spots-total { color: var(--text-muted); }

.lot-price { margin-bottom: 20px; }
.price-value { font-size: 1.6rem; font-weight: 800; color: var(--primary); }
.price-unit { font-size: 13px; color: var(--text-muted); margin-left: 2px; }

.lot-actions { display: flex; gap: 10px; }
.btn-outline { flex: 1; padding: 9px; border-radius: 9px; border: 1px solid var(--border); background: none; font-size: 13px; font-weight: 600; color: var(--text-secondary); cursor: pointer; transition: var(--transition); }
.btn-outline:hover { border-color: var(--primary); color: var(--primary); background: var(--primary-bg); }
.btn-primary-sm { flex: 1; padding: 9px; border-radius: 9px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 13px; font-weight: 600; border: none; cursor: pointer; transition: var(--transition); }
.btn-primary-sm:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(37,99,235,0.35); }
.btn-primary-sm:disabled { opacity: 0.5; cursor: not-allowed; }

/* Skeleton */
.skeleton-card { animation: none !important; }
.skeleton-header { height: 44px; background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 8px; margin-bottom: 16px; }
.skeleton-line { height: 14px; background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 4px; margin-bottom: 10px; }
.skeleton-line.short { width: 60%; }
.skeleton-body { padding: 0; }

.empty-state { text-align: center; padding: 80px 20px; color: var(--text-muted); }
.empty-state p { margin-top: 16px; font-size: 15px; }

.pagination-wrap { display: flex; justify-content: center; margin-top: 40px; }

@media (max-width: 1024px) { .lots-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px) { .lots-grid { grid-template-columns: 1fr; } .page-hero { padding: 40px 20px 60px; } }
</style>
