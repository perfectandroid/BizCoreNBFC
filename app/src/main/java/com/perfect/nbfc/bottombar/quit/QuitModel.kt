package com.perfect.nbfc.bottombar.quit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuitModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is quit page"
    }
    val text: LiveData<String> = _text
}