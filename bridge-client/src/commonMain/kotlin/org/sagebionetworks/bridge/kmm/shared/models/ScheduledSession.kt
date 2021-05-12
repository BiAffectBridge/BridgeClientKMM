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
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

/**
 * An individual session instance the participant should perform as part of the study. The `refGuid` property will refer to a session in the list of sessions in this timeline. The `instanceGuid` is a unique GUID to identify any data that is generated from the execution of this scheduled session. This allows scientists to contextualize data based on when it was gathered in the execution of the study’s schedule. Further details about how a client should interpret a timeline can be found at the Bridge developer portal.
 * @param refGuid 
 * @param instanceGuid 
 * @param startDay 
 * @param endDay 
 * @param startTime 
 * @param delayTime 
 * @param expiration 
 * @param persistent 
 * @param assessments 
 * @param type ScheduledSession
 */
@Serializable
data class ScheduledSession (
    @SerialName("refGuid")
    val refGuid: kotlin.String,
    @SerialName("instanceGuid")
    val instanceGuid: kotlin.String,
    @SerialName("startDay")
    val startDay: kotlin.Int,
    @SerialName("endDay")
    val endDay: kotlin.Int,
    @SerialName("startTime")
    val startTime: kotlin.String,
    @SerialName("delayTime")
    val delayTime: kotlin.String? = null,
    @SerialName("expiration")
    val expiration: DateTimePeriod,
    @SerialName("persistent")
    val persistent: kotlin.Boolean = false,
    @SerialName("assessments")
    val assessments: kotlin.collections.List<ScheduledAssessment>,
    /* ScheduledSession */
    @SerialName("type")
    val type: kotlin.String? = null
) {

}

