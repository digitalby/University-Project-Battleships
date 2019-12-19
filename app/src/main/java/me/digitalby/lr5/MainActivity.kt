package me.digitalby.lr5

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_lobby.*
import java.io.*

class MainActivity : AppCompatActivity(),
    ConstructionFragmentListener,
    FieldFragmentListener,
    LobbyFragmentListener{

    private val auth = FirebaseAuth.getInstance()
    override var currentUser = auth.currentUser
    val database = FirebaseDatabase.getInstance()
    val reference = database.reference

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

    private lateinit var sharedPreferences: SharedPreferences

    private var selectedShipType: ShipType? = null

    private lateinit var fieldFragment: FieldFragment
    private lateinit var lobbyFragment: LobbyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        sharedPreferences = getSharedPreferences("me.digitalby.lr5", Context.MODE_PRIVATE)

        fieldFragment = supportFragmentManager.findFragmentById(R.id.mainFieldFragment) as FieldFragment
        fieldFragment.listener = this
        lobbyFragment = supportFragmentManager.findFragmentById(R.id.mainLobbyFragment) as LobbyFragment
        lobbyFragment.listener = this

        blueprint = Blueprint(fieldSize, shipRules, loadShips() ?: arrayListOf())
        updateFieldFromBlueprint()

        ConstructionFragment.instance.listener = this

        val bundle: Bundle? = intent.extras
        if(bundle != null) {
            uid = bundle.getString("uid")!!
        } else if(savedInstanceState != null) {
            uid = savedInstanceState.getString("uid")!!
        } else {
            uid = sharedPreferences.getString("uid", null)!!
        }
        if (uid.isNotEmpty()) {
            lobbyFragment.uid = uid
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("uid", uid)
        super.onSaveInstanceState(outState)
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

    private fun loadShips(): ArrayList<Ship>? {
        return try {
            val fileInputStream = openFileInput("ships.lr5")
            val objectInputStream = ObjectInputStream(fileInputStream)
            val blueprintShips = objectInputStream.readObject() as ArrayList<Ship>
            objectInputStream.close()
            fileInputStream.close()
            blueprintShips
        } catch (e: FileNotFoundException) {
            null
        }
    }

    private fun saveShips() {
        try {
            val fileOutputStream = openFileOutput("ships.lr5", Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(blueprint.getShips())
            objectOutputStream.close()
            fileOutputStream.close()
        } catch(e: InvalidClassException) {
            Log.e("LR5", "The class is not valid. $e")
        } catch (e: NotSerializableException) {
            Log.e("LR5", "The class is not serializable. $e")
        } catch (e: IOException) {
            Log.e("LR5", "$e")
        }
    }

    private fun updateFieldFromBlueprint() {
        field = blueprint.makeField()
        fieldFragment.drawField(field)
        ConstructionFragment.instance.didChangeShips(blueprint)
        if(blueprint.valid) {
            textViewError.text = ""
            buttonCreateGame.isEnabled = true
            buttonJoinGame.isEnabled = true
        } else {
            textViewError.text = getString(R.string.lobby_place_ships)
            buttonCreateGame.isEnabled = false
            buttonJoinGame.isEnabled = false
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
        saveShips()
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
        blueprint.clearShips()
        saveShips()
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun didRequestStats(sender: Fragment) {
        val edit = sharedPreferences.edit()
        edit.putString("uid", uid)
        edit.apply()
        val intent = Intent(this, StatsActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    override fun didRequestCreateGame(sender: Fragment) {
        if(!blueprint.valid)
            return
        val intent = Intent(this, CreateGameActivity::class.java)
        intent.putExtra("uid", uid)
        val fieldString = Field.toFieldString(field)
        intent.putExtra("fieldString", fieldString)
        val edit = sharedPreferences.edit()
        edit.putString("uid", uid)
        edit.apply()
        startActivity(intent)
    }

    private fun readyToPlay() {
        //TODO: initialize the receiver
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("uid", uid)
        val fieldString = Field.toFieldString(field)
        intent.putExtra("fieldString", fieldString)
        startActivity(intent)
        finish()
    }

    override fun didRequestJoinGame(sender: Fragment) {
        if(!blueprint.valid)
            return
        val builder = AlertDialog.Builder(this)
        val inputView = EditText(this)
        inputView.inputType = InputType.TYPE_CLASS_TEXT
        builder.setMessage("Enter the game's code to join it.")
            .setTitle("Join Game")
            .setView(inputView)
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                val gameToJoin = inputView.text.toString().trim()
                if(gameToJoin.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "The game code is empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                val gameReference = reference
                    .child("PendingGames")
                    .child(gameToJoin)
                gameReference.addListenerForSingleValueEvent(object:ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()) {
                            gameReference.setValue(true)
                            readyToPlay()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "The game with this code does not exist.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            }.setNegativeButton(getString(android.R.string.cancel)) {_, _ ->}
            .show()
    }

}
