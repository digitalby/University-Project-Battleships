package me.digitalby.lr5

interface GameStateListener {
    var gameState: GameState
    var field: Field
    fun didChangeGameState()
}