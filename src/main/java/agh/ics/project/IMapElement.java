package agh.ics.project;

public interface IMapElement {
    /** returning the elements position
     *
     * @return Vector2d
     *                  position of the element
     */
    Vector2d getPosition();

    /** used purely for testing purposes
     *
     * @return string
     *                 representation of the item
     * no parameters
     */
    String toString();

    /** getting the image for the usage of GUI
     *
     * @return - string
     *                  the address of the image we want to use in resources
     * no parameters
     */
    String imageAddress();
}
