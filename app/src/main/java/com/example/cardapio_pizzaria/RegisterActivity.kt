package com.example.cardapio_pizzaria

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pizzaria.LoginActivity
import com.example.cardapio_pizzaria.MainActivity
import com.example.cardapio_pizzaria.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar o ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Ação do botão de criar conta
        binding.registerBtn.setOnClickListener(View.OnClickListener { createAccount() })

        // Redirecionar para a tela de login
        binding.loginLink.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        })
    }

    //Função para criar cadastro do usuário no Firebase
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
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                    // Redirecionar para a MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Caso ocorra um erro
                    Toast.makeText(this, "Falha no registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}