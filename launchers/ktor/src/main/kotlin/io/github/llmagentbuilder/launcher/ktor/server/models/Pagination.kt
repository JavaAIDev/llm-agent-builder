package io.github.llmagentbuilder.launcher.ktor.server.models


/**
 *
 * @param totalItems Total number of items.
 * @param totalPages Total number of pages.
 * @param currentPage Current_page page number.
 * @param pageSize Number of items per page.
 */
data class Pagination(
    /* Total number of items. */
    val totalItems: kotlin.Int,
    /* Total number of pages. */
    val totalPages: kotlin.Int,
    /* Current_page page number. */
    val currentPage: kotlin.Int,
    /* Number of items per page. */
    val pageSize: kotlin.Int
) 

