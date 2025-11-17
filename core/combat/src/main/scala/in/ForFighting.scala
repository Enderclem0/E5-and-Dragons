package in

import model.DndCharacter
import errors.Death

type NewCharacterState = DndCharacter

trait ForFighting:
  /**
   * First, both characters roll a d20 to determine their turn order.
   * Then, following that order, each character typically uses their
   * primary Action to attack, which requires rolling a d20 to hit the
   * target's Armor Class (AC); if that roll is successful, they then
   * roll the specified dice for damage. Finally, if an ability or spell
   * allows for a Bonus Action, the character rolls the die (or dice)
   * associated with that Bonus Action effect.
   */
  def fight(character: DndCharacter, villain: DndCharacter): Either[Death, NewCharacterState]
