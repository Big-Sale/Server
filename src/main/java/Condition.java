public enum Condition {
    NOT_WORKING_PROPERLY(1),
    GOOD(2),
    VERY_GOOD(3),
    NEW(4);

    private final int value;
    Condition(int value) {
        this.value = value;
    }
}
