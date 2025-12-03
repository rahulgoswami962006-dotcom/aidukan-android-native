package com.aidukan.kirana.utils

data class VoiceCommandResult(
    val action: String,
    val response: String,
    val data: Map<String, Any>
)

object VoiceCommandProcessor {
    
    fun process(command: String): VoiceCommandResult {
        val lowerCommand = command.lowercase()
        
        // Sale commands
        when {
            containsSaleKeywords(lowerCommand) -> {
                return processSaleCommand(command)
            }
            containsStockKeywords(lowerCommand) -> {
                return processStockCommand(command)
            }
            containsReportKeywords(lowerCommand) -> {
                return processReportCommand(command)
            }
            containsExpenseKeywords(lowerCommand) -> {
                return processExpenseCommand(command)
            }
            else -> {
                return VoiceCommandResult(
                    action = "UNKNOWN",
                    response = "Sorry, I didn't understand that command",
                    data = emptyMap()
                )
            }
        }
    }
    
    private fun containsSaleKeywords(command: String): Boolean {
        val keywords = listOf("bech", "becha", "sold", "sale", "rupaye", "rupees")
        return keywords.any { command.contains(it) }
    }
    
    private fun containsStockKeywords(command: String): Boolean {
        val keywords = listOf("stock", "inventory", "kitna", "bacha", "check")
        return keywords.any { command.contains(it) }
    }
    
    private fun containsReportKeywords(command: String): Boolean {
        val keywords = listOf("report", "sales", "profit", "batao", "dikhao", "total")
        return keywords.any { command.contains(it) }
    }
    
    private fun containsExpenseKeywords(command: String): Boolean {
        val keywords = listOf("expense", "kharcha", "bill", "payment")
        return keywords.any { command.contains(it) }
    }
    
    private fun processSaleCommand(command: String): VoiceCommandResult {
        // Extract amount
        val amountRegex = """(\d+)\s*(rupaye|rupees|rs)""".toRegex()
        val amountMatch = amountRegex.find(command.lowercase())
        val amount = amountMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        
        // Extract product name (simplified)
        val productName = extractProductName(command)
        
        return VoiceCommandResult(
            action = "SALE",
            response = "Sale recorded! $productName - ₹$amount. Stock updated.",
            data = mapOf(
                "product" to productName,
                "amount" to amount,
                "quantity" to 1
            )
        )
    }
    
    private fun processStockCommand(command: String): VoiceCommandResult {
        val productName = extractProductName(command)
        
        // Mock stock data
        val stockLevel = (20..100).random()
        
        return VoiceCommandResult(
            action = "STOCK_CHECK",
            response = "$productName stock: $stockLevel units available",
            data = mapOf(
                "product" to productName,
                "stock" to stockLevel
            )
        )
    }
    
    private fun processReportCommand(command: String): VoiceCommandResult {
        val lowerCommand = command.lowercase()
        
        return when {
            lowerCommand.contains("aaj") || lowerCommand.contains("today") -> {
                VoiceCommandResult(
                    action = "REPORT",
                    response = "Today's total sales: ₹12,450. Profit: ₹2,890",
                    data = mapOf(
                        "period" to "today",
                        "sales" to 12450,
                        "profit" to 2890
                    )
                )
            }
            lowerCommand.contains("week") || lowerCommand.contains("hafte") -> {
                VoiceCommandResult(
                    action = "REPORT",
                    response = "This week's sales: ₹87,200. Profit: ₹18,500",
                    data = mapOf(
                        "period" to "week",
                        "sales" to 87200,
                        "profit" to 18500
                    )
                )
            }
            else -> {
                VoiceCommandResult(
                    action = "REPORT",
                    response = "Opening reports screen",
                    data = emptyMap()
                )
            }
        }
    }
    
    private fun processExpenseCommand(command: String): VoiceCommandResult {
        val amountRegex = """(\d+)\s*(rupaye|rupees|rs)""".toRegex()
        val amountMatch = amountRegex.find(command.lowercase())
        val amount = amountMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        
        return VoiceCommandResult(
            action = "EXPENSE",
            response = "Expense of ₹$amount recorded successfully",
            data = mapOf(
                "amount" to amount,
                "category" to "General"
            )
        )
    }
    
    private fun extractProductName(command: String): String {
        // Common product keywords
        val products = mapOf(
            "fortune" to "Fortune Oil 1L",
            "oil" to "Fortune Oil 1L",
            "tata salt" to "Tata Salt 1kg",
            "salt" to "Tata Salt 1kg",
            "parle" to "Parle-G 100g",
            "biscuit" to "Parle-G 100g",
            "maggi" to "Maggi Noodles",
            "noodles" to "Maggi Noodles",
            "colgate" to "Colgate 200g",
            "toothpaste" to "Colgate 200g"
        )
        
        val lowerCommand = command.lowercase()
        for ((keyword, productName) in products) {
            if (lowerCommand.contains(keyword)) {
                return productName
            }
        }
        
        return "Product"
    }
}
