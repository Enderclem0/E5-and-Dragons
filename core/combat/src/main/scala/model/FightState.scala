package model

enum Turn:
  case PlayerTurn, VillainTurn

case class FightState(
    player: PlayableCharacter,
    villain: DndCharacter,

    currentPlayerHp: Int,
    currentVillainHp: Int,

    turnOrder: Turn, //Who started first
    currentTurn: Turn,

    lastAction: String //Pour l'affichage des évenements
)
