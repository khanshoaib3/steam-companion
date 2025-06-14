package com.github.khanshoaib3.steamcompanion.data.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.khanshoaib3.steamcompanion.R
import com.github.khanshoaib3.steamcompanion.data.local.MainDatabase
import com.github.khanshoaib3.steamcompanion.data.model.detail.PriceTracking
import com.github.khanshoaib3.steamcompanion.data.remote.SteamInternalWebApiService
import com.github.khanshoaib3.steamcompanion.data.repository.OnlineGameDetailRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class PriceCheckWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val db = MainDatabase.getDatabase(appContext)
    val json = Json { ignoreUnknownKeys = true }
    val rer = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl("https://store.steampowered.com")
        .build()
        .create(SteamInternalWebApiService::class.java)

    private val repository = OnlineGameDetailRepository(
        steamInternalWebApiService = rer,
        priceTrackingDao = db.priceTrackingDao()
    )

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return try {
            val trackedGames = repository.getAllTrackedGames()

            for (entry in trackedGames) {
                val currentPrice = fetchCurrentPrice(entry.appId)
                if (currentPrice != null && currentPrice <= entry.targetPrice) {
                    showNotification(entry, currentPrice)
                    if (!entry.notifyEveryDay) {
                        repository.stopTracking(entry.appId)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun fetchCurrentPrice(appId: Int): Float? {
        val result = repository.fetchDataForAppId(appId)
        return if (result?.success == true) {
            result.data?.priceOverview?.finalPrice?.div(100f)
        } else null
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(entry: PriceTracking, currentPrice: Float) {
        try {
            val notificationManager = NotificationManagerCompat.from(appContext)
            val notification = NotificationCompat.Builder(appContext, "price_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Price Drop: ${entry.gameName}")
                .setContentText("Now only ₹$currentPrice!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(entry.appId, notification)
        } catch (e: Exception) {
            Log.e("PriceCheckWorker", "Failed to show notification", e)
        }
    }
}
