package com.coriant.AutoClient;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class MyAutoClient
{
    public static String userId = "Admin";
    public static String newPasswd = "pa$$w0rd";
    public static String origPasswd = "pa$$w0rd";
    public static String controlXmlFile = "control.xml";
    //public static String emsInstallDir = "C:/Program Files/Tellabs";
    public static String emsInstallDir = "C:/Program Files/Tellabs/Tellabs_7190_EMS";
    public static String ems_ver = "FP7.0";
    public static String EMS_VersionStr = "";
    static String configType;
    int START_MACHINE;
    int STOP_MACHINE;
    String IP_Address;
    String IP_PREFIX;
    boolean coldStart = false;
    boolean createSite = false;
    boolean createRegion = false;
    boolean createSG = false;
    boolean createNE = false;
    public static boolean chgPasswd = false;
    int nesPerSG;

    private void createSites(SimSite site) throws NodeI.NodeNavigationException, NodeI.UserNavigationException
    {
        if ( createSite)
        {
            for (int i = 0; i < ReadXMLFile.listOfSites.length; i++)
            {
                System.out.println("Creating site: " + ReadXMLFile.listOfSites[i].NAMEKey_val );
                site.createSiteNode( ReadXMLFile.listOfSites[i] );
            }
        }
    }

    private void createRegions(SimRegion region) throws NodeI.NodeNavigationException, NodeI.UserNavigationException
    {
        if ( createRegion)
        {
            for (int i = 0; i < ReadXMLFile.listOfRegions.length; i++)
            {
                System.out.println("Creating region: " + ReadXMLFile.listOfRegions[i].NAMEKey_val );
                region.createRegionNode( ReadXMLFile.listOfRegions[i] );
            }
        }
    }

    /*****************
     * Constructor
     */
    public MyAutoClient(String[] args)
    {
        System.out.println("args size:"+args.length);
        configType = args[2];
        controlXmlFile = args[4];
        userId = args[5];
        newPasswd = origPasswd = args[6];
        emsInstallDir = args[7];
        retriveEMSVersion();
    }
    
    public void retriveEMSVersion(){
    	//String versionFile = emsInstallDir + "/Tellabs_7190_EMS/EMS/ems.ver";
    	String versionFile = emsInstallDir + "/EMS/ems.ver";
    	try{
    	    BufferedReader br = new BufferedReader(new FileReader(versionFile));
    	    String line = "";
    	    do{
    	        line = br.readLine();
    	        if(line!=null && line.indexOf("FP")>0){
    	        	EMS_VersionStr = line;
    	        	int beginIndex = line.indexOf("FP");
    	        	int endIndex = line.indexOf(' ',beginIndex);
    	        	ems_ver = line.substring(beginIndex,endIndex);
    	        	line = null;    	        	
    	        }
    	    }while(line != null);
    	}catch(Exception e){
    		System.out.println( "Get EMS version fail" );
    	}
    }

    public static void main( String [] args ) throws Exception
    {
        System.out.println( "AutoClient starting ... " );

        try
        {
            MyAutoClient autoClient = new MyAutoClient(args);
            SessionData sd = new SessionData(args);
            sd.setEMSVersionStr(EMS_VersionStr);
            sd.setEMSVer(ems_ver);
            
            ReadXMLFile r = new ReadXMLFile();
            r.doParseControlXMLFile(autoClient.controlXmlFile);
            r.doParseConfigXMLFile(autoClient.configType);
            r.doParseConfigLinksXMLFile(r.linksConfigPath);
            r.doParseConfigCircuitsXMLFile(r.circuitsConfigPath);
            System.out.println("Control variables:");
            System.out.println("Site=" + r.siteMode);
            System.out.println("SG=" + r.sgMode);
            System.out.println("NE=" + r.neMode);
            System.out.println("ConfigFile=" + r.configPath);
            System.out.println("ConfigLinksFile=" + r.linksConfigPath);
            System.out.println("ConfigCircuitsFile=" + r.circuitsConfigPath);

            if ( ReadXMLFile.coldStartMode.equalsIgnoreCase("YES") )
            {
                autoClient.coldStart = true;
                MyAutoClient.origPasswd = "tellabs1#";
                MyAutoClient.chgPasswd = true;
            }

            // Get new sim session
            SimSession session = null;
			// for old ems ver to encrypt pw
			/*
            int fp = autoClient.ems_ver.charAt(2)-48;
            if(fp >= 7)
                session = new SimSession(sd);
            else
                session = new SimSessionExt(sd);
			*/
			session = new SimSession(sd);
			
            /*
            SimSession session[] = new SimSession[1];
            for ( int x = 0; x < 1; x++)
            {
                session[x] = new SimSession(sd);
            }
            */
            
            if ( ReadXMLFile.regionMode.equalsIgnoreCase("YES") )
            {
            	session.connectToSM();
                SimRegion region = new SimRegion( session );
                autoClient.createRegion = true;
                autoClient.createRegions(region);
                session.disconnectToSM();
            }
            if ( ReadXMLFile.siteMode.equalsIgnoreCase("YES") )
            {
            	session.connectToSM();
                SimSite site = new SimSite( session );
                autoClient.createSite = true;
                autoClient.createSites(site);
                session.disconnectToSM();
            }

            if ( ReadXMLFile.sgMode.equalsIgnoreCase("YES") )
            {
                autoClient.createSG = true;
            }
            if ( ReadXMLFile.neMode.equalsIgnoreCase("YES") )
            {
                autoClient.createNE = true;
            }
            NEData neData = new NEData();
            session.connectToSM();
            autoClient.addNes(sd, session, neData);
            session.disconnectToSM();
//            session.destroySession();
            /***
            * Add for LPM
               *
            ******/
            if ( ReadXMLFile.sitelinkMode.equalsIgnoreCase("YES"))
            {
            	session.connectToSM();
                SitelinkData sitelinkData = new SitelinkData();
                autoClient.addSitelinks(sd, session, sitelinkData);
                session.disconnectToSM();
            }

            if ( ReadXMLFile.dwdmlinkMode.equalsIgnoreCase("YES"))
            {
            	session.connectToSM();
                DwdmlinkData dwdmlinkData = new DwdmlinkData();
                autoClient.addDwdmlinks(sd, session, dwdmlinkData);
                session.disconnectToSM();
            }

            if ( ReadXMLFile.circuitMode.equalsIgnoreCase("YES"))
            {
            	session.connectToSM();
                CircuitData circuitData = new CircuitData();
                if (ReadXMLFile.autoCircuitMode.equalsIgnoreCase("YES"))
                {
                    autoClient.addCircuits(sd, session, circuitData, true);
                }
                else
                {
                    autoClient.addCircuits(sd, session, circuitData, false);
                }
                session.disconnectToSM();
            }
            session.destroySession();
        }
        catch ( OutOfMemoryError ex )
        {
            // Out of memory; output current memory stats but
            // don't use ErrorLogger!
            Runtime rt = Runtime.getRuntime();
            System.out.println( "Free memory: " + rt.freeMemory() + "; Total memory: " + rt.totalMemory() );
        }

        System.out.println( "MyAutoClient finished ... " );
        System.exit(0);
    }

    //
    // This method is responsible for adding all the NEs that are to be added by the simulator.
    //
    public void addNes(SessionData sd, SimSession session, NEData neData) throws NodeI.NodeControlException, NodeI.UserControlException
    {
        System.out.println("MyAutoClient::addNes() starting ...");

        SimSG sg = new SimSG( sd.NMInterface, session );
        if ( createSG )
        {
        	int sgCount = 0;
            for (int i = 0; i < ReadXMLFile.listOfSGs.length; i++)
            {   	
            	sgCount ++;
            	
                sg.createSG( ReadXMLFile.listOfSGs[i], false );
                //re-establish session after 5 sg created 
                if(sgCount == 5){
            		sgCount = 0;
            		session.disconnectToSM();
            		session.connectToSM();
            		sg.session = session.session();
            	}           	
            }
        }

        //SimSession.sleepFor(60); // 1 min.

        if ( createNE )
        {
        	int neCount = 0;
            for (int i = 0; i < ReadXMLFile.listOfNEs.length; i++)
            {
            	neCount ++;
            	
                ReadXMLFile.listOfNEs[i].CLLI_val = ReadXMLFile.listOfNEs[i].NAMEKey_val + "CLLI";
                ReadXMLFile.listOfNEs[i].RootMoiKey_val = neData.IDS_MOI_HEADER + "\"" + ReadXMLFile.listOfNEs[i].NAMEKey_val + "\"";
                
                Node.NodeSummary sgNode = sg.getSgNode(ReadXMLFile.listOfNEs[i].PARENT_NAME);
                if(sgNode == null){
                    System.out.print("SG "+ReadXMLFile.listOfNEs[i].PARENT_NAME+" is not exist in EMS.");
                    System.out.println("Add NE "+ReadXMLFile.listOfNEs[i].NAMEKey_val+" fail...");
                    continue;
                }
                session.addNe( ReadXMLFile.listOfNEs[i], sg.getSgNode(ReadXMLFile.listOfNEs[i].PARENT_NAME) );
                System.out.println("MyAutoClient::addNes(): NE" + i + " added ...");
                //re-establish session after 10 ne created
                if(neCount == 10){
                	neCount = 0;
                	session.disconnectToSM();
                	session.connectToSM();
                }       
            }
        }
        System.out.println("MyAutoClient::addNes() Finished ...");
    }

    // This method is responsible for adding all the Sitelinks that are defined.
    //
    public void addSitelinks(SessionData sd, SimSession session, SitelinkData sitelinkData) throws NodeI.NodeControlException, NodeI.UserControlException
    {
        System.out.println("MyAutoClient::addSitelinks() starting ...");

        SimSitelink sitelink = new SimSitelink(session, sitelinkData);

        for (int i = 0; i < ReadXMLFile.listOfSitelinks.length; i++)
        {
            System.out.println("Creating Sitelink: " + ReadXMLFile.listOfSitelinks[i]);
            sitelink.createSitelink(ReadXMLFile.listOfSitelinks[i], sitelink.getSiteNode(ReadXMLFile.listOfSitelinks[i].FromSiteKey_val),
                                    sitelink.getSiteNode(ReadXMLFile.listOfSitelinks[i].ToSiteKey_val));
        }

        System.out.println("MyAutoClient::addSitelinks() Finished ...");
    }

    // This method is responsible for adding all the Dwdmlinks that are defined.
    //
    public void addDwdmlinks(SessionData sd, SimSession session, DwdmlinkData dwdmlinkData) throws NodeI.NodeControlException, NodeI.UserControlException
    {
        System.out.println("MyAutoClient::addDwdmlinks() starting ...");

        SimDwdmlink dwdmlink = new SimDwdmlink(session, dwdmlinkData);

        for (int i = 0; i < ReadXMLFile.listOfDwdmlinks.length; i++)
        {
            System.out.println("Creating dwdmlink: " + ReadXMLFile.listOfDwdmlinks[i]);
            dwdmlink.createDwdmlink(ReadXMLFile.listOfDwdmlinks[i], dwdmlink.getNeNode(ReadXMLFile.listOfDwdmlinks[i].FromNEKey_val),
                                    dwdmlink.getNeNode(ReadXMLFile.listOfDwdmlinks[i].ToNEKey_val), dwdmlink.getSitelinkNode(ReadXMLFile.listOfDwdmlinks[i].PARENT_ID_KEY_val));
        }

        System.out.println("MyAutoClient::addDwdmlinks() Finished ...");
    }

    // This method is responsible for adding all the Circuits that are defined.
    //
    public void addCircuits(SessionData sd, SimSession session, CircuitData circuitData, boolean autoIncrement) throws NodeI.NodeControlException, NodeI.UserControlException
    {
        System.out.println("MyAutoClient::addCircuits() starting ...");

        ArrayList dwdmlinks = new ArrayList();
        String dwdmlinkId = "";
        int maxChannels = Integer.parseInt(ReadXMLFile.maxChannels);
        System.out.println("What is maxChannels: " + maxChannels);

        SimCircuit circuit = new SimCircuit(session, circuitData);

        for (int i = 0; i < ReadXMLFile.listOfCircuits.length; i++)
        {
            dwdmlinks.clear();
            if (!ReadXMLFile.listOfCircuits[i].ALinkKey_val.equals(""))
            {
                dwdmlinkId = circuit.getDwdmlinkNode(ReadXMLFile.listOfCircuits[i].ALinkKey_val);
                dwdmlinks.add(dwdmlinkId);
            }
            if (!ReadXMLFile.listOfCircuits[i].BLinkKey_val.equals(""))
            {
                dwdmlinkId = circuit.getDwdmlinkNode(ReadXMLFile.listOfCircuits[i].BLinkKey_val);
                dwdmlinks.add(dwdmlinkId);
            }

            System.out.println("Creating circuits: " + ReadXMLFile.listOfCircuits[i]);
            if (autoIncrement)
            {
                //for (int x=1; x < 25; x++)
                for (int x = 1; x < maxChannels + 1; x++)
                {
                    circuit.createCircuit(ReadXMLFile.listOfCircuits[i],
                                          circuit.getNeNode(ReadXMLFile.listOfCircuits[i].FromZNEKey_val),
                                          circuit.getNeNode(ReadXMLFile.listOfCircuits[i].ToANEKey_val),
                                          circuit.getCustomer(ReadXMLFile.listOfCircuits[i].CustomerKey_val), dwdmlinks, autoIncrement, x);
                }
            }
            else
            {
                circuit.createCircuit(ReadXMLFile.listOfCircuits[i],
                                      circuit.getNeNode(ReadXMLFile.listOfCircuits[i].FromZNEKey_val),
                                      circuit.getNeNode(ReadXMLFile.listOfCircuits[i].ToANEKey_val),
                                      circuit.getCustomer(ReadXMLFile.listOfCircuits[i].CustomerKey_val), dwdmlinks, autoIncrement, 0);
            }
        }

        System.out.println("Total circuits created: " + SimCircuit.success); //a counter for #circuits created
        System.out.println("MyAutoClient::addCircuits() Finished ...");
    }
}
