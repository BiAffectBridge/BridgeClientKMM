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
 * A pointer to a file revision hosted for this app. 
 * @param guid The file guid
 * @param createdOn The createdOn timestamp of the file revision
 * @param href An URL to retrieve the file revision
 * @param type FileReference
 */
@Serializable
data class FileReference (
    /* The file guid */
    @SerialName("guid")
    val guid: String,
    /* The createdOn timestamp of the file revision */
    @SerialName("createdOn")
    val createdOn: String,
    /* An URL to retrieve the file revision */
    @SerialName("href")
    val href: String? = null,
    /* FileReference */
    @SerialName("type")
    val type: String? = null
)

