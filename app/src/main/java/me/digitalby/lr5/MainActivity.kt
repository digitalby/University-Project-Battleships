package me.digitalby.lr5

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_lobby.*

class MainActivity : AppCompatActivity(),
    ConstructionFragmentListener,
    FieldFragmentListener,
    LobbyFragmentListener{

    private val auth = FirebaseAuth.getInstance()
    override var currentUser = auth.currentUser

    override lateinit var blueprint: Blueprint

    val fieldSize = Vector2(10, 10)

    val shipRules: HashMap<ShipType, Int> = hashMapOf(
        ShipType.Ship1 to 4,
        ShipType.Ship2 to 3,
        ShipType.Ship3 to 2,
        ShipType.Ship4 to 1
    )

    override lateinit var field: Field

    private lateinit var uid: String

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
        lobbyFragment.listener = this

        blueprint = Blueprint(fieldSize, shipRules)
        updateFieldFromBlueprint()

        ConstructionFragment.instance.listener = this

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!
        if(uid.isNotEmpty()) {
            lobbyFragment.uid = uid
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

    override fun didSelectShip(sender: ConstructionFragment, ship: ShipType?) {
        if(selectedShipType == ship) {
            selectedShipType = null
            sender.setSelected(null)
        } else {
            selectedShipType = ship
            sender.setSelected(ship)
        }
    }

    override fun didRequestLogout(sender: Fragment) {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun didRequestStats(sender: Fragment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didRequestCreateGame(sender: Fragment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didRequestJoinGame(sender: Fragment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
