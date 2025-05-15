package com.jordan.chess.utils

enum class Language {
    CZECH,
    ENGLISH,
}

object StringResources {
    private var currentLanguage = Language.CZECH

    fun setLanguage(language: Language) {
        currentLanguage = language
    }

    fun getString(key: StringKey): String {
        return when (currentLanguage) {
            Language.ENGLISH -> englishStrings[key] ?: key.name
            Language.CZECH -> czechStrings[key] ?: englishStrings[key] ?: key.name
        }
    }
}

enum class StringKey {
    SETTINGS,
    BACK,
    START,
    SHOW_POSSIBLE_MOVES,
    SOUNDS,
    DARK_THEME,
    GAME_LENGTH,
    NO_LIMIT,
    MINUTE,
    MINUTES,
    CHESS,
    LANGUAGE,
    PLAY_WITH_BOT,
    BOT_DIFFICULTY,
    TOP_PIECE_ROTATION,
    BOTTOM_PIECE_ROTATION,
    PIECE_ROTATION,
    USE_FOR_BOT,

    WHITE,
    BLACK,

    CURRENT_TURN,
    MOVE_HISTORY,
    EXPORT_GAME,
    GAME_OVER,
    GAME_LENGTH_LABEL,
    BACK_TO_MENU,
    CONFIRM_EXIT,
    CONFIRM_EXIT_MESSAGE,
    YES,
    NO,

    GAME_COPIED,

    GAME_OVER_INS_MATERIAL,
    GAME_OVER_CHECKMATE,
    GAME_OVER_STALEMATE,
    GAME_OVER_REPEATED,
    TIMEOUT,

    ABOUT_TITLE,
    ABOUT_VERSION,
    ABOUT_PROJECT_DESC,
    ABOUT_BOT_DESC,
    ABOUT_REPO_LABEL,
    ABOUT_COPYRIGHT,
}

private val englishStrings = mapOf(
    StringKey.SETTINGS to "Settings",
    StringKey.BACK to "Back",
    StringKey.START to "Start",
    StringKey.SHOW_POSSIBLE_MOVES to "Show possible moves: ",
    StringKey.SOUNDS to "Sounds: ",
    StringKey.DARK_THEME to "Dark theme: ",
    StringKey.GAME_LENGTH to "Game length:",
    StringKey.NO_LIMIT to "Unlimited",
    StringKey.LANGUAGE to "Language:",
    StringKey.MINUTE to "minute",
    StringKey.MINUTES to "minutes",
    StringKey.CHESS to "Chess",
    StringKey.WHITE to "White",
    StringKey.BLACK to "Black",
    StringKey.CURRENT_TURN to "Currently playing: %s",
    StringKey.MOVE_HISTORY to "Move history",
    StringKey.EXPORT_GAME to "Export game",
    StringKey.GAME_OVER to "Game Over",
    StringKey.GAME_LENGTH_LABEL to "Game length: %s",
    StringKey.BACK_TO_MENU to "Back to menu",
    StringKey.CONFIRM_EXIT to "Do you want to exit the game?",
    StringKey.CONFIRM_EXIT_MESSAGE to "Are you sure you want to leave the game?",
    StringKey.YES to "Yes",
    StringKey.NO to "No",
    StringKey.GAME_COPIED to "Game copied to clipboard!",
    StringKey.GAME_OVER_INS_MATERIAL to "Draw due to insufficient material!",
    StringKey.GAME_OVER_CHECKMATE to "Checkmate! %s loses.",
    StringKey.GAME_OVER_STALEMATE to "Stalemate! The game is a draw.",
    StringKey.GAME_OVER_REPEATED to "Draw due to repetition!",
    StringKey.TIMEOUT to "Time's up! %s loses.",
    StringKey.PLAY_WITH_BOT to "Play with bot:",
    StringKey.BOT_DIFFICULTY to "Difficulty:",
    StringKey.TOP_PIECE_ROTATION to "Rotate top pieces: ",
    StringKey.BOTTOM_PIECE_ROTATION to "Rotate bottom pieces: ",
    StringKey.PIECE_ROTATION to "Piece rotation: ",
    StringKey.USE_FOR_BOT to "Use for bot too: ",
    StringKey.ABOUT_TITLE to "About",
    StringKey.ABOUT_VERSION to "version %s",
    StringKey.ABOUT_PROJECT_DESC to "This application is a student project developed in Kotlin and Jetpack Compose.",
    StringKey.ABOUT_BOT_DESC to "Level one bot is programmed by me, higher levels use the Karballo engine.",
    StringKey.ABOUT_REPO_LABEL to "The Karballo repository and MIT license are available on GitHub:",
    StringKey.ABOUT_COPYRIGHT to "© %d · Developed by %s",
)

private val czechStrings = mapOf(
    StringKey.SETTINGS to "Nastavení",
    StringKey.BACK to "Zpět",
    StringKey.START to "Start",
    StringKey.SHOW_POSSIBLE_MOVES to "Zobrazovat možné tahy: ",
    StringKey.SOUNDS to "Zvuky: ",
    StringKey.DARK_THEME to "Tmavé téma: ",
    StringKey.GAME_LENGTH to "Délka hry:",
    StringKey.NO_LIMIT to "Bez limitu",
    StringKey.LANGUAGE to "Jazyk:",
    StringKey.MINUTE to "minuta",
    StringKey.MINUTES to "minut",
    StringKey.CHESS to "Šachy",
    StringKey.WHITE to "Bílý",
    StringKey.BLACK to "Černý",
    StringKey.CURRENT_TURN to "Právě na tahu: %s",
    StringKey.MOVE_HISTORY to "Historie tahů",
    StringKey.EXPORT_GAME to "Exportovat hru",
    StringKey.GAME_OVER to "Konec hry",
    StringKey.GAME_LENGTH_LABEL to "Délka hry: %s",
    StringKey.BACK_TO_MENU to "Zpět do menu",
    StringKey.CONFIRM_EXIT to "Opustit hru",
    StringKey.CONFIRM_EXIT_MESSAGE to "Opravdu chcete opustit hru?",
    StringKey.YES to "Ano",
    StringKey.NO to "Ne",
    StringKey.GAME_COPIED to "Hra byla uložena do schránky!",
    StringKey.GAME_OVER_INS_MATERIAL to "Remíza nedostatkem materiálu!",
    StringKey.GAME_OVER_CHECKMATE to "Šachmat! %s prohrává.",
    StringKey.GAME_OVER_STALEMATE to "Pat! Hra skončila remízou.",
    StringKey.GAME_OVER_REPEATED to "Remíza opakováním!",
    StringKey.TIMEOUT to "Vypršel čas! %s prohrává.",
    StringKey.PLAY_WITH_BOT to "Hrát s botem:",
    StringKey.BOT_DIFFICULTY to "Obtížnost:",
    StringKey.TOP_PIECE_ROTATION to "Otočit horní postavičky: ",
    StringKey.BOTTOM_PIECE_ROTATION to "Otočit spodní postavičky: ",
    StringKey.PIECE_ROTATION to "Rotace postaviček: ",
    StringKey.USE_FOR_BOT to "Použít také pro bota: ",
    StringKey.ABOUT_TITLE to "O aplikaci",
    StringKey.ABOUT_VERSION to "verze %s",
    StringKey.ABOUT_PROJECT_DESC to "Aplikace je studentský projekt vytvořený v Kotlinu a Jetpack Compose.",
    StringKey.ABOUT_BOT_DESC to "Bot úrovně jedna je můj vlastní, vyšší úrovně běží na Karballo enginu.",
    StringKey.ABOUT_REPO_LABEL to "Repozitář Karballo a jeho licenci MIT najdete na GitHubu:",
    StringKey.ABOUT_COPYRIGHT to "© %d · Aplikaci vyvinul %s",
)