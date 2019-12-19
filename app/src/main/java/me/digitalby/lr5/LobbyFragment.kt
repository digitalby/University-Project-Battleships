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

    var listener: LobbyFragmentListener? = null
    var uid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buttonLogout.setOnClickListener {
            listener?.didRequestLogout(this)
        }
        buttonCreateGame.setOnClickListener {
            listener?.didRequestCreateGame(this)
        }
        buttonJoinGame.setOnClickListener {
            listener?.didRequestJoinGame(this)
        }
        buttonStats.setOnClickListener {
            listener?.didRequestStats(this)
        }
        textViewPlayerName.text = uid
    }

}
