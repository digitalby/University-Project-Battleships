package me.digitalby.lr5

class Field(val size: Vector2, private val allDeadCondition: Int) {

    companion object {
        fun fromFieldString(fieldString: String):Field {
            val emptyField = Field(Vector2(0,0), 0)
            val lines = fieldString.split('\n')
            if(lines.isEmpty())
                return emptyField
            val columns = lines[0].length
            val rows = lines.count()
            val totalNonEmpty = columns * rows - fieldString.count { c -> c == 'E' || c == 'M' }
            val ret = Field(Vector2(rows, columns), totalNonEmpty)
            for(i in 0 until rows) {
                for(j in 0 until columns) {
                    ret.cells[i][j] = when(lines[i][j]) {
                        'D' -> Cell.Dead
                        'H' -> Cell.Hurt
                        'M' -> Cell.Miss
                        'S' -> Cell.Ship
                        else -> Cell.Empty
                    }
                }
            }
            return ret
        }

        fun toFieldString(field: Field): String {
            var ret = ""
            for(i in 0 until field.size.x) {
                for(j in 0 until field.size.y) {
                    ret += when(field.cells[i][j]) {
                        Cell.Empty -> 'E'
                        Cell.Ship -> 'S'
                        Cell.Miss -> 'M'
                        Cell.Hurt -> 'H'
                        Cell.Dead -> 'D'
                    }
                }
                if(i != field.size.x - 1)
                    ret += '\n'
            }
            return ret
        }
    }

    val allDead: Boolean
    get() {
        return flatCells.count { cell -> cell == Cell.Dead } == allDeadCondition
    }

    val flatCells: List<Cell>
    get() {
        return cells.flatten()
    }

    val cells by lazy {
        makeField(size)
    }

    private fun makeField(size: Vector2): ArrayList<ArrayList<Cell>> {
        val ret = ArrayList<ArrayList<Cell>>()
        for(i in 0 until size.x) {
            val inner = ArrayList<Cell>()
            for(j in 0 until size.y) {
                val cell = Cell.Empty
                inner.add(cell)
            }
            ret.add(inner)
        }
        return ret
    }

    fun getCell(position: Vector2): Cell {
        return cells[position.x][position.y]
    }

    fun getCell(x: Int, y: Int): Cell {
        val position = Vector2(x, y)
        return getCell(position)
    }

}