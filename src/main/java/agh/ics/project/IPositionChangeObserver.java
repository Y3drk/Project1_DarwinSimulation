package agh.ics.project;

public interface IPositionChangeObserver {
    /** responsible for informing the map that an animal has changed its position
     *  return typ: void
     * @param oldPosition - as named
     * @param newPosition - as named
     * @param animal - the animal that has moved
     */
    void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal);
}
