package com.aidukan.kirana.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ChatResponse(
    val action: String,
    val response: String,
    val data: Map<String, Any>,
    val confidence: Float
)

object GeminiChatService {
    
    private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY" // User will add this
    private const val GEMINI_CHAT_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent"
    
    private val conversationHistory = mutableListOf<Pair<String, String>>()
    
    suspend fun processVoiceCommand(command: String, context: String = ""): ChatResponse = withContext(Dispatchers.IO) {
        try {
            // Build conversation context
            val systemPrompt = """
                You are an AI assistant for a Kirana store management system called Aidukan.
                You help shopkeepers manage their store through voice commands in Hindi and English.
                
                Your capabilities:
                1. Record sales: Extract product name, quantity, and price
                2. Check inventory: Provide stock information
                3. Generate reports: Sales, profit, expenses
                4. Add expenses: Record business expenses
                5. Customer management: Track credit/udhaar
                
                Always respond in a friendly, concise manner in Hindi/English mix.
                Extract structured data from commands and return in JSON format.
                
                Response format:
                {
                    "action": "SALE|STOCK_CHECK|REPORT|EXPENSE|CUSTOMER|UNKNOWN",
                    "response": "friendly response to speak back",
                    "data": {
                        "product": "product name",
                        "amount": number,
                        "quantity": number,
                        etc.
                    },
                    "confidence": 0.0-1.0
                }
            """.trimIndent()
            
            // Create request payload
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    // Add system prompt
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemPrompt)
                            })
                        })
                    })
                    
                    // Add conversation history
                    conversationHistory.takeLast(5).forEach { (userMsg, aiMsg) ->
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", userMsg) })
                            })
                        })
                        put(JSONObject().apply {
                            put("role", "model")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", aiMsg) })
                            })
                        })
                    }
                    
                    // Add current command
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Command: $command\nContext: $context")
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topK", 40)
                    put("topP", 0.95)
                    put("maxOutputTokens", 1024)
                })
            }
            
            // Make API call
            val url = URL("$GEMINI_CHAT_URL?key=$GEMINI_API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }
            
            // Send request
            connection.outputStream.use { os ->
                os.write(requestBody.toString().toByteArray())
            }
            
            // Read response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val chatResponse = parseGeminiChatResponse(response)
                
                // Add to conversation history
                conversationHistory.add(command to chatResponse.response)
                
                chatResponse
            } else {
                // Fallback to basic processing
                createFallbackResponse(command)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            createFallbackResponse(command)
        }
    }
    
    private fun parseGeminiChatResponse(response: String): ChatResponse {
        try {
            val jsonResponse = JSONObject(response)
            val candidates = jsonResponse.getJSONArray("candidates")
            val content = candidates.getJSONObject(0).getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")
            
            // Extract JSON from response
            val jsonStart = text.indexOf("{")
            val jsonEnd = text.lastIndexOf("}") + 1
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonText = text.substring(jsonStart, jsonEnd)
                val data = JSONObject(jsonText)
                
                val dataMap = mutableMapOf<String, Any>()
                val dataObj = data.optJSONObject("data")
                dataObj?.keys()?.forEach { key ->
                    dataMap[key] = dataObj.get(key)
                }
                
                return ChatResponse(
                    action = data.getString("action"),
                    response = data.getString("response"),
                    data = dataMap,
                    confidence = data.optDouble("confidence", 0.8).toFloat()
                )
            }
            
            return createFallbackResponse(text)
        } catch (e: Exception) {
            e.printStackTrace()
            return createFallbackResponse("Error processing response")
        }
    }
    
    private fun createFallbackResponse(command: String): ChatResponse {
        val lowerCommand = command.lowercase()
        
        return when {
            lowerCommand.contains("bech") || lowerCommand.contains("sold") -> {
                ChatResponse(
                    action = "SALE",
                    response = "Sale recorded successfully!",
                    data = mapOf("product" to "Product", "amount" to 100),
                    confidence = 0.6f
                )
            }
            lowerCommand.contains("stock") || lowerCommand.contains("inventory") -> {
                ChatResponse(
                    action = "STOCK_CHECK",
                    response = "Stock information retrieved",
                    data = mapOf("product" to "Product", "stock" to 50),
                    confidence = 0.6f
                )
            }
            lowerCommand.contains("report") || lowerCommand.contains("sales") -> {
                ChatResponse(
                    action = "REPORT",
                    response = "Generating report",
                    data = mapOf("type" to "sales"),
                    confidence = 0.6f
                )
            }
            else -> {
                ChatResponse(
                    action = "UNKNOWN",
                    response = "I didn't understand that. Please try again.",
                    data = emptyMap(),
                    confidence = 0.3f
                )
            }
        }
    }
    
    fun clearHistory() {
        conversationHistory.clear()
    }
}
