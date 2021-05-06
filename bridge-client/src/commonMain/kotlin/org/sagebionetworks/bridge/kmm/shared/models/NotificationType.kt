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

import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

/**
* They type of notification to provide relative to a session. If present, this enum specifies when the notification should be shown to the participant.  |Status|Description| |---|---| |participant_choice|The participant can specify when to receive the notification.| |start_of_window|The notification should be delivered at the start of each session window instance.| |random|The notification should be delivered at a random point in time during each session window instance.| 
* Values: "participant_choice","start_of_window","random"
*/
@Serializable
enum class NotificationType(val serialName: String? = null) {

    @SerialName("participant_choice")
    PARTICIPANT_CHOICE,

    @SerialName("start_of_window")
    START_OF_WINDOW,

    @SerialName("random")
    RANDOM;

}
