package eu.delath.animenotifier.network

import com.google.gson.annotations.SerializedName

data class AnilistResponse(
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("Page") val page: Page
    ) {
        data class Page(
            @SerializedName("media") val media: List<Media>
        ) {
            data class Media(
                @SerializedName("id") val id: Int,
                @SerializedName("title") val title: Title,
                @SerializedName("coverImage") val coverImage: CoverImage,
                @SerializedName("episodes") val episodes: Int?
            ) {
                data class Title(
                    @SerializedName("romaji") val romaji: String
                )

                data class CoverImage(
                    @SerializedName("extraLarge") val extraLarge: String
                )
            }
        }
    }
}
