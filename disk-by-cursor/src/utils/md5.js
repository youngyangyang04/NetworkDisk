/**
 * MD5计算工具
 */

import SparkMD5 from 'spark-md5'

export function MD5(file, callback) {
    const chunkSize = 2097152 // 2MB
    const chunks = Math.ceil(file.size / chunkSize)
    const spark = new SparkMD5.ArrayBuffer()
    const fileReader = new FileReader()
    let currentChunk = 0

    fileReader.onload = function (e) {
        spark.append(e.target.result)
        currentChunk++

        if (currentChunk < chunks) {
            loadNext()
        } else {
            callback(null, spark.end())
        }
    }

    fileReader.onerror = function (e) {
        callback(e)
    }

    function loadNext() {
        const start = currentChunk * chunkSize
        const end = Math.min(start + chunkSize, file.size)
        fileReader.readAsArrayBuffer(file.slice(start, end))
    }

    loadNext()
}
