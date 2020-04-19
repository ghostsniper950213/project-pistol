<template>
  <div id="gallery">
    <loading :show="isLoading" />

    <scroller
      class="galleries"
      ref="galleries"
      @scroll="handleGalleryScroll"
      @touchstart.native="handleGalleryTouchStart"
    >
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

    <modal :show="showPageInputModal" @close="showPageInputModal = false">
      <div class="page-input-modal-title">
        <span>跳转页码{{ totalPages > 0 ? '（1 ~ ' + totalPages + '）': '' }}：</span>
      </div>
      <div class="page-input-modal-input">
        <input type="number" v-model="jumpPage" @keyup.enter="handlePageJump"/>
      </div>
      <div class="page-input-modal-btns">
        <btn class="page-input-cancel-btn" @click="showPageInputModal = false">取消</btn>
        <btn class="page-input-confirm-btn" type="blue" @click="handlePageJump">确认</btn>
      </div>
    </modal>

    <transition name="functions-trans">
      <div class="functions" v-show="isFunctionsShow">
        <router-link tag="div" class="function" :to="{name: 'setting'}">
          <icon icon="cog" />
        </router-link>
        <router-link tag="div" class="function" :to="{name: 'user'}">
          <icon icon="user" />
        </router-link>
        <router-link tag="div" class="function" :to="{name: 'blockTag'}">
          <icon icon="eye-slash" />
        </router-link>
        <router-link tag="div" class="function" :to="{name: 'download'}">
          <icon icon="download" />
        </router-link>
      </div>
    </transition>

    <bottom-bar class="status-bar">
      <div class="page-info" @click="handlePageInfoClick">
        <div class="page-number">
          <span>{{ parseInt(searchParams.page) + 1 }}</span>
        </div>
        <div class="total-pages">
          <span>{{ totalPages > 0 ? totalPages : '-' }}</span>
        </div>
      </div>
      <router-link :to="{name: 'search', query: searchParams}" tag="div" class="search-val">
        <icon icon="search" v-if="!searchParams.f_search" />
        <span>{{ searchParams.f_search }}</span>
      </router-link>
      <div class="function-btn">
        <button @click="isFunctionsShow = !isFunctionsShow">
          <icon icon="angle-up" :class="{'open': isFunctionsShow}" />
        </button>
      </div>
    </bottom-bar>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import Modal from '@/components/Modal'
import BottomBar from '@/components/BottomBar'
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
    BottomBar,
    Scroller
  },
  mounted() {
    let searchParams = { ...defaults.searchParams }
    for (let key of Object.keys(searchParams)) {
      if (this.$route.query[key] !== undefined) {
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

      isFunctionsShow: false,
      isLoading: false,
      showPageInputModal: false,
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
    handleGalleryTouchStart() {
      this.isFunctionsShow = false
    },
    handlePageInfoClick() {
      this.showPageInputModal = this.totalPages > 0
      this.jumpPage = this.searchParams ? parseInt(this.searchParams.page) + 1 : 1
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
    },
  }
}
</script>

<style scoped>
.galleries {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 40px;
  top: 0;
}

.gallery {
  display: block;
  padding: 10px;
  height: 160px;
  overflow: hidden;
  background-color: #fff;
  border-top: 0.5px solid #bbb;
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
  width: calc(100% - 110px);
  margin-left: 10px;
  height: 100%;
  vertical-align: middle;
}

.gallery-title {
  height: 60px;
  word-wrap: break-word;
  font-weight: bold;
  line-height: 20px;
  margin-bottom: 12.5px;
  overflow: hidden;
}

.gallery-title span {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.gallery-info-row {
  height: 22.5px;
  line-height: 22.5px;
}

.gallery-info-item {
  display: inline-block;
  margin-right: 10px;
}

.gallery-info-item:last-of-type {
  margin-right: 0;
}

.functions {
  position: absolute;
  right: 10px;
  bottom: 50px;
  padding: 5px;
  transition: transform 0.2s ease;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
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
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.function:active {
  background-color: #ddd;
  box-shadow: none;
}

.functions-trans-enter,
.functions-trans-leave-to {
  transform: translateY(80px);
}

.status-bar {
  align-items: center;
  display: flex;
}

.function-btn {
  width: 60px;
}

.function-btn button {
  height: 40px;
  width: 100%;
  background: none;
  line-height: 40px;
  font-size: 24px;
}

.function-btn button:active {
  background-color: #ddd;
}

.function-btn .icon {
  transition: transform 0.2s ease;
}

.function-btn .icon.open {
  transform: rotateX(180deg);
}

.search-val {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  line-height: 40px;
  padding: 0 10px;
  text-align: center;
}

.search-val:active {
  background-color: #ddd;
}

.page-info {
  width: 60px;
  height: 40px;
  padding: 5px 10px;
  text-align: center;
}

.page-info:active {
  background-color: #ddd;
}

.page-info * {
  font-size: 12px;
}

.page-number {
  line-height: 15px;
  border-bottom: 1px solid #234;
  white-space: nowrap;
}

.total-pages {
  white-space: nowrap;
  line-height: 15px;
}

.page-input-modal-title {
  line-height: 40px;
}

.page-input-modal-input input {
  width: 100%;
  height: 40px;
  border-radius: 5px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
  opacity: 1;
  padding: 0 10px;
}

.page-input-modal-btns {
  line-height: 40px;
  margin-top: 10px;
  display: flex;
}

.page-input-modal-btns button {
  flex: 1;
  margin-left: 10px;
}

.page-input-modal-btns button:first-of-type {
  margin-left: 0;
}
</style>