package com.aidukan.kirana.whatsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class WhatsAppMessage(
    val phoneNumber: String,
    val message: String,
    val attachmentPath: String? = null
)

object WhatsAppService {
    
    /**
     * Send bill via WhatsApp
     */
    fun sendBill(context: Context, phoneNumber: String, billDetails: Map<String, Any>) {
        val message = formatBillMessage(billDetails)
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send payment reminder via WhatsApp
     */
    fun sendPaymentReminder(context: Context, phoneNumber: String, customerName: String, amount: Int, daysOverdue: Int) {
        val message = """
            नमस्ते $customerName जी,
            
            आपका बकाया भुगतान:
            राशि: ₹$amount
            बकाया दिन: $daysOverdue
            
            कृपया जल्द से जल्द भुगतान करें।
            
            धन्यवाद,
            Aidukan Store
        """.trimIndent()
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send daily report via WhatsApp
     */
    fun sendDailyReport(context: Context, phoneNumber: String, reportData: Map<String, Any>) {
        val message = formatDailyReport(reportData)
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send low stock alert via WhatsApp
     */
    fun sendLowStockAlert(context: Context, phoneNumber: String, products: List<Map<String, Any>>) {
        val message = formatLowStockAlert(products)
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send custom message via WhatsApp
     */
    fun sendMessage(context: Context, phoneNumber: String, message: String) {
        try {
            // Format phone number (remove spaces, add country code if needed)
            val formattedNumber = formatPhoneNumber(phoneNumber)
            
            // Create WhatsApp intent
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$formattedNumber?text=${Uri.encode(message)}")
                setPackage("com.whatsapp")
            }
            
            // Check if WhatsApp is installed
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // WhatsApp not installed, try WhatsApp Business
                intent.setPackage("com.whatsapp.w4b")
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    // Neither installed, open in browser
                    intent.setPackage(null)
                    context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Send message with attachment (PDF bill, etc.)
     */
    fun sendMessageWithAttachment(context: Context, phoneNumber: String, message: String, filePath: String) {
        try {
            val formattedNumber = formatPhoneNumber(phoneNumber)
            val file = File(filePath)
            
            if (!file.exists()) {
                sendMessage(context, phoneNumber, message)
                return
            }
            
            val uri = Uri.fromFile(file)
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra("jid", "$formattedNumber@s.whatsapp.net")
                setPackage("com.whatsapp")
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback to message without attachment
                sendMessage(context, phoneNumber, message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sendMessage(context, phoneNumber, message)
        }
    }
    
    /**
     * Format bill message
     */
    private fun formatBillMessage(billDetails: Map<String, Any>): String {
        val customerName = billDetails["customerName"] as? String ?: "Customer"
        val items = billDetails["items"] as? List<Map<String, Any>> ?: emptyList()
        val subtotal = billDetails["subtotal"] as? Int ?: 0
        val gst = billDetails["gst"] as? Int ?: 0
        val total = billDetails["total"] as? Int ?: 0
        val billNumber = billDetails["billNumber"] as? String ?: "N/A"
        
        val itemsText = items.joinToString("\n") { item ->
            val name = item["name"] as? String ?: ""
            val qty = item["quantity"] as? Int ?: 0
            val price = item["price"] as? Int ?: 0
            val itemTotal = item["total"] as? Int ?: 0
            "$name x$qty @ ₹$price = ₹$itemTotal"
        }
        
        return """
            🧾 *BILL RECEIPT*
            
            Bill No: $billNumber
            Customer: $customerName
            Date: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date())}
            
            *ITEMS:*
            $itemsText
            
            ─────────────────
            Subtotal: ₹$subtotal
            GST (5%): ₹$gst
            *TOTAL: ₹$total*
            ─────────────────
            
            Thank you for shopping with us! 🙏
            
            *Aidukan Store*
            Powered by AI
        """.trimIndent()
    }
    
    /**
     * Format daily report message
     */
    private fun formatDailyReport(reportData: Map<String, Any>): String {
        val date = reportData["date"] as? String ?: java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())
        val totalSales = reportData["totalSales"] as? Int ?: 0
        val totalProfit = reportData["totalProfit"] as? Int ?: 0
        val itemsSold = reportData["itemsSold"] as? Int ?: 0
        val topProduct = reportData["topProduct"] as? String ?: "N/A"
        
        return """
            📊 *DAILY SALES REPORT*
            Date: $date
            
            💰 Total Sales: ₹$totalSales
            📈 Profit: ₹$totalProfit
            📦 Items Sold: $itemsSold
            🏆 Top Product: $topProduct
            
            Great work today! 🎉
            
            *Aidukan Store*
        """.trimIndent()
    }
    
    /**
     * Format low stock alert message
     */
    private fun formatLowStockAlert(products: List<Map<String, Any>>): String {
        val productList = products.joinToString("\n") { product ->
            val name = product["name"] as? String ?: ""
            val stock = product["stock"] as? Int ?: 0
            val minStock = product["minStock"] as? Int ?: 0
            "• $name: $stock units (Min: $minStock)"
        }
        
        return """
            ⚠️ *LOW STOCK ALERT*
            
            The following items are running low:
            
            $productList
            
            Please reorder soon!
            
            *Aidukan Store*
        """.trimIndent()
    }
    
    /**
     * Format phone number for WhatsApp
     */
    private fun formatPhoneNumber(phoneNumber: String): String {
        var formatted = phoneNumber.replace(Regex("[^0-9]"), "")
        
        // Add country code if not present
        if (!formatted.startsWith("91") && formatted.length == 10) {
            formatted = "91$formatted"
        }
        
        return formatted
    }
    
    /**
     * Check if WhatsApp is installed
     */
    fun isWhatsAppInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.whatsapp", 0)
            true
        } catch (e: Exception) {
            try {
                context.packageManager.getPackageInfo("com.whatsapp.w4b", 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
