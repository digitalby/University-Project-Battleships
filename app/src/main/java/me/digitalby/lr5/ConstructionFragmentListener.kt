package me.digitalby.lr5

import androidx.fragment.app.Fragment

interface ConstructionFragmentListener {
    //var selectedConstructionShipType: ShipType?
    var blueprint: Blueprint
    fun didSelectShip(sender: ConstructionFragment, ship: ShipType?)
}