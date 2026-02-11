package di

import domain.useCase.EmitAppFocusStateUseCase
import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.EmitTimerFlowUseCase
import domain.useCase.PauseTimerUseCase
import domain.useCase.ResumeTimerUseCase
import domain.useCase.SetOnboardingFinishedUseCase
import domain.useCase.StartTimerUseCase
import domain.useCase.SkipBlockUseCase
import domain.useCase.StopTimerUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        SetOnboardingFinishedUseCase(
            dataStoreRepository = get(),
        )
    }

    factory {
        EmitOnboardingFinishedUseCase(
            dataStoreRepository = get(),
        )
    }

    factory {
        EmitAppFocusStateUseCase(
            appStateRepository = get()
        )
    }

    factory {
        StartTimerUseCase(
            timerRepository = get(),
        )
    }

    factory {
        StopTimerUseCase(
            timerRepository = get(),
        )
    }

    factory {
        PauseTimerUseCase(
            timerRepository = get(),
        )
    }

    factory {
        ResumeTimerUseCase(
            timerRepository = get(),
        )
    }

    factory {
        EmitTimerFlowUseCase(
            timerRepository = get(),
        )
    }

    factory {
        SkipBlockUseCase(
            timerRepository = get(),
        )
    }
}
