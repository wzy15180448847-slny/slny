<template>
  <div class="landlord-add-house">
    <div class="page-header">
      <h1>发布房源</h1>
      <el-progress :percentage="stepProgress" :show-text="false" />
    </div>
    
    <el-steps :active="currentStep" align-center>
      <el-step title="基础信息" />
      <el-step title="设施配套" />
      <el-step title="租金配置" />
    </el-steps>
    
    <div class="step-content">
      <div v-if="currentStep === 0" class="step-form">
        <el-form :model="houseForm" label-width="120px">
          <el-form-item label="房源名称" required>
            <el-input v-model="houseForm.houseName" placeholder="请输入房源名称" />
          </el-form-item>
          <el-form-item label="详细地址" required>
            <el-input v-model="houseForm.address" placeholder="请输入详细地址" />
          </el-form-item>
          <el-form-item label="所在城市">
            <el-select v-model="houseForm.city" placeholder="请选择城市">
              <el-option label="北京" value="北京" />
              <el-option label="上海" value="上海" />
              <el-option label="广州" value="广州" />
              <el-option label="深圳" value="深圳" />
            </el-select>
          </el-form-item>
          <el-form-item label="房屋类型">
            <el-select v-model="houseForm.houseType" placeholder="请选择房屋类型">
              <el-option label="住宅" value="RESIDENTIAL" />
              <el-option label="公寓" value="APARTMENT" />
              <el-option label="别墅" value="VILLA" />
            </el-select>
          </el-form-item>
          <el-form-item label="建筑面积">
            <el-input v-model="houseForm.area" placeholder="请输入建筑面积" suffix="㎡" />
          </el-form-item>
          <el-form-item label="户型结构">
            <el-select v-model="houseForm.rooms" placeholder="请选择户型">
              <el-option label="1室1厅" value="1室1厅" />
              <el-option label="2室1厅" value="2室1厅" />
              <el-option label="2室2厅" value="2室2厅" />
              <el-option label="3室1厅" value="3室1厅" />
              <el-option label="3室2厅" value="3室2厅" />
              <el-option label="4室及以上" value="4室及以上" />
            </el-select>
          </el-form-item>
          <el-form-item label="朝向">
            <el-select v-model="houseForm.direction" placeholder="请选择朝向">
              <el-option label="朝南" value="朝南" />
              <el-option label="朝北" value="朝北" />
              <el-option label="朝东" value="朝东" />
              <el-option label="朝西" value="朝西" />
              <el-option label="南北通透" value="南北通透" />
            </el-select>
          </el-form-item>
          <el-form-item label="楼层">
            <el-input v-model="houseForm.floor" placeholder="如：5/18" />
          </el-form-item>
          <el-form-item label="装修情况">
            <el-select v-model="houseForm.decoration" placeholder="请选择装修情况">
              <el-option label="毛坯" value="RAW" />
              <el-option label="简装" value="SIMPLE" />
              <el-option label="精装" value="FINE" />
            </el-select>
          </el-form-item>
          <el-form-item label="房源描述">
            <el-input type="textarea" v-model="houseForm.description" :rows="4" placeholder="请详细描述房源特点" />
          </el-form-item>
        </el-form>
      </div>
      
      <div v-if="currentStep === 1" class="step-form">
        <el-form :model="houseForm" label-width="120px">
          <el-form-item label="配套设施">
            <el-checkbox-group v-model="houseForm.facilities">
              <el-checkbox label="空调" value="airConditioner" />
              <el-checkbox label="暖气" value="heating" />
              <el-checkbox label="热水器" value="waterHeater" />
              <el-checkbox label="洗衣机" value="washer" />
              <el-checkbox label="冰箱" value="refrigerator" />
              <el-checkbox label="微波炉" value="microwave" />
              <el-checkbox label="电视" value="tv" />
              <el-checkbox label="宽带" value="internet" />
              <el-checkbox label="电梯" value="elevator" />
              <el-checkbox label="车位" value="parking" />
              <el-checkbox label="阳台" value="balcony" />
              <el-checkbox label="飘窗" value="bayWindow" />
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="房源图片">
            <el-upload
              action="#"
              :auto-upload="false"
              :file-list="houseForm.images"
              list-type="picture-card"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <p class="upload-tip">支持 JPG、PNG 格式，最多上传 9 张图片</p>
          </el-form-item>
        </el-form>
      </div>
      
      <div v-if="currentStep === 2" class="step-form">
        <el-form :model="houseForm" label-width="120px">
          <el-form-item label="月租金" required>
            <el-input v-model="houseForm.rent" placeholder="请输入月租金" prefix="¥" />
          </el-form-item>
          <el-form-item label="押金">
            <el-input v-model="houseForm.deposit" placeholder="请输入押金金额" prefix="¥" />
          </el-form-item>
          <el-form-item label="付款方式">
            <el-select v-model="houseForm.paymentMethod" placeholder="请选择付款方式">
              <el-option label="押一付一" value="MONTHLY" />
              <el-option label="押一付三" value="QUARTERLY" />
              <el-option label="押一付六" value="HALF_YEAR" />
              <el-option label="押一付年" value="YEARLY" />
            </el-select>
          </el-form-item>
          <el-form-item label="最短租期">
            <el-select v-model="houseForm.minLeaseTerm" placeholder="请选择最短租期">
              <el-option label="1个月" value="1" />
              <el-option label="3个月" value="3" />
              <el-option label="6个月" value="6" />
              <el-option label="1年" value="12" />
            </el-select>
          </el-form-item>
          <el-form-item label="可入住时间">
            <el-date-picker v-model="houseForm.availableDate" type="date" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="租金包含">
            <el-checkbox-group v-model="houseForm.rentIncludes">
              <el-checkbox label="物业费" value="propertyFee" />
              <el-checkbox label="水费" value="waterFee" />
              <el-checkbox label="电费" value="electricFee" />
              <el-checkbox label="燃气费" value="gasFee" />
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="其他费用说明">
            <el-input type="textarea" v-model="houseForm.feeDescription" :rows="3" placeholder="请说明其他费用" />
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <div class="step-actions">
      <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
      <el-button v-if="currentStep < 2" type="primary" @click="nextStep">下一步</el-button>
      <el-button v-if="currentStep === 2" type="primary" :loading="submitting" @click="submitHouse">发布房源</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentStep = ref(0)
const submitting = ref(false)

const houseForm = reactive({
  houseName: '',
  address: '',
  city: '北京',
  houseType: 'RESIDENTIAL',
  area: '',
  rooms: '',
  direction: '',
  floor: '',
  decoration: 'FINE',
  description: '',
  facilities: [],
  images: [],
  rent: '',
  deposit: '',
  paymentMethod: 'MONTHLY',
  minLeaseTerm: '3',
  availableDate: '',
  rentIncludes: [],
  feeDescription: ''
})

const stepProgress = computed(() => {
  return Math.round(((currentStep.value + 1) / 3) * 100)
})

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const nextStep = () => {
  if (currentStep.value < 2) {
    currentStep.value++
  }
}

const submitHouse = async () => {
  if (!houseForm.houseName || !houseForm.address || !houseForm.rent) {
    ElMessage.error('请填写必填项')
    return
  }
  
  submitting.value = true
  
  await new Promise(resolve => setTimeout(resolve, 1500))
  
  ElMessage.success('房源发布成功，等待审核')
  router.push('/landlord/houses')
  
  submitting.value = false
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-add-house {
  padding: 30px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  
  h1 {
    font-size: 24px;
    color: $text-primary;
    margin-bottom: 20px;
  }
}

.step-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
}

.step-form {
  .upload-tip {
    color: $text-secondary;
    font-size: 13px;
    margin-top: 10px;
  }
}

.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.el-steps {
  margin-bottom: 30px;
}
</style>