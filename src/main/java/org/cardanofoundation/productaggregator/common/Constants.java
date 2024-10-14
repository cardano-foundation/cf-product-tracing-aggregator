package org.cardanofoundation.productaggregator.common;

public class Constants {

    public static final String PRODCUER_METADATA_INDEX = "d";
    public static final String PRODUCER_PUBKEYS_INDEX = "s";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final int METADATA_TAG=1904;
    public static final String SCM_TAG = "scm";
    public static final String CID_TAG = "cid";
    public static final String CERT_TAG = "conformityCert";
    public static final String NUMBER_OF_BOTTLES = "number_of_bottles";
}
