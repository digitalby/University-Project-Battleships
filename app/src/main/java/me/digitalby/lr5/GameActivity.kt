package me.digitalby.lr5

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity(), FieldFragmentListener {
    private lateinit var uid: String

    private lateinit var fieldString: String
    override lateinit var field: Field

    private lateinit var leftFieldFragment: FieldFragment
    private lateinit var rightFieldFragment: FieldFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!
        fieldString = bundle.getString("fieldString")!!
        field = Field.fromFieldString(fieldString)

        leftFieldFragment = supportFragmentManager.findFragmentById(R.id.leftFieldFragment) as FieldFragment
        leftFieldFragment.drawField(field)

        rightFieldFragment = supportFragmentManager.findFragmentById(R.id.rightFieldFragment) as FieldFragment
        rightFieldFragment.listener = this
    }

    override fun didSelectCell(sender: FieldFragment, position: Vector2) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

