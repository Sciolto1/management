import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/Login/MyLogin.vue'),
    },
    // 管理员路由
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Admin/AdminLayout.vue'),
      meta: { requiresAuth: true, role: 'admin' },
      redirect: '/admin/statistics',
      children: [
        {
          path: 'statistics',
          name: 'DataStatistics',
          component: () => import('../views/Admin/DataStatistics.vue')
        },
        {
          path: 'department',
          name: 'DepartmentManage',
          component: () => import('../views/Admin/DepartmentManage.vue')
        },
        {
          path: 'employee',
          name: 'EmployeeManage',
          component: () => import('../views/Admin/EmployeeManage.vue')
        },
        {
          path: 'notice',
          name: 'NoticeManage',
          component: () => import('../views/Admin/NoticeManage.vue')
        },
        {
          path: 'leave',
          name: 'LeaveApproval',
          component: () => import('../views/Admin/LeaveApproval.vue')
        },
        {
          path: 'salary',
          name: 'SalaryManage',
          component: () => import('../views/Admin/SalaryManage.vue')
        },
        {
          path: 'attendance',
          name: 'AttendanceManage',
          component: () => import('../views/Admin/AttendanceManage.vue')
        },
        {
          path: 'performance',
          name: 'PerformanceManage',
          component: () => import('../views/Admin/PerformanceManage.vue')
        },
        {
          path: 'training',
          name: 'TrainingManage',
          component: () => import('../views/Admin/TrainingManage.vue')
        },
        {
          path: 'fund',
          name: 'FundManage',
          component: () => import('../views/Admin/FundManage.vue')
        },
        {
          path: 'transfer',
          name: 'TransferManage',
          component: () => import('../views/Admin/TransferManage.vue')
        }
      ]
    },
    // 用户路由
    {
      path: '/user',
      name: 'user',
      component: () => import('../views/User/UserLayout.vue'),
      meta: { requiresAuth: true, role: 'user' },
      children: [
        {
          path: 'profile',
          name: 'UserProfile',
          component: () => import('../views/User/UserProfile.vue')
        },
        {
          path: 'notice',
          name: 'UserNotice',
          component: () => import('../views/User/UserNotice.vue')
        },
        {
          path: 'leave',
          name: 'UserLeave',
          component: () => import('../views/User/UserLeave.vue')
        },
        {
          path: 'attendance',
          name: 'UserAttendance',
          component: () => import('../views/User/UserAttendance.vue')
        },
        {
          path: 'salary',
          name: 'UserSalary',
          component: () => import('../views/User/UserSalary.vue')
        },
        {
          path: 'performance',
          name: 'UserPerformance',
          component: () => import('../views/User/UserPerformance.vue')
        },
        {
          path: 'training',
          name: 'UserTraining',
          component: () => import('../views/User/UserTraining.vue')
        },
        {
          path: 'fund',
          name: 'UserFund',
          component: () => import('../views/User/UserFund.vue')
        }
      ]
    }
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth) {
    if (!authStore.isLoggedIn) {
      next({ name: 'login' })
    } else {
      const userRole = authStore.currentUser?.role
      const pageRole = to.meta.role
      // dept_admin 可以访问 admin 和 user 路由
      if (pageRole && userRole !== pageRole) {
        if (userRole === 'dept_admin' && (pageRole === 'admin' || pageRole === 'user')) {
          next()
        } else if (userRole === 'admin') {
          next()
        } else {
          next({ name: userRole === 'admin' ? 'admin' : 'user' })
        }
      } else {
        next()
      }
    }
  } else {
    next()
  }
})

export default router
