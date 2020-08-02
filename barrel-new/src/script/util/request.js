import axios from 'axios'
import project from '@/script/conf/project'

const request = axios.create({
  baseURL: project.baseRequestUrl,
  timeout: 10000
})

const source = axios.CancelToken.source();

request.interceptors.request.use(
  config => {
    if (config.data) {
      let formData = new FormData()
      for (let key of Object.keys(config.data)) {
        if (config.data[key] === undefined) {
          continue
        }
        if (config.data[key] === null) {
          formData.append(key, '')
          continue
        }
        if (config.data[key] instanceof Object) {
          formData.append(key, JSON.stringify(config.data[key]))
          continue
        }
        formData.append(key, config.data[key])
      }
      config.data = formData
    }

    return config
  },
  error => {
    alert('网络连接异常')
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    let result = response.data

    if (result.code === 200) {
      return result.data
    }

    alert(result.data)
    return Promise.reject(new Error(result.message || 'Error'))
  },
  error => {
    source.cancel()
    alert('网络连接异常')
    return Promise.reject(error)
  }
)

export default request 
