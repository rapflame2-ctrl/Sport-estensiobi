package recloudstream

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.newLiveSearchResponse
import org.json.JSONObject

class DiretteItaliaProvider : MainAPI() {
    override var mainUrl = "https://mercato.direttecommunity.online"
    override var name = "DiretteItalia"
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "it"
    override val hasMainPage = true

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val response = app.get("$mainUrl/api/events.php")
        val json = JSONObject(response.text)
        val events = json.optJSONArray("events") ?: return newHomePageResponse(emptyList())

        val items = (0 until events.length()).mapNotNull { i ->
            val event = events.optJSONObject(i) ?: return@mapNotNull null
            val title = event.optString("title").takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            newLiveSearchResponse(
                title,
                "$mainUrl/",
                TvType.Live
            )
        }

        return newHomePageResponse(
            listOf(HomePageList("Eventi sportivi", items)),
            hasNext = false
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        throw IllegalStateException(
            "Apri l'evento dal sito ufficiale per verificare i flussi disponibili."
        )
    }
}
