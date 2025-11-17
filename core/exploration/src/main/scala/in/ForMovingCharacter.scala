package in

import actions.NextAction
import model.CardinalDirection

trait   ForMovingCharacter:
  def move(cardinalDirection: CardinalDirection): NextAction
