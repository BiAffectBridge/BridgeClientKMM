package org.sagebionetworks.bridge.kmm.shared

import co.touchlab.kermit.Logger
import io.ktor.http.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.sagebionetworks.bridge.kmm.shared.cache.ResourceResult
import org.sagebionetworks.bridge.kmm.shared.cache.ResourceStatus
import org.sagebionetworks.bridge.kmm.shared.models.SharingScope
import org.sagebionetworks.bridge.kmm.shared.models.UserSessionInfo
import org.sagebionetworks.bridge.kmm.shared.repo.AuthenticationRepository
import org.sagebionetworks.bridge.kmm.shared.repo.ParticipantRepo

open class NativeAuthenticationManager(
    private val viewUpdate: (UserSessionInfo?) -> Unit
) : KoinComponent {

    private val authManager : AuthenticationRepository by inject(mode = LazyThreadSafetyMode.NONE)
    private val participantManager : ParticipantRepo by inject(mode = LazyThreadSafetyMode.NONE)

    private val scope = MainScope()

    fun observeUserSessionInfo() {
        scope.launch {
            authManager.sessionAsFlow().collect { resourceResult ->
                viewUpdate(resourceResult)
            }
        }
    }

    fun isAuthenticated() : Boolean {
        return authManager.isAuthenticated()
    }

    open fun reauth(completion: (Error?) -> Unit) {
        scope.launch {
            val response = authManager.reAuthWithError()
            if (response.first) {
                completion(null)
            } else {
                completion(response.second ?: Error("reAuth failed. Unknown cause."))
            }
        }
    }

    open fun notifyUIOfBridgeError(statusCode: HttpStatusCode) {
        authManager.notifyUIOfBridgeError(statusCode)
    }

    open fun session() : UserSessionInfo? {
        return try {
            authManager.session()
        } catch (err: Exception) {
            Logger.e("Failed to retrieve session", err)
            null
        }
    }

    fun sessionState() : UserSessionState {
        return try {
            UserSessionState(authManager.session())
        } catch (err: Exception) {
            Logger.e("Failed tp retrieve session", err)
            UserSessionState(error = err.message ?: "Unknown error")
        }
    }

    fun getParticipantRecord() : ParticipantRepo.UpdateParticipantRecord? {
        return session()?.let { ParticipantRepo.UpdateParticipantRecord.getUpdateParticipantRecord(it) }
    }

    fun updateParticipant(record: ParticipantRepo.UpdateParticipantRecord) {
        participantManager.updateParticipant(record)
    }

    /**
     * Set the cached [UserSessionInfo]. This should only be used for migrating apps using old
     * Bridge client libraries. This will do nothing if there already is a session.
     */
    fun migrateSession(userSession: UserSessionInfo) {
        authManager.migrateSession(userSession)
    }

    fun signInEmail(userName: String, password: String, callBack: (UserSessionInfo?, ResourceStatus) -> Unit) {
        scope.launch {
            when(val userSessionResult = authManager.signInEmail(email = userName, password = password)) {
                is ResourceResult.Success -> {
                    val consentGuid = IOSBridgeConfig.defaultConsentGuid
                    if ((consentGuid == null) ||
                        (userSessionResult.data.consentStatuses?.get(consentGuid)?.consented == true)) {
                        callBack(userSessionResult.data, userSessionResult.status)
                    }
                    else {
                        when(val consentResult = participantManager.createConsentSignature(consentGuid)) {
                            is ResourceResult.Success -> callBack(consentResult.data, consentResult.status)
                            is ResourceResult.Failed -> {
                                // If consent fails during sign in then need to sign the participant out and try again.
                                authManager.signOut()
                                callBack(null, userSessionResult.status)
                            }
                            else -> {}  // do nothing if in progress
                        }
                    }
                }
                is ResourceResult.Failed -> callBack(null, userSessionResult.status)
                else -> {}  // do nothing if in progress
            }
        }
    }

    fun signInExternalId(externalId: String, password: String?, callBack: (UserSessionInfo?, ResourceStatus) -> Unit) {
        scope.launch {
            when(val userSessionResult = authManager.signInExternalId(externalId, password ?: externalId)) {
                is ResourceResult.Success -> callBack(userSessionResult.data, userSessionResult.status)
                is ResourceResult.Failed -> callBack(null, userSessionResult.status)
                else -> {}  // do nothing if in progress
            }
        }
    }

    /**
     * Attempt to reauthorize the participant using their stored password.
     * @return
     * - `success` if sign in succeeded
     * - `failed` if the username/password is no longer valid
     * - `retry` if sign in failed, but should be retried later (poor network connection)
     */
    fun reauthWithCredentials(password: String, callBack: (UserSessionInfo?, ResourceStatus) -> Unit) {
        scope.launch {
            when(val userSessionResult = authManager.reauthWithCredentials(password)) {
                is ResourceResult.Success -> callBack(userSessionResult.data, userSessionResult.status)
                is ResourceResult.Failed -> callBack(null, userSessionResult.status)
                else -> {}  // do nothing if in progress
            }
        }
    }

    fun signUpEmail(email: String, password: String,
                    testUser: Boolean,
                    dataGroups: List<String>?,
                    name: String?,
                    sharingScope: SharingScope?,
                    callBack: (Boolean) -> Unit) {
        scope.launch {
            val signUpResult = authManager.signUpEmail(
                email = email,
                password = password,
                testUser = testUser,
                name = name,
                sharingScope = sharingScope,
                dataGroups = dataGroups
            )
            callBack(signUpResult)
        }
    }

    fun signUpEmail(email: String, password: String, callBack: (Boolean) -> Unit) {
        scope.launch {
            val signUpResult = authManager.signUpEmail(email, password)
            callBack(signUpResult)
        }
    }

    fun signOut() {
        scope.launch {
            authManager.signOut()
        }
    }

    @Throws(Throwable::class)
    fun onCleared() {
        try {
            scope.cancel()
        } catch (err: Exception) {
            throw Throwable(err.message)
        }
    }
}

data class UserSessionState(val sessionInfo: UserSessionInfo? = null, val error: String? = null)
