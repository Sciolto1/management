import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useAttendanceStore = defineStore('attendance', () => {
  const attendanceRecords = ref([])
  const todayRecord = ref(null)
  const allRecords = ref([])

  // 获取所有考勤记录（管理员）
  async function fetchAllRecords() {
    const res = await request.get('/attendance/list')
    allRecords.value = res.data
  }

  // 获取员工考勤记录
  async function fetchByEmployeeId(employeeId) {
    const res = await request.get(`/attendance/employee/${employeeId}`)
    attendanceRecords.value = res.data
  }

  // 获取今日考勤
  async function fetchTodayRecord(employeeId) {
    const res = await request.get(`/attendance/today/${employeeId}`)
    todayRecord.value = res.data
  }

  // 上班打卡
  async function clockIn(employeeId) {
    await request.post(`/attendance/clockIn/${employeeId}`)
    await fetchTodayRecord(employeeId)
    await fetchByEmployeeId(employeeId)
  }

  // 下班打卡
  async function clockOut(employeeId) {
    await request.post(`/attendance/clockOut/${employeeId}`)
    await fetchTodayRecord(employeeId)
    await fetchByEmployeeId(employeeId)
  }

  // 更新考勤状态
  async function updateStatus(id, status) {
    await request.put(`/attendance/updateStatus/${id}?status=${status}`)
    await fetchAllRecords()
  }

  return {
    attendanceRecords,
    todayRecord,
    allRecords,
    fetchAllRecords,
    fetchByEmployeeId,
    fetchTodayRecord,
    clockIn,
    clockOut,
    updateStatus
  }
})
