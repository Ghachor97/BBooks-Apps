package com.galang.bbooks.util

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Singleton ImageLoader with aggressive caching for book covers
 */
object ImageLoaderFactory {
    
    @Volatile
    private var imageLoader: ImageLoader? = null
    
    fun getImageLoader(context: Context): ImageLoader {
        return imageLoader ?: synchronized(this) {
            imageLoader ?: createImageLoader(context).also { imageLoader = it }
        }
    }
    
    private fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory cache - keeps decoded images in RAM
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // Use 25% of app memory for cache
                    .build()
            }
            // Disk cache - saves images to storage
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100 MB disk cache
                    .build()
            }
            // Enable aggressive caching
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            // Respect cache headers but with longer timeout
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
