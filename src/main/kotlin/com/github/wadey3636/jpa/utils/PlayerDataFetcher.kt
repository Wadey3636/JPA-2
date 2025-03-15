package com.github.wadey3636.jpa.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.modcore.Core.logger
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object PlayerDataFetcher {
    fun getPlayerSkinURL(playerName: String): String? {
        try {
            val uuid = getUUID(playerName) ?: return null
            val profileURL = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid")
            val profileConnection = profileURL.openConnection() as HttpURLConnection
            profileConnection.requestMethod = "GET"

            val profileReader = InputStreamReader(profileConnection.inputStream).use { it.readText() }
            val profileResponse = JsonParser().parse(profileReader).asJsonObject
            val base64 = profileResponse.getAsJsonArray("properties")[0].asJsonObject["value"].asString

            val json = String(Base64.getDecoder().decode(base64))
            val textures = JsonParser().parse(json).asJsonObject.getAsJsonObject("textures")
            return textures.getAsJsonObject("SKIN")["url"].asString
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun getUUID(playerName: String): UUID? {
        try {
            val uuidURL = URL("https://api.mojang.com/users/profiles/minecraft/$playerName")
            val uuidConnection = uuidURL.openConnection() as HttpURLConnection
            uuidConnection.requestMethod = "GET"

            val uuidReader = InputStreamReader(uuidConnection.inputStream).use { it.readText() }
            val uuidResponse = JsonParser().parse(uuidReader).asJsonObject
            return UUID.fromString(
                uuidResponse["id"]
                    .asString
                    .replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                        "$1-$2-$3-$4-$5"
                    )
            )

        } catch (e: Exception) {
            logger.error("Exception fetching UUID", e)
            return null
        }

    }
}
