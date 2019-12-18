package me.digitalby.lr5

import androidx.fragment.app.Fragment

interface CreateGameListener {
    var gameId: String?
    fun didCancelCreating(sender: CreateGameFragment)
}