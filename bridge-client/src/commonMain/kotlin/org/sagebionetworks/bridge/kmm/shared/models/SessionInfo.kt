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

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

/**
 * Information about a session in order to render it in a UI prior to execution.
 * @param guid The unique identifier for this model.
 * @param label A required name for this session that will be shown to schedule authors, and can be used as a label for participants if no label can be found.
 * @param startEventId An identifier for the event which should be used to schedule this sequence of sessions. The identifier may be mapped to a timestamp for a user. If the event ID does not exist for the user, the client should ignore this session and schedule nothing. If the event ID exists, the client should start with that timestamp and schedule forward in time according to the information in the session and its schedule.
 * @param performanceOrder 
 * @param notifyAt 
 * @param remindAt 
 * @param reminderPeriod The time after a window starts or before it ends (based on the `remindAt` property) when a reminder should be issued to the client. Only minute, hour, day, and week values are allowed (although these can be mixed). 
 * @param allowSnooze Allow the participant to “snooze” a notification or reminder (dismiss it with some amount of time before it occurs again). Cannot be true if `notifyAt` is null. 
 * @param minutesToComplete 
 * @param message 
 * @param type SessionInfo
 */
@Serializable
data class SessionInfo (
    /* The unique identifier for this model. */
    @SerialName("guid")
    val guid: kotlin.String? = null,
    /* A required name for this session that will be shown to schedule authors, and can be used as a label for participants if no label can be found. */
    @SerialName("label")
    val label: kotlin.String? = null,
    /* An identifier for the event which should be used to schedule this sequence of sessions. The identifier may be mapped to a timestamp for a user. If the event ID does not exist for the user, the client should ignore this session and schedule nothing. If the event ID exists, the client should start with that timestamp and schedule forward in time according to the information in the session and its schedule. */
    @SerialName("startEventId")
    val startEventId: String? = null,
    @SerialName("performanceOrder")
    val performanceOrder: PerformanceOrder? = null,
    @SerialName("notifyAt")
    val notifyAt: NotificationType? = null,
    @SerialName("remindAt")
    val remindAt: ReminderType? = null,
    /* The time after a window starts or before it ends (based on the `remindAt` property) when a reminder should be issued to the client. Only minute, hour, day, and week values are allowed (although these can be mixed).  */
    @SerialName("reminderPeriod")
    val reminderPeriod: kotlin.String? = null,
    /* Allow the participant to “snooze” a notification or reminder (dismiss it with some amount of time before it occurs again). Cannot be true if `notifyAt` is null.  */
    @SerialName("allowSnooze")
    val allowSnooze: kotlin.Boolean? = null,
    @SerialName("minutesToComplete")
    val minutesToComplete: kotlin.Int? = null,
    @SerialName("message")
    val message: NotificationMessage? = null,
    /* SessionInfo */
    @SerialName("type")
    val type: kotlin.String? = null
) {

}
