<template>
  <div id="search">
    <top-bar title="搜索" />

    <bottom-bar>
      <div class="bottom-bar-right">
        <button class="reset-btn" @click="resetSearch">重置</button>
        <button class="confirm-btn" @click="handleConfirm">出发</button>
      </div>
    </bottom-bar>

    <div class="search-form">
      <ipt class="search-input" v-model="searchVal" icon="search" />
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
        <span>张数</span>
      </div>
      <div class="search-pages-row">
        <input type="number" class="search-pages" v-model.number="searchPagesMin" />
        <span>-</span>
        <input type="number" class="search-pages" v-model.number="searchPagesMax" />
      </div>
    </div>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Ipt from '@/components/Ipt'
import Btn from '@/components/Btn'
import TopBar from '@/components/TopBar'
import BottomBar from '@/components/BottomBar'

import { axios, urls, requestImage } from '@/axios'
import defaults from '@/data/defaults'

export default {
  name: 'Search',
  components: {
    Icon,
    Ipt,
    Btn,
    TopBar,
    BottomBar
  },
  mounted() {
    let searchParams = { ...defaults.searchParams }
    for (let key of Object.keys(searchParams)) {
      if (this.$route.query[key] != undefined) {
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
  top: 40px;
  bottom: 40px;
  left: 0;
  right: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.search-input {
  margin: 10px;
}

.search-title {
  margin: 10px;
  margin-top: 20px;
}

.search-title span {
  margin-left: 10px;
}

.search-category-row {
  margin: 0 7px;
}

.search-category {
  display: inline-block;
  margin: 2px;
  padding: 0 10px;
  line-height: 25px;
  border-radius: 5px;
  opacity: 0.5;
}

.search-category.selected {
  opacity: 1;
}

.search-category span {
  color: #fff;
}

.search-rate-row {
  margin: 0 10px;
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
  margin: 0 10px;
}

.search-pages-row span {
  margin: 0 10px;
}

.search-pages {
  width: 100px;
  height: 30px;
  border-radius: 5px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
  padding: 0 10px;
}

.search-btns-row {
  margin: 10px;
  margin-top: 20px;
  text-align: right;
}

.reset-btn {
  color: #f60;
}

.confirm-btn {
  color: #2a2;
}
</style>