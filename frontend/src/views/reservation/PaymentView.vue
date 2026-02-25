<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-content">
      <button class="back-btn" @click="goBack">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
        返回
      </button>

      <div class="payment-wrap">
        <div class="payment-main">
          <!-- Order info -->
          <div class="order-card">
            <h3 class="section-title">订单信息</h3>
            <div class="order-rows">
              <div class="order-row">
                <span class="order-label">订单编号</span>
                <span class="order-val mono">{{ orderInfo.orderId }}</span>
              </div>
              <div class="order-row">
                <span class="order-label">停车场</span>
                <span class="order-val">{{ orderInfo.parkingLotName }}</span>
              </div>
              <div class="order-row">
                <span class="order-label">车位编号</span>
                <span class="order-val">{{ orderInfo.spotNumber }}</span>
              </div>
              <div class="order-row">
                <span class="order-label">预约时间</span>
                <span class="order-val">{{ orderInfo.startTime }} → {{ orderInfo.endTime }}</span>
              </div>
              <div class="order-row">
                <span class="order-label">车牌号</span>
                <span class="order-val">{{ orderInfo.licensePlate }}</span>
              </div>
              <div class="order-divider"></div>
              <div class="order-row total">
                <span class="order-label">订单金额</span>
                <span class="order-amount">¥{{ orderInfo.totalFee }}</span>
              </div>
            </div>
          </div>

          <!-- Payment method -->
          <div class="method-card">
            <h3 class="section-title">选择支付方式</h3>
            <div class="method-grid">
              <div
                class="method-option"
                :class="{ selected: selectedPaymentMethod === 'alipay' }"
                @click="selectedPaymentMethod = 'alipay'"
              >
                <div class="method-icon alipay">
                  <svg width="28" height="28" viewBox="0 0 100 100" fill="none">
                    <rect width="100" height="100" rx="20" fill="#1296db"/>
                    <text x="50" y="68" text-anchor="middle" font-size="52" font-weight="bold" fill="white">支</text>
                  </svg>
                </div>
                <div class="method-info">
                  <div class="method-name">支付宝</div>
                  <div class="method-desc">推荐使用</div>
                </div>
                <div class="method-check" v-if="selectedPaymentMethod === 'alipay'">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>
                </div>
              </div>

              <div
                class="method-option"
                :class="{ selected: selectedPaymentMethod === 'wechat' }"
                @click="selectedPaymentMethod = 'wechat'"
              >
                <div class="method-icon wechat">
                  <svg width="28" height="28" viewBox="0 0 100 100" fill="none">
                    <rect width="100" height="100" rx="20" fill="#07c160"/>
                    <text x="50" y="68" text-anchor="middle" font-size="52" font-weight="bold" fill="white">微</text>
                  </svg>
                </div>
                <div class="method-info">
                  <div class="method-name">微信支付</div>
                  <div class="method-desc">安全便捷</div>
                </div>
                <div class="method-check" v-if="selectedPaymentMethod === 'wechat'">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Pay sidebar -->
        <div class="pay-sidebar">
          <div class="pay-summary">
            <div class="pay-amount-label">应付金额</div>
            <div class="pay-amount">¥{{ orderInfo.totalFee }}</div>
            <div class="pay-method-tag">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
              {{ selectedPaymentMethod === 'alipay' ? '支付宝' : '微信支付' }}
            </div>
            <button class="btn-pay" @click="handlePayment" :disabled="loading">
              <span v-if="!loading">立即支付</span>
              <span v-else class="paying">支付中...</span>
            </button>
            <div class="pay-secure">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              安全加密支付
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { payReservation } from '@/api/reservation'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'PaymentView',
  components: { HeaderNav },
  setup() {
    const router = useRouter()
    const selectedPaymentMethod = ref('alipay')
    const loading = ref(false)

    // 从路由 state 读取真实预约数据
    const raw = window.history.state?.reservation
    const reservation = raw ? JSON.parse(raw) : null

    if (!reservation) {
      ElMessage.error('订单信息丢失，请重新进入')
      router.replace('/reservations')
    }

    const orderInfo = reactive({
      orderId: reservation ? `ORD${reservation.id}` : '-',
      reservationId: reservation?.id,
      parkingLotName: reservation?.parkingLotName || '停车场',
      spotNumber: reservation?.spotNumber || '-',
      startTime: reservation?.startTime || '-',
      endTime: reservation?.endTime || '-',
      licensePlate: reservation?.licensePlate || '-',
      totalFee: reservation?.totalFee || '0.00'
    })

    const handlePayment = async () => {
      if (!selectedPaymentMethod.value) { ElMessage.warning('请选择支付方式'); return }
      loading.value = true
      try {
        // 模拟支付延迟
        await new Promise(r => setTimeout(r, 1200))
        const res = await payReservation(orderInfo.reservationId)
        if (res.success) {
          ElMessage.success('支付成功！')
          router.replace('/reservations')
        } else {
          ElMessage.error(res.message || '支付失败')
        }
      } catch (e) {
        ElMessage.error('支付失败：' + e.message)
      } finally { loading.value = false }
    }

    const goBack = () => router.go(-1)
    return { selectedPaymentMethod, loading, orderInfo, handlePayment, goBack }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1000px; margin: 0 auto; width: 100%; padding: 32px 24px 60px; }

.back-btn { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--border); background: white; font-size: 14px; color: var(--text-secondary); cursor: pointer; transition: var(--transition); margin-bottom: 28px; }
.back-btn:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-bg); }

.payment-wrap { display: flex; gap: 24px; align-items: flex-start; }
.payment-main { flex: 1; display: flex; flex-direction: column; gap: 20px; min-width: 0; }

.order-card, .method-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 24px; }
.section-title { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border); }

.order-rows { display: flex; flex-direction: column; gap: 12px; }
.order-row { display: flex; justify-content: space-between; align-items: center; }
.order-label { font-size: 13px; color: var(--text-muted); }
.order-val { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.order-val.mono { font-family: monospace; font-size: 13px; }
.order-divider { height: 1px; background: var(--border); margin: 4px 0; }
.order-row.total .order-label { font-size: 15px; font-weight: 700; color: var(--text-primary); }
.order-amount { font-size: 1.6rem; font-weight: 800; color: var(--primary); }

.method-grid { display: flex; flex-direction: column; gap: 12px; }
.method-option { display: flex; align-items: center; gap: 16px; padding: 16px; border-radius: 12px; border: 2px solid var(--border); cursor: pointer; transition: var(--transition); }
.method-option:hover { border-color: var(--primary-light); background: var(--primary-bg); }
.method-option.selected { border-color: var(--primary); background: var(--primary-bg); }
.method-icon { width: 44px; height: 44px; border-radius: 12px; overflow: hidden; flex-shrink: 0; }
.method-info { flex: 1; }
.method-name { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 2px; }
.method-desc { font-size: 12px; color: var(--text-muted); }
.method-check { width: 24px; height: 24px; border-radius: 50%; background: var(--primary); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }

.pay-sidebar { width: 240px; flex-shrink: 0; }
.pay-summary { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 24px; text-align: center; position: sticky; top: 80px; }
.pay-amount-label { font-size: 13px; color: var(--text-muted); margin-bottom: 8px; }
.pay-amount { font-size: 2.4rem; font-weight: 800; color: var(--primary); margin-bottom: 12px; }
.pay-method-tag { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); background: var(--bg); padding: 5px 12px; border-radius: 100px; margin-bottom: 20px; }
.btn-pay { width: 100%; padding: 14px; border-radius: 12px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 15px; font-weight: 700; border: none; cursor: pointer; box-shadow: 0 6px 20px rgba(37,99,235,0.35); transition: var(--transition); margin-bottom: 12px; }
.btn-pay:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 10px 28px rgba(37,99,235,0.45); }
.btn-pay:disabled { opacity: 0.6; cursor: not-allowed; }
.pay-secure { display: flex; align-items: center; justify-content: center; gap: 5px; font-size: 12px; color: var(--text-muted); }

@media (max-width: 768px) {
  .payment-wrap { flex-direction: column; }
  .pay-sidebar { width: 100%; }
}
</style>
