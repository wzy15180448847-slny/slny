<template>
  <div class="edit-house-page">
    <div class="page-header">
      <h2>编辑房源</h2>
      <p>修改房源信息</p>
    </div>

    <el-card class="house-form-card">
      <el-form ref="houseFormRef" :model="houseForm" label-width="120px" class="house-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房源编号" prop="houseNo">
              <el-input v-model="houseForm.houseNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋类型" prop="houseType">
              <el-select v-model="houseForm.houseType" placeholder="请选择房屋类型">
                <el-option label="1室1厅1卫" value="1室1厅1卫" />
                <el-option label="2室1厅1卫" value="2室1厅1卫" />
                <el-option label="2室2厅1卫" value="2室2厅1卫" />
                <el-option label="3室1厅1卫" value="3室1厅1卫" />
                <el-option label="3室2厅2卫" value="3室2厅2卫" />
                <el-option label="4室2厅2卫" value="4室2厅2卫" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房源标题" prop="title">
              <el-input v-model="houseForm.title" placeholder="请输入房源标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在省份" prop="province">
              <el-input v-model="houseForm.province" placeholder="请输入省份" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在城市" prop="city">
              <el-input v-model="houseForm.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在区县" prop="district">
              <el-input v-model="houseForm.district" placeholder="请输入区县" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="houseForm.address" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="area">
              <el-input v-model="houseForm.area" placeholder="请输入建筑面积" suffix="㎡" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input v-model="houseForm.floor" placeholder="请输入楼层" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总楼层" prop="totalFloor">
              <el-input v-model="houseForm.totalFloor" placeholder="请输入总楼层" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="租金(元/月)" prop="rentPrice">
              <el-input v-model="houseForm.rentPrice" placeholder="请输入租金" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金(月)" prop="depositMonth">
              <el-input v-model="houseForm.depositMonth" placeholder="请输入押金月份" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="付款方式" prop="paymentWay">
              <el-select v-model="houseForm.paymentWay" placeholder="请选择付款方式">
                <el-option :label="paymentWayOptions[0]" :value="1" />
                <el-option :label="paymentWayOptions[1]" :value="2" />
                <el-option :label="paymentWayOptions[2]" :value="3" />
                <el-option :label="paymentWayOptions[3]" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租赁方式" prop="rentWay">
              <el-select v-model="houseForm.rentWay" placeholder="请选择租赁方式">
                <el-option :label="rentWayOptions[0]" :value="1" />
                <el-option :label="rentWayOptions[1]" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="装修情况" prop="decoration">
              <el-select v-model="houseForm.decoration" placeholder="请选择装修情况">
                <el-option :label="decorationOptions[0]" :value="1" />
                <el-option :label="decorationOptions[1]" :value="2" />
                <el-option :label="decorationOptions[2]" :value="3" />
                <el-option :label="decorationOptions[3]" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="朝向" prop="orientation">
              <el-select v-model="houseForm.orientation" placeholder="请选择朝向">
                <el-option :label="orientationOptions[0]" :value="1" />
                <el-option :label="orientationOptions[1]" :value="2" />
                <el-option :label="orientationOptions[2]" :value="3" />
                <el-option :label="orientationOptions[3]" :value="4" />
                <el-option :label="orientationOptions[4]" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否有电梯">
              <el-switch v-model="houseForm.hasElevator" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最短租期(月)" prop="minLeaseTerm">
              <el-input v-model="houseForm.minLeaseTerm" placeholder="请输入最短租期" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="房源描述" prop="description">
          <el-input type="textarea" v-model="houseForm.description" placeholder="请输入房源描述" :rows="3" />
        </el-form-item>

        <el-form-item label="配套设施">
          <el-checkbox-group v-model="facilities">
            <el-checkbox label="wifi" />
            <el-checkbox label="airConditioner" />
            <el-checkbox label="washingMachine" />
            <el-checkbox label="refrigerator" />
            <el-checkbox label="waterHeater" />
            <el-checkbox label="tv" />
            <el-checkbox label="sofa" />
            <el-checkbox label="bed" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="房源图片">
          <el-upload
            action="/api/upload/image"
            list-type="picture-card"
            :file-list="imageList"
            :on-success="handleImageUpload"
            :before-upload="beforeImageUpload"
          >
            <i class="el-icon-plus" />
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">保存修改</el-button>
          <el-button @click="goBack">返回列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHouseDetail, updateHouse } from '@/api/house'

const route = useRoute()
const router = useRouter()

const houseFormRef = ref(null)
const loading = ref(false)

const paymentWayOptions = ['押一付一', '押一付三', '押一付六', '年付']
const rentWayOptions = ['整租', '合租']
const decorationOptions = ['毛坯', '简单装修', '中等装修', '精装修']
const orientationOptions = ['东', '南', '西', '北', '南北通透']

const facilities = ref([])
const imageList = ref([])

const houseForm = reactive({
  id: '',
  houseNo: '',
  title: '',
  province: '',
  city: '',
  district: '',
  address: '',
  houseType: '',
  area: '',
  floor: '',
  totalFloor: '',
  hasElevator: '0',
  decoration: '',
  orientation: '',
  rentPrice: '',
  depositMonth: '',
  paymentWay: '',
  rentWay: '',
  description: '',
  minLeaseTerm: '',
  images: '',
  coverImage: ''
})

const initForm = async () => {
  const houseId = route.params.id
  if (houseId) {
    try {
      const res = await getHouseDetail(houseId)
      const data = res.data
      Object.assign(houseForm, data)
      
      if (data.facilities) {
        const facilityObj = JSON.parse(data.facilities)
        facilities.value = Object.keys(facilityObj).filter(key => facilityObj[key])
      }
      
      if (data.images) {
        const imageUrls = JSON.parse(data.images)
        imageList.value = imageUrls.map(url => ({ url }))
        if (imageUrls.length > 0) {
          houseForm.coverImage = imageUrls[0]
        }
      }
    } catch (error) {
      ElMessage.error('获取房源信息失败')
    }
  }
}

const handleImageUpload = (response) => {
  if (response.code === 200) {
    imageList.value.push({ url: response.data })
    if (imageList.value.length === 1) {
      houseForm.coverImage = response.data
    }
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

const submitForm = async () => {
  if (!houseFormRef.value) return
  
  await houseFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const facilityObj = {}
      facilities.value.forEach(f => { facilityObj[f] = true })
      houseForm.facilities = JSON.stringify(facilityObj)
      houseForm.images = JSON.stringify(imageList.value.map(item => item.url))
      
      await updateHouse(houseForm.id, houseForm)
      ElMessage.success('修改成功')
      router.push('/landlord/houses')
    } catch (error) {
      ElMessage.error('修改失败')
    } finally {
      loading.value = false
    }
  })
}

const goBack = () => {
  router.push('/landlord/houses')
}

onMounted(() => {
  initForm()
})
</script>

<style lang="scss" scoped>
.edit-house-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #999;
}

.house-form-card {
  max-width: 900px;
}

.house-form {
  padding: 20px 0;
}

.el-upload--picture-card {
  width: 140px;
  height: 140px;
}
</style>