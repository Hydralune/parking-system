<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :width="width"
    :top="top"
    :fullscreen="fullscreen"
    :show-close="showClose"
    :close-on-click-modal="closeOnClickModal"
    :close-on-press-escape="closeOnPressEscape"
    :destroy-on-close="destroyOnClose"
    :before-close="beforeClose"
    :modal="modal"
    :lock-scroll="lockScroll"
    class="modal-dialog"
  >
    <div class="modal-content">
      <slot></slot>
    </div>
    
    <template #footer v-if="showFooter">
      <div class="modal-footer">
        <slot name="footer">
          <el-button @click="handleCancel" v-if="showCancelButton">
            {{ cancelText }}
          </el-button>
          <el-button 
            type="primary" 
            @click="handleConfirm" 
            :loading="confirmLoading"
            v-if="showConfirmButton"
          >
            {{ confirmText }}
          </el-button>
        </slot>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: 'ModalDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '提示'
    },
    width: {
      type: String,
      default: '50%'
    },
    top: {
      type: String,
      default: '15vh'
    },
    fullscreen: {
      type: Boolean,
      default: false
    },
    showClose: {
      type: Boolean,
      default: true
    },
    closeOnClickModal: {
      type: Boolean,
      default: true
    },
    closeOnPressEscape: {
      type: Boolean,
      default: true
    },
    destroyOnClose: {
      type: Boolean,
      default: false
    },
    modal: {
      type: Boolean,
      default: true
    },
    lockScroll: {
      type: Boolean,
      default: true
    },
    showFooter: {
      type: Boolean,
      default: true
    },
    showCancelButton: {
      type: Boolean,
      default: true
    },
    showConfirmButton: {
      type: Boolean,
      default: true
    },
    cancelText: {
      type: String,
      default: '取消'
    },
    confirmText: {
      type: String,
      default: '确定'
    },
    confirmLoading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue', 'confirm', 'cancel', 'close', 'open'],
  setup(props, { emit }) {
    const dialogVisible = ref(props.modelValue)
    
    // 监听外部v-model变化
    watch(
      () => props.modelValue,
      (newVal) => {
        dialogVisible.value = newVal
      }
    )
    
    // 监听内部变化并同步到外部
    watch(
      () => dialogVisible.value,
      (newVal) => {
        emit('update:modelValue', newVal)
      }
    )
    
    // 处理确认
    const handleConfirm = () => {
      emit('confirm')
    }
    
    // 处理取消
    const handleCancel = () => {
      emit('cancel')
      dialogVisible.value = false
    }
    
    // 处理关闭
    const beforeClose = (done) => {
      emit('close')
      done()
    }
    
    // 打开时触发
    const open = () => {
      emit('open')
    }
    
    return {
      dialogVisible,
      handleConfirm,
      handleCancel,
      beforeClose,
      open
    }
  }
}
</script>

<style scoped>
.modal-dialog :deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

.modal-content {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>