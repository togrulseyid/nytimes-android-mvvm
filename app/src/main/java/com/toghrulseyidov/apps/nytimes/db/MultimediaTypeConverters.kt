package com.toghrulseyidov.apps.nytimes.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toghrulseyidov.apps.nytimes.model.Multimedia
import java.lang.reflect.Type
import java.util.*


class MultimediaTypeConverters {
    private var gson = Gson()

    @TypeConverter
    fun stringToMultimediaList(data: String?): List<Multimedia?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Multimedia?>?>() {}.type
        return gson.fromJson<List<Multimedia?>>(data, listType)
    }

    @TypeConverter
    fun multimediaListToString(someObjects: List<Multimedia?>?): String? {
        return gson.toJson(someObjects)
    }

}