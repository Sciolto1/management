<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>数据统计</h2>
      <p>平台运营数据概览</p>
    </div>
    
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ store.totalProducts }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">🏷️</div>
        <div class="stat-info">
          <div class="stat-value">{{ store.onSaleCount }}</div>
          <div class="stat-label">在售商品</div>
        </div>
      </div>
      <div class="stat-card info">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-value">{{ store.totalOrders }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ store.totalTradeAmount.toLocaleString() }}</div>
          <div class="stat-label">交易金额</div>
        </div>
      </div>
    </div>

    <div class="stats-row secondary">
      <div class="stat-card small">
        <div class="stat-value">{{ store.completedOrders }}</div>
        <div class="stat-label">已完成订单</div>
      </div>
      <div class="stat-card small">
        <div class="stat-value">{{ store.pendingOrders }}</div>
        <div class="stat-label">待付款订单</div>
      </div>
      <div class="stat-card small">
        <div class="stat-value">{{ store.unreadMessages }}</div>
        <div class="stat-label">未读消息</div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-title">📊 商品分类分布</div>
        <div ref="pieChart" class="chart"></div>
      </div>
      <div class="chart-card">
        <div class="chart-title">📈 月度交易金额</div>
        <div ref="barChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import { useSecondhandStore } from '@/stores/secondhand'

const store = useSecondhandStore()
const pieChart = ref(null)
const barChart = ref(null)
let pieInstance, barInstance

const initCharts = () => {
  // 饼图 - 商品分类
  pieInstance = echarts.init(pieChart.value)
  pieInstance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}件 ({d}%)' },
    color: ['#52c41a', '#73d13d', '#95de64', '#b7eb8f', '#d9f7be', '#f6ffed'],
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '55%'],
      data: store.categoryStats,
      label: { show: true, formatter: '{b}' },
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
      }
    }]
  })

  // 柱状图 - 月度交易
  barInstance = echarts.init(barChart.value)
  barInstance.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}<br/>交易额: ¥{c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: { 
      type: 'category', 
      data: store.monthlyTrade.map(m => m.month),
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' }
    },
    yAxis: { 
      type: 'value', 
      axisLabel: { formatter: '¥{value}', color: '#666' },
      splitLine: { lineStyle: { color: '#eee' } }
    },
    series: [{
      data: store.monthlyTrade.map(m => m.amount),
      type: 'bar',
      barWidth: '50%',
      itemStyle: { 
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#52c41a' },
          { offset: 1, color: '#95de64' }
        ]),
        borderRadius: [6, 6, 0, 0]
      }
    }]
  })
}

const handleResize = () => {
  pieInstance?.resize()
  barInstance?.resize()
}

watch(() => store.categoryStats, () => {
  pieInstance?.setOption({ series: [{ data: store.categoryStats }] })
}, { deep: true })

watch(() => store.monthlyTrade, () => {
  barInstance?.setOption({
    xAxis: { data: store.monthlyTrade.map(m => m.month) },
    series: [{ data: store.monthlyTrade.map(m => m.amount) }]
  })
}, { deep: true })

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieInstance?.dispose()
  barInstance?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 8px 0;
}

.page-header p {
  color: #999;
  margin: 0;
  font-size: 14px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stats-row.secondary {
  grid-template-columns: repeat(3, 1fr);
}

.stat-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-card.small {
  padding: 20px;
  flex-direction: column;
  text-align: center;
  gap: 8px;
}

.stat-icon {
  font-size: 36px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
  border-radius: 12px;
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #f6ffed 0%, #b7eb8f 100%);
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #e6f7ff 0%, #91d5ff 100%);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #fffbe6 0%, #ffe58f 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
}

.stat-card.small .stat-value {
  font-size: 24px;
  color: #52c41a;
}

.stat-label {
  color: #999;
  font-size: 14px;
  margin-top: 4px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.chart {
  height: 300px;
}
</style>
