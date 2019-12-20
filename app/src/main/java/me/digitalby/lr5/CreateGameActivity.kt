package me.digitalby.lr5

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_game.*
import java.util.Random

class CreateGameActivity : AppCompatActivity() {

    private lateinit var uid: String
    private lateinit var gameId: String

    val database = FirebaseDatabase.getInstance()
    val reference = database.reference

    private lateinit var fieldString: String
    private lateinit var fieldFragment: FieldFragment
    private lateinit var ships: HashMap<String, HashMap<String, Long>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!
        fieldString = bundle.getString("fieldString")!!
        ships = bundle.getSerializable("ships") as HashMap<String, HashMap<String, Long>>
        val field = Field.fromFieldString(fieldString)

        fieldFragment = supportFragmentManager.findFragmentById(R.id.createGameField) as FieldFragment
        fieldFragment.drawField(field)

        val newReference = reference.child("PendingGames").push()
        newReference.setValue(uid)
        gameId = newReference.key!!
        linkEditText.setText(gameId)
        newReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value as? Boolean == true) {
                    readyToPlay()
                }
            }
        })
    }

    private fun readyToPlay() {
        reference.child("PendingGames").child(gameId).removeValue()
        reference.child("CurrentGames").child(gameId).child("Player1").setValue(uid)
        val random = Random()
        val player1GoesFirst = random.nextBoolean()
        reference.child("CurrentGames").child(gameId).child("Turn")
            .setValue(if (player1GoesFirst) 1 else 2)

        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("fieldString", fieldString)
        intent.putExtra("gameId", gameId)
        intent.putExtra("myTurn", 1L)
        intent.putExtra("ships", ships)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        reference.child("PendingGames").child(gameId).removeValue()
        super.onDestroy()
    }
}
