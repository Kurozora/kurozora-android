package app.kurozora.tracker.api

import app.kurozora.tracker.api.response.RelationshipsResponse
import app.kurozora.tracker.api.response.ShowListResponse
import app.kurozora.tracker.api.response.game.GameResponse
import app.kurozora.tracker.api.response.show.ShowResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class RelationshipDeserializer: TypeAdapter<RelationshipsResponse?>() {
    val gson = Gson()

    override fun write(out: JsonWriter?, value: RelationshipsResponse?) {
        gson.toJson(value, RelationshipsResponse::class.java, out)
    }

    override fun read(reader: JsonReader): RelationshipsResponse? {
        var result = null
        val response = RelationshipsResponse(null, null)
        reader.beginObject()
        var fieldName: String? = null

        while (reader.hasNext()) {
            var token = reader.peek()

            if (token.equals(JsonToken.NAME)) {
                fieldName = reader.nextName()
            }

            if (fieldName == "games") {
                token = reader.peek()
                val listType = object : TypeToken<ShowListResponse<GameResponse?>?>() {}.type
                val test = gson.fromJson(reader, listType) as ShowListResponse<GameResponse>
                response.games = test
            }

            if (fieldName == "shows") {
                token = reader.peek()
                val listType = object : TypeToken<ShowListResponse<ShowResponse?>?>() {}.type
                val test = gson.fromJson(reader, listType) as ShowListResponse<ShowResponse>
                response.shows = test
            }
        }

        return result
    }
}
