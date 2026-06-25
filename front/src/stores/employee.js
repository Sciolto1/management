import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useEmployeeStore = defineStore('employee', () => {
  const employees = ref([])
  const employeePage = ref({
    records: [],
    total: 0,
    current: 1,
    size: 10
  })

  async function fetchEmployees(department) {
    const params = department ? { department } : {}
    const res = await request.get('/employee/list', { params })
    employees.value = res.data
  }

  async function fetchEmployeePage(params) {
    const res = await request.get('/employee/page', { params })
    employeePage.value = res.data
    return res.data
  }

  async function getEmployeeById(id) {
    const res = await request.get(`/employee/${id}`)
    return res.data
  }

  async function searchEmployees(params) {
    const res = await request.get('/employee/search', { params })
    return res.data
  }

  async function getStatistics() {
    const res = await request.get('/employee/statistics')
    return res.data
  }

  async function addEmployee(employee) {
    await request.post('/employee/add', employee)
    await fetchEmployees()
  }

  async function updateEmployee(employee) {
    await request.put('/employee/update', employee)
    await fetchEmployees()
  }

  async function deleteEmployee(id) {
    await request.delete(`/employee/delete/${id}`)
    await fetchEmployees()
  }

  async function batchDeleteEmployees(ids) {
    await request.delete('/employee/batch-delete', { data: ids })
    await fetchEmployees()
  }

  return {
    employees,
    employeePage,
    fetchEmployees,
    fetchEmployeePage,
    getEmployeeById,
    searchEmployees,
    getStatistics,
    addEmployee,
    updateEmployee,
    deleteEmployee,
    batchDeleteEmployees
  }
})
