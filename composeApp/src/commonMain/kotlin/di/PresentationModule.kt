package di

import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.EmitTimerToggleStateUseCase
import domain.useCase.SetOnboardingFinishedUseCase
import domain.useCase.StartLiveTimerNotificationUseCase
import domain.useCase.StopLiveTimerNotificationUseCase
import domain.useCase.UpdateLiveTimerNotificationUseCase
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
        HomeScreenViewModel(
            startLiveTimerNotificationUseCase = get<StartLiveTimerNotificationUseCase>(),
            updateLiveTimerNotificationUseCase = get<UpdateLiveTimerNotificationUseCase>(),
            stopLiveTimerNotificationUseCase = get<StopLiveTimerNotificationUseCase>(),
            emitTimerToggleStateUseCase = get<EmitTimerToggleStateUseCase>(),
        )
    }
}
