package kz.gvsx.tou.ui.article

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
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor() : ViewModel() {

    private val _images = MutableLiveData<List<String>>()
    val images: LiveData<List<String>> = _images

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    fun fetchArticle(url: String) {
        viewModelScope.launch {
            val doc = getDoc(url)
            doc.selectFirst("div[itemprop = articleBody]")?.let { article ->
                val images = article.select("img").map { img ->
                    img.attr("abs:src")
                }
                _images.postValue(images)

                val paragraphs = article.select("p").map { p ->
                    p.text()
                }
                _text.postValue(paragraphs.filterNot { it.isBlank() }.joinToString("\n\n"))
            }
        }
    }

    private suspend fun getDoc(url: String): Document = withContext(Dispatchers.IO) {
        Jsoup
            .connect(url)
            .ignoreHttpErrors(true)
            .followRedirects(true)
            .get()
    }
}
