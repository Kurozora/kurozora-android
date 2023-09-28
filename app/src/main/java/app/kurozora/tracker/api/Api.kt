package app.kurozora.tracker.api

import app.kurozora.tracker.api.service.ExploreInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    // Create a place for constants
    private val baseUrl = "https://api.kurozora.app/v1/"
    private var gson: Gson = GsonBuilder()
        .serializeNulls()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()
    val apiService : ExploreInterface
    init {
        apiService = getRetrofit(baseUrl).create(ExploreInterface::class.java)
    }
    private fun getRetrofit(baseurl : String) : Retrofit {
        val client = OkHttpClient.Builder()
        client.addInterceptor {
            val original: Request = it.request()
            val requestBuilder: Request.Builder = original.newBuilder()
            requestBuilder.header("Accept", "application/json")
            requestBuilder.header("Content-Type", "application/json")
            val original2 = requestBuilder.build()
            val response = it.proceed(original2)



            response

        }
       // val gsonBuilder = GsonBuilder()
      //  gsonBuilder.registerTypeAdapter(RelationshipDeserializer::class.java, RelationshipDeserializer())
      //  val myGson = gsonBuilder.create()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
        return retrofit.build()
    }

    fun getClient() : Retrofit{
       return  getRetrofit(baseUrl)
    }
}