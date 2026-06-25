import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useLeaveStore = defineStore('leave', () => {
  const leaveRequests = ref([])

  // 获取所有请假申请
  async function fetchLeaveRequests() {
    const res = await request.get('/leave/list')
    leaveRequests.value = res.data
  }

  // 获取员工请假记录
  async function fetchByEmployeeId(employeeId) {
    const res = await request.get(`/leave/employee/${employeeId}`)
    leaveRequests.value = res.data
  }

  // 提交请假申请
  async function addLeaveRequest(leaveRequest) {
    await request.post('/leave/add', leaveRequest)
  }

  // 审批请假
  async function approveLeave(id, status) {
    await request.put(`/leave/approve/${id}?status=${status}`)
    await fetchLeaveRequests()
  }

  // 删除请假申请
  async function deleteLeaveRequest(id) {
    await request.delete(`/leave/delete/${id}`)
  }

  return {
    leaveRequests,
    fetchLeaveRequests,
    fetchByEmployeeId,
    addLeaveRequest,
    approveLeave,
    deleteLeaveRequest
  }
})
