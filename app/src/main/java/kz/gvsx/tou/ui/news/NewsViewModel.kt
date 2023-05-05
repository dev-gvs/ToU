package kz.gvsx.tou.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@HiltViewModel
class NewsViewModel @Inject constructor() : ViewModel() {

    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            val doc = getDoc()
            doc.selectFirst("div.row > div.col.s12 > div.news-list")?.let { newsDiv ->
                val news = newsDiv.select("div.row").map { row ->
                    val titleDiv = row.selectFirst("div.news-list-title")

                    val imageUrl = row
                        .selectFirst("div.news-list-image")
                        ?.selectFirst("img")
                        ?.attr("abs:src") ?: ""
                    val title = titleDiv
                        ?.selectFirst("a")
                        ?.text() ?: ""
                    val dateTime = titleDiv
                        ?.selectFirst("div.date")
                        ?.text()
                    val linkToArticle = "https://tou.edu.kz/ru" + titleDiv
                        ?.selectFirst("a")
                        ?.attr("href")

                    News(
                        imageUrl,
                        title,
                        LocalDateTime.from(dateTimeFormatter.parse(dateTime)),
                        linkToArticle
                    )
                }
                _news.postValue(news)
            }
        }
    }

    private suspend fun getDoc(): Document = withContext(Dispatchers.IO) {
        Jsoup
            .connect("https://tou.edu.kz/ru/component/news")
            .ignoreHttpErrors(true)
            .get()
    }
}
