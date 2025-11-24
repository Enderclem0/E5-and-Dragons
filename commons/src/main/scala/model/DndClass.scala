package model

import actions.CombatAction
import rolls.Die.{D4, D6, D8}

enum DndClass:
  case BARBARIAN(lvl: Int) extends DndClass
  case BARD(lvl: Int) extends DndClass
  case CLERIC(lvl: Int) extends DndClass
  case DRUID(lvl: Int) extends DndClass
  case SORCERER(lvl: Int) extends DndClass
  case WARRIOR(lvl: Int) extends DndClass
  case WIZARD(lvl: Int) extends DndClass
  case MONK(lvl: Int) extends DndClass
  case WARLOCK(lvl: Int) extends DndClass
  case PALADIN(lvl: Int) extends DndClass
  case RANGER(lvl: Int) extends DndClass
  case THIEF(lvl: Int) extends DndClass

  def action: CombatAction =
    this match
      case DndClass.PALADIN(_) => CombatAction(2, D6)
      case DndClass.BARBARIAN(_) => CombatAction(2, D8)
      case DndClass.WARRIOR(_) => CombatAction(2, D8)
      case DndClass.MONK(_) => CombatAction(2, D4)
      case DndClass.RANGER(_) => CombatAction(2, D6)
      case DndClass.THIEF(_) => CombatAction(1, D6)
      case DndClass.BARD(_) => CombatAction(1, D6)
      case DndClass.CLERIC(_) => CombatAction(1, D6)
      case DndClass.DRUID(_) => CombatAction(1, D6)
      case DndClass.WIZARD(_) => CombatAction(1, D4)
      case DndClass.SORCERER(_) => CombatAction(1, D4)
      case DndClass.WARLOCK(_) => CombatAction(1, D6)

  def bonusAction: Option[CombatAction] =
    this match
      case DndClass.PALADIN(lvl) => if lvl > 3 then Some(CombatAction(1, D6)) else None
      case DndClass.MONK(lvl) => Some(CombatAction(1, D4)) // monks always get a bonus action punch
      case DndClass.THIEF(lvl) => if lvl >= 2 then Some(CombatAction(1, D6)) else None
      case DndClass.BARBARIAN(lvl) => if lvl >= 5 then Some(CombatAction(1, D6)) else None
      case DndClass.WARRIOR(lvl) => if lvl >= 5 then Some(CombatAction(1, D6)) else None
      case DndClass.BARD(_) => None
      case DndClass.CLERIC(_) => None
      case DndClass.DRUID(_) => None
      case DndClass.WIZARD(_) => None
      case DndClass.SORCERER(_) => None
      case DndClass.WARLOCK(_) => None
      case DndClass.RANGER(_) => None
