<template>
  <div class="admin-home">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="custom-header">
        <div class="logo">
          <el-icon class="logo-icon"><Setting /></el-icon>
          <h2>管理员后台</h2>
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" @select="handleMenuSelect" class="custom-menu">
          <el-menu-item index="dashboard">数据监控</el-menu-item>
          <el-menu-item index="users">用户管理</el-menu-item>
          <el-menu-item index="sellers">卖家管理</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="after_sales">售后管理</el-menu-item>
          <el-menu-item index="complaints">举报投诉</el-menu-item>
          <el-menu-item index="news">内容管理</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <el-button @click="handleLogout" class="logout-btn" round type="danger" plain>
            <el-icon><SwitchButton /></el-icon>退出登录
          </el-button>
        </div>
      </el-header>

      <el-main class="main-content">

        <!-- ==================== 平台数据监控 ==================== -->
        <div v-if="activeMenu === 'dashboard'" class="page-section">
          <div class="section-header">
            <h3>📊 平台数据监控</h3>
          </div>

          <!-- 核心数据指标 -->
          <div class="stats-row">
            <div class="stat-card stat-primary">
              <div class="stat-icon">👥</div>
              <div class="stat-info">
                <div class="stat-value">{{ totalUsers }}</div>
                <div class="stat-label">注册用户</div>
              </div>
            </div>
            <div class="stat-card stat-success">
              <div class="stat-icon">🏪</div>
              <div class="stat-info">
                <div class="stat-value">{{ totalSellers }}</div>
                <div class="stat-label">卖家数量</div>
              </div>
            </div>
            <div class="stat-card stat-warning">
              <div class="stat-icon">📦</div>
              <div class="stat-info">
                <div class="stat-value">{{ totalProducts }}</div>
                <div class="stat-label">商品总数</div>
              </div>
            </div>
            <div class="stat-card stat-danger">
              <div class="stat-icon">📋</div>
              <div class="stat-info">
                <div class="stat-value">{{ totalOrders }}</div>
                <div class="stat-label">订单总数</div>
              </div>
            </div>
          </div>

          <!-- 交易数据分析 -->
          <div class="dashboard-grid">
            <div class="dashboard-card">
              <h4>💰 交易数据分析</h4>
              <div class="data-list">
                <div class="data-item">
                  <span class="data-label">交易总额</span>
                  <span class="data-value price">¥{{ totalRevenue }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">已完成订单</span>
                  <span class="data-value">{{ completedOrders }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">待处理订单</span>
                  <span class="data-value warn">{{ pendingOrders }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">已取消订单</span>
                  <span class="data-value">{{ cancelledOrders }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">平均订单金额</span>
                  <span class="data-value price">¥{{ avgOrderAmount }}</span>
                </div>
              </div>
            </div>

            <div class="dashboard-card">
              <h4>📈 平台概览</h4>
              <div class="data-list">
                <div class="data-item">
                  <span class="data-label">买家用户</span>
                  <span class="data-value">{{ totalBuyers }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">活跃卖家</span>
                  <span class="data-value">{{ activeSellers }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">在售商品</span>
                  <span class="data-value">{{ onSaleProducts }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">待处理投诉</span>
                  <span class="data-value warn">{{ pendingComplaints }}</span>
                </div>
                <div class="data-item">
                  <span class="data-label">公告数量</span>
                  <span class="data-value">{{ totalNews }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 最近订单 -->
          <div class="dashboard-card full-width">
            <h4>🕐 最近订单</h4>
            <el-table :data="recentOrders" class="custom-table" stripe size="small">
              <el-table-column prop="orderNo" label="订单号" min-width="160" />
              <el-table-column label="金额" width="100">
                <template #default="scope">
                  <span class="price-text">¥{{ scope.row.totalAmount }}</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="orderStatusType(scope.row.status)" effect="light" round size="small">
                    {{ orderStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 近半年数据趋势图 -->
          <div class="dashboard-card full-width" style="margin-top: 24px;">
            <h4>📈 近半年趋势分析</h4>
            <div ref="chartRef" style="width: 100%; height: 400px;"></div>
          </div>
        </div>

        <!-- ==================== 用户管理 ==================== -->
        <div v-if="activeMenu === 'users'" class="page-section">
          <div class="section-header">
            <h3>👥 用户管理</h3>
            <el-input v-model="userSearch" placeholder="搜索用户名/昵称" class="search-input" clearable>
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </div>

          <el-table :data="filteredUsers" class="custom-table" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column label="角色" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.role === 1" type="primary" effect="light" round size="small">买家</el-tag>
                <el-tag v-else-if="scope.row.role === 2" type="success" effect="light" round size="small">卖家</el-tag>
                <el-tag v-else type="danger" effect="light" round size="small">管理员</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success" effect="light" round size="small">正常</el-tag>
                <el-tag v-else type="danger" effect="light" round size="small">禁用</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="注册时间" min-width="160">
              <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  size="small"
                  :type="scope.row.status === 1 ? 'danger' : 'success'"
                  plain
                  @click="toggleUserStatus(scope.row)"
                >
                  <el-icon><component :is="scope.row.status === 1 ? 'Lock' : 'Check'" /></el-icon>
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 卖家管理 ==================== -->
        <div v-if="activeMenu === 'sellers'" class="page-section">
          <div class="section-header">
            <h3>🏪 卖家管理</h3>
          </div>

          <div class="stats-row">
            <div class="stat-card-sm">
              <div class="stat-value">{{ sellers.length }}</div>
              <div class="stat-label">总卖家数</div>
            </div>
            <div class="stat-card-sm stat-active">
              <div class="stat-value">{{ sellers.filter(s => s.status === 1).length }}</div>
              <div class="stat-label">正常状态</div>
            </div>
            <div class="stat-card-sm">
              <div class="stat-value">{{ sellers.filter(s => s.status !== 1).length }}</div>
              <div class="stat-label">已禁用</div>
            </div>
          </div>

          <el-table :data="sellers" class="custom-table" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="shopName" label="店铺名称" min-width="150" />
            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success" effect="light" round size="small">正常</el-tag>
                <el-tag v-else type="danger" effect="light" round size="small">禁用</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="注册时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
                  size="small"
                  :type="scope.row.status === 1 ? 'danger' : 'success'"
                  plain
                  @click="toggleUserStatus(scope.row)"
                >
                  <el-icon><component :is="scope.row.status === 1 ? 'Lock' : 'Check'" /></el-icon>
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 商品订单管理 ==================== -->
        <div v-if="activeMenu === 'orders'" class="page-section">
          <div class="section-header">
            <h3>📋 商品订单管理</h3>
            <el-select v-model="orderFilter" placeholder="筛选状态" clearable class="filter-select">
              <el-option label="全部" value="" />
              <el-option label="待支付" :value="0" />
              <el-option label="待发货" :value="1" />
              <el-option label="待收货" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
            </el-select>
          </div>

          <div class="stats-row">
            <div class="stat-card-sm">
              <div class="stat-value">{{ allOrders.length }}</div>
              <div class="stat-label">全部订单</div>
            </div>
            <div class="stat-card-sm stat-active">
              <div class="stat-value">{{ allOrders.filter(o => o.status === 1).length }}</div>
              <div class="stat-label">待发货</div>
            </div>
            <div class="stat-card-sm">
              <div class="stat-value">{{ allOrders.filter(o => o.status === 3).length }}</div>
              <div class="stat-label">已完成</div>
            </div>
            <div class="stat-card-sm">
              <div class="stat-value">{{ allOrders.filter(o => o.status === 4).length }}</div>
              <div class="stat-label">已取消</div>
            </div>
          </div>

          <el-table :data="filteredOrders" class="custom-table" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="160" />
            <el-table-column label="交易双方" min-width="180">
              <template #default="scope">
                <div class="trade-users">
                  <div class="user-line">买家: <span class="u-name">{{ scope.row.buyerName || '用户#'+scope.row.buyerId }}</span></div>
                  <div class="user-line">卖家: <span class="u-name">{{ scope.row.sellerName || '用户#'+scope.row.sellerId }}</span></div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="商品预览" min-width="200">
               <template #default="scope">
                  <div class="order-product-item" v-if="scope.row.items && scope.row.items.length > 0">
                      <el-image :src="scope.row.items[0].productImage || getFirstImage(scope.row.product?.images)" class="order-product-img" />
                      <div class="order-product-info">
                          <div class="name">{{ scope.row.items[0].productName || scope.row.product?.name || ('商品#'+scope.row.items[0].productId) }}</div>
                          <div class="count">x{{ scope.row.items[0].quantity }}</div>
                      </div>
                  </div>
                  <span v-else style="color:#a09088;font-size:12px">无商品明细</span>
               </template>
            </el-table-column>
            <el-table-column label="金额" width="100">
              <template #default="scope">
                <span class="price-text">¥{{ scope.row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="orderStatusType(scope.row.status)" effect="light" round size="small">
                  {{ orderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="下单时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="scope">
                <el-button size="small" type="info" plain @click="openOrderDetail(scope.row)">
                  <el-icon><View /></el-icon> 详情
                </el-button>
                <el-button v-if="scope.row.status === 1" size="small" type="primary" plain @click="shipOrder(scope.row.id)">
                  <el-icon><Promotion /></el-icon> 发货
                </el-button>
                <el-button v-if="scope.row.status === 0" size="small" type="danger" plain @click="cancelOrder(scope.row.id)">
                  <el-icon><CircleClose /></el-icon> 取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 售后管理 ==================== -->
        <div v-if="activeMenu === 'after_sales'" class="page-section">
          <div class="section-header">
            <h3>🛠️ 全平台售后管理</h3>
          </div>

          <div class="stats-row">
            <div class="stat-card-sm">
              <div class="stat-value">{{ allAfterSales.length }}</div>
              <div class="stat-label">总售后件数</div>
            </div>
            <div class="stat-card-sm stat-active">
              <div class="stat-value">{{ allAfterSales.filter(a => a.status === 0).length }}</div>
              <div class="stat-label">待审核</div>
            </div>
            <div class="stat-card-sm">
              <div class="stat-value">{{ allAfterSales.filter(a => a.status === 1).length }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </div>

          <el-table :data="allAfterSales" class="custom-table" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="orderId" label="订单号" width="100" />
            <el-table-column label="买卖双方" min-width="180">
                <template #default="scope">
                    <div class="trade-users">
                        <div class="user-line">买家: <span class="u-name">{{ scope.row.buyerName || 'ID:'+scope.row.buyerId }}</span></div>
                        <div class="user-line">卖家: <span class="u-name">{{ scope.row.sellerName || 'ID:'+scope.row.sellerId }}</span></div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="商品信息" min-width="220">
              <template #default="scope">
                <div class="order-product-item">
                  <el-image v-if="scope.row.productImage" :src="scope.row.productImage" class="order-product-img" />
                  <div v-else class="no-img">无图</div>
                  <div class="order-product-info">
                    <div class="name">{{ scope.row.productName || (scope.row.productId ? ('商品#' + scope.row.productId) : '商品信息') }}</div>
                    <div class="count" v-if="scope.row.productId">ID:{{ scope.row.productId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="申请原因" width="120" />
            <el-table-column prop="description" label="说明" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="danger" effect="light" round size="small">待审核</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="warning" effect="light" round size="small">处理中</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="success" effect="light" round size="small">已完成</el-tag>
                <el-tag v-else type="info" effect="light" round size="small">已驳回</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button size="small" type="primary" plain @click="openAfterSaleDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 举报投诉 ==================== -->
        <div v-if="activeMenu === 'complaints'" class="page-section">
          <div class="section-header">
            <h3>🚩 举报投诉处理</h3>
          </div>

          <el-table :data="complaints" class="custom-table" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="submitterName" label="举报人" width="120" />
            <el-table-column prop="targetName" label="被投诉方" width="150" />
            <el-table-column label="商品信息" min-width="220">
              <template #default="scope">
                <div class="order-product-item">
                  <el-image v-if="scope.row.targetProductImage" :src="scope.row.targetProductImage" class="order-product-img" />
                  <div v-else class="no-img">无图</div>
                  <div class="order-product-info">
                    <div class="name">{{ scope.row.targetProductName || (scope.row.type === 2 ? ('商品#' + scope.row.targetId) : '卖家投诉') }}</div>
                    <div class="count">{{ scope.row.type === 2 ? '商品投诉' : '卖家投诉' }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="投诉内容" min-width="200">
              <template #default="scope">
                <div class="complaint-reason-cell">
                  <div class="reason-text"><b>原因:</b> {{ scope.row.reason }}</div>
                  <div class="complaint-images" v-if="scope.row.images">
                    <el-image 
                      v-for="(img, index) in parseImages(scope.row.images)" 
                      :key="index"
                      :src="img" 
                      :preview-src-list="parseImages(scope.row.images)"
                      :initial-index="index"
                      class="mini-complaint-img"
                      fit="cover"
                    />
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="danger" round size="small">待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="warning" round size="small">处理中</el-tag>
                <el-tag v-else type="success" round size="small">已处理</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button size="small" type="info" plain @click="openComplaintDetail(scope.row)">详情</el-button>
                <el-button v-if="scope.row.status < 2" size="small" type="primary" plain @click="openComplaintDialog(scope.row)">处理</el-button>
                <el-button v-else size="small" type="info" plain disabled>已结案</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 内容管理 ==================== -->
        <div v-if="activeMenu === 'news'" class="page-section">
          <div class="section-header">
            <h3>📰 内容管理</h3>
            <el-button type="primary" @click="showAddNewsDialog" class="action-btn">
              <el-icon><Plus /></el-icon> 发布公告
            </el-button>
          </div>

          <el-table :data="newsList" class="custom-table" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="title" label="标题" min-width="200" />
            <el-table-column label="封面图" width="100">
              <template #default="scope">
                <el-image
                  v-if="scope.row.coverImage"
                  :src="scope.row.coverImage"
                  style="width: 50px; height: 35px; border-radius: 4px"
                  fit="cover"
                  :preview-src-list="[scope.row.coverImage]"
                />
                <span v-else style="color: #ccc">无</span>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="发布时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <div class="news-action-group">
                  <el-button size="small" type="primary" plain @click="editNews(scope.row)">
                    <el-icon><Edit /></el-icon> 编辑
                  </el-button>
                  <el-button size="small" type="danger" plain @click="deleteNews(scope.row.id)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

      </el-main>
    </el-container>

    <!-- ==================== 处理投诉弹窗 ==================== -->
    <el-dialog v-model="complaintDialogVisible" title="处理举报投诉" width="450px" class="custom-dialog">
      <el-form :model="complaintHandleForm" label-width="90px">
        <el-form-item label="投诉原因">
          <el-input :model-value="complaintHandleForm.reason" disabled type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="凭证图片" v-if="complaintHandleForm.images">
          <div class="dialog-images">
            <el-image
              v-for="(img, index) in parseImages(complaintHandleForm.images)"
              :key="index"
              :src="img"
              :preview-src-list="parseImages(complaintHandleForm.images)"
              :initial-index="index"
              fit="cover"
              class="dialog-complaint-img"
            />
          </div>
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

    <el-dialog v-model="orderDetailVisible" title="订单详情" width="720px" class="custom-dialog">
      <div v-if="selectedOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatFullTime(selectedOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="买家">{{ selectedOrder.buyerName || ('用户#' + selectedOrder.buyerId) }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ selectedOrder.sellerName || ('用户#' + selectedOrder.sellerId) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ orderStatusText(selectedOrder.status) }}</el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ selectedOrder.totalAmount }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 14px; font-weight: 600;">商品明细</div>
        <el-table :data="selectedOrder.items || []" stripe size="small" style="margin-top:8px">
          <el-table-column label="商品" min-width="260">
            <template #default="scope">
              <div class="order-product-item">
                <el-image v-if="scope.row.productImage" :src="scope.row.productImage" class="order-product-img" />
                <div v-else class="no-img">无图</div>
                <div class="order-product-info">
                  <div class="name">{{ scope.row.productName || ('商品#' + scope.row.productId) }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="90" />
          <el-table-column label="单价" width="110">
            <template #default="scope">¥{{ scope.row.price }}</template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="orderDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="afterSaleDetailVisible" title="售后详情" width="680px" class="custom-dialog">
      <div v-if="selectedAfterSale">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ selectedAfterSale.orderId }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatFullTime(selectedAfterSale.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="买家">{{ selectedAfterSale.buyerName || ('用户#' + selectedAfterSale.buyerId) }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ selectedAfterSale.sellerName || ('用户#' + selectedAfterSale.sellerId) }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ selectedAfterSale.productName || (selectedAfterSale.productId ? ('商品#' + selectedAfterSale.productId) : '—') }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ afterSaleStatusText(selectedAfterSale.status) }}</el-descriptions-item>
          <el-descriptions-item label="申请原因" :span="2">{{ selectedAfterSale.reason || '—' }}</el-descriptions-item>
          <el-descriptions-item label="说明" :span="2">{{ selectedAfterSale.description || '—' }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 14px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">商品/凭证图片</div>
          <div class="dialog-images" v-if="selectedAfterSale.images && parseImages(selectedAfterSale.images).length">
            <el-image
              v-for="(img, index) in parseImages(selectedAfterSale.images)"
              :key="index"
              :src="img"
              :preview-src-list="parseImages(selectedAfterSale.images)"
              class="dialog-complaint-img"
            />
          </div>
          <div class="dialog-images" v-else-if="selectedAfterSale.productImage">
            <el-image :src="selectedAfterSale.productImage" :preview-src-list="[selectedAfterSale.productImage]" class="dialog-complaint-img" />
          </div>
          <div v-else style="color:#999;">无图片</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="afterSaleDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="complaintDetailVisible" title="投诉详情" width="680px" class="custom-dialog">
      <div v-if="selectedComplaint">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="举报人">{{ selectedComplaint.submitterName || ('用户#' + selectedComplaint.submitterId) }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ formatFullTime(selectedComplaint.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="被投诉方">{{ selectedComplaint.targetName || ('ID:' + selectedComplaint.targetId) }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ selectedComplaint.type === 2 ? '商品投诉' : '卖家投诉' }}</el-descriptions-item>
          <el-descriptions-item label="投诉原因" :span="2">{{ selectedComplaint.reason || '—' }}</el-descriptions-item>
          <el-descriptions-item label="处理状态">{{ selectedComplaint.status === 0 ? '待处理' : (selectedComplaint.status === 1 ? '处理中' : '已处理') }}</el-descriptions-item>
          <el-descriptions-item label="处理结果">{{ selectedComplaint.result || '—' }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 14px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">商品/凭证图片</div>
          <div class="dialog-images" v-if="selectedComplaint.images && parseImages(selectedComplaint.images).length">
            <el-image
              v-for="(img, index) in parseImages(selectedComplaint.images)"
              :key="index"
              :src="img"
              :preview-src-list="parseImages(selectedComplaint.images)"
              class="dialog-complaint-img"
            />
          </div>
          <div class="dialog-images" v-else-if="selectedComplaint.targetProductImage">
            <el-image :src="selectedComplaint.targetProductImage" :preview-src-list="[selectedComplaint.targetProductImage]" class="dialog-complaint-img" />
          </div>
          <div v-else style="color:#999;">无图片</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="complaintDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 发布/编辑公告弹窗 ==================== -->
    <el-dialog v-model="newsDialogVisible" :title="isEditNews ? '编辑公告' : '发布公告'" width="520px" class="custom-dialog">
      <el-form :model="newsForm" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="newsForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="newsForm.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="封面图上传">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
          >
            <img v-if="newsForm.coverImage" :src="newsForm.coverImage" class="avatar" style="width:160px; height:90px; border-radius:8px; object-fit: cover;" />
            <el-icon v-else class="avatar-uploader-icon" style="font-size: 28px; width:160px; height:90px; border:1px dashed #d9d9d9; border-radius:8px; display:flex; align-items:center; justify-content:center"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="newsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNews">{{ isEditNews ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, SwitchButton, Plus, Search, Edit, Delete, VideoPlay, Promotion, CircleClose, View, Lock, Check, EditPen } from '@element-plus/icons-vue'
import request from '../../utils/request'

const router = useRouter()
const adminId = ref(localStorage.getItem('userId'))
const activeMenu = ref('dashboard')

// ============ 数据 ============
const allUsers = ref([])
const allOrders = ref([])
const allProducts = ref([])
const complaints = ref([])
const allAfterSales = ref([])
const newsList = ref([])
const userSearch = ref('')
const orderFilter = ref('')

// ============ 弹窗控制 ============
const complaintDialogVisible = ref(false)
const newsDialogVisible = ref(false)
const orderDetailVisible = ref(false)
const afterSaleDetailVisible = ref(false)
const complaintDetailVisible = ref(false)
const isEditNews = ref(false)
const editingNewsId = ref(null)
const selectedOrder = ref(null)
const selectedAfterSale = ref(null)
const selectedComplaint = ref(null)

const chartRef = ref(null)
let myChart = null

// ============ 表单 ============
const complaintHandleForm = reactive({ id: null, reason: '', result: '', images: '' })
const newsForm = reactive({ title: '', content: '', coverImage: '', adminId: adminId.value })

const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const handleCoverSuccess = (res) => {
  if (res.code === 200) {
    newsForm.coverImage = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// ============ 计算属性 ============
const sellers = computed(() => allUsers.value.filter(u => u.role === 2))
const totalUsers = computed(() => allUsers.value.length)
const totalSellers = computed(() => sellers.value.length)
const totalBuyers = computed(() => allUsers.value.filter(u => u.role === 1).length)
const activeSellers = computed(() => sellers.value.filter(s => s.status === 1).length)
const totalProducts = computed(() => allProducts.value.length)
const onSaleProducts = computed(() => allProducts.value.filter(p => p.status === 1).length)
const totalOrders = computed(() => allOrders.value.length)
const completedOrders = computed(() => allOrders.value.filter(o => o.status === 3).length)
const pendingOrders = computed(() => allOrders.value.filter(o => o.status <= 1).length)
const cancelledOrders = computed(() => allOrders.value.filter(o => o.status === 4).length)
const pendingComplaints = computed(() => complaints.value.filter(c => c.status === 0).length)
const totalNews = computed(() => newsList.value.length)
const totalRevenue = computed(() => {
  return allOrders.value
    .filter(o => o.status === 3)
    .reduce((sum, o) => sum + Number(o.totalAmount || 0), 0)
    .toFixed(2)
})
const avgOrderAmount = computed(() => {
  const completed = allOrders.value.filter(o => o.status === 3)
  if (completed.length === 0) return '0.00'
  const sum = completed.reduce((s, o) => s + Number(o.totalAmount || 0), 0)
  return (sum / completed.length).toFixed(2)
})
const recentOrders = computed(() => allOrders.value.slice(0, 8))
const filteredUsers = computed(() => {
  if (!userSearch.value) return allUsers.value
  const kw = userSearch.value.toLowerCase()
  return allUsers.value.filter(u =>
    (u.username && u.username.toLowerCase().includes(kw)) ||
    (u.nickname && u.nickname.toLowerCase().includes(kw))
  )
})
const filteredOrders = computed(() => {
  if (orderFilter.value === '' || orderFilter.value === null) return allOrders.value
  return allOrders.value.filter(o => o.status === orderFilter.value)
})

// ============ 菜单切换 ============
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'dashboard') loadDashboard()
  else if (index === 'users') loadUsers()
  else if (index === 'sellers') loadUsers()
  else if (index === 'orders') loadOrders()
  else if (index === 'after_sales') loadAfterSales()
  else if (index === 'complaints') loadComplaints()
  else if (index === 'news') loadNews()

  if (index === 'dashboard') {
    nextTick(() => {
      initChart()
    })
  }
}

const isValidId = (id) => {
  if (id === null || id === undefined) return false
  const s = String(id).trim().toLowerCase()
  if (!s || s === 'null' || s === 'undefined') return false
  return !Number.isNaN(Number(s))
}

// ============ 数据加载 ============
const loadDashboard = async () => {
  await Promise.all([loadUsers(), loadOrders(), loadProducts(), loadComplaints(), loadNews(), loadAfterSales()])
}

const loadUsers = async () => {
  try {
    const res = await request.get('/user/list')
    allUsers.value = res.data
  } catch (error) {
    ElMessage.error('加载用户失败')
  }
}

const loadOrders = async () => {
  try {
    const res = await request.get('/order/list')
    const list = res.data || []
    const userCache = new Map()
    const productCache = new Map()

    for (const o of list) {
      // order/list 可能不含 items，补查详情
      if (!o.items || o.items.length === 0) {
        try {
          const detailRes = await request.get(`/order/${o.id}`)
          if (detailRes.data?.items) o.items = detailRes.data.items
        } catch (e) {}
      }

      // 补全买卖双方信息
      if (isValidId(o.buyerId)) {
        const key = String(o.buyerId)
        if (userCache.has(key)) o.buyerName = userCache.get(key)
        else {
          try {
            const bRes = await request.get(`/user/${o.buyerId}`)
            const name = bRes.data?.nickname || `用户#${o.buyerId}`
            userCache.set(key, name)
            o.buyerName = name
          } catch (e) {}
        }
      }
      if (isValidId(o.sellerId)) {
        const key = String(o.sellerId)
        if (userCache.has(key)) o.sellerName = userCache.get(key)
        else {
          try {
            const sRes = await request.get(`/user/${o.sellerId}`)
            const name = sRes.data?.nickname || `用户#${o.sellerId}`
            userCache.set(key, name)
            o.sellerName = name
          } catch (e) {}
        }
      }

      // 补全订单商品信息（图片/名称）
      if (o.items && o.items.length > 0) {
        for (const item of o.items) {
          if (!isValidId(item.productId)) continue
          const pKey = String(item.productId)
          let product = null
          if (productCache.has(pKey)) product = productCache.get(pKey)
          else {
            try {
              const pRes = await request.get(`/product/${item.productId}`)
              product = pRes.data || null
            } catch (e) {
              product = null
            }
            productCache.set(pKey, product)
          }
          if (product) {
            item.productName = item.productName || product.name
            item.productImage = item.productImage || getFirstImage(product.images)
          }
        }
        if (o.items[0]) {
          o.product = {
            name: o.items[0].productName,
            images: JSON.stringify(o.items[0].productImage ? [o.items[0].productImage] : [])
          }
        }
      }
    }
    allOrders.value = list

    // 初始化/更新图表数据
    nextTick(() => {
      initChart()
    })
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

// ============ 图表初始化 ============
const initChart = () => {
  if (activeMenu.value !== 'dashboard' || !chartRef.value) return;
  if (!myChart) {
    myChart = echarts.init(chartRef.value);
  }

  // 生成近6个月的月份数组
  const months = [];
  const now = new Date();
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
    months.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`);
  }

  // 统计近6个月的数据
  const orderCounts = Array(6).fill(0);
  const orderRevenues = Array(6).fill(0);

  allOrders.value.forEach(order => {
    if (!order.createTime) return;
    const date = new Date(order.createTime);
    const monthStr = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
    const idx = months.indexOf(monthStr);
    if (idx !== -1) {
      if (order.status === 3) {
         orderRevenues[idx] += Number(order.totalAmount || 0);
      }
      orderCounts[idx] += 1;
    }
  });

  const fixedRevenues = orderRevenues.map(v => Number(v.toFixed(2)));

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['订单数量', '交易金额(元)'],
      bottom: 0
    },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: [
      {
        type: 'category',
        data: months,
        axisPointer: { type: 'shadow' }
      }
    ],
    yAxis: [
      {
         type: 'value',
         name: '订单数量',
         min: 0,
         minInterval: 1
      },
      {
         type: 'value',
         name: '交易金额',
         min: 0,
         axisLabel: { formatter: '¥{value}' }
      }
    ],
    series: [
      {
        name: '订单数量',
        type: 'line',
        data: orderCounts,
        smooth: true,
        itemStyle: { color: '#e7a969' }
      },
      {
        name: '交易金额(元)',
        type: 'line',
        yAxisIndex: 1,
        data: fixedRevenues,
        smooth: true,
        itemStyle: { color: '#f5576c' }
      }
    ]
  };

  myChart.setOption(option);
}

window.addEventListener('resize', () => {
  if (myChart) myChart.resize()
})

const loadProducts = async () => {
  try {
    const res = await request.get('/product/search', { params: { keyword: '' } })
    allProducts.value = res.data.records || []
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const loadComplaints = async () => {
  try {
    const res = await request.get('/complaint/list')
    const list = res.data || []
    const userCache = new Map()
    const productCache = new Map()

    for (const c of list) {
      try {
        if (isValidId(c.submitterId)) {
          const key = String(c.submitterId)
          if (userCache.has(key)) c.submitterName = userCache.get(key)
          else {
            const uRes = await request.get(`/user/${c.submitterId}`)
            const name = uRes.data?.nickname || `用户#${c.submitterId}`
            userCache.set(key, name)
            c.submitterName = name
          }
        }

        if (c.type === 1 && isValidId(c.targetId)) {
          const key = String(c.targetId)
          if (userCache.has(key)) c.targetName = userCache.get(key)
          else {
            const sRes = await request.get(`/user/${c.targetId}`)
            const name = sRes.data?.shopName || sRes.data?.nickname || `用户#${c.targetId}`
            userCache.set(key, name)
            c.targetName = name
          }
        } else if (c.type !== 1 && isValidId(c.targetId)) {
          const key = String(c.targetId)
          let product = null
          if (productCache.has(key)) product = productCache.get(key)
          else {
            const pRes = await request.get(`/product/${c.targetId}`)
            product = pRes.data || null
            productCache.set(key, product)
          }
          if (product) {
            c.targetName = product.name
            c.targetProductName = product.name
            c.targetProductImage = getFirstImage(product.images)
          }
        }
      } catch (e) {}
    }
    complaints.value = list
  } catch (error) {
    ElMessage.error('加载投诉失败')
  }
}

const loadAfterSales = async () => {
    try {
        const [afterRes, orderRes] = await Promise.all([
          request.get('/aftersale/list'),
          request.get('/order/list')
        ])
        const list = afterRes.data || []
        const orderMap = new Map((orderRes.data || []).map(o => [String(o.id), o]))
        const userCache = new Map()
        const productCache = new Map()

        for(let a of list) {
            const linkedOrder = orderMap.get(String(a.orderId))
            if (!isValidId(a.buyerId) && linkedOrder && isValidId(linkedOrder.buyerId)) {
              a.buyerId = linkedOrder.buyerId
            }
            if (!isValidId(a.sellerId) && linkedOrder && isValidId(linkedOrder.sellerId)) {
              a.sellerId = linkedOrder.sellerId
            }

            // 订单商品信息
            if (isValidId(a.orderId)) {
              try {
                const detailRes = await request.get(`/order/${a.orderId}`)
                const orderDetail = detailRes.data || linkedOrder
                if (orderDetail?.items?.length) {
                  const firstItem = orderDetail.items[0]
                  a.productId = firstItem.productId
                  a.productName = firstItem.productName
                  a.productImage = firstItem.productImage

                  if ((!a.productName || !a.productImage) && isValidId(firstItem.productId)) {
                    const pKey = String(firstItem.productId)
                    let product = null
                    if (productCache.has(pKey)) product = productCache.get(pKey)
                    else {
                      const pRes = await request.get(`/product/${firstItem.productId}`)
                      product = pRes.data || null
                      productCache.set(pKey, product)
                    }
                    if (product) {
                      if (!a.productName) a.productName = product.name
                      if (!a.productImage) a.productImage = getFirstImage(product.images)
                    }
                  }
                }
              } catch (e) {}
            }
            try {
                if (isValidId(a.buyerId)) {
                  const key = String(a.buyerId)
                  if (userCache.has(key)) a.buyerName = userCache.get(key)
                  else {
                    const bRes = await request.get(`/user/${a.buyerId}`)
                    const name = bRes.data?.nickname || `用户#${a.buyerId}`
                    userCache.set(key, name)
                    a.buyerName = name
                  }
                } else {
                  a.buyerName = '未知用户'
                }

                if (isValidId(a.sellerId)) {
                  const key = String(a.sellerId)
                  if (userCache.has(key)) a.sellerName = userCache.get(key)
                  else {
                    const sRes = await request.get(`/user/${a.sellerId}`)
                    const name = sRes.data?.nickname || `用户#${a.sellerId}`
                    userCache.set(key, name)
                    a.sellerName = name
                  }
                } else {
                  a.sellerName = '未知卖家'
                }
            } catch(e){}
        }
        allAfterSales.value = list
    } catch { 
        allAfterSales.value = []
    }
}

const openOrderDetail = (row) => {
  selectedOrder.value = row
  orderDetailVisible.value = true
}

const openAfterSaleDetail = (row) => {
  selectedAfterSale.value = row
  afterSaleDetailVisible.value = true
}

const openComplaintDetail = (row) => {
  selectedComplaint.value = row
  complaintDetailVisible.value = true
}

const formatFullTime = (timeStr) => {
  if (!timeStr) return '—'
  return timeStr.replace('T', ' ').split('.')[0]
}

const parseImages = (images) => {
  if (!images) return []
  try {
    // 尝试解析 JSON 字符串
    const list = JSON.parse(images)
    return Array.isArray(list) ? list : [images]
  } catch (e) {
    // 如果不是 JSON，尝试逗号分割或作为单个 URL
    if (images.includes(',')) return images.split(',')
    return [images]
  }
}

const getFirstImage = (images) => {
  if (!images) return null
  try {
    const list = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(list) && list.length > 0 ? list[0] : null
  } catch { return null }
}

const loadNews = async () => {
  try {
    const res = await request.get('/news/list')
    newsList.value = res.data
  } catch (error) {
    ElMessage.error('加载公告失败')
  }
}

// ============ 用户管理 ============
const toggleUserStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}用户 ${user.username} 吗？`, '提示', { type: 'warning' })
    await request.put(`/user/profile/${user.id}`, { status: newStatus })
    ElMessage.success(`${actionText}成功`)
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

// ============ 订单管理 ============
const orderStatusText = (status) => {
  return ['待支付', '待发货', '待收货', '已完成', '已取消'][status] || '未知'
}

const orderStatusType = (status) => {
  return ['', 'warning', 'info', 'success', 'danger'][status] || 'info'
}

const afterSaleStatusText = (status) => {
  return status === 0 ? '待审核' : status === 1 ? '处理中' : status === 2 ? '已完成' : '已驳回'
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

const cancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await request.put(`/order/${id}/status`, null, { params: { status: 4 } })
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

// ============ 投诉管理 ============
const openComplaintDialog = (complaint) => {
  complaintHandleForm.id = complaint.id
  complaintHandleForm.reason = complaint.reason
  complaintHandleForm.images = complaint.images
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

// ============ 内容管理 ============
const showAddNewsDialog = () => {
  isEditNews.value = false
  editingNewsId.value = null
  Object.assign(newsForm, { title: '', content: '', coverImage: '', adminId: adminId.value })
  newsDialogVisible.value = true
}

const editNews = (news) => {
  isEditNews.value = true
  editingNewsId.value = news.id
  Object.assign(newsForm, {
    title: news.title,
    content: news.content,
    coverImage: news.coverImage || '',
    adminId: adminId.value
  })
  newsDialogVisible.value = true
}

const submitNews = async () => {
  try {
    if (isEditNews.value) {
      await request.put(`/news/${editingNewsId.value}`, newsForm)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/news', newsForm)
      ElMessage.success('发布成功')
    }
    newsDialogVisible.value = false
    loadNews()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteNews = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？', '提示', { type: 'warning' })
    await request.delete(`/news/${id}`)
    ElMessage.success('删除成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
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
  loadDashboard()
})
</script>

<style scoped>
.admin-home {
  min-height: 100vh;
  background-color: #faf8f6;
  width: 100%;
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

.logo-icon { font-size: 24px; margin-right: 10px; }
.logo h2 { margin: 0; font-size: 20px; font-weight: 600; letter-spacing: 1px; }

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
  background-color: transparent !important;
}

.custom-menu :deep(.el-menu-item:focus) {
  background-color: transparent !important;
}

.custom-menu :deep(.el-menu-item:hover) {
  color: #fff !important;
  background: rgba(255, 255, 255, 0.1) !important;
}

.trade-users { font-size: 12px; line-height: 1.6; }
.user-line { color: #8c939d; }
.u-name { color: #5a4a42; font-weight: 500; }

.order-product-item { display: flex; align-items: center; gap: 8px; }
.order-product-img { width: 40px; height: 40px; border-radius: 4px; object-fit: cover; border: 1px solid #f0ebe8; }
.order-product-info .name { font-size: 12px; font-weight: 500; color: #2d2520; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 130px; }

.logout-btn {
  border-color: rgba(255,255,255,0.6) !important;
  color: rgba(255,255,255,0.9) !important;
  background: rgba(255,255,255,0.1) !important;
}
.logout-btn:hover {
  background: rgba(255,255,255,0.2) !important;
}

/* ====== 主内容 ====== */
.main-content {
  padding: 24px 40px;
  width: 100% !important;
  max-width: none !important;
  margin: 0;
  overflow-x: hidden;
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

.search-input {
  width: 280px;
}

.filter-select {
  width: 150px;
}

/* ====== 统计卡片 (大) ====== */
.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  border: 1px solid #f0ebe8;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.stat-card .stat-icon {
  font-size: 36px;
  width: 60px;
  height: 60px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-primary .stat-icon { background: #fff0f2; }
.stat-success .stat-icon { background: #f0faf0; }
.stat-warning .stat-icon { background: #fef8ee; }
.stat-danger .stat-icon { background: #fef0f0; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2d2520;
}

.stat-label {
  font-size: 13px;
  color: #99857a;
  margin-top: 2px;
}

/* ====== 统计卡片 (小) ====== */
.stat-card-sm {
  flex: 1;
  background: white;
  border-radius: 14px;
  padding: 18px 24px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  border: 1px solid #f0ebe8;
}

.stat-card-sm:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.stat-card-sm.stat-active {
  background: linear-gradient(135deg, #f5576c, #ff8a5c);
  border: none;
  color: white;
}
.stat-card-sm.stat-active .stat-label { color: rgba(255,255,255,0.85); }
.stat-card-sm.stat-active .stat-value { color: white; }

/* ====== Dashboard ====== */
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.dashboard-card {
  background: white;
  border-radius: 14px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0ebe8;
}

.dashboard-card.full-width {
  margin-bottom: 20px;
}

.dashboard-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: #2d2520;
  margin: 0 0 16px 0;
}

.data-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f0ed;
}

.data-item:last-child {
  border-bottom: none;
}

.data-label {
  font-size: 14px;
  color: #7a6b62;
}

.data-value {
  font-size: 16px;
  font-weight: 600;
  color: #2d2520;
}

.data-value.price {
  color: #f5576c;
}

.data-value.warn {
  color: #ff8a5c;
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

.price-text {
  color: #f5576c;
  font-weight: 600;
}

/* ====== 弹窗 ====== */
.custom-dialog :deep(.el-dialog) {
  border-radius: 16px;
}

.custom-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #f0ebe8;
  padding: 16px 24px;
}

.custom-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.custom-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid #f0ebe8;
  padding: 12px 24px;
}
.complaint-reason-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.reason-text {
  line-height: 1.6;
}
.complaint-images {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
.mini-complaint-img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #eee;
  transition: transform 0.2s;
}
.mini-complaint-img:hover {
  transform: scale(1.1);
  z-index: 1;
}
.dialog-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 5px 0;
}
.dialog-complaint-img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.news-action-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
}

.news-action-group .el-button {
  margin-left: 0 !important;
}
</style>
