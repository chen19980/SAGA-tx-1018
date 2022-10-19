package tw.com.firstbank.fcbcore.fcbframework.core.saga.type;

import java.util.Locale;

public enum SagaStatus {
    NONE(""),
    RUNNING("running"),
    DONE("done"),
    COMPENSATING("compensating"),
    COMPENSATED("compensated"),
    COMPLETING("completing"),
    COMPLETED("completed");

    private String value;

    private SagaStatus(String value) {
        this.value = value;
    }

    public String getValue()  {
        return value;
    }

    public static SagaStatus fromValue(String value) {
        if (value == null)
            return NONE;

        switch (value.toLowerCase()) {
            case "running":
                return RUNNING;
            case "done":
                return DONE;
            case "compensating":
                return COMPENSATING;
            case "compensated":
                return COMPENSATED;
            case "completing":
                return COMPLETING;
            case "completed":
                return COMPLETED;
            default:
                return NONE;
        }
    }
}
