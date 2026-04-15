import { defineStore } from "pinia";

const userUserStore=defineStore("user",{
    
    state:()=>{
        return{
         username:""
        }
    },
    actions:{
        
           saveUser(name){
               this.username=name
           },
           delUser(){
               this.username=""
           }
       
        
    },
    getters:{
      
    }
})
export default userUserStore