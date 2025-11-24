package domain

import errors.MapError
import model.*

import scala.util.Try

object MapParser:

  def parse(lines: List[String]): Either[MapError, DndMapState] =
    val builtMapOrError =
      lines.foldLeft[Either[MapError, BeingBuiltMap]](Right(BeingBuiltMap())) { (currentRes, line) =>
        currentRes.flatMap(map => parseLine(map, line))
      }

    builtMapOrError.flatMap(buildFinalState)

  // Etat intermédiaire de la map en construction
  private case class BeingBuiltMap(
      width:      Int = 0,
      height:     Int = 0,
      characters: Map[Coordinates, PlayableCharacter] = Map.empty,
      villains:   Map[Coordinates, DndCharacter] = Map.empty,
      npcs:       Map[Coordinates, NPC] = Map.empty,
      goldPiles:  Map[Coordinates, GoldPile] = Map.empty
  )

  //Support de - et - avec espaces autour pour les nombres négatifs
  private def tokenize(line: String): List[String] = line.split("\\s+-\\s+").map(_.trim).toList

  private def parseLine(map: BeingBuiltMap, l: String): Either[MapError, BeingBuiltMap] =
    val line = l.trim
    if line.isEmpty || line.startsWith("--") then Right(map)
    else
      tokenize(line) match
        case "M" :: w :: h :: Nil =>
          if map.width > 0 || map.height > 0 then Left(MapError.IllegalMapFormat("Map size already set"))
          else
            for
              width  <- safeStrictlyPositiveInt(w)
              height <- safeStrictlyPositiveInt(h)
            yield map.copy(width = width, height = height)

        case "NPC" :: x :: y :: Nil =>
          for
            coordX <- safePositiveInt(x)
            coordY <- safePositiveInt(y)
          yield
            val id = s"npc_${map.npcs.size + 1}"
            map.copy(npcs = map.npcs + (Coordinates(coordX, coordY) -> NPC(id)))

        case "PC" :: x :: y :: lvl :: race :: cls :: ac :: hp :: Nil =>
          for
            coordX     <- safePositiveInt(x)
            coordY     <- safePositiveInt(y)
            level      <- safeStrictlyPositiveInt(lvl)
            armor      <- safeStrictlyPositiveInt(ac)
            health     <- safeStrictlyPositiveInt(hp)
            raceParsed <- safeParseRace(race)
            clsParsed  <- safeParseClass(cls, level)
          yield
            val coord = Coordinates(coordX, coordY)
            val enemy = DndCharacter(raceParsed, clsParsed, "Enemy", armor, health, 0)
            map.copy(villains = map.villains + (coord -> enemy))

        case "C" :: x :: y :: lvl :: race :: cls :: ac :: hp :: orientation :: Nil =>
          for
            coordX            <- safePositiveInt(x)
            coordY            <- safePositiveInt(y)
            level             <- safeStrictlyPositiveInt(lvl)
            armor             <- safeStrictlyPositiveInt(ac)
            health            <- safeStrictlyPositiveInt(hp)
            raceParsed        <- safeParseRace(race)
            clsParsed         <- safeParseClass(cls, level)
            orientationParsed <- safeParseOrientation(orientation)
          yield
            val coord        = Coordinates(coordX, coordY)
            val dndCharacter = DndCharacter(raceParsed, clsParsed, "Player", armor, health, health)
            val charId       = s"pc_${map.characters.size + 1}"
            val character    = PlayableCharacter(charId, dndCharacter, orientationParsed)
            map.copy(characters = map.characters + (coord -> character))

        case "GP" :: x :: y :: amount :: Nil =>
          for
            coordX <- safePositiveInt(x)
            coordY <- safePositiveInt(y)
            amt    <- safeStrictlyPositiveInt(amount)
          yield
            val coord = Coordinates(coordX, coordY)
            map.copy(goldPiles = map.goldPiles + (coord -> GoldPile(amt)))

        case _ => Left(MapError.IllegalMapFormat("Unrecognized line format"))

  private def buildFinalState(built: BeingBuiltMap): Either[MapError, DndMapState] =
    if built.width <= 0 || built.height <= 0 then Left(MapError.IllegalMapFormat("Map size not properly defined"))
    else if built.characters.isEmpty then Left(MapError.IllegalMapFormat("No playable character defined"))
    else
      val playerId = built.characters.values.head.id
      Right(
        DndMapState(
          width                  = built.width,
          height                 = built.height,
          currentPlayedCharacter = playerId,
          characters             = built.characters,
          villains               = built.villains,
          npcs                   = built.npcs,
          goldPiles              = built.goldPiles
        )
      )

  private def safeToInt(token: String): Either[MapError, Int] =
    Try(token.toInt).toEither.left.map(_ => MapError.IllegalMapFormat(s"Invalid integer value: $token"))

  // Pour Coordonnées (x, y) et GoldPile : Accepte 0 et +
  private def safePositiveInt(token: String): Either[MapError, Int] =
    safeToInt(token).flatMap { i =>
      if i < 0 then Left(MapError.IllegalMapFormat(s"Value cannot be negative: $token"))
      else Right(i)
    }

  // Pour Dimensions, Niveau, PV, AC : Refuse 0 et -
  private def safeStrictlyPositiveInt(token: String): Either[MapError, Int] =
    safeToInt(token).flatMap { i =>
      if i <= 0 then Left(MapError.IllegalMapFormat(s"Value must be strictly positive (>0): $token"))
      else Right(i)
    }

  private def safeParseRace(token: String): Either[MapError, DndRace] =
    Try(DndRace.valueOf(token)).toEither.left.map(_ => MapError.IllegalMapFormat(s"Unrecognized race: $token"))

  private def safeParseClass(token: String, level: Int): Either[MapError, DndClass] =
    token match
      case "PALADIN" => Right(DndClass.PALADIN(level))
      case other     => Left(MapError.IllegalMapFormat(s"Unrecognized class: $other"))

  private def safeParseOrientation(token: String): Either[MapError, CardinalDirection] =
    token match
      case "S" => Right(CardinalDirection.SOUTH)
      case "N" => Right(CardinalDirection.NORTH)
      case "E" => Right(CardinalDirection.EAST)
      case "W" => Right(CardinalDirection.WEST)
      case _   => Left(MapError.IllegalMapFormat(s"Unrecognized orientation: $token"))
