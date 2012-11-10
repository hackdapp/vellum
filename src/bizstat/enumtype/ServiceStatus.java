/*
 * Apache Software License 2.0, Apache Software License 2.0, (c) Copyright 2012, Evan Summers 2012, Evan Summers
 * 
 */
package bizstat.enumtype;

/**
 *
 * @author evan
 */
public enum ServiceStatus {

    OK,
    WARNING,
    CRITICAL,
    UNKNOWN,
    NONZERO,
    BLOCKED,
    DISABLED,
    INDETERMINATE,
    ERROR;

    public boolean isKnown() {
        return ordinal() < UNKNOWN.ordinal();
    }

    public boolean isOk() {
        return ordinal() < CRITICAL.ordinal();
    }

    public StatusChangeType getStatusChangeType(ServiceStatus fromServiceStatus) {
        if (this == WARNING) {
            if (fromServiceStatus == CRITICAL) {
                return StatusChangeType.CRITICAL_WARNING;
            }
            return StatusChangeType.OK_WARNING;
        } else if (this == CRITICAL) {
            if (fromServiceStatus == WARNING) {
                return StatusChangeType.WARNING_CRITICAL;
            }
            return StatusChangeType.OK_CRITICAL;
        } else if (this == OK) {
            if (fromServiceStatus == CRITICAL) {
                return StatusChangeType.CRITICAL_OK;
            }
            return StatusChangeType.WARNING_OK;
        }
        return null;
    }

    public static ServiceStatus find(int ordinal) {
        for (ServiceStatus status : values()) {
            if (status.ordinal() == ordinal) {
                return status;
            }
        }
        return INDETERMINATE;
    }
}
