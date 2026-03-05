<template>
  <div class="seller-home">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="custom-header">
        <div class="logo">
          <el-icon class="logo-icon"><Shop /></el-icon>
          <h2>卖家中心</h2>
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" @select="handleMenuSelect" class="custom-menu">
          <el-menu-item index="products">商品管理</el-menu-item>
          <el-menu-item index="auctions">拍卖活动</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="reviews">评价管理</el-menu-item>
          <el-menu-item index="complaints">举报投诉</el-menu-item>
          <!-- 个人中心入口已从菜单隐藏，仅通过右上角进入 -->
        </el-menu>
        <div class="user-actions">
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-avatar-trigger">
              <el-avatar :size="36" :src="profileForm.avatar || ''"><span v-if="!profileForm.avatar" style="font-size:16px">🧸</span></el-avatar>
              <span class="user-nickname">{{ profileForm.nickname || '卖家' }}</span>
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" style="color: #f56c6c;">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">

        <!-- ==================== 商品管理 ==================== -->
        <div v-if="activeMenu === 'products'" class="page-section">
          <div class="section-header">
            <h3>📦 商品上架管理</h3>
            <el-button type="primary" @click="showAddProductDialog" class="action-btn">
              <el-icon><Plus /></el-icon> 上架新商品
            </el-button>
          </div>

          <!-- 商品统计卡片 -->
          <div class="stats-row">
            <div class="stat-card">
              <div class="stat-value">{{ products.length }}</div>
              <div class="stat-label">全部商品</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ products.filter(p => p.status === 1).length }}</div>
              <div class="stat-label">已上架</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ products.filter(p => p.status === 0).length }}</div>
              <div class="stat-label">已下架</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ products.filter(p => p.status === 2).length }}</div>
              <div class="stat-label">定时上架</div>
            </div>
          </div>

          <el-table :data="products" class="custom-table" stripe>
            <el-table-column label="商品图片" width="90">
              <template #default="scope">
                <el-image
                  v-if="getFirstImage(scope.row.images)"
                  :src="getFirstImage(scope.row.images)"
                  style="width: 60px; height: 60px; border-radius: 8px"
                  fit="cover"
                  :preview-src-list="getImageList(scope.row.images)"
                />
                <div v-else class="no-img">无图</div>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" min-width="140" />
            <el-table-column label="价格" width="100">
              <template #default="scope">
                <span class="price-text">¥{{ scope.row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="80" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success" effect="light" round>上架中</el-tag>
                <el-tag v-else-if="scope.row.status === 0" type="info" effect="light" round>已下架</el-tag>
                <el-tag v-else type="warning" effect="light" round>定时上架</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="定时上架时间" width="160">
              <template #default="scope">
                <span v-if="scope.row.status === 2">{{ scope.row.publishTime || '—' }}</span>
                <span v-else>—</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button size="small" type="primary" plain @click="editProduct(scope.row)">编辑</el-button>
                <el-button v-if="scope.row.status === 1" size="small" type="warning" plain @click="toggleProductStatus(scope.row, 0)">下架</el-button>
                <el-button v-else-if="scope.row.status === 0" size="small" type="success" plain @click="toggleProductStatus(scope.row, 1)">上架</el-button>
                <el-button size="small" type="danger" plain @click="deleteProduct(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 拍卖活动 ==================== -->
        <div v-if="activeMenu === 'auctions'" class="page-section">
          <div class="section-header">
            <h3>🔨 拍卖活动管理</h3>
            <el-button type="primary" @click="showAddAuctionDialog" class="action-btn">
              <el-icon><Plus /></el-icon> 创建拍卖
            </el-button>
          </div>

          <!-- 拍卖数据分析 -->
          <div class="stats-row">
            <div class="stat-card">
              <div class="stat-value">{{ auctions.length }}</div>
              <div class="stat-label">全部拍卖</div>
            </div>
            <div class="stat-card stat-active">
              <div class="stat-value">{{ auctions.filter(a => a.status === 1).length }}</div>
              <div class="stat-label">进行中</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ auctions.filter(a => a.status === 2).length }}</div>
              <div class="stat-label">已结束</div>
            </div>
            <div class="stat-card stat-revenue">
              <div class="stat-value">¥{{ totalAuctionRevenue }}</div>
              <div class="stat-label">拍卖总收入</div>
            </div>
          </div>

          <el-table :data="auctions" class="custom-table" stripe>
            <el-table-column prop="productId" label="商品ID" width="90" />
            <el-table-column label="起拍价" width="100">
              <template #default="scope">
                <span class="price-text">¥{{ scope.row.startPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="当前价" width="100">
              <template #default="scope">
                <span class="price-text highlight">¥{{ scope.row.currentPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="info" effect="light" round>未开始</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success" effect="light" round>进行中</el-tag>
                <el-tag v-else type="danger" effect="light" round>已结束</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="开始时间" min-width="160">
              <template #default="scope">{{ scope.row.startTime || '—' }}</template>
            </el-table-column>
            <el-table-column label="结束时间" min-width="160">
              <template #default="scope">{{ scope.row.endTime || '—' }}</template>
            </el-table-column>
            <el-table-column label="中标者ID" width="100">
              <template #default="scope">{{ scope.row.winnerId || '—' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 0" size="small" type="success" plain @click="startAuction(scope.row)">开始</el-button>
                <el-button v-if="scope.row.status === 1" size="small" type="warning" plain @click="endAuction(scope.row)">结束</el-button>
                <el-button size="small" type="danger" plain @click="deleteAuction(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 订单管理 ==================== -->
        <div v-if="activeMenu === 'orders'" class="page-section">
          <div class="section-header">
            <h3>📋 订单管理</h3>
          </div>

          <div class="stats-row">
            <div class="stat-card">
              <div class="stat-value">{{ orders.length }}</div>
              <div class="stat-label">全部订单</div>
            </div>
            <div class="stat-card stat-active">
              <div class="stat-value">{{ orders.filter(o => o.status === 1).length }}</div>
              <div class="stat-label">待发货</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ orders.filter(o => o.status === 3).length }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>

          <el-table :data="orders" class="custom-table" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="150" />
            <el-table-column label="商品信息" min-width="200">
              <template #default="scope">
                <div v-for="item in scope.row.items" :key="item.id" class="order-product-item">
                  <el-image :src="item.productImage" class="order-product-img">
                    <template #error><div class="img-error">🧸</div></template>
                  </el-image>
                  <div class="order-product-info">
                    <div class="name">{{ item.productName }}</div>
                    <div class="count">x{{ item.quantity }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="金额" width="100">
              <template #default="scope">
                <span class="price-text">¥{{ scope.row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" effect="light" round>待支付</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="warning" effect="light" round>待发货</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="info" effect="light" round>待收货</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="success" effect="light" round>已完成</el-tag>
                <el-tag v-else type="danger" effect="light" round>已取消</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" min-width="160" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 1" size="small" type="primary" plain @click="shipOrder(scope.row.id)">发货</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 评价管理 ==================== -->
        <div v-if="activeMenu === 'reviews'" class="page-section">
          <div class="section-header">
            <h3>⭐ 评价管理</h3>
          </div>

          <div class="stats-row">
            <div class="stat-card stat-active">
              <div class="stat-value">{{ averageRating }}</div>
              <div class="stat-label">平均评分</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ reviews.length }}</div>
              <div class="stat-label">总评价数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ reviews.filter(r => r.rating >= 4).length }}</div>
              <div class="stat-label">好评数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ reviews.filter(r => r.rating <= 2).length }}</div>
              <div class="stat-label">差评数</div>
            </div>
          </div>

          <el-table :data="reviews" class="custom-table" stripe>
            <el-table-column prop="productId" label="商品ID" width="90" />
            <el-table-column label="评分" width="180">
              <template #default="scope">
                <el-rate v-model="scope.row.rating" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" label="评价时间" width="160" />
          </el-table>
        </div>

        <!-- ==================== 举报投诉 ==================== -->
        <div v-if="activeMenu === 'complaints'" class="page-section">
          <div class="section-header">
            <h3>🚨 举报投诉管理</h3>
          </div>

          <div class="stats-row">
            <div class="stat-card">
              <div class="stat-value">{{ complaints.length }}</div>
              <div class="stat-label">总投诉</div>
            </div>
            <div class="stat-card stat-active">
              <div class="stat-value">{{ complaints.filter(c => c.status === 0).length }}</div>
              <div class="stat-label">待处理</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ complaints.filter(c => c.status === 2).length }}</div>
              <div class="stat-label">已处理</div>
            </div>
          </div>

          <el-table :data="complaints" class="custom-table" stripe>
            <el-table-column prop="reason" label="投诉原因" min-width="200" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="danger" effect="light" round>待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="warning" effect="light" round>处理中</el-tag>
                <el-tag v-else type="success" effect="light" round>已处理</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="处理结果" min-width="160" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.result || '—' }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="投诉时间" width="160" />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status < 2" size="small" type="primary" plain @click="openComplaintDialog(scope.row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 个人中心 ==================== -->
        <div v-if="activeMenu === 'profile'" class="page-section">
          <div class="section-header">
            <h3>👤 个人中心</h3>
          </div>

          <div class="profile-container">
            <div class="profile-card">
              <div class="profile-avatar">
                <el-avatar :size="80" :src="profileForm.avatar || ''">
                  <span style="font-size: 32px">🧸</span>
                </el-avatar>
              </div>
              <el-form :model="profileForm" label-width="100px" class="profile-form">
                <el-form-item label="店铺名称">
                  <el-input v-model="profileForm.shopName" placeholder="请输入店铺名称" />
                </el-form-item>
                <el-form-item label="店铺描述">
                  <el-input v-model="profileForm.shopDescription" type="textarea" :rows="3" placeholder="请输入店铺描述" />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="联系电话">
                  <el-input v-model="profileForm.phone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item label="头像上传">
                  <el-upload
                    class="avatar-uploader"
                    action="/api/upload"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                  >
                    <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" style="width:100px; height:100px; border-radius:10px; object-fit: cover;" />
                    <el-icon v-else class="avatar-uploader-icon" style="font-size: 28px; width:100px; height:100px; border:1px dashed #d9d9d9; border-radius:10px; display:flex; align-items:center; justify-content:center"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" class="action-btn">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>

      </el-main>
    </el-container>

    <!-- ==================== 上架/编辑商品弹窗 ==================== -->
    <el-dialog v-model="productDialogVisible" :title="isEditProduct ? '编辑商品' : '上架商品'" width="520px" class="custom-dialog">
      <el-form :model="productForm" label-width="90px">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="productForm.price" type="number" placeholder="请输入价格">
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="库存">
          <el-input v-model="productForm.stock" type="number" placeholder="请输入库存数量" />
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="image-upload-area">
            <el-upload
              action="/api/upload"
              :headers="uploadHeaders"
              list-type="picture-card"
              :file-list="productFileList"
              :on-success="handleProductImageSuccess"
              :on-remove="handleProductImageRemove"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="上架方式">
          <el-radio-group v-model="productForm.status">
            <el-radio :value="1">立即上架</el-radio>
            <el-radio :value="2">定时上架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="productForm.status === 2" label="上架时间">
          <el-date-picker v-model="productForm.publishTime" type="datetime" placeholder="选择上架时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProduct">确定</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 创建拍卖弹窗 ==================== -->
    <el-dialog v-model="auctionDialogVisible" title="创建拍卖活动" width="480px" class="custom-dialog">
      <el-form :model="auctionForm" label-width="90px">
        <el-form-item label="选择商品">
          <el-select v-model="auctionForm.productId" placeholder="请选择拍卖商品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="起拍价">
          <el-input v-model="auctionForm.startPrice" type="number" placeholder="请输入起拍价">
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="auctionForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="auctionForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auctionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAuction">确定</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 处理投诉弹窗 ==================== -->
    <el-dialog v-model="complaintDialogVisible" title="处理举报投诉" width="450px" class="custom-dialog">
      <el-form :model="complaintHandleForm" label-width="90px">
        <el-form-item label="投诉原因">
          <el-input :model-value="complaintHandleForm.reason" disabled type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="处理结果">
          <el-input v-model="complaintHandleForm.result" type="textarea" :rows="3" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="complaintDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplaintHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Shop, SwitchButton, Plus, Delete, CaretBottom, User } from '@element-plus/icons-vue'
import request from '../../utils/request'

const router = useRouter()
const sellerId = ref(localStorage.getItem('userId'))
const activeMenu = ref('products')

// ============ 数据 ============
const products = ref([])
const auctions = ref([])
const orders = ref([])
const reviews = ref([])
const complaints = ref([])

// ============ 弹窗控制 ============
const productDialogVisible = ref(false)
const auctionDialogVisible = ref(false)
const complaintDialogVisible = ref(false)
const isEditProduct = ref(false)
const editingProductId = ref(null)

// ============ 表单 ============
const productForm = reactive({
  name: '', description: '', price: 0, stock: 0,
  sellerId: sellerId.value, status: 1, publishTime: null,
  imageList: []
})
const productFileList = ref([])

const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    profileForm.avatar = res.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const updateProductImageList = (fileList) => {
  productForm.imageList = fileList
    .map(f => f.url || (f.response && f.response.data))
    .filter(url => url)
}

const handleProductImageSuccess = (res, file, fileList) => {
  if (res.code === 200) {
    file.url = res.data
    updateProductImageList(fileList)
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleProductImageRemove = (file, fileList) => {
  updateProductImageList(fileList)
}

const auctionForm = reactive({
  productId: null, startPrice: 0,
  sellerId: sellerId.value,
  startTime: null, endTime: null,
  currentPrice: 0, status: 0
})

const profileForm = reactive({
  shopName: '', shopDescription: '', nickname: '', phone: '', avatar: ''
})

const complaintHandleForm = reactive({
  id: null, reason: '', result: ''
})

// ============ 计算属性 ============
const averageRating = computed(() => {
  if (reviews.value.length === 0) return '0.0'
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const totalAuctionRevenue = computed(() => {
  return auctions.value
    .filter(a => a.status === 2 && a.currentPrice)
    .reduce((sum, a) => sum + Number(a.currentPrice), 0)
    .toFixed(2)
})

// ============ 菜单切换 ============
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'products') loadProducts()
  else if (index === 'auctions') loadAuctions()
  else if (index === 'orders') loadOrders()
  else if (index === 'reviews') loadReviews()
  else if (index === 'complaints') loadComplaints()
  else if (index === 'profile') loadProfile()
}

const handleUserCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'profile') {
    activeMenu.value = 'profile'
    loadProfile()
  }
}

// ============ 商品管理 ============
const loadProducts = async () => {
  try {
    const res = await request.get('/product/recommend', { params: { userId: sellerId.value } })
    products.value = res.data.records.filter(p => p.sellerId == sellerId.value)
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const getFirstImage = (images) => {
  if (!images) return null
  try {
    const list = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(list) && list.length > 0 ? list[0] : null
  } catch { return null }
}

const getImageList = (images) => {
  if (!images) return []
  try {
    const list = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(list) ? list : []
  } catch { return [] }
}

const showAddProductDialog = () => {
  isEditProduct.value = false
  editingProductId.value = null
  Object.assign(productForm, {
    name: '', description: '', price: 0, stock: 0,
    sellerId: sellerId.value, status: 1, publishTime: null, imageList: []
  })
  productFileList.value = []
  productDialogVisible.value = true
}

const editProduct = (product) => {
  isEditProduct.value = true
  editingProductId.value = product.id
  const dbImages = getImageList(product.images)
  Object.assign(productForm, {
    name: product.name,
    description: product.description,
    price: product.price,
    stock: product.stock,
    sellerId: sellerId.value,
    status: product.status,
    publishTime: product.publishTime,
    imageList: dbImages
  })
  productFileList.value = dbImages.map((url, idx) => ({ name: `img_${idx}`, url }))
  productDialogVisible.value = true
}

const submitProduct = async () => {
  try {
    const productData = {
      ...productForm,
      images: JSON.stringify(productForm.imageList.filter(img => img.trim()))
    }
    if (isEditProduct.value) {
      await request.put(`/product/${editingProductId.value}`, productData)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/product', productData)
      ElMessage.success('上架成功')
    }
    productDialogVisible.value = false
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const toggleProductStatus = async (product, newStatus) => {
  try {
    await request.put(`/product/${product.id}`, { status: newStatus })
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    await request.delete(`/product/${id}`)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 这里可以留空，原有的 addImageInput/removeImage 已经删除

// ============ 拍卖管理 ============
const loadAuctions = async () => {
  try {
    const res = await request.get(`/auction/seller/${sellerId.value}`)
    auctions.value = res.data
  } catch (error) {
    ElMessage.error('加载拍卖活动失败')
  }
}

const showAddAuctionDialog = () => {
  Object.assign(auctionForm, {
    productId: null, startPrice: 0, sellerId: sellerId.value,
    startTime: null, endTime: null, currentPrice: 0, status: 0
  })
  auctionDialogVisible.value = true
}

const submitAuction = async () => {
  try {
    await request.post('/auction', auctionForm)
    ElMessage.success('拍卖活动创建成功')
    auctionDialogVisible.value = false
    loadAuctions()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const startAuction = async (auction) => {
  try {
    await request.put(`/auction/${auction.id}`, { status: 1 })
    ElMessage.success('拍卖已开始')
    loadAuctions()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const endAuction = async (auction) => {
  try {
    await ElMessageBox.confirm('确定要结束该拍卖活动吗？', '提示', { type: 'warning' })
    await request.put(`/auction/${auction.id}`, { status: 2 })
    ElMessage.success('拍卖已结束')
    loadAuctions()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const deleteAuction = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该拍卖活动吗？', '提示', { type: 'warning' })
    await request.delete(`/auction/${id}`)
    ElMessage.success('删除成功')
    loadAuctions()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// ============ 订单管理 ============
const loadOrders = async () => {
  try {
    const res = await request.get(`/order/seller/${sellerId.value}`)
    const ordersData = res.data || []
    
    // 补全商品详情信息
    for (const order of ordersData) {
      if (order.items) {
        for (const item of order.items) {
          if (!item.productName || !item.productImage) {
            try {
              const pRes = await request.get(`/product/${item.productId}`)
              if (pRes.data) {
                item.productName = pRes.data.name
                item.productImage = getFirstImage(pRes.data.images)
              }
            } catch (e) {}
          }
        }
      }
    }
    orders.value = ordersData
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const shipOrder = async (id) => {
  try {
    await request.put(`/order/${id}/status`, null, { params: { status: 2 } })
    ElMessage.success('发货成功')
    loadOrders()
  } catch (error) {
    ElMessage.error('发货失败')
  }
}

// ============ 评价管理 ============
const loadReviews = async () => {
  try {
    const res = await request.get(`/review/seller/${sellerId.value}`)
    reviews.value = res.data
  } catch (error) {
    ElMessage.error('加载评价失败')
  }
}

// ============ 举报投诉管理 ============
const loadComplaints = async () => {
  try {
    const res = await request.get('/complaint/list')
    complaints.value = res.data.filter(c => c.targetId == sellerId.value && c.type === 1)
  } catch (error) {
    ElMessage.error('加载投诉失败')
  }
}

const openComplaintDialog = (complaint) => {
  complaintHandleForm.id = complaint.id
  complaintHandleForm.reason = complaint.reason
  complaintHandleForm.result = ''
  complaintDialogVisible.value = true
}

const submitComplaintHandle = async () => {
  try {
    await request.put(`/complaint/${complaintHandleForm.id}`, {
      status: 2,
      result: complaintHandleForm.result
    })
    ElMessage.success('处理成功')
    complaintDialogVisible.value = false
    loadComplaints()
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

// ============ 个人中心 ============
const loadProfile = async () => {
  try {
    const res = await request.get(`/user/${sellerId.value}`)
    Object.assign(profileForm, {
      shopName: res.data.shopName || '',
      shopDescription: res.data.shopDescription || '',
      nickname: res.data.nickname || '',
      phone: res.data.phone || '',
      avatar: res.data.avatar || ''
    })
  } catch (error) {
    ElMessage.error('加载个人信息失败')
  }
}

const updateProfile = async () => {
  try {
    await request.put(`/user/profile/${sellerId.value}`, profileForm)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// ============ 退出登录 ============
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('role')
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.seller-home {
  min-height: 100vh;
  background-color: #faf8f6;
}

/* ====== 顶部导航栏 ====== */
.custom-header {
  background: linear-gradient(135deg, #f5576c 0%, #ff8a5c 100%);
  padding: 0 40px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  color: white;
}

.logo-icon {
  font-size: 24px;
  margin-right: 10px;
}

.logo h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.custom-menu {
  background: transparent !important;
  border-bottom: none !important;
  flex: 1;
  margin-left: 40px;
}

.custom-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 15px;
  height: 70px;
  line-height: 70px;
  transition: all 0.3s;
}

.custom-menu :deep(.el-menu-item.is-active) {
  color: #fff !important;
  font-weight: bold;
  border-bottom: 3px solid #fff !important;
}

.custom-menu :deep(.el-menu-item:hover) {
  color: #fff !important;
  background: rgba(255, 255, 255, 0.1) !important;
}

.logout-btn {
  border-color: rgba(255,255,255,0.6) !important;
  color: rgba(255,255,255,0.9) !important;
  background: rgba(255,255,255,0.1) !important;
}

.logout-btn:hover {
  background: rgba(255,255,255,0.2) !important;
}

/* ====== 主体区域 ====== */
.main-content {
  padding: 24px 32px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-section {
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d2520;
  margin: 0;
}

.action-btn {
  background: linear-gradient(135deg, #f5576c, #ff8a5c);
  border: none;
  border-radius: 8px;
}

.action-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.order-product-item { display: flex; align-items: center; gap: 10px; margin-bottom: 5px; }
.order-product-img { width: 40px; height: 40px; border-radius: 4px; flex-shrink: 0; }
.img-error { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #f5f7fa; color: #909399; font-size: 18px; }
.order-product-info .name { font-size: 13px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 140px; }
.order-product-info .count { font-size: 12px; color: #999; }

/* ====== 统计卡片 ====== */
.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  flex: 1;
  background: white;
  border-radius: 14px;
  padding: 20px 24px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  border: 1px solid #f0ebe8;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.stat-card.stat-active {
  background: linear-gradient(135deg, #f5576c, #ff8a5c);
  border: none;
  color: white;
}

.stat-card.stat-active .stat-label {
  color: rgba(255,255,255,0.85);
}

.stat-card.stat-revenue {
  background: linear-gradient(135deg, #2d2520, #4a3830);
  border: none;
  color: white;
}

.stat-card.stat-revenue .stat-label {
  color: rgba(255,255,255,0.7);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #99857a;
}

/* ====== 表格 ====== */
.custom-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.custom-table :deep(.el-table__header th) {
  background: #f9f5f2 !important;
  color: #5a4a42;
  font-weight: 600;
  font-size: 13px;
}

.custom-table :deep(.el-table__row) {
  transition: background 0.2s;
}

.seller-home { min-height: 100vh; background: #faf8f6; }

/* ====== Header ====== */
.custom-header {
  background: linear-gradient(135deg, #f5576c 0%, #ff8a5c 100%);
  padding: 0 40px; height: 70px;
  display: flex; align-items: center; justify-content: space-between;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  position: sticky; top: 0; z-index: 1000;
}
.logo { display: flex; align-items: center; color: white; }
.logo-icon { font-size: 24px; margin-right: 10px; }
.logo h2 { margin: 0; font-size: 20px; font-weight: 600; letter-spacing: 1px; }
.custom-menu { background: transparent !important; border-bottom: none !important; flex: 1; margin-left: 40px; }
.custom-menu :deep(.el-menu-item) { color: rgba(255,255,255,0.8) !important; font-size: 15px; height: 70px; line-height: 70px; transition: all 0.3s; }
.custom-menu :deep(.el-menu-item.is-active) { color: #fff !important; font-weight: bold; border-bottom: 3px solid #fff !important; background-color: transparent !important; }
.custom-menu :deep(.el-menu-item:hover) { color: #fff !important; background: rgba(255,255,255,0.1) !important; }
.user-avatar-trigger { display: flex; align-items: center; gap: 8px; cursor: pointer; color: white; padding: 4px 12px; border-radius: 20px; transition: background 0.3s; }
.user-avatar-trigger:hover { background: rgba(255,255,255,0.1); }
.user-nickname { font-size: 14px; font-weight: 600; }

/* ====== Main Content ====== */
.main-content { padding: 24px 40px; width: 100% !important; max-width: none !important; margin: 0; overflow-x: hidden; }
.page-section { animation: fadeIn 0.4s ease; width: 100%; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.section-header h3 { font-size: 20px; font-weight: 600; color: #2d2520; margin: 0; }
.action-btn { background: linear-gradient(135deg, #f5576c, #ff8a5c); border: none; border-radius: 8px; font-weight: 600; color: white; }
.action-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3); }

/* ====== Stats Cards ====== */
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }
.stat-card { background: white; padding: 20px; border-radius: 12px; border: 1px solid #f0ebe8; text-align: center; transition: all 0.3s; }
.stat-card:hover { transform: translateY(-4px); box-shadow: 0 8px 20px rgba(0,0,0,0.05); }
.stat-value { font-size: 24px; font-weight: bold; color: #f5576c; margin-bottom: 6px; }
.stat-label { font-size: 13px; color: #a09088; }
.stat-active .stat-value { color: #52c41a; }
.stat-revenue .stat-value { color: #fadb14; }

/* ====== Table ====== */
.custom-table { border-radius: 12px; overflow: hidden; box-shadow: 0 4px 16px rgba(0,0,0,0.03); border: 1px solid #f0ebe8; }
.custom-table :deep(.el-table__header-wrapper th) { background-color: #f9fbfd !important; color: #5a4a42 !important; font-weight: bold; }
.price-text { color: #f5222d; font-weight: bold; }
.highlight { font-size: 1.1em; }
.no-img { width: 60px; height: 60px; background: #f0f0f0; border-radius: 8px; line-height: 60px; text-align: center; color: #999; font-size: 12px; }

/* ====== Profile ====== */
.profile-container { display: flex; justify-content: center; width: 100%; }
.profile-card { background: white; padding: 40px; border-radius: 16px; border: 1px solid #f0ebe8; box-shadow: 0 8px 30px rgba(0,0,0,0.05); width: 100%; max-width: 800px; }
.profile-avatar { width: 100%; display: flex; justify-content: center; margin-bottom: 30px; }
.profile-form { width: 100%; }

/* ====== Dialog ====== */
.custom-dialog :deep(.el-dialog) { border-radius: 16px; overflow: hidden; }
.custom-dialog :deep(.el-dialog__header) { margin:0; padding: 20px 24px; background: #f9fbfd; border-bottom: 1px solid #f0ebe8; }
.custom-dialog :deep(.el-dialog__title) { font-size: 18px; font-weight: bold; color: #2d2520; }
.image-upload-area :deep(.el-upload--picture-card) { width: 100px; height: 100px; line-height: 110px; }
.image-upload-area :deep(.el-upload-list--picture-card .el-upload-list__item) { width: 100px; height: 100px; }
</style>
