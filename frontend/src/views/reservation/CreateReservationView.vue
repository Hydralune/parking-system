<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-content">
      <button class="back-btn" @click="goBack">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
        返回
      </button>

      <div class="reservation-wrap">
        <div class="reservation-main">
          <div class="form-card">
            <h2 class="form-title">创建预约</h2>
            <p class="form-desc">填写预约信息，确认后完成停车位预订</p>

            <el-form :model="reservationForm" :rules="reservationRules" ref="reservationFormRef" label-position="top" class="res-form">
              <div class="form-row">
                <el-form-item label="停车场">
                  <el-input v-model="reservationForm.parkingLotName" disabled size="large" />
                </el-form-item>
                <el-form-item label="车位编号">
                  <el-input v-model="reservationForm.spotNumber" disabled size="large" />
                </el-form-item>
              </div>

              <el-form-item label="预约时间段" prop="reservationTime">
                <el-date-picker
                  v-model="reservationForm.reservationTime"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  format="YYYY-MM-DD HH:mm"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  size="large"
                  style="width:100%"
                  :default-time="[new Date(2000,1,1,8,0), new Date(2000,1,1,18,0)]"
                />
              </el-form-item>

              <el-form-item label="车牌号" prop="licensePlate">
                <el-input v-model="reservationForm.licensePlate" placeholder="例：京A12345" size="large" maxlength="8" />
              </el-form-item>

              <div class="form-actions">
                <button class="btn-cancel" @click.prevent="goBack">取消</button>
                <button class="btn-confirm" @click.prevent="handleCreateReservation" :disabled="loading">
                  {{ loading ? '提交中...' : '确认预约' }}
                </button>
              </div>
            </el-form>
          </div>
        </div>

        <!-- Fee summary sidebar -->
        <div class="fee-sidebar">
          <div class="fee-card">
            <h3 class="fee-title">费用预估</h3>
            <div class="fee-rows">
              <div class="fee-row">
                <span class="fee-label">停车时长</span>
                <span class="fee-val">{{ durationText }}</span>
              </div>
              <div class="fee-row">
                <span class="fee-label">基础费用</span>
                <span class="fee-val">¥{{ calculatedFee }}</span>
              </div>
              <div class="fee-row discount" v-if="memberDiscount < 1">
                <span class="fee-label">会员折扣</span>
                <span class="fee-val green">{{ (memberDiscount * 100).toFixed(0) }}%</span>
              </div>
              <div class="fee-divider"></div>
              <div class="fee-row total">
                <span class="fee-label">预计总费用</span>
                <span class="fee-val total-val">¥{{ finalFee }}</span>
              </div>
            </div>
            <div class="fee-note">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              实际费用以离场时计算为准
            </div>
          </div>

          <div class="tips-card">
            <h4 class="tips-title">预约须知</h4>
            <ul class="tips-list">
              <li>请在预约时间内到达停车场</li>
              <li>超时将按实际停车时长计费</li>
              <li>取消预约请提前30分钟操作</li>
              <li>会员享受专属折扣优惠</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createReservation } from '@/api/reservation'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'CreateReservationView',
  components: { HeaderNav },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const reservationFormRef = ref(null)
    const loading = ref(false)
    const memberDiscount = ref(1.0)
    const hourlyRate = ref(5.0)

    const reservationForm = reactive({ parkingLotName: '中央商务区停车场', spotNumber: 'A001', reservationTime: [], licensePlate: '' })

    const reservationRules = {
      reservationTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
      licensePlate: [{ required: true, message: '请输入车牌号', trigger: 'blur' }]
    }

    const durationText = computed(() => {
      if (!reservationForm.reservationTime?.length) return '-'
      const diff = new Date(reservationForm.reservationTime[1]) - new Date(reservationForm.reservationTime[0])
      const h = Math.ceil(diff / 3600000)
      return h > 0 ? `${h} 小时` : '-'
    })

    const calculatedFee = computed(() => {
      if (!reservationForm.reservationTime?.length) return '0.00'
      const diff = new Date(reservationForm.reservationTime[1]) - new Date(reservationForm.reservationTime[0])
      const h = Math.ceil(diff / 3600000)
      return (h * hourlyRate.value).toFixed(2)
    })

    const finalFee = computed(() => (parseFloat(calculatedFee.value) * memberDiscount.value).toFixed(2))

    const handleCreateReservation = async () => {
      if (!reservationFormRef.value) return
      await reservationFormRef.value.validate(async (valid) => {
        if (!valid) return
        loading.value = true
        try {
          const res = await createReservation({
            parkingLotId: route.query.parkingLotId,
            parkingSpotId: route.query.spotId,
            startTime: reservationForm.reservationTime[0],
            endTime: reservationForm.reservationTime[1],
            licensePlate: reservationForm.licensePlate,
            totalFee: parseFloat(finalFee.value)
          })
          if (res.success) { ElMessage.success('预约创建成功！'); router.push('/reservations') }
          else ElMessage.error(res.message || '预约创建失败')
        } catch (e) { ElMessage.error('预约创建失败：' + e.message) }
        finally { loading.value = false }
      })
    }

    const goBack = () => router.go(-1)

    onMounted(() => {
      if (route.query.startTime && route.query.endTime) reservationForm.reservationTime = [route.query.startTime, route.query.endTime]
    })

    return { reservationFormRef, reservationForm, reservationRules, loading, memberDiscount, calculatedFee, finalFee, durationText, handleCreateReservation, goBack }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1100px; margin: 0 auto; width: 100%; padding: 32px 24px 60px; }

.back-btn { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--border); background: white; font-size: 14px; color: var(--text-secondary); cursor: pointer; transition: var(--transition); margin-bottom: 28px; }
.back-btn:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-bg); }

.reservation-wrap { display: flex; gap: 24px; align-items: flex-start; }
.reservation-main { flex: 1; min-width: 0; }

.form-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 32px; }
.form-title { font-size: 1.4rem; font-weight: 800; color: var(--text-primary); margin-bottom: 6px; }
.form-desc { font-size: 14px; color: var(--text-muted); margin-bottom: 28px; }

.res-form :deep(.el-form-item__label) { font-size: 13px; font-weight: 600; color: var(--text-secondary); margin-bottom: 6px; }
.res-form :deep(.el-input__wrapper) { border-radius: 10px !important; box-shadow: 0 0 0 1px var(--border) !important; }
.res-form :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px var(--primary-light) !important; }
.res-form :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px rgba(37,99,235,0.2) !important; }
.res-form :deep(.el-date-editor .el-range-input) { font-size: 14px; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.form-actions { display: flex; gap: 12px; margin-top: 8px; }
.btn-cancel { flex: 1; padding: 13px; border-radius: 10px; border: 1px solid var(--border); background: white; font-size: 14px; font-weight: 600; color: var(--text-secondary); cursor: pointer; transition: var(--transition); }
.btn-cancel:hover { border-color: var(--primary); color: var(--primary); background: var(--primary-bg); }
.btn-confirm { flex: 2; padding: 13px; border-radius: 10px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 14px; font-weight: 700; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(37,99,235,0.3); transition: var(--transition); }
.btn-confirm:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(37,99,235,0.4); }
.btn-confirm:disabled { opacity: 0.6; cursor: not-allowed; }

/* Fee sidebar */
.fee-sidebar { width: 280px; flex-shrink: 0; display: flex; flex-direction: column; gap: 16px; }
.fee-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 24px; }
.fee-title { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 20px; }
.fee-rows { display: flex; flex-direction: column; gap: 12px; }
.fee-row { display: flex; justify-content: space-between; align-items: center; }
.fee-label { font-size: 13px; color: var(--text-secondary); }
.fee-val { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.fee-val.green { color: var(--success); }
.fee-divider { height: 1px; background: var(--border); margin: 4px 0; }
.fee-row.total .fee-label { font-weight: 700; color: var(--text-primary); }
.total-val { font-size: 1.4rem; font-weight: 800; color: var(--primary); }
.fee-note { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--text-muted); margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border); }

.tips-card { background: var(--primary-bg); border-radius: var(--radius-lg); border: 1px solid rgba(37,99,235,0.15); padding: 20px; }
.tips-title { font-size: 13px; font-weight: 700; color: var(--primary); margin-bottom: 12px; }
.tips-list { list-style: none; display: flex; flex-direction: column; gap: 8px; }
.tips-list li { font-size: 13px; color: var(--text-secondary); padding-left: 16px; position: relative; }
.tips-list li::before { content: '·'; position: absolute; left: 4px; color: var(--primary); font-weight: 700; }

@media (max-width: 768px) {
  .reservation-wrap { flex-direction: column; }
  .fee-sidebar { width: 100%; }
  .form-row { grid-template-columns: 1fr; }
}
</style>
