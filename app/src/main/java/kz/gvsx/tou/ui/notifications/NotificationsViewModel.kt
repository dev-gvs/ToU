package kz.gvsx.tou.ui.notifications

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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            val doc = getDoc()
            doc.selectFirst("div.notification")?.let { notificationsDiv ->
                val notifications = notificationsDiv.children().map { notification ->
                    notification.selectFirst("div.introtext")?.let { element ->
                        val date = notification.selectFirst("div.details > div > span")?.text()
                        // Convert relative paths into absolute.
                        element.select("a[href]").forEach { url ->
                            url.attr("href", url.absUrl("href"))
                        }
                        Notification(element, LocalDate.parse(date))
                    }
                }
                _notifications.postValue(notifications.filterNotNull())
            }
        }
    }

    private suspend fun getDoc(): Document = withContext(Dispatchers.IO) {
        Jsoup.connect("https://tou.edu.kz/ru/component/notifications").timeout(5000).get()
    }
}