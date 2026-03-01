package di

import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.EmitUserNameUseCase
import domain.useCase.EmitTimerFlowUseCase
import domain.useCase.PauseTimerUseCase
import domain.useCase.ResumeTimerUseCase
import domain.useCase.SetOnboardingFinishedUseCase
import domain.useCase.SetUserNameUseCase
import domain.useCase.StartTimerUseCase
import domain.useCase.ExtendBlockUseCase
import domain.useCase.SkipBlockUseCase
import domain.useCase.StopTimerUseCase
import navigation.viewModel.NavigationViewModel
import org.koin.dsl.module
import presentation.screen.calendar.viewModel.CalendarScreenViewModel
import presentation.screen.home.viewModel.HomeScreenViewModel
import presentation.screen.onboarding.nameScreen.viewModel.NameScreenViewModel

val presentationModule = module {
    factory {
        CalendarScreenViewModel(
            emitUserNameUseCase = get<EmitUserNameUseCase>(),
        )
    }

    factory {
        NameScreenViewModel(
            setOnboardingFinishedUseCase = get<SetOnboardingFinishedUseCase>(),
            setUserNameUseCase = get<SetUserNameUseCase>(),
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
            extendBlockUseCase = get<ExtendBlockUseCase>(),
        )
    }
}
