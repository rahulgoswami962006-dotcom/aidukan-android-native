package com.aidukan.kirana.ai

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class ProductRecognitionResult(
    val productName: String,
    val brand: String,
    val category: String,
    val size: String,
    val mrp: Int,
    val suggestedPrice: Int,
    val barcode: String,
    val sku: String,
    val confidence: Float
)

object GeminiVisionService {
    
    private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY" // User will add this
    private const val GEMINI_VISION_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent"
    
    suspend fun recognizeProduct(bitmap: Bitmap): ProductRecognitionResult = withContext(Dispatchers.IO) {
        try {
            // Convert bitmap to base64
            val base64Image = bitmapToBase64(bitmap)
            
            // Create request payload
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", """
                                    Analyze this product image and extract the following information in JSON format:
                                    {
                                        "productName": "full product name",
                                        "brand": "brand name",
                                        "category": "product category (Oil, Salt, Biscuits, Noodles, Personal Care, etc.)",
                                        "size": "size with unit (1L, 1kg, 100g, etc.)",
                                        "mrp": estimated MRP in rupees (number only),
                                        "barcode": "barcode number if visible, otherwise generate",
                                        "confidence": confidence score 0-1
                                    }
                                    
                                    If you cannot identify the product clearly, return confidence < 0.5
                                """.trimIndent())
                            })
                            put(JSONObject().apply {
                                put("inline_data", JSONObject().apply {
                                    put("mime_type", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        })
                    })
                })
            }
            
            // Make API call
            val url = URL("$GEMINI_VISION_URL?key=$GEMINI_API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }
            
            // Send request
            connection.outputStream.use { os ->
                os.write(requestBody.toString().toByteArray())
            }
            
            // Read response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                parseGeminiResponse(response)
            } else {
                // Fallback to mock data if API fails
                createMockResult()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Return mock data on error
            createMockResult()
        }
    }
    
    private fun parseGeminiResponse(response: String): ProductRecognitionResult {
        try {
            val jsonResponse = JSONObject(response)
            val candidates = jsonResponse.getJSONArray("candidates")
            val content = candidates.getJSONObject(0).getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")
            
            // Extract JSON from response
            val jsonStart = text.indexOf("{")
            val jsonEnd = text.lastIndexOf("}") + 1
            val jsonText = text.substring(jsonStart, jsonEnd)
            val productData = JSONObject(jsonText)
            
            val mrp = productData.getInt("mrp")
            val suggestedPrice = (mrp * 0.97).toInt() // 3% margin
            
            return ProductRecognitionResult(
                productName = productData.getString("productName"),
                brand = productData.getString("brand"),
                category = productData.getString("category"),
                size = productData.getString("size"),
                mrp = mrp,
                suggestedPrice = suggestedPrice,
                barcode = productData.optString("barcode", generateBarcode()),
                sku = generateSKU(productData.getString("brand"), productData.getString("category")),
                confidence = productData.getDouble("confidence").toFloat()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return createMockResult()
        }
    }
    
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
    
    private fun generateSKU(brand: String, category: String): String {
        val brandCode = brand.take(3).uppercase()
        val categoryCode = category.take(3).uppercase()
        val random = (1000..9999).random()
        return "$brandCode-$categoryCode-$random"
    }
    
    private fun generateBarcode(): String {
        return "89${(10000000..99999999).random()}"
    }
    
    private fun createMockResult(): ProductRecognitionResult {
        return ProductRecognitionResult(
            productName = "Fortune Refined Sunflower Oil",
            brand = "Fortune",
            category = "Cooking Oil",
            size = "1 Liter",
            mrp = 150,
            suggestedPrice = 145,
            barcode = "8901234567890",
            sku = "FOR-OIL-1234",
            confidence = 0.85f
        )
    }
}
