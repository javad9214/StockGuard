package ir.yar.anbar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.yar.anbar.domain.model.User
import ir.yar.anbar.domain.usecase.user.GetUserInfoUseCase
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Idle : ProfileUiState
    data object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(forceRefresh: Boolean = false) {
        val current = _uiState.value
        if (!forceRefresh && (current is ProfileUiState.Loading || current is ProfileUiState.Success)) return

        viewModelScope.launch {
            getUserInfoUseCase().collect { resource ->
                _uiState.value = when (resource) {
                    is Resource.Loading -> ProfileUiState.Loading
                    is Resource.Success -> ProfileUiState.Success(resource.data)
                    is Resource.Error -> ProfileUiState.Error(resource.message)
                }
            }
        }
    }

    companion object {
        const val TAG = "ProfileViewModel"
    }
}
