package me.digitalby.lr5

class Ship(val origin: Vector2, val size: Vector2) {

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
            val ret = this.vicinity
            val points = this.points
            for(vec2 in ret) {
                if(vec2 in points)
                    ret.remove(vec2)
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