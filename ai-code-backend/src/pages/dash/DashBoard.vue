<script setup lang="ts">
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { getCountByCodeGenType, getAppCount } from '@/api/appController'
import { getViewTrend, getTotalViews } from '@/api/viewController'
import { getUserCount } from '@/api/userController'
import { message } from 'ant-design-vue'

const typeMap: Record<string, string> = {
  html: '原生HTML模式',
  multi_file: '原生多文件模式',
  vue_project: 'Vue项目模式'
}

// 图表相关引用
const pieChartRef = ref<HTMLElement>()
const pieChart = shallowRef<echarts.ECharts | null>(null)
const lineChartRef = ref<HTMLElement>()
const lineChart = shallowRef<echarts.ECharts | null>(null)
const totalViews = ref<number>(0)
const appCount = ref<number>(0)
const userCount = ref<number>(0)

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

// 折线图配置
const getLineChartOption = (dates: string[], values: number[]): EChartsOption => ({
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    top: '10%',
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: dates
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '访问量',
      type: 'line',
      stack: 'Total',
      smooth: true,
      lineStyle: {
        width: 3
      },
      showSymbol: false,
      areaStyle: {
        opacity: 0.8,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          {
            offset: 0,
            color: 'rgb(128, 255, 165)'
          },
          {
            offset: 1,
            color: 'rgb(1, 191, 236)'
          }
        ])
      },
      emphasis: {
        focus: 'series'
      },
      data: values
    }
  ]
})

// 初始化图表
const initCharts = async () => {
  // 初始化饼图
  if (pieChartRef.value) {
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

  // 初始化折线图
  if (lineChartRef.value) {
    try {
      const res = await getViewTrend()
      if (res.data && res.data.code === 0 && res.data.data) {
        // 后端是降序查出(最新的在前面)，图表需要时间从左到右递增，所以需要反转
        const data = res.data.data.reverse()
        const dates = data.map(item => item.dateStr || '')
        const values = data.map(item => item.viewNum || 0)

        // 确保实例被清除或重用
        if (lineChart.value) {
          lineChart.value.dispose()
        }
        lineChart.value = echarts.init(lineChartRef.value)
        lineChart.value.setOption(getLineChartOption(dates, values))
      }
    } catch (e: any) {
      message.error('加载访问趋势图表出错: ' + e.message)
    }
  }
}

const loadOverviewData = async () => {
  try {
    const [appRes, userRes, viewRes] = await Promise.all([
      getAppCount(),
      getUserCount(),
      getTotalViews()
    ])
    if (appRes.data?.code === 0) {
      appCount.value = Number(appRes.data.data) || 0
    }
    if (userRes.data?.code === 0) {
      userCount.value = Number(userRes.data.data) || 0
    }
    if (viewRes.data?.code === 0) {
      totalViews.value = Number(viewRes.data.data) || 0
    }
  } catch (e: any) {
    message.error('加载统计数据失败: ' + e.message)
  }
}

// 处理窗口大小变化
const handleResize = () => {
  pieChart.value?.resize()
  lineChart.value?.resize()
}

onMounted(() => {
  loadOverviewData()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart.value?.dispose()
  lineChart.value?.dispose()
})
</script>

<template>
  <div class="dashboard-container">
    <a-row :gutter="15" style="margin-bottom: 15px">
      <a-col :xs="8" :sm="8" :md="8">
        <a-card :bordered="false" hoverable>
          <a-statistic title="总用户数" :value="userCount" />
        </a-card>
      </a-col>
      <a-col :xs="8" :sm="8" :md="8">
        <a-card :bordered="false" hoverable>
          <a-statistic title="总应用数" :value="appCount" />
        </a-card>
      </a-col>
      <a-col :xs="8" :sm="8" :md="8">
        <a-card :bordered="false" hoverable>
          <a-statistic title="总访问量" :value="totalViews" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[20,20]">
      <a-col :xs="24" :sm="24" :md="24" :lg="12">
        <a-card title="最近七天访问量趋势" :bordered="false" class="chart-card">
          <div ref="lineChartRef" class="chart"></div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="24" :md="24" :lg="12">
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
  height: 350px;
  width: 100%;
}
.chart-card {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .chart {
    width: 310px;
  }

  .dashboard-container {
    padding: 12px;
  }
}
</style>
