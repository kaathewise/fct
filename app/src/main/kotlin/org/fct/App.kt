package org.fct

import org.fct.persistence.Planes

fun main() {
    val service = Service(Planes.get(), Parser())
    while (true) {
        val line = readlnOrNull() ?: break
        service.handleLine(line)?.let { println(">> $line\n$it") }
    }
}
