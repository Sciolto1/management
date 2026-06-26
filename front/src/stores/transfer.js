import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useTransferStore = defineStore('transfer', () => {
  const transferPage = ref({
    records: [],
    total: 0,
    current: 1,
    size: 10
  })
  const transferDetail = ref(null)
  const employeeTransfers = ref([])
  const statistics = ref(null)

  async function fetchTransferPage(params) {
    const res = await request.get('/transfer/page', { params })
    transferPage.value = res.data
    return res.data
  }

  async function getTransferById(id) {
    const res = await request.get(`/transfer/${id}`)
    transferDetail.value = res.data
    return res.data
  }

  async function getTransfersByEmployee(employeeId) {
    const res = await request.get(`/transfer/employee/${employeeId}`)
    employeeTransfers.value = res.data
    return res.data
  }

  async function addTransfer(transfer) {
    const res = await request.post('/transfer/add', transfer)
    return res.data
  }

  async function approveTransfer(id, approver) {
    const res = await request.put(`/transfer/approve/${id}`, null, {
      params: { approver }
    })
    return res.data
  }

  async function rejectTransfer(id, approver, remark) {
    const res = await request.put(`/transfer/reject/${id}`, null, {
      params: { approver, remark }
    })
    return res.data
  }

  async function deleteTransfer(id) {
    const res = await request.delete(`/transfer/delete/${id}`)
    return res.data
  }

  async function batchDeleteTransfers(ids) {
    const res = await request.delete('/transfer/batchDelete', { data: ids })
    return res.data
  }

  async function fetchStatistics() {
    const res = await request.get('/transfer/statistics')
    statistics.value = res.data
    return res.data
  }

  return {
    transferPage,
    transferDetail,
    employeeTransfers,
    statistics,
    fetchTransferPage,
    getTransferById,
    getTransfersByEmployee,
    addTransfer,
    approveTransfer,
    rejectTransfer,
    deleteTransfer,
    batchDeleteTransfers,
    fetchStatistics
  }
})
