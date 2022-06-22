package com.su.mediabox.viewmodel

import androidx.lifecycle.ViewModel
import com.su.mediabox.database.getAppDataBase

class MediaDataViewModel : ViewModel() {
    val favorite = getAppDataBase().favoriteDao().getFavoriteListLiveData()
    val history = getAppDataBase().historyDao().getHistoryListLiveData()
}