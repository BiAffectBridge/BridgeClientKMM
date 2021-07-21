package kotlin.org.sagebionetworks.bridge.kmm.shared

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.sagebionetworks.bridge.kmm.shared.cache.*
import org.sagebionetworks.bridge.kmm.shared.encodeObject
import org.sagebionetworks.bridge.kmm.shared.models.Study
import org.sagebionetworks.bridge.kmm.shared.repo.*

class NativeStudyManager(
    val studyId: String,
    private val viewUpdate: (Study) -> Unit
) : KoinComponent {

    private val repo : StudyRepo by inject(mode = LazyThreadSafetyMode.NONE)

    private val scope = MainScope()

    fun observeStudy() {
        scope.launch {
            repo.getStudy(studyId).collect { resource ->
                (resource as? ResourceResult.Success)?.data?.let { result ->
                    viewUpdate(result)
                }
            }
        }
    }

    @Throws(Throwable::class)
    fun onCleared() {
        if (!scope.isActive) return
        try {
            scope.cancel()
        } catch (err: Exception) {
            throw Throwable(err.message)
        }
    }
}

