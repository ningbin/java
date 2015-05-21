package com.coriant.AutoClient;
/**
* Title: SitelinkData.java
* Project: AutoClient
* Description: This class holds the values that can be set for the creation of a Sitelink
* History:
* Author:  Lucia Leung
*/

public class SitelinkData
{
    public final static String FromSiteKey = "FromSiteId";  // From Site ID
    public String FromSiteKey_val = null;

    public final static String ToSiteKey = "ToSiteId";  // To Site ID
    public String ToSiteKey_val = null;

    public final static String NAMEKey = "NAME";  // Sitelink Name
    public String NAMEKey_val = null;

    public final static String LinkLength = "LinkLength" ;  // Link Length
    public double LinkLength_val = 0.0;

    public final static String Description = "EMS_DESCRIPTION" ;  // Description

    public SitelinkData()
    {}

}
