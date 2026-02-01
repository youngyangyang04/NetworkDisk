import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import panUtil from '@/utils/common.js'

export const useTaskStore = defineStore('task', () => {
    const taskList = ref([])
    const viewFlag = ref(false)

    const uploadTaskNum = computed(() => taskList.value.length)
    const uploadTaskFlag = computed(() => taskList.value.length <= 0)

    function clear() {
        taskList.value = new Array()
        viewFlag.value = false
    }

    function add(taskItem) {
        taskList.value.push(taskItem)
    }

    function remove(filename) {
        for (let i = 0; i < taskList.value.length; i++) {
            if (filename === taskList.value[i].filename) {
                taskList.value.splice(i, 1)
                break
            }
        }
    }

    function updateStatus(param) {
        let taskItem = taskList.value.find(taskItem => param.filename === taskItem.filename)
        if (taskItem) {
            taskItem.status = param.status
            taskItem.statusText = param.statusText
        }
    }

    function updateProcess(param) {
        let taskItem = taskList.value.find(taskItem => param.filename === taskItem.filename)
        if (taskItem) {
            taskItem.speed = param.speed
            taskItem.percentage = param.percentage
            taskItem.uploadedSize = param.uploadedSize
            taskItem.timeRemaining = param.timeRemaining
        }
    }

    function pause(filename) {
        let taskItem = taskList.value.find(taskItem => filename === taskItem.filename)
        if (taskItem && taskItem.target) {
            taskItem.target.pause()
            taskItem.status = panUtil.fileStatus.PAUSE.code
            taskItem.statusText = panUtil.fileStatus.PAUSE.text
        }
    }

    function resume(filename) {
        let taskItem = taskList.value.find(taskItem => filename === taskItem.filename)
        if (taskItem && taskItem.target) {
            taskItem.target.resume()
        }
    }

    function cancel(filename) {
        for (let i = 0; i < taskList.value.length; i++) {
            if (filename === taskList.value[i].filename) {
                if (taskList.value[i].target) {
                    taskList.value[i].target.cancel()
                }
                taskList.value.splice(i, 1)
                if (taskList.value.length === 0) {
                    viewFlag.value = false
                }
                break
            }
        }
    }

    function retry(filename) {
        let taskItem = taskList.value.find(taskItem => filename === taskItem.filename)
        if (taskItem && taskItem.target) {
            taskItem.target.bootstrap()
            taskItem.target.resume()
        }
    }

    function updateViewFlag(newViewFlag) {
        viewFlag.value = newViewFlag
    }

    function getUploadTask(filename) {
        return taskList.value.find(taskItem => filename === taskItem.filename)
    }

    return {
        taskList,
        viewFlag,
        uploadTaskNum,
        uploadTaskFlag,
        clear,
        add,
        remove,
        updateStatus,
        updateProcess,
        pause,
        resume,
        cancel,
        retry,
        updateViewFlag,
        getUploadTask
    }
})
