package domain

import errors.MapError
import in.ForValidatingMap
import model.DndMapState
import out.ExplorationDataPortOut

class MapManager(explorationDataPortOut: ExplorationDataPortOut) extends ForValidatingMap:

  override def validateAndStoreMap(dataLines: List[String]): Either[MapError, Unit] =
    val parseResult: Either[MapError, DndMapState] = MapParser.parse(dataLines)

    parseResult.flatMap { map =>
      explorationDataPortOut.saveMapState(map)
      Right(())
    }