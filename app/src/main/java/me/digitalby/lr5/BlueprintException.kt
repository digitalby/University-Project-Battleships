package me.digitalby.lr5

open class BlueprintException(message: String) : Exception(message)

class BlueprintOutOfBoundsException(message: String) : BlueprintException(message)

class BlueprintIntersectionException(message: String) : BlueprintException(message)

class BlueprintNumberOfShipsException(message: String) : BlueprintException(message)