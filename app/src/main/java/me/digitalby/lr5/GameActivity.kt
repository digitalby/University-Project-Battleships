package me.digitalby.lr5

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import me.digitalby.lr3.DateTimeHelper
import java.net.SecureCacheResponse

class GameActivity : AppCompatActivity(), FieldFragmentListener {
    private lateinit var uid: String

    private lateinit var fieldString: String
    override lateinit var field: Field
    private lateinit var otherField: Field
    private lateinit var gameId: String

    private var otherFieldInfo: Field? = null
    private var leftBlueprint: Blueprint? = null
    private var rightBlueprint: Blueprint? = null

    val database = FirebaseDatabase.getInstance()
    val reference = database.reference
    private lateinit var gameReference: DatabaseReference

    private lateinit var leftFieldFragment: FieldFragment
    private lateinit var rightFieldFragment: FieldFragment

    private var processing = true
    private var myTurn = 0L
    private var turn = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!
        fieldString = bundle.getString("fieldString")!!
        gameId = bundle.getString("gameId")!!
        myTurn = bundle.getLong("myTurn")
        field = Field.fromFieldString(fieldString)
        otherField = Field(Vector2(10, 10), 20)

        gameReference = reference.child("CurrentGames").child(gameId)

        val ships = bundle.getSerializable("ships") as HashMap<String, HashMap<String, Long>>

        gameReference.child("player${myTurn}ships").setValue(ships)

        leftFieldFragment = supportFragmentManager.findFragmentById(R.id.leftFieldFragment) as FieldFragment
        leftFieldFragment.drawField(field)

        rightFieldFragment = supportFragmentManager.findFragmentById(R.id.rightFieldFragment) as FieldFragment
        rightFieldFragment.listener = this



        onTurnChange()
        onMadeTurnResponse()

        if(myTurn == 1L) {
            respondToBlueprints()
            respondToMadeTurn()
        }
        processing = false
    }

    private fun respondToBlueprints() {
        gameReference.child("player1ships").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val shipsHashMap = p0.value as? ArrayList<HashMap<String, Long>> ?: return
                val shipsList = Blueprint.fromParcelable(shipsHashMap)
                val blueprint = Blueprint(Vector2(10, 10), null, shipsList)
                leftBlueprint = blueprint
                gameReference.child("player1ships").setValue(null)
            }

        })
        gameReference.child("player2ships").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val shipsHashMap = p0.value as? ArrayList<HashMap<String, Long>> ?: return
                val shipsList = Blueprint.fromParcelable(shipsHashMap)
                val blueprint = Blueprint(Vector2(10, 10), null, shipsList)
                rightBlueprint = blueprint
                otherFieldInfo = rightBlueprint!!.makeField()
                gameReference.child("player2ships").setValue(null)
            }

        })
    }

    private fun respondToMadeTurn() {
        gameReference.child("MadeTurn").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val dict = p0.value as? HashMap<String, Any> ?: return
                val sender = dict["sender"] as? Long ?: return
                val xLong = dict["x"] as? Long ?: return
                val yLong = dict["y"] as? Long ?: return
                if(dict["response"] != null) return
                val x = xLong.toInt()
                val y = yLong.toInt()
                val f = if(turn == myTurn) otherFieldInfo!! else field
                val response = when(f.cells[x][y]) {
                    Cell.Empty -> "M"
                    Cell.Ship -> "H"
                    Cell.Miss -> "M"
                    Cell.Hurt -> "H"
                    Cell.Dead -> "D"
                }
                gameReference.child("MadeTurn").child("response").setValue(response)
                if(response == "M")
                    turn = if(turn == 1L) 2L else 1L
                gameReference.child("Turn").setValue(turn)
            }
        })
    }

    private fun onMadeTurnResponse() {
        gameReference.child("MadeTurn").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val dict = p0.value as? HashMap<String, Any> ?: return
                val response = dict["response"] as? String ?: return
                //TODO: process signals like game over and concede here (don't forget to log stats!)
                val xLong = dict["x"] as? Long ?: return
                val yLong = dict["y"] as? Long ?: return
                val sender = dict["sender"] as? Long ?: return
                val x = xLong.toInt()
                val y = yLong.toInt()
                val fragment = if(sender == myTurn) rightFieldFragment else leftFieldFragment
                val field = if(sender == myTurn) otherField else field
                field.cells[x][y] = when(response) {
                    "S" -> Cell.Ship
                    "H" -> Cell.Hurt
                    "D" -> Cell.Dead
                    else -> Cell.Miss
                }
                fragment.drawField(field)

                gameReference.child("MadeTurn").removeValue()
                checkDeadShips(sender)
                processing = false
            }

        })
    }

    private fun checkGameOver() {
        val builder = AlertDialog.Builder(this)
        val date = DateTimeHelper.getFormattedDate()

        val leftCount = field.flatCells.count { cell -> cell == Cell.Hurt }
        if(leftCount == 20) {
            processing = true
            reference.child("PastGames").child(uid).child(date).setValue(false)
            builder.setMessage("You lose.")
                .setTitle("Game Over")
                .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                    finishGame()
                }.setCancelable(false)
                .show()
        }
        val rightCount = otherField.flatCells.count { cell -> cell == Cell.Hurt }
        if(rightCount == 20) {
            processing = true
            reference.child("PastGames").child(uid).child(date).setValue(true)
            builder.setMessage("You win.")
                .setTitle("Game Over")
                .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                    finishGame()
                }.setCancelable(false)
                .show()
        }
    }

    private fun finishGame() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }

    private fun checkDeadShips(sender: Long) {
        //TODO: actually check for dead ships
        checkGameOver()
    }

    private fun onTurnChange() {
        gameReference.child("Turn").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val t = p0.value as? Long

                if(t != null) {
                    turn = t
                    title = if (turn == myTurn) "Your turn" else "Opponent's turn"
                }
            }
        })
    }

    private fun sendMadeTurn(position: Vector2, sender: Long, response: String? = null) {
        gameReference.child("MadeTurn").child("response").setValue(response)
        gameReference.child("MadeTurn").child("sender").setValue(sender)
        gameReference.child("MadeTurn").child("x").setValue(position.x)
        gameReference.child("MadeTurn").child("y").setValue(position.y)
    }

    override fun didSelectCell(sender: FieldFragment, position: Vector2) {
        if(turn != myTurn || processing)
            return
        if(otherField.cells[position.x][position.y] != Cell.Empty)
            return
        processing = true
        sendMadeTurn(position, myTurn)
    }

}

