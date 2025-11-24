package model

import model.DndCharacter

//Idée de départ matrice d'un type unifié Element
// -> problème pour les copies
// -> on préfère stocker la taille de la grille et chaque type d'élément dans une map
case class DndMapState(
    width:                  Int,
    height:                 Int,
    currentPlayedCharacter: String,
    characters:             Map[Coordinates, PlayableCharacter], //Pour l'instant un seul possible, mais extendable
    villains:               Map[Coordinates, DndCharacter],
    npcs:                   Map[Coordinates, NPC],
    goldPiles:              Map[Coordinates, GoldPile]
)
