package domain

import actions.NextAction
import in.ForMovingCharacter
import model.CardinalDirection

class MovementEngine() extends ForMovingCharacter:
  override def move(cardinalDirection: CardinalDirection): NextAction = ???
