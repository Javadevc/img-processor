package com.imgprocessor.util;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Store the state of each job alongwith
 * other relevant fields.
 * */
@Getter
@Setter
public class JobUrlLists {
    private final CopyOnWriteArrayList<String> pending;
    private final CopyOnWriteArrayList<String> completed;
    private final CopyOnWriteArrayList<String> failed;
    private final CopyOnWriteArrayList<String> inProgress;
    private final CopyOnWriteArrayList<String> deleted;
    Long id;
    String created;
    String finished;
    String status;
    String deleteHash;

    public JobUrlLists() {
        completed = new CopyOnWriteArrayList<>();
        pending = new CopyOnWriteArrayList<>();
        failed = new CopyOnWriteArrayList<>();
        inProgress = new CopyOnWriteArrayList<>();
        deleted = new CopyOnWriteArrayList<>();
    }

    @Override
    public String toString() {
        return "JobUrlLists{" +
                "id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", finished='" + finished + '\'' +
                ", status='" + status + '\'' +
                ", pending=" + pending +
                ", completed=" + completed +
                ", failed=" + failed +
                ", inProgress=" + inProgress +
                '}';
    }


}
