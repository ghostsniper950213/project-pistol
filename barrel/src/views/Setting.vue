<template>
  <div id="setting">
    <loading v-show="isLoading" />

    <scroller>
      <div class="setting-title">
        <icon icon="info-circle"/>
        <span>版本</span>
      </div>
      <div class="version-part">
        <btn @click="handleVersionClick">检查更新</btn>
      </div>
      <div class="setting-title">
        <icon icon="file-image"/>
        <span>缓存</span>
        <span class="cache-size">{{ cacheSize }}</span>
      </div>
      <div class="cache-part">
        <div class="clear-cache-row">
          <btn @click="fetchClearCache">清除缓存</btn>
        </div>
      </div>
    </scroller>

    <modal :show="showVersionModal" @close="showVersionModal = false">
      <div class="version-modal-title">
        <div>
          <span>最新版本：</span>
          <span class="version-release">{{ versionInfo.latest || ' - ' }}</span>
        </div>
        <div>
          <span>当前版本：</span>
          <span
            class="version-release"
            :class="{'diff' : versionInfo.latest !== versionInfo.current}"
          >{{ versionInfo.current || ' - ' }}</span>
        </div>
      </div>
      <div class="version-modal-btns">
        <btn class="version-cancel-btn" @click="handleVersionCancel">取消</btn>
        <btn class="version-go-btn" type="blue" @click="handleVersionGo">前往</btn>
      </div>
    </modal>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Ipt from '@/components/Ipt'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import Scroller from '@/components/Scroller'
import Modal from '@/components/Modal'

import { axios, urls, requestImage } from '@/axios'

export default {
  name: 'Setting',
  components: {
    Icon,
    Ipt,
    Btn,
    Loading,
    Scroller,
    Modal
  },
  mounted() {
    this.fetchCacheSize();
  },
  data() {
    return {
      isLoading: false,

      versionInfo: '',
      showVersionModal: false,

      cacheSize: 0,
    }
  },
  methods: {
    fetchVersion() {
      this.isLoading = true
      axios
        .get(urls.app.version)
        .then(response => {
          this.versionInfo = response.data.data
          this.isLoading = false
          this.showVersionModal = true
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchCacheSize() {
      this.isLoading = true
      axios
        .get(urls.image.cacheSize)
        .then(response => {
          this.cacheSize = response.data.data
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchClearCache() {
      this.isLoading = true
      axios
        .get(urls.image.clearCache)
        .then(response => {
          this.fetchCacheSize()
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleVersionClick() {
      this.fetchVersion()
    },
    handleVersionCancel() {
      this.showVersionModal = false
      this.versionInfo = ''
    },
    handleVersionGo() {
      if (this.versionInfo) {
        window.location.href = this.versionInfo.url
      }
    }
  }
}
</script>

<style scoped>
.setting-title {
  padding: 0 10px;
  line-height: 40px;
  margin-top: 20px;
}

.setting-title:first-of-type {
  margin-top: 0;
}

.setting-title span {
  margin-left: 10px;
}

.version-part {
  padding: 0 10px;
}

.version-modal-title {
  line-height: 40px;
}

.version-release {
  display: inline-block;
  width: 80px;
  text-align: center;
  color: #2a2;
}

.version-release.diff {
  color: #f00;
}

.version-modal-btns {
  line-height: 40px;
  margin-top: 10px;
  display: flex;
}

.version-modal-btns button {
  flex: 1;
  margin-left: 10px;
}

.version-modal-btns button:first-of-type {
  margin-left: 0;
}

.cache-part {
  padding: 0 10px;
}

.cache-size {
  display: inline-block;
  margin: 2px;
  padding: 0 10px;
  line-height: 25px;
  border-radius: 13px;
  color: #89a;
  background-color: #eee;
}
</style>