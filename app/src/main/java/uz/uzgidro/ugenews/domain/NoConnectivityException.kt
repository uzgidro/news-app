package uz.uzgidro.ugenews.domain

import java.io.IOException

/** Бросается интерцептором, когда сети нет — отличает офлайн от прочих сетевых ошибок. */
class NoConnectivityException : IOException("No network connection available")
