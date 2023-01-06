/**
* Bridge Server API
* No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
*
* OpenAPI spec version: 0.25.15
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package org.sagebionetworks.bridge.kmm.shared.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * A localizable string value to show study participants.
 * @param lang A valid ISO 639 alpha-2 or alpha-3 language code specifying the language of the label.
 * @param value The text of the label.
 * @param type Label
 */
@Serializable
public data class Label (

    /* A valid ISO 639 alpha-2 or alpha-3 language code specifying the language of the label. */
    @SerialName("lang")
    val lang: String? = null,

    /* The text of the label. */
    @SerialName("value")
    val value: String? = null,

    /* Label */
    @SerialName("type")
    val type: String? = null

) {

}

