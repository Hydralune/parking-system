<template>
  <div class="loading-spinner" v-if="visible">
    <div class="spinner-container">
      <el-icon class="loading-icon">
        <Loading />
      </el-icon>
      <p class="loading-text">{{ text }}</p>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: 'LoadingSpinner',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    text: {
      type: String,
      default: '加载中...'
    }
  },
  setup(props) {
    // 监听visible变化
    watch(
      () => props.visible,
      (newVal) => {
        if (newVal) {
          document.body.style.overflow = 'hidden'
        } else {
          document.body.style.overflow = ''
        }
      },
      { immediate: true }
    )
    
    return {}
  }
}
</script>

<style scoped>
.loading-spinner {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.spinner-container {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.loading-icon {
  font-size: 24px;
  color: #409eff;
  animation: rotating 2s linear infinite;
  margin-bottom: 15px;
}

.loading-text {
  margin: 0;
  color: #606266;
  font-size: 16px;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>