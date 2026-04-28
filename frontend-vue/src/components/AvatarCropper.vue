<template>
  <el-dialog title="裁剪头像" :visible="visible" width="600px" @close="handleClose">
    <div class="cropper-container">
      <div class="cropper-wrapper">
        <vue-cropper
          ref="cropperRef"
          :img="imageUrl"
          :output-size="1"
          :output-type="'png'"
          :info="true"
          :can-scale="true"
          :auto-crop="true"
          :fixed="true"
          :fixed-number="[1, 1]"
          :center-box="true"
          :high="true"
          mode="contain"
        />
      </div>
      <div class="cropper-actions">
        <div class="action-buttons">
          <el-button @click="rotateLeft">
            <el-icon><RotateCcw /></el-icon>
            左旋
          </el-button>
          <el-button @click="rotateRight">
            <el-icon><RotateCw /></el-icon>
            右旋
          </el-button>
          <el-button @click="scaleDown">
            <el-icon><Minus /></el-icon>
            缩小
          </el-button>
          <el-button @click="scaleUp">
            <el-icon><Plus /></el-icon>
            放大
          </el-button>
          <el-button @click="reset">
            <el-icon><RefreshCw /></el-icon>
            重置
          </el-button>
        </div>
        <div class="preview-section">
          <div class="preview-label">预览</div>
          <div class="preview-images">
            <div class="preview-item">
              <div class="preview-size">50×50</div>
              <vue-cropper
                ref="previewRef"
                :img="imageUrl"
                :output-size="1"
                :output-type="'png'"
                :info="false"
                :can-scale="false"
                :auto-crop="true"
                :fixed="true"
                :fixed-number="[1, 1]"
                :center-box="true"
                :high="true"
                mode="contain"
                class="preview-cropper"
              />
            </div>
            <div class="preview-item">
              <div class="preview-size">100×100</div>
              <vue-cropper
                ref="previewRef2"
                :img="imageUrl"
                :output-size="1"
                :output-type="'png'"
                :info="false"
                :can-scale="false"
                :auto-crop="true"
                :fixed="true"
                :fixed-number="[1, 1]"
                :center-box="true"
                :high="true"
                mode="contain"
                class="preview-cropper"
              />
            </div>
            <div class="preview-item">
              <div class="preview-size">150×150</div>
              <vue-cropper
                ref="previewRef3"
                :img="imageUrl"
                :output-size="1"
                :output-type="'png'"
                :info="false"
                :can-scale="false"
                :auto-crop="true"
                :fixed="true"
                :fixed-number="[1, 1]"
                :center-box="true"
                :high="true"
                mode="contain"
                class="preview-cropper"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="confirmCrop">确认裁剪</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { VueCropper } from 'vue-cropper'
import { RotateCcw, RotateCw, Plus, Minus, RefreshCw } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  imageUrl: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close', 'crop'])

const cropperRef = ref(null)

const rotateLeft = () => {
  cropperRef.value?.rotateLeft()
}

const rotateRight = () => {
  cropperRef.value?.rotateRight()
}

const scaleDown = () => {
  cropperRef.value?.scaleDown()
}

const scaleUp = () => {
  cropperRef.value?.scaleUp()
}

const reset = () => {
  cropperRef.value?.reset()
}

const confirmCrop = () => {
  cropperRef.value?.getCropData((data) => {
    if (data) {
      emit('crop', data)
      handleClose()
    }
  })
}

const handleClose = () => {
  emit('close')
}
</script>

<style lang="scss" scoped>
.cropper-container {
  display: flex;
  gap: 20px;

  .cropper-wrapper {
    flex: 1;
    height: 300px;
    background: #f5f5f5;
    border-radius: 8px;
    overflow: hidden;
  }

  .cropper-actions {
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: 180px;

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .el-button {
        width: 100%;
        justify-content: flex-start;
      }
    }

    .preview-section {
      flex: 1;
      background: #f8f9fa;
      border-radius: 8px;
      padding: 15px;

      .preview-label {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 10px;
        text-align: center;
      }

      .preview-images {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .preview-item {
          text-align: center;

          .preview-size {
            font-size: 12px;
            color: #999;
            margin-bottom: 5px;
          }

          .preview-cropper {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            overflow: hidden;
            margin: 0 auto;
          }
        }
      }
    }
  }
}

:deep(.cropper-view-box, .cropper-face) {
  border-radius: 50%;
}

:deep(.cropper-view-box) {
  box-shadow: 0 0 0 1px #39f;
}
</style>
