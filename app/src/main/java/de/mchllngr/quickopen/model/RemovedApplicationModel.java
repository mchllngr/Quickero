package de.mchllngr.quickopen.model;

/**
 * Class for holding the needed data about an removed item for the remove to be undone.
 */
public class RemovedApplicationModel {

    /**
     * Last position of removed item.
     */
    public int position;
    /**
     * The removed item.
     */
    public ApplicationModel applicationModel;

    /**
     * Constructor for creating the {@link RemovedApplicationModel}.
     */
    public RemovedApplicationModel(int position, ApplicationModel applicationModel) {
        this.position = position;
        this.applicationModel = applicationModel;
    }
}
