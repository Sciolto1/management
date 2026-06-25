import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useDepartmentStore = defineStore('department', () => {
  const departments = ref([])
  const departmentPage = ref({
    records: [],
    total: 0,
    current: 1,
    size: 10
  })

  async function fetchDepartments() {
    const res = await request.get('/department/list')
    departments.value = res.data
  }

  async function fetchDepartmentPage(params) {
    const res = await request.get('/department/page', { params })
    departmentPage.value = res.data
    return res.data
  }

  async function getDepartmentById(id) {
    const res = await request.get(`/department/${id}`)
    return res.data
  }

  async function addDepartment(department) {
    await request.post('/department/add', department)
    await fetchDepartments()
  }

  async function updateDepartment(department) {
    await request.put('/department/update', department)
    await fetchDepartments()
  }

  async function deleteDepartment(id) {
    await request.delete(`/department/delete/${id}`)
    await fetchDepartments()
  }

  async function batchDeleteDepartments(ids) {
    await request.delete('/department/batch-delete', { data: ids })
    await fetchDepartments()
  }

  return {
    departments,
    departmentPage,
    fetchDepartments,
    fetchDepartmentPage,
    getDepartmentById,
    addDepartment,
    updateDepartment,
    deleteDepartment,
    batchDeleteDepartments
  }
})
