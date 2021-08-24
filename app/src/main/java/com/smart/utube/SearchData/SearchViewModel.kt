package com.smart.utube.SearchData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smart.utube.Data.Recent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application : Application) : AndroidViewModel(application) {
    val readAllSearch : LiveData<List<SearchDataModel>>
    private val repository : SearchRepository

    init {
        val searchDao = SearchDatabase.getDatabase(application).searchDao()
        repository = SearchRepository(searchDao)
        readAllSearch = repository.readAllData
    }

    fun addSearch (search : SearchDataModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecent(search)
        }
    }

    fun deleteSearch (search : SearchDataModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSearch(search)
        }
    }
}