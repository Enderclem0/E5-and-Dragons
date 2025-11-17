package model

import model.DndCharacter

//Idée de départ matrice d'un type unifié Element
// -> problème pour les copies et la quantité de mémoire
// -> on préfère stocker la taille de la grille et chaque type d'élément dans une map
case class DndMapState(
    width:                  Int,
    height:                 Int,
    currentPlayedCharacter: String,
    characters:             Map[Coordinates, PlayableCharacter],
    villains:               Map[Coordinates, DndCharacter],
    npcs:                   Map[Coordinates, NPC],
    goldPiles:              Map[Coordinates, GoldPile]
)
