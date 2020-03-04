<template>
  <div id="blockTag">
    <loading v-show="isLoading" />

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

    <bottom-bar>
      <button class="remove-btn" @click="fetchRemove">
        <icon icon="trash-alt" />
      </button>
    </bottom-bar>
  </div>
</template>

<script>
import Loading from '@/components/Loading'
import BottomBar from '@/components/BottomBar'
import Scroller from '@/components/Scroller'
import Icon from '@/components/Icon'

import { axios, urls } from '@/axios'

export default {
  name: 'BlockTag',
  components: {
    Loading,
    BottomBar,
    Scroller,
    Icon
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
  top: 0;
}

.blocked-tag {
  padding: 0 10px;
  line-height: 40px;
  border-top: 0.5px solid #bbb;
  background-color: #fff;
}

.blocked-tag:first-of-type {
  border-top: none;
}

.blocked-tag.checked {
  background-color: #eee;
}
</style>