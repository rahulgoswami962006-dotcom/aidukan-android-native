package com.aidukan.kirana.cloud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class SyncResult(
    val success: Boolean,
    val message: String,
    val syncedItems: Int
)

object SupabaseService {
    
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL" // User will add this
    private const val SUPABASE_KEY = "YOUR_SUPABASE_ANON_KEY" // User will add this
    
    /**
     * Sync products to cloud
     */
    suspend fun syncProducts(products: List<Map<String, Any>>): SyncResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/products")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=minimal")
                doOutput = true
            }
            
            val jsonArray = JSONArray()
            products.forEach { product ->
                jsonArray.put(JSONObject(product))
            }
            
            connection.outputStream.use { os ->
                os.write(jsonArray.toString().toByteArray())
            }
            
            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                SyncResult(true, "Products synced successfully", products.size)
            } else {
                SyncResult(false, "Sync failed: $responseCode", 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult(false, "Sync error: ${e.message}", 0)
        }
    }
    
    /**
     * Sync sales to cloud
     */
    suspend fun syncSales(sales: List<Map<String, Any>>): SyncResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/sales")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=minimal")
                doOutput = true
            }
            
            val jsonArray = JSONArray()
            sales.forEach { sale ->
                jsonArray.put(JSONObject(sale))
            }
            
            connection.outputStream.use { os ->
                os.write(jsonArray.toString().toByteArray())
            }
            
            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                SyncResult(true, "Sales synced successfully", sales.size)
            } else {
                SyncResult(false, "Sync failed: $responseCode", 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult(false, "Sync error: ${e.message}", 0)
        }
    }
    
    /**
     * Fetch products from cloud
     */
    suspend fun fetchProducts(): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/products?select=*")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
            }
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonArray = JSONArray(response)
                
                val products = mutableListOf<Map<String, Any>>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val product = mutableMapOf<String, Any>()
                    jsonObject.keys().forEach { key ->
                        product[key] = jsonObject.get(key)
                    }
                    products.add(product)
                }
                products
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Create backup of entire database
     */
    suspend fun createBackup(data: Map<String, List<Map<String, Any>>>): SyncResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/backups")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }
            
            val backupData = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("data", JSONObject(data))
            }
            
            connection.outputStream.use { os ->
                os.write(backupData.toString().toByteArray())
            }
            
            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                SyncResult(true, "Backup created successfully", 1)
            } else {
                SyncResult(false, "Backup failed: $responseCode", 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult(false, "Backup error: ${e.message}", 0)
        }
    }
    
    /**
     * Restore from backup
     */
    suspend fun restoreBackup(backupId: String): Map<String, List<Map<String, Any>>>? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/backups?id=eq.$backupId")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
            }
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 0) {
                    val backup = jsonArray.getJSONObject(0)
                    val dataJson = backup.getJSONObject("data")
                    
                    val restoredData = mutableMapOf<String, List<Map<String, Any>>>()
                    dataJson.keys().forEach { key ->
                        val items = mutableListOf<Map<String, Any>>()
                        val itemsArray = dataJson.getJSONArray(key)
                        for (i in 0 until itemsArray.length()) {
                            val item = mutableMapOf<String, Any>()
                            val itemJson = itemsArray.getJSONObject(i)
                            itemJson.keys().forEach { itemKey ->
                                item[itemKey] = itemJson.get(itemKey)
                            }
                            items.add(item)
                        }
                        restoredData[key] = items
                    }
                    restoredData
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
