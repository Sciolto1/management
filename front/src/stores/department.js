import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useDepartmentStore = defineStore('department', () => {
  const departments = ref([])

  // 获取所有部门
  async function fetchDepartments() {
    const res = await request.get('/department/list')
    departments.value = res.data
  }

  // 添加部门
  async function addDepartment(department) {
    await request.post('/department/add', department)
    await fetchDepartments()
  }

  // 更新部门
  async function updateDepartment(department) {
    await request.put('/department/update', department)
    await fetchDepartments()
  }

  // 删除部门
  async function deleteDepartment(id) {
    await request.delete(`/department/delete/${id}`)
    await fetchDepartments()
  }

  return {
    departments,
    fetchDepartments,
    addDepartment,
    updateDepartment,
    deleteDepartment
  }
})
