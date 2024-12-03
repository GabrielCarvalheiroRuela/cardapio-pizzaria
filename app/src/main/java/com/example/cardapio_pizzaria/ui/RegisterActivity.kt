package com.example.cardapio_pizzaria.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pizzaria.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Ação do botão de criar conta
        binding.registerBtn.setOnClickListener {
            createAccount()
        }

        // Redirecionar para a tela de login
        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Função para criar cadastro do usuário no Firebase
    private fun createAccount() {
        val name = binding.nameInput.text.toString()
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        // Validações dos campos
        if (name.isEmpty()) {
            binding.nameInput.error = "O nome é obrigatório"
            return
        }

        if (email.isEmpty()) {
            binding.emailInput.error = "O e-mail é obrigatório"
            return
        }

        if (password.isEmpty()) {
            binding.passwordInput.error = "A senha é obrigatória"
            return
        }

        if (password != confirmPassword) {
            binding.confirmPasswordInput.error = "As senhas não coincidem"
            return
        }

        // Criar um novo usuário no Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid

                    if (userId != null) {
                        saveUserToFirestore(userId, name, email)
                    } else {
                        Toast.makeText(this, "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Falha no registro: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // Salvar dados do usuário no Firestore
    private fun saveUserToFirestore(userId: String, name: String, email: String) {
        val userMap = mapOf(
            "name" to name,
            "email" to email
        )

        firestore.collection("user").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Erro ao salvar dados do usuário: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
