<template>
  <div class="admin-complaints">
    <div class="page-header">
      <h1>投诉仲裁</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card" @tab-change="handleTabChange">
      <el-tab-pane label="待处理" name="pending">
        <el-table :data="pendingComplaints" border>
          <el-table-column prop="title" label="投诉标题" />
          <el-table-column prop="complainant" label="投诉人" />
          <el-table-column prop="defendant" label="被投诉人" />
          <el-table-column prop="type" label="投诉类型">
            <template #default="scope">
              <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="handleComplaint(scope.row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已处理" name="handled">
        <el-table :data="handledComplaints" border>
          <el-table-column prop="title" label="投诉标题" />
          <el-table-column prop="complainant" label="投诉人" />
          <el-table-column prop="defendant" label="被投诉人" />
          <el-table-column prop="type" label="投诉类型">
            <template #default="scope">
              <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="result" label="处理结果">
            <template #default="scope">
              <el-tag :type="scope.row.result === 'SUPPORT' ? 'success' : 'warning'">
                {{ scope.row.result === 'SUPPORT' ? '支持投诉' : '驳回投诉' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="handleTime" label="处理时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="投诉详情" v-model="showDetailDialog" width="600px">
      <div v-if="selectedComplaint" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">投诉标题</span>
          <span class="detail-value">{{ selectedComplaint.title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">投诉类型</span>
          <el-tag :type="getTypeTag(selectedComplaint.type)">{{ getTypeText(selectedComplaint.type) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">投诉人</span>
          <span class="detail-value">{{ selectedComplaint.complainant }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">被投诉人</span>
          <span class="detail-value">{{ selectedComplaint.defendant }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">投诉内容</span>
          <span class="detail-value text-content">{{ selectedComplaint.content }}</span>
        </div>
        <div class="detail-images" v-if="selectedComplaint.images && selectedComplaint.images.length > 0">
          <span class="detail-label">证据图片</span>
          <div class="images-grid">
            <img v-for="(img, index) in selectedComplaint.images" :key="index" :src="img" class="evidence-image" />
          </div>
        </div>
        <div class="detail-row" v-if="selectedComplaint.result">
          <span class="detail-label">处理结果</span>
          <el-tag :type="selectedComplaint.result === 'SUPPORT' ? 'success' : 'warning'">
            {{ selectedComplaint.result === 'SUPPORT' ? '支持投诉' : '驳回投诉' }}
          </el-tag>
        </div>
        <div class="detail-row" v-if="selectedComplaint.handleRemark">
          <span class="detail-label">处理备注</span>
          <span class="detail-value text-content">{{ selectedComplaint.handleRemark }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedComplaint?.status === 'PENDING'" type="primary" @click="openHandleDialog">处理投诉</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="处理投诉" v-model="showHandleDialog" width="500px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleForm.result">
            <el-radio label="SUPPORT">支持投诉</el-radio>
            <el-radio label="REJECT">驳回投诉</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input type="textarea" v-model="handleForm.remark" :rows="4" placeholder="请输入处理备注" />
        </el-form-item>
        <div v-if="handleForm.result === 'SUPPORT'" class="punishment-section">
          <h4>处罚措施</h4>
          <el-form-item label="扣除信用分">
            <el-input v-model="handleForm.creditDeduct" type="number" placeholder="输入扣除的信用分数" />
          </el-form-item>
          <el-form-item label="其他处罚">
            <el-input type="textarea" v-model="handleForm.punishment" :rows="2" placeholder="其他处罚措施" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getComplaints, arbitrateComplaint } from '@/api/admin'

const activeTab = ref('pending')

const pendingComplaints = ref([])
const handledComplaints = ref([])

const selectedComplaint = ref(null)
const showDetailDialog = ref(false)
const showHandleDialog = ref(false)

const handleForm = reactive({
  result: '',
  remark: '',
  creditDeduct: '',
  punishment: ''
})

const getTypeTag = (type) => {
  const types = {
    'LANDLORD_BAD': 'danger',
    'TENANT_BAD': 'warning',
    'FALSE_INFO': 'primary',
    'ILLEGAL_CHARGE': 'info'
  }
  return types[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    'LANDLORD_BAD': '房东违规',
    'TENANT_BAD': '租客违规',
    'FALSE_INFO': '信息虚假',
    'ILLEGAL_CHARGE': '不合理收费'
  }
  return texts[type] || type
}

const loadComplaints = async (status = 'PENDING') => {
  try {
    const { data } = await getComplaints({ status })
    if (status === 'PENDING') {
      pendingComplaints.value = data || []
    } else {
      handledComplaints.value = data || []
    }
  } catch (error) {
    console.error('加载投诉列表失败:', error)
    ElMessage.error('加载投诉列表失败')
  }
}

const handleTabChange = (tab) => {
  if (tab === 'handled' && handledComplaints.value.length === 0) {
    loadComplaints('HANDLED')
  }
}

const viewDetail = (complaint) => {
  selectedComplaint.value = complaint
  showDetailDialog.value = true
}

const handleComplaint = (complaint) => {
  selectedComplaint.value = complaint
  showHandleDialog.value = true
}

const openHandleDialog = () => {
  showHandleDialog.value = true
}

const confirmHandle = async () => {
  if (!handleForm.result || !handleForm.remark) {
    ElMessage.error('请填写完整信息')
    return
  }
  
  try {
    const data = {
      result: handleForm.result,
      remark: handleForm.remark
    }
    
    if (handleForm.result === 'SUPPORT') {
      data.creditDeduct = handleForm.creditDeduct
      data.punishment = handleForm.punishment
    }
    
    await arbitrateComplaint(selectedComplaint.value.id, data)
    
    pendingComplaints.value = pendingComplaints.value.filter(c => c.id !== selectedComplaint.value.id)
    
    ElMessage.success('投诉处理完成')
    showHandleDialog.value = false
    showDetailDialog.value = false
    
    handleForm.result = ''
    handleForm.remark = ''
    handleForm.creditDeduct = ''
    handleForm.punishment = ''
  } catch (error) {
    console.error('处理投诉失败:', error)
    ElMessage.error('处理投诉失败')
  }
}

onMounted(() => {
  loadComplaints('PENDING')
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-complaints {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  
  h1 {
    font-size: 22px;
    color: $text-primary;
    margin: 0;
  }
}

.detail-content {
  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid $border-color-base;
    
    &:last-child {
      border-bottom: none;
    }
    
    .detail-label {
      color: $text-secondary;
      font-size: 14px;
      flex-shrink: 0;
      width: 80px;
    }
    
    .detail-value {
      color: $text-primary;
      font-size: 14px;
      font-weight: 500;
      flex: 1;
      text-align: right;
      
      &.text-content {
        text-align: left;
      }
    }
  }
  
  .detail-images {
    padding: 12px 0;
    
    .images-grid {
      display: flex;
      gap: 10px;
      margin-top: 10px;
      
      .evidence-image {
        width: 120px;
        height: 80px;
        object-fit: cover;
        border-radius: 8px;
      }
    }
  }
}

.punishment-section {
  background: $bg-color;
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
  
  h4 {
    font-size: 14px;
    color: $text-primary;
    margin-bottom: 10px;
  }
}
</style>