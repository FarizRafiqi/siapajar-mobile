package id.siapajar.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import id.siapajar.app.data.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

object ImageCompressor {
    private const val TAG = "ImageCompressor"

    /**
     * Mengompresi file foto lokal berdasarkan preferensi kualitas (Kompresi Cepat, Standar HD, Kualitas Asli).
     */
    suspend fun compressFile(
        context: Context,
        sourceFile: File,
        customQuality: String? = null
    ): File = withContext(Dispatchers.IO) {
        val quality = customQuality ?: TokenManager.getInstance(context).getPhotoQuality()
        if (quality == "Kualitas Asli") {
            return@withContext sourceFile
        }

        try {
            val (maxDimension, jpegQuality) = getQualityParams(quality)
            val photosDir = File(context.cacheDir, "compressed_photos").apply { mkdirs() }
            val outputFile = File(photosDir, "compressed_${System.currentTimeMillis()}_${sourceFile.name}")

            val orientation = getExifOrientation(sourceFile.absolutePath)
            val bitmap = decodeSampledBitmap(sourceFile.absolutePath, maxDimension, orientation)

            if (bitmap != null) {
                FileOutputStream(outputFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, out)
                }
                bitmap.recycle()
                Log.d(TAG, "Compressed: ${sourceFile.length() / 1024}KB -> ${outputFile.length() / 1024}KB ($quality)")
                return@withContext outputFile
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to compress file, using original", e)
        }
        return@withContext sourceFile
    }

    /**
     * Mengompresi gambar dari Android Uri (misal: Galeri) ke file JPEG lokal yang sudah teroptimasi.
     */
    suspend fun compressUri(
        context: Context,
        sourceUri: Uri,
        customQuality: String? = null
    ): File = withContext(Dispatchers.IO) {
        val quality = customQuality ?: TokenManager.getInstance(context).getPhotoQuality()
        val photosDir = File(context.cacheDir, "compressed_photos").apply { mkdirs() }
        val outputFile = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")

        try {
            val (maxDimension, jpegQuality) = getQualityParams(quality)
            
            // 1. Dapatkan orientasi EXIF dari Uri Stream jika didukung
            var orientation = ExifInterface.ORIENTATION_NORMAL
            try {
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    val exif = ExifInterface(input)
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                }
            } catch (_: Exception) {}

            // 2. Decode & Scale Bitmap
            val bitmap = decodeSampledBitmapFromUri(context, sourceUri, maxDimension, orientation)
            if (bitmap != null) {
                FileOutputStream(outputFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, out)
                }
                bitmap.recycle()
                Log.d(TAG, "Compressed Uri: ${outputFile.length() / 1024}KB ($quality)")
                return@withContext outputFile
            }

            // Fallback: Copy stream biasa jika decode gagal
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to compress Uri, fallback to direct copy", e)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        return@withContext outputFile
    }

    private fun getQualityParams(quality: String): Pair<Int, Int> {
        return when (quality) {
            "Standar HD" -> Pair(1920, 85)
            "Kualitas Asli" -> Pair(3840, 95)
            else -> Pair(1080, 70) // Kompresi Cepat (Default)
        }
    }

    private fun getExifOrientation(filePath: String): Int {
        return try {
            val exif = ExifInterface(filePath)
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    private fun decodeSampledBitmap(
        filePath: String,
        maxDimension: Int,
        orientation: Int
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(filePath, options)

        val srcWidth = options.outWidth
        val srcHeight = options.outHeight
        if (srcWidth <= 0 || srcHeight <= 0) return null

        options.inSampleSize = calculateInSampleSize(srcWidth, srcHeight, maxDimension)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565 // Menghemat memory 50%

        val decodedBitmap = BitmapFactory.decodeFile(filePath, options) ?: return null
        return transformBitmap(decodedBitmap, maxDimension, orientation)
    }

    private fun decodeSampledBitmapFromUri(
        context: Context,
        uri: Uri,
        maxDimension: Int,
        orientation: Int
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        }

        val srcWidth = options.outWidth
        val srcHeight = options.outHeight
        if (srcWidth <= 0 || srcHeight <= 0) return null

        options.inSampleSize = calculateInSampleSize(srcWidth, srcHeight, maxDimension)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565

        val decodedBitmap = context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        } ?: return null

        return transformBitmap(decodedBitmap, maxDimension, orientation)
    }

    private fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, maxDimension: Int): Int {
        var inSampleSize = 1
        val maxSrc = max(srcWidth, srcHeight)
        if (maxSrc > maxDimension) {
            val halfMax = maxSrc / 2
            while ((halfMax / inSampleSize) >= maxDimension) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun transformBitmap(
        bitmap: Bitmap,
        maxDimension: Int,
        orientation: Int
    ): Bitmap {
        val matrix = Matrix()

        // Handle rotation from EXIF
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        }

        // Scale down if still larger than maxDimension
        val currentMax = max(bitmap.width, bitmap.height)
        if (currentMax > maxDimension) {
            val scale = maxDimension.toFloat() / currentMax.toFloat()
            matrix.postScale(scale, scale)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
