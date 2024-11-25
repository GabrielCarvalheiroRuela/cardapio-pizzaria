package com.example.cardapio_pizzaria.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pizzaria.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o FirebaseAuth e FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Exibe o e-mail do usuário logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvUserEmail.text = currentUser.email

            getUserName(currentUser.uid)
        } else {
            Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Ação do botão de voltar
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Ação do botão "Deletar Conta"
        binding.btnDeleteAccount.setOnClickListener {
            confirmarExclusaoDeConta()
        }
    }

    // Coletar da coleção nome do user
    private fun getUserName(userId: String) {
        firestore.collection("user").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("name")
                    binding.tvUserName.text = userName ?: "Nome não encontrado"
                } else {
                    Toast.makeText(this, "Erro ao carregar os dados do usuário", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao acessar o Firestore: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Confirmação de exclusão de conta
    private fun confirmarExclusaoDeConta() {
        AlertDialog.Builder(this)
            .setTitle("Confirmação de Exclusão")
            .setMessage("Tem certeza de que deseja excluir sua conta? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                excluirConta()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Excluir conta
    private fun excluirConta() {
        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao excluir conta: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
