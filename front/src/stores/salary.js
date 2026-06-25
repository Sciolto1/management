import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useSalaryStore = defineStore('salary', () => {
  const salaryRecords = ref([])

  // 获取所有工资记录
  async function fetchSalaries() {
    const res = await request.get('/salary/list')
    salaryRecords.value = res.data
  }

  // 获取员工工资记录
  async function fetchByEmployeeId(employeeId) {
    const res = await request.get(`/salary/employee/${employeeId}`)
    salaryRecords.value = res.data
  }

  // 添加工资记录
  async function addSalary(salary) {
    await request.post('/salary/add', salary)
    await fetchSalaries()
  }

  // 更新工资记录
  async function updateSalary(salary) {
    await request.put('/salary/update', salary)
    await fetchSalaries()
  }

  // 删除工资记录
  async function deleteSalary(id) {
    await request.delete(`/salary/delete/${id}`)
    await fetchSalaries()
  }

  // 更新工资状态（发放）
  async function updateStatus(id, status) {
    await request.put(`/salary/updateStatus/${id}?status=${status}`)
    await fetchSalaries()
  }

  return {
    salaryRecords,
    fetchSalaries,
    fetchByEmployeeId,
    addSalary,
    updateSalary,
    updateStatus,
    deleteSalary
  }
})
