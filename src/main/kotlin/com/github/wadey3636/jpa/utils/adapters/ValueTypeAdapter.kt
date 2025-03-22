package com.github.wadey3636.jpa.utils.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ValueTypeAdapter : TypeAdapter<Map<String, Float>>() {
    override fun write(p0: JsonWriter?, p1: Map<String, Float>?) {
        TODO("Not yet implemented")
    }

    override fun read(p0: JsonReader?): Map<String, Float> {
        TODO("Not yet implemented")
    }
}