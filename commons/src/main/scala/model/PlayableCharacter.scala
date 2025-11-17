package model

case class PlayableCharacter(
    id:          String,
    stats:       DndCharacter,
    orientation: CardinalDirection
)
