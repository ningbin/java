package com.coriant.AutoClient;
/**
* Title: DwdmlinkData.java
* Project: AutoClient
* Description: This class holds the values that can be set for the creation of a dwdmlink
* History:
* Author:  Lucia Leung
*/

public class DwdmlinkData
{
    public final static String PARENT_ID_KEY = "ParentLinkId"; // sitelink ID
    public String PARENT_ID_KEY_val = null;

    public final static String FromNEKey = "FromNeId";  // From NE ID
    public String FromNEKey_val = null;

    public final static String ToNEKey = "ToNeId";  // To NE ID
    public String ToNEKey_val = null;

    public final static String NAMEKey = "NAME";  // Dwdmlink Name
    public String NAMEKey_val = null;

    public final static String AZLinkLossKey = "AZLinkLoss" ;  // AZ Link Loss
    public double AZLinkLoss_val = 0.0;

    public final static String ZALinkLossKey = "ZALinkLoss" ;  // ZA Link Loss
    public double ZALinkLoss_val = 0.0;

    public final static String AZDispersionKey = "AZDispersion" ; // AZ Dispersion
    public double AZDispersion_val = 0.0;

    public final static String ZADispersionKey = "ZADispersion" ;  // ZA Dispersion
    public double ZADispersion_val = 0.0;

    public final static String LinkLength = "LinkLength" ;  // Link Length
    public double LinkLength_val = 0.0;

    public final static String FromLineSideKey = "InterfaceFromLineSide";  // from interface
    public String FromLineSideKey_val = null;

    public final static String ToLineSideKey = "InterfaceToLineSide";  // to interface
    public String ToLineSideKey_val = null;

    public final static String FromNESubtypeKey = "FromNeSubtype";  // from NE subtype

    public final static String ToNESubtypeKey = "ToNeSubtype";  // to NE subtype

    public final static String Description = "EMS_DESCRIPTION";  // Description

    public final static String CWDMNetwork = "ASSOCIATED_CWDM_NETWORK";

    public DwdmlinkData()
    {}

}
