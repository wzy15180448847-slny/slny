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

        <!-- <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否有电梯">
              <el-switch v-model="houseForm.hasElevator" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最短租期 (月)" prop="minLeaseTerm">
              <el-input v-model="houseForm.minLeaseTerm" placeholder="请输入最短租期" />
            </el-form-item>
          </el-col>
        </el-row> -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最短租期 (月)" prop="minLeaseTerm">
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
            action="/api/files/upload"
            list-type="picture-card"
            :file-list="imageList"
            :on-success="handleImageUpload"
            :before-upload="beforeImageUpload"
            :on-remove="handleImageRemove"
            :on-change="handleImageChange"
            :http-request="customUploadRequest"
            :on-error="handleImageError"
          >
            <div v-if="imageList.length < 9">
              <el-icon><Plus /></el-icon>
            </div>
          </el-upload>
          <!-- 调试信息 -->
          <div v-if="imageList.length > 0" style="margin-top: 10px; font-size: 12px; color: #999;">
            <p>图片数量：{{ imageList.length }}</p>
            <p v-for="(img, idx) in imageList" :key="idx" style="word-break: break-all;">
              图片{{ idx + 1 }}: 
              <span :style="{color: img.url ? 'green' : 'red'}">{{ img.url || '无 URL' }}</span>
            </p>
          </div>
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
import request from '@/utils/request'

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

// 确保图片列表响应式更新的辅助函数
const updateImageList = (newList) => {
  imageList.value = newList.map(item => ({
    url: item.url || '',
    name: item.name || 'image.jpg'
  }))
}

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

// 生成完整的 MinIO 图片 URL
const getFullImageUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整 URL（包括 blob URL），直接返回
  if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('blob:')) {
    return url
  }
  // 否则拼接 MinIO 基础 URL
  const minioBaseUrl = 'http://localhost:9000'
  // 确保路径以 / 开头
  return url.startsWith('/') ? `${minioBaseUrl}${url}` : `${minioBaseUrl}/${url}`
}

const initForm = async () => {
  const houseId = route.params.id
  if (houseId) {
    try {
      const data = await getHouseDetail(houseId)
      Object.assign(houseForm, data)
      
      if (data.facilities) {
        const facilityObj = JSON.parse(data.facilities)
        facilities.value = Object.keys(facilityObj).filter(key => facilityObj[key])
      }
      
      if (data.images) {
        try {
          const imageUrls = typeof data.images === 'string' ? JSON.parse(data.images) : data.images
          console.log('原始图片 URLs:', imageUrls)
          
          // 过滤掉 blob URL 和无效 URL
          const validUrls = imageUrls.filter(url => url && !url.startsWith('blob:'))
          console.log('过滤后的有效图片 URLs:', validUrls)
          
          const newList = validUrls.map((url, index) => {
            const fullUrl = getFullImageUrl(url)
            console.log(`图片 ${index + 1}: ${url} -> ${fullUrl}`)
            return {
              url: fullUrl,
              name: `image-${index}.jpg`
            }
          })
          updateImageList(newList)
          console.log('处理后的图片列表:', imageList.value)
          if (imageList.value.length > 0) {
            houseForm.coverImage = imageList.value[0].url
          }
        } catch (parseError) {
          console.error('解析图片 URLs 失败:', parseError)
          console.log('原始图片数据:', data.images)
        }
      }
    } catch (error) {
      ElMessage.error('获取房源信息失败')
    }
  }
}

const handleImageUpload = (response, file, fileList) => {
  console.log('=== 图片上传回调 ===')
  console.log('response:', response)
  console.log('file:', file)
  console.log('fileList:', fileList)
  
  // 检查后端返回的数据格式
  if (response && (response.code === 200 || response.success)) {
    const imageUrl = response.data || response.url
    console.log('从后端获取的图片 URL:', imageUrl)
    
    if (imageUrl) {
      // 使用统一的 URL 处理函数
      const fullUrl = getFullImageUrl(imageUrl)
      console.log('处理后的完整 URL:', fullUrl)
      
      // 更新图片列表，确保每个项目都有正确的 url 属性
      const newList = fileList.map(item => {
        let url = ''
        if (item.response) {
          // 已上传的文件，从响应中获取
          const itemUrl = item.response.data || item.response.url
          url = getFullImageUrl(itemUrl)
        } else if (item.url && !item.url.startsWith('blob:')) {
          // 已有 URL（排除 blob URL）
          url = item.url
        } else if (item.url && item.url.startsWith('blob:')) {
          // blob URL 保持原样
          url = item.url
        }
        
        return {
          url,
          name: item.name || 'image.jpg'
        }
      })
      updateImageList(newList)
      
      console.log('更新后的图片列表:', imageList.value)
      
      if (imageList.value.length === 1) {
        houseForm.coverImage = fullUrl
      }
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('后端未返回图片 URL')
    }
  } else {
    console.error('上传失败，后端返回:', response)
    ElMessage.error(response?.message || '图片上传失败')
  }
}

const handleImageRemove = (file) => {
  console.log('移除图片:', file)
  imageList.value = imageList.value.filter(item => item.url !== file.url)
}

const handleImageChange = (file, fileList) => {
  console.log('=== 图片列表变化 ===')
  console.log('当前文件:', file)
  console.log('当前文件 URL:', file.url)
  console.log('当前文件 response:', file.response)
  console.log('当前文件 status:', file.status)
  console.log('文件列表:', fileList)
  
  // 更新图片列表
  const newList = fileList.map(item => {
    let imageUrl = ''
    
    console.log('处理文件:', item.name)
    console.log('文件 URL:', item.url)
    console.log('文件 response:', item.response)
    console.log('文件 status:', item.status)
    
    // 如果文件已上传成功，使用后端返回的 URL
    if (item.response && (item.response.code === 200 || item.response.success)) {
      imageUrl = item.response.data || item.response.url
      console.log('从响应中获取 URL:', imageUrl)
    } else if (item.url && !item.url.startsWith('blob:')) {
      // 已有 URL（排除 blob URL）
      imageUrl = item.url
      console.log('使用已有 URL:', imageUrl)
    } else if (item.response) {
      // 从响应中获取
      imageUrl = item.response.data || item.response.url
      console.log('从 response 中获取 URL:', imageUrl)
    } else if (item.url && item.url.startsWith('blob:')) {
      // 保持 blob URL 用于预览
      imageUrl = item.url
      console.log('使用 blob URL:', imageUrl)
    }
    
    // 如果是后端返回的 URL，转换为完整 URL；否则保持原样
    const finalUrl = imageUrl && !imageUrl.startsWith('blob:') ? getFullImageUrl(imageUrl) : imageUrl
    console.log('最终 URL:', finalUrl)
    
    return {
      url: finalUrl,
      name: item.name || 'image.jpg'
    }
  })
  updateImageList(newList)
  console.log('更新后的图片列表:', imageList.value)
}

const handleImageError = (error, file, fileList) => {
  console.error('=== 图片上传错误 ===')
  console.error('错误信息:', error)
  console.error('文件:', file)
  ElMessage.error('图片上传失败：' + (error.message || '未知错误'))
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('请上传图片格式')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  
  // 创建本地预览 URL
  file.url = URL.createObjectURL(file)
  
  return true
}

// 自定义上传请求
const customUploadRequest = async (options) => {
  const { file, onSuccess, onError, onProgress } = options
  
  console.log('=== 自定义上传请求 ===')
  console.log('文件:', file)
  console.log('文件名:', file.name)
  console.log('文件大小:', file.size)
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const response = await request({
      url: '/files/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        const percent = Math.floor((progressEvent.loaded * 100) / progressEvent.total)
        console.log('上传进度:', percent)
        if (onProgress) {
          onProgress({ percent })
        }
      }
    })
    
    console.log('上传成功，原始响应:', response)
    console.log('响应类型:', typeof response)
    console.log('响应内容:', JSON.stringify(response))
    
    // 处理不同的响应格式
    let imageUrl = null
    
    if (typeof response === 'string') {
      // 如果返回的是字符串，直接使用
      console.log('返回的是字符串')
      imageUrl = response
    } else if (response && typeof response === 'object') {
      // 如果返回的是对象，检查是否有 code 字段（Result 格式）
      if (response.code !== undefined) {
        console.log('返回的是 Result 格式，code:', response.code)
        if (response.code === 200 || response.code === 0) {
          imageUrl = response.data
          console.log('从 data 字段获取 URL:', imageUrl)
        }
      } else if (response.data) {
        // 或者直接有 data 字段
        imageUrl = response.data
      } else if (response.url) {
        imageUrl = response.url
      }
    }
    
    if (imageUrl) {
      console.log('最终获取的图片 URL:', imageUrl)
      
      // 设置文件的 url 属性为后端返回的完整 URL
      file.url = imageUrl
      file.status = 'success'
      
      // 立即更新 imageList，用后端返回的 URL 替换 blob URL
      const fullImageUrl = getFullImageUrl(imageUrl)
      const newList = imageList.value.map(item => {
        if (item.name === file.name) {
          return {
            url: fullImageUrl,
            name: file.name
          }
        }
        return {
          url: item.url || '',
          name: item.name || 'image.jpg'
        }
      })
      updateImageList(newList)
      console.log('已更新图片列表中的 URL:', fullImageUrl)
      console.log('更新后的 imageList:', imageList.value)
      
      if (onSuccess) {
        // 传递包含 imageUrl 的对象给 onSuccess
        onSuccess({ code: 200, data: imageUrl }, file)
      }
    } else {
      console.error('无法从响应中提取图片 URL')
      console.error('响应内容:', JSON.stringify(response))
      if (onError) {
        onError(new Error('后端未返回有效的图片 URL'))
      }
    }
  } catch (error) {
    console.error('上传异常:', error)
    if (onError) {
      onError(error)
    }
  }
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
      
      // 过滤掉 blob URL，只保存后端返回的真实 URL
      const validImageUrls = imageList.value
        .filter(item => item.url && !item.url.startsWith('blob:'))
        .map(item => item.url)
      
      houseForm.images = JSON.stringify(validImageUrls)
      
      console.log('准备保存的图片 URLs:', validImageUrls)
      
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
  padding: 30px;
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
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
  width: 100%;
}

.house-form {
  padding: 20px 0;
}

.el-upload--picture-card {
  width: 140px;
  height: 140px;
}
</style>