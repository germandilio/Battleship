package battleship.view.matrixprinter;

public interface Printer<T> {
    /**
     * convert object to string
     *
     * @param obj object
     * @return String
     */
    String print(T obj);
}
