package com.paulohenrique.poketeam.util

/** "mr-mime" -> "Mr Mime" */
fun String.prettyName(): String =
    split("-").joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
