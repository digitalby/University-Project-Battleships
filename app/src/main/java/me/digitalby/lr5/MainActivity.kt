package me.digitalby.lr5

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(),
    ConstructionListener,
    FieldListener,
    GameStateListener{
    override var blueprint: Blueprint? = null

    val fieldSize = Vector2(10, 10)

    val shipRules: HashMap<ShipType, Int> = hashMapOf(
        ShipType.Ship1 to 4,
        ShipType.Ship2 to 3,
        ShipType.Ship3 to 2,
        ShipType.Ship4 to 1
    )

    override var gameState = GameState.Construction
    set(value) {
        field = value
        didChangeGameState()
    }
    override var field: Field = Field(fieldSize, 20)

    private var selectedShipType: ShipType? = null
    private lateinit var fieldFragment: FieldFragment

    private lateinit var rightFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        fieldFragment = supportFragmentManager.findFragmentById(R.id.myFieldFragment) as FieldFragment
        fieldFragment.listener = this
        rightFragment = supportFragmentManager.findFragmentById(R.id.rightFragment)!!
        didChangeGameState()
        ConstructionFragment.instance.listener = this
    }
    override fun didSelectCell(sender: FieldFragment, position: Vector2) {
        when(gameState) {
            GameState.Construction -> {
                val blueprint = this.blueprint!!
                if (selectedShipType == null) {
                    blueprint.removeShip(position)
                    updateFieldFromBlueprint()
                } else {
                    val selectedConstructionShipType = this.selectedShipType!!
                    val size = Ship.shipTypeToSize(selectedConstructionShipType)
                    val ship = Ship(position, size!!)
                    ship.type = Ship.simplifyShipType(selectedConstructionShipType)
                    tryPlaceShip(ship, position)
                }
            }
            GameState.YourTurn -> TODO()
            else -> return
        }
    }

    fun tryPlaceShip(ship: Ship, position: Vector2) {
        val blueprint = this.blueprint!!
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

    fun updateFieldFromBlueprint() {
        if(blueprint == null)
            return
        val blueprint = this.blueprint!!
        field = blueprint.makeField()
        fieldFragment.drawField(field)
        ConstructionFragment.instance.didChangeShips(blueprint)
        if(blueprint.valid) {
            Snackbar.make(findViewById(android.R.id.content),
                "The blueprint is valid",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun didChangeGameState() {
        when(gameState) {
            GameState.Construction -> {
                blueprint = Blueprint(fieldSize, shipRules)
                field = blueprint!!.makeField()
                fieldFragment.drawField(field)
            }
            GameState.WaitingForOpponent -> TODO()
            GameState.YourTurn -> TODO()
            GameState.OpponentTurn -> TODO()
            GameState.GameOver -> TODO()
        }
    }
}
