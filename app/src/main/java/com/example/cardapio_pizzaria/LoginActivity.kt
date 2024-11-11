package com.example.cardapio_pizzaria

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pizzaria.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar o View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Ação para o botão de esqueci a senha
        binding.forgotPassword.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(email)
            }
        }

        // Ação do botão entrar
        binding.loginBtn.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            loginUser(email, password)
        }

        // Ação do botao para se cadastrar
        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        // Validação dos campos
        if (TextUtils.isEmpty(email)) {
            binding.emailInput.error = "O e-mail é obrigatório"
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordInput.error = "A senha é obrigatória"
            return
        }

        // Autenticar com o Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                    // Redirecionar para a MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Falha no login
                    Toast.makeText(
                        this,
                        "Falha no login: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    private fun resetPassword(email: String) {
        // Validação adicional para verificar se o formato do e-mail é válido
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Tentando enviar o e-mail de redefinição de senha
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // E-mail de recuperação enviado com sucesso
                    Toast.makeText(this, "E-mail de recuperação enviado para $email", Toast.LENGTH_SHORT).show()
                } else {
                    // Falha ao enviar o e-mail de recuperação
                    val errorMessage = task.exception?.message ?: "Erro desconhecido"
                    Toast.makeText(this, "Falha ao enviar e-mail de recuperação: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
