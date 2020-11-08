package h577870.utils

import h577870.dao.BrukerService
import h577870.dao.OppgaveService
import h577870.dao.VareService
import io.ktor.util.*

@KtorExperimentalAPI
val vareservice = VareService()
@KtorExperimentalAPI
val brukerservice = BrukerService()
@KtorExperimentalAPI
val oppgaveservice = OppgaveService()

val validator = Validator()