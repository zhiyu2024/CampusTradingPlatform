<template>
  <div class="hot-books-container">
    <h2>热门商品</h2>
    <!-- 外层遍历分类 -->
    <div v-for="book in books" :key="book.id" class="book-category">
      <h5 class="category-name">{{ book.name }}</h5>
      <!-- 内层遍历分类下的商品，展示图片+名称 -->
      <div class="book-items">
        <span v-for="item in book.children" :key="item.id" class="book-item">
          <!-- 商品图片 -->
          <img 
            :src="item.image || 'https://picsum.photos/80/80?random=' + item.id" 
            alt="商品图片" 
            class="book-img"
          >
          <!-- 商品名称（保留路由跳转） -->
          <router-link to="" class="book-name">{{ item.name }}</router-link>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";

// 修正：初始化books为数组（原代码是字符串，遍历会报错）
const books = ref([]);

onMounted(() => {
  axios.get("/category")
    .then(res => {
      console.log(res);
      // 接口返回数据赋值给books（需确保res.data是数组格式）
      books.value = res.data;
    })
    .catch(err => {
      console.log(err);
    });
});
</script>

<style scoped>
/* 容器样式，调整布局紧凑性 */
.hot-books-container {
  padding: 10px;
}

/* 分类标题样式 */
.book-category {
  margin-bottom: 15px;
}
.category-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

/* 商品列表布局 */
.book-items {
  display: flex;
  flex-wrap: wrap;
  gap: 15px; /* 商品间距 */
}

/* 单个商品样式（图片+名称） */
.book-item {
  display: flex;
  flex-direction: column; /* 图片在上，名称在下 */
  align-items: center;
  width: 80px; /* 固定宽度，保证布局整齐 */
}

/* 商品图片样式 */
.book-img {
  width: 80px;
  height: 80px;
  object-fit: cover; /* 图片自适应，不变形 */
  border-radius: 4px; /* 轻微圆角，更美观 */
  margin-bottom: 5px;
}

/* 商品名称样式 */
.book-name {
  font-size: 12px;
  color: #666;
  text-decoration: none; /* 去除路由链接下划线 */
  text-align: center;
  white-space: nowrap; /* 名称不换行 */
  overflow: hidden; /* 超出隐藏 */
  text-overflow: ellipsis; /* 超出显示省略号 */
  width: 100%;
}
.book-name:hover {
  color: #1989fa; /* hover时变色，提升交互体验 */
}
</style>