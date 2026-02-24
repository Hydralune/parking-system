<template>
  <header class="header" :class="{ scrolled: isScrolled }">
    <div class="header-inner">
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <rect x="2" y="3" width="20" height="18" rx="3" fill="white" fill-opacity="0.2" stroke="white" stroke-width="1.5"/>
            <path d="M7 8h5.5a2.5 2.5 0 010 5H7V8z" fill="white"/>
            <path d="M7 13h3v4H7v-4z" fill="white" opacity="0.7"/>
          </svg>
        </div>
        <span class="logo-text">智停</span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">首页</router-link>
        <router-link to="/parking-lots" class="nav-link" :class="{ active: $route.path.startsWith('/parking-lots') }">停车场</router-link>
        <router-link v-if="userStore.isAuthenticated" to="/reservations" class="nav-link" :class="{ active: $route.path.startsWith('/reservations') }">我的预约</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin" class="nav-link admin-link" :class="{ active: $route.path.startsWith('/admin') }">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
          管理后台
        </router-link>
      </nav>

      <div class="header-right">
        <template v-if="userStore.isAuthenticated">
          <div class="user-menu" @click="menuOpen = !menuOpen" ref="menuRef">
            <div class="avatar">{{ userStore.username?.charAt(0)?.toUpperCase() }}</div>
            <span class="username">{{ userStore.username }}</span>
            <svg class="chevron" :class="{ open: menuOpen }" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9l6 6 6-6"/></svg>
            <div class="dropdown" v-show="menuOpen">
              <router-link to="/profile" class="dropdown-item" @click="menuOpen = false">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
                个人资料
              </router-link>
              <div class="dropdown-divider"></div>
              <button class="dropdown-item danger" @click="handleLogout">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/></svg>
                退出登录
              </button>
            </div>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-ghost">登录</router-link>
          <router-link to="/register" class="btn-primary">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

export default {
  name: 'HeaderNav',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const menuOpen = ref(false)
    const menuRef = ref(null)
    const isScrolled = ref(false)

    const handleScroll = () => { isScrolled.value = window.scrollY > 20 }
    const handleClickOutside = (e) => {
      if (menuRef.value && !menuRef.value.contains(e.target)) menuOpen.value = false
    }

    onMounted(() => {
      window.addEventListener('scroll', handleScroll)
      document.addEventListener('click', handleClickOutside)
    })
    onUnmounted(() => {
      window.removeEventListener('scroll', handleScroll)
      document.removeEventListener('click', handleClickOutside)
    })

    const handleLogout = () => {
      menuOpen.value = false
      userStore.logout()
      router.push('/')
      window.location.reload()
    }

    return { userStore, menuOpen, menuRef, isScrolled, handleLogout }
  }
}
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid transparent;
  transition: var(--transition);
}
.header.scrolled {
  border-bottom-color: var(--border);
  box-shadow: 0 2px 20px rgba(37,99,235,0.08);
}
.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 32px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(37,99,235,0.3);
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}
.nav-link {
  padding: 6px 14px;
  border-radius: 8px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  transition: var(--transition);
  display: flex;
  align-items: center;
  gap: 6px;
}
.nav-link:hover { color: var(--primary); background: var(--primary-bg); }
.nav-link.active { color: var(--primary); background: var(--primary-bg); font-weight: 600; }
.admin-link { color: var(--primary-dark); }
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.btn-ghost {
  padding: 7px 16px;
  border-radius: 8px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  border: 1px solid var(--border);
  transition: var(--transition);
}
.btn-ghost:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-bg); }
.btn-primary {
  padding: 7px 18px;
  border-radius: 8px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  box-shadow: 0 4px 12px rgba(37,99,235,0.3);
  transition: var(--transition);
}
.btn-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(37,99,235,0.4); }
.user-menu {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: var(--transition);
  border: 1px solid var(--border);
}
.user-menu:hover { background: var(--primary-bg); border-color: var(--primary); }
.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.username { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.chevron { color: var(--text-muted); transition: transform 0.2s; }
.chevron.open { transform: rotate(180deg); }
.dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: white;
  border: 1px solid var(--border);
  border-radius: 12px;
  box-shadow: var(--shadow-lg);
  min-width: 160px;
  padding: 6px;
  animation: fadeInUp 0.15s ease;
}
.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 12px;
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-primary);
  text-decoration: none;
  cursor: pointer;
  transition: var(--transition);
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}
.dropdown-item:hover { background: var(--bg); }
.dropdown-item.danger { color: var(--danger); }
.dropdown-item.danger:hover { background: #fef2f2; }
.dropdown-divider { height: 1px; background: var(--border); margin: 4px 0; }
</style>
