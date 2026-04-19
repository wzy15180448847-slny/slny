<template>
  <div class="admin-house-audit">
    <div class="page-header">
      <h1>房源审核</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="待审核" name="pending">
        <el-table :data="pendingHouses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="success" @click="approveHouse(scope.row)">通过</el-button>
              <el-button size="small" type="danger" @click="rejectHouse(scope.row)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已通过" name="approved">
        <el-table :data="approvedHouses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="auditTime" label="审核时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已拒绝" name="rejected">
        <el-table :data="rejectedHouses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="rejectReason" label="拒绝原因" />
          <el-table-column prop="auditTime" label="审核时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="reApprove(scope.row)">重新审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="房源审核详情" v-model="showDetailDialog" width="600px">
      <div v-if="selectedHouse" class="detail-content">
        <div class="detail-images">
          <img v-for="(img, index) in selectedHouse.images" :key="index" :src="img" class="house-image" />
        </div>
        <div class="detail-info">
          <h3>{{ selectedHouse.houseName }}</h3>
          <p class="address">{{ selectedHouse.address }}</p>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">房东</span>
              <span class="info-value">{{ selectedHouse.landlordName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">租金</span>
              <span class="info-value">¥{{ selectedHouse.rent }}/月</span>
            </div>
            <div class="info-item">
              <span class="info-label">面积</span>
              <span class="info-value">{{ selectedHouse.area }}㎡</span>
            </div>
            <div class="info-item">
              <span class="info-label">户型</span>
              <span class="info-value">{{ selectedHouse.rooms }}</span>
            </div>
          </div>
          <div class="description">
            <h4>房源描述</h4>
            <p>{{ selectedHouse.description }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedHouse?.status === 'PENDING'" type="success" @click="approveHouse(selectedHouse)">通过审核</el-button>
        <el-button v-if="selectedHouse?.status === 'PENDING'" type="danger" @click="openRejectDialog">拒绝</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="拒绝审核" v-model="showRejectDialog" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input type="textarea" v-model="rejectForm.reason" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getPendingAuditList, auditHouse } from '@/api/house';
const activeTab = ref('pending');
const pendingHouses = ref([]);
const approvedHouses = ref([]);
const rejectedHouses = ref([]);
const selectedHouse = ref(null);
const showDetailDialog = ref(false);
const showRejectDialog = ref(false);
const rejectForm = reactive({
 reason: ''
});
const rentWayMapping = {
 1: '整租',
 2: '合租',
 3: '单间'
};
const houseTypeMapping = {
 'ONE_BEDROOM': '一室一厅',
 'TWO_BEDROOM': '两室一厅',
 'THREE_BEDROOM': '三室一厅',
 'FOUR_PLUS_BEDROOM': '四室及以上'
};
const formatHouse = (house) => {
 return {
 id: house.id,
 houseName: house.title,
 address: house.address,
 landlordName: '房东',
 rent: house.rentPrice,
 area: house.area,
 rooms: houseTypeMapping[house.houseType] || '未知',
 rentWay: rentWayMapping[house.rentWay] || '未知',
 createTime: house.createdTime,
 auditTime: house.auditTime,
 images: house.images ? JSON.parse(house.images) : [],
 description: house.description,
 rejectReason: house.auditRemark,
 status: house.auditStatus === 0 ? 'PENDING' : (house.auditStatus === 1 ? 'ACTIVE' : 'REJECTED'),
 auditStatus: house.auditStatus
 };
};
const loadPendingAuditList = async () => {
 try {
 const response = await getPendingAuditList({ page: 1, size: 20 });
 pendingHouses.value = response.data.records.map(formatHouse);
 }
 catch (error) {
 console.error('加载待审核列表失败:', error);
 ElMessage.error('加载待审核列表失败');
 }
};
const viewDetail = (house) => {
 selectedHouse.value = house;
 showDetailDialog.value = true;
};
const approveHouse = async (house) => {
  try {
    await auditHouse(house.id, { auditStatus: 1, auditRemark: "审核通过" });
    ElMessage.success('审核通过');
    loadPendingAuditList();
    showDetailDialog.value = false;
  }
  catch (error) {
    console.error('审核通过失败:', error);
    ElMessage.error('审核通过失败');
  }
};
const openRejectDialog = () => {
 showRejectDialog.value = true;
};
const confirmReject = async () => {
 if (!rejectForm.reason) {
 ElMessage.error('请输入拒绝原因');
 return;
 }
 try {
 await auditHouse(selectedHouse.value.id, {
 auditStatus: 2,
 auditRemark: rejectForm.reason
 });
 ElMessage.success('已拒绝');
 loadPendingAuditList();
 showRejectDialog.value = false;
 showDetailDialog.value = false;
 rejectForm.reason = '';
 }
 catch (error) {
 console.error('拒绝审核失败:', error);
 ElMessage.error('拒绝审核失败');
 }
};
const rejectHouse = (house) => {
 selectedHouse.value = house;
 showRejectDialog.value = true;
};
const reApprove = (house) => {
 const index = rejectedHouses.value.findIndex(h => h.id === house.id);
 if (index > -1) {
 rejectedHouses.value.splice(index, 1);
 pendingHouses.value.unshift({
 ...house,
 status: 'PENDING',
 auditStatus: 0
 });
 }
 ElMessage.success('已重新提交审核');
};
onMounted(() => {
 loadPendingAuditList();
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-house-audit {
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
  .detail-images {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    overflow-x: auto;
    
    .house-image {
      width: 150px;
      height: 100px;
      object-fit: cover;
      border-radius: 8px;
      flex-shrink: 0;
    }
  }
  
  .detail-info {
    .address {
      color: $text-secondary;
      font-size: 14px;
      margin-bottom: 15px;
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
      margin-bottom: 15px;
      
      .info-item {
        display: flex;
        justify-content: space-between;
        
        .info-label {
          color: $text-secondary;
          font-size: 14px;
        }
        
        .info-value {
          color: $text-primary;
          font-weight: 500;
        }
      }
    }
    
    .description {
      h4 {
        font-size: 14px;
        color: $text-primary;
        margin-bottom: 10px;
      }
      
      p {
        font-size: 14px;
        color: $text-secondary;
        line-height: 1.6;
      }
    }
  }
}
</style>