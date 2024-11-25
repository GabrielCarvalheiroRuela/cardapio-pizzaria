package com.example.cardapio_pizzaria.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userEmail = MutableLiveData<String>()
    private val _deleteStatus = MutableLiveData<Boolean>()

    val userEmail: LiveData<String> get() = _userEmail
    val deleteStatus: LiveData<Boolean> get() = _deleteStatus

    init {
        val currentUser: FirebaseUser? = auth.currentUser
        _userEmail.value = currentUser?.email ?: "Usuário não autenticado"
    }

    fun deleteUser() {
        val currentUser = auth.currentUser
        currentUser?.delete()?.addOnCompleteListener { task ->
            _deleteStatus.value = task.isSuccessful
        } ?: run {
            _deleteStatus.value = false
        }
    }
}
