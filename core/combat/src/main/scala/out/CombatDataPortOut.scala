package out

import model.DndCharacter

trait CombatDataPortOut:
  def saveCharacterState(dndCharacter: DndCharacter, villain: DndCharacter): Unit
