import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useEmployeeStore = defineStore('employee', () => {
  const employees = ref([])

  // 获取所有员工（支持按部门过滤）
  async function fetchEmployees(department) {
    const params = department ? { department } : {}
    const res = await request.get('/employee/list', { params })
    employees.value = res.data
  }

  // 根据ID获取员工
  async function getEmployeeById(id) {
    const res = await request.get(`/employee/${id}`)
    return res.data
  }

  // 添加员工
  async function addEmployee(employee) {
    await request.post('/employee/add', employee)
    await fetchEmployees()
  }

  // 更新员工
  async function updateEmployee(employee) {
    await request.put('/employee/update', employee)
    await fetchEmployees()
  }

  // 删除员工
  async function deleteEmployee(id) {
    await request.delete(`/employee/delete/${id}`)
    await fetchEmployees()
  }

  return {
    employees,
    fetchEmployees,
    getEmployeeById,
    addEmployee,
    updateEmployee,
    deleteEmployee
  }
})
