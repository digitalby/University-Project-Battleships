package me.digitalby.lr5

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_game.*

class CreateGameActivity : AppCompatActivity() {

    private lateinit var uid: String

    val database = FirebaseDatabase.getInstance()
    val reference = database.reference

    private lateinit var gameId: String

    private lateinit var fieldFragment: FieldFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!
        val fieldString = bundle.getString("fieldString")!!
        val field = Field.fromFieldString(fieldString)

        fieldFragment = supportFragmentManager.findFragmentById(R.id.createGameField) as FieldFragment
        fieldFragment.drawField(field)

        val newReference = reference.child("PendingGames").push()
        newReference.setValue(uid)
        gameId = newReference.key!!
        linkEditText.setText(gameId)
    }

    override fun onDestroy() {
        reference.child("PendingGames").child(gameId).removeValue()
        super.onDestroy()
    }
}
