package com.example.socialnetwork.utils.observer;

import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);

}
