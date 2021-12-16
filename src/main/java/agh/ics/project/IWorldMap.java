package agh.ics.project;

/*
 * @author apohllo
 *
 * extended by me ~ @Y3drk
 */
public interface IWorldMap {
    /**
     * Indicate if any object can move to the given position.
     *
     * @param position
     *            The position checked for the movement possibility.
     * @return True if the object can move to that position.
     */
    boolean canMoveTo(Vector2d position);

    /**
     * Place an animal on the map.
     *
     * @param animal
     *            The animal to place on the map.
     * @return True if the animal was placed. The animal cannot be placed if the map is already occupied.
     */
    boolean place(Animal animal);

    /**
     * Return true if given position on the map is occupied. Should not be
     * confused with canMove since there might be empty positions where the animal
     * cannot move.
     *
     * @param position
     *            Position to check.
     * @return True if the position is occupied.
     */
    boolean isOccupied(Vector2d position);

    /**
     * Return an object at a given position.
     *
     * @param position
     *            The position of the object.
     * @return Object or null if the position is not occupied.
     */
    Object objectAt(Vector2d position);

    /* additional methods required for the map to work*/

    /**
     * move all living animals randomly
     * return type: void
     * no parameters
     */
    void moveAllAnimals();

    /**
     * remove all dead animals and count them
     * @return amount of dead animals
     * no parameters
     */
    int removeDeadAnimals();

    /**
     * eat all tufts of grass that have animals standing on it
     * return type: void
     * no parameters
     */
    void eatingGrass();

    /**
     * make all the possible reproductions
     * return type: void
     * no parameters
     */
    void reproduction();

    /**
     * adding two new tufts of grass, one on steppe, one in the jungle
     * return type: void
     * no parameters
     */
    void AddNewGrass();

    /** counting all the living animals
     * @return a number of all living animals
     * no parameters
     */
    int countAnimals();

    /** returning two crucial corners of the map
     * @return a Vector2d array with mentioned corners
     * no parameters
     */
    Vector2d[] getCorners();

    /** returning two crucial corners of the jungle inside the map
     * @return a Vector2d array with mentioned corners
     * no parameters
     */
    Vector2d[] getJungleCorners();

    /** implementing the magical strategy
     * return type: void
     * no parameters
     */
    void cloneAnimals();

    /** returning value regarding teleport ability of the map, enabling or disabling it
     * @return boolean mentioned value
     * no parameters
     */
    boolean getTeleportValue();
}

