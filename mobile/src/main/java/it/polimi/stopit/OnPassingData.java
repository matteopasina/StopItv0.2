package it.polimi.stopit;

import java.util.EventListener;

/**
 * Created by alessiorossotti on 21/12/15.
 */
public interface OnPassingData extends EventListener {
    void callBack(String name,int imgResource);

}
