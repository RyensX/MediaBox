package com.su.mediabox.v2.viewmodel

import androidx.lifecycle.ViewModel
import com.su.mediabox.database.getAppDataBase

class VideoFavoriteViewModel : ViewModel() {
    val favorite = getAppDataBase().favoriteAnimeDao().getFavoriteAnimeListLiveData()
}