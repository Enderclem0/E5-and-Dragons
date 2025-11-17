package model

import actions.CombatAction
import rolls.Die.D6

enum DndClass:
  case PALADIN(lvl: Int) extends DndClass

  def action: CombatAction =
    this match
      case DndClass.PALADIN(_) => CombatAction(2, D6)

  def bonusAction: Option[CombatAction] =
    this match
      case DndClass.PALADIN(lvl) => if lvl > 3 then Some(CombatAction(1, D6)) else None
