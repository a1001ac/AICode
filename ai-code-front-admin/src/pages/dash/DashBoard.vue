<script setup lang="ts">
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { getCountByCodeGenType } from '@/api/appController'
import { message } from 'ant-design-vue'

const typeMap: Record<string, string> = {
  html: '原生HTML模式',
  multi_file: '原生多文件模式',
  vue_project: 'Vue项目模式'
}

// 图表相关引用
const pieChartRef = ref<HTMLElement>()
const pieChart = shallowRef<echarts.ECharts | null>(null)

// 饼图配置
const getPieChartOption = (data: any[]): EChartsOption => ({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [{
    name: '应用分类统计',
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: {
      borderRadius: 10,
      borderColor: '#fff',
      borderWidth: 2
    },
    label: {
      show: false,
      position: 'center'
    },
    emphasis: {
      label: {
        show: true,
        fontSize: 20,
        fontWeight: 'bold'
      }
    },
    labelLine: {
      show: false
    },
    data: data
  }]
})

// 初始化图表
const initCharts = async () => {
  if (!pieChartRef.value) return

  try {
    const res = await getCountByCodeGenType()
    if (res.data.code === 0 && res.data.data) {
      // 将后端 Map<String, Long> 转换为 ECharts 需要的 { name, value } 格式
      const chartData = Object.keys(res.data.data).map(key => ({
        name: typeMap[key] || key,
        value: res.data.data?.[key]
      }))

      pieChart.value = echarts.init(pieChartRef.value)
      pieChart.value.setOption(getPieChartOption(chartData))
    } else {
      message.error('获取图表数据失败: ' + res.data.message)
    }
  } catch (e: any) {
    message.error('加载图表出错: ' + e.message)
  }
}

// 处理窗口大小变化
const handleResize = () => {
  pieChart.value?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart.value?.dispose()
})
</script>

<template>
  <div class="dashboard-container">
    <a-row :gutter="24">
      <a-col :span="12">
        <a-card title="应用分类统计" :bordered="false" class="chart-card">
          <div ref="pieChartRef" class="chart"></div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.chart {
  height: 400px;
  width: 100%;
}
.chart-card {
  margin-bottom: 20px;
}
</style>
