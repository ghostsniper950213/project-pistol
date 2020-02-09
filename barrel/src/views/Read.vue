<template>
  <div id="read">
    <loading v-show="isLoading" />

    <top-bar title="阅读" />
    
    <bottom-bar>
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
    </bottom-bar>

    <div
      class="swiper"
      ref="swiper"
      @touchstart="handleSlideStart"
      @touchmove="handleSlideMove"
      @touchend="handleSlideEnd"
    >
      <div
        class="slide"
        :class="{'animate': animate}"
        v-for="i of totalElements"
        :key="i"
        :ref="i === 1? 'firstSlide' : null"
      >
        <img
          v-if="download"
          :src="imgs[i - 1] ? localImageUrl(imgs[i - 1]) : loadingImg"
          @error="handleImgError"
        />
        <img v-else v-lazy="imgs[i - 1] ? imageUrl(imgs[i - 1]) : ''" @error="handleImgError" />
      </div>
    </div>
  </div>
</template>

<script>
import Loading from '@/components/Loading'
import TopBar from '@/components/TopBar'
import BottomBar from '@/components/BottomBar'

import { axios, urls, requestLocalImage, requestViewImage } from '@/axios'

export default {
  name: 'Read',
  components: {
    Loading,
    TopBar,
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
      draggerAnimate: true
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
            setTimeout(() => {
              this.fetchImages(false)
            }, 5000)
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

      if (this.totalElements <= 0) {
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
      this.draggerAnimate = true

      if (!this.firstSlide) {
        return
      }

      let clientWidth = this.firstSlide.clientWidth
      this.firstSlide.style.marginLeft = -this.currentIndex * clientWidth + 'px'

      this.stageToIndex()
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
  top: 40px;
  bottom: 40px;
  overflow: hidden;
  white-space: nowrap;
}

.slide {
  width: 100%;
  height: 100%;
  display: inline-block;
  position: relative;
}

.slide.animate {
  transition: margin-left 0.2s;
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

.page-info {
  margin-left: 10px;
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

.total-elements {
  line-height: 15px;
}

.stage-container {
  flex: 1;
  margin: 0 20px;
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
  border: 2px solid #234;
  border-radius: 10px;
  z-index: 10;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.dragger.animate {
  transition: left 0.2s;
}
</style>