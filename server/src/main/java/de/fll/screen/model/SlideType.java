package de.fll.screen.model;

public enum SlideType {
    IMAGE,
    SCORE
    // 可扩展更多类型

    @Override
    public String toString() {
        return name();
    }
} 