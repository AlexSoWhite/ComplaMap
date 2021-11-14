package com.example.complamap.model

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*

class PointAddressConverter(
    listener: Session.SearchListener,
    searchType: Int
) { // На момент создания SearchFactory должна быть инициализирована
    private val searchListener: Session.SearchListener = listener
    private val searchType: Int = searchType // Либо GEO(топоним), либо BIZ(организация)
    private var searchSession: Session? = null
    private val searchManager: SearchManager = SearchFactory
        .getInstance()
        .createSearchManager(SearchManagerType.ONLINE)

    fun addressFromPoint(p: Point, zoom: Int) { // От zoom зависит избыточность адреса
        searchSession = searchManager
            .submit(
                p,
                zoom,
                SearchOptions().setSearchTypes(searchType),
                searchListener
            )
    }
}
