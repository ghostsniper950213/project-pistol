<template>
  <div id="read">
    <loading v-show="isLoading" />

    <div class="swiper" ref="swiper" :class="readMode" @scroll="handleScroll">
      <div
        class="slide"
        :class="[{'animate': animate && !readModeChanging}, readMode]"
        @touchstart="handleSlideStart"
        @touchmove="handleSlideMove"
        @touchend="handleSlideEnd"
        v-for="i of totalElements"
        :key="i"
        :ref="i === 1 ? 'firstSlide' : null"
      >
        <img
          v-if="download"
          :src="imgs[i - 1] ? localImageUrl(imgs[i - 1]) : loadingImg"
          @error="handleImgError"
        />
        <img v-else v-lazy="imgs[i - 1] ? imageUrl(imgs[i - 1]) : ''" @error="handleImgError" />
      </div>
    </div>

    <bottom-bar class="status-bar">
      <div class="page-info">
        <div class="page-number">
          <span>{{ currentIndex + 1 }}</span>
        </div>
        <div class="total-elements">
          <span>{{ totalElements > 0 ? totalElements : '-' }}</span>
        </div>
      </div>

      <div class="stage-container" @touchmove="handleStageMove" @touchend="handleStageEnd">
        <div class="stage" ref="stage">
          <div class="dragger" ref="dragger" :class="{'animate': draggerAnimate}"></div>
        </div>
      </div>

      <button class="read-mode-btn" @click="handleReadChange">
        <icon icon="arrows-alt-h" :class="readMode" />
      </button>
    </bottom-bar>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Loading from '@/components/Loading'
import BottomBar from '@/components/BottomBar'

import { axios, urls, requestLocalImage, requestViewImage } from '@/axios'

export default {
  name: 'Read',
  components: {
    Icon,
    Loading,
    BottomBar
  },
  mounted() {
    this.detailUrl = this.$route.query.detailUrl
    this.fetchImages(true)
  },
  data() {
    return {
      detailUrl: '',

      download: false,
      isLoading: false,

      pageNumber: 0,
      totalPages: 0,
      totalElements: 0,
      currentIndex: 0,
      imgs: [],

      prevX: 0,
      dx: 0,
      animate: true,
      draggerAnimate: true,

      readMode: 'horizontal',
      readModeChanging: false
    }
  },
  methods: {
    fetchImages(load) {
      this.isLoading = load
      axios
        .get(urls.gallery.imagePages, {
          params: { detailUrl: this.detailUrl, page: this.pageNumber }
        })
        .then(response => {
          this.imgs = this.imgs.concat(
            response.data.data.elements.filter(
              element => this.imgs.indexOf(element) < 0
            )
          )
          this.pageNumber = response.data.data.pageNumber
          this.totalPages = response.data.data.totalPages
          this.totalElements = response.data.data.totalElements
          this.download = response.data.data.meta.download

          this.stageToIndex()

          this.isLoading = false

          if (this.pageNumber < this.totalPages - 1) {
            this.pageNumber = this.pageNumber + 1
            this.fetchImages(false)
          }
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleSlideStart(e) {
      this.prevX = e.touches[0].screenX
    },
    handleSlideMove(e) {
      if (this.readMode === 'vertical') {
        return
      }

      this.animate = false

      if (!this.firstSlide) {
        return
      }

      let firstSlide = this.firstSlide
      let prevMargin = parseInt(
        firstSlide.style.marginLeft
          ? firstSlide.style.marginLeft.replace('px', '')
          : '0'
      )
      let nextX = e.touches[0].screenX
      let move = nextX - this.prevX
      firstSlide.style.marginLeft = prevMargin + move + 'px'

      this.dx = nextX - this.prevX
      this.prevX = nextX
    },
    handleSlideEnd(e) {
      if (this.readMode === 'vertical') {
        return
      }

      this.readModeChanging = false
      this.animate = true

      if (!this.firstSlide) {
        return
      }

      let firstSlide = this.firstSlide
      let prevMargin = parseInt(
        firstSlide.style.marginLeft
          ? firstSlide.style.marginLeft.replace('px', '')
          : '0'
      )
      let clientWidth = firstSlide.clientWidth

      let mod = prevMargin % clientWidth

      if (mod > 0) {
        firstSlide.style.marginLeft = 0
      } else if (-prevMargin / clientWidth >= this.totalElements - 1) {
        firstSlide.style.marginLeft =
          (this.totalElements - 1) * -clientWidth + 'px'
      } else if (this.dx >= 0) {
        firstSlide.style.marginLeft = prevMargin - mod + 'px'
      } else if (this.dx < 0) {
        firstSlide.style.marginLeft = prevMargin - mod - clientWidth + 'px'
      }

      this.currentIndex =
        -parseInt(
          firstSlide.style.marginLeft
            ? firstSlide.style.marginLeft.replace('px', '')
            : '0'
        ) / clientWidth

      this.stageToIndex()
    },
    handleScroll(e) {
      if (this.readMode === 'horizontal') {
        return
      }
      let scrollTop = e.target.scrollTop
      let index = 0
      let swiper = this.$refs.swiper
      if (scrollTop + swiper.clientHeight >= swiper.scrollHeight) {
        this.currentIndex = this.imgs.length - 1
        this.stageToIndex()
        return
      }
      for (let slide of swiper.children) {
        if (slide.offsetTop - slide.clientHeight / 2 > scrollTop) {
          if (index <= 1) {
            this.currentIndex = 0
          } else if (index >= this.imgs.length) {
            this.currentIndex = this.imgs.length - 1
          } else {
            this.currentIndex = index - 1
          }
          this.stageToIndex()
          break
        }
        index++
      }
    },
    calcPage() {
      let dragger = this.$refs.dragger
      let left = dragger.style.left
        ? parseInt(dragger.style.left.replace('px', ''))
        : 0
      let subWidth = this.stageWidth / (this.totalElements - 1)
      let page = Math.round((left + 10) / subWidth)

      return page
    },
    stageToIndex() {
      let dragger = this.$refs.dragger
      let width = this.stageWidth / (this.totalElements - 1)
      let left = this.currentIndex * width - 10
      dragger.style.left = left + 'px'
    },
    handleStageMove(e) {
      this.draggerAnimate = false

      if (this.readMode === 'vertical' || this.totalElements <= 0) {
        return
      }

      let dragger = this.$refs.dragger

      let left = e.touches[0].clientX - this.stageLeft - 10
      if (left < -10 || left > this.stageWidth - 10) {
        return
      }
      dragger.style.left = e.touches[0].clientX - this.stageLeft - 10 + 'px'

      this.currentIndex = this.calcPage()
    },
    handleStageEnd() {
      this.readModeChanging = false
      this.draggerAnimate = true

      if (this.readMode === 'vertical' || !this.firstSlide) {
        return
      }

      let clientWidth = this.firstSlide.clientWidth
      this.firstSlide.style.marginLeft = -this.currentIndex * clientWidth + 'px'
      this.stageToIndex()
    },
    handleReadChange() {
      if (this.readMode === 'horizontal') {
        this.firstSlide.style.margin = 0
        this.readMode = 'vertical'
      } else {
        this.readMode = 'horizontal'
      }
      this.readModeChanging = true
      this.$nextTick(() => {
        if (this.readMode === 'horizontal') {
          let clientWidth = this.firstSlide.clientWidth
          this.firstSlide.style.marginLeft =
            -this.currentIndex * clientWidth + 'px'
        } else {
          let swiper = this.$refs.swiper
          let index = 0
          let scrollTop = 0
          for (let slide of swiper.children) {
            if (index === this.currentIndex) {
              scrollTop = slide.offsetTop
              break
            }
            index++
          }
          swiper.scrollTop = scrollTop
        }
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
      return url => requestViewImage(url)
    },
    localImageUrl() {
      return url => requestLocalImage(url)
    },
    firstSlide() {
      if (!this.$refs.firstSlide || this.$refs.firstSlide.length <= 0) {
        return
      }
      return this.$refs.firstSlide[0]
    },
    stageWidth() {
      return this.$refs.stage.clientWidth
    },
    stageLeft() {
      return this.$refs.stage.offsetLeft
    },
    loadingImg() {
      return require('@/assets/loading.png')
    },
    errorImg() {
      return require('@/assets/error.png')
    }
  }
}
</script>

<style scoped>
.swiper {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 40px;
  top: 0;
  overflow: hidden;
  white-space: nowrap;
}

.swiper.vertical {
  overflow: scroll;
}

.slide {
  width: 100%;
  height: 100%;
  display: inline-block;
  position: relative;
}

.slide.vertical {
  height: auto;
  margin: 0;
  display: block;
  position: relative;
}

.slide.animate {
  transition: margin-left 0.2s ease;
}

.slide img {
  max-width: 100%;
  max-height: 100%;
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
}

.slide.vertical img {
  width: 100%;
  position: relative;
  margin: 0;
}

.status-bar {
  display: flex;
  align-items: center;
}

.page-info {
  width: 60px;
  padding: 0 10px;
  text-align: center;
}

.page-info * {
  font-size: 12px;
}

.page-number {
  line-height: 15px;
  border-bottom: 1px solid #234;
}

.total-elements {
  line-height: 15px;
}

.stage-container {
  flex: 1;
  margin: 0 10px;
}

.stage {
  background-color: #eee;
  height: 4px;
  overflow: visible;
  position: relative;
}

.dragger {
  position: absolute;
  left: -10px;
  top: -8px;
  width: 20px;
  height: 20px;
  background-color: #fff;
  border: 2px solid #567;
  border-radius: 10px;
  z-index: 10;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.dragger.animate {
  transition: left 0.2s ease;
}

.read-mode-btn .icon {
  transition: transform 0.2s ease;
}

.read-mode-btn .icon.vertical {
  transform: rotateZ(90deg);
}
</style>