package me.digitalby.lr5

interface FieldFragmentListener {
    var field: Field
    fun didSelectCell(sender: FieldFragment, position: Vector2)
}