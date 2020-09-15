package za.ac.mandela.WRPV301.Capstone.Action;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * specifies a serializable callable functional interface
 */
public interface SerializableCallable<T> extends Callable<T>, Serializable {
    //dummy interface
}
