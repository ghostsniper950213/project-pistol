<template>
  <div id="detail">
    <loading v-show="isLoading" />

    <top-bar title="详情" />

    <bottom-bar>
      <div class="bottom-bar-right">
        <button class="download-btn" v-if="!detail.download" @click="handleDownload">下载</button>
        <button class="read-btn" @click="handleRead">阅读</button>
      </div>
    </bottom-bar>

    <modal :show="clickedTag" @close="clickedTag = ''">
      <div class="tag-block-modal-title">{{ clickedTag }}</div>
        <div class="tag-block-modal-btns">
          <btn class="tag-block-cancel-btn" @click="clickedTag = ''">
            <icon icon="times" />&nbsp;取消
          </btn>
          <btn class="tag-block-block-btn" @click="handleBlockTag">
            <icon icon="eye-slash" />&nbsp;屏蔽
          </btn>
          <btn class="tag-block-search-btn" @click="handleSearchTag">
            <icon icon="search" />&nbsp;搜索
          </btn>
        </div>
    </modal>

    <scroller class="detail" v-if="detail">
      <div class="thumbs-row" @scroll="handleThumbsScroll">
        <img v-for="thumb of thumbs" :key="thumb" v-lazy="imageUrl(thumb)" @error="handleImgError" />
      </div>

      <div class="title">
        <span>{{ detail.title }}</span>
      </div>

      <div class="detail-row">
        <div class="detail-item category">
          <icon icon="tags" />&nbsp;
          <span>{{ detail.category }}</span>
        </div>
        <div class="detail-item language">
          <icon icon="atlas" />&nbsp;
          <span>{{ detail.language }}</span>
        </div>
      </div>

      <div class="detail-row">
        <div class="detail-item rating">
          <icon icon="star" />&nbsp;
          <span>{{ detail.rating }}</span>
        </div>
        <div class="detail-item favorites">
          <icon icon="heart" />&nbsp;
          <span>{{ detail.favorites }}</span>
        </div>
        <div class="detail-item pages">
          <icon icon="images" />&nbsp;
          <span>{{ detail.pages }}</span>
        </div>
      </div>

      <div class="detail-row">
        <div class="detail-item uploader" @click="handleSearchUploader(detail.uploader)">
          <icon icon="upload" />&nbsp;
          <span>{{ detail.uploader }}</span>
        </div>
        <div class="detail-item time">
          <icon icon="clock" />&nbsp;
          <span>{{ detail.time }}</span>
        </div>
      </div>

      <div class="tags-row">
        <div class="tag-row" v-for="tag of detail.tags" :key="tag.name">
          <div class="tag-name">{{ tagNameDisplay(tag.name) }}</div>
          <div class="tag-vals">
            <div
              class="tag-val"
              v-for="val of tag.vals"
              :key="val"
              @click="clickedTag = tag.name + ':' +  val"
            >{{ tagValDisplay(tag.name, val) }}</div>
          </div>
        </div>
      </div>

      <div class="comments-row">
        <div class="comment" v-for="(comment, index) of detail.comments" :key="index">
          <div class="comment-infos">
            <div class="comment-info comment-author">
              <icon :icon="comment.isUploader ? 'upload' : 'user'" />&nbsp;
              <span>{{ comment.author }}</span>
            </div>
            <br />
            <div class="comment-info comment-postTime">
              <icon icon="clock" />&nbsp;
              <span>{{ comment.postTime }}</span>
            </div>
            <div class="comment-info comment-vote" v-if="comment.vote">
              <span>{{ comment.vote }}</span>
            </div>
          </div>
          <div class="comment-content">
            <span v-html="comment.content"></span>
          </div>
        </div>
      </div>
    </scroller>

  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import TopBar from '@/components/TopBar'
import BottomBar from '@/components/BottomBar'
import Modal from '@/components/Modal'
import Scroller from '@/components/Scroller'

import { axios, urls, requestImage } from '@/axios'
import defaults from '@/data/defaults'
import tagData from '@/data/db.text.json'

export default {
  name: 'Detail',
  components: {
    Icon,
    Btn,
    Loading,
    TopBar,
    BottomBar,
    Modal,
    Scroller
  },
  mounted() {
    this.detailUrl = this.$route.query.detailUrl
    if (!this.detailUrl) {
      return
    }
    this.fetchDetails()
  },
  data() {
    return {
      detailUrl: '',
      detail: '',
      thumbs: [],
      thumbPage: 0,
      isLoading: false,
      clickedTag: ''
    }
  },
  methods: {
    fetchDetails() {
      this.detail = ''
      this.isLoading = true
      axios
        .get(urls.gallery.detail, {
          params: {
            detailUrl: this.detailUrl
          }
        })
        .then(response => {
          this.detail = response.data.data
          this.isLoading = false
          this.fetchThumbs()
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchThumbs() {
      axios
        .get(urls.gallery.thumbs, {
          params: {
            detailUrl: this.detailUrl,
            page: this.thumbPage
          }
        })
        .then(response => {
          let elements = response.data.data
          elements = elements.filter(e => this.thumbs.indexOf(e) < 0)
          this.thumbs = this.thumbs.concat(elements)
        })
    },
    handleSearch(searchVal) {
      let searchParams = { ...defaults.searchParams }
      searchParams.f_search = searchVal
      this.$router.push({
        name: 'gallery',
        query: searchParams
      })
    },
    handleBlockTag() {
      this.isLoading = true
      axios
        .post(urls.tag.blockTag, null, {
          params: {
            tag: this.clickedTag
          }
        })
        .then(response => {
          this.clickedTag = ''
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleSearchTag() {
      let tagName = this.clickedTag.split(':')[0]
      let tagVal = this.clickedTag.split(':')[1]
      this.handleSearch(tagName + ':"' + tagVal + '"')
    },
    handleSearchUploader(uploader) {
      this.handleSearch('uploader:"' + uploader + '"')
    },
    handleThumbsScroll(e) {
      if (this.isLoading || this.thumbPage >= this.detail.thumbPages - 1) {
        return
      }

      let scrollLeft = e.target.scrollLeft
      let scrollWidth = e.target.scrollWidth
      let clientWidth = e.target.clientWidth

      if (scrollLeft + clientWidth >= scrollWidth) {
        this.thumbPage++
        this.fetchThumbs()
      }
    },
    handleRead() {
      this.$router.push({ name: 'read', query: { detailUrl: this.detailUrl } })
    },
    handleDownload() {
      this.isLoading = true
      axios
        .post(urls.download.download, null, {
          params: {
            detailUrl: this.detailUrl
          }
        })
        .then(response => {
          this.detail.download = true
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
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
    size() {
      return {
        width:
          window.innerWidth ||
          document.documentElement.clientWidth ||
          document.body.clientWidth,
        height:
          window.innerHeight ||
          document.documentElement.clientHeight ||
          document.body.clientHeight
      }
    },
    tagNameDisplay() {
      return name =>
        tagData.data.filter(d => d.namespace === 'rows')[0].data[name].name
    },
    tagValDisplay() {
      return (name, val) => {
        let data = tagData.data.filter(d => d.namespace === name)[0].data
        return data[val] ? data[val].name : val
      }
    },
    errorImg() {
      return require('@/assets/error.png')
    }
  }
}
</script>

<style scoped>
.detail {
  position: absolute;
  top: 40px;
  bottom: 40px;
  left: 0;
  right: 0;
}

.thumbs-row {
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  height: 150px;
}

.thumbs-row img {
  display: inline-block;
  height: 100%;
}

.title {
  margin: 10px;
  margin-bottom: 20px;
  word-wrap: break-word;
  font-weight: bold;
}

.detail-row {
  margin: 0 10px;
  line-height: 25px;
}

.detail-item {
  display: inline-block;
  margin-right: 20px;
}

.detail-item:last-of-type {
  margin-right: 0;
}

.tags-row {
  margin: 20px 10px 0 10px;
}

.tag-row {
  margin-bottom: 5px;
}

.tag-name {
  display: inline-block;
  width: 80px;
  vertical-align: top;
  margin: 2px 0;
  line-height: 25px;
}

.tag-vals {
  display: inline-block;
  vertical-align: top;
  width: calc(100% - 100px);
}

.tag-val {
  display: inline-block;
  margin: 2px;
  padding: 0 10px;
  line-height: 25px;
  border-radius: 5px;
  background-color: #eee;
}

.comments-row {
  margin-top: 20px;
}

.comment {
  border-top: 0.5px solid #ccc;
  padding: 10px;
}

.comment-infos * {
  color: #567;
}

.comment-info {
  display: inline-block;
  margin-right: 10px;
}

.comment-vote {
  float: right;
}

.comment-content {
  margin-top: 5px;
  word-wrap: break-word;
  background-color: #eee;
  border-radius: 5px;
  padding: 5px 10px;
}

.download-btn {
  color: #2af;
}

.read-btn {
  color: #2a2;
}

.tag-block-modal-title {
  line-height: 40px;
  text-align: center;
}

.tag-block-modal-btns {
  line-height: 40px;
  margin-top: 10px;
  text-align: center;
}

.tag-block-cancel-btn {
  color: #aaa;
}

.tag-block-block-btn {
  color: #f22;
}

.tag-block-search-btn {
  color: #2af;
}
</style>