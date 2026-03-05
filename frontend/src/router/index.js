import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import BuyerHome from '../views/buyer/Home.vue'
import SellerHome from '../views/seller/Home.vue'
import AdminHome from '../views/admin/Home.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/buyer', component: BuyerHome },
  { path: '/seller', component: SellerHome },
  { path: '/admin', component: AdminHome }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
