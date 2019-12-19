package me.digitalby.lr5


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_lobby.*

/**
 * A simple [Fragment] subclass.
 */
class LobbyFragment : Fragment() {
    companion object {
        lateinit var instance: LobbyFragment
            private set
    }

    var listener: LobbyListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        instance = this

        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buttonLogout.setOnClickListener { _ ->
            listener?.didRequestLogout(this)
        }
        buttonCreateGame.setOnClickListener { _ ->
            listener?.didRequestCreateGame(this)
        }
        buttonJoinGame.setOnClickListener { _ ->
            listener?.didRequestJoinGame(this)
        }
        buttonStats.setOnClickListener { _ ->
            listener?.didRequestStats(this)
        }
    }

}
