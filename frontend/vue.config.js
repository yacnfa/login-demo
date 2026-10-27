const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  publicPath: '/',
  devServer: {
    port: 8081,
    // 前端（8081）调用 /api 时，代理到后端 redisson-login-demo（8080）
    // 注意：后端接口本身前缀就是 /api，所以不要做 pathRewrite 去掉它
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
