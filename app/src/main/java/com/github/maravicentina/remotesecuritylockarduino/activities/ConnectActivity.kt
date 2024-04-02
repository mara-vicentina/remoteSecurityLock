package com.github.maravicentina.remotesecuritylockarduino.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.maravicentina.remotesecuritylockarduino.R
import com.github.maravicentina.remotesecuritylockarduino.entities.BluetoothConnection

class ConnectActivity : AppCompatActivity() {
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>
    companion object {
        @SuppressLint("StaticFieldLeak")
        var bluetoothConnection: BluetoothConnection? = null
    }

    /**
     * Método chamado quando a atividade é criada.
     * Inicializa a interface de usuário e verifica se o Bluetooth está ativado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connect)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkAndEnableBluetooth()
    }

    /**
     * Inicializa uma conexão Bluetooth.
     * Cria uma instância de BluetoothConnection e tenta iniciar a conexão Bluetooth.
     * Se a conexão for bem-sucedida, inicia a atividade LockControlActivity.
     */
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    fun initBluetoothConnection(view: View) {
        bluetoothConnection = BluetoothConnection(this, bluetoothAdapter)

        if (bluetoothConnection!!.start()) {
            val intent = Intent(this, LockControlActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Verifica se o Bluetooth está ativado e tenta ativá-lo se não estiver.
     * Retorna true se o Bluetooth foi ativado através da solicitação, caso contrário, retorna false.
     */
    private fun checkAndEnableBluetooth(): Boolean {
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager!!.adapter

        enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "O Bluetooth foi ativado com sucesso.", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            Toast.makeText(this, "Erro ao ativar o Bluetooth.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "O dispositivo não suporta o Bluetooth.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBluetoothIntent)
            return true
        }

        Toast.makeText(this, "O Bluetooth está ativo.", Toast.LENGTH_SHORT).show()
        return false
    }
}