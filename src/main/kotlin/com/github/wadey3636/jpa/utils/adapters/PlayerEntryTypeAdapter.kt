import com.github.wadey3636.jpa.features.render.PlayerEntry
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.mojang.authlib.GameProfile
import java.io.IOException
import java.util.*

class PlayerEntryTypeAdapter : TypeAdapter<PlayerEntry?>() {


    @Throws(IOException::class)
    override fun write(out: JsonWriter, playerEntry: PlayerEntry?) {
        if (playerEntry == null) {
            out.nullValue()
            return
        }

        out.beginObject()
        out.name("name").value(playerEntry.name)
        out.name("entryX").value(playerEntry.entryX)
        out.name("entryY").value(playerEntry.entryY)
        out.name("entryZ").value(playerEntry.entryZ)
        out.name("toggleTexture").value(playerEntry.toggleTexture)

        playerEntry.texture?.let {
            out.name("texture").value(it.toString())
        } ?: out.name("texture").nullValue()

        out.name("dinnerBone").value(playerEntry.dinnerBone)
        out.name("hideHelmet").value(playerEntry.hideHelmet)
        out.name("hideChestplate").value(playerEntry.hideChestplate)
        out.name("hideLeggings").value(playerEntry.hideLeggings)
        out.name("hideBoots").value(playerEntry.hideBoots)
        out.name("toggle").value(playerEntry.toggle)

        // Serialize GameProfile
        playerEntry.profile?.let { profile ->
            out.name("profile").beginObject()
            out.name("id").value(profile.id?.toString())
            out.name("name").value(profile.name)
            out.endObject()
        } ?: out.name("profile").nullValue()

        out.endObject()
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): PlayerEntry {
        var name = ""
        var entryX = 0.0
        var entryY = 0.0
        var entryZ = 0.0
        var toggleTexture: Boolean = false
        var texture: String? = null
        var dinnerBone: Boolean = false
        var hideHelmet = false
        var hideChestplate = false
        var hideLeggings = false
        var hideBoots = false
        var toggle = false
        var profile: GameProfile? = null

        `in`.beginObject()
        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "name" -> name = `in`.nextString()
                "entryX" -> entryX = `in`.nextDouble()
                "entryY" -> entryY = `in`.nextDouble()
                "entryZ" -> entryZ = `in`.nextDouble()
                "toggleTexture" -> toggleTexture = `in`.nextBoolean()
                "texture" -> texture = `in`.nextString()
                "dinnerBone" -> dinnerBone = `in`.nextBoolean()
                "hideHelmet" -> hideHelmet = `in`.nextBoolean()
                "hideChestplate" -> hideChestplate = `in`.nextBoolean()
                "hideLeggings" -> hideLeggings = `in`.nextBoolean()
                "hideBoots" -> hideBoots = `in`.nextBoolean()
                "toggle" -> toggle = `in`.nextBoolean()
                "profile" -> {
                    `in`.beginObject()
                    var id: UUID? = null
                    var profileName: String? = null
                    while (`in`.hasNext()) {
                        when (`in`.nextName()) {
                            "id" -> id = UUID.fromString(`in`.nextString())
                            "name" -> profileName = `in`.nextString()
                        }
                    }
                    `in`.endObject()
                    if (id != null && profileName != null) {
                        profile = GameProfile(id, profileName)
                    }
                }

                else -> `in`.skipValue()
            }
        }
        `in`.endObject()

        return PlayerEntry(
            name,
            entryX,
            entryY,
            entryZ,
            toggleTexture,
            texture,
            dinnerBone,
            hideHelmet,
            hideChestplate,
            hideLeggings,
            hideBoots,
            toggle,
            profile
        )
    }
}