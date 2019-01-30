package com.github.dinsaw.valuestore.util;

public class RestPathConstants {
    public static final String MUTUAL_FUNDS_BASE = "/mutualFunds";
    public static final String SCHEME_CODE_PARAM = ":schemeCode";
    public static final String GET_MUTUAL_FUND_PATH = MUTUAL_FUNDS_BASE + "/" + SCHEME_CODE_PARAM;
    public static final String GET_MF_NAV_PATH = MUTUAL_FUNDS_BASE + "/" + SCHEME_CODE_PARAM + "/netAssetValues";
    public static final String NET_ASSET_VALUES_URL_KEY = "netAssetValuesUrl";
    public static final String HEALTH_PATH = "/health";
}
