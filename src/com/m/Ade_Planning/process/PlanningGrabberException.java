package com.m.Ade_Planning.process;

public class PlanningGrabberException extends Exception {
    public PlanningGrabberException(String detailMessage) {
        super(detailMessage);
    }

    public PlanningGrabberException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PlanningGrabberException(Throwable throwable) {
        super(throwable);
    }

    public PlanningGrabberException() {
        super();
    }
}
