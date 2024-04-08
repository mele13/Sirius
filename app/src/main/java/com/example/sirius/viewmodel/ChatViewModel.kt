package com.example.sirius.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sirius.data.dao.ChatDao
import com.example.sirius.model.Chat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(private val chatDao: ChatDao, private val userViewModel: UserViewModel) : ViewModel() {

    private var _recipientUserId = MutableLiveData<Int>()
    val recipientUserId: LiveData<Int> = _recipientUserId


    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private val _messages = MutableLiveData<List<Chat>>(emptyList())
    val messages: LiveData<List<Chat>> = _messages


    private var _isMessageSeen = MutableLiveData<Boolean>(false)
    val isMessageSeen: LiveData<Boolean> = _isMessageSeen

    init {
        recipientUserId.observeForever { userId ->
            Log.e("recipientUserId", userId.toString())
            val currentUser = userViewModel.getAuthenticatedUser()
            Log.e("currentUserId", currentUser.toString())
            val chatId = currentUser?.let { generateChatId(it.id, userId) }
            Log.e("chatId", chatId.toString())
            if (chatId != null) {
                loadMessages(chatId)
            }
        }
    }



    private fun generateChatId(user1Id: Int, user2Id: Int): String {
        return if (user1Id < user2Id) {
            "$user1Id-$user2Id"
        } else {
            "$user2Id-$user1Id"
        }
    }


    fun updateMessage(message: String) {
        _message.value = message
    }

     fun addMessage(recipientUserId: Int){
        val message: String = _message.value ?: throw IllegalArgumentException("message empty")
        if (message.isNotEmpty()) {
            val currentUser = userViewModel.getAuthenticatedUser()
            val chatId = currentUser?.let { generateChatId(it.id, recipientUserId) }
            val message = chatId?.let {
                Chat(
                    chatId = it,
                    message = message,
                    seen = 0,
                    sentBy = currentUser.id,
                    sentOn = System.currentTimeMillis().toString()
                )
            }
            if (message != null) {
                viewModelScope.launch {
                    chatDao.insertMessage(message)
                }

            }
        }

    }

    private fun loadMessages(chatId: String) {
        viewModelScope.launch {
            chatDao.loadMessages(chatId).onEach { messages ->
                _messages.postValue(messages.reversed())
            }.launchIn(viewModelScope)
        }
    }


    fun initRecipientUserId(userId: Int) {
        _recipientUserId.value = userId
    }



}