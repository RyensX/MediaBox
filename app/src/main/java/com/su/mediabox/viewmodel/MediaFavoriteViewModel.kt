package com.su.mediabox.viewmodel

import androidx.lifecycle.ViewModel
import com.su.mediabox.database.getAppDataBase

class MediaFavoriteViewModel : ViewModel() {
    val favorite = getAppDataBase().favoriteDao().getFavoriteListLiveData()
}