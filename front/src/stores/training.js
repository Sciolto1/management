import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useTrainingStore = defineStore('training', () => {
  const trainings = ref([])

  // 获取所有培训
  async function fetchTrainings() {
    const res = await request.get('/training/list')
    trainings.value = res.data
  }

  // 添加培训
  async function addTraining(training) {
    await request.post('/training/add', training)
    await fetchTrainings()
  }

  // 更新培训
  async function updateTraining(training) {
    await request.put('/training/update', training)
    await fetchTrainings()
  }

  // 删除培训
  async function deleteTraining(id) {
    await request.delete(`/training/delete/${id}`)
    await fetchTrainings()
  }

  // 报名培训
  async function enrollTraining(trainingId, employeeId) {
    await request.post('/training/enroll', { trainingId, employeeId })
    await fetchTrainings()
  }

  // 取消报名
  async function cancelEnroll(trainingId, employeeId) {
    await request.post('/training/cancelEnroll', { trainingId, employeeId })
    await fetchTrainings()
  }

  return {
    trainings,
    fetchTrainings,
    addTraining,
    updateTraining,
    deleteTraining,
    enrollTraining,
    cancelEnroll
  }
})
