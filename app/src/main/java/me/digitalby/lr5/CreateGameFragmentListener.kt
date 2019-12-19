package me.digitalby.lr5

import androidx.fragment.app.Fragment

interface CreateGameFragmentListener {
    var gameId: String?
    fun didCancelCreating(sender: CreateGameFragment)
}