<template>
  <div class="buyer-home">
    <el-container>
      <el-header class="custom-header">
        <div class="logo">
          <el-icon class="logo-icon"><Shop /></el-icon>
          <h2>玩偶循环</h2>
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" @select="handleMenuSelect" class="custom-menu">
          <el-menu-item index="products">商品浏览</el-menu-item>
          <el-menu-item index="cart">购物车</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="community">互动社区</el-menu-item>
          <el-menu-item index="auctions">拍卖专区</el-menu-item>
          <el-menu-item index="complaints">举报投诉</el-menu-item>
          <el-menu-item index="news">资讯浏览</el-menu-item>
          <!-- 个人中心入口已从菜单隐藏，仅通过右上角进入 -->
        </el-menu>
        <div class="user-actions">
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-avatar-trigger">
              <el-avatar :size="36" :src="profileForm.avatar || ''"><span v-if="!profileForm.avatar" style="font-size:16px">🧸</span></el-avatar>
              <span class="user-nickname">{{ profileForm.nickname || '买家' }}</span>
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="my_address">
                  <el-icon><Location /></el-icon>收货地址
                </el-dropdown-item>
                <el-dropdown-item command="my_posts">
                  <el-icon><ChatLineSquare /></el-icon>我的发帖
                </el-dropdown-item>
                <el-dropdown-item command="my_favorites">
                  <el-icon><Star /></el-icon>我的收藏
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

        <!-- ==================== 商品浏览 ==================== -->
        <div v-if="activeMenu === 'products'" class="page-section">
          <div class="search-bar">
            <el-input v-model="searchKeyword" placeholder="搜索心仪的玩偶..." class="custom-search" size="large" @keyup.enter="searchProducts">
              <template #prefix><el-icon><Search /></el-icon></template>
              <template #append>
                <el-button @click="searchProducts">搜索</el-button>
              </template>
            </el-input>
          </div>
          <div class="products-grid">
            <div v-for="product in products" :key="product.id" class="product-card" @click="openProductDetail(product)">
              <div class="product-img-wrap">
                <el-image v-if="getFirstImage(product.images)" :src="getFirstImage(product.images)" class="product-img" fit="cover" />
                <div v-else class="product-img-placeholder">🧸</div>
                <div class="product-badge" v-if="product.stock <= 3 && product.stock > 0">仅剩{{ product.stock }}件</div>
                <div class="product-badge sold-out" v-if="product.stock === 0">已售罄</div>
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ product.name }}</h4>
                <p class="product-desc">{{ product.description || '精品玩偶，品质保证' }}</p>
                <div class="product-footer">
                  <div class="product-price">
                    <span class="price-sym">¥</span><span class="price-num">{{ product.price }}</span>
                  </div>
                  <div class="product-actions">
                    <el-button type="text" circle @click.stop="toggleFavorite(product.id)">
                      <el-icon :color="isProductFavorited(product.id) ? '#f5576c' : '#9ea7b4'" size="20">
                        <component :is="isProductFavorited(product.id) ? 'StarFilled' : 'Star'" />
                      </el-icon>
                    </el-button>
                    <el-button type="text" circle @click.stop="reportProduct(product)">
                      <el-icon color="#9ea7b4" size="20"><Warning /></el-icon>
                    </el-button>
                    <el-button type="primary" size="small" round class="cart-btn" @click.stop="addToCart(product.id)" :disabled="product.stock === 0">
                      <el-icon><ShoppingCart /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-if="products.length === 0" description="暂无商品" />
        </div>

        <!-- ==================== 购物车 ==================== -->
        <div v-if="activeMenu === 'cart'" class="page-section">
          <div class="section-header"><h3>🛒 我的购物车</h3></div>
          <div v-if="cartItems.length > 0">
            <el-table :data="cartItems" class="custom-table" stripe @selection-change="handleCartSelectionChange">
              <el-table-column type="selection" width="55" :selectable="row => row.status === 1 && row.stock > 0" />
              <el-table-column label="商品信息" min-width="250">
                <template #default="scope">
                  <div style="display: flex; align-items: center; gap: 12px; cursor: pointer" @click="openProductDetailById(scope.row.productId)">
                    <el-image v-if="scope.row.productImage" :src="scope.row.productImage" style="width: 60px; height: 60px; border-radius: 8px;" fit="cover" />
                    <div v-else style="width: 60px; height: 60px; border-radius: 8px; background: #f0f0f0; display:flex; align-items:center; justify-content:center; font-size:24px">🧸</div>
                    <div style="display: flex; flex-direction: column;">
                      <span style="font-weight: 600; color: #2d2520;">{{ scope.row.productName || `商品ID: ${scope.row.productId}` }}</span>
                      <el-tag v-if="scope.row.status !== 1" type="danger" size="small" effect="plain" style="margin-top: 4px; width: fit-content;">已下架</el-tag>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="单价" width="120">
                <template #default="scope">
                  <span class="price-text">¥{{ scope.row.productPrice || '0.00' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="160">
                <template #default="scope">
                  <el-input-number v-model="scope.row.quantity" :min="1" :max="scope.row.stock || 99" size="small" @change="updateCartItem(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="小计" width="120">
                <template #default="scope">
                  <span class="price-text" style="font-size: 16px;">¥{{ ((scope.row.productPrice || 0) * scope.row.quantity).toFixed(2) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button type="danger" size="small" plain @click="removeFromCart(scope.row.id)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="cart-footer">
              <div class="cart-total">
                已选 {{ selectedCartItems.length }} 件商品，合计：<span class="price-num">¥{{ cartTotalAmount }}</span>
              </div>
              <el-button type="warning" size="large" class="checkout-btn" @click="openCheckoutDialog">立即结算</el-button>
            </div>
          </div>
          <el-empty v-else description="购物车是空的，去逛逛吧~" />
        </div>

        <!-- ==================== 订单管理与售后 ==================== -->
        <div v-if="activeMenu === 'orders'" class="page-section">
          <div class="section-header">
            <h3>📋 订单管理</h3>
            <el-radio-group v-model="orderTabFilter" size="small" @change="filterMyOrders">
              <el-radio-button value="">全部</el-radio-button>
              <el-radio-button :value="0">待支付</el-radio-button>
              <el-radio-button :value="1">待发货</el-radio-button>
              <el-radio-button :value="2">待收货</el-radio-button>
              <el-radio-button :value="3">已完成</el-radio-button>
            </el-radio-group>
          </div>
          <el-table :data="filteredMyOrders" class="custom-table" stripe style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" min-width="160" />
            <el-table-column label="商品信息" min-width="250">
              <template #default="scope">
                <div v-if="scope.row.items && scope.row.items.length > 0" style="display: flex; flex-direction: column; gap: 8px;">
                  <div v-for="item in scope.row.items" :key="item.id" style="display: flex; align-items: center; gap: 12px; cursor: pointer" @click="openProductDetailById(item.productId)">
                    <el-image v-if="item.productImage" :src="item.productImage" style="width: 50px; height: 50px; border-radius: 6px;" fit="cover" />
                    <div v-else style="width: 50px; height: 50px; border-radius: 6px; background: #f0f0f0; display:flex; align-items:center; justify-content:center; font-size:20px">🧸</div>
                    <div style="display: flex; flex-direction: column; justify-content: center;">
                      <span style="font-weight: 600; font-size: 13px; color: #2d2520;">{{ item.productName || `商品ID: ${item.productId}` }}</span>
                      <span style="font-size: 12px; color: #a09088;">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
                <div v-else style="color: #a09088;">暂无商品明细</div>
              </template>
            </el-table-column>
            <el-table-column label="金额" width="120">
              <template #default="scope"><span class="price-text">¥{{ scope.row.totalAmount }}</span></template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="orderStatusType(scope.row.status)" effect="light" round size="small">{{ orderStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="下单时间" width="170">
              <template #default="scope">
                {{ formatFullTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="scope">
                  <div class="order-action-group">
                    <el-button v-if="scope.row.status === 0" size="small" type="warning" plain @click="payOrder(scope.row.id)">
                      <el-icon><CreditCard /></el-icon> 立即支付
                    </el-button>
                    <el-button v-if="scope.row.status === 0" size="small" type="info" plain @click="cancelOrder(scope.row.id)">
                      <el-icon><CircleClose /></el-icon> 取消订单
                    </el-button>
                    <el-button v-if="scope.row.status === 2" size="small" type="success" plain @click="confirmReceive(scope.row.id)">
                      <el-icon><Check /></el-icon> 确认收货
                    </el-button>
                    <el-button v-if="scope.row.status === 3" size="small" type="primary" class="custom-btn review-btn" @click="openReviewDialog(scope.row)">
                      <el-icon><ChatLineSquare /></el-icon> 发表评价
                    </el-button>
                    <el-button v-if="scope.row.status >= 2" size="small" type="warning" plain class="custom-btn aftersale-btn" @click="openAfterSaleDialog(scope.row)">
                      <el-icon><RefreshRight /></el-icon> 申请售后
                    </el-button>
                  </div>
              </template>
            </el-table-column>
          </el-table>
          <!-- 我的售后 -->
          <div class="section-header" style="margin-top:32px"><h3>🔄 售后记录</h3></div>
          <el-table :data="afterSales" class="custom-table" stripe>
            <el-table-column prop="orderId" label="订单ID" width="90" />
            <el-table-column label="商品信息" min-width="250">
              <template #default="scope">
                <div style="display: flex; align-items: center; gap: 12px;">
                  <el-image v-if="scope.row.productImage" :src="scope.row.productImage" style="width: 50px; height: 50px; border-radius: 6px;" fit="cover" />
                  <div v-else style="width: 50px; height: 50px; border-radius: 6px; background: #f0f0f0; display:flex; align-items:center; justify-content:center; font-size:20px">🧸</div>
                  <span style="font-weight: 600; color: #2d2520;">{{ scope.row.productName || '商品信息' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="申请原因" min-width="160" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="warning" effect="light" round size="small">待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="info" effect="light" round size="small">处理中</el-tag>
                <el-tag v-else type="success" effect="light" round size="small">已完成</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" width="160">
                <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 拍卖专区 ==================== -->
        <div v-if="activeMenu === 'auctions'" class="page-section">
          <div class="section-header">
            <h3>🔨 热门拍卖</h3>
            <p style="font-size: 14px; color: #9a8a82;">发现珍稀玩偶，即刻开启竞拍</p>
          </div>
          
          <div class="products-grid">
            <div v-for="auc in auctions" :key="auc.id" class="product-card">
              <div class="product-img-wrap" @click="openProductDetailById(auc.productId)">
                <el-image v-if="auc.productImage" :src="auc.productImage" class="product-img" fit="cover" />
                <div v-else class="product-img-placeholder">🧸</div>
                <div class="product-badge" v-if="auc.status === 1">进行中</div>
                <div class="product-badge sold-out" v-else-if="auc.status === 2">已结束</div>
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ auc.productName || '拍卖商品' }}</h4>
                <div class="auction-prices">
                    <div class="price-item">
                        <span class="label">起拍价:</span>
                        <span class="val">¥{{ auc.startPrice }}</span>
                    </div>
                    <div class="price-item current">
                        <span class="label">当前价:</span>
                        <span class="val">¥{{ auc.currentPrice }}</span>
                    </div>
                </div>
                <div v-if="auc.status === 2" style="margin-top: 8px; font-size: 12px;">
                  <span v-if="auc.winnerId && String(auc.winnerId) === String(userId)" style="color:#67c23a; font-weight: 600;">
                    你已中标，请到订单管理完成支付
                  </span>
                  <span v-else-if="auc.winnerName" style="color:#909399;">中标者：{{ auc.winnerName }}</span>
                  <span v-else-if="auc.winnerId" style="color:#909399;">中标者：用户#{{ auc.winnerId }}</span>
                  <span v-else style="color:#f56c6c;">本场流拍</span>
                </div>
                <div class="product-footer" style="margin-top: 12px;">
                  <div class="time-left" style="font-size: 11px; color: #a09088;">
                    截止: {{ formatFullTime(auc.endTime) }}
                  </div>
                  <el-button type="warning" size="small" round @click="openBidDialog(auc)" :disabled="auc.status !== 1">
                    <el-icon><Pointer /></el-icon> {{ auc.status === 1 ? '参与竞拍' : '已结束' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-if="auctions.length === 0" description="暂无进行中的拍卖活动" />
        </div>

        <!-- ==================== 互动社区 ==================== -->
        <div v-if="activeMenu === 'community'" class="page-section">
          <div class="simple-community">
            <div class="community-header">
              <div class="header-left">
                <h3>🧸 玩偶动态</h3>
                <p>分享你的收藏，结交同好</p>
              </div>
              <el-button type="primary" class="publish-btn" @click="showPostDialog = true">
                <el-icon><EditPen /></el-icon> 发布动态
              </el-button>
            </div>
            
            <div class="posts-feed">
              <div v-for="post in posts" :key="post.id" class="simple-post-card" @click="openPostDetail(post)">
                <div class="post-user-info">
                  <el-avatar :size="32">{{ post.userId }}</el-avatar>
                  <div class="user-meta">
                    <span class="user-id">收藏家#{{ post.userId }}</span>
                    <span class="post-date">{{ formatFullTime(post.createTime) }}</span>
                  </div>
                </div>
                <h4 class="post-title">{{ post.title }}</h4>
                <p class="post-text">{{ post.content }}</p>
                <div v-if="getImageList(post.images).length" class="post-media">
                  <el-image v-for="(img, i) in getImageList(post.images)" :key="i" :src="img" fit="cover" class="feed-img" lazy />
                </div>
                <div class="post-actions">
                  <span class="action"><el-icon><ChatLineRound /></el-icon> 评论</span>
                </div>
              </div>
              <el-empty v-if="posts.length === 0" description="社区空空如也，快来分享吧" />
            </div>
          </div>
        </div>

        <!-- ==================== 举报投诉 ==================== -->
        <div v-if="activeMenu === 'complaints'" class="page-section">
          <div class="section-header">
            <h3>🚨 举报投诉管理</h3>
            <el-button type="primary" class="action-btn" @click="showComplaintDialog = true"><el-icon><Plus /></el-icon> 提交投诉</el-button>
          </div>
          <el-table :data="myComplaints" class="custom-table" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column label="投诉对象" min-width="250">
              <template #default="scope">
                <div style="display: flex; align-items: center; gap: 12px;">
                  <el-image v-if="scope.row.type === 2 && scope.row.productImage" :src="scope.row.productImage" style="width: 50px; height: 50px; border-radius: 6px;" fit="cover" />
                  <div v-else-if="scope.row.type === 2" style="width: 50px; height: 50px; border-radius: 6px; background: #f0f0f0; display:flex; align-items:center; justify-content:center; font-size:20px">🧸</div>
                  <div style="font-weight: 600; color: #2d2520;">{{ scope.row.targetName || 'ID:'+scope.row.targetId }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="类型" width="80">
              <template #default="scope">
                <el-tag v-if="scope.row.type === 1" effect="light" size="small">卖家</el-tag>
                <el-tag v-else type="warning" effect="light" size="small">商品</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="内容于证据" min-width="200">
              <template #default="scope">
                <div class="complaint-content-cell">
                  <div class="reason-text">{{ scope.row.reason }}</div>
                  <div class="complaint-images" v-if="scope.row.images">
                    <el-image 
                      v-for="(img, idx) in parseImages(scope.row.images)" 
                      :key="idx" 
                      :src="img" 
                      class="mini-complaint-img" 
                      :preview-src-list="parseImages(scope.row.images)"
                      :initial-index="idx"
                    />
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="处理进度" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="danger" effect="light" round size="small">待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="warning" effect="light" round size="small">处理中</el-tag>
                <el-tag v-else type="success" effect="light" round size="small">已处理</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="反馈信息" min-width="160" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.result || '暂无反馈' }}</template>
            </el-table-column>
            <el-table-column label="提交时间" width="160">
              <template #default="scope">{{ formatFullTime(scope.row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <!-- ==================== 资讯浏览 ==================== -->
        <div v-if="activeMenu === 'news'" class="page-section">
          <div class="section-header"><h3>📰 资讯浏览</h3></div>
          <div class="news-grid">
            <div v-for="item in newsList" :key="item.id" class="news-card" @click="openNewsDetail(item)" style="cursor: pointer;">
              <div class="news-cover" v-if="item.coverImage">
                <el-image :src="item.coverImage" fit="cover" class="news-img" />
              </div>
              <div class="news-body">
                <h4 class="news-title">{{ item.title }}</h4>
                <p class="news-content">{{ item.content }}</p>
                <span class="news-time">{{ item.createTime }}</span>
              </div>
            </div>
            <el-empty v-if="newsList.length === 0" description="暂无资讯" />
          </div>
        </div>

        <!-- ==================== 个人中心 ==================== -->
        <div v-if="activeMenu === 'profile'" class="page-section">
          <div class="section-header">
            <h3>👤 个人中心</h3>
          </div>

          <div class="profile-container" style="display: flex; justify-content: center; width: 100%;">
            <div class="profile-card" style="background: white; padding: 40px; border-radius: 16px; border: 1px solid #f0ebe8; box-shadow: 0 8px 30px rgba(0,0,0,0.05); width: 100%; max-width: 800px;">
              <div class="profile-avatar" style="width: 100%; display: flex; justify-content: center; margin-bottom: 30px;">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                >
                  <el-avatar :size="80" :src="profileForm.avatar || ''">
                    <span v-if="!profileForm.avatar" style="font-size: 32px">🧸</span>
                  </el-avatar>
                </el-upload>
              </div>
              <el-form :model="profileForm" label-width="100px" class="profile-form">
                <el-form-item label="昵称">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="联系电话">
                  <el-input v-model="profileForm.phone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" class="action-btn">保存修改</el-button>
                  <el-button type="warning" @click="passwordDialogVisible = true" style="margin-left: 10px;">修改密码</el-button>
                </el-form-item>
              </el-form>

              <el-divider>其他管理</el-divider>
              <div style="display: flex; justify-content: space-around; margin-top: 20px;">
                <el-button type="text" @click="profileActiveTab = 'address'"><el-icon><Location /></el-icon> 收货地址</el-button>
                <el-button type="text" @click="profileActiveTab = 'posts'"><el-icon><ChatLineSquare /></el-icon> 我的发帖</el-button>
                <el-button type="text" @click="profileActiveTab = 'favorites'"><el-icon><Star /></el-icon> 我的收藏</el-button>
              </div>

              <!-- 根据点击展示子面板 (地址/帖子/收藏) -->
              <div v-if="profileActiveTab !== 'info'" style="margin-top: 30px; border-top: 1px solid #f0ebe8; padding-top: 20px; text-align: left;">
                <div v-if="profileActiveTab === 'address'">
                  <h4 style="margin-bottom: 20px;">📍 收货地址管理</h4>
                  <el-form :model="defaultAddress" label-position="top">
                    <el-row :gutter="20">
                      <el-col :span="12"><el-form-item label="姓名"><el-input v-model="defaultAddress.receiver" /></el-form-item></el-col>
                      <el-col :span="12"><el-form-item label="电话"><el-input v-model="defaultAddress.phone" /></el-form-item></el-col>
                    </el-row>
                    <el-row :gutter="20">
                      <el-col :span="8">
                        <el-form-item label="省份">
                          <el-select v-model="defaultAddress.province" placeholder="省份">
                            <el-option v-for="p in provinces" :key="p" :label="p" :value="p" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="8"><el-form-item label="城市"><el-input v-model="defaultAddress.city" /></el-form-item></el-col>
                      <el-col :span="8"><el-form-item label="区县"><el-input v-model="defaultAddress.district" /></el-form-item></el-col>
                    </el-row>
                    <el-form-item label="详细地址"><el-input v-model="defaultAddress.detail" type="textarea" :rows="2" /></el-form-item>
                    <el-button type="primary" style="width:100%" @click="saveDefaultAddress">保存地址</el-button>
                  </el-form>
                </div>

                <div v-if="profileActiveTab === 'favorites'">
                  <h4 style="margin-bottom: 20px;">⭐ 我的收藏商品</h4>
                  <div class="fav-grid" v-if="favoriteProducts && favoriteProducts.length > 0">
                    <div v-for="fp in favoriteProducts" :key="fp.id" class="fav-item" @click="openProductDetailById(fp.id)">
                      <el-image :src="getFirstImage(fp.images)" class="fav-img" fit="cover" />
                      <div class="fav-info">
                        <div class="fav-name">{{ fp.name }}</div>
                        <div class="fav-price">¥{{ fp.price }}</div>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无收藏" />
                </div>

                <div v-if="profileActiveTab === 'posts'">
                  <h4 style="margin-bottom: 20px;">📝 我的发布记录</h4>
                  <div class="user-posts" v-if="myOwnPosts && myOwnPosts.length > 0">
                    <div v-for="post in myOwnPosts" :key="post.id" class="user-post-item" @click="openPostDetail(post)">
                      <div class="up-title">{{ post.title }}</div>
                      <div class="up-time">{{ formatFullTime(post.createTime) }}</div>
                    </div>
                  </div>
                  <el-empty v-else description="尚未发布帖子" />
                </div>
                <div style="text-align: center; margin-top: 20px;">
                  <el-button @click="profileActiveTab = 'info'">返回资料修改</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>


      </el-main>
    </el-container>

    <!-- ==================== 商品详情弹窗 ==================== -->
    <el-dialog v-model="productDetailVisible" width="750px" class="detail-dialog" align-center :show-close="false">
      <div v-if="selectedProduct" class="product-detail-layout">
        <div class="product-detail-left">
          <el-carousel v-if="getImageList(selectedProduct.images).length > 1" height="420px" trigger="click" :autoplay="false">
            <el-carousel-item v-for="(img, idx) in getImageList(selectedProduct.images)" :key="idx">
              <el-image :src="img" fit="cover" class="detail-image" :preview-src-list="getImageList(selectedProduct.images)" :initial-index="idx" />
            </el-carousel-item>
          </el-carousel>
          <el-image v-else-if="getFirstImage(selectedProduct.images)" :src="getFirstImage(selectedProduct.images)" fit="cover" class="detail-image" :preview-src-list="getImageList(selectedProduct.images)" />
          <div v-else class="detail-image-placeholder">🧸</div>
        </div>
        <div class="product-detail-right">
          <div class="detail-header">
            <h2 class="detail-title">{{ selectedProduct.name }}</h2>
            <el-button class="detail-close-btn" circle @click="productDetailVisible = false"><el-icon><Close /></el-icon></el-button>
          </div>
          <div class="detail-price-wrap">
            <span class="detail-price-sym">¥</span>
            <span class="detail-price-num">{{ selectedProduct.price }}</span>
            <el-tag v-if="selectedProduct.stock > 0" class="detail-stock-tag" type="success" effect="light" round>库存 {{ selectedProduct.stock }} 件</el-tag>
            <el-tag v-else class="detail-stock-tag" type="danger" effect="light" round>已售罄</el-tag>
          </div>
          
          <div class="detail-desc-box">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
              <h4 style="margin:0">商品描述</h4>
              <div class="detail-util-actions">
                <el-button type="text" @click="toggleFavorite(selectedProduct.id)">
                  <el-icon :color="isProductFavorited(selectedProduct.id) ? '#f5576c' : '#9ea7b4'" size="20">
                    <component :is="isProductFavorited(selectedProduct.id) ? 'StarFilled' : 'Star'" />
                  </el-icon>
                  <span :style="{color: isProductFavorited(selectedProduct.id) ? '#f5576c' : '#9ea7b4', marginLeft: '4px'}">
                    {{ isProductFavorited(selectedProduct.id) ? '已收藏' : '收藏' }}
                  </span>
                </el-button>
                <el-button type="text" @click="reportProduct(selectedProduct)">
                  <el-icon color="#9ea7b4" size="20"><Warning /></el-icon>
                  <span style="color: #9ea7b4; marginLeft: '4px'">举报</span>
                </el-button>
              </div>
            </div>
            <p>{{ selectedProduct.description || '主人很懒，没有写描述~' }}</p>
          </div>
          
          <div class="detail-actions">
            <el-button class="buy-now-btn" type="warning" size="large" round :disabled="!selectedProduct || selectedProduct.stock === 0" @click="checkoutDirectly(selectedProduct.id)">
              立即购买
            </el-button>
            <el-button class="add-cart-btn" type="primary" size="large" round :disabled="!selectedProduct || selectedProduct.stock === 0" @click="addToCart(selectedProduct.id); productDetailVisible = false">
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- ==================== 评价弹窗 ==================== -->
    <el-dialog v-model="reviewDialogVisible" title="评价订单" width="450px" class="custom-dialog">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分"><el-rate v-model="reviewForm.rating" /></el-form-item>
        <el-form-item label="评价内容"><el-input v-model="reviewForm.content" type="textarea" :rows="3" placeholder="说说你的使用感受~" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 售后弹窗 ==================== -->
    <el-dialog v-model="afterSaleDialogVisible" title="申请售后" width="450px" class="custom-dialog">
      <el-form :model="afterSaleForm" label-width="80px">
        <el-form-item label="原因"><el-input v-model="afterSaleForm.reason" placeholder="请输入售后原因" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="afterSaleForm.description" type="textarea" :rows="3" placeholder="请详细描述问题" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="afterSaleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAfterSale">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 发帖弹窗 ==================== -->
    <el-dialog v-model="showPostDialog" title="发布内容" width="500px" class="custom-dialog">
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="postForm.title" placeholder="请输入标题" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="postForm.content" type="textarea" :rows="4" placeholder="分享你的玩偶故事~" /></el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            action="/api/upload"
            :headers="uploadHeaders"
            list-type="picture-card"
            :limit="1"
            :file-list="postFileList"
            :on-success="handlePostImageSuccess"
            :on-remove="handlePostImageRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 投诉弹窗 ==================== -->
    <el-dialog v-model="showComplaintDialog" title="提交投诉" width="450px" class="custom-dialog">
      <el-form :model="complaintForm" label-width="90px">
        <el-form-item label="投诉类型">
          <el-radio-group v-model="complaintForm.type">
            <el-radio :value="1">卖家</el-radio>
            <el-radio :value="2">商品</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="complaintForm.type === 1 ? '被投诉卖家' : '被投诉商品'">
          <el-input v-model="complaintForm.targetName" readonly placeholder="请从商品或详情页发起投诉" />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">ID: {{ complaintForm.targetId }}</div>
        </el-form-item>
        <el-form-item label="投诉原因"><el-input v-model="complaintForm.reason" type="textarea" :rows="3" placeholder="请详细描述投诉原因" /></el-form-item>
        <el-form-item label="凭证图片">
          <el-upload
            action="/api/upload"
            :headers="uploadHeaders"
            list-type="picture-card"
            multiple
            :limit="5"
            :file-list="complaintFileList"
            :on-success="handleComplaintImageSuccess"
            :on-remove="handleComplaintImageRemove"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div style="font-size: 12px; color: #999; margin-top: 8px;">最多上传5张图片，建议尺寸 800x800</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showComplaintDialog = false">取消</el-button>
        <el-button type="primary" @click="submitComplaint">提交投诉</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 结算确认弹窗 ==================== -->
    <el-dialog v-model="checkoutDialogVisible" title="确认订单信息" width="600px" class="custom-dialog">
      <div v-if="checkoutItems.length > 0" class="checkout-content">
        <h4 style="margin-top:0;">📝 商品清单</h4>
        <div class="checkout-list">
          <div v-for="item in checkoutItems" :key="item.id || item.productId" class="checkout-item">
            <el-image v-if="item.productImage" :src="item.productImage" class="checkout-img" fit="cover" />
            <div v-else class="checkout-img-placeholder">🧸</div>
            <div class="checkout-item-info">
              <div class="item-name">{{ item.productName || '商品' }}</div>
              <div class="item-price">单价: ¥{{ item.productPrice || '0.00' }} × {{ item.quantity }} = ¥{{ ((item.productPrice || 0) * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
        </div>
        <el-divider />
        <el-form :model="addressForm" label-width="90px" class="checkout-form">
          <h4 style="margin: 0 0 16px;">📍 收货信息</h4>
          <el-form-item label="收货人"><el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="addressForm.phone" placeholder="请输入手机号" /></el-form-item>
          <el-form-item label="详细地址"><el-input v-model="addressForm.address" type="textarea" :rows="2" placeholder="请输入收货地址" /></el-form-item>
        </el-form>
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

    <!-- ==================== 资讯详情弹窗 ==================== -->
    <el-dialog v-model="newsDetailVisible" width="600px" title="资讯详情" class="custom-dialog">
      <div v-if="selectedNews" class="news-detail-content" style="padding: 10px">
        <h2 style="margin: 0 0 16px; color:#2d2520;">{{ selectedNews.title }}</h2>
        <div class="news-detail-meta" style="margin-bottom: 20px; font-size: 13px; color: #a09088;">
          <span>发布时间：{{ selectedNews.createTime }}</span>
        </div>
        <el-image v-if="selectedNews.coverImage" :src="selectedNews.coverImage" style="width:100%; border-radius:12px; margin-bottom:20px" fit="cover" />
        <div style="white-space: pre-wrap; line-height: 1.8; color:#5a4a42; font-size:15px;" v-html="selectedNews.content"></div>
      </div>
    </el-dialog>

    <!-- ==================== 帖子详情弹窗 ==================== -->
    <el-dialog v-model="postDetailVisible" width="700px" class="custom-dialog post-detail-dialog">
      <div v-if="selectedPost" class="post-detail-content">
        <div class="post-header" style="margin-bottom: 20px; display: flex; align-items: center; gap: 16px;">
          <el-avatar :size="48">{{ selectedPost.userId }}</el-avatar>
          <div class="post-meta" style="flex: 1;">
            <div class="post-user" style="font-size:16px; font-weight: 600; color: #2d2520;">用户 #{{ selectedPost.userId }}</div>
            <div class="post-time" style="font-size: 13px; color: #a09088;">{{ formatFullTime(selectedPost.createTime) }}</div>
          </div>
        </div>
        <h2 style="margin: 0 0 16px; color:#2d2520;">{{ selectedPost.title }}</h2>
        <p style="white-space: pre-wrap; line-height: 1.8; color:#5a4a42; font-size:15px; margin-bottom: 24px;">{{ selectedPost.content }}</p>
        <div v-if="getImageList(selectedPost.images).length" style="display:flex; flex-direction:column; gap:16px; margin-bottom: 32px;">
          <el-image v-for="(img, i) in getImageList(selectedPost.images)" :key="i" :src="img" style="width:100%; border-radius:12px;" :preview-src-list="getImageList(selectedPost.images)" />
        </div>
        
        <!-- 评论区 -->
        <el-divider />
        <div class="comments-section">
          <h4 style="margin: 0 0 16px; color: #2d2520;">💬 评论 ({{ postComments.length }})</h4>
          
          <!-- 发表评论 -->
          <div class="comment-input-box" style="margin-bottom: 24px;">
            <el-input 
              v-model="newComment" 
              type="textarea" 
              :rows="3" 
              placeholder="说说你的看法..." 
              style="margin-bottom: 12px;"
            />
            <el-button type="primary" @click="submitPostComment" :disabled="!newComment.trim()">发表评论</el-button>
          </div>
          
          <!-- 评论列表 -->
          <div class="comments-list">
            <div v-for="comment in postComments" :key="comment.id" class="comment-item" style="padding: 16px 0; border-bottom: 1px dashed #f0ebe8;">
              <div style="display: flex; gap: 12px;">
                <el-avatar :size="36">{{ comment.userId }}</el-avatar>
                <div style="flex: 1;">
                  <div style="font-weight: 600; color: #2d2520; margin-bottom: 4px;">用户 #{{ comment.userId }}</div>
                  <div style="color: #5a4a42; line-height: 1.6; margin-bottom: 8px;">{{ comment.content }}</div>
                  <div style="font-size: 12px; color: #a09088;">{{ formatFullTime(comment.createTime) }}</div>
                </div>
              </div>
            </div>
            <el-empty v-if="postComments.length === 0" description="暂无评论，快来抢沙发吧~" :image-size="80" />
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 竞拍弹窗 -->
    <el-dialog v-model="bidDialogVisible" title="参与竞拍" width="400px" align-center class="custom-dialog">
      <div class="bid-confirm-box" v-if="biddingAuction">
        <div class="current-price-tag">
          <span>当前价格: </span>
          <span class="symbol">¥</span>
          <span class="value">{{ biddingAuction.currentPrice }}</span>
        </div>
        <el-form :model="bidForm" label-position="top" style="margin-top: 20px;">
          <el-form-item label="您的出价 (元)">
            <el-input-number v-model="bidForm.bidPrice" :min="biddingAuction.currentPrice + 1" :step="10" style="width: 100%" size="large" />
          </el-form-item>
        </el-form>
        <p class="bid-tip">温馨提示：拍卖结束前出价最高者获胜</p>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="bidDialogVisible = false" round>取消</el-button>
          <el-button type="warning" @click="submitBid" round class="action-btn">提交出价</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ==================== 修改密码弹窗 ==================== -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px" class="custom-dialog">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPasswordChange">确定修改</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, List, ChatLineSquare, User, Plus, Star, StarFilled, Warning, CreditCard, Check, RefreshRight, CircleClose, Location, Close, Shop, CaretBottom, EditPen, HomeFilled, ChatDotRound, Notification, QuestionFilled, CircleCheckFilled, ChatLineRound } from '@element-plus/icons-vue'
import request from '../../utils/request'

const router = useRouter()
const auctions = ref([])


const formatFullTime = (timeStr) => {
  if (!timeStr) return '—'
  return timeStr.replace('T', ' ').split('.')[0]
}

// 检查是否已登录
const userId = ref(localStorage.getItem('userId'))
const isLogin = computed(() => !!userId.value)
const activeMenu = ref('products')
const profileActiveTab = ref('info') // info, address, posts

// ============ 数据 ============
const products = ref([])
const cartItems = ref([])
const myOrders = ref([])
const afterSales = ref([])
const posts = ref([])
const myComplaints = ref([])
const newsList = ref([])
const favoriteProducts = ref([])
const favoriteIds = computed(() => favoriteProducts.value.map(p => p.id))
const isProductFavorited = (id) => favoriteIds.value.includes(id)
const searchKeyword = ref('')
const orderTabFilter = ref('')
const communityCategory = ref('all') // all, featured, hot, daily, diy, trade

// ============ 弹窗 ============
const productDetailVisible = ref(false)
const reviewDialogVisible = ref(false)
const afterSaleDialogVisible = ref(false)
const showPostDialog = ref(false)
const showComplaintDialog = ref(false)
const checkoutDialogVisible = ref(false)
const postDetailVisible = ref(false)
const newsDetailVisible = ref(false)
const passwordDialogVisible = ref(false)
const bidDialogVisible = ref(false)
const biddingAuction = ref(null)
const bidForm = reactive({
    bidPrice: 0
})
const selectedProduct = ref(null)

const selectedPost = ref(null)
const selectedNews = ref(null)

// ============ 结算与购物车选中 ============
const selectedCartItems = ref([])
const checkoutItems = ref([])
const isSubmittingOrder = ref(false)
const isDirectBuy = ref(false)
const directBuyProduct = ref(null)

// ============ 表单 ============
const reviewForm = reactive({ orderId: null, productId: null, buyerId: userId.value, sellerId: null, rating: 5, content: '' })
const afterSaleForm = reactive({ orderId: null, buyerId: userId.value, reason: '', description: '' })
const postForm = reactive({ title: '', content: '', imageUrl: '' })
const complaintForm = reactive({ targetId: '', targetName: '', type: 1, reason: '', submitterId: userId.value, images: '' })
const profileForm = reactive({ nickname: '', phone: '', avatar: '' })
const addressForm = reactive({ receiver: '', phone: '', address: '' })
const defaultAddress = reactive({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', address: '', isDefault: true })
const postFileList = ref([])
const complaintFileList = ref([])
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const provinces = ['北京市', '天津市', '河北省', '山西省', '内蒙古自治区', '辽宁省', '吉林省', '黑龙江省', '上海市', '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省', '河南省', '湖北省', '湖南省', '广东省', '广西壮族自治区', '海南省', '重庆市', '四川省', '贵州省', '云南省', '西藏自治区', '陕西省', '甘肃省', '青海省', '宁夏回族自治区', '新疆维吾尔自治区', '香港特别行政区', '澳门特别行政区', '台湾省']

const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    profileForm.avatar = res.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handlePostImageSuccess = (res, file, fileList) => {
  if (res.code === 200) {
    postForm.imageUrl = res.data
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handlePostImageRemove = (file, fileList) => {
  postForm.imageUrl = ''
}

const handleComplaintImageSuccess = (res, file, fileList) => {
  if (res.code === 200) {
    updateComplaintImages(fileList)
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleComplaintImageRemove = (file, fileList) => {
  updateComplaintImages(fileList)
}

const updateComplaintImages = (fileList) => {
  const images = fileList
    .filter(f => f.response && f.response.code === 200)
    .map(f => f.response.data)
  complaintForm.images = JSON.stringify(images)
}

// ============ 计算属性 ============
const filteredMyOrders = computed(() => {
  if (orderTabFilter.value === '' || orderTabFilter.value === null) return myOrders.value
  return myOrders.value.filter(o => o.status === orderTabFilter.value)
})

const myOwnPosts = computed(() => {
  return posts.value.filter(p => p.userId == userId.value)
})

const cartTotalAmount = computed(() => {
  return selectedCartItems.value.reduce((sum, item) => sum + (item.productPrice || 0) * item.quantity, 0).toFixed(2)
})

const checkoutTotalAmount = computed(() => {
  return checkoutItems.value.reduce((sum, item) => sum + (item.productPrice || 0) * item.quantity, 0).toFixed(2)
})

// ============ 用户操作下拉菜单 ============
const handleUserCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else {
    activeMenu.value = 'profile'
    if (command === 'profile') profileActiveTab.value = 'info'
    else if (command === 'my_address') profileActiveTab.value = 'address'
    else if (command === 'my_posts') profileActiveTab.value = 'posts'
    else if (command === 'my_favorites') profileActiveTab.value = 'favorites'
    
    if (profileActiveTab.value === 'info') loadProfile()
    else if (profileActiveTab.value === 'address') loadDefaultAddress()
    else if (profileActiveTab.value === 'posts') loadPosts()
    else if (profileActiveTab.value === 'favorites') loadFavorites()
  }
}

// ============ 菜单切换 ============
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'products') searchProducts()
  else if (index === 'cart') loadCart()
  else if (index === 'orders') { loadOrders(); loadAfterSales() }
  else if (index === 'community') loadPosts()
  else if (index === 'auctions') loadAuctions()
  else if (index === 'complaints') loadMyComplaints()
  else if (index === 'news') loadNews()
  else if (index === 'profile') loadProfile()
}

// ============ 拍卖互动 ============
const loadAuctions = async () => {
    try {
        const res = await request.get('/auction/list')
        const list = res.data || []
        const winnerNameCache = new Map()
        // 补全商品基本信息用于展示
        for(let a of list) {
            try {
                const pRes = await request.get(`/product/${a.productId}`)
                if(pRes.data) {
                    a.productName = pRes.data.name
                    a.productImage = getFirstImage(pRes.data.images)
                }
            } catch(e){}

            if (a.winnerId) {
              const key = String(a.winnerId)
              if (winnerNameCache.has(key)) {
                a.winnerName = winnerNameCache.get(key)
              } else {
                try {
                  const uRes = await request.get(`/user/${a.winnerId}`)
                  const winnerName = uRes.data?.nickname || `用户#${a.winnerId}`
                  winnerNameCache.set(key, winnerName)
                  a.winnerName = winnerName
                } catch (e) {
                  const fallback = `用户#${a.winnerId}`
                  winnerNameCache.set(key, fallback)
                  a.winnerName = fallback
                }
              }
            }
        }
        auctions.value = list
    } catch {
        ElMessage.error('加载拍卖失败')
    }
}

const openBidDialog = (auc) => {
    biddingAuction.value = auc
    bidForm.bidPrice = auc.currentPrice + 10 // 默认加10元起跳
    bidDialogVisible.value = true
}

const submitBid = async () => {
    if (bidForm.bidPrice <= biddingAuction.value.currentPrice) {
        return ElMessage.warning('出价必须高于当前价格！')
    }
    try {
        await request.put(`/auction/${biddingAuction.value.id}/bid`, null, {
            params: { userId: userId.value, bidPrice: bidForm.bidPrice }
        })
        ElMessage.success('出价成功！')
        bidDialogVisible.value = false
        loadAuctions()
    } catch {
        ElMessage.error('出价失败')
    }
}

// ============ 工具函数 ============
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
const orderStatusText = (s) => ['待支付','待发货','待收货','已完成','已取消'][s] || '未知'
const orderStatusType = (s) => ['','warning','info','success','danger'][s] || 'info'

// ============ 商品浏览 ============
const searchProducts = async () => {
  try {
    const res = await request.get('/product/search', { params: { keyword: searchKeyword.value } })
    products.value = res.data.records || []
  } catch { ElMessage.error('搜索失败') }
}
const openProductDetail = (p) => { 
  router.push(`/product/${p.id}`) 
}
const openProductDetailById = async (pid) => {
  router.push(`/product/${pid}`)
}
const addToCart = async (productId) => {
  try {
    await request.post('/cart', { productId, quantity: 1, userId: userId.value })
    ElMessage.success('已加入购物车')
  } catch { ElMessage.error('添加失败') }
}

// ============ 收藏与举报 ============
const loadFavorites = async () => {
  try {
    const res = await request.get(`/favorite/user/${userId.value}`)
    const list = res.data || []
    const detailedList = []
    for (let f of list) {
        try {
            const pRes = await request.get(`/product/${f.productId}`)
            if (pRes.data) detailedList.push(pRes.data)
        } catch(e) {
            console.error('Failed to load favorite product info', e)
        }
    }
    favoriteProducts.value = detailedList
  } catch (err) {
    favoriteProducts.value = []
  }
}



const toggleFavorite = async (productId) => {
  try {
    await request.post('/favorite', { userId: userId.value, productId: productId })
    const isNowFavorited = !isProductFavorited(productId)
    ElMessage.success(isNowFavorited ? '收藏成功' : '已取消收藏')
    loadFavorites() // 刷新收藏列表
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const reportProduct = (product) => {
  complaintForm.type = 2
  complaintForm.targetId = product.id
  complaintForm.targetName = product.name
  complaintForm.reason = ''
  showComplaintDialog.value = true
}

// ============ 购物车 ============
const loadCart = async () => {
  try {
    const res = await request.get(`/cart/user/${userId.value}`)
    const items = res.data || []
    // 遍历获取商品详细信息补充至购物车列表中
    for (let item of items) {
      try {
        const pRes = await request.get(`/product/${item.productId}`)
        if (pRes.data) {
          item.productName = pRes.data.name
          item.productImage = getFirstImage(pRes.data.images)
          item.productPrice = pRes.data.price
          item.sellerId = pRes.data.sellerId
          item.stock = pRes.data.stock
          item.status = pRes.data.status
        }
      } catch (e) {
        console.error('获取商品详情失败', e)
      }
    }
    cartItems.value = items
  } catch { ElMessage.error('加载购物车失败') }
}
const handleCartSelectionChange = (val) => {
  selectedCartItems.value = val
}
const removeFromCart = async (id) => {
  try { await request.delete(`/cart/${id}`); ElMessage.success('已删除'); loadCart() }
  catch { ElMessage.error('删除失败') }
}
const updateCartItem = async (item) => {
  try { await request.put(`/cart/${item.id}`, { quantity: item.quantity }) }
  catch { ElMessage.error('更新失败') }
}

const openCheckoutDialog = () => {
  if(selectedCartItems.value.length === 0) return ElMessage.warning('请先勾选需要结算的商品')
  isDirectBuy.value = false
  checkoutItems.value = JSON.parse(JSON.stringify(selectedCartItems.value))
  
  // 填充默认地址
  addressForm.receiver = defaultAddress.receiver
  addressForm.phone = defaultAddress.phone
  addressForm.address = defaultAddress.address

  checkoutDialogVisible.value = true
}

const checkoutDirectly = (productId) => {
  if(!selectedProduct.value) return
  isDirectBuy.value = true
  directBuyProduct.value = selectedProduct.value
  checkoutItems.value = [{
    productId: selectedProduct.value.id,
    productName: selectedProduct.value.name,
    productImage: getFirstImage(selectedProduct.value.images),
    productPrice: selectedProduct.value.price,
    sellerId: selectedProduct.value.sellerId,
    quantity: 1
  }]
  
  // 填充默认地址
  addressForm.receiver = defaultAddress.receiver
  addressForm.phone = defaultAddress.phone
  addressForm.address = defaultAddress.address

  productDetailVisible.value = false
  checkoutDialogVisible.value = true
}

const confirmCheckout = async () => {
  if (!addressForm.receiver || !addressForm.phone || !addressForm.address) {
    return ElMessage.warning('请填写完整的收货信息！')
  }
  isSubmittingOrder.value = true
  try {
    if(isDirectBuy.value) {
      const orderData = {
        buyerId: userId.value,
        sellerId: directBuyProduct.value.sellerId,
        productId: directBuyProduct.value.id,
        quantity: checkoutItems.value[0].quantity,
        totalAmount: checkoutTotalAmount.value,
        receiver: addressForm.receiver,
        phone: addressForm.phone,
        address: addressForm.address
      }
      const res = await request.post('/order', orderData)
      // 模拟支付成功，将状态修改为 1(待发货)
      if (res.data && res.data.id) {
        await request.put(`/order/${res.data.id}/status`, null, { params: { status: 1 } })
      }
    } else {
      // 购物车批量下单，假设有/order/batch接口，如果没有则循环下单或者改用后端支持的逻辑
      // 此处假设为循环下单示例(实际项目中应提供批量接口)
      for(const item of checkoutItems.value) {
         const orderData = {
           buyerId: userId.value,
           sellerId: item.sellerId, // 购物车需包含sellerId
           productId: item.productId,
           quantity: item.quantity,
           totalAmount: ((item.productPrice || 0) * item.quantity).toFixed(2),
           receiver: addressForm.receiver,
           phone: addressForm.phone,
           address: addressForm.address
         }
         const res = await request.post('/order', orderData)
         // 模拟支付成功，将状态修改为 1(待发货)
         if (res.data && res.data.id) {
           await request.put(`/order/${res.data.id}/status`, null, { params: { status: 1 } })
         }
      }
      // 下单成功后清理购物车
      for(const item of cartItems.value) {
        await request.delete(`/cart/${item.id}`)
      }
    }
    
    ElMessage.success('下单支付成功！')
    checkoutDialogVisible.value = false
    activeMenu.value = 'orders'
    loadOrders()
  } catch (err) {
    ElMessage.error('下单失败，可能库存不足')
  } finally {
    isSubmittingOrder.value = false
  }
}

// ============ 订单管理 ============
const loadOrders = async () => {
  try {
    const res = await request.get(`/order/buyer/${userId.value}`)
    const orders = res.data || []
    
    // Process each order to ensure items have product names and images
    for (const order of orders) {
      if (order.items && order.items.length > 0) {
        for (const item of order.items) {
          // If product info is missing, fetch it
          if (!item.productName || !item.productImage) {
            try {
              const pRes = await request.get(`/product/${item.productId}`)
              if (pRes.data) {
                item.productName = pRes.data.name
                item.productImage = getFirstImage(pRes.data.images)
              }
            } catch (e) {
              console.error('Failed to fetch product details for order item', e)
            }
          }
        }
      } else {
        // Fallback: This shouldn't happen with the new backend logic, 
        // but adding for robustness in case items aren't returned.
        order.items = []
      }
    }
    
    myOrders.value = orders
  } catch (err) {
    ElMessage.error('加载订单失败')
  }
}
const filterMyOrders = () => {}
const confirmReceive = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
    await request.put(`/order/${id}/status`, null, { params: { status: 3 } })
    ElMessage.success('已确认收货')
    loadOrders()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const payOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '提示', { type: 'info' })
    await request.put(`/order/${id}/status`, null, { params: { status: 1 } })
    ElMessage.success('支付成功')
    loadOrders()
  } catch (e) { if (e !== 'cancel') ElMessage.error('支付失败') }
}

const cancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '警告', { type: 'warning' })
    await request.put(`/order/${id}/status`, null, { params: { status: 4 } })
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

// ============ 评价 ============
const openReviewDialog = (order) => {
  reviewForm.orderId = order.id
  reviewForm.sellerId = order.sellerId
  reviewForm.buyerId = userId.value
  // 为评价关联商品ID（解决数据库不能为空报错）
  if (order.items && order.items.length > 0) {
    reviewForm.productId = order.items[0].productId
  }
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewDialogVisible.value = true
}
const submitReview = async () => {
  try {
    await request.post('/review', reviewForm)
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    loadOrders() // 刷新订单列表以更新状态（如果评价后状态改变）
  } catch { ElMessage.error('评价失败') }
}

// ============ 售后 ============
const loadAfterSales = async () => {
  try {
    const res = await request.get(`/aftersale/buyer/${userId.value}`)
    const list = res.data || []
    // 使用买家订单列表进行关联，避免调用可能不存在的 /order/{id} 接口导致 404
    let orderList = myOrders.value || []
    if (!orderList.length) {
      try {
        const ordersRes = await request.get(`/order/buyer/${userId.value}`)
        orderList = ordersRes.data || []
      } catch (e) {
        orderList = []
      }
    }
    const orderMap = new Map(orderList.map(o => [String(o.id), o]))

    for (const as of list) {
      const order = orderMap.get(String(as.orderId))
      if (!order) continue

      as.orderInfo = order
      if (order.items && order.items.length > 0) {
        const firstItem = order.items[0]
        as.productName = firstItem.productName
        as.productImage = firstItem.productImage

        if ((!as.productName || !as.productImage) && firstItem.productId) {
          try {
            const pRes = await request.get(`/product/${firstItem.productId}`)
            if (pRes.data) {
              if (!as.productName) as.productName = pRes.data.name
              if (!as.productImage) as.productImage = getFirstImage(pRes.data.images)
            }
          } catch (e) {}
        }
      }
    }
    afterSales.value = list
  } catch { afterSales.value = [] }
}
const openAfterSaleDialog = (order) => {
  afterSaleForm.orderId = order.id
  afterSaleForm.buyerId = userId.value
  afterSaleForm.reason = ''
  afterSaleForm.description = ''
  afterSaleDialogVisible.value = true
}
const submitAfterSale = async () => {
  try {
    await request.post('/aftersale', afterSaleForm)
    ElMessage.success('售后申请已提交')
    afterSaleDialogVisible.value = false
    loadAfterSales()
  } catch { ElMessage.error('申请失败') }
}

// ============ 互动社区 ============
const postComments = ref([])
const newComment = ref('')
const favoritedPostIds = ref([])
// 使用Map存储每个帖子的评论，key是postId，value是评论数组
const postCommentsMap = ref(new Map())
const postCommentsStorageKey = computed(() => `doll_post_comments_${userId.value || 'guest'}`)

const normalizePostId = (postId) => String(postId)

const loadPersistedPostComments = () => {
  try {
    const raw = localStorage.getItem(postCommentsStorageKey.value)
    if (!raw) {
      postCommentsMap.value = new Map()
      return
    }
    const parsed = JSON.parse(raw)
    const entries = Object.entries(parsed).map(([key, value]) => [String(key), Array.isArray(value) ? value : []])
    postCommentsMap.value = new Map(entries)
  } catch (e) {
    postCommentsMap.value = new Map()
  }
}

const persistPostComments = () => {
  const obj = {}
  for (const [key, value] of postCommentsMap.value.entries()) {
    obj[String(key)] = Array.isArray(value) ? value : []
  }
  localStorage.setItem(postCommentsStorageKey.value, JSON.stringify(obj))
}

const loadPosts = async () => {
  try {
    const res = await request.get('/post/list')
    posts.value = res.data
  } catch { ElMessage.error('加载帖子失败') }
}

const openPostDetail = async (post) => {
  selectedPost.value = post
  postDetailVisible.value = true
  // 从Map中加载该帖子的评论
  const postKey = normalizePostId(post.id)
  if (postCommentsMap.value.has(postKey)) {
    postComments.value = postCommentsMap.value.get(postKey)
  } else {
    postComments.value = []
  }
  newComment.value = ''
}

const isPostFavorited = (postId) => {
  return favoritedPostIds.value.includes(postId)
}

const togglePostFavorite = async (postId) => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    // 这里使用favorite接口，type可以扩展支持帖子收藏
    // 暂时使用简单的本地状态管理
    const index = favoritedPostIds.value.indexOf(postId)
    if (index > -1) {
      favoritedPostIds.value.splice(index, 1)
      ElMessage.success('已取消收藏')
    } else {
      favoritedPostIds.value.push(postId)
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const submitPostComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!selectedPost.value) {
    ElMessage.error('帖子信息丢失')
    return
  }
  try {
    // 创建评论对象
    const comment = {
      id: Date.now(),
      userId: userId.value,
      content: newComment.value,
      createTime: new Date().toISOString()
    }

    // 保存到Map中并持久化，避免重新进入帖子后评论丢失
    const postKey = normalizePostId(selectedPost.value.id)
    if (!postCommentsMap.value.has(postKey)) {
      postCommentsMap.value.set(postKey, [])
    }
    const list = postCommentsMap.value.get(postKey)
    list.unshift(comment)
    postComments.value = list
    persistPostComments()

    newComment.value = ''
    ElMessage.success('评论成功')
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const submitPost = async () => {
  try {
    const data = {
      userId: userId.value, title: postForm.title, content: postForm.content,
      images: postForm.imageUrl ? JSON.stringify([postForm.imageUrl]) : null,
      status: 1
    }
    await request.post('/post', data)
    ElMessage.success('发布成功')
    showPostDialog.value = false
    Object.assign(postForm, { title: '', content: '', imageUrl: '' })
    postFileList.value = []
    loadPosts()
  } catch { ElMessage.error('发布失败') }
}

// ============ 举报投诉 ============
const loadMyComplaints = async () => {
  try {
    const res = await request.get(`/complaint/user/${userId.value}`)
    const list = res.data || []
    for(let c of list) {
      if(c.type === 1) { // 卖家
        const sRes = await request.get(`/user/${c.targetId}`)
        if(sRes.data) c.targetName = sRes.data.shopName || sRes.data.nickname
      } else { // 商品
        const pRes = await request.get(`/product/${c.targetId}`)
        if(pRes.data) {
          c.targetName = pRes.data.name
          c.productImage = getFirstImage(pRes.data.images) // 添加商品图片
        }
      }
    }
    myComplaints.value = list
  } catch { myComplaints.value = [] }
}
const submitComplaint = async () => {
  try {
    await request.post('/complaint', complaintForm)
    ElMessage.success('投诉已提交')
    showComplaintDialog.value = false
    Object.assign(complaintForm, { targetId: '', targetName: '', type: 2, reason: '', images: '' })
    complaintFileList.value = []
    loadMyComplaints()
  } catch { ElMessage.error('提交失败') }
}

// ============ 资讯浏览 ============
const loadNews = async () => {
  try {
    const res = await request.get('/news/list')
    newsList.value = res.data
  } catch { ElMessage.error('加载资讯失败') }
}
const openNewsDetail = (news) => {
  selectedNews.value = news
  newsDetailVisible.value = true
}

// ============ 个人中心 ============
const loadProfile = async () => {
  try {
    const res = await request.get(`/user/${userId.value}`)
    Object.assign(profileForm, { nickname: res.data.nickname || '', phone: res.data.phone || '', avatar: res.data.avatar || '' })
  } catch { ElMessage.error('加载信息失败') }
}
const updateProfile = async () => {
  try {
    await request.put(`/user/profile/${userId.value}`, profileForm)
    ElMessage.success('保存成功')
  } catch { ElMessage.error('保存失败') }
}

// 缓存默认地址
const loadDefaultAddress = () => {
  const addr = localStorage.getItem('defaultAddress')
  if (addr) {
    try {
      const parsed = JSON.parse(addr)
      Object.assign(defaultAddress, parsed)
    } catch {}
  }
}
const saveDefaultAddress = () => {
  // 合并详细地址
  defaultAddress.address = `${defaultAddress.province||''}${defaultAddress.city||''}${defaultAddress.district||''} ${defaultAddress.detail||''}`
  localStorage.setItem('defaultAddress', JSON.stringify(defaultAddress))
  ElMessage.success('地址已保存')
}

// ============ 退出 ============
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    .then(() => { localStorage.removeItem('token'); localStorage.removeItem('userId'); localStorage.removeItem('role'); ElMessage.success('已退出'); router.push('/login') })
    .catch(() => {})
}

const submitPasswordChange = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  try {
    const res = await request.post(`/user/password/${userId.value}`, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      passwordDialogVisible.value = false
      Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('role')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '修改失败，请检查原密码')
  }
}


onMounted(() => { 
  loadPersistedPostComments()
  searchProducts() 
  loadDefaultAddress()
  loadFavorites()
})
</script>

<style scoped>
.buyer-home { min-height: 100vh; background: #faf8f6; }

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
.logout-btn { border-color: rgba(255,255,255,0.6) !important; color: rgba(255,255,255,0.9) !important; background: rgba(255,255,255,0.1) !important; }
.logout-btn:hover { background: rgba(255,255,255,0.2) !important; }

/* ====== Main Content ====== */
.main-content { padding: 24px 32px; width: 100%; margin: 0 auto; }
.page-section { animation: fadeIn 0.4s ease; width: 100%; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; width: 100%; }
.section-header h3 { font-size: 20px; font-weight: 600; color: #2d2520; margin: 0; }
.action-btn { background: linear-gradient(135deg, #f5576c, #ff8a5c); border: none; border-radius: 8px; }
.action-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(245,87,108,0.3); }

/* ====== Search ====== */
.search-bar { display: flex; justify-content: center; margin-bottom: 28px; }
.custom-search { width: 560px; }
.custom-search :deep(.el-input__wrapper) { border-radius: 24px 0 0 24px; box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
.custom-search :deep(.el-input-group__append) { background: linear-gradient(135deg, #f5576c, #ff8a5c); color: white; border: none; border-radius: 0 24px 24px 0; font-weight: 600; }

/* ====== Products Grid ====== */
.products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card {
  background: white; border-radius: 16px; overflow: hidden; cursor: pointer;
  transition: all 0.3s ease; border: 1px solid #f0ebe8;
}
.product-card:hover { transform: translateY(-6px); box-shadow: 0 12px 28px rgba(0,0,0,0.08); }
.product-img-wrap { position: relative; height: 200px; overflow: hidden; background: #f5f0ed; }
.product-img { width: 100%; height: 100%; }
.product-img-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 60px; background: linear-gradient(135deg, #fff5f3, #ffeee8); }
.product-badge {
  position: absolute; top: 10px; right: 10px; background: #f5576c; color: white;
  padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 600;
}
.product-badge.sold-out { background: #999; }
.product-info { padding: 16px; }
.product-name { margin: 0 0 6px; font-size: 15px; color: #2d2520; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-desc { margin: 0 0 12px; font-size: 12px; color: #a09088; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-footer { display: flex; align-items: center; justify-content: space-between; }
.product-price { display: flex; align-items: baseline; }
.price-sym { color: #f5576c; font-size: 13px; font-weight: 700; }
.price-num { color: #f5576c; font-size: 22px; font-weight: 700; margin-left: 2px; }
.cart-btn { background: linear-gradient(135deg, #f5576c, #ff8a5c); border: none; width: 36px; height: 36px; padding: 0; }
.cart-btn :deep(.el-icon) { font-size: 16px; }

/* ====== Cart Footer ====== */
.cart-footer { display: flex; justify-content: flex-end; align-items: center; gap: 24px; margin-top: 24px; background: white; padding: 20px 32px; border-radius: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
.cart-total { font-size: 15px; color: #5a4a42; }
.cart-total .price-num { font-size: 28px; color: #f5576c; font-weight: bold; margin-left: 8px; }
.checkout-btn { background: linear-gradient(135deg, #fce38a, #f38181); border: none; font-weight: 600; padding: 0 32px; height: 44px; color: white; box-shadow: 0 4px 12px rgba(243, 129, 129, 0.2); }
.checkout-btn:hover { box-shadow: 0 6px 16px rgba(243, 129, 129, 0.3); transform: translateY(-1px); }

/* ====== Custom Premium Buttons ====== */
.custom-btn {
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none !important;
}

.review-btn {
  background: linear-gradient(135deg, #ff8a5c 0%, #f5576c 100%) !important;
  color: white !important;
}

.aftersale-btn {
  background: linear-gradient(135deg, #fce38a 0%, #f38181 100%) !important;
  color: white !important;
}

.custom-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  opacity: 0.9;
}

.order-action-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* ====== Checkout Dialog ====== */
.checkout-list { max-height: 250px; overflow-y: auto; display: flex; flex-direction: column; gap: 12px; margin-bottom: 16px; }
.checkout-item { display: flex; gap: 16px; background: #faf8f6; padding: 12px; border-radius: 8px; align-items: center; border: 1px solid #f0ebe8; }
.checkout-img { width: 48px; height: 48px; border-radius: 6px; object-fit: cover; }
.checkout-img-placeholder { width: 48px; height: 48px; border-radius: 6px; background: #e0d7d2; display: flex; align-items: center; justify-content: center; font-size: 20px; }
.checkout-item-info { flex: 1; }
.item-name { font-size: 14px; font-weight: 600; color: #2d2520; margin-bottom: 4px; }
.item-price { font-size: 13px; color: #7a6b62; }
.checkout-summary { text-align: right; padding-top: 16px; align-items: baseline; }

/* ====== Simple Community ====== */
.simple-community { max-width: 800px; margin: 0 auto; }
.community-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #f0ebe8; }
.community-header h3 { margin: 0 0 4px; color: #2d2520; }
.community-header p { margin: 0; color: #a09088; font-size: 14px; }
.publish-btn { border-radius: 12px; font-weight: bold; background: linear-gradient(135deg, #f5576c, #ff8a5c); border: none; }

.posts-feed { display: flex; flex-direction: column; gap: 20px; }
.simple-post-card { background: white; border-radius: 16px; padding: 24px; border: 1px solid #f0ebe8; cursor: pointer; transition: all 0.3s; }
.simple-post-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0,0,0,0.06); }
.post-user-info { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.user-meta { display: flex; flex-direction: column; }
.user-id { font-size: 14px; font-weight: 600; color: #2d2520; }
.post-date { font-size: 12px; color: #b5a89f; }
.post-title { margin: 0 0 12px; font-size: 18px; color: #2d2520; }
.post-text { margin: 0 0 16px; font-size: 14px; color: #5a4a42; line-height: 1.6; }
.post-media { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; }
.feed-img { width: 140px; height: 140px; border-radius: 8px; object-fit: cover; }
.post-actions { display: flex; gap: 24px; padding-top: 16px; border-top: 1px dashed #f0ebe8; }
.post-actions .action { font-size: 13px; color: #a09088; display: flex; align-items: center; gap: 6px; }
.post-actions .action:hover { color: #f5576c; }

/* ====== Post Detail Dialog ====== */
.post-detail-content { padding: 10px; }

/* ====== News ====== */
.news-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
.news-card {
  background: white; border-radius: 14px; overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04); border: 1px solid #f0ebe8;
  transition: all 0.3s;
}
.news-card:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.06); }
.news-cover { height: 160px; overflow: hidden; }
.news-img { width: 100%; height: 100%; }
.news-body { padding: 16px 20px; }
.news-title { margin: 0 0 8px; font-size: 16px; font-weight: 600; color: #2d2520; }
.news-content { margin: 0 0 8px; font-size: 13px; color: #7a6b62; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.news-time { font-size: 12px; color: #b5a89f; }

/* ====== Profile ====== */
.custom-address-form :deep(.el-form-item__label) { color: #5a4a42; font-weight: bold; padding-bottom: 4px; }
.profile-layout { display: flex; gap: 24px; }
.profile-sidebar { width: 340px; flex-shrink: 0; }
.profile-main { flex: 1; }
.profile-card { background: white; border-radius: 16px; padding: 32px; box-shadow: 0 4px 20px rgba(0,0,0,0.04); text-align: center; border: 1px solid #f0ebe8; }
.form-card { text-align: left; padding: 32px 40px; }
.profile-avatar { display: flex; justify-content: center; margin-bottom: 20px; }
.profile-avatar .avatar-uploader { display: block; }
.profile-avatar .avatar-uploader .el-upload { border: 2px dashed #e4dcd7; border-radius: 50%; cursor: pointer; position: relative; overflow: hidden; transition: var(--el-transition-duration-fast); }
.profile-avatar .avatar-uploader .el-upload:hover { border-color: #f5576c; }
.profile-avatar .avatar { width: 100px; height: 100px; display: block; object-fit: cover; border-radius: 50%; }
.profile-avatar .avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 100px; height: 100px; text-align: center; line-height: 100px; border-radius: 50%; background: #f9f5f2; }
.profile-name { margin: 0 0 8px; font-size: 20px; color: #2d2520; }
.profile-role { display: flex; align-items: center; justify-content: center; gap: 8px; margin: 0 0 24px; }
.profile-id { font-size: 13px; color: #a09088; }
.profile-stats { display: flex; justify-content: center; align-items: center; background: #faf8f6; padding: 16px; border-radius: 12px; }
.stat-item { flex: 1; cursor: pointer; transition: all 0.3s; padding: 4px; border-radius: 8px; }
.stat-item:hover { background: #f0ebe8; }
.stat-num { font-size: 20px; font-weight: bold; color: #f5576c; margin-bottom: 4px; }
.stat-desc { font-size: 12px; color: #7a6b62; }
.profile-form { margin-top: 24px; }
.profile-form :deep(.el-input__wrapper), .profile-form :deep(.el-textarea__inner) { border-radius: 10px; box-shadow: 0 0 0 1px #e4dcd7 inset; }
.profile-form :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #f5576c inset !important; }

/* ====== Dialog ====== */
.custom-dialog :deep(.el-dialog) { border-radius: 16px; }
.custom-dialog :deep(.el-dialog__header) { border-bottom: 1px solid #f0ebe8; padding: 16px 24px; }
.custom-dialog :deep(.el-dialog__body) { padding: 24px; }
.custom-dialog :deep(.el-dialog__footer) { border-top: 1px solid #f0ebe8; padding: 12px 24px; }

/* ====== Product Detail Dialog ====== */
.detail-dialog { background: transparent !important; box-shadow: none !important; }
.detail-dialog :deep(.el-dialog) { border-radius: 20px; overflow: hidden; }
.detail-dialog :deep(.el-dialog__header) { display: none; margin: 0; padding: 0; }
.detail-dialog :deep(.el-dialog__body) { padding: 0; }
.product-detail-layout { display: flex; height: 420px; background: white; border-radius: 20px; overflow: hidden; }
.product-detail-left { width: 400px; flex-shrink: 0; background: #f5f0ed; position: relative; }
.detail-image { width: 100%; height: 100%; }
.detail-image-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 80px; background: linear-gradient(135deg, #fff5f3, #ffeee8); }
.product-detail-right { flex: 1; padding: 32px; display: flex; flex-direction: column; overflow: hidden; }
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; gap: 16px; }
.detail-title { margin: 0; font-size: 22px; font-weight: 600; color: #2d2520; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.detail-close-btn { border: none; background: #f0ebe8; color: #7a6b62; transition: all 0.3s; flex-shrink: 0; }
.detail-close-btn:hover { background: #e0d7d2; transform: rotate(90deg); color: #f5576c; }
.detail-price-wrap { display: flex; align-items: baseline; margin-bottom: 24px; }
.detail-price-sym { color: #f5576c; font-size: 20px; font-weight: 600; }
.detail-price-num { color: #f5576c; font-size: 36px; font-weight: 700; margin-left: 2px; }
.detail-stock-tag { margin-left: 16px; font-size: 13px; }
.detail-desc-box { flex: 1; background: #faf8f6; padding: 16px; border-radius: 12px; margin-bottom: 24px; overflow-y: auto; }
.detail-desc-box h4 { margin: 0 0 8px; font-size: 14px; color: #9a8a82; font-weight: normal; }
.detail-desc-box p { margin: 0; font-size: 14px; color: #5a4a42; line-height: 1.6; white-space: pre-wrap; }
.detail-actions { display: flex; gap: 16px; margin-top: auto; }
.auction-prices { margin-top: 10px; background: #fffcf0; padding: 10px; border-radius: 8px; border: 1px dashed #fce38a; }
.price-item { display: flex; justify-content: space-between; font-size: 12px; color: #9a8a82; margin-bottom: 4px; }
.price-item.current { color: #f5576c; font-weight: bold; border-top: 1px solid #f9f5f2; padding-top: 4px; margin-top: 4px; font-size: 14px; }

.bid-confirm-box { text-align: center; padding: 10px 0; }
.current-price-tag { font-size: 16px; color: #5a4a42; }
.current-price-tag .value { font-size: 32px; font-weight: bold; color: #f5576c; margin-left: 4px; }
.bid-tip { font-size: 12px; color: #a09088; margin-top: 12px; }

.buy-now-btn { flex: 1; background: linear-gradient(135deg, #fce38a, #f38181); border: none; font-weight: 600; color: #fff; box-shadow: 0 4px 12px rgba(243, 129, 129, 0.2); }
.buy-now-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 6px 16px rgba(243, 129, 129, 0.3); }
.add-cart-btn { flex: 1; background: linear-gradient(135deg, #f5576c, #ff8a5c); border: none; font-weight: 600; box-shadow: 0 4px 12px rgba(245, 87, 108, 0.2); }
.add-cart-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 6px 16px rgba(245, 87, 108, 0.3); }

/* ====== Order Actions ====== */
.order-action-group { display: flex; gap: 8px; justify-content: flex-end; }
.review-btn, .aftersale-btn { border-radius: 8px; font-weight: bold; transition: all 0.3s; }
.review-btn { background: linear-gradient(135deg, #f5576c, #ff8a5c); border: none; }
.review-btn:hover { box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3); transform: scale(1.05); }
.aftersale-btn:hover { background: #fdf6f5; color: #f5576c; transform: scale(1.05); }

.complaint-images { display: flex; gap: 8px; margin-top: 8px; flex-wrap: wrap; }
.mini-complaint-img { width: 50px; height: 50px; border-radius: 4px; border: 1px solid #f0ebe8; cursor: pointer; }

/* ====== Profile Layout Update ====== */
.profile-sidebar-nav { width: 220px; flex-shrink: 0; background: white; border-radius: 16px; padding: 24px 0; box-shadow: 0 4px 20px rgba(0,0,0,0.04); border: 1px solid #f0ebe8; align-self: flex-start; }
.profile-nav-header { padding: 0 24px 24px; border_bottom: 1px solid #f9f5f2; margin-bottom: 20px; display: flex; flex_direction: column; align-items: center; text-align: center; }
.profile-avatar-wrap { margin-bottom: 12px; }
.avatar-uploader-small .el-upload { border: 2px dashed #e4dcd7; border-radius: 50%; cursor: pointer; position: relative; overflow: hidden; transition: 0.3s; }
.avatar-uploader-small .el-upload:hover { border_color: #f5576c; }
.avatar-small { width: 64px; height: 64px; border_radius: 50%; object-fit: cover; }
.avatar-uploader-icon-small { font-size: 20px; color: #8c939d; width: 64px; height: 64px; line-height: 64px; text-align: center; background: #f9f5f2; border_radius: 50%; }
.nav-nickname { font-size: 16px; font-weight: 600; color: #2d2520; }
.nav-id { font-size: 12px; color: #a09088; margin-top: 4px; }

.side-nav-menu { display: flex; flex-direction: column; gap: 4px; padding: 0 12px; }
.side-nav-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 8px; cursor: pointer; color: #5a4a42; font-size: 14px; transition: all 0.3s; }
.side-nav-item:hover { background: #fdf6f5; color: #f5576c; }
.side-nav-item.active { background: #fff0f2; color: #f5576c; font-weight: bold; }

.profile-main { flex: 1; min-width: 0; }
.card-header { margin-bottom: 24px; border-bottom: 1px solid #f9f5f2; padding-bottom: 12px; text-align: left; }
.card-header h3 { margin: 0; font-size: 18px; color: #2d2520; }

/* ====== Favorite & Posts Styling ====== */
.fav-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 16px; }
.fav-item { cursor: pointer; transition: 0.3s; border-radius: 12px; overflow: hidden; border: 1px solid #f0ebe8; }
.fav-item:hover { transform: translateY(-4px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.fav-img { width: 100%; height: 140px; }
.fav-info { padding: 10px; text-align: center; }
.fav-name { font-size: 13px; font-weight: bold; color: #2d2520; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.fav-price { color: #f5576c; font-size: 14px; font-weight: bold; }

.user-posts { display: flex; flex-direction: column; gap: 12px; }
.user-post-item { padding: 16px; background: #faf8f6; border-radius: 12px; cursor: pointer; border: 1px solid #f0ebe8; transition: 0.3s; text-align: left; }
.user-post-item:hover { background: #fff0f2; border-color: #f5576c; }
.up-title { font-size: 15px; font-weight: 600; color: #2d2520; margin-bottom: 6px; }
.up-time { font-size: 12px; color: #a09088; }

/* ====== Product Action Buttons ====== */
.product-actions { display: flex; align-items: center; gap: 8px; }
.fav-icon-btn { padding: 0; min-width: 32px; height: 32px; font-size: 20px; transition: transform 0.2s; }
.fav-icon-btn:hover { transform: scale(1.2); }

@media (max-width: 1200px) { .products-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px) { .products-grid { grid-template-columns: repeat(2, 1fr); } .news-grid { grid-template-columns: 1fr; } .profile-layout { flex-direction: column; } .profile-sidebar-nav { width: 100%; } }
@media (max-width: 600px) { .products-grid { grid-template-columns: 1fr; } }
</style>
