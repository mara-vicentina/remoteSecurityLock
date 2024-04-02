package com.github.maravicentina.remotesecuritylockarduino.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.maravicentina.remotesecuritylockarduino.R

/**
 * Atividade responsável pelo controle do bloqueio.
 * Esta atividade permite ao usuário interagir com o bloqueio remoto através do aplicativo.
 */
class LockControlActivity : AppCompatActivity() {
    private var passwordField: EditText? = null

    /**
     * Método chamado quando a atividade é criada.
     * Inicializa a interface de usuário e configura o comportamento dos elementos visuais.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lock_control)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passwordField = findViewById(R.id.passwordField)
    }

    /**
     * Método chamado ao clicar no botão para desconectar o Bluetooth.
     * Fecha a conexão Bluetooth e encerra a atividade.
     */
    fun disconnectBluetooth(view: View) {
        if (ConnectActivity.bluetoothConnection?.close() == true) {
            finish()
        }
    }

    /**
     * Método chamado ao clicar no botão para desbloquear o dispositivo.
     * Envia uma mensagem para o dispositivo de bloqueio com a senha inserida pelo usuário.
     */
    fun unlock(view: View) {
        if (passwordField?.text.toString() != "") {
            ConnectActivity.bluetoothConnection?.sendMessage("a:" + passwordField?.text.toString())
        }
    }

    /**
     * Método chamado ao clicar no botão para bloquear o dispositivo.
     * Envia uma mensagem para o dispositivo de bloqueio para ativar o bloqueio.
     */
    fun lock(view: View) {
        ConnectActivity.bluetoothConnection?.sendMessage("f:0")
    }
}