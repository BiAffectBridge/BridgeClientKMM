/**
* Bridge Server API
* No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
*
* OpenAPI spec version: 0.21.18
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package org.sagebionetworks.bridge.kmm.shared.models

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

/**
 * An individual session instance the participant should perform as part of the study. The `refGuid` property will refer to a session in the list of sessions in this timeline. The `instanceGuid` is a unique GUID to identify any data that is generated from the execution of this scheduled session. This allows scientists to contextualize data based on when it was gathered in the execution of the study’s schedule. Further details about how a client should interpret a timeline can be found at the Bridge developer portal.
 * @param refGuid
 * @param instanceGuid
 * @param startEventId An event ID for this scheduled session. The event ID may be mapped to a timestamp for a user. If the event ID does not exist for the user, the client should ignore this scheduled session. Otherwise, it should be shown to the user according to the scheduling information.
 * @param startDay
 * @param endDay
 * @param startTime
 * @param delayTime
 * @param expiration
 * @param persistent
 * @param studyBurstId
 * @param studyBurstNum
 * @param timeWindowGuid
 * @param assessments
 * @param type ScheduledSession
 */
@Serializable
data class ScheduledSession (

    @SerialName("refGuid")
    val refGuid: String,

    @SerialName("instanceGuid")
    val instanceGuid: String,

    /* An event ID for this scheduled session. The event ID may be mapped to a timestamp for a user.
    If the event ID does not exist for the user, the client should ignore this scheduled session.
    Otherwise, it should be shown to the user according to the scheduling information. */
    @SerialName("startEventId")
    val startEventId: String? = null,

//    @SerialName("startDay")
//    val startDay: Int,
//
//    @SerialName("endDay")
//    val endDay: Int,

    // startTime is a var so that apps (DIAN) that define this client side can change it -nbrown 7/8/22
    @SerialName("startTime")
    var startTime: String,

    @SerialName("delayTime")
    val delayTime: String? = null,

    @SerialName("expiration")
    val expiration: DateTimePeriod,

    @SerialName("persistent")
    val persistent: Boolean = false,

    @SerialName("studyBurstId")
    val studyBurstId: kotlin.String? = null,

    @SerialName("studyBurstNum")
    val studyBurstNum: kotlin.Int? = null,

    @SerialName("timeWindowGuid")
    val timeWindowGuid: String? = null,

    @SerialName("assessments")
    val assessments: List<ScheduledAssessment>,

    /* Only set for the participant schedule. */
    @SerialName("startDate")
    val startDate: LocalDate,

    /* Only set for the participant schedule. */
    @SerialName("endDate")
    val endDate: LocalDate,

    /* ScheduledSession */
    @SerialName("type")
    val type: String? = null

)


