<template>
  <div id="user">
    <loading v-show="isLoading" />

    <scroller class="user-form">
      <div class="login-title">
        <span>使用用户名与密码获取 Cookie：</span>
      </div>
      <div class="login-part">
        <div class="login-ipt-group">
          <ipt icon="user" type="text" v-model="username" @keyup.enter.native="handleLogin" />
        </div>
        <div class="login-ipt-group">
          <ipt icon="lock" type="password" v-model="password" @keyup.enter.native="handleLogin" />
        </div>
      </div>
      <div class="login-btns">
        <btn class="login-btn" type="blue" @click="handleLogin" :disabled="isLoading">获取</btn>
      </div>
      <div class="login-title">
        <span>或者直接填写 Cookie：</span>
      </div>
      <div class="cookie-part">
        <div class="cookie-ipt-group">
          <label>ipb_member_id：</label>
          <input type="text" v-model="ipb_member_id" @keyup.enter="handleSaveCookies"/>
        </div>
        <div class="cookie-ipt-group">
          <label>ipb_pass_hash：</label>
          <input type="text" v-model="ipb_pass_hash" @keyup.enter="handleSaveCookies"/>
        </div>
        <div class="cookie-ipt-group">
          <label>igneous：</label>
          <input type="text" v-model="igneous" @keyup.enter="handleSaveCookies"/>
        </div>
      </div>
      <div class="finish-btns">
        <btn class="logout-btn" type="red" @click="handleLogout" :disabled="isLoading">清除</btn>
        <btn class="save-btn" type="blue" @click="handleSaveCookies" :disabled="isLoading">上传</btn>
      </div>
    </scroller>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Ipt from '@/components/Ipt'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import Scroller from '@/components/Scroller'

import { axios, urls, requestImage } from '@/axios'

export default {
  name: 'Login',
  components: {
    Icon,
    Ipt,
    Btn,
    Loading,
    Scroller
  },
  mounted() {
    this.loadCookies()
  },
  data() {
    return {
      username: '',
      password: '',

      ipb_member_id: '',
      ipb_pass_hash: '',
      igneous: '',

      isLoading: false
    }
  },
  methods: {
    loadCookies() {
      this.isLoading = true
      axios
        .get(urls.user.cookie)
        .then(response => {
          this.ipb_member_id = response.data.data.ipb_member_id
          this.ipb_pass_hash = response.data.data.ipb_pass_hash
          this.igneous = response.data.data.igneous
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
        })
    },
    handleLogin() {
      if (!this.username || !this.password) {
        alert('请先输入用户名和密码')
        return
      }
      this.isLoading = true
      axios
        .post(urls.user.login, null, {
          params: { username: this.username, password: this.password }
        })
        .then(response => {
          this.ipb_member_id = response.data.data.ipb_member_id
          this.ipb_pass_hash = response.data.data.ipb_pass_hash
          this.igneous = response.data.data.igneous
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
          this.loadCookies()
        })
    },
    handleSaveCookies() {
      this.isLoading = true
      axios
        .post(urls.user.cookie, null, {
          params: {
            cookies: {
              ipb_member_id: this.ipb_member_id,
              ipb_pass_hash: this.ipb_pass_hash,
              igneous: this.igneous
            }
          }
        })
        .then(response => {
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
          this.loadCookies()
        })
    },
    handleLogout() {
      this.isLoading = true
      axios
        .get(urls.user.logout)
        .then(response => {
          this.ipb_member_id = ''
          this.ipb_pass_hash = ''
          this.igneous = ''
          this.isLoading = false
        })
        .catch(error => {
          this.isLoading = false
          this.loadCookies()
        })
    }
  }
}
</script>

<style scoped>
.user-form {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 40px;
  top: 0;
}

.login-title {
  padding: 0 10px;
  line-height: 40px;
  margin-top: 20px;
}

.login-title:first-of-type {
  margin-top: 0;
}

.login-part {
  padding: 0 10px;
}

.login-ipt-group {
  position: relative;
  margin-bottom: 10px;
}

.login-ipt-group ipt {
  height: 40px;
  width: 100%;
  padding: 0 10px 0 40px;
  height: 40px;
  border-radius: 10px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
}

.login-btns {
  padding: 0 10px;
  text-align: right;
}

.logout-btn {
  margin-right: 10px;
}

.cookie-part {
  padding: 0 10px;
}

.cookie-ipt-group {
  margin-bottom: 10px;
}

.cookie-ipt-group label {
  display: inline-block;
  width: 140px;
  text-align: right;
}

.cookie-ipt-group input {
  display: inline-block;
  height: 40px;
  width: calc(100% - 140px);
  padding: 10px;
  height: 40px;
  border-radius: 5px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
}

.finish-btns {
  padding: 0 10px;
  text-align: right;
}
</style>