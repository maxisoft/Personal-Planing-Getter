package com.m.Ade_Planning.process;

public enum Groupe_Value {
    TP1A(115), TP1B(114), TP2B(118), TP2C(119);
    private int value;

    private Groupe_Value(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static int getIntValueFromString(String s) {
        return Groupe_Value.valueOf(s).getValue();
    }
}
