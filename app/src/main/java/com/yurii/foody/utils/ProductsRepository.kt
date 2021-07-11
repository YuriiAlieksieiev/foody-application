package com.yurii.foody.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.yurii.foody.api.*
import com.yurii.foody.authorization.AuthorizationRepository
import com.yurii.foody.screens.admin.categories.CategoriesPagingSource
import com.yurii.foody.screens.admin.products.ProductPagingSource
import okhttp3.MediaType
import okhttp3.RequestBody

class ProductsRepository(private val service: Service) {

    private val pagingConfig = PagingConfig(pageSize = 10, enablePlaceholders = false)

    fun getProductsPager(query: ProductPagingSource.Query? = null) =
        Pager(config = pagingConfig, pagingSourceFactory = { ProductPagingSource(service, query) }).flow

    fun getCategoriesPager() = Pager(config = pagingConfig, pagingSourceFactory = { CategoriesPagingSource(service) }).flow

    suspend fun deleteCategories(items: List<Long>) = service.categories.deleteCategories(items.joinToString(","))

    suspend fun createProduct(product: Product) = service.productsService.createProduct(product)

    suspend fun getProduct(id: Long) = service.productsService.getProduct(id)

    suspend fun createProductAvailability(productAvailability: ProductAvailability) =
        service.productAvailability.createProductAvailability(productAvailability)

    suspend fun getProductAvailability(productId: Long) = service.productAvailability.getProductAvailability(productId)

    suspend fun createProductCategory(productCategory: ProductCategory) = service.productCategory.createProductCategory(productCategory)

    suspend fun deleteProducts(items: List<Long>) = service.productsService.deleteProducts(items.joinToString(","))

    suspend fun createProductImage(productImage: ProductImage) = service.productImage.createProductImage(productImage)

    suspend fun updateProduct(product: Product) = service.productsService.updateProduct(id = product.id, product = product)

    suspend fun updateProductAvailability(productAvailability: ProductAvailability) =
        service.productAvailability.updateProductAvailability(productAvailability.productId, productAvailability)

    suspend fun getProductCategory(productId: Long) = service.productCategory.getProductCategory(productId)

    suspend fun updateProductCategory(productId: Long, productCategory: ProductCategory) =
        service.productCategory.updateProductCategory(productId, productCategory)

    suspend fun removeProductCategory(productId: Long) = service.productCategory.removeProductCategory(productId)

    suspend fun getMainProductImage(productId: Long): ProductImage =
        service.productImage.getProductsImages(
            productIds = productId.toString(),
            page = 1, size = 1, isDefault = true
        ).results.first()

    suspend fun getCategories() = getAllCategories(page = 1)

    private suspend fun getAllCategories(page: Int): List<Category> {
        val res = service.categories.getCategories(page, size = 100)
        return if (res.next != null)
            res.results + getAllCategories(page + 1)
        else
            res.results
    }

    suspend fun getAdditionalProductImages(productId: Long) = getAdditionalProductImages(productId, page = 1)

    suspend fun deleteProductImage(productImageId: Long) = service.productImage.deleteProductImage(productImageId)

    private suspend fun getAdditionalProductImages(productId: Long, page: Int): List<ProductImage> {
        val res = service.productImage.getProductsImages(
            productIds = productId.toString(),
            page = page, size = 1, isDefault = false
        )

        return if (res.next != null)
            res.results + getAdditionalProductImages(productId, page + 1)
        else
            res.results
    }

    suspend fun uploadImage(bytes: ByteArray): LoadedImage {
        val requestBody = RequestBody.create(MediaType.parse("image/*"), bytes)
        return service.productImage.uploadImage(requestBody)
    }

    companion object {
        private var INSTANCE: ProductsRepository? = null
        fun create(api: Service): ProductsRepository {
            if (INSTANCE == null)
                synchronized(AuthorizationRepository::class.java) { INSTANCE = ProductsRepository(api) }

            return INSTANCE!!
        }
    }
}