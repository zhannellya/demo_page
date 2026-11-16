package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptme.app.di.AppContainer

@Suppress("UNCHECKED_CAST")
class AdoptMeViewModelFactory(
    private val container: AppContainer,
    private val animalId: String? = null,
    private val articleId: String? = null,
    private val conversationId: String? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val model = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(container)
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(container.authRepository)
            modelClass.isAssignableFrom(CatalogViewModel::class.java) ->
                CatalogViewModel(container.animalRepository, container.authRepository)
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) ->
                FavoritesViewModel(container.animalRepository)
            modelClass.isAssignableFrom(DetailsViewModel::class.java) ->
                DetailsViewModel(container.animalRepository, animalId.orEmpty())
            modelClass.isAssignableFrom(AdoptionFormViewModel::class.java) ->
                AdoptionFormViewModel(
                    container.animalRepository,
                    container.applicationRepository,
                    container.authRepository,
                    animalId.orEmpty()
                )
            modelClass.isAssignableFrom(MyApplicationsViewModel::class.java) ->
                MyApplicationsViewModel(container.applicationRepository)
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(container.authRepository)
            modelClass.isAssignableFrom(ShelterViewModel::class.java) ->
                ShelterViewModel(container.contentRepository)
            modelClass.isAssignableFrom(NewsViewModel::class.java) ->
                NewsViewModel(container.contentRepository)
            modelClass.isAssignableFrom(NewsDetailViewModel::class.java) ->
                NewsDetailViewModel(container.contentRepository, articleId.orEmpty())
            modelClass.isAssignableFrom(ChatListViewModel::class.java) ->
                ChatListViewModel(container.chatRepository)
            modelClass.isAssignableFrom(ChatThreadViewModel::class.java) ->
                ChatThreadViewModel(container.chatRepository, conversationId.orEmpty())
            modelClass.isAssignableFrom(NotificationsViewModel::class.java) ->
                NotificationsViewModel(container.contentRepository)
            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(container.animalRepository, container.applicationRepository)
            modelClass.isAssignableFrom(AnimalEditorViewModel::class.java) ->
                AnimalEditorViewModel(container.animalRepository, animalId)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return model as T
    }
}
