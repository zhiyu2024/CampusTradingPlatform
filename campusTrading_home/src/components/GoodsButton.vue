<template>
  <div class="section">
    <div class="section-header">
      <h2 class="section-title">公告</h2>
    </div>
    <div class="notice-list">
      <div v-for="news in campusNews" :key="news.id" class="notice-item">
        <span class="notice-tag">公告</span>
        <a href="javascript:void(0)">{{ news.title }}</a>
        <span class="notice-time">{{ news.time }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 校园新闻数据
const campusNews = ref([
  {
    id: 1,
    title: "校园二手交易市集本周六举办",
    time: "2024-05-20"
  },
  {
    id: 2,
    title: "新版教材回收政策发布",
    time: "2024-05-18"
  }
])
</script>

<style scoped>
.section {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: "";
  width: 5px;
  height: 22px;
  background: #4CAF50;
  border-radius: 3px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.notice-tag {
  background: #FF5722;
  color: #fff;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
}

.notice-item a {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.notice-item a:hover {
  color: #4CAF50;
}

.notice-time {
  color: #999;
  font-size: 13px;
}
</style>