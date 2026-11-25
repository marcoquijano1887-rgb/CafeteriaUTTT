package mx.uttt.cafeuttt.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mx.uttt.cafeuttt.data.repository.*
import mx.uttt.cafeuttt.domain.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindProductRepository(implementation: ProductRepositoryImp): ProductRepository
    @Binds @Singleton abstract fun bindCartRepository(implementation: CartRepositoryImp): CartRepository
    @Binds @Singleton abstract fun bindOrderRepository(implementation: OrderRepositoryImp): OrderRepository
    @Binds @Singleton abstract fun bindUserRepository(implementation: UserRepositoryImp): UserRepository
    @Binds @Singleton abstract fun bindCouponRepository(implementation: CouponRepositoryImp): CouponRepository
}
