import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNavbarStore = defineStore('navbar', () => {
  const active = ref('Files')

  const change = (name) => {
    active.value = name
  }

  return {
    active,
    change
  }
})

