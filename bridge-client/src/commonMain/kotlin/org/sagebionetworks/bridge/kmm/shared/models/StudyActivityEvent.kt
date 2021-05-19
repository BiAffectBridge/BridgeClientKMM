/**
* Bridge Server API
* No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
*
* OpenAPI spec version: 0.21.29
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package org.sagebionetworks.bridge.kmm.shared.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

/**
 * Activity event for a participant in a specific study, used for scheduling activities. 
 * @param eventId event identifier
 * @param answerValue answer value for a question event
 * @param timestamp ISO 8601 date and time that the event occurred
 * @param clientTimeZone An IANA Time Zone Database (TZDB) name for the participant’s time zone at the time the record is submitted. If present, this field will be validated to be a valid time zone name.
 * @param recordCount The number of unique timestamp values that have been recorded for this events. Immutable events should only ever have one record, while mutable events (`future_only` and `mutable`) can have any number of historical values.
 * @param createdOn ISO 8601 date and time that the user account was created.
 * @param type StudyActivityEvent
 */
@Serializable
data class StudyActivityEvent (
    /* event identifier */
    @SerialName("eventId")
    val eventId: kotlin.String,
    /* ISO 8601 date and time that the event occurred */
    @SerialName("timestamp")
    val timestamp: Instant,
    /* answer value for a question event */
    @SerialName("answerValue")
    val answerValue: kotlin.String? = null,
    /* An IANA Time Zone Database (TZDB) name for the participant’s time zone at the time the record is submitted. If present, this field will be validated to be a valid time zone name. */
    @SerialName("clientTimeZone")
    val clientTimeZone: kotlin.String? = null,
    /* The number of unique timestamp values that have been recorded for this events. Immutable events should only ever have one record, while mutable events (`future_only` and `mutable`) can have any number of historical values. */
    @SerialName("recordCount")
    val recordCount: kotlin.Int? = null,
    /* ISO 8601 date and time that the user account was created. */
    @SerialName("createdOn")
    val createdOn: kotlin.String? = null,
    /* StudyActivityEvent */
    @SerialName("type")
    val type: kotlin.String? = null
) {

}

