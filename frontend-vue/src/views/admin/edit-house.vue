<template>
  <div class="admin-edit-house">
    <div class="page-header">
      <h1>编辑房源</h1>
      <el-button @click="goBack">返回列表</el-button>
    </div>
    
    <el-card class="house-form-card">
      <el-form ref="houseFormRef" :model="houseForm" label-width="100px" class="house-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房源名称" prop="houseName">
              <el-input v-model="houseForm.houseName" placeholder="请输入房源名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租金" prop="rent">
              <el-input v-model="houseForm.rent" placeholder="请输入租金" suffix="元/月" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面积" prop="area">
              <el-input v-model="houseForm.area" placeholder="请输入面积" suffix="㎡" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="户型" prop="rooms">
              <el-input v-model="houseForm.rooms" placeholder="请输入户型" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="朝向" prop="direction">
              <el-select v-model="houseForm.direction" placeholder="请选择朝向">
                <el-option label="东" value="东" />
                <el-option label="南" value="南" />
                <el-option label="西" value="西" />
                <el-option label="北" value="北" />
                <el-option label="南北通透" value="南北通透" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="houseForm.status" placeholder="请选择状态">
                <el-option label="展示中" value="ACTIVE" />
                <el-option label="审核中" value="PENDING" />
                <el-option label="已下架" value="INACTIVE" />
                <el-option label="已出租" value="RENTED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input v-model="houseForm.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-form-item label="房东" prop="landlordName">
          <el-input v-model="houseForm.landlordName" placeholder="请输入房东姓名" />
        </el-form-item>

        <el-form-item label="房源描述" prop="description">
          <el-input type="textarea" v-model="houseForm.description" placeholder="请输入房源描述" :rows="3" />
        </el-form-item>

        <el-form-item label="标签">
          <el-checkbox-group v-model="houseForm.tags">
            <el-checkbox label="精装修" />
            <el-checkbox label="近地铁" />
            <el-checkbox label="拎包入住" />
            <el-checkbox label="带车位" />
            <el-checkbox label="单身公寓" />
            <el-checkbox label="豪华装修" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="房源图片">
          <el-upload
            action="/api/upload/image"
            list-type="picture-card"
            :file-list="imageList"
            :on-success="handleImageUpload"
            :before-upload="beforeImageUpload"
            :on-remove="handleImageRemove"
          >
            <i class="el-icon-plus" />
          </el-upload>
          <div style="margin-top: 10px; color: #999; font-size: 12px;">
            支持 jpg、png、gif 格式，单张图片不超过 5MB
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">保存修改</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const houseFormRef = ref(null)
const loading = ref(false)
const imageList = ref([])

const houseForm = reactive({
  id: '',
  houseName: '',
  address: '',
  landlordName: '',
  rent: '',
  area: '',
  rooms: '',
  direction: '',
  status: '',
  description: '',
  tags: []
})

const initForm = () => {
  const houseId = parseInt(route.params.id)
  const mockHouses = [
    { id: 1, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', landlordName: '张房东', rent: 3500, area: 120, rooms: '3室2厅', direction: '南北通透', status: 'ACTIVE', description: '阳光充足，视野开阔。', tags: ['精装修', '近地铁'], images: ['https://picsum.photos/seed/house1/800/600', 'https://picsum.photos/seed/house1a/800/600'] },
    { id: 2, houseName: '幸福花园2室1厅', address: '海淀区幸福街12号', landlordName: '李房东', rent: 2800, area: 85, rooms: '2室1厅', direction: '朝南', status: 'ACTIVE', description: '温馨舒适，拎包入住。', tags: ['拎包入住'], images: ['https://picsum.photos/seed/house2/800/600'] },
    { id: 3, houseName: '锦绣家园1室1厅', address: '西城区锦绣路36号', landlordName: '王房东', rent: 2000, area: 55, rooms: '1室1厅', direction: '朝北', status: 'PENDING', description: '单身公寓，适合年轻人居住。', tags: ['单身公寓'], images: ['https://picsum.photos/seed/house3/800/600'] },
    { id: 4, houseName: '星河湾4室2厅', address: '东城区星河大道1号', landlordName: '赵房东', rent: 5500, area: 180, rooms: '4室2厅', direction: '南北通透', status: 'RENTED', description: '豪华大四居，带地下车位。', tags: ['豪华装修', '带车位'], images: ['https://picsum.photos/seed/house4/800/600', 'https://picsum.photos/seed/house4a/800/600'] }
  ]
  
  const house = mockHouses.find(h => h.id === houseId)
  if (house) {
    Object.assign(houseForm, house)
    if (house.images) {
      imageList.value = house.images.map(url => ({ url }))
    }
  }
}

const handleImageUpload = (response) => {
  if (response.code === 200) {
    imageList.value.push({ url: response.data })
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('请上传图片格式')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleImageRemove = (file) => {
  const index = imageList.value.findIndex(item => item.url === file.url)
  if (index > -1) {
    imageList.value.splice(index, 1)
  }
}

const submitForm = async () => {
  if (!houseFormRef.value) return
  
  await houseFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      ElMessage.success('修改成功')
      router.push('/admin/houses')
    } catch (error) {
      ElMessage.error('修改失败')
    } finally {
      loading.value = false
    }
  })
}

const goBack = () => {
  router.push('/admin/houses')
}

onMounted(() => {
  initForm()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-edit-house {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h1 {
    font-size: 22px;
    color: $text-primary;
    margin: 0;
  }
}

.house-form-card {
  max-width: 800px;
}

.house-form {
  padding: 20px 0;
}
</style>