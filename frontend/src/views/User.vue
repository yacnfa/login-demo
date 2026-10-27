<template>
  <el-card>
    <div class="search-box">
      <el-input v-model="query.keyword" placeholder="搜索用户名" style="width:220px" />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button v-if="can('user:add')" type="success" @click="openAddDialog">新增用户</el-button>
      <el-button
        v-if="can('user:delete')"
        type="danger"
        :loading="deleting"
        :disabled="selectedRows.length === 0"
        @click="handleBatchDelete"
      >
        批量删除（已选 {{ selectedRows.length }} 项）
      </el-button>
    </div>

    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="margin-top:15px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="手机号" prop="phone" />
      <el-table-column label="角色" prop="role" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="can('user:edit')" link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button v-if="can('user:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 使用封装好的弹窗组件 -->
    <UserFormDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      :row-data="currentRow"
      :submitting="submitting"
      @submit="submitForm"
    />
  </el-card>
</template>

<script>
import { getUserList, addUser, editUser, delUser, batchDeleteUsers } from '../api/user'
import UserFormDialog from '../components/UserFormDialog.vue'

export default {
  name: "User",
  components: { UserFormDialog },
  data() {
    return {
      query: { keyword: "" },
      tableData: [],
      loading: false,
      submitting: false,
      deleting: false,
      selectedRows: [],
      dialogVisible: false,
      dialogTitle: "新增用户",
      currentRow: {}    // 传给弹窗的那一行数据
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    can(permission) {
      return this.$store.state.userInfo?.permissions?.includes(permission) || false
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await getUserList({ keyword: this.query.keyword })
        if (res.code === 200) this.tableData = res.data
      } catch (e) {
        this.$message.error("获取列表失败")
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.fetchList()
    },
    // 新增：currentRow 传空对象
    openAddDialog() {
      this.dialogTitle = "新增用户"
      this.currentRow = {}
      this.dialogVisible = true
    },
    // 编辑：currentRow 传该行数据
    openEditDialog(row) {
      this.dialogTitle = "编辑用户"
      this.currentRow = { ...row }
      this.dialogVisible = true
    },
    // 接收弹窗抛上来的表单数据
    async submitForm(form) {
      this.submitting = true
      try {
        const res = form.id ? await editUser(form) : await addUser(form)
        if (res.code === 200) {
          this.$message.success(res.msg || "保存成功")
          this.dialogVisible = false
          this.fetchList()
        }
      } catch (e) {
        this.$message.error("保存失败")
      } finally {
        this.submitting = false
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm(`确定删除用户「${row.username}」吗？`, '提示', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        })
      } catch {
        return
      }
      try {
        await delUser(row.id)
        this.$message.success('删除成功')
        await this.fetchList()
        this.$refs.tableRef.clearSelection()
      } catch (error) {
        this.$message.error(error.message || '删除失败')
      }
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    async handleBatchDelete() {
      const ids = this.selectedRows.map(row => row.id)
      if (ids.length === 0) return
      try {
        await this.$confirm(`确定删除选中的 ${ids.length} 个用户吗？`, '批量删除', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        })
      } catch {
        return  // 用户取消
      }

      this.deleting = true
      try {
        const res = await batchDeleteUsers(ids)
        this.$message.success(res.msg || `成功删除 ${ids.length} 条数据`)
      } catch (error) {
        this.$message.error(`删除中断：${error.message || '请求失败'}`)
      } finally {
        await this.fetchList()
        this.$refs.tableRef.clearSelection()
        this.selectedRows = []
        this.deleting = false
      }
    }
  }
}
</script>

<style scoped>
.search-box { display: flex; gap: 10px; align-items: center; }
</style>
