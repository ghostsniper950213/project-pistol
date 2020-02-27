<template>
  <div id="blockTag">
    <loading v-show="isLoading" />

    <top-bar title="屏蔽标签" />

    <bottom-bar>
      <div class="bottom-bar-right">
        <button class="remove-btn" @click="fetchRemove">删除</button>
      </div>
    </bottom-bar>

    <scroller class="blocked-tags">
      <div
        class="blocked-tag"
        v-for="tag of blockedTags"
        :key="tag"
        @click="handleCheck(tag)"
        :class="{'checked' : checked.indexOf(tag) >= 0}"
      >
        <span>{{ tag }}</span>
      </div>
    </scroller>
  </div>
</template>

<script>
import Loading from '@/components/Loading'
import TopBar from '@/components/TopBar'
import BottomBar from '@/components/BottomBar'
import Scroller from '@/components/Scroller'

import { axios, urls } from '@/axios'

export default {
  name: 'BlockTag',
  components: {
    Loading,
    TopBar,
    BottomBar,
    Scroller
  },
  mounted() {
    this.fetchBlockedTags()
  },
  data() {
    return {
      blockedTags: [],
      checked: [],

      isLoading: false
    }
  },
  methods: {
    fetchBlockedTags() {
      this.isLoading = true
      axios
        .get(urls.tag.blockedTags)
        .then(response => {
          this.blockedTags = response.data.data
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    fetchRemove() {
      if (this.checked.length <= 0) {
        this.checkMode = false
        return
      }
      this.isLoading = true
      let tags = this.checked.reduce((p, n) => p + ',' + n)
      axios
        .post(urls.tag.unblockTag, null, { params: { tags: tags } })
        .then(response => {
          this.checked = []
          this.isLoading = false
          this.fetchBlockedTags()
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleCheck(tag) {
      if (this.checked.indexOf(tag) >= 0) {
        this.checked.splice(this.checked.indexOf(tag), 1)
      } else {
        this.checked.push(tag)
      }
    }
  }
}
</script>

<style scoped>
.blocked-tags {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 40px;
  top: 40px;
}

.blocked-tag {
  padding: 0 10px;
  line-height: 40px;
  border-top: 0.5px solid #ccc;
}

.blocked-tag:first-of-type {
  border-top: none;
}

.blocked-tag.checked {
  background-color: #eee;
}

.remove-btn {
  color: #f22;
}
</style>