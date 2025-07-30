package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

/// A root object that stores information about a collection of explore category.
@Serializable
data class ExploreCategoryResponse(
    /// The data included in the response for an explore category object request.
    val data: List<ExploreCategory>,
)
