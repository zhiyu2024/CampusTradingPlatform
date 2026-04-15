<template>
    <div>query参数：用户名：{{$route.query.name}}
        密码：{{$route.query.pwd}}
    </div>
    <div>params参数：用户名：{{$route.params.name}}
                    密码：{{$route.params.pwd}}
    </div>
</template>
<script>
export default {
    created(){
        console.log(this.$route)
        console.log(this.$router)
    }
}
</script>
