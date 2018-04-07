package nhdphuong.com.manga.data

import nhdphuong.com.manga.Constants

/*
 * Created by nhdphuong on 3/17/18.
 */
enum class Tab(val defaultName: String) {
    RANDOM(Constants.RANDOM),
    ARTISTS(Constants.ARTISTS),
    TAGS(Constants.TAGS),
    CHARACTERS(Constants.CHARACTERS),
    GROUPS(Constants.GROUPS),
    PARODIES(Constants.PARODIES),
    INFO(Constants.INFO);

    val label
        get() = defaultName
}