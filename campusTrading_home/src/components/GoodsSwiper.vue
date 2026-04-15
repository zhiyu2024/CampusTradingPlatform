<template>
  <div class="carousel">
    <img :src="carouselImages[currentIndex]" alt="轮播图">
    <div class="carousel-dots">
      <span 
        v-for="(img, idx) in carouselImages" 
        :key="idx"
        :class="{ active: currentIndex === idx }"
        @click="switchImg(idx)"
      ></span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

// 使用 require() 正确引用图片
const carouselImages = ref([
  require('@/assets/轮播图1.png'),
  require('@/assets/轮播图2.png')
]);


// 当前显示的图片索引
const currentIndex = ref(0);

// 自动轮播定时器
let carouselTimer = null;

// 下一张
const nextImg = () => {
  currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
};

// 点击指示器切换图片
const switchImg = (idx) => {
  currentIndex.value = idx;
};

// 开启自动轮播（挂载时）
onMounted(() => {
  carouselTimer = setInterval(() => {
    nextImg();
  }, 3000);
});

// 销毁时清除定时器
onUnmounted(() => {
  clearInterval(carouselTimer);
});
</script>

<style scoped>
.carousel {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  height: 350px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32px;
  margin-bottom: 30px;
  overflow: hidden;
  position: relative;
}

.carousel img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.carousel-dots span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
}

.carousel-dots span.active {
  background: #fff;
}
</style>