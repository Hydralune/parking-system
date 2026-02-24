<template>
  <div class="footer-info">
    <div class="footer-content">
      <div class="footer-section">
        <h4>智能停车系统</h4>
        <p>便捷、高效、智能的停车解决方案</p>
      </div>
      
      <div class="footer-section">
        <h4>快速链接</h4>
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/parking-lots">停车场</router-link></li>
          <li><router-link to="/reservations">我的预约</router-link></li>
          <li><router-link to="/login" v-if="!userStore.isAuthenticated">登录</router-link></li>
          <li><router-link to="/register" v-if="!userStore.isAuthenticated">注册</router-link></li>
        </ul>
      </div>
      
      <div class="footer-section">
        <h4>联系方式</h4>
        <p>电话: 400-123-4567</p>
        <p>邮箱: support@parking-system.com</p>
      </div>
      
      <div class="footer-section">
        <h4>关注我们</h4>
        <div class="social-links">
          <el-link href="#" :underline="false" class="social-link">
            <el-icon><Pointer /></el-icon>
          </el-link>
          <el-link href="#" :underline="false" class="social-link">
            <el-icon><ChatLineSquare /></el-icon>
          </el-link>
          <el-link href="#" :underline="false" class="social-link">
            <el-icon><Share /></el-icon>
          </el-link>
        </div>
      </div>
    </div>
    
    <div class="footer-bottom">
      <p>&copy; 2023 智能停车系统. 保留所有权利.</p>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/user'

export default {
  name: 'FooterInfo',
  setup() {
    const userStore = useUserStore()
    
    return {
      userStore
    }
  }
}
</script>

<style scoped>
.footer-info {
  background-color: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  padding: 0 20px;
}

.footer-section h4 {
  color: #ecf0f1;
  margin-bottom: 15px;
  font-size: 18px;
}

.footer-section p {
  color: #bdc3c7;
  margin: 5px 0;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 8px;
}

.footer-section ul li a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-section ul li a:hover {
  color: #3498db;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  color: #bdc3c7;
  font-size: 20px;
  transition: color 0.3s;
}

.social-link:hover {
  color: #3498db;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  margin-top: 30px;
  text-align: center;
}

.footer-bottom p {
  color: #95a5a6;
  margin: 0;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .footer-section {
    margin-bottom: 20px;
  }
}
</style>