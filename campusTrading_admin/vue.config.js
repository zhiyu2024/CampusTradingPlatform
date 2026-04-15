const { defineConfig } = require('@vue/cli-service')
const path = require('path')
 
module.exports = defineConfig({
    lintOnSave: false,  // ✅ 添加这一行，禁用 ESLint
  transpileDependencies: true,
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true
      }
    }
  },
  configureWebpack: {
    
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    }
  }
})