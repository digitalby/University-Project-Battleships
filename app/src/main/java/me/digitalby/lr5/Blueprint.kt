package me.digitalby.lr5

class Blueprint(val size: Vector2,
                val rules: HashMap<ShipType, Int>? = null,
                private val ships:ArrayList<Ship> = ArrayList<Ship>()) {

    var listener: BlueprintListener? = null
    set(value) {
        field = value
        field?.didChangeShips(this)
    }

    val maximumShips: Int
    get() {
        if(rules == null)
            return Int.MAX_VALUE
        return rules.values.sum()
    }

    val valid: Boolean
    get() {
        if(rules.isNullOrEmpty())
            return true
        for(rule in rules) {
            if(ships.count { ship -> ship.type == rule.key } != rule.value)
                return false
        }
        return true
    }

    val currentRules: HashMap<ShipType, Int>
    get() {
        return hashMapOf(
            ShipType.Ship4 to ships.count { ship -> ship.type in listOf(ShipType.Ship4, ShipType.Ship4Horizontal, ShipType.Ship4Vertical) },
            ShipType.Ship3 to ships.count { ship -> ship.type in listOf(ShipType.Ship3, ShipType.Ship3Horizontal, ShipType.Ship3Vertical) },
            ShipType.Ship2 to ships.count { ship -> ship.type in listOf(ShipType.Ship2, ShipType.Ship2Horizontal, ShipType.Ship2Vertical) },
            ShipType.Ship1 to ships.count { ship -> ship.type == ShipType.Ship1 }
        )
    }

    fun getShips(): ArrayList<Ship> {
        return ships
    }

    fun tryAddShip(ship: Ship) {
        if(rules != null) {
            if(maximumShips == ships.count())
                throw BlueprintNumberOfShipsException("Cannot place any more ships" +
                        " (maximum is $maximumShips).")
            val shipsOfType = currentRules[ship.type]!!
            val maxShipsOfType = rules[ship.type]
            if(maxShipsOfType != null && shipsOfType >= maxShipsOfType)
                throw BlueprintNumberOfShipsException("Cannot place any more ships of type ${ship.type.toString()}" +
                        " (maximum is $maxShipsOfType).")
        }
        if(ship.points.count { point ->
            point.x <= -1 ||
            point.x >= size.x ||
            point.y <= -1 ||
            point.y >= size.y
            } != 0)
            throw BlueprintOutOfBoundsException("Cannot place ships outside of play field.")
        ships.forEach { existingShip ->
            ship.points.forEach { point ->

                if(point in existingShip.points) {
                    throw BlueprintIntersectionException("Cannot intersect other ships.")
                } else if(point in existingShip.adjacent) {
                    throw BlueprintIntersectionException("Cannot place next to other ships.")
                }
            }
        }
        listener?.didChangeShips(this)
        ships.add(ship)
    }

    fun removeShip(position: Vector2) {
        val shipToRemove = ships.filter { ship -> ship.points.contains(position) }
        listener?.didChangeShips(this)
        if(shipToRemove.isNotEmpty())
            ships.remove(shipToRemove[0])
    }

    fun makeField(): Field {
        var totalCells = 0
        for(ship in ships) {
            totalCells += ship.size.x * ship.size.y
        }
        val field = Field(size, totalCells)
        ships.forEach { ship ->
            ship.points.forEach { vec2 ->
                field.cells[vec2.x][vec2.y] = Cell.Ship
            }
        }
        return field
    }
}