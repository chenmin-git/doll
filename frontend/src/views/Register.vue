<template>
  <div class="register-container">
    <!-- 动态背景粒子 -->
    <div class="bg-particles">
      <div v-for="i in 20" :key="i" class="particle" :style="particleStyle(i)"></div>
    </div>

    <!-- 装饰圆环 -->
    <div class="deco-ring ring-1"></div>
    <div class="deco-ring ring-2"></div>

    <div class="register-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="brand-panel">
        <div class="brand-content">
          <div class="brand-icon">🧸</div>
          <h1 class="brand-title">玩偶循环</h1>
          <p class="brand-subtitle">加入我们，开启玩偶之旅</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-icon">🎭</span>
              <span>海量精品玩偶</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🤝</span>
              <span>买卖双方保障</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🌟</span>
              <span>社区互动交流</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="form-panel">
        <div class="form-content">
          <div class="form-header">
            <h2>创建账户</h2>
            <p>填写信息完成注册</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" label-position="top" autocomplete="off" class="register-form">
            <div class="form-row">
              <el-form-item label="用户名" prop="username" class="form-item-half">
                <el-input
                  v-model="form.username"
                  prefix-icon="User"
                  placeholder="请输入用户名"
                  size="large"
                  autocomplete="off"
                />
              </el-form-item>
              <el-form-item label="密码" prop="password" class="form-item-half">
                <el-input
                  v-model="form.password"
                  type="password"
                  prefix-icon="Lock"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  autocomplete="new-password"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="昵称" prop="nickname" class="form-item-half">
                <el-input
                  v-model="form.nickname"
                  prefix-icon="UserFilled"
                  placeholder="请输入昵称"
                  size="large"
                  autocomplete="off"
                />
              </el-form-item>
              <el-form-item label="手机号" prop="phone" class="form-item-half">
                <el-input
                  v-model="form.phone"
                  prefix-icon="Phone"
                  placeholder="请输入手机号"
                  size="large"
                  autocomplete="off"
                />
              </el-form-item>
            </div>

            <el-form-item label="注册角色" prop="role">
              <div class="role-selector">
                <div
                  class="role-card"
                  :class="{ active: form.role === 1 }"
                  @click="form.role = 1"
                >
                  <span class="role-icon">🛒</span>
                  <span class="role-name">买家</span>
                  <span class="role-desc">浏览购买心仪玩偶</span>
                </div>
                <div
                  class="role-card"
                  :class="{ active: form.role === 2 }"
                  @click="form.role = 2"
                >
                  <span class="role-icon">🏪</span>
                  <span class="role-name">卖家</span>
                  <span class="role-desc">开店出售闲置玩偶</span>
                </div>
              </div>
            </el-form-item>

            <transition name="slide-fade">
              <el-form-item v-if="form.role === 2" label="店铺名称" prop="shopName">
                <el-input
                  v-model="form.shopName"
                  prefix-icon="Shop"
                  placeholder="请输入您的店铺名称"
                  size="large"
                />
              </el-form-item>
            </transition>

            <el-button
              type="primary"
              size="large"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              <span v-if="!loading">立即注册</span>
            </el-button>

            <div class="form-footer">
              <span>已有账户？</span>
              <el-link type="primary" :underline="false" @click="$router.push('/login')">
                返回登录
              </el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  role: 1,
  shopName: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await request.post('/user/register', form)
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error) {
      ElMessage.error('注册失败，请重试')
    } finally {
      loading.value = false
    }
  })
}

const particleStyle = (i) => {
  const size = Math.random() * 6 + 3
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 6}s`,
    animationDuration: `${Math.random() * 10 + 8}s`
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a0e0a 0%, #2e1510 40%, #3d1a15 70%, #2e1510 100%);
  position: relative;
  overflow: hidden;
  padding: 24px;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 粒子背景 */
.bg-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 138, 92, 0.3);
  animation: float-particle linear infinite;
}

@keyframes float-particle {
  0%, 100% {
    transform: translateY(0) translateX(0) scale(1);
    opacity: 0;
  }
  10% { opacity: 1; }
  90% { opacity: 1; }
  50% {
    transform: translateY(-150px) translateX(50px) scale(1.5);
  }
}

/* 装饰圆环 */
.deco-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(245, 87, 108, 0.12);
  pointer-events: none;
}

.ring-1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  animation: rotate-ring 30s linear infinite;
}

.ring-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  animation: rotate-ring 25s linear infinite reverse;
}

@keyframes rotate-ring {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 主卡片 */
.register-wrapper {
  display: flex;
  width: 960px;
  min-height: 580px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow:
    0 25px 60px rgba(0, 0, 0, 0.5),
    0 0 120px rgba(245, 87, 108, 0.08);
  position: relative;
  z-index: 1;
  animation: card-enter 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes card-enter {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 品牌面板 */
.brand-panel {
  width: 340px;
  flex-shrink: 0;
  background: linear-gradient(160deg, #f5576c 0%, #ff8a5c 50%, #ffb88c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 36px;
  position: relative;
  overflow: hidden;
}

.brand-panel::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.08) 0%, transparent 40%);
}

.brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
}

.brand-icon {
  font-size: 56px;
  margin-bottom: 12px;
  animation: bounce-gentle 3s ease-in-out infinite;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
}

@keyframes bounce-gentle {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.85;
  margin: 0 0 32px 0;
  letter-spacing: 1px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  opacity: 0.9;
  background: rgba(255, 255, 255, 0.15);
  padding: 10px 18px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(5px);
}

.feature-icon {
  font-size: 16px;
}

/* 表单面板 */
.form-panel {
  flex: 1;
  background: #1c1210;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 48px;
}

.form-content {
  width: 100%;
  max-width: 480px;
}

.form-header {
  margin-bottom: 28px;
}

.form-header h2 {
  font-size: 26px;
  font-weight: 700;
  color: #f5ede8;
  margin: 0 0 6px 0;
}

.form-header p {
  font-size: 14px;
  color: #a09088;
  margin: 0;
}

/* 表单行 */
.form-row {
  display: flex;
  gap: 16px;
}

.form-item-half {
  flex: 1;
}

/* 表单样式覆盖 */
.register-form :deep(.el-form-item__label) {
  color: #b8a59c;
  font-size: 13px;
  font-weight: 500;
  padding-bottom: 4px;
}

.register-form :deep(.el-input__wrapper) {
  background: #261c18;
  border-radius: 12px;
  box-shadow: none !important;
  border: 1px solid #3d2e28;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: #f5576c;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: #ff8a5c;
  box-shadow: 0 0 0 3px rgba(255, 138, 92, 0.15) !important;
}

.register-form :deep(.el-input__inner) {
  color: #f0e6df;
  font-size: 14px;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #6b574d;
}

.register-form :deep(.el-input__prefix .el-icon) {
  color: #f5576c;
}

/* 角色选择器 */
.role-selector {
  display: flex;
  gap: 16px;
  width: 100%;
}

.role-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 16px 12px;
  border-radius: 14px;
  background: #261c18;
  border: 2px solid #3d2e28;
  cursor: pointer;
  transition: all 0.3s ease;
}

.role-card:hover {
  border-color: #f5576c;
  background: #30221c;
}

.role-card.active {
  border-color: #ff8a5c;
  background: linear-gradient(135deg, rgba(245, 87, 108, 0.15), rgba(255, 138, 92, 0.1));
  box-shadow: 0 0 0 3px rgba(255, 138, 92, 0.12);
}

.role-icon {
  font-size: 28px;
}

.role-name {
  font-size: 15px;
  font-weight: 600;
  color: #f0e6df;
}

.role-desc {
  font-size: 11px;
  color: #a09088;
}

/* 滑入动画 */
.slide-fade-enter-active {
  transition: all 0.35s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.25s ease-in;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #f5576c, #ff8a5c);
  border: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  margin-top: 8px;
}

.register-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #ff8a5c, #ffb88c);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245, 87, 108, 0.4);
}

.register-btn:hover::before {
  opacity: 1;
}

.register-btn:active {
  transform: translateY(0);
}

.register-btn span {
  position: relative;
  z-index: 1;
}

/* 底部链接 */
.form-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #a09088;
}

.form-footer :deep(.el-link) {
  font-size: 14px;
  font-weight: 600;
  margin-left: 4px;
}

.form-footer :deep(.el-link .el-link__inner) {
  color: #ff8a5c;
}

.form-footer :deep(.el-link:hover .el-link__inner) {
  color: #ffb88c;
}

/* 响应式 */
@media (max-width: 768px) {
  .register-wrapper {
    flex-direction: column;
    width: 100%;
    max-width: 440px;
    min-height: auto;
  }

  .brand-panel {
    width: 100%;
    padding: 28px 24px;
  }

  .brand-features {
    display: none;
  }

  .brand-icon {
    font-size: 42px;
  }

  .brand-title {
    font-size: 22px;
  }

  .form-panel {
    padding: 28px 24px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
