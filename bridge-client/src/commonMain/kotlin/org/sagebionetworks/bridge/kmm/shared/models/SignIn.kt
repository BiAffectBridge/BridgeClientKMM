/**
* Bridge Server API
* No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
*
* OpenAPI spec version: 0.16.25
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
 * Information to sign in to the Bridge server. For a successful sign in, this object must include an *identifier* (either email or phone number) and a *secret* (either password or a reauthToken). Depending on the credentials supplied, there are currently different server endpoints to call. The appId is always required. 
 * @param appId App identifier of the participant's app
 * @param email User's email address
 * @param phone 
 * @param externalId The external ID associated with this app participant
 * @param password User's password
 * @param reauthToken The token sent with a new session to reauthenticate with the server. This removes the need for a client app to store the user's password. The reauth token is rotated each time the caller authenticates or reauthenticates. 
 * @param type SignIn
 */
@Serializable
internal data class SignIn (
    /* App identifier of the participant's app */
    @SerialName("appId")
    val appId: String,
    /* User's email address */
    @SerialName("email")
    val email: String? = null,
    @SerialName("phone")
    val phone: Phone? = null,
    /* The external ID associated with this app participant */
    @SerialName("externalId")
    val externalId: String? = null,
    /* User's password */
    @SerialName("password")
    val password: String? = null,
    /* The token sent with a new session to reauthenticate with the server. This removes the need for a client app to store the user's password. The reauth token is rotated each time the caller authenticates or reauthenticates.  */
    @SerialName("reauthToken")
    val reauthToken: String? = null,
    /* SignIn */
    @SerialName("type")
    val type: String? = null
)

