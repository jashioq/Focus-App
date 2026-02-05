package di

import domain.useCase.EmitAppFocusStateUseCase
import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.EmitTimerToggleStateUseCase
import domain.useCase.SetOnboardingFinishedUseCase
import domain.useCase.StartLiveTimerNotificationUseCase
import domain.useCase.StopLiveTimerNotificationUseCase
import domain.useCase.UpdateLiveTimerNotificationUseCase
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
        StartLiveTimerNotificationUseCase(
            liveTimerNotificationRepository = get(),
        )
    }

    factory {
        UpdateLiveTimerNotificationUseCase(
            liveTimerNotificationRepository = get(),
        )
    }

    factory {
        StopLiveTimerNotificationUseCase(
            liveTimerNotificationRepository = get(),
        )
    }

    factory {
        EmitTimerToggleStateUseCase(
            liveTimerNotificationRepository = get(),
        )
    }
}
