package me.digitalby.lr5

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SimpleAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : AppCompatActivity() {

    private lateinit var uid: String

    val database = FirebaseDatabase.getInstance()
    val reference = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!

        val c = this

        reference.child("PastGames").child(uid).addListenerForSingleValueEvent(object:ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.value as? HashMap<String, Boolean> ?: hashMapOf()
                val arrayList = arrayListOf<HashMap<String, String>>()

                for(key in data.keys) {
                    val newMap = HashMap<String, String>()
                    newMap["text"] = (if(data[key] == true) "VICTORY" else "DEFEAT") + " at $key"
                    arrayList.add(newMap)
                }

                val itemResource = R.layout.listview_item
                val from = arrayOf("text")
                val to = intArrayOf(R.id.textViewTitle)

                val adapter = SimpleAdapter(c, arrayList, itemResource, from, to)
                statsListView.adapter = adapter
            }

        })
    }
}
