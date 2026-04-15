<template>
  <h2>分类图书</h2> 
    <div style="text-align: left;" v-for="(book, index) in books" :key="index">
      <router-link :to="'/book/' + book.id">
      <div class="search-value">
        <div class="search-left">
          <img :src="book.imgUrl" alt="" width="200" />
        </div>
        <div class="search-right">
          <h3>{{ book.title }}</h3>
          <p>作者：{{ book.author }}</p>
          <p>{{ book.brief }}</p>
        </div>
        </div>
      </router-link>
    </div>  
    <h4>{{info}}</h4>
</template>
<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const num = ref(1);
const size = ref(3);
const books = ref("");
const info=ref("")
// /book/category/3/page?pageNum=1&pageSize=3
//当第一次进入组件时，请求查询数据
onMounted(() => {
  console.log(route);
  // let url = "/book"+route.fullPath+"/page?";
   let url = "/book"+route.fullPath+"/page";
  getData(url)
});
const getData=function(url){
//   let url = route.fullPath;
  axios
    // .get(url + "pageNum=" + num.value + "&pageSize=" + size.value)
     .get(url,{params:{pageNum:num.value,pageSize:size.value}})
    .then((res) => {
      console.log("search:", res);
      books.value = res.data.data;
      if(books.value.length==0){
          info.value="没有相应分类图书"
      }else{
         info.value="" 
      }
    })
    .catch((err) => {
      console.log(err);
    });
}
</script>
<style>
.search-value {
  display: flex;
  border-bottom: 1px solid rgb(230, 226, 226);
}
.search-value-left {
  flex: 1;
}
.search-value-right {
  flex: 4;
}
a{
    text-decoration: none;
}
</style>