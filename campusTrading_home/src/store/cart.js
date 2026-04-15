import { defineStore } from "pinia";

const userShopStore=defineStore("cart",{
    persist:true,
    state:()=>{
        return{
            items:[]
        }
    },
    actions:{
        pushToCart({id,title,price,count,imgUrl}){
            let item=this.items.find(item=>item.id==id)
            if(item){
               item.count+=count
            }else{
                this.items.push({id,title,price,count,imgUrl})
            }
           
        },
        add(id){
            this.items.forEach(item=>{
                if(item.id==id){
                    item.count++
                }
            })
        },
        sub(id){
            this.items.forEach(item=>{
                if(item.id==id){
                    item.count--
                }
            })
        },
        delOne(index){
            this.items.splice(index,1)

        }

    },
    getters:{
      totalMoney(state){
          let total=0
          state.items.forEach(item=>{
              total+=item.price*item.count
          })
          return total
      }
    }
})
export default userShopStore