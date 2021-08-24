package com.smart.utube.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smart.utube.Network.Repositry

class YoutubeViewModelFactory(val repositry: Repositry) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return YoutubeViewModel(repositry) as T
    }
}