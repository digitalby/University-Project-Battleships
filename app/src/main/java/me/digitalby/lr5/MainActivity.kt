package me.digitalby.lr5

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_lobby.*

class MainActivity : AppCompatActivity(),
    ConstructionFragmentListener,
    FieldFragmentListener{
    override lateinit var blueprint: Blueprint

    val fieldSize = Vector2(10, 10)

    val shipRules: HashMap<ShipType, Int> = hashMapOf(
        ShipType.Ship1 to 4,
        ShipType.Ship2 to 3,
        ShipType.Ship3 to 2,
        ShipType.Ship4 to 1
    )

    override lateinit var field: Field

    private var selectedShipType: ShipType? = null

    private lateinit var fieldFragment: FieldFragment

    private lateinit var lobbyFragment: LobbyFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        fieldFragment = supportFragmentManager.findFragmentById(R.id.mainFieldFragment) as FieldFragment
        fieldFragment.listener = this
        lobbyFragment = supportFragmentManager.findFragmentById(R.id.mainLobbyFragment) as LobbyFragment

        blueprint = Blueprint(fieldSize, shipRules)
        updateFieldFromBlueprint()


        ConstructionFragment.instance.listener = this
    }

    override fun didSelectCell(sender: FieldFragment, position: Vector2) {
        if (selectedShipType == null) {
            blueprint.removeShip(position)
            updateFieldFromBlueprint()
        } else {
            val selectedConstructionShipType = this.selectedShipType!!
            val size = Ship.shipTypeToSize(selectedConstructionShipType)
            val ship = Ship(position, size!!)
            ship.type = Ship.simplifyShipType(selectedConstructionShipType)
            tryPlaceShip(ship)
        }
    }

    private fun tryPlaceShip(ship: Ship) {
        try {
            blueprint.tryAddShip(ship)
        } catch (e: BlueprintException) {
            Snackbar.make(findViewById(android.R.id.content),
                e.message!!,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        } finally {
            selectedShipType = null
            updateFieldFromBlueprint()
        }
    }

    override fun didSelectShip(sender: ConstructionFragment, ship: ShipType?) {
        if(selectedShipType == ship) {
            selectedShipType = null
            sender.setSelected(null)
        } else {
            selectedShipType = ship
            sender.setSelected(ship)
        }
    }

    private fun updateFieldFromBlueprint() {
        field = blueprint.makeField()
        fieldFragment.drawField(field)
        ConstructionFragment.instance.didChangeShips(blueprint)
        if(blueprint.valid) {
            textViewError.text = ""
        } else {
            textViewError.text = getString(R.string.lobby_place_ships)
        }
    }
}
