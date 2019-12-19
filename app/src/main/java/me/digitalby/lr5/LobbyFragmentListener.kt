package me.digitalby.lr5

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser

interface LobbyFragmentListener {
    var currentUser: FirebaseUser
    fun didRequestLogout(sender: Fragment)
    fun didRequestStats(sender: Fragment)
    fun didRequestCreateGame(sender: Fragment)
    fun didRequestJoinGame(sender: Fragment)
}