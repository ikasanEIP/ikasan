package org.ikasan.spec.scheduled.instance.model;

public enum InstanceStatus {
    RUNNING {
        @Override
        public String getTranslationLabel() {
            return "status.RUNNING";
        }
    },
    SKIPPED_RUNNING{
        @Override
        public String getTranslationLabel() {
            return "status.SKIPPED";
        }
    },
    COMPLETE{
        @Override
        public String getTranslationLabel() {
            return "status.COMPLETE";
        }
    },
    SKIPPED_COMPLETE{
        @Override
        public String getTranslationLabel() {
            return "status.SKIPPED";
        }
    },
    WAITING{
        @Override
        public String getTranslationLabel() {
            return "status.WAITING";
        }
    },
    ERROR{
        @Override
        public String getTranslationLabel() {
            return "status.ERROR";
        }
    },
    SKIPPED{
        @Override
        public String getTranslationLabel() {
            return "status.SKIPPED";
        }
    },
    ON_HOLD{
        @Override
        public String getTranslationLabel() {
            return "status.ON_HOLD";
        }
    },
    RELEASED{
        @Override
        public String getTranslationLabel() {
            return "status.RELEASED";
        }
    },
    ENDED{
        @Override
        public String getTranslationLabel() {
            return "status.ENDED";
        }
    },
    DISABLED{
        @Override
        public String getTranslationLabel() {
            return "status.ENDED";
        }
    },
    LOCK_QUEUED{
        @Override
        public String getTranslationLabel() {
            return "status.LOCK_QUEUED";
        }
    },
    PREPARED{
        @Override
        public String getTranslationLabel() {
            return "status.PREPARED";
        }
    };

    public abstract String getTranslationLabel();
}
