import axios from 'axios'

const baseRestUrl = 'http://192.168.123.192:8848/api/'
const baseWsUrl = 'ws://192.168.123.192:8848/ws'
// const baseRestUrl = 'http://' + window.location.host + '/api/'
// const baseWsUrl = 'ws://' + window.location.host + '/ws'

const urls = {
  user: {
    login: baseRestUrl + '/user/login',
    logout: baseRestUrl + '/user/logout',
    cookie: baseRestUrl + '/user/cookie',
  },
  gallery: {
    page: baseRestUrl + '/gallery/page',
    detail: baseRestUrl + '/gallery/detail',
    thumbs: baseRestUrl + '/gallery/thumbs',
    imagePages: baseRestUrl + '/gallery/image/page',
    viewImage: baseRestUrl + '/gallery/image/view',
  },
  download: {
    downloadWs: baseWsUrl + '/download',
    download: baseRestUrl + '/download',
    remove: baseRestUrl + '/download/remove',
    resume: baseRestUrl + '/download/resume',
    pause: baseRestUrl + '/download/pause',
    update: baseRestUrl + '/download/update',
  },
  tag: {
    blockedTags: baseRestUrl + '/tag/blocked',
    blockTag: baseRestUrl + '/tag/block',
    unblockTag: baseRestUrl + '/tag/unblock',
  },
  image: {
    image: baseRestUrl + '/image',
    localImage: baseRestUrl + '/image/local',
  }
}

const requestImage = url => urls.image.image + '?url=' + (url)
const requestViewImage = url => urls.gallery.viewImage + '?url=' + encodeURI(url)
const requestLocalImage = path => urls.image.localImage + '?path=' + encodeURI(path)

//请求拦截器
axios.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

//响应拦截器
axios.interceptors.response.use(
  response => {
    if (response.status === 200 && response.data.code === 200) {
      return response
    } else {
      alert(response.data.data)
      return
    }
  },
  error => {
    alert("后端项目异常")
    // 对响应错误做点什么
    return Promise.reject(error)
  }
)

export { axios, urls, requestImage, requestViewImage, requestLocalImage }