package id.siapajar.app.data.remote

import android.content.Context
import id.siapajar.app.BuildConfig
import id.siapajar.app.data.local.TokenManager
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var currentBaseUrl: String? = null
    private var retrofitInstance: Retrofit? = null
    private var apiServiceInstance: SiapAjarApiService? = null

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    fun getApiService(context: Context): SiapAjarApiService {
        val tokenManager = TokenManager.getInstance(context)
        val targetBaseUrl = tokenManager.getBaseUrl()

        if (apiServiceInstance != null && currentBaseUrl == targetBaseUrl) {
            return apiServiceInstance!!
        }

        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Accept", "application/json")

            val token = tokenManager.getToken()
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }

        val fallbackInterceptor = Interceptor { chain ->
            val request = chain.request()
            try {
                val response = chain.proceed(request)
                // If primary host returns 5xx server error, try fallback domain if available
                if (!response.isSuccessful && response.code >= 500 && BuildConfig.FALLBACK_BASE_URL.isNotBlank()) {
                    val fallbackHttpUrl = BuildConfig.FALLBACK_BASE_URL.toHttpUrlOrNull()
                    if (fallbackHttpUrl != null && request.url.host != fallbackHttpUrl.host) {
                        response.close()
                        val newUrl = request.url.newBuilder()
                            .scheme(fallbackHttpUrl.scheme)
                            .host(fallbackHttpUrl.host)
                            .port(fallbackHttpUrl.port)
                            .build()
                        return@Interceptor chain.proceed(request.newBuilder().url(newUrl).build())
                    }
                }
                response
            } catch (e: Exception) {
                // If connection or DNS fails on primary host, seamlessly retry on fallback host
                val fallbackHttpUrl = BuildConfig.FALLBACK_BASE_URL.toHttpUrlOrNull()
                if (fallbackHttpUrl != null && request.url.host != fallbackHttpUrl.host) {
                    val newUrl = request.url.newBuilder()
                        .scheme(fallbackHttpUrl.scheme)
                        .host(fallbackHttpUrl.host)
                        .port(fallbackHttpUrl.port)
                        .build()
                    chain.proceed(request.newBuilder().url(newUrl).build())
                } else {
                    throw e
                }
            }
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(fallbackInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl(targetBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        currentBaseUrl = targetBaseUrl
        retrofitInstance = retrofit
        val service = retrofit.create(SiapAjarApiService::class.java)
        apiServiceInstance = service
        return service
    }
}
