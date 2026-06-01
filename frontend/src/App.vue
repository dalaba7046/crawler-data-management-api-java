<script setup>
import { ref, onMounted } from 'vue'

// 連到 Spring Boot API（對齊 FastAPI 版前端，只改 base URL 與路由 prefix）
const API_BASE = 'http://localhost:8000'

const items = ref([])
const loading = ref(false)
const error = ref('')
const form = ref({ skuId: '', siteId: '', itemName: '' })

async function loadItems() {
  loading.value = true
  error.value = ''
  try {
    const res = await fetch(`${API_BASE}/v1/item/items`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    items.value = await res.json()
  } catch (e) {
    error.value = '載入失敗：' + e.message
  } finally {
    loading.value = false
  }
}

async function createItem() {
  error.value = ''
  try {
    const res = await fetch(`${API_BASE}/v1/item/item`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    form.value = { skuId: '', siteId: '', itemName: '' }
    await loadItems()
  } catch (e) {
    error.value = '新增失敗：' + e.message
  }
}

async function softDelete(skuId) {
  error.value = ''
  try {
    const res = await fetch(`${API_BASE}/v1/item/item/${skuId}`, { method: 'PUT' })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    await loadItems()
  } catch (e) {
    error.value = '刪除失敗：' + e.message
  }
}

onMounted(loadItems)
</script>

<template>
  <main style="max-width: 860px; margin: 40px auto; font-family: system-ui, sans-serif;">
    <h1>Crawler Data 管理儀表板</h1>
    <p style="color:#666">Spring Boot + MS SQL ・ SKU 主檔查詢與管理</p>

    <section style="margin: 24px 0; padding: 16px; border: 1px solid #ddd; border-radius: 8px;">
      <h2 style="font-size: 16px;">新增 SKU</h2>
      <input v-model="form.skuId" placeholder="SKU_ID" />
      <input v-model="form.siteId" placeholder="SITE_ID" />
      <input v-model="form.itemName" placeholder="ITEM_NAME" />
      <button @click="createItem">新增</button>
    </section>

    <p v-if="error" style="color:#c00">{{ error }}</p>
    <p v-if="loading">載入中…</p>

    <table style="width:100%; border-collapse: collapse;">
      <thead>
        <tr style="text-align:left; border-bottom: 2px solid #333;">
          <th>SKU_ID</th><th>SITE_ID</th><th>ITEM_NAME</th><th>建立時間</th><th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="it in items" :key="it.skuId" style="border-bottom: 1px solid #eee;">
          <td>{{ it.skuId }}</td>
          <td>{{ it.siteId }}</td>
          <td>{{ it.itemName }}</td>
          <td>{{ it.createdAt }}</td>
          <td><button @click="softDelete(it.skuId)">軟刪除</button></td>
        </tr>
      </tbody>
    </table>
  </main>
</template>
