package me.digitalby.lr5

class Ship(val origin: Vector2, val size: Vector2) {

    companion object {
        fun shipTypeToSize(shipType: ShipType): Vector2? {
            //HACK
            return when(shipType) {
                ShipType.Ship4Vertical -> Vector2(4,1)
                ShipType.Ship4Horizontal -> Vector2(1,4 )
                ShipType.Ship3Vertical -> Vector2(3,1)
                ShipType.Ship3Horizontal -> Vector2(1,3)
                ShipType.Ship2Vertical -> Vector2(2,1)
                ShipType.Ship2Horizontal -> Vector2(1,2)
                ShipType.Ship1 -> Vector2(1, 1)
                ShipType.Ship2 -> null
                ShipType.Ship3 -> null
                ShipType.Ship4 -> null
            }
        }

        fun simplifyShipType(shipType: ShipType): ShipType {
            return when(shipType) {
                ShipType.Ship4Vertical -> ShipType.Ship4
                ShipType.Ship4Horizontal -> ShipType.Ship4
                ShipType.Ship3Vertical -> ShipType.Ship3
                ShipType.Ship3Horizontal -> ShipType.Ship3
                ShipType.Ship2Vertical -> ShipType.Ship2
                ShipType.Ship2Horizontal -> ShipType.Ship2
                ShipType.Ship1 -> ShipType.Ship1
                ShipType.Ship2 -> ShipType.Ship2
                ShipType.Ship3 -> ShipType.Ship3
                ShipType.Ship4 -> ShipType.Ship4
            }
        }
    }

    var type: ShipType? = null

    val points: ArrayList<Vector2>
        get() {
            val ret = arrayListOf<Vector2>()
            for(i in origin.x until origin.x + size.x) {
                for(j in origin.y until origin.y + size.y) {
                    val vec2 = Vector2(i,j)
                    ret.add(vec2)
                }
            }
            return ret
        }

    val adjacent: ArrayList<Vector2>
        get() {
            val vicinity = this.vicinity
            val ret = arrayListOf<Vector2>()
            val points = this.points
            for(vec2 in vicinity) {
                if(points.contains(vec2))
                    continue
                ret.add(vec2)
            }
            return ret
        }

    val vicinity: ArrayList<Vector2>
        get() {
            val ret = arrayListOf<Vector2>()
            for(i in origin.x - 1 .. origin.x + size.x) {
                for(j in origin.y - 1 .. origin.y + size.y) {
                    val vec2 = Vector2(i, j)

                    ret.add(vec2)
                }
            }
            return ret
        }

}