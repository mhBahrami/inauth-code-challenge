package com.inauth.codechallenge.tasks

/**
 * Used to control the visibility of controls on tasks screen.
 */
enum class VisibilityType {
    /**
     * Display nothing!
     */
    NONE,

    /**
     * Display empty view
     */
    EMPTY_VIEW,

    /**
     * Display pretty json screen
     */
    PRETTY_JSON_VIEW,

    /**
     * Display location info screen
     */
    LOCATION_INFO_VIEW,

    /**
     * Display applications info screen
     */
    APPLICATION_INFO_VIEW,
}