package di

import domain.useCase.EmitOnboardingFinishedUseCase
import domain.useCase.SetOnboardingFinishedUseCase
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
}
