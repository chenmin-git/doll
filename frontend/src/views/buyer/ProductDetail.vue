<template>
  <div class="product-detail-page">
    <el-container>
      <el-header class="custom-header">
        <div class="logo" @click="goBack" style="cursor: pointer;">
          <el-icon class="logo-icon"><ArrowLeft /></el-icon>
          <h2>返回上一页</h2>
        </div>
      </el-header>

      <el-main class="main-content">
        <div class="product-info-section" v-if="product">
          <div class="detail-layout">
            <div class="detail-left">
              <el-image 
                :src="getFirstImage(product.images)" 
                class="main-img" 
                :preview-src-list="getImageList(product.images)" 
                fit="cover"
              />
            </div>
            <div class="detail-right">
              <h1 class="product-title">{{ product.name }}</h1>
              <div class="price-box">
                <span class="currency">¥</span>
                <span class="amount">{{ product.price }}</span>
                <el-tag size="small" :type="product.stock > 0 ? 'success' : 'danger'" class="stock-tag">
                  {{ product.stock > 0 ? `库存: ${product.stock}` : '已售罄' }}
                </el-tag>
              </div>
              <div class="description-box">
                <h4>商品描述</h4>
                <p>{{ product.description || '暂无详细描述' }}</p>
              </div>
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  class="buy-btn" 
                  @click="buyNow"
                  :disabled="product.stock <= 0"
                >
                  立即购买
                </el-button>
                <el-button 
                  type="warning" 
                  plain 
                  class="cart-btn" 
                  @click="addToCart"
                  :disabled="product.stock <= 0"
                >
                  加入购物车
                </el-button>
                <el-button 
                  :type="isFavorited ? 'danger' : 'info'" 
                  plain 
                  class="fav-btn" 
                  @click="toggleFavorite"
                >
                  <el-icon><Star v-if="!isFavorited" /><StarFilled v-else /></el-icon>
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 评价部分 -->
        <div class="reviews-section">
          <div class="section-title">
            <h3>商品评价 ({{ reviews.length }})</h3>
          </div>
          <el-empty v-if="reviews.length === 0" description="暂无评价，快来抢先评价吧~" />
          <div class="review-list" v-else>
            <div v-for="r in reviews" :key="r.id" class="review-item">
              <div class="review-user">
                <el-avatar :size="40">{{ r.buyerNickname?.charAt(0) || '买' }}</el-avatar>
                <div class="user-info">
                  <span class="nickname">{{ r.buyerNickname || '匿名用户' }}</span>
                  <div class="review-header">
                    <el-rate v-model="r.rating" disabled size="small" />
                    <span class="review-time">{{ formatTime(r.createTime) }}</span>
                  </div>
                </div>
              </div>
              <p class="review-content">{{ r.content || '用户未填写评价内容' }}</p>
            </div>
          </div>
        </div>

        <!-- 结算确认弹窗 -->
        <el-dialog v-model="checkoutDialogVisible" title="确认订单信息" width="650px" class="custom-dialog">
          <div v-if="checkoutItems.length > 0" class="checkout-content">
            <h4 style="margin-top:0;">📝 商品清单</h4>
            <div class="checkout-list">
              <div v-for="item in checkoutItems" :key="item.productId" class="checkout-item">
                <el-image v-if="item.productImage" :src="item.productImage" class="checkout-img" fit="cover" />
                <div v-else class="checkout-img-placeholder">🧸</div>
                <div class="checkout-item-info">
                  <div class="item-name">{{ item.productName || '商品' }}</div>
                  <div class="item-price">单价: ¥{{ item.productPrice || '0.00' }} × {{ item.quantity }} = ¥{{ ((item.productPrice || 0) * item.quantity).toFixed(2) }}</div>
                </div>
              </div>
            </div>
            <el-divider />
            
            <!-- 收货地址选择 -->
            <div class="address-section">
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h4 style="margin: 0;">📍 收货地址</h4>
                <el-button size="small" type="primary" text @click="showAddressForm = true" v-if="!showAddressForm">
                  <el-icon><Plus /></el-icon> 新增地址
                </el-button>
              </div>
              
              <!-- 已有地址列表 -->
              <div v-if="!showAddressForm && savedAddresses.length > 0" class="address-list">
                <div 
                  v-for="addr in savedAddresses" 
                  :key="addr.id" 
                  class="address-card"
                  :class="{ 'selected': selectedAddressId === addr.id }"
                  @click="selectAddress(addr)"
                >
                  <div class="address-header">
                    <span class="receiver-name">{{ addr.receiver }}</span>
                    <span class="receiver-phone">{{ addr.phone }}</span>
                    <el-tag v-if="addr.isDefault === 1" type="danger" size="small" effect="plain">默认</el-tag>
                  </div>
                  <div class="address-detail">{{ addr.getFullAddress || addr.detail }}</div>
                  <el-icon v-if="selectedAddressId === addr.id" class="check-icon" color="#f5576c"><CircleCheckFilled /></el-icon>
                </div>
              </div>
              
              <!-- 新增/编辑地址表单 -->
              <el-form v-if="showAddressForm || savedAddresses.length === 0" :model="addressForm" label-width="90px" class="checkout-form">
                <el-form-item label="收货人">
                  <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="详细地址">
                  <el-input v-model="addressForm.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
                </el-form-item>
                <el-form-item>
                  <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
                </el-form-item>
                <el-form-item v-if="savedAddresses.length > 0">
                  <el-button size="small" @click="showAddressForm = false">取消</el-button>
                  <el-button size="small" type="primary" @click="saveNewAddress">保存地址</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <div class="checkout-summary">
              <span style="font-size:16px;">总计支付：</span>
              <span class="price-sym">¥</span><span class="price-num" style="font-size:28px">{{ checkoutTotalAmount }}</span>
            </div>
          </div>
          <template #footer>
            <el-button @click="checkoutDialogVisible = false">取消返回</el-button>
            <el-button type="warning" size="large" @click="confirmCheckout" :loading="isSubmittingOrder">确认支付并下单</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star, StarFilled, Plus, CircleCheckFilled } from '@element-plus/icons-vue'
import request from '../../utils/request'

const route = useRoute()
const router = useRouter()
const userId = ref(localStorage.getItem('userId'))
const productId = route.params.id
const fromMenu = computed(() => {
  const menu = String(route.query.fromMenu || '').trim()
  return menu || 'products'
})

const product = ref(null)
const reviews = ref([])
const isFavorited = ref(false)

// 结算相关
const checkoutDialogVisible = ref(false)
const checkoutItems = ref([])
const isSubmittingOrder = ref(false)
const addressForm = reactive({ receiver: '', phone: '', detail: '', isDefault: false })

// 地址管理
const savedAddresses = ref([])
const selectedAddressId = ref(null)
const showAddressForm = ref(false)

const checkoutTotalAmount = computed(() => {
  return checkoutItems.value.reduce((sum, item) => sum + (item.productPrice || 0) * item.quantity, 0).toFixed(2)
})

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push({ path: '/buyer', query: fromMenu.value ? { menu: fromMenu.value } : {} })
}

// 工具函数
const getFirstImage = (images) => {
  if (!images) return 'https://via.placeholder.com/400/FFB6C1/000000?text=No+Image'
  try {
    const list = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(list) && list.length > 0 ? list[0] : 'https://via.placeholder.com/400/FFB6C1/000000?text=No+Image'
  } catch { 
    return images || 'https://via.placeholder.com/400/FFB6C1/000000?text=No+Image'
  }
}

const getImageList = (images) => {
  if (!images) return []
  try {
    const list = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(list) ? list : [images]
  } catch { 
    return [images] 
  }
}

const formatTime = (ts) => {
  if (!ts) return ''
  return ts.replace('T', ' ').split('.')[0]
}

// 加载商品详情
const loadProduct = async () => {
  try {
    await request.post(`/product/${productId}/click`)
    const res = await request.get(`/product/${productId}`)
    product.value = res.data
    
    // 加载评价
    loadReviews()
    
    // 检查是否已收藏
    checkFavorite()
  } catch (error) {
    ElMessage.error('加载商品失败')
    console.error(error)
  }
}

// 加载评价列表
const loadReviews = async () => {
  try {
    const res = await request.get(`/review/product/${productId}`)
    const reviewList = res.data || []
    
    // 为每条评价加载买家信息
    for (let review of reviewList) {
      try {
        const userRes = await request.get(`/user/${review.buyerId}`)
        if (userRes.data) {
          review.buyerNickname = userRes.data.nickname || userRes.data.username
          review.buyerAvatar = userRes.data.avatar
        }
      } catch (e) {
        console.error('加载用户信息失败', e)
      }
    }
    
    reviews.value = reviewList
  } catch (error) {
    console.error('加载评价失败', error)
    reviews.value = []
  }
}

// 检查是否已收藏
const checkFavorite = async () => {
  if (!userId.value) return
  try {
    const res = await request.get(`/favorite/user/${userId.value}`)
    const favorites = res.data || []
    isFavorited.value = favorites.some(f => f.productId == productId)
  } catch (error) {
    console.error('检查收藏状态失败', error)
  }
}

// 切换收藏
const toggleFavorite = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await request.post('/favorite', { userId: userId.value, productId: productId })
    isFavorited.value = !isFavorited.value
    ElMessage.success(isFavorited.value ? '收藏成功' : '已取消收藏')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 加入购物车
const addToCart = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await request.post('/cart', { 
      productId: product.value.id, 
      quantity: 1, 
      userId: userId.value 
    })
    const quantity = res.data?.quantity
    ElMessage.success(quantity ? `已加入购物车，当前数量 ${quantity}` : '已加入购物车')
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 加载用户保存的地址
const loadSavedAddresses = async () => {
  if (!userId.value) return
  try {
    const res = await request.get(`/address/user/${userId.value}`)
    savedAddresses.value = res.data || []
    
    // 自动选择默认地址
    const defaultAddr = savedAddresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    } else if (savedAddresses.value.length > 0) {
      selectedAddressId.value = savedAddresses.value[0].id
    }
    
    // 如果没有地址，显示表单
    showAddressForm.value = savedAddresses.value.length === 0
  } catch (error) {
    console.error('加载地址失败', error)
    savedAddresses.value = []
    showAddressForm.value = true
  }
}

// 选择地址
const selectAddress = (addr) => {
  selectedAddressId.value = addr.id
}

// 保存新地址
const saveNewAddress = async () => {
  if (!addressForm.receiver || !addressForm.phone || !addressForm.detail) {
    return ElMessage.warning('请填写完整的地址信息')
  }
  try {
    const newAddr = {
      userId: userId.value,
      receiver: addressForm.receiver,
      phone: addressForm.phone,
      detail: addressForm.detail,
      province: '',
      city: '',
      district: '',
      isDefault: addressForm.isDefault ? 1 : 0
    }
    const res = await request.post('/address', newAddr)
    ElMessage.success('地址保存成功')
    
    // 重新加载地址列表
    await loadSavedAddresses()
    
    // 选中新添加的地址
    if (res.data && res.data.id) {
      selectedAddressId.value = res.data.id
    }
    
    // 重置表单并隐藏
    addressForm.receiver = ''
    addressForm.phone = ''
    addressForm.detail = ''
    addressForm.isDefault = false
    showAddressForm.value = false
  } catch (error) {
    ElMessage.error('保存地址失败')
    console.error(error)
  }
}

// 立即购买
const buyNow = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    return
  }
  if (!product.value || product.value.stock <= 0) {
    ElMessage.warning('商品库存不足')
    return
  }
  
  checkoutItems.value = [{
    productId: product.value.id,
    productName: product.value.name,
    productImage: getFirstImage(product.value.images),
    productPrice: product.value.price,
    sellerId: product.value.sellerId,
    quantity: 1
  }]
  
  // 加载地址列表
  await loadSavedAddresses()
  
  checkoutDialogVisible.value = true
}

// 确认结算
const confirmCheckout = async () => {
  // 如果使用已保存的地址
  if (!showAddressForm.value && savedAddresses.value.length > 0) {
    if (!selectedAddressId.value) {
      return ElMessage.warning('请选择收货地址')
    }
    const selectedAddr = savedAddresses.value.find(a => a.id === selectedAddressId.value)
    if (!selectedAddr) {
      return ElMessage.warning('请选择有效的收货地址')
    }
    
    isSubmittingOrder.value = true
    try {
      const orderData = {
        buyerId: userId.value,
        sellerId: checkoutItems.value[0].sellerId,
        productId: checkoutItems.value[0].productId,
        quantity: checkoutItems.value[0].quantity,
        totalAmount: checkoutTotalAmount.value,
        receiver: selectedAddr.receiver,
        phone: selectedAddr.phone,
        address: selectedAddr.detail || `${selectedAddr.province}${selectedAddr.city}${selectedAddr.district}${selectedAddr.detail}`
      }
      const res = await request.post('/order', orderData)
      // 模拟支付成功，将状态修改为 1(待发货)
      if (res.data && res.data.id) {
        await request.put(`/order/${res.data.id}/status`, null, { params: { status: 1 } })
      }
      ElMessage.success('下单支付成功！')
      checkoutDialogVisible.value = false
      router.push({ path: '/buyer', query: { menu: 'orders' } })
    } catch (err) {
      ElMessage.error('下单失败，可能库存不足')
    } finally {
      isSubmittingOrder.value = false
    }
  } else {
    // 使用新填写的地址
    if (!addressForm.receiver || !addressForm.phone || !addressForm.detail) {
      return ElMessage.warning('请填写完整的收货信息！')
    }
    
    // 先保存地址
    try {
      const newAddr = {
        userId: userId.value,
        receiver: addressForm.receiver,
        phone: addressForm.phone,
        detail: addressForm.detail,
        province: '',
        city: '',
        district: '',
        isDefault: addressForm.isDefault ? 1 : 0
      }
      await request.post('/address', newAddr)
    } catch (error) {
      console.error('保存地址失败', error)
    }
    
    isSubmittingOrder.value = true
    try {
      const orderData = {
        buyerId: userId.value,
        sellerId: checkoutItems.value[0].sellerId,
        productId: checkoutItems.value[0].productId,
        quantity: checkoutItems.value[0].quantity,
        totalAmount: checkoutTotalAmount.value,
        receiver: addressForm.receiver,
        phone: addressForm.phone,
        address: addressForm.detail
      }
      const res = await request.post('/order', orderData)
      // 模拟支付成功，将状态修改为 1(待发货)
      if (res.data && res.data.id) {
        await request.put(`/order/${res.data.id}/status`, null, { params: { status: 1 } })
      }
      ElMessage.success('下单支付成功！')
      checkoutDialogVisible.value = false
      router.push({ path: '/buyer', query: { menu: 'orders' } })
    } catch (err) {
      ElMessage.error('下单失败，可能库存不足')
    } finally {
      isSubmittingOrder.value = false
    }
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail-page { background: #faf8f6; min-height: 100vh; }
.custom-header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 0 40px; height: 70px; display: flex; align-items: center; color: white;
  position: sticky; top: 0; z-index: 100;
}
.logo { display: flex; align-items: center; gap: 10px; }
.logo h2 { font-size: 18px; margin: 0; }

.main-content { max-width: 1000px; margin: 0 auto; padding: 40px 20px; }

.detail-layout { display: flex; gap: 40px; background: white; padding: 32px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); }
.detail-left { width: 400px; flex-shrink: 0; }
.main-img { width: 400px; height: 400px; border-radius: 12px; object-fit: cover; border: 1px solid #f0ebe8; }

.detail-right { flex: 1; display: flex; flex-direction: column; }
.product-title { font-size: 28px; margin: 0 0 16px; color: #2d2520; }
.price-box { display: flex; align-items: baseline; gap: 4px; margin-bottom: 24px; }
.currency { font-size: 20px; color: #f5576c; font-weight: bold; }
.amount { font-size: 40px; color: #f5576c; font-weight: bold; }
.stock-tag { margin-left:16px; }

.description-box { flex: 1; background: #faf8f6; padding: 20px; border-radius: 12px; margin-bottom: 32px; }
.description-box h4 { margin: 0 0 12px; color: #9a8a82; }
.description-box p { margin: 0; line-height: 1.6; color: #5a4a42; white-space: pre-wrap; }

.action-buttons { display: flex; gap: 16px; }
.buy-btn { flex: 2; height: 50px; font-weight: bold; font-size: 16px; border-radius: 12px; }
.cart-btn, .fav-btn { flex: 1; height: 50px; border-radius: 12px; }

.reviews-section { margin-top: 40px; background: white; padding: 32px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); }
.section-title { margin-bottom: 24px; border-bottom: 1px solid #f0ebe8; padding-bottom: 12px; }
.section-title h3 { margin: 0; color: #2d2520; }

.review-item { padding: 24px 0; border-bottom: 1px dashed #f0ebe8; }
.review-item:last-child { border-bottom: none; }
.review-user { display: flex; gap: 16px; margin-bottom: 12px; }
.user-info { flex: 1; }
.nickname { font-weight: bold; color: #2d2520; font-size: 15px; margin-bottom: 4px; display: block; }
.review-header { display: flex; justify-content: space-between; align-items: center; }
.review-time { font-size: 12px; color: #a09088; }
.review-content { margin: 0; color: #5a4a42; line-height: 1.6; padding-left: 56px; }

/* 结算弹窗样式 */
.checkout-list { max-height: 300px; overflow-y: auto; padding-right: 10px; margin-bottom: 20px; }
.checkout-item { display: flex; align-items: center; gap: 16px; padding: 12px; background: #faf8f6; border-radius: 12px; margin-bottom: 12px; }
.checkout-img { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; }
.checkout-img-placeholder { width: 60px; height: 60px; border-radius: 8px; background: #f0ebe8; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.checkout-item-info { flex: 1; }
.item-name { font-weight: bold; color: #2d2520; margin-bottom: 4px; }
.item-price { color: #f5576c; font-size: 14px; }
.checkout-summary { text-align: right; margin-top: 24px; padding-top: 16px; border-top: 2px dashed #f0ebe8; }
.price-sym { color: #f5576c; font-weight: bold; font-size: 18px; margin-right: 4px; }
.price-num { color: #f5576c; font-weight: bold; }

/* 地址选择样式 */
.address-section { margin-bottom: 24px; }
.address-list { display: flex; flex-direction: column; gap: 12px; max-height: 300px; overflow-y: auto; }
.address-card {
  position: relative;
  padding: 16px;
  border: 2px solid #f0ebe8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #faf8f6;
}
.address-card:hover {
  border-color: #f5576c;
  background: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.1);
}
.address-card.selected {
  border-color: #f5576c;
  background: #fff5f7;
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.2);
}
.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.receiver-name {
  font-weight: bold;
  color: #2d2520;
  font-size: 15px;
}
.receiver-phone {
  color: #9a8a82;
  font-size: 14px;
}
.address-detail {
  color: #5a4a42;
  font-size: 14px;
  line-height: 1.5;
}
.check-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 24px;
}
.checkout-form {
  margin-top: 16px;
}
</style>
