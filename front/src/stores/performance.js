import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const usePerformanceStore = defineStore('performance', () => {
  const performances = ref([])

  // 获取所有绩效记录
  async function fetchPerformances() {
    const res = await request.get('/performance/list')
    performances.value = res.data
  }

  // 获取员工绩效记录
  async function fetchByEmployeeId(employeeId) {
    const res = await request.get(`/performance/employee/${employeeId}`)
    performances.value = res.data
  }

  // 添加绩效记录
  async function addPerformance(performance) {
    await request.post('/performance/add', performance)
    await fetchPerformances()
  }

  // 更新绩效记录
  async function updatePerformance(performance) {
    await request.put('/performance/update', performance)
    await fetchPerformances()
  }

  // 删除绩效记录
  async function deletePerformance(id) {
    await request.delete(`/performance/delete/${id}`)
    await fetchPerformances()
  }

  return {
    performances,
    fetchPerformances,
    fetchByEmployeeId,
    addPerformance,
    updatePerformance,
    deletePerformance
  }
})
