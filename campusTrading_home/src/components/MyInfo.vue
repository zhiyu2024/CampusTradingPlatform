<template>
  <div class="my-info-page">
    <h2>我的个人信息</h2>
    <div class="info-card">
      <el-form label-width="100px" label-position="left">
        <el-form-item label="学号">
          <el-input v-model="userInfo.studentNo" disabled />
        </el-form-item>
        
        <el-form-item label="昵称">
          <el-input v-model="userInfo.nickname" placeholder="请输入昵称" />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="校区">
          <el-input v-model="userInfo.campus" placeholder="请输入校区" />
        </el-form-item>
        
        <el-form-item label="头像">
          <div class="avatar-display">
            <img v-if="avatarPreviewUrl" :src="avatarPreviewUrl" alt="头像" class="avatar-preview" />
            <img v-else-if="userInfo.avatar" :src="getAvatarUrl(userInfo.avatar)" alt="头像" class="avatar-preview" />
            <div v-else class="avatar-empty">暂无头像</div>
          </div>
          <el-upload
            class="avatar-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            :show-file-list="false"
            accept="image/*"
          >
            <el-button type="primary" size="small">更换头像</el-button>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveInfo" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const userInfo = reactive({
  studentNo: '',
  nickname: '',
  phone: '',
  avatar: '',
  campus: '',
  role: '',
  status: '',
  createdAt: ''
})

const saving = ref(false)
const avatarFile = ref(null)
const avatarPreviewUrl = ref('')

// 获取头像URL（带时间戳防缓存）
const getAvatarUrl = (url) => {
  if (!url) return '/头像.png'
  // 如果已经是完整URL
  if (url.startsWith('http')) return `${url}?t=${Date.now()}`
  // 数据库已经存了 /res/images/xxx，直接用
  return `${url}?t=${Date.now()}`
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.code === 200) {
      Object.assign(userInfo, res.data)
      console.log('获取用户信息:', userInfo)
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
    ElMessage.error('获取用户信息失败')
  }
}

// 处理头像选择
const handleAvatarChange = (file) => {
  if (!file?.raw) return
  
  // 验证
  if (!file.raw.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  if (file.raw.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  avatarFile.value = file.raw
  
  // 立即预览
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreviewUrl.value = e.target.result  // 临时显示预览
  }
  reader.readAsDataURL(file.raw)
}

// 保存信息
const saveInfo = async () => {
  saving.value = true
  
  const formData = new FormData()
  // ✅ 确保所有字段都有值，避免null
  formData.append('nickname', userInfo.nickname || '')
  formData.append('phone', userInfo.phone || '')
  formData.append('campus', userInfo.campus || '')
  
  if (avatarFile.value) {
    formData.append('avatar', avatarFile.value)
    console.log('添加了头像文件:', avatarFile.value.name)
  }

  console.log('📤 发送的请求数据:')
  for (let [key, value] of formData.entries()) {
    console.log(`  ${key}:`, value)
  }

  try {
    const res = await request.post('/user/update', formData)
    
    console.log('📥 后端返回:', res)
    
    if (res.code === 200) {
      ElMessage.success('保存成功！')
      
      // ✅ 立即刷新用户信息
      await getUserInfo()
      
      // ✅ 通知其他组件刷新（如PersonalCenter）
      window.dispatchEvent(new Event('user-info-updated'))
      
      avatarFile.value = null
      avatarPreviewUrl.value = ''
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (err) {
    console.error('保存失败:', err)
    ElMessage.error(err.response?.data?.msg || err.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 初始化
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.my-info-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
}

.info-card {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.avatar-display {
  margin-bottom: 10px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-empty {
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 50%;
  color: #999;
}

.avatar-uploader {
  display: inline-block;
}
</style>