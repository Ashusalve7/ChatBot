package com.example.last
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import com.google.ai.client.generativeai.type.content

class ChatViewModel : ViewModel(){

    val messagelist by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.apikey
    )


    fun sendMessage(question: String){
        Log.i("in ChatViewModel", question)
        viewModelScope.launch {

            try{
                val chat = generativeModel.startChat(
                    history = messagelist.map {
                        content(it.role){ text(it.message)}
                    }.toList()
                )

                messagelist.add(MessageModel(question, "user"))
                messagelist.add(MessageModel("Typing...", "model"))
                val response = chat.sendMessage(question)
                messagelist.removeLast()
                messagelist.add(MessageModel(response.text.toString(), "model"))
            }
            catch (e: Exception){
                messagelist.removeLast()
                messagelist.add(MessageModel("Error: "+e.message.toString(),role = "model"))

            }



        }
    }
}
