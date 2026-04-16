package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel

class StartViewModel : ViewModel() {
    fun alHacerClickEnParchate() {
        println("¡Botón presionado! El usuario quiere empezar.")
    }
}