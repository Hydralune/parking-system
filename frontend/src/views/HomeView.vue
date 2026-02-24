<template>
  <div class="home">
    <HeaderNav />

    <!-- Hero -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-blob blob1"></div>
        <div class="hero-blob blob2"></div>
        <div class="hero-grid"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge animate-fade-up">
          <span class="badge-dot"></span>
          智能停车管理平台
        </div>
        <h1 class="hero-title animate-fade-up" style="animation-delay:0.1s">
          找车位，就用<br/>
          <span class="gradient-text">智停系统</span>
        </h1>
        <p class="hero-desc animate-fade-up" style="animation-delay:0.2s">
          实时车位查询 · 在线预约 · 智能计费 · 会员优惠<br/>让停车变得简单高效
        </p>
        <div class="hero-actions animate-fade-up" style="animation-delay:0.3s">
          <button class="btn-hero-primary" @click="goToParkingLots">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
            查找停车场
          </button>
          <button class="btn-hero-ghost" @click="goToLogin" v-if="!userStore.isAuthenticated">
            立即注册
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </button>
        </div>
        <div class="hero-stats animate-fade-up" style="animation-delay:0.4s">
          <div class="hero-stat" v-for="s in stats" :key="s.label">
            <span class="hero-stat-value">{{ s.value }}</span>
            <span class="hero-stat-label">{{ s.label }}</span>
          </div>
        </div>
      </div>
      <div class="hero-visual animate-fade" style="animation-delay:0.3s">
        <div class="parking-card-demo">
          <div class="demo-header">
            <div class="demo-dot red"></div>
            <div class="demo-dot yellow"></div>
            <div class="demo-dot green"></div>
            <span>实时车位状态</span>
          </div>
          <div class="demo-grid">
            <div v-for="(spot, i) in demoSpots" :key="i" class="demo-spot" :class="spot"></div>
          </div>
          <div class="demo-legend">
            <span><i class="legend-dot available"></i>空闲</span>
            <span><i class="legend-dot occupied"></i>占用</span>
            <span><i class="legend-dot reserved"></i>预约</span>
          </div>
          <div class="demo-info">
            <div class="demo-info-item">
              <span class="demo-info-label">剩余车位</span>
              <span class="demo-info-value">24</span>
            </div>
            <div class="demo-info-item">
              <span class="demo-info-label">每小时</span>
              <span class="demo-info-value">¥5</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-tag">核心功能</div>
          <h2 class="section-title">为什么选择智停</h2>
          <p class="section-desc">全方位的智能停车解决方案，让每次停车都轻松愉快</p>
        </div>
        <div class="features-grid">
          <div class="feature-card" v-for="(f, i) in features" :key="f.title" :style="`animation-delay:${i*0.1}s`">
            <div class="feature-icon-wrap" :style="`background:${f.bg}`">
              <component :is="f.icon" style="width:24px;height:24px;color:white" />
            </div>
            <h3 class="feature-title">{{ f.title }}</h3>
            <p class="feature-desc">{{ f.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats banner -->
    <section class="stats-banner">
      <div class="section-inner">
        <div class="stats-grid">
          <div class="stat-item" v-for="s in bigStats" :key="s.label">
            <div class="stat-number">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section" v-if="!userStore.isAuthenticated">
      <div class="section-inner">
        <div class="cta-card">
          <div class="cta-blob"></div>
          <h2 class="cta-title">立即开始使用</h2>
          <p class="cta-desc">注册账号，享受智能停车全套服务</p>
          <div class="cta-actions">
            <button class="btn-cta-primary" @click="goToRegister">免费注册</button>
            <button class="btn-cta-ghost" @click="goToParkingLots">浏览停车场</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="section-inner">
        <div class="footer-inner">
          <div class="footer-brand">
            <div class="footer-logo">
              <div class="logo-icon-sm"></div>
              <span>智停系统</span>
            </div>
            <p>智能停车管理平台，让停车更简单</p>
          </div>
          <div class="footer-links">
            <div class="footer-col">
              <h4>产品</h4>
              <router-link to="/parking-lots">停车场</router-link>
              <router-link to="/reservations" v-if="userStore.isAuthenticated">我的预约</router-link>
            </div>
            <div class="footer-col">
              <h4>账户</h4>
              <router-link to="/login" v-if="!userStore.isAuthenticated">登录</router-link>
              <router-link to="/register" v-if="!userStore.isAuthenticated">注册</router-link>
              <router-link to="/profile" v-if="userStore.isAuthenticated">个人资料</router-link>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <span>© 2024 智停系统. All rights reserved.</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import HeaderNav from '@/components/HeaderNav.vue'
import { Location, Clock, Money, User, Star, Lock } from '@element-plus/icons-vue'

export default {
  name: 'HomeView',
  components: { HeaderNav, Location, Clock, Money, User, Star, Lock },
  setup() {
    const router = useRouter()
    const userStore = useUserStore()

    const stats = ref([
      { value: '50+', label: '停车场' },
      { value: '5000+', label: '停车位' },
      { value: '10000+', label: '用户' },
      { value: '24/7', label: '服务' },
    ])

    const bigStats = ref([
      { value: '50+', label: '合作停车场' },
      { value: '5,000+', label: '可用车位' },
      { value: '10,000+', label: '注册用户' },
      { value: '99.9%', label: '服务可用率' },
    ])

    const features = ref([
      { title: '实时车位查询', description: '实时掌握各停车场车位状态，告别盲目寻位', icon: 'Location', bg: 'linear-gradient(135deg,#2563eb,#0ea5e9)' },
      { title: '在线预约停车', description: '提前锁定车位，到场即停，节省宝贵时间', icon: 'Clock', bg: 'linear-gradient(135deg,#7c3aed,#a855f7)' },
      { title: '智能计费系统', description: '按时计费，透明公正，支持多种支付方式', icon: 'Money', bg: 'linear-gradient(135deg,#059669,#10b981)' },
      { title: '会员专属优惠', description: '四级会员体系，享受专属折扣和积分奖励', icon: 'Star', bg: 'linear-gradient(135deg,#d97706,#f59e0b)' },
      { title: '安全保障', description: '全程监控，车辆安全有保障，放心停放', icon: 'Lock', bg: 'linear-gradient(135deg,#dc2626,#ef4444)' },
      { title: '个人中心', description: '查看历史记录，管理预约，一站式服务', icon: 'User', bg: 'linear-gradient(135deg,#0891b2,#06b6d4)' },
    ])

    const demoSpots = ref([
      'available','available','occupied','available','reserved',
      'occupied','available','available','occupied','available',
      'available','reserved','available','occupied','available',
      'available','available','occupied','available','available',
    ])

    const goToParkingLots = () => router.push('/parking-lots')
    const goToLogin = () => router.push('/login')
    const goToRegister = () => router.push('/register')

    return { userStore, stats, bigStats, features, demoSpots, goToParkingLots, goToLogin, goToRegister }
  }
}
</script>

<style scoped>
.home { min-height: 100vh; display: flex; flex-direction: column; }

/* Hero */
.hero {
  min-height: 88vh;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  padding: 80px 24px 60px;
  gap: 60px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}
.hero-bg { position: fixed; inset: 0; z-index: -1; pointer-events: none; }
.hero-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}
.blob1 { width: 600px; height: 600px; background: var(--primary); top: -200px; right: -100px; animation: float 8s ease-in-out infinite; }
.blob2 { width: 400px; height: 400px; background: var(--accent); bottom: -100px; left: -100px; animation: float 10s ease-in-out infinite reverse; }
.hero-grid {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(37,99,235,0.04) 1px, transparent 1px), linear-gradient(90deg, rgba(37,99,235,0.04) 1px, transparent 1px);
  background-size: 40px 40px;
}
.hero-content { flex: 1; max-width: 560px; }
.hero-badge {
  display: inline-flex; align-items: center; gap: 8px;
  background: var(--primary-bg); color: var(--primary);
  border: 1px solid rgba(37,99,235,0.2);
  padding: 6px 14px; border-radius: 100px;
  font-size: 13px; font-weight: 600; margin-bottom: 24px;
}
.badge-dot { width: 7px; height: 7px; border-radius: 50%; background: var(--primary); animation: pulse 2s infinite; }
.hero-title { font-size: clamp(2.4rem, 5vw, 3.6rem); font-weight: 800; line-height: 1.15; color: var(--text-primary); margin-bottom: 20px; }
.gradient-text { background: linear-gradient(135deg, var(--primary), var(--accent)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.hero-desc { font-size: 16px; color: var(--text-secondary); line-height: 1.8; margin-bottom: 36px; }
.hero-actions { display: flex; gap: 14px; flex-wrap: wrap; margin-bottom: 48px; }
.btn-hero-primary {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 28px; border-radius: 12px;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white; font-size: 15px; font-weight: 600;
  border: none; cursor: pointer;
  box-shadow: 0 8px 24px rgba(37,99,235,0.35);
  transition: var(--transition);
}
.btn-hero-primary:hover { transform: translateY(-2px); box-shadow: 0 12px 32px rgba(37,99,235,0.45); }
.btn-hero-ghost {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 28px; border-radius: 12px;
  background: white; color: var(--text-primary);
  font-size: 15px; font-weight: 600;
  border: 1px solid var(--border); cursor: pointer;
  transition: var(--transition);
}
.btn-hero-ghost:hover { border-color: var(--primary); color: var(--primary); transform: translateY(-2px); box-shadow: var(--shadow); }
.hero-stats { display: flex; gap: 32px; }
.hero-stat { display: flex; flex-direction: column; gap: 2px; }
.hero-stat-value { font-size: 22px; font-weight: 800; color: var(--primary); }
.hero-stat-label { font-size: 12px; color: var(--text-muted); }

/* Demo card */
.hero-visual { flex-shrink: 0; }
.parking-card-demo {
  background: white; border-radius: 20px;
  box-shadow: var(--shadow-lg); padding: 24px;
  width: 320px; border: 1px solid var(--border);
  animation: float 6s ease-in-out infinite;
}
.demo-header { display: flex; align-items: center; gap: 6px; margin-bottom: 20px; font-size: 13px; font-weight: 600; color: var(--text-secondary); }
.demo-dot { width: 10px; height: 10px; border-radius: 50%; }
.demo-dot.red { background: #ef4444; }
.demo-dot.yellow { background: #f59e0b; }
.demo-dot.green { background: #10b981; }
.demo-header span { margin-left: 4px; }
.demo-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; margin-bottom: 16px; }
.demo-spot { height: 36px; border-radius: 6px; transition: var(--transition); }
.demo-spot.available { background: #dcfce7; border: 1px solid #86efac; }
.demo-spot.occupied { background: #fee2e2; border: 1px solid #fca5a5; }
.demo-spot.reserved { background: #dbeafe; border: 1px solid #93c5fd; }
.demo-legend { display: flex; gap: 16px; font-size: 12px; color: var(--text-muted); margin-bottom: 16px; }
.legend-dot { display: inline-block; width: 8px; height: 8px; border-radius: 2px; margin-right: 4px; }
.legend-dot.available { background: #86efac; }
.legend-dot.occupied { background: #fca5a5; }
.legend-dot.reserved { background: #93c5fd; }
.demo-info { display: flex; gap: 16px; }
.demo-info-item { flex: 1; background: var(--bg); border-radius: 10px; padding: 12px; text-align: center; }
.demo-info-label { display: block; font-size: 11px; color: var(--text-muted); margin-bottom: 4px; }
.demo-info-value { font-size: 20px; font-weight: 700; color: var(--primary); }

/* Features */
.features { padding: 100px 24px; background: white; }
.section-inner { max-width: 1280px; margin: 0 auto; }
.section-header { text-align: center; margin-bottom: 60px; }
.section-tag { display: inline-block; background: var(--primary-bg); color: var(--primary); font-size: 13px; font-weight: 600; padding: 5px 14px; border-radius: 100px; margin-bottom: 16px; }
.section-title { font-size: clamp(1.8rem, 3vw, 2.4rem); font-weight: 800; color: var(--text-primary); margin-bottom: 12px; }
.section-desc { font-size: 16px; color: var(--text-secondary); }
.features-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.feature-card {
  padding: 32px; border-radius: var(--radius-lg);
  border: 1px solid var(--border); background: white;
  transition: var(--transition); animation: fadeInUp 0.5s ease both;
}
.feature-card:hover { transform: translateY(-6px); box-shadow: var(--shadow-lg); border-color: rgba(37,99,235,0.2); }
.feature-icon-wrap { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }
.feature-title { font-size: 17px; font-weight: 700; color: var(--text-primary); margin-bottom: 10px; }
.feature-desc { font-size: 14px; color: var(--text-secondary); line-height: 1.7; }

/* Stats banner */
.stats-banner { padding: 80px 24px; background: linear-gradient(135deg, var(--primary) 0%, var(--accent) 100%); }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; text-align: center; }
.stat-number { font-size: 2.8rem; font-weight: 800; color: white; margin-bottom: 8px; }
.stat-label { font-size: 14px; color: rgba(255,255,255,0.75); }

/* CTA */
.cta-section { padding: 100px 24px; background: var(--bg); }
.cta-card {
  background: linear-gradient(135deg, var(--primary-dark), var(--primary));
  border-radius: 24px; padding: 64px; text-align: center;
  position: relative; overflow: hidden;
}
.cta-blob { position: absolute; width: 400px; height: 400px; background: rgba(255,255,255,0.05); border-radius: 50%; top: -200px; right: -100px; }
.cta-title { font-size: 2.2rem; font-weight: 800; color: white; margin-bottom: 12px; }
.cta-desc { font-size: 16px; color: rgba(255,255,255,0.8); margin-bottom: 36px; }
.cta-actions { display: flex; gap: 16px; justify-content: center; }
.btn-cta-primary { padding: 14px 32px; border-radius: 12px; background: white; color: var(--primary); font-size: 15px; font-weight: 700; border: none; cursor: pointer; transition: var(--transition); }
.btn-cta-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.2); }
.btn-cta-ghost { padding: 14px 32px; border-radius: 12px; background: rgba(255,255,255,0.15); color: white; font-size: 15px; font-weight: 600; border: 1px solid rgba(255,255,255,0.3); cursor: pointer; transition: var(--transition); }
.btn-cta-ghost:hover { background: rgba(255,255,255,0.25); }

/* Footer */
.footer { background: var(--text-primary); padding: 60px 24px 32px; }
.footer-inner { display: flex; gap: 60px; margin-bottom: 40px; }
.footer-brand { flex: 1; }
.footer-logo { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.logo-icon-sm { width: 28px; height: 28px; background: linear-gradient(135deg, var(--primary), var(--accent)); border-radius: 8px; }
.footer-logo span { font-size: 16px; font-weight: 700; color: white; }
.footer-brand p { font-size: 14px; color: #64748b; }
.footer-links { display: flex; gap: 60px; }
.footer-col { display: flex; flex-direction: column; gap: 10px; }
.footer-col h4 { font-size: 13px; font-weight: 600; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 4px; }
.footer-col a { font-size: 14px; color: #64748b; text-decoration: none; transition: var(--transition); }
.footer-col a:hover { color: white; }
.footer-bottom { border-top: 1px solid #1e293b; padding-top: 24px; font-size: 13px; color: #475569; }

@media (max-width: 900px) {
  .hero { flex-direction: column; padding: 60px 20px 40px; }
  .hero-visual { display: none; }
  .features-grid { grid-template-columns: 1fr 1fr; }
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .footer-inner { flex-direction: column; gap: 32px; }
}
@media (max-width: 600px) {
  .features-grid { grid-template-columns: 1fr; }
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .cta-card { padding: 40px 24px; }
  .cta-actions { flex-direction: column; }
}
</style>
