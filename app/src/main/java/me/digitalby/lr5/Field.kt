package me.digitalby.lr5

class Field(val size: Vector2, private val allDeadCondition: Int) {

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