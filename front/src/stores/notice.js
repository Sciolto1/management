import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useNoticeStore = defineStore('notice', () => {
  const notices = ref([])

  // 获取所有通知
  async function fetchNotices() {
    const res = await request.get('/notice/list')
    notices.value = res.data
  }

  // 添加通知
  async function addNotice(notice) {
    await request.post('/notice/add', notice)
    await fetchNotices()
  }

  // 更新通知
  async function updateNotice(notice) {
    await request.put('/notice/update', notice)
    await fetchNotices()
  }

  // 删除通知
  async function deleteNotice(id) {
    await request.delete(`/notice/delete/${id}`)
    await fetchNotices()
  }

  return {
    notices,
    fetchNotices,
    addNotice,
    updateNotice,
    deleteNotice
  }
})
