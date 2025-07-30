package app.kurozora.tracker.api

import app.kurozora.tracker.api.response.GameResponse
import app.kurozora.tracker.api.response.RelationshipsResponse
import app.kurozora.tracker.api.response.ShowListResponse
import app.kurozora.tracker.api.response.ShowResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter


class RelationshipDeserializer : TypeAdapter<RelationshipsResponse?>() {
    val gson = Gson()
    override fun write(out: JsonWriter?, value: RelationshipsResponse?) {
       gson.toJson(value , RelationshipsResponse::class.java , out)
    }

    override fun read(reader: JsonReader): RelationshipsResponse? {
        var result = null
        val response = RelationshipsResponse(null )
        reader.beginObject()
        var fieldname : String? = null

        while (reader.hasNext()){
            var token = reader.peek()
            if(token.equals(JsonToken.NAME)){
                fieldname = reader.nextName()
            }

          /*  if ("games".equals(fieldname)){
                token = reader.peek()
                val listType = object : TypeToken<ShowListResponse<GameResponse?>?>() {}.type
                val test = gson.fromJson(reader, listType) as ShowListResponse<GameResponse>
                response.games = test
            }*/
            if ("shows".equals(fieldname)){
                token = reader.peek()
                val listType = object : TypeToken<ShowListResponse<ShowResponse?>?>() {}.type
                val test = gson.fromJson(reader, listType) as ShowListResponse<ShowResponse>
                response.shows = test
            }
        }
        return result
    }

}