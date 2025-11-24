package domain

import model.*
import munit.FunSuite
import errors.MapError

class MapParserTest extends FunSuite:

  val validMapLines = List(
    "M - 10 - 10",
    "-- Commentaire",
    "C - 1 - 1 - 5 - HUMAN - PALADIN - 15 - 50 - S",
    "PC - 5 - 5 - 3 - HUMAN - PALADIN - 12 - 30",
    "PC - 2 - 2 - 2 - HUMAN - PALADIN - 10 - 20",
    "NPC - 2 - 2",
    "-- Un autre commentaire",
    "  ",
    "NPC - 4 - 4",
    "GP - 3 - 3 - 100",
    "GP - 6 - 6 - 50"
  )

  test("Valid map should be parsed correctly") {
    val result = MapParser.parse(validMapLines)

    assert(result.isRight, "Le parsing devrait réussir")

    result.map { state =>
      // Vérification des dimensions
      assertEquals(state.width, 10)
      assertEquals(state.height, 10)

      // Vérification du joueur
      val playerOpt = state.characters.get(Coordinates(1, 1))
      assert(playerOpt.isDefined)
      assertEquals(playerOpt.get.orientation, CardinalDirection.SOUTH)
      assertEquals(playerOpt.get.stats.hp, 50)

      // Vérification des méchants
      val villainOpt = state.villains.get(Coordinates(5, 5))
      assert(villainOpt.isDefined)
      assertEquals(villainOpt.get.hp, 30)

      val villainOpt2 = state.villains.get(Coordinates(2, 2))
      assert(villainOpt2.isDefined)
      assertEquals(villainOpt2.get.hp, 20)

      // Vérification des NPC
      assert(state.npcs.contains(Coordinates(2, 2)))
      assert(state.npcs.contains(Coordinates(4, 4)))

      // Vérification de l'or
      val goldOpt = state.goldPiles.get(Coordinates(3, 3))
      assert(goldOpt.isDefined)
      assertEquals(goldOpt.get.amount, 100)

      val goldOpt2 = state.goldPiles.get(Coordinates(6, 6))
      assert(goldOpt2.isDefined)
      assertEquals(goldOpt2.get.amount, 50)
    }
  }


  CardinalDirection.values.foreach { direction =>
    val inputLetter = direction.toString.substring(0, 1)

    test(s"Should parse direction correctly for input '$inputLetter' -> $direction") {
      val line = s"C - 1 - 1 - 1 - HUMAN - PALADIN - 10 - 10 - $inputLetter"
      val result = MapParser.parse(List("M - 5 - 5", line))

      assert(result.isRight)
      val character = result.toOption.get.characters.values.head
      assertEquals(character.orientation, direction)
    }
  }


  case class TestCase(
                       name: String,
                       inputLine: List[String],
                       expectedMessagePart: String,
                       defaultLine: Boolean = true
                     )

  val errorCases = List(
    TestCase(
      "Missing Map dimensions",
      List("C - 1 - 1 - 5 - HUMAN - PALADIN - 10 - 10 - S"),
      "Map size not properly defined",
      false
    ),
    TestCase(
      "Invalid Map dimensions (negative)",
      List("M - -10 - 10"),
      "Value must be strictly positive",
      false
    ),
    TestCase(
      "Multiple Map dimensions",
      List("M - 10 - 10"), // defaultLine=true -> "M - 5 - 5" before
      "Map size already set"
    ),

    TestCase(
      "Missing coordinates (bad format)",
      List("C - NAN - 5 - 1 - HUMAN - PALADIN - 10 - 10 - S"),
      "Invalid integer value"
    ),
    TestCase(
      "Wrong Line Format",
      List("C : 1 : 1"),
      "Unrecognized line format"
    ),

    // Cas d'Enums
    TestCase(
      "Invalid Race",
      List("C - 1 - 1 - 1 - ORC - PALADIN - 10 - 10 - S"),
      "Unrecognized race: ORC"
    ),
    TestCase(
      "Invalid Class",
      List("C - 1 - 1 - 1 - HUMAN - NINJA - 10 - 10 - S"),
      "Unrecognized class: NINJA"
    ),
    TestCase(
      "Invalid Orientation",
      List("C - 1 - 1 - 1 - HUMAN - PALADIN - 10 - 10 - UP"),
      "Unrecognized orientation: UP"
    ),

    TestCase(
      "Negative Level",
      List("C - 1 - 1 - -5 - HUMAN - PALADIN - 10 - 10 - S"),
      "Value must be strictly positive"
    ),
    TestCase(
      "Missing Player",
      List(),
      "No playable character defined"
    ),
    TestCase(
      "Invalid Gold Amount (Negative)",
      List("GP - 3 - 3 - -100"),
      "Value must be strictly positive"
    ),
    TestCase(
      "Negative Coordinate",
      List("NPC - -2 - 5"),
      "Value cannot be negative"
    )
  )

  errorCases.foreach { testCase =>
    test(s"Parser fails for: ${testCase.name}") {
      val lines = if testCase.defaultLine then
        "M - 5 - 5" :: testCase.inputLine
      else
        testCase.inputLine

      val result = MapParser.parse(lines)

      assert(result.isLeft, s"Le parsing aurait dû échouer pour : ${testCase.name}")

      val error = result.left.toOption.get

      assert(error.isInstanceOf[MapError.IllegalMapFormat])

      assert(
        error.getMessage.contains(testCase.expectedMessagePart),
        s"""
           |Message d'erreur incorrect pour '${testCase.name}'.
           |Attendu contenant : "${testCase.expectedMessagePart}"
           |Reçu              : "${error.getMessage}"
           |""".stripMargin
      )
    }
  }