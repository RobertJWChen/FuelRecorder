package studio.robert.fuelrecorder.utils;

/**
 * Created by robert on 2017/5/27.
 */

public class Preconditions {
    public static <T> T checkNotNull(T reference) {
        if(reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
