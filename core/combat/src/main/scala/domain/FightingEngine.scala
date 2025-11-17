package domain

import model.DndCharacter
import errors.Death
import in.{ForFighting, NewCharacterState}
import out.{RandomnessPortOut, FightRenderingPortOut}

class FightingEngine(randomnessPortOut: RandomnessPortOut, renderingPortOut: FightRenderingPortOut) extends ForFighting:
  override def fight(character1: DndCharacter, character2: DndCharacter): Either[Death, NewCharacterState] = ???
