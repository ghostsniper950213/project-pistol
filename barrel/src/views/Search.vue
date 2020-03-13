<template>
  <div id="search">
    <scroller class="search-form">
      <ipt
        class="search-input"
        v-model="searchVal"
        icon="search"
        type="search"
        @keyup.enter.native="handleConfirm"
      />
      <div class="search-title">
        <icon icon="tags" />
        <span>类型</span>
      </div>
      <div class="search-category-row">
        <div
          class="search-category"
          v-for="c of categories"
          :key="c.name"
          :style="{backgroundColor: c.color}"
          :class="{'selected': searchCategories.indexOf(c.name) >= 0}"
          @click="handleCategoryClick(c.name)"
        >
          <span>{{ c.name }}</span>
        </div>
      </div>
      <div class="search-title">
        <icon icon="star" />
        <span>评分</span>
      </div>
      <div class="search-rate-row">
        <icon
          icon="star"
          class="search-rate"
          v-for="i of 5"
          :key="i"
          :class="{'active' : i <= searchRating}"
          @click="handleRateClick(i)"
        />
      </div>
      <div class="search-title">
        <icon icon="images" />
        <span>页数</span>
      </div>
      <div class="search-pages-row">
        <div class="flex-input-container">
          <input
            type="number"
            class="search-pages"
            v-model.number="searchPagesMin"
            @keyup.enter="handleConfirm"
          />
        </div>
        <div class="search-pages-split">-</div>
        <div class="flex-input-container">
          <input
            type="number"
            class="search-pages"
            v-model.number="searchPagesMax"
            @keyup.enter="handleConfirm"
          />
        </div>
      </div>
      <div class="search-btns-row">
        <btn class="reset-btn" @click="resetSearch">重置</btn>
        <btn class="confirm-btn" type="blue" @click="handleConfirm">出发</btn>
      </div>
    </scroller>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Ipt from '@/components/Ipt'
import Btn from '@/components/Btn'
import Scroller from '@/components/Scroller'

import { axios, urls, requestImage } from '@/axios'
import defaults from '@/data/defaults'

export default {
  name: 'Search',
  components: {
    Icon,
    Ipt,
    Btn,
    Scroller
  },
  mounted() {
    let searchParams = { ...defaults.searchParams }
    for (let key of Object.keys(searchParams)) {
      if (this.$route.query[key] !== undefined) {
        searchParams[key] = this.$route.query[key]
      }
    }

    this.searchCategories = this.calcCategories(searchParams.f_cats)
    this.searchVal = searchParams.f_search
    this.searchRating = searchParams.f_srdd
    this.searchPagesMin = searchParams.f_spf
    this.searchPagesMax = searchParams.f_spt
  },
  data() {
    return {
      searchVal: '',
      searchRating: '',
      searchCategories: '',
      searchPagesMin: '',
      searchPagesMax: ''
    }
  },
  methods: {
    resetSearch() {
      let searchParams = { ...defaults.searchParams }
      this.searchCategories = this.calcCategories(searchParams.f_cats)
      this.searchVal = searchParams.f_search
      this.searchRating = searchParams.f_srdd
      this.searchPagesMin = searchParams.f_spf
      this.searchPagesMax = searchParams.f_spt
    },
    handleCategoryClick(name) {
      let index = this.searchCategories.indexOf(name)
      if (index >= 0) {
        this.searchCategories.splice(index, 1)
      } else {
        this.searchCategories.push(name)
      }
    },
    handleRateClick(i) {
      this.searchRating = i
    },
    handleConfirm() {
      let fCats = 0
      this.categories.forEach(c => {
        if (this.searchCategories.indexOf(c.name) < 0) {
          fCats += c.score
        }
      })
      let searchParams = {
        page: 0,
        f_cats: fCats,
        f_search: this.searchVal,
        f_spf: this.searchPagesMin,
        f_spt: this.searchPagesMax,
        f_srdd: this.searchRating
      }
      this.$router.push({ name: 'gallery', query: searchParams })
    },
    calcCategories(score) {
      let categories = [...this.categories]
      let searchCategories = categories.map(category => category.name)
      categories
        .sort((prev, next) => next.score - prev.score)
        .forEach(category => {
          if (category.score <= score) {
            score -= category.score
            searchCategories.splice(searchCategories.indexOf(category.name), 1)
          }
        })
      return searchCategories
    }
  },
  computed: {
    categories: () => defaults.categories
  }
}
</script>

<style scoped>
.search-form {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 50px;
  top: 0;
}

.search-input {
  margin: 10px;
}

.search-title {
  padding: 10px;
  margin-top: 20px;
}

.search-title span {
  margin-left: 10px;
}

.search-category-row {
  padding: 0 7px;
}

.search-category {
  display: inline-block;
  margin: 2px;
  padding: 0 10px;
  line-height: 25px;
  border-radius: 13px;
  opacity: 0.5;
}

.search-category.selected {
  opacity: 1;
}

.search-category span {
  color: #fff;
}

.search-rate-row {
  padding: 0 10px;
}

.search-rate {
  margin-right: 10px;
  font-size: 20px;
  color: #ddd;
}

.search-rate.active {
  color: #fd0;
}

.search-pages-row {
  width: 50%;
  padding: 0 10px;
  display: inline-flex;
  position: relative;
}

.flex-input-container {
  flex: 1;
}

.search-pages {
  width: 100%;
  height: 40px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
  padding: 0 10px;
}

.flex-input-container:first-of-type .search-pages {
  border-radius: 5px 0 0 5px;
}

.flex-input-container:last-of-type .search-pages {
  border-radius: 0 5px 5px 0;
}

.search-pages-split {
  position: absolute;
  text-align: center;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  width: 20px;
  height: 20px;
  line-height: 20px;
  margin: auto;
}

.search-btns-row {
  margin: 20px 10px 10px 10px;
  text-align: right;
}

.confirm-btn {
  margin-left: 10px;
}
</style>