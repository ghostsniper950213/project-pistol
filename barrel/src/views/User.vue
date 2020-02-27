<template>
  <div id="user">
    <loading v-show="isLoading" />

    <top-bar title="用户" />

    <scroller class="user-form">
      <div class="login-title">
        <span>使用用户名与密码获取Cookie：</span>
      </div>
      <div class="login-part">
        <div class="login-ipt-group">
          <ipt icon="user" type="text" v-model="username" />
        </div>
        <div class="login-ipt-group">
          <ipt icon="lock" type="password" v-model="password" />
        </div>
      </div>
      <div class="login-btns">
        <btn class="logout-btn" @click="handleLogout" :disabled="isLoading">
          <icon icon="sign-out-alt" />&nbsp;登出
        </btn>
        <btn class="login-btn" @click="handleLogin" :disabled="isLoading">
          登入&nbsp;
          <icon icon="sign-in-alt" />
        </btn>
      </div>
      <div class="login-title">
        <span>或者直接填写Cookie：</span>
      </div>
      <div class="cookie-part">
        <div class="cookie-ipt-group">
          <label>ipb_member_id：</label>
          <input type="text" v-model="ipb_member_id" />
        </div>
        <div class="cookie-ipt-group">
          <label>ipb_pass_hash：</label>
          <input type="text" v-model="ipb_pass_hash" />
        </div>
        <div class="cookie-ipt-group">
          <label>igneous：</label>
          <input type="text" v-model="igneous" />
        </div>
      </div>
      <div class="finish-btns">
        <btn class="save-btn" @click="handleSaveCookies" :disabled="isLoading">
          上传&nbsp;
          <icon icon="save" />
        </btn>
      </div>
    </scroller>
  </div>
</template>

<script>
import Icon from '@/components/Icon'
import Ipt from '@/components/Ipt'
import Btn from '@/components/Btn'
import Loading from '@/components/Loading'
import TopBar from '@/components/TopBar'
import Scroller from '@/components/Scroller'

import { axios, urls, requestImage } from '@/axios'

export default {
  name: 'Login',
  components: {
    Icon,
    Ipt,
    Btn,
    Loading,
    TopBar,
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
  top: 40px;
  bottom: 0;
  left: 0;
  right: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.login-title {
  padding: 10px;
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

.login-btns btn {
  border-radius: 10px;
  height: 40px;
  padding: 0 20px;
  margin-left: 10px;
}

.login-btns btn:active {
  opacity: 0.8;
}

.login-btn {
  color: #2af;
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
  border-radius: 10px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
}

.finish-btns {
  padding: 0 10px;
  text-align: right;
}

.save-btn {
  color: #2a2;
}

.logout-btn {
  color: #f22;
}
</style>