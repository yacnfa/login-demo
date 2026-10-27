<template>
  <div>
    <el-card>
      <h3>仪表盘首页</h3>
      <p>欢迎进入工作中台，展示统计数据</p>

      <el-row :gutter="20" style="margin-top:20px">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="num-card">用户总数：136</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="num-card">待办任务：18</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="num-card">已完成任务：421</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="num-card">系统消息：6</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>月度任务完成情况</template>
          <!-- 必须有高度，否则图表高度为 0 看不到 -->
          <div ref="barRef" class="chart" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>用户角色分布</template>
          <div ref="pieRef" class="chart" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// ECharts 按需引入（第 7.6 节）：只注册用到的模块，减小打包体积
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  CanvasRenderer
])

export default {
  name: "Dashboard",
  data() {
    return {
      barChart: null,   // 保存图表实例，供 resize / dispose 使用
      pieChart: null
    }
  },
  mounted() {
    // DOM 已经渲染完成，可以初始化图表了
    this.initBarChart()
    this.initPieChart()
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    // 清理，防止内存泄漏与报错
    window.removeEventListener('resize', this.handleResize)
    this.barChart && this.barChart.dispose()
    this.pieChart && this.pieChart.dispose()
  },
  methods: {
    initBarChart() {
      // ① 初始化实例，绑定到 ref 拿到的 DOM
      this.barChart = echarts.init(this.$refs.barRef)
      // ② 配置图表
      this.barChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['已完成', '待办'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月', '6月']
        },
        yAxis: { type: 'value' },
        series: [
          {
            name: '已完成',
            type: 'bar',
            data: [120, 200, 150, 80, 70, 110],
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '待办',
            type: 'bar',
            data: [20, 32, 18, 30, 25, 12],
            itemStyle: { color: '#E6A23C' }
          }
        ]
      })
    },
    initPieChart() {
      this.pieChart = echarts.init(this.$refs.pieRef)
      this.pieChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            name: '角色分布',
            type: 'pie',
            radius: ['45%', '65%'],   // 环形图
            center: ['50%', '45%'],
            data: [
              { value: 26, name: '管理员' },
              { value: 110, name: '普通用户' }
            ]
          }
        ]
      })
    },
    handleResize() {
      this.barChart && this.barChart.resize()
      this.pieChart && this.pieChart.resize()
    }
  }
}
</script>

<style scoped>
.num-card {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.chart {
  width: 100%;
}
</style>
