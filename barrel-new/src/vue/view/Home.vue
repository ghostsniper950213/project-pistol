<template>
  <div id="home">
    <list-container @scroll="handleListScroll">
      <list-item v-for="gallery of galleries" :key="gallery.url">
        <div class="cover-part">
          <img v-lazy="getCoverImage(gallery.url, gallery.coverUrl)" />
        </div>
        <div class="info-part">
          <div class="title">{{ gallery.title }}</div>
          <div class="info">
            <div class="info-row">
              <div class="info-group">
                <image-icon icon="category" size="14" />
                <span>{{ gallery.category }}</span>
              </div>
              <div class="info-group">
                <image-icon icon="pages" />
                <span>{{ gallery.pages }}</span>
              </div>
              <div class="info-group">
                <image-icon icon="star" />
                <span>{{ gallery.rating }}</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-group">
                <image-icon icon="uploader" size="14" />
                <span>{{ gallery.uploader }}</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-group">
                <image-icon icon="time" size="15"/>
                <span>{{ gallery.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </list-item>

      <div id="gallery-list-bottom" @click="fetchingPage || handleNextPage()">
        <image-icon
          v-if="fetchingPage || searchParams.page < totalPages - 1"
          :icon="fetchingPage ? 'loading' : 'reload'"
          :rotate="fetchingPage"
        />
        <span v-else>下面没有了</span>
      </div>
    </list-container>

    <tool-bar>
      <tool-bar-item width="60" @click.native="showPageNumberDialog = totalPages > 1">
        <page-number :pageNumber="searchParams.page + 1" :totalPages="totalPages" />
      </tool-bar-item>
      <tool-bar-item>2</tool-bar-item>
      <tool-bar-item width="60" @click.native="showFunctions = !showFunctions">
        <image-icon :icon="showFunctions ? 'functionsActive' : 'functions'" />
      </tool-bar-item>
    </tool-bar>

    <text-dialog :show="showPageNumberDialog">
      <text-dialog-row>跳转页码（1 ~ {{ totalPages }}）：</text-dialog-row>
      <text-dialog-row>
        <text-input v-model="jumpPage" />
      </text-dialog-row>
      <text-dialog-row>
        <text-button @click="showPageNumberDialog = false">取消</text-button>
        <text-button
          type="blue"
          id="page-number-jump-button"
          @click="handleJumpPage(); showPageNumberDialog = false"
        >跳转</text-button>
      </text-dialog-row>
    </text-dialog>
  </div>
</template>

<script>
import project from '@/script/conf/project'
import defaults from '@/script/data/defaults'
import request from '@/script/util/request'

export default {
  name: 'Home',
  mounted() {
    if (this.$route.query.searchParams) {
      this.searchParams = JSON.parse(this.$route.query.searchParams)
    }

    this.fetchPage()
  },
  data() {
    return {
      searchParams: defaults.searchParams,

      galleries: [],
      totalPages: 0,
      jumpPage: 1,

      showPageNumberDialog: false,

      showFunctions: false,

      fetchingPage: false,
    }
  },
  methods: {
    fetchPage() {
      this.fetchingPage = true
      request
        .get('/api/gallery/page', {
          params: this.searchParams,
        })
        .then((data) => {
          this.galleries = this.galleries.concat(data.elements)
          this.totalPages = data.totalPages
          this.fetchingPage = false
        })
        .catch((err) => {
          this.fetchingPage = false
        })
    },
    handleListScroll(e) {
      let scrollTop = e.target.scrollTop
      let scrollHeight = e.target.scrollHeight
      let clientHeight = e.target.clientHeight

      if (scrollHeight - scrollTop - clientHeight <= clientHeight) {
        this.handleNextPage()
      }
    },
    handleNextPage() {
      if (this.searchParams.page >= this.totalPages - 1) {
        return
      }

      this.searchParams.page++
      this.fetchPage()
    },
    handleJumpPage() {
      this.galleries = []
      this.searchParams.page = this.jumpPage - 1
      this.fetchPage()
    },
    getCoverImage(galleryUrl, coverUrl) {
      return `${project.baseRequestUrl}/api/image/cover?galleryUrl=${galleryUrl}&coverUrl=${coverUrl}`
    },
  },
}
</script>

<style scoped>
.list-container {
  height: 100%;
}

.list-item {
  height: 160px;
  padding: 10px;
}

.cover-part {
  display: flex;
  width: 100px;
  height: 100%;
  align-items: center;
  justify-content: center;
}

.cover-part img {
  max-width: 100%;
  max-height: 100%;
}

.info-part {
  flex: 1;
  height: 100%;
  margin-left: 10px;
}

.title {
  height: 60px;
  line-height: 20px;
  display: -webkit-box;
  word-wrap: break-word;
  word-break: break-all;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.info {
  margin-top: 5px;
  height: 80px;
}

.info-row {
  margin-top: 5px;
}

.info-group {
  vertical-align: middle;
  display: inline-flex;
  align-items: center;
  background-color: #eee;
  height: 20px;
  padding: 0 10px 0 7.5px;
  margin-left: 5px;
  border-radius: 10px;
}

.info-group * {
  font-size: 14px;
}

.info-group:first-of-type {
  margin-left: 0;
}

.info-group .image-icon {
  margin-right: 5px;
}

.tool-bar-item:active {
  background-color: #ddd;
}

#gallery-list-bottom {
  height: 50px;
  line-height: 50px;
  margin-bottom: 50px;
  text-align: center;
}

#page-number-jump-button {
  margin-left: 10px;
}
</style>