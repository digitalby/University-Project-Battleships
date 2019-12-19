package me.digitalby.lr5


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_construction.*

/**
 * A simple [Fragment] subclass.
 */
class ConstructionFragment : Fragment(), BlueprintListener {
    companion object {

        lateinit var instance: ConstructionFragment
            private set
    }
    var listener: ConstructionListener? = null
    set(value) {
        field = value
        field?.blueprint?.listener = this
    }

    private val noDrawable by lazy {
        ContextCompat.getDrawable((activity as Context?)!!, R.drawable.battleships_no)
    }
    private val selectedDrawable by lazy {
        ContextCompat.getDrawable((activity as Context?)!!, R.drawable.battleships_yes)
    }
    private val ship4Buttons by lazy { listOf(buttonShip4Horizontal, buttonShip4Vertical) }
    private val ship3Buttons by lazy { listOf(buttonShip3Horizontal, buttonShip3Vertical) }
    private val ship2Buttons by lazy { listOf(buttonShip2Horizontal, buttonShip2Vertical) }
    private val ship1Buttons by lazy { listOf(buttonShip1) }
    private val shipButtons by lazy { listOf(ship1Buttons, ship2Buttons, ship3Buttons, ship4Buttons) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        instance = this
        return inflater.inflate(R.layout.fragment_construction, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buttonShip1.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship1) }
        buttonShip2Horizontal.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship2Horizontal) }
        buttonShip2Vertical.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship2Vertical) }
        buttonShip3Horizontal.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship3Horizontal) }
        buttonShip3Vertical.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship3Vertical) }
        buttonShip4Horizontal.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship4Horizontal) }
        buttonShip4Vertical.setOnClickListener { _ -> listener?.didSelectShip(this, ShipType.Ship4Vertical) }
    }

    override fun didChangeShips(sender: Blueprint) {
        sender.rules?.forEach { rule ->
            val currentRule = sender.currentRules[rule.key]
            val maximumRule = rule.value

            val index = when (rule.key) {
                ShipType.Ship4Vertical -> 3
                ShipType.Ship4Horizontal -> 3
                ShipType.Ship3Vertical -> 2
                ShipType.Ship3Horizontal -> 2
                ShipType.Ship2Vertical -> 1
                ShipType.Ship2Horizontal -> 1
                ShipType.Ship1 -> 0
                ShipType.Ship2 -> 1
                ShipType.Ship3 -> 2
                ShipType.Ship4 -> 3
            }
            if (currentRule != null)
                setButtons(shipButtons[index], currentRule, maximumRule)
        }
    }

    private fun setButtons(list: List<ImageButton>, currentRule: Int, maximumRule: Int) {
        list.forEach { button ->
            if (currentRule >= maximumRule) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    button.foreground = noDrawable
                }
                button.isEnabled = false
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    button.foreground = null
                }
                button.isEnabled = true
            }
        }
    }

    fun setSelected(shipType: ShipType?) {
        val flatButtons = shipButtons.flatten()
        flatButtons.forEach { button ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(button.foreground == selectedDrawable) {
                    button.foreground = null
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (shipType) {
                ShipType.Ship4Vertical -> buttonShip4Vertical.foreground = selectedDrawable
                ShipType.Ship4Horizontal -> buttonShip4Horizontal.foreground = selectedDrawable
                ShipType.Ship3Vertical -> buttonShip3Vertical.foreground = selectedDrawable
                ShipType.Ship3Horizontal -> buttonShip3Horizontal.foreground = selectedDrawable
                ShipType.Ship2Vertical -> buttonShip2Vertical.foreground = selectedDrawable
                ShipType.Ship2Horizontal -> buttonShip2Horizontal.foreground = selectedDrawable
                ShipType.Ship1 -> buttonShip1.foreground = selectedDrawable
                else -> return
            }
        }
    }

}
