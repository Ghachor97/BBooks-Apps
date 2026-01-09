package com.galang.bbooks.util

/**
 * Utility object for handling Google Drive links
 * Converts Google Drive share links to direct download/view links
 */
object GoogleDriveUtils {
    
    /**
     * Converts a Google Drive share link to a direct image URL
     * 
     * Supported formats:
     * - https://drive.google.com/file/d/FILE_ID/view?usp=sharing
     * - https://drive.google.com/open?id=FILE_ID
     * - https://drive.google.com/uc?id=FILE_ID
     * 
     * Returns the direct download URL that can be used with image loaders
     */
    fun convertToDriveDirectUrl(url: String): String {
        if (url.isBlank()) return ""
        
        // Already a direct URL or non-Drive URL
        if (!url.contains("drive.google.com")) {
            return url
        }
        
        // Extract file ID from various Google Drive URL formats
        val fileId = extractFileId(url) ?: return url
        
        // Return thumbnail URL which works better for viewing
        // Using export=view for direct viewing
        return "https://drive.google.com/uc?export=view&id=$fileId"
    }
    
    /**
     * Extracts the file ID from various Google Drive URL formats
     */
    private fun extractFileId(url: String): String? {
        // Format: /file/d/FILE_ID/
        val filePattern = Regex("/file/d/([a-zA-Z0-9_-]+)")
        filePattern.find(url)?.let {
            return it.groupValues[1]
        }
        
        // Format: ?id=FILE_ID or &id=FILE_ID
        val idPattern = Regex("[?&]id=([a-zA-Z0-9_-]+)")
        idPattern.find(url)?.let {
            return it.groupValues[1]
        }
        
        // Format: /open?id=FILE_ID
        val openPattern = Regex("/open\\?id=([a-zA-Z0-9_-]+)")
        openPattern.find(url)?.let {
            return it.groupValues[1]
        }
        
        return null
    }
    
    /**
     * Validates if a URL is a valid Google Drive link
     */
    fun isGoogleDriveUrl(url: String): Boolean {
        return url.contains("drive.google.com") && extractFileId(url) != null
    }
}
