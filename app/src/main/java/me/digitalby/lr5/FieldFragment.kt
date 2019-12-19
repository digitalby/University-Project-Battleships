package me.digitalby.lr5


import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_my_field.*

/**
 * A simple [Fragment] subclass.
 */
class FieldFragment : Fragment() {

    var listener: FieldListener? = null
    private val cells: List<List<Button>> by lazy {
        listOf(
            listOf(
                button_0_0_0,
                button_0_0_1,
                button_0_0_2,
                button_0_0_3,
                button_0_0_4,
                button_0_0_5,
                button_0_0_6,
                button_0_0_7,
                button_0_0_8,
                button_0_0_9
            ),
            listOf(
                button_0_1_0,
                button_0_1_1,
                button_0_1_2,
                button_0_1_3,
                button_0_1_4,
                button_0_1_5,
                button_0_1_6,
                button_0_1_7,
                button_0_1_8,
                button_0_1_9
            ),
            listOf(
                button_0_2_0,
                button_0_2_1,
                button_0_2_2,
                button_0_2_3,
                button_0_2_4,
                button_0_2_5,
                button_0_2_6,
                button_0_2_7,
                button_0_2_8,
                button_0_2_9
            ),
            listOf(
                button_0_3_0,
                button_0_3_1,
                button_0_3_2,
                button_0_3_3,
                button_0_3_4,
                button_0_3_5,
                button_0_3_6,
                button_0_3_7,
                button_0_3_8,
                button_0_3_9
            ),
            listOf(
                button_0_4_0,
                button_0_4_1,
                button_0_4_2,
                button_0_4_3,
                button_0_4_4,
                button_0_4_5,
                button_0_4_6,
                button_0_4_7,
                button_0_4_8,
                button_0_4_9
            ),
            listOf(
                button_0_5_0,
                button_0_5_1,
                button_0_5_2,
                button_0_5_3,
                button_0_5_4,
                button_0_5_5,
                button_0_5_6,
                button_0_5_7,
                button_0_5_8,
                button_0_5_9
            ),
            listOf(
                button_0_6_0,
                button_0_6_1,
                button_0_6_2,
                button_0_6_3,
                button_0_6_4,
                button_0_6_5,
                button_0_6_6,
                button_0_6_7,
                button_0_6_8,
                button_0_6_9
            ),
            listOf(
                button_0_7_0,
                button_0_7_1,
                button_0_7_2,
                button_0_7_3,
                button_0_7_4,
                button_0_7_5,
                button_0_7_6,
                button_0_7_7,
                button_0_7_8,
                button_0_7_9
            ),
            listOf(
                button_0_8_0,
                button_0_8_1,
                button_0_8_2,
                button_0_8_3,
                button_0_8_4,
                button_0_8_5,
                button_0_8_6,
                button_0_8_7,
                button_0_8_8,
                button_0_8_9
            ),
            listOf(
                button_0_9_0,
                button_0_9_1,
                button_0_9_2,
                button_0_9_3,
                button_0_9_4,
                button_0_9_5,
                button_0_9_6,
                button_0_9_7,
                button_0_9_8,
                button_0_9_9
            )
        )
    }
    private val typeToDrawableId = hashMapOf(
        Cell.Empty to R.drawable.battleships_cell_empty,
        Cell.Ship to R.drawable.battleships_cell_ship,
        Cell.Miss to R.drawable.battleships_cell_miss,
        Cell.Hurt to R.drawable.battleships_cell_hurt,
        Cell.Dead to R.drawable.battleships_cell_dead
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_field, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        for(i in 0 until 10) {
            for(j in 0 until 10) {
                val cell = cells[i][j]
                cell.setOnClickListener { _ -> listener?.didSelectCell(this, Vector2(i, j)) }
            }
        }
    }

    fun drawField(field: Field) {
        for(i in 0 until field.size.x) {
            for(j in 0 until field.size.y) {
                val cell = field.cells[i][j]
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cells[i][j].background = ContextCompat.getDrawable(context!!,
                        typeToDrawableId[cell]!!)
                } else {
                    cells[i][j].setBackgroundDrawable(ContextCompat.getDrawable(context!!,
                        typeToDrawableId[cell]!!))
                }
            }
        }
    }

}
