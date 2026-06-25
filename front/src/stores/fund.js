import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useFundStore = defineStore('fund', () => {
  const funds = ref([])
  const transactions = ref([])
  const statistics = ref(null)

  // 获取所有公积金账户
  async function fetchFunds(department, status) {
    const params = {}
    if (department) params.department = department
    if (status) params.status = status
    const res = await request.get('/fund/list', { params })
    funds.value = res.data
  }

  // 获取有效账户
  async function fetchActiveFunds() {
    const res = await request.get('/fund/active')
    funds.value = res.data
  }

  // 根据ID获取
  async function getFundById(id) {
    const res = await request.get(`/fund/${id}`)
    return res.data
  }

  // 根据账号获取
  async function getFundByAccountNo(accountNo) {
    const res = await request.get(`/fund/account/${accountNo}`)
    return res.data
  }

  // 根据员工ID获取
  async function getFundByEmployeeId(employeeId) {
    const res = await request.get(`/fund/employee/${employeeId}`)
    return res.data
  }

  // 添加公积金账户
  async function addFund(fund) {
    await request.post('/fund/add', fund)
    await fetchFunds()
  }

  // 更新公积金账户
  async function updateFund(fund) {
    await request.put('/fund/update', fund)
    await fetchFunds()
  }

  // 封存账户
  async function sealFund(id) {
    await request.put(`/fund/seal/${id}`)
    await fetchFunds()
  }

  // 解封账户
  async function unsealFund(id) {
    await request.put(`/fund/unseal/${id}`)
    await fetchFunds()
  }

  // 缴存公积金
  async function depositFund(id) {
    await request.post(`/fund/deposit/${id}`)
    await fetchFunds()
  }

  // 删除公积金账户
  async function deleteFund(id) {
    await request.delete(`/fund/delete/${id}`)
    await fetchFunds()
  }

  // 获取统计数据
  async function fetchStatistics() {
    const res = await request.get('/fund/statistics')
    statistics.value = res.data
    return res.data
  }

  // 获取帐务记录
  async function fetchTransactions(fundId) {
    const res = await request.get(`/fund/transaction/list/${fundId}`)
    transactions.value = res.data
    return res.data
  }

  // 获取所有帐务记录
  async function fetchAllTransactions() {
    const res = await request.get('/fund/transaction/all')
    transactions.value = res.data
    return res.data
  }

  // 根据时间范围查询帐务
  async function fetchTransactionsByDateRange(startDate, endDate) {
    const res = await request.get('/fund/transaction/dateRange', {
      params: { startDate, endDate }
    })
    transactions.value = res.data
    return res.data
  }

  // 根据类型查询帐务
  async function fetchTransactionsByType(type) {
    const res = await request.get(`/fund/transaction/type/${type}`)
    transactions.value = res.data
    return res.data
  }

  // 添加帐务记录
  async function addTransaction(transaction) {
    await request.post('/fund/transaction/add', transaction)
  }

  // 删除帐务记录
  async function deleteTransaction(id) {
    await request.delete(`/fund/transaction/delete/${id}`)
  }

  // 批量缴存
  async function batchDeposit(ids) {
    await request.post('/fund/batchDeposit', { ids })
    await fetchFunds()
  }

  return {
    funds,
    transactions,
    statistics,
    fetchFunds,
    fetchActiveFunds,
    getFundById,
    getFundByAccountNo,
    getFundByEmployeeId,
    addFund,
    updateFund,
    sealFund,
    unsealFund,
    depositFund,
    deleteFund,
    fetchStatistics,
    fetchTransactions,
    fetchAllTransactions,
    fetchTransactionsByDateRange,
    fetchTransactionsByType,
    addTransaction,
    deleteTransaction,
    batchDeposit
  }
})
