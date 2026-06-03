package droga_krzyzowa.droga_krzyzowa.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import droga_krzyzowa.droga_krzyzowa.remote.NewsResponse
import droga_krzyzowa.droga_krzyzowa.remote.RetrofitClient
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)

    private val _newsList = mutableStateOf<List<NewsResponse>>(emptyList())
    val newsList: State<List<NewsResponse>> = _newsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _hasUnreadNews = mutableStateOf(false)
    val hasUnreadNews: State<Boolean> = _hasUnreadNews

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getNewsFeed()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _newsList.value = list
                    checkUnreadStatus(list) // Sprawdzamy czy są nowe posty [cite: 2026-02-17]
                }
            } catch (e: Exception) {
                // Logowanie błędów
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Porównujemy ID z serwera z ID zapisanym lokalnie [cite: 2026-02-17]
    private fun checkUnreadStatus(list: List<NewsResponse>) {
        val lastReadId = prefs.getInt("last_read_id", 0)
        val newestId = list.firstOrNull()?.id ?: 0
        _hasUnreadNews.value = newestId > lastReadId
    }

    // Wywołujemy to w NewsFeedScreen, gdy użytkownik zobaczy listę [cite: 2026-02-17]
    fun markAllAsRead() {
        val newestId = _newsList.value.firstOrNull()?.id ?: 0
        if (newestId > 0) {
            prefs.edit().putInt("last_read_id", newestId).apply()
            _hasUnreadNews.value = false
        }
    }
}