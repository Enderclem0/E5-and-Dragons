# Rapport d'Implémentation - E5 and Dragons (END)

**Date du rapport:** 17 novembre 2025  
**Projet:** E5 and Dragons - Jeu de type Donjons & Dragons en ligne de commande  
**Architecture:** Hexagonale (Ports & Adapters) avec Scala 3.7.3

Rapport généré par intelligence artificielle

---

## 📋 Vue d'Ensemble

Ce projet suit une architecture hexagonale claire avec :
- **commons/** : Modèles de domaine partagés
- **core/** : Logique métier (combat, exploration, social-interaction)
- **infra/** : Adaptateurs pour l'infrastructure (I/O, stockage, randomisation)
- **app/end-game/** : Application principale

**Objectif du projet:** Créer un jeu où le joueur peut parser une carte, déplacer son personnage, combattre des PCs, parler à des NPCs (optionnel), collecter des trésors, et afficher chaque étape dans la console.

---

## ❌ Implémentations Manquantes

### 🔴 CRITIQUE - Main Application (app/end-game)

#### `Main.scala`
**État actuel:**
```scala
@main def Main(): Unit = println("hello world")
```

**Manque:**
- ❌ Parsing du fichier `e5-dungeon.dndmap` depuis les ressources
- ❌ Initialisation de tous les composants (adapters, engines, managers)
- ❌ Boucle de jeu principale (game loop)
- ❌ Gestion des actions utilisateur (MOVE, FIGHT, TALK, LOOT)
- ❌ Orchestration entre les différents modules (exploration, combat, interaction sociale)
- ❌ Gestion des erreurs et de la fin de jeu
- ❌ Câblage des dépendances (dependency injection)

**Doit implémenter:**
1. Lecture et validation de la carte via `ForValidatingMap`
2. Boucle de jeu qui attend les inputs utilisateur
3. Routage des actions vers les bons modules
4. Rendu de l'état du jeu après chaque action
5. Gestion de la condition de fin (mort du personnage, victoire)

---

### 🔴 CRITIQUE - Module Exploration (core/exploration)

#### `MapManager.scala`
**État actuel:** `??? (non implémenté)`

**Manque:**
- ❌ Parsing du format `.dndmap`
- ❌ Validation de la structure de la carte (dimensions, format)
- ❌ Vérification de la présence obligatoire (dimensions M, personnage C)
- ❌ Validation des coordonnées (dans les limites de la carte)
- ❌ Gestion des éléments : NPC, PC (villains), GP (gold pieces), Character
- ❌ Stockage de l'état de la carte via `ExplorationDataPortOut`

**Format à parser:**
```
M - width - height
NPC - x - y
PC - x - y - lvl - race - class - AC - HP
C - x - y - lvl - race - class - AC - HP - orientation
GP - x - y - amount
```

#### `MovementEngine.scala`
**État actuel:** `??? (non implémenté)`

**Manque:**
- ❌ Gestion des déplacements selon `CardinalDirection` (NORTH, SOUTH, EAST, WEST)
- ❌ Vérification des limites de la carte
- ❌ Détection de collision avec les éléments de la carte
- ❌ Mise à jour de la position du personnage
- ❌ Détermination de la prochaine action basée sur la case (vide, NPC, PC, GP)
- ❌ Retour du `NextAction` approprié (MOVE, FIGHT, TALK, LOOT)
- ❌ Mise à jour de l'orientation du personnage

#### `DndMapState.scala`
**État actuel:** Case class vide

**Manque:**
- ❌ Dimensions de la carte (width, height)
- ❌ Position du personnage (x, y, orientation)
- ❌ Liste des NPCs avec positions
- ❌ Liste des PCs (villains) avec positions et caractéristiques
- ❌ Liste des trésors (GP) avec positions et montants
- ❌ Méthodes utilitaires pour accéder aux éléments par position

---

### 🔴 CRITIQUE - Module Combat (core/combat)

#### `FightingEngine.scala`
**État actuel:** `??? (non implémenté)`

**Manque:**
- ❌ Initiative : lancer d20 pour les deux personnages pour déterminer l'ordre
- ❌ Tour de combat : attaque avec d20 pour toucher l'AC
- ❌ Calcul des dégâts basé sur `CombatAction` (diceAmount, diceRoll)
- ❌ Gestion des bonus actions (selon la classe et le niveau)
- ❌ Mise à jour des HP des personnages
- ❌ Détection de la mort (HP <= 0)
- ❌ Gestion de l'or du villain vaincu
- ❌ Rendu des états de combat via `FightRenderingPortOut`
- ❌ Sauvegarde de l'état via `CombatDataPortOut`

**Logique D&D à implémenter:**
1. Lancer d20 pour l'initiative (le plus haut commence)
2. Tour d'attaque : d20 + modificateurs >= AC de la cible
3. Si touché : lancer les dés de dégâts (selon la classe)
4. Appliquer les dégâts aux HP
5. Gérer les bonus actions si disponibles
6. Alterner jusqu'à ce qu'un personnage tombe à 0 HP

#### `FightState.scala`
**État actuel:** Case class vide avec TODO

**Manque:**
- ❌ État des deux combattants (character, villain)
- ❌ HP actuels de chaque combattant
- ❌ Ordre d'initiative
- ❌ Tour actuel
- ❌ Dernière action effectuée
- ❌ Résultat des lancers de dés

---

### 🔴 CRITIQUE - Module Social Interaction (core/social-interaction)

**État:** Module quasi-vide, marqué comme optionnel mais structure présente

#### `ForInteracting.scala`
**État actuel:** Trait vide

**Manque:**
- ❌ Méthode `interact(npc: ???): ???`
- ❌ Gestion des dialogues avec les NPCs
- ❌ Système de quêtes ou informations (optionnel)

#### `ForGeneratingCharacters.scala`
**État actuel:** Trait vide

**Manque:**
- ❌ Génération aléatoire de personnages
- ❌ Génération d'attributs basés sur la race/classe
- ❌ Génération de NPCs avec caractéristiques

**Note:** Ce module semble optionnel selon le README mais la structure est en place.

---

### 🟠 IMPORTANT - Module Infrastructure (infra)

#### `MachineDefaultRandomnessAdapter.scala`
**État actuel:** `??? (non implémenté)`

**Manque:**
- ❌ Implémentation de `getRandom(die: Die): Int`
- ❌ Support pour D20 (1-20)
- ❌ Support pour D6 (1-6)
- ❌ Utilisation de `scala.util.Random` ou équivalent

**Implémentation suggérée:**
```scala
override def getRandom(die: Die): Int = 
  die match
    case Die.D20 => scala.util.Random.nextInt(20) + 1
    case Die.D6  => scala.util.Random.nextInt(6) + 1
```

#### `ConsoleRenderingAdapter.scala`
**État actuel:** `??? (non implémenté)`

**Manque:**
- ❌ Rendu de la carte avec symboles ASCII
- ❌ Affichage de la position du personnage
- ❌ Affichage des NPCs, PCs, trésors
- ❌ Rendu de l'état du combat (tours, HP, actions)
- ❌ Messages formatés pour les actions
- ❌ Affichage de l'interface utilisateur

**Doit afficher:**
- Carte en mode texte avec symboles (P pour player, N pour NPC, V pour villain, G pour gold)
- Stats du personnage (HP, AC, Gold)
- Résultats des combats (jets de dés, dégâts)
- Messages d'événements

#### `MutableCollectionDataStorageAdapter.scala`
**État actuel:** `??? (non implémenté)`

**Manque:**
- ❌ Stockage en mémoire de l'état de la carte (`DndMapState`)
- ❌ Stockage des états des personnages
- ❌ Méthodes de récupération des données
- ❌ Utilisation de collections mutables (Map, Buffer, etc.)

---

### 🟡 MOYEN - Module Commons

#### `Die.scala`
**État actuel:** Enum avec seulement D20 et D6

**Pourrait ajouter:**
- ⚠️ D4, D8, D10, D12 pour extension future
- ⚠️ Méthode `maxValue: Int` pour obtenir la valeur max du dé

#### `DndRace.scala`
**État actuel:** Seulement HUMAN

**Manque pour un jeu complet:**
- ⚠️ ELF, DWARF, HALFLING, etc.
- ⚠️ Modificateurs de race (bonus/malus selon D&D)

#### `DndClass.scala`
**État actuel:** Seulement PALADIN

**Manque pour un jeu complet:**
- ⚠️ WARRIOR, MAGE, ROGUE, CLERIC, etc.
- ⚠️ Actions spécifiques par classe

**Note:** Ces extensions ne sont probablement pas nécessaires pour le MVP.

#### `MapError.scala`
**État actuel:** Seulement `IllegalMapFormat`

**Pourrait ajouter:**
- ⚠️ `InvalidCoordinates` pour coordonnées hors limites
- ⚠️ `MissingRequiredElement` pour éléments obligatoires manquants
- ⚠️ `DuplicateElement` pour éléments en double

---

### 🟢 BONUS - Tests

**État actuel:** Aucun test présent

**Manque:**
- ❌ Tests unitaires pour `MapManager` (parsing de cartes valides/invalides)
- ❌ Tests unitaires pour `MovementEngine` (limites, collisions)
- ❌ Tests unitaires pour `FightingEngine` (logique de combat)
- ❌ Tests d'intégration pour le flux complet
- ❌ Tests pour les adapters

**Framework disponible:** MUnit (déjà dans les dépendances)

**Recommandation:** Au minimum, tester :
1. Parsing de carte valide/invalide
2. Déplacements valides/invalides
3. Logique de combat de base

---

## 📊 Résumé des Priorités

### 🔴 PRIORITÉ 1 - BLOQUANT (Must Have)
1. **`Main.scala`** - Point d'entrée complet avec game loop
2. **`MapManager.validateAndStoreMap`** - Parsing et validation de la carte
3. **`MovementEngine.move`** - Gestion des déplacements
4. **`FightingEngine.fight`** - Logique de combat complète
5. **`DndMapState`** - Modèle de données pour la carte
6. **`FightState`** - Modèle de données pour le combat
7. **`MachineDefaultRandomnessAdapter.getRandom`** - Génération aléatoire
8. **`ConsoleRenderingAdapter`** - Affichage console (les 2 méthodes)
9. **`MutableCollectionDataStorageAdapter`** - Stockage en mémoire (les 2 méthodes)

**Sans ces implémentations, le jeu ne peut pas fonctionner.**

### 🟠 PRIORITÉ 2 - IMPORTANT (Should Have)
1. Module **social-interaction** (marqué optionnel dans le README)
2. Gestion d'erreurs robuste
3. Messages et rendu utilisateur améliorés

### 🟡 PRIORITÉ 3 - NICE TO HAVE (Could Have)
1. Tests unitaires et d'intégration
2. Extension des races et classes
3. Types de dés additionnels
4. Messages d'erreur détaillés

---

## 🎯 Estimation d'Effort

| Module | Complexité | Temps estimé |
|--------|------------|--------------|
| Main.scala + Game Loop | ⭐⭐⭐⭐ | 4-6h |
| MapManager | ⭐⭐⭐ | 3-4h |
| MovementEngine | ⭐⭐⭐ | 2-3h |
| FightingEngine | ⭐⭐⭐⭐⭐ | 5-7h |
| Models (MapState, FightState) | ⭐⭐ | 1-2h |
| Adapters (Rendering, Storage, Random) | ⭐⭐⭐ | 3-4h |
| Social Interaction (optionnel) | ⭐⭐ | 2-3h |
| Tests | ⭐⭐⭐ | 4-5h |

**Total estimé (sans social-interaction & tests) : 18-26 heures**  
**Total avec tout : 24-34 heures**

---

## 🚀 Plan d'Action Recommandé

### Phase 1 : Infrastructure (Foundation)
1. Implémenter `MachineDefaultRandomnessAdapter` (simple)
2. Implémenter les modèles `DndMapState` et `FightState`
3. Implémenter `MutableCollectionDataStorageAdapter` (basique)

### Phase 2 : Exploration
1. Implémenter `MapManager.validateAndStoreMap` avec parsing complet
2. Implémenter `MovementEngine.move` avec détection d'actions
3. Tester manuellement le parsing et mouvement

### Phase 3 : Combat
1. Implémenter la logique d'initiative dans `FightingEngine`
2. Implémenter les tours de combat (attaque, dégâts)
3. Implémenter la gestion de la mort et du loot

### Phase 4 : Rendering
1. Implémenter `ConsoleRenderingAdapter.renderMapState`
2. Implémenter `ConsoleRenderingAdapter.renderFightState`
3. Améliorer l'affichage avec couleurs/symboles

### Phase 5 : Application
1. Implémenter le câblage des dépendances dans `Main`
2. Implémenter la game loop avec inputs utilisateur
3. Tester le flux complet

### Phase 6 : Polish (optionnel)
1. Ajouter social-interaction si le temps le permet
2. Ajouter des tests unitaires
3. Améliorer les messages d'erreur et l'UX

---

## 📝 Notes Importantes

### Format de la Carte
Le fichier `e5-dungeon.dndmap` définit :
- Dimensions : `M - 3 - 4` (3x4)
- NPC : `NPC - x - y`
- PC (villain) : `PC - x - y - lvl - race - class - AC - HP`
- Personnage : `C - x - y - lvl - race - class - AC - HP - orientation`
- Or : `GP - x - y - amount`

### Règles D&D Simplifiées
- **Initiative** : d20, le plus haut commence
- **Attaque** : d20 >= AC pour toucher
- **Dégâts** : Selon `CombatAction` de la classe
- **Bonus Action** : Si niveau > 3 pour Paladin
- **Mort** : HP <= 0

### Architecture Hexagonale
- **in/** : Ports d'entrée (use cases)
- **out/** : Ports de sortie (interfaces externes)
- **domain/** : Logique métier pure
- **model/** : Structures de données
- **infra/** : Implémentations concrètes des ports out

---

## ✅ Ce qui est Déjà Implémenté

- ✅ Structure complète du projet (SBT multi-modules)
- ✅ Modèles de domaine de base (Character, Race, Class)
- ✅ Énumérations (Actions, Directions, Dés)
- ✅ Interfaces de ports (tous les traits)
- ✅ Gestion des erreurs (MapError, Death)
- ✅ Configuration du build
- ✅ Fichier de carte exemple

**Le squelette est solide, il ne reste "que" l'implémentation de la logique !**

---

## 🎓 Conseils pour l'Implémentation

1. **Commencer petit** : Implémenter d'abord le parsing de carte
2. **Tester au fur et à mesure** : Ne pas attendre la fin pour tester
3. **Utiliser le REPL Scala** : Tester les fonctions interactivement
4. **Logs/Prints** : Ajouter des println pour débugger
5. **Git commits réguliers** : Commiter après chaque feature
6. **Ne pas sur-optimiser** : Le but est un MVP fonctionnel

---

**Deadline : 1er décembre 2025 pour la présentation**  
**Extension possible jusqu'au 8 décembre pour peaufiner**

Bon courage ! 💪🎲

