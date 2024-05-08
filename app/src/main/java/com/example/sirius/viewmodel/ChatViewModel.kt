package com.example.sirius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sirius.data.dao.ChatDao
import com.example.sirius.model.Animal
import com.example.sirius.model.Chat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(private val chatDao: ChatDao, private val userViewModel: UserViewModel) : ViewModel() {

    private var _recipientUserId = MutableLiveData<Int>()
    private val recipientUserId: LiveData<Int> = _recipientUserId


    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private val _messages = MutableLiveData<List<Chat>>(emptyList())
    val messages: LiveData<List<Chat>> = _messages

    init {
        recipientUserId.observeForever { userId ->
            val currentUser = userViewModel.getAuthenticatedUser()
            val chatId = currentUser?.let { generateChatId(it.id, userId) }
            if (chatId != null) {
                loadMessages(chatId)
            }
        }
    }

    fun generateChatId(user1Id: Int, user2Id: Int): String {
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


    fun addMessageAdoption(recipientUserId: Int, animal : Animal){
        val message = "Hello, I would like to start the process of adopting ${animal.nameAnimal} from your shelter."
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

    fun getUnseenMessages() : Flow<List<Int>> {
        return chatDao.getUnseenMessages()
    }


    fun getChatId() : Flow<List<String>>{
        return chatDao.getChatId()
    }

    private fun loadMessages(chatId: String) {
        viewModelScope.launch {
            chatDao.loadMessages(chatId).onEach { messages ->
                _messages.postValue(messages.reversed())
            }.launchIn(viewModelScope)
        }
    }

    fun splitChatId(chatIds: List<String>): List<String> {
        val chatIdParts = mutableListOf<String>()

        for (chatId in chatIds) {
            val currentPart = StringBuilder()
            for (char in chatId) {
                if (char != '-') {
                    currentPart.append(char)
                } else {
                    chatIdParts.add(currentPart.toString())
                    currentPart.clear()
                }
            }
            chatIdParts.add(currentPart.toString())
        }

        return chatIdParts
    }


    fun initRecipientUserId(userId: Int) {
        _recipientUserId.value = userId
    }

    suspend fun getLastMessage(chatId: String): String {

        return chatDao.getLastMessage(chatId)
    }



}