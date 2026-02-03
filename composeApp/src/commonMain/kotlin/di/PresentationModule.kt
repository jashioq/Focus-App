package di

import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.SetOnboardingFinishedUseCase
import navigation.viewModel.NavigationViewModel
import org.koin.dsl.module
import presentation.screen.home.viewModel.HomeScreenViewModel
import presentation.screen.onboarding.nameScreen.viewModel.NameScreenViewModel

val presentationModule = module {
    factory {
        NameScreenViewModel(
            setOnboardingFinishedUseCase = get<SetOnboardingFinishedUseCase>(),
        )
    }

    factory {
        NavigationViewModel(
            emitOnboardingFinishedUseCase = get<EmitOnboardingFinishedUseCase>(),
        )
    }

    factory {
        HomeScreenViewModel()
    }
}
