const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: '/', // ✅ 确保静态资源正确访问
  
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        pathRewrite: {}
      },
      '/images': {
        target: 'http://localhost:8090',
        changeOrigin: true
      }
    }
  }
})