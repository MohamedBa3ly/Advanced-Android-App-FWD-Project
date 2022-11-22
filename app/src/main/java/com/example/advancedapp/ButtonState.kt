package com.example.advancedapp

sealed class ButtonState{
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}
