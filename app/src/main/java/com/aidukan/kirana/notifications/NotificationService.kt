package com.aidukan.kirana.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aidukan.kirana.R
import com.aidukan.kirana.ui.MainActivity

object NotificationService {
    
    private const val CHANNEL_ID_SALES = "sales_channel"
    private const val CHANNEL_ID_INVENTORY = "inventory_channel"
    private const val CHANNEL_ID_REPORTS = "reports_channel"
    private const val CHANNEL_ID_PAYMENTS = "payments_channel"
    private const val CHANNEL_ID_GENERAL = "general_channel"
    
    /**
     * Initialize notification channels
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_SALES,
                    "Sales Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications about sales and transactions"
                },
                NotificationChannel(
                    CHANNEL_ID_INVENTORY,
                    "Inventory Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Low stock and inventory alerts"
                },
                NotificationChannel(
                    CHANNEL_ID_REPORTS,
                    "Reports",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Daily and weekly reports"
                },
                NotificationChannel(
                    CHANNEL_ID_PAYMENTS,
                    "Payment Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Customer payment reminders"
                },
                NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "General notifications"
                }
            )
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * Show daily sales summary notification
     */
    fun showDailySalesSummary(context: Context, totalSales: Int, totalProfit: Int, itemsSold: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("📊 Daily Sales Summary")
            .setContentText("Sales: ₹$totalSales | Profit: ₹$totalProfit | Items: $itemsSold")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Today's Performance:\n\n" +
                        "💰 Total Sales: ₹$totalSales\n" +
                        "📈 Profit: ₹$totalProfit\n" +
                        "📦 Items Sold: $itemsSold\n\n" +
                        "Great work! Keep it up! 🎉"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
    
    /**
     * Show low stock alert
     */
    fun showLowStockAlert(context: Context, productName: String, currentStock: Int, minStock: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_INVENTORY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⚠️ Low Stock Alert")
            .setContentText("$productName is running low!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$productName\n\n" +
                        "Current Stock: $currentStock units\n" +
                        "Minimum Required: $minStock units\n\n" +
                        "Please reorder soon!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(productName.hashCode(), notification)
    }
    
    /**
     * Show payment reminder
     */
    fun showPaymentReminder(context: Context, customerName: String, amount: Int, daysOverdue: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_PAYMENTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("💳 Payment Reminder")
            .setContentText("$customerName owes ₹$amount")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Payment Due:\n\n" +
                        "Customer: $customerName\n" +
                        "Amount: ₹$amount\n" +
                        "Overdue: $daysOverdue days\n\n" +
                        "Tap to send reminder"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(customerName.hashCode(), notification)
    }
    
    /**
     * Show AI business tip
     */
    fun showBusinessTip(context: Context, tip: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("💡 AI Business Tip")
            .setContentText(tip)
            .setStyle(NotificationCompat.BigTextStyle().bigText(tip))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(2001, notification)
    }
    
    /**
     * Show backup completion notification
     */
    fun showBackupComplete(context: Context, itemsBackedUp: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("☁️ Backup Complete")
            .setContentText("$itemsBackedUp items backed up successfully")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(3001, notification)
    }
    
    /**
     * Show weekly report notification
     */
    fun showWeeklyReport(context: Context, totalSales: Int, topProduct: String, growth: Float) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val growthText = if (growth >= 0) "+$growth%" else "$growth%"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("📈 Weekly Report")
            .setContentText("Sales: ₹$totalSales | Growth: $growthText")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("This Week's Performance:\n\n" +
                        "💰 Total Sales: ₹$totalSales\n" +
                        "📈 Growth: $growthText\n" +
                        "🏆 Top Product: $topProduct\n\n" +
                        "Tap to view detailed report"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(1002, notification)
    }
}
