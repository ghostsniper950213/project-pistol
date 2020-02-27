<template>
  <div id="gallery">
    <loading :show="isLoading" />

    <modal :show="showPageInputModal" @close="showPageInputModal = false">
      <div class="page-input-modal-title">
        <span>请输入跳转页码{{ totalPages > 0 ? '（1~' + totalPages + '）': '' }}：</span>
      </div>
      <div class="page-input-modal-input">
        <input type="number" v-model="jumpPage" />
      </div>
      <div class="page-input-modal-btns">
        <btn class="page-input-cancel-btn" @click="showPageInputModal = !showPageInputModal">
          <icon icon="times" />&nbsp;取消
        </btn>
        <btn class="page-input-confirm-btn" @click="handlePageJump">
          确认&nbsp;
          <icon icon="check" />
        </btn>
      </div>
    </modal>

    <scroller class="galleries" ref="galleries" @scroll="handleGalleryScroll">
      <router-link
        tag="div"
        class="gallery"
        v-for="gallery of galleries"
        :key="gallery.detailUrl"
        :to="{name: 'detail', query: {detailUrl: gallery.detailUrl}}"
      >
        <div class="gallery-thumb">
          <img v-lazy="imageUrl(gallery.coverUrl)" @error="handleImgError" />
        </div>
        <div class="gallery-info">
          <div class="gallery-title">
            <span>{{ gallery.title }}</span>
          </div>
          <div class="gallery-info-row">
            <div class="gallery-info-item gallery-category">
              <icon icon="tags" />&nbsp;
              <span>{{ gallery.category }}</span>
            </div>
            <div class="gallery-info-item gallery-rating">
              <icon icon="star" />&nbsp;
              <span>{{ gallery.rating }}</span>
            </div>
            <div class="gallery-info-item gallery-pages">
              <icon :icon="gallery.download ? 'download' :'images'" />&nbsp;
              <span>{{ gallery.pages }}</span>
            </div>
          </div>
          <div class="gallery-info-row">
            <div class="gallery-info-item gallery-uploader">
              <icon icon="upload" />&nbsp;
              <span>{{ gallery.uploader }}</span>
            </div>
          </div>
          <div class="gallery-info-row">
            <div class="gallery-info-item gallery-time">
              <icon icon="clock" />&nbsp;
              <span>{{ gallery.time }}</span>
            </div>
          </div>
        </div>
      </router-link>
    </scroller>

    <div class="tool-bar" :class="{'open': isToolBarOpen}">
      <div class="controls">
        <div class="search-val-part">
          <router-link :to="{name: 'search', query: searchParams}" tag="div" class="search-val">
            <icon icon="search" v-if="!searchParams.f_search" />
            <span>{{ searchParams.f_search }}</span>
          </router-link>
        </div>
        <div class="page-info" @click="showPageInputModal = totalPages > 0 ? true: false">
          <div class="page-number">
            <span>{{ parseInt(searchParams.page) + 1 }}</span>
          </div>
          <div class="total-pages">
            <span>{{ totalPages > 0 ? totalPages : '-' }}</span>
          </div>
        </div>
        <div class="function-btn">
          <button @click="isToolBarOpen = !isToolBarOpen">
            <icon icon="angle-up" :class="{'fa-flip-vertical': isToolBarOpen}" />
          </button>
        </div>
      </div>

      <div class="functions">
        <router-link tag="div" class="function" :to="{name: 'user'}">
          <icon icon="user" />
        </router-link>
        <router-link tag="div" class="function" :to="{name: 'download'}">
          <icon icon="download" />
        </router-link>
        <router-link tag="div" class="function" :to="{name: 'blockTag'}">
          <icon icon="eye-slash" />
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import Modal from '@/components/Modal'
import Scroller from '@/components/Scroller'

import { axios, urls, requestImage } from '@/axios'
import defaults from '@/data/defaults'

export default {
  name: 'Gallery',
  components: {
    Icon,
    Btn,
    Loading,
    Modal,
    Scroller
  },
  mounted() {
    let searchParams = { ...defaults.searchParams }
    for (let key of Object.keys(searchParams)) {
      if (this.$route.query[key] != undefined) {
        searchParams[key] = this.$route.query[key]
      }
    }
    this.searchParams = searchParams
    this.fetchPage()
  },
  data() {
    return {
      searchParams: '',
      totalPages: 0,
      jumpPage: 1,

      galleries: [],

      isToolBarOpen: false,
      isLoading: false,
      showPageInputModal: false
    }
  },
  methods: {
    fetchPage() {
      this.isLoading = true
      axios
        .get(urls.gallery.page, { params: { searchParams: this.searchParams } })
        .then(response => {
          let pageData = response.data.data
          this.totalPages = pageData.totalPages

          if (pageData.totalPages === 0) {
            this.galleries = []
            this.isLoading = false
            return
          }

          this.galleries = this.galleries.concat(
            pageData.elements.filter(
              element =>
                this.galleries.filter(
                  gallery => gallery.detailUrl === element.detailUrl
                ).length === 0
            )
          )
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleGalleryScroll(e) {
      this.isToolBarOpen = false
      if (this.isLoading || this.searchParams.page >= this.totalPages - 1) {
        return
      }
      let scrollTop = e.target.scrollTop
      let scrollHeight = e.target.scrollHeight
      let clientHeight = e.target.clientHeight
      if (scrollTop + clientHeight >= scrollHeight) {
        this.searchParams.page++
        this.fetchPage()
      }
    },
    handlePageJump() {
      if (this.jumpPage < 1 || this.jumpPage > this.totalPages) {
        alert('页码超过限制（1~' + this.totalPages + ')')
        this.jumpPage = 1
        return
      }

      this.searchParams.page = this.jumpPage - 1
      this.showPageInputModal = false
      this.jumpPage = 1
      this.galleries = []
      this.fetchPage()
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
    errorImg() {
      return require('@/assets/error.png')
    }
  }
}
</script>

<style scoped>
.galleries {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 40px;
}

.gallery {
  display: block;
  height: 160px;
  padding: 10px;
  border-top: 0.5px solid #ccc;
  overflow: hidden;
}

.gallery:first-of-type {
  border-top: none;
}

.gallery-thumb {
  display: inline-block;
  width: 100px;
  height: 100%;
  position: relative;
  vertical-align: middle;
  align-items: center;
}

.gallery-thumb img {
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

.gallery-info {
  display: inline-block;
  margin-left: 10px;
  width: calc(100% - 110px);
  height: 100%;
  vertical-align: middle;
}

.gallery-title {
  height: 60px;
  word-wrap: break-word;
  font-weight: bold;
  line-height: 20px;
  margin-bottom: 20px;
  overflow: hidden;
}

.gallery-title span {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.gallery-info-row {
  height: 20px;
  line-height: 20px;
}

.gallery-info-item {
  display: inline-block;
  margin-right: 10px;
}

.gallery-info-item:last-of-type {
  margin-right: 0;
}

.gallery-info-item {
  color: #567;
}

.tool-bar {
  position: absolute;
  left: 0;
  right: 0;
  top: calc(100% - 40px);
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  transition: top 0.2s;
  background-color: #fff;
}

.tool-bar.open {
  top: calc(100% - 105px);
}

.controls {
  height: 40px;
  display: flex;
  align-items: center;
}

.function-btn {
  width: 50px;
}

.function-btn button {
  height: 50px;
  width: 50px;
  background: none;
  line-height: 50px;
  font-size: 24px;
}

.function-btn .icon {
  transition: transform 0.2s;
}

.search-val-part {
  flex: 1;
  overflow: hidden;
}

.search-val {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  line-height: 30px;
  margin: 5px 10px;
  background-color: #eee;
  padding: 0 10px;
  border-radius: 5px;
  text-align: center;
}

.search-val-part .icon {
  color: #aaa;
}

.page-info {
  width: 50px;
  text-align: center;
}

.page-info * {
  font-size: 12px;
}

.page-number {
  line-height: 15px;
  border-bottom: 1px solid #ccc;
}

.total-pages {
  line-height: 15px;
}

.functions {
  height: 55px;
  padding: 5px;
  padding-top: 0;
  margin-bottom: 10px;
}

.function {
  display: inline-block;
  width: 50px;
  height: 50px;
  margin: 5px;
  background-color: #eee;
  font-size: 24px;
  line-height: 50px;
  text-align: center;
  border-radius: 5px;
}

.page-input-modal-title {
  line-height: 40px;
}

.page-input-modal-input {
  margin-top: 10px;
}

.page-input-modal-input input {
  width: 100%;
  height: 30px;
  border-radius: 5px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
  opacity: 1;
  padding: 0 10px;
}

.page-input-modal-btns {
  line-height: 40px;
  margin-top: 10px;
  text-align: right;
}

.page-input-cancel-btn {
  color: #aaa;
}

.page-input-confirm-btn {
  color: #2a2;
}
</style>