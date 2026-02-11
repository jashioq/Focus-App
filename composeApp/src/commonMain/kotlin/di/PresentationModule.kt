package di

import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.EmitTimerFlowUseCase
import domain.useCase.PauseTimerUseCase
import domain.useCase.ResumeTimerUseCase
import domain.useCase.SetOnboardingFinishedUseCase
import domain.useCase.StartTimerUseCase
import domain.useCase.SkipBlockUseCase
import domain.useCase.StopTimerUseCase
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
            startTimerUseCase = get<StartTimerUseCase>(),
            stopTimerUseCase = get<StopTimerUseCase>(),
            pauseTimerUseCase = get<PauseTimerUseCase>(),
            resumeTimerUseCase = get<ResumeTimerUseCase>(),
            emitTimerFlowUseCase = get<EmitTimerFlowUseCase>(),
            skipBlockUseCase = get<SkipBlockUseCase>(),
        )
    }
}
