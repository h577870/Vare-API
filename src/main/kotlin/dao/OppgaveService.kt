package h577870.dao

import h577870.dao.DatabaseFactory.dbQuery
import h577870.entity.*
import io.ktor.util.*
import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*

@KtorExperimentalAPI
@ExperimentalSerializationApi
class OppgaveService {

    suspend fun hentOppgaveMedId(oppgaveid: Int): OppgaveClass? = dbQuery {
        Oppgave.select {
            (Oppgave.id eq oppgaveid)
        }.mapNotNull {
            convertJsonToClass(it)
        }.singleOrNull()
    }

    suspend fun leggTilOppgave(oppgaveClass: OppgaveClass): Int = dbQuery {
        return@dbQuery Oppgave.insertAndGetId {
            it[brukerid] = oppgaveClass.brukerid
            it[tittel] = oppgaveClass.tittel
            it[beskrivelse] = oppgaveClass.beskrivelse
            it[vareliste] = Json.encodeToString(oppgaveClass.vareliste)
            it[type] = Json.encodeToString(OppgaveTypeSerializer, oppgaveClass.type)
            it[status] = Json.encodeToString(OppgaveStatusSerializer, oppgaveClass.status)
            it[tidogdato] = Json.encodeToString(TidSerializer, oppgaveClass.tidogdato)
            it[tidsfrist] = Json.encodeToString(TidSerializer, oppgaveClass.tidsfrist)
        }.value
    }

    suspend fun hentOppgaverMedBrukerid(brukerid: String): List<OppgaveClass>? = dbQuery {
        Oppgave.selectAll().andWhere {
            (Oppgave.brukerid eq brukerid)
        }.mapNotNull {
            convertJsonToClass(it)
        }.toList()
    }

    suspend fun checkOppgave(tidsfrist: Instant, bestilling: OppgaveType) = dbQuery {
        Oppgave.select {(Oppgave.tidsfrist eq Json.encodeToString(TidSerializer, tidsfrist)
                and(Oppgave.type eq Json.encodeToString(OppgaveTypeSerializer, bestilling)))
        }.mapNotNull {
            convertJsonToClass(it)
        }.singleOrNull()
    }

    suspend fun oppdaterOppgave(oppgaveClass: OppgaveClass): Int = dbQuery {
        return@dbQuery Oppgave.update({Oppgave.id eq oppgaveClass.oppgaveid}) {
            it[vareliste] = Json.encodeToString(oppgaveClass.vareliste)
            it[status] = Json.encodeToString(OppgaveStatusSerializer, oppgaveClass.status)
        }
    }

    private fun convertJsonToClass(row: ResultRow) : OppgaveClass {
        val listFromRow = row[Oppgave.vareliste]
        return OppgaveClass(
            oppgaveid = row[Oppgave.id].value,
            brukerid = row[Oppgave.brukerid],
            tittel = row[Oppgave.tittel],
            beskrivelse = row[Oppgave.beskrivelse],
            vareliste = Json.decodeFromString(listFromRow),
            type = Json.decodeFromString(OppgaveTypeSerializer, string = row[Oppgave.type]),
            status = Json.decodeFromString(OppgaveStatusSerializer, string = row[Oppgave.status]),
            tidogdato = Json.decodeFromString(TidSerializer, row[Oppgave.tidogdato]),
            tidsfrist = Json.decodeFromString(TidSerializer, row[Oppgave.tidsfrist])
        )
    }
}
