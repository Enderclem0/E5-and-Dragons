package data

import model.DndCharacter
import model.DndMapState
import out.{CombatDataPortOut, ExplorationDataPortOut}

class MutableCollectionDataStorageAdapter extends ExplorationDataPortOut, CombatDataPortOut:
  override def saveMapState(dndMap: DndMapState): Unit                                     = ???
  override def saveCharacterState(dndCharacter: DndCharacter, villain: DndCharacter): Unit = ???
