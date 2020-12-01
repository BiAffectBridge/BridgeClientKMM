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

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

/**
 * 
 * @param config The JSON configuration for this assessment.
 * @param createdOn The date and time the app config was created.
 * @param modifiedOn The date and time the app config was last modified.
 * @param version The optimistic locking version of the assessment config. This value must be submitted as part of the next update of the model. If it does not match the value on the server, a 409 error (Conflict) will prevent the update from occurring. It can also serve as a key to determine if a local cache of this `AssessmentConfig` revision needs to be updated. 
 * @param type AssessmentConfig
 */
@Serializable
data class AssessmentConfig (
    /* The JSON configuration for this assessment. */
    @SerialName("config")
    val config: kotlinx.serialization.json.JsonElement,
    /* The date and time the app config was created. */
    @SerialName("createdOn")
    val createdOn: kotlin.String? = null,
    /* The date and time the app config was last modified. */
    @SerialName("modifiedOn")
    val modifiedOn: kotlin.String? = null,
    /* The optimistic locking version of the assessment config. This value must be submitted as part of the next update of the model. If it does not match the value on the server, a 409 error (Conflict) will prevent the update from occurring. It can also serve as a key to determine if a local cache of this `AssessmentConfig` revision needs to be updated.  */
    @SerialName("version")
    val version: kotlin.Long? = null,
    /* AssessmentConfig */
    @SerialName("type")
    val type: kotlin.String? = null
)

