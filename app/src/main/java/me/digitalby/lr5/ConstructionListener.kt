package me.digitalby.lr5

import androidx.fragment.app.Fragment

interface ConstructionListener {
    //var selectedConstructionShipType: ShipType?
    var blueprint: Blueprint?
    fun didSelectShip(sender: ConstructionFragment, ship: ShipType?)
}