<template>
  <div class="login-container">
    <!-- 动态背景粒子 -->
    <div class="bg-particles">
      <div v-for="i in 20" :key="i" class="particle" :style="particleStyle(i)"></div>
    </div>

    <!-- 装饰圆环 -->
    <div class="deco-ring ring-1"></div>
    <div class="deco-ring ring-2"></div>

    <div class="login-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="brand-panel">
        <div class="brand-content">
          <div class="brand-icon">🧸</div>
          <h1 class="brand-title">玩偶循环</h1>
          <p class="brand-subtitle">二手玩偶交易平台</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-icon">🔒</span>
              <span>安全交易保障</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">💝</span>
              <span>品质严格把控</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🚀</span>
              <span>极速便捷体验</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-panel">
        <div class="form-content">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账户</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" label-position="top" autocomplete="off" class="login-form">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                prefix-icon="User"
                placeholder="请输入用户名"
                size="large"
                autocomplete="off"
              />
            </el-form-item>
            <el-form-item label="密码" prop="password">
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

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>

            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <span v-if="!loading">登 录</span>
            </el-button>

            <div class="form-footer">
              <span>还没有账户？</span>
              <el-link type="primary" :underline="false" @click="$router.push('/register')">
                立即注册
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
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await request.post('/user/login', form)
      const { token, userId, role } = res.data
      localStorage.setItem('token', token)
      localStorage.setItem('userId', userId)
      localStorage.setItem('role', role)
      ElMessage.success('登录成功')
      if (role === 2) {
        router.push('/seller')
      } else if (role === 3) {
        router.push('/admin')
      } else {
        router.push('/buyer')
      }
    } catch (error) {
      ElMessage.error('登录失败，请检查用户名和密码')
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
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a0e0a 0%, #2e1510 40%, #3d1a15 70%, #2e1510 100%);
  position: relative;
  overflow: hidden;
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
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
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
.login-wrapper {
  display: flex;
  width: 880px;
  min-height: 520px;
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
  flex: 1;
  background: linear-gradient(160deg, #f5576c 0%, #ff8a5c 50%, #ffb88c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
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
  font-size: 64px;
  margin-bottom: 16px;
  animation: bounce-gentle 3s ease-in-out infinite;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
}

@keyframes bounce-gentle {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 15px;
  opacity: 0.85;
  margin: 0 0 36px 0;
  letter-spacing: 1px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.9;
  background: rgba(255, 255, 255, 0.15);
  padding: 10px 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(5px);
}

.feature-icon {
  font-size: 18px;
}

/* 表单面板 */
.form-panel {
  flex: 1;
  background: #1c1210;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.form-content {
  width: 100%;
  max-width: 340px;
}

.form-header {
  margin-bottom: 36px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #f5ede8;
  margin: 0 0 8px 0;
}

.form-header p {
  font-size: 14px;
  color: #a09088;
  margin: 0;
}

/* 表单样式覆盖 */
.login-form :deep(.el-form-item__label) {
  color: #b8a59c;
  font-size: 13px;
  font-weight: 500;
  padding-bottom: 4px;
}

.login-form :deep(.el-input__wrapper) {
  background: #261c18;
  border-radius: 12px;
  box-shadow: none !important;
  border: 1px solid #3d2e28;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #f5576c;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #ff8a5c;
  box-shadow: 0 0 0 3px rgba(255, 138, 92, 0.15) !important;
}

.login-form :deep(.el-input__inner) {
  color: #f0e6df;
  font-size: 14px;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #6b574d;
}

.login-form :deep(.el-input__prefix .el-icon) {
  color: #f5576c;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-options :deep(.el-checkbox__label) {
  color: #a09088;
  font-size: 13px;
}

.form-options :deep(.el-checkbox__inner) {
  background: #261c18;
  border-color: #3d2e28;
}

.form-options :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: #f5576c;
  border-color: #f5576c;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #f5576c, #ff8a5c);
  border: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #ff8a5c, #ffb88c);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245, 87, 108, 0.4);
}

.login-btn:hover::before {
  opacity: 1;
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn span {
  position: relative;
  z-index: 1;
}

/* 底部链接 */
.form-footer {
  text-align: center;
  margin-top: 28px;
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
  .login-wrapper {
    flex-direction: column;
    width: 90%;
    max-width: 420px;
    min-height: auto;
  }

  .brand-panel {
    padding: 32px 24px;
  }

  .brand-features {
    display: none;
  }

  .brand-icon {
    font-size: 48px;
  }

  .brand-title {
    font-size: 24px;
  }

  .form-panel {
    padding: 32px 24px;
  }
}
</style>
