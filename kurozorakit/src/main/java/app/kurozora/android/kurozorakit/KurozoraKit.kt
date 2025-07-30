package app.kurozora.android.kurozorakit

import android.os.Build
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class KurozoraKit(
    val debugURL: String? = null,
    var authenticationKey: String = "",
    var services: KKServices = KKServices(),
) {
    // Properties
    private val baseUrl = "https://api.kurozora.app/v1/"

    // Define your Retrofit instance
    val retrofit: Retrofit

    init {
        // Define your OkHttp client with headers
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .header("X-API-Key", "")
                    .header("User-Agent", this.getUserAgent())
                // Add authentication header if authenticationKey is set
                if (this.authenticationKey.isNotEmpty()) {
                    requestBuilder.header("Authorization", this.authenticationKey)
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
        // Initialize Retrofit with GsonConverterFactory and your base URL
        val networkJson = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        this.retrofit = Retrofit.Builder()
            .baseUrl(this.debugURL ?: this.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
    }
    // MARK: - Functions
    /**
     * Sets the `authenticationKey` property with the given auth key.
     *
     *  @param authKey The current user's authentication key.
     *  @return Reference to `self`.
     */
    fun authenticationKey(authKey: String): KurozoraKit {
        this.authenticationKey = authKey
        return this
    }

    /**
     * Sets the `services` property with the given [KKServices](x-source-tag://KKServices) object.
     *
     * @parameter services The [KKServices](x-source-tag://KKServices) object to be used when performing API requests.
     * @Return Reference to `self`.
     */
    fun services(services: KKServices): KurozoraKit {
        this.services = services
        return this
    }

    private fun getUserAgent(): String {
        val osVersion = Build.VERSION.RELEASE
        val buildNumber = Build.VERSION.INCREMENTAL
        val bundleID = "app.kurozora.android"
        val appVersion = "1.12.3"
        val clientVersion = OkHttp.VERSION
        return "Kurozora/$appVersion ($bundleID; build:$buildNumber; Android $osVersion) okhttp/$clientVersion"
    }
}
