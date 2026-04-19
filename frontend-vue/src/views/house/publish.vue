<template>
  <div class="publish-page">
    <div class="publish-container">
      <div class="publish-header">
        <h2>发布房源</h2>
        <p>填写以下信息，发布您的房源</p>
      </div>
      
      <el-form 
        ref="publishFormRef"
        :model="publishForm"
        :rules="publishRules"
        class="publish-form"
        label-position="top"
      >
        <el-form-item label="基本信息">
          <div class="form-grid">
            <el-form-item prop="title">
              <el-input 
                v-model="publishForm.title"
                placeholder="房源标题"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="price">
              <el-input 
                v-model="publishForm.price"
                type="number"
                placeholder="租金 (元/月)"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="rentType">
              <el-select 
                v-model="publishForm.rentType"
                placeholder="租赁类型"
                size="large"
              >
                <el-option label="整租" value="ENTIRE" />
                <el-option label="合租" value="SHARED" />
                <el-option label="单间" value="SINGLE_ROOM" />
              </el-select>
            </el-form-item>
            
            <el-form-item prop="houseType">
              <el-select 
                v-model="publishForm.houseType"
                placeholder="户型"
                size="large"
              >
                <el-option label="一室一厅" value="ONE_BEDROOM" />
                <el-option label="两室一厅" value="TWO_BEDROOM" />
                <el-option label="三室一厅" value="THREE_BEDROOM" />
                <el-option label="四室及以上" value="FOUR_PLUS_BEDROOM" />
              </el-select>
            </el-form-item>
          </div>
        </el-form-item>
        
        <el-form-item label="房屋信息">
          <div class="form-grid">
            <el-form-item prop="area">
              <el-input 
                v-model="publishForm.area"
                type="number"
                placeholder="面积 (㎡)"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="floor">
              <el-input 
                v-model="publishForm.floor"
                type="number"
                placeholder="所在楼层"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="totalFloors">
              <el-input 
                v-model="publishForm.totalFloors"
                type="number"
                placeholder="总楼层"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="orientation">
              <el-select 
                v-model="publishForm.orientation"
                placeholder="朝向"
                size="large"
              >
                <el-option label="东" value="EAST" />
                <el-option label="南" value="SOUTH" />
                <el-option label="西" value="WEST" />
                <el-option label="北" value="NORTH" />
                <el-option label="东南" value="SOUTHEAST" />
                <el-option label="西南" value="SOUTHWEST" />
                <el-option label="东北" value="NORTHEAST" />
                <el-option label="西北" value="NORTHWEST" />
              </el-select>
            </el-form-item>
          </div>
        </el-form-item>
        
        <el-form-item label="位置信息">
          <div class="form-grid">
            <el-form-item prop="city">
              <el-input 
                v-model="publishForm.city"
                placeholder="城市"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="district">
              <el-input 
                v-model="publishForm.district"
                placeholder="区域"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="community">
              <el-input 
                v-model="publishForm.community"
                placeholder="小区名称"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="address">
              <el-input 
                v-model="publishForm.address"
                placeholder="详细地址"
                size="large"
              />
            </el-form-item>
          </div>
        </el-form-item>
        
        <el-form-item label="房屋配置">
          <div class="amenities-grid">
            <div v-for="amenity in amenities" :key="amenity.value" class="amenity-item">
              <el-checkbox v-model="publishForm.amenities" :label="amenity.value">
                <el-icon :size="20">{{ amenity.icon }}</el-icon>
                <span>{{ amenity.label }}</span>
              </el-checkbox>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item prop="description" label="房屋描述">
          <el-input 
            v-model="publishForm.description"
            type="textarea"
            placeholder="详细描述房屋特点、周边环境等信息"
            rows="5"
            size="large"
          />
        </el-form-item>
        
        <el-form-item label="房屋图片">
          <el-upload
            class="uploader"
            :action="''"
            :auto-upload="false"
            :on-change="handleImageChange"
            list-type="picture-card"
            multiple
          >
            <template #default>
              <el-icon><Plus /></el-icon>
            </template>
            <template #file="{file}">
              <img
                v-for="(url, index) in publishForm.images"
                :key="index"
                :src="url"
                class="uploaded-image"
              />
            </template>
          </el-upload>
          <p class="upload-tip">请上传房屋的真实图片，最多支持9张</p>
        </el-form-item>
        
        <el-form-item label="联系方式">
          <div class="form-grid">
            <el-form-item prop="contactName">
              <el-input 
                v-model="publishForm.contactName"
                placeholder="联系人姓名"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="contactPhone">
              <el-input 
                v-model="publishForm.contactPhone"
                placeholder="联系电话"
                size="large"
              />
            </el-form-item>
          </div>
        </el-form-item>
        
        <el-form-item>
          <div class="form-actions">
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" @click="submitForm">发布房源</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useHouseStore } from '@/store/house'

const router = useRouter()
const houseStore = useHouseStore()
const publishFormRef = ref(null)

const publishForm = reactive({
  title: '',
  price: '',
  rentType: '',
  houseType: '',
  area: '',
  floor: '',
  totalFloors: '',
  orientation: '',
  city: '',
  district: '',
  community: '',
  address: '',
  amenities: [],
  description: '',
  images: [],
  contactName: '',
  contactPhone: ''
})

const publishRules = {
  title: [
    { required: true, message: '请输入房源标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度在 5 到 50 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入租金', trigger: 'blur' },
    { type: 'number', min: 100, message: '租金不能低于 100 元', trigger: 'blur' }
  ],
  rentType: [
    { required: true, message: '请选择租赁类型', trigger: 'change' }
  ],
  houseType: [
    { required: true, message: '请选择户型', trigger: 'change' }
  ],
  area: [
    { required: true, message: '请输入面积', trigger: 'blur' },
    { type: 'number', min: 1, message: '面积不能小于 1 ㎡', trigger: 'blur' }
  ],
  floor: [
    { required: true, message: '请输入所在楼层', trigger: 'blur' },
    { type: 'number', min: 1, message: '楼层不能小于 1', trigger: 'blur' }
  ],
  totalFloors: [
    { required: true, message: '请输入总楼层', trigger: 'blur' },
    { type: 'number', min: 1, message: '总楼层不能小于 1', trigger: 'blur' }
  ],
  orientation: [
    { required: true, message: '请选择朝向', trigger: 'change' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区域', trigger: 'blur' }
  ],
  community: [
    { required: true, message: '请输入小区名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入房屋描述', trigger: 'blur' },
    { min: 20, message: '描述长度不能少于 20 个字符', trigger: 'blur' }
  ],
  images: [
    { required: true, message: '请上传至少一张房屋图片', trigger: 'change' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ]
}

const amenities = [
  { label: '空调', value: 'AIR_CONDITIONER', icon: 'Snowflake' },
  { label: '洗衣机', value: 'WASHING_MACHINE', icon: 'Grid' },
  { label: '冰箱', value: 'REFRIGERATOR', icon: 'IceCream' },
  { label: '热水器', value: 'WATER_HEATER', icon: 'Water' },
  { label: '天然气', value: 'GAS', icon: 'Fire' },
  { label: '宽带', value: 'INTERNET', icon: 'Wifi' },
  { label: '电视', value: 'TV', icon: 'Monitor' },
  { label: '家具齐全', value: 'FURNISHED', icon: 'Home' },
  { label: '独卫', value: 'PRIVATE_BATHROOM', icon: 'Toilet' },
  { label: '阳台', value: 'BALCONY', icon: 'Sunny' },
  { label: '电梯', value: 'ELEVATOR', icon: 'Position' },
  { label: '停车位', value: 'PARKING', icon: 'Car' }
]

const fileList = ref([])

const handleImageChange = (file, files) => {
  fileList.value = files
  publishForm.images = files.map(f => URL.createObjectURL(f.raw))
}

const resetForm = () => {
  if (publishFormRef.value) {
    publishFormRef.value.resetFields()
    publishForm.images = []
  }
}

const submitForm = async () => {
  if (!publishFormRef.value) return
  
  await publishFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await houseStore.publishHouse(publishForm, fileList.value)
      ElMessage.success('房源发布成功')
      router.push('/my-houses')
    } catch (error) {
      console.error(error)
      ElMessage.error('房源发布失败')
    }
  })
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.publish-page {
  padding: 40px 20px;
  min-height: 80vh;
}

.publish-container {
  max-width: 1200px;
  margin: 0 auto;
}

.publish-header {
  text-align: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 32px;
    color: $text-primary;
    margin-bottom: 10px;
    font-weight: 600;
  }
  
  p {
    color: $text-secondary;
    font-size: 16px;
  }
}

.publish-form {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 40px;
  
  .form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 30px;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }
  
  .amenities-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;
    
    @media (max-width: 1024px) {
      grid-template-columns: repeat(3, 1fr);
    }
    
    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .amenity-item {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .el-icon {
        margin-right: 5px;
      }
    }
  }
  
  .uploader {
    margin-bottom: 15px;
    
    .uploaded-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .upload-tip {
    color: $text-secondary;
    font-size: 14px;
    margin-top: 10px;
  }
  
  .form-actions {
    display: flex;
    gap: 15px;
    justify-content: flex-end;
    margin-top: 40px;
    
    .el-button {
      min-width: 120px;
      height: 45px;
      font-size: 16px;
    }
  }
}

@media (max-width: 768px) {
  .publish-form {
    padding: 20px;
  }
}
</style>