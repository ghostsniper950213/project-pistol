<template>
  <div id="download">
    <loading v-show="isLoading" />

    <scroller class="downloads" ref="downloads" @scroll="handleScroll">
      <router-link
        tag="div"
        class="download"
        v-for="download of downloads"
        :key="download.detail.detailUrl"
        :to="checkMode ? '' : {name: 'detail', query: {detailUrl: download.detail.detailUrl}}"
        @click.native="checkMode && handleCheck(download.detail.detailUrl)"
        :class="{'checked': checkMode && checked.indexOf(download.detail.detailUrl) >= 0}"
      >
        <div class="download-thumb">
          <img
            :src="download.cover ? localImageUrl(download.cover) : imageUrl(download.detail.coverUrl)"
            @error="handleImgError"
          />
        </div>
        <div class="download-info">
          <div class="download-title">
            <span>{{ download.detail.title }}</span>
          </div>
          <div class="download-info-row">
            <div class="download-info-item download-category">
              <icon icon="tags" />&nbsp;
              <span>{{ download.detail.category }}</span>
            </div>
            <div class="download-info-item download-rating">
              <icon icon="star" />&nbsp;
              <span>{{ download.detail.rating }}</span>
            </div>
          </div>
          <div class="download-info-row">
            <div class="download-info-item download-pages">
              <icon :icon="downloadStatusIcon(download.status)" />&nbsp;
              <span>{{ downloadedAmount(download.images) }}/{{ download.detail.pages }}</span>
            </div>
          </div>
          <div class="download-info-row">
            <div class="download-info-item download-uploader">
              <icon icon="upload" />&nbsp;
              <span>{{ download.detail.uploader }}</span>
            </div>
          </div>
          <div class="download-info-row">
            <div class="download-info-item download-time">
              <icon icon="clock" />&nbsp;
              <span>{{ download.detail.time }}</span>
            </div>
          </div>
        </div>
      </router-link>
    </scroller>

    <bottom-bar>
      <button class="redownload-btn" v-show="checkMode" @click="fetchRedownload">
        <icon icon="redo" />
      </button>
      <button class="remove-btn" v-show="checkMode" @click="fetchRemove">
        <icon icon="trash-alt" />
      </button>
      <button class="check-btn" @click="checkMode = !checkMode">
        <icon icon="tasks" />
      </button>
      <button class="pause-btn" v-show="checkMode" @click="fetchPause">
        <icon icon="pause" />
      </button>
      <button class="resume-btn" v-show="checkMode" @click="fetchResume">
        <icon icon="play" />
      </button>
    </bottom-bar>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import BottomBar from '@/components/BottomBar'
import Scroller from '@/components/Scroller'

import { axios, urls, requestImage, requestLocalImage } from '@/axios'

export default {
  name: 'Download',
  components: {
    Icon,
    Btn,
    Loading,
    BottomBar,
    Scroller
  },
  mounted() {
    this.fetchGalleries()
  },
  data() {
    return {
      downloads: [],
      checked: [],

      checkMode: false,
      isLoading: false,

      donwloadWs: ''
    }
  },
  methods: {
    fetchGalleries() {
      this.isLoading = true
      let ws = new WebSocket(urls.download.downloadWs)
      ws.onopen = () => {
        this.donwloadWs = ws
      }
      ws.onmessage = message => {
        this.downloads = JSON.parse(message.data)
        this.isLoading = false
      }
    },
    fetchRemove() {
      if (this.checked.length <= 0) {
        this.checkMode = false
        return
      }
      this.isLoading = true
      let detailUrls = this.checked.reduce((p, n) => p + ',' + n)
      axios
        .post(urls.download.remove, null, {
          params: { detailUrls: detailUrls }
        })
        .then(response => {
          this.fetchGalleries()
          this.checked = []
          this.checkMode = false
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchPause() {
      if (this.checked.length <= 0) {
        this.checkMode = false
        return
      }
      this.isLoading = true
      let detailUrls = this.checked.reduce((p, n) => p + ',' + n)
      axios
        .post(urls.download.pause, null, { params: { detailUrls: detailUrls } })
        .then(response => {
          this.fetchGalleries()
          this.checked = []
          this.checkMode = false
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchResume() {
      if (this.checked.length <= 0) {
        this.checkMode = false
        return
      }
      this.isLoading = true
      let detailUrls = this.checked.reduce((p, n) => p + ',' + n)
      axios
        .post(urls.download.resume, null, {
          params: { detailUrls: detailUrls }
        })
        .then(response => {
          this.fetchGalleries()
          this.checked = []
          this.checkMode = false
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchRedownload() {
      if (this.checked.length <= 0) {
        this.checkMode = false
        return
      }
      this.isLoading = true
      let detailUrls = this.checked.reduce((p, n) => p + ',' + n)
      axios
        .post(urls.download.redownload, null, {
          params: { detailUrls: detailUrls }
        })
        .then(response => {
          this.fetchGalleries()
          this.checked = []
          this.checkMode = false
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleCheck(detailUrl) {
      if (this.checked.indexOf(detailUrl) >= 0) {
        this.checked.splice(this.checked.indexOf(detailUrl), 1)
      } else {
        this.checked.push(detailUrl)
      }
    },
    handleImgError(e) {
      let img = e.srcElement
      img.src = this.errorImg
      img.onerror = null
    }
  },
  computed: {
    imageUrl() {
      return url => requestImage(url)
    },
    localImageUrl() {
      return path => requestLocalImage(path)
    },
    downloadStatusIcon() {
      return status => {
        switch (status) {
          case 'FINISHED':
            return 'images'
          case 'FAILED':
            return 'times'
          case 'PAUSED':
            return 'pause'
          default:
            return 'download'
        }
      }
    },
    downloadedAmount() {
      return imgs => imgs.filter(img => img).length
    },
    errorImg() {
      return require('@/assets/error.png')
    }
  }
}
</script>

<style scoped>
.downloads {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 40px;
  top: 0;
}

.download {
  display: block;
  height: 160px;
  padding: 10px;
  border-top: 0.5px solid #bbb;
  background-color: #fff;
}

.download.checked {
  background-color: #eee;
}

.download:first-of-type {
  border-top: none;
}

.download-thumb {
  display: inline-block;
  width: 100px;
  height: 100%;
  position: relative;
  vertical-align: middle;
  align-items: center;
}

.download-thumb img {
  max-width: 100%;
  max-height: 100%;
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  border-radius: 5px;
}

.download-info {
  display: inline-block;
  margin-left: 10px;
  width: calc(100% - 110px);
  height: 100%;
  vertical-align: middle;
}

.download-title {
  height: 40px;
  overflow: auto;
  word-wrap: break-word;
  font-weight: bold;
  line-height: 20px;
  margin-bottom: 10px;
}

.download-title span {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.download-info-row {
  height: 22.5px;
  line-height: 22.5px;
}

.download-info-item {
  display: inline-block;
  margin-right: 10px;
}

.download-info-item:last-of-type {
  margin-right: 0;
}
</style>