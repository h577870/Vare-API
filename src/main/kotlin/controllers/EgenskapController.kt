package h577870.controllers

import h577870.oppgaveserver.Kassequeue
import h577870.utils.vareeservice
import io.ktor.util.*
import kotlinx.coroutines.*

@KtorExperimentalAPI
object EgenskapController {

    fun updateEgenskaper() {
        var bool = true
        CoroutineScope(context = Dispatchers.Default).launch {
            while (bool) {
                 if (!Kassequeue.emptyQueue()) {
                     val nextMeld = Kassequeue.retrieveMelding()
                     nextMeld.forEach { (t, u) ->
                         /*
                         TODO: Optimisering trengs. Kjører én transaksjon pr vare.
                         TODO: Lage batch-oppdatering?
                          */
                        vareeservice.oppdaterMedEan(t, u)
                     }
                 }
                delay(10000)
            }
        }
    }





}