package de.mchllngr.quickero.model

/**
 * Class for holding the needed data about an removed item for the remove to be undone.
 */
/**
 * Constructor for creating the [RemovedApplicationModel].
 */
class RemovedApplicationModel(
    /**
     * Last position of removed item.
     */
    val position: Int,
    /**
     * The removed item.
     */
    val applicationModel: ApplicationModel
)

