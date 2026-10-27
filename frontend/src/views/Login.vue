<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align:center;margin-bottom:20px">工作中台登录</h2>
      <el-form ref="loginFormRef" :model="loginForm" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="loginForm.username" @keyup.enter="handleLogin"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" @keyup.enter="handleLogin"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width:100%">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="demo-accounts">
        <span class="tip">演示账号（密码均为 123456）：</span>
        <el-tag
          v-for="a in demoAccounts"
          :key="a.username"
          class="account-tag"
          type="info"
          effect="plain"
          @click="fill(a)"
        >{{ a.username }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script>
import { login as loginApi } from '../api/auth'

export default {
  name: "Login",
  data() {
    return {
      loginForm: { username: "", password: "" },
      loading: false,
      // 一键填充：点击标签自动填入账号密码，方便体验不同角色的权限差异
      demoAccounts: [
        { username: "admin",   password: "123456" },
        { username: "manager", password: "123456" },
        { username: "viewer",  password: "123456" }
      ]
    }
  },
  methods: {
    fill(a) {
      this.loginForm.username = a.username
      this.loginForm.password = a.password
    },
    async handleLogin() {
      const username = this.loginForm.username.trim()
      const password = this.loginForm.password
      if (!username || !password) {
        this.$message.warning('请输入账号和密码')
        return
      }
      this.loading = true
      try {
        // 真实调用后端 redisson-login-demo 的 /api/login
        const res = await loginApi({ username, password })
        // res: { code, msg, token, userInfo:{username,role,permissions} }
        this.$store.commit('setToken', res.token)
        this.$store.commit('setUserInfo', res.userInfo)
        this.$message.success('登录成功')
        this.$router.push('/dashboard')
      } catch (e) {
        this.$message.error((e && e.message) || '登录失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}
.login-card { width: 420px; }
.demo-accounts { margin-top: 6px; text-align: center; }
.tip { font-size: 12px; color: #909399; }
.account-tag { margin: 4px; cursor: pointer; }
</style>
