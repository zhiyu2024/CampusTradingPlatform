<template>
  <div class="home">
  
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>校园交易平台 © 2024 版权所有</p>
        <p class="mt-2">联系我们：service@campusmarket.com</p>
        <p class="mt-1">地址：水汽大学闪电大楼</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
/* 核心：让整个容器至少占满视口高度 */
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 0 !important; /* 强制移除之前的内边距 */
}

/* 关键：让主体内容自动撑开剩余空间 */
.main-content {
  flex: 1 0 auto; /* 自动填充，不被压缩 */
  width: 100%;
}

/* 页脚 - 移除固定定位，自然沉底 */
.footer {
  background-color: #e4e1e1; /* 保留你的原背景色 */
  color: #b4b4b5;          /* 保留你的原文字色 */
  width: 100%;              /* 占满整个页面宽度 */
  margin-top: auto;         /* 自动推到最底部 */
  /* 移除所有fixed相关样式 */
  position: static !important;
}

/* 页脚内容区 - 居中显示 */
.footer-content {
  max-width: 1280px;
  margin: 0 auto;    /* 内容居中 */
  padding: 24px 16px; /* py-6 = 24px */
  text-align: center;
  font-size: 14px;
}

/* 段落间距 */
.footer-content p {
  margin: 0;
  line-height: 1.6;
}

.footer-content p + p {
  margin-top: 8px; /* 等同于mt-2 */
}
</style>