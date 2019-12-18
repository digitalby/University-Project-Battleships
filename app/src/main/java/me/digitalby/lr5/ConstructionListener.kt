package me.digitalby.lr5

import androidx.fragment.app.Fragment

interface ConstructionListener {
    fun didSelectShip(sender: ConstructionFragment, ship: ShipType?)
}