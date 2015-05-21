package com.coriant.AutoClient;
/**
* Title: SimSession.java
* Projct: AutoClient
* Description: This class simulates the EMS client to create a session and connect to Security Manager.
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 2001  Jeff England Initial version
* 10/2003 Haishan Wang Modification due to changes of EMS code and 7100 NE simulators
*/

import java.net.InetAddress;
import java.net.UnknownHostException;

import Client.EMSClientPOATie;
import SM.ClientService;
import SM.ClientSession;
import SM.ClientServicePackage.InvalidSession;

import com.tellabs.ucc.notifications.CMEventProcessor;
import com.tellabs.ucc.notifications.FMEventProcessor;
import com.tellabs.ucc.notifications.SMEventProcessor;
import com.tellabs.ucc.notifications.UCPushConsumerMonitor;
import com.tellabs.ucc.sm.ClientImplementation;
import com.tellabs.ucc.util.ComManager;
//from idlClasses

//import SimCryptor;
//import SimNe;
//import SessionData;
//import NEData;


public class SimSession
{
    private SessionData sd;
    private ClientSession session = null;  //mimicks to exist in UCFrame entitled myOwner
    private ClientService SMProxy = null;  //mimicks the ClientServiceInterface kept available to UCPushConsumerMonitor
    private ComManager cm = null;
    private UCPushConsumerMonitor myUCPushConsumerMonitor;
    
//    private Client.EMSClient emsclientPtr = null;

    public ClientSession session()
    {
        return session;
    }
    /*
    private void initClientImpl(){
    	try{
    	    ClientImplementation ci = new ClientImplementation();
            org.omg.PortableServer.POA root_poa = ComManager.instance().getPOA();
            final org.omg.CORBA.ORB orb = ComManager.instance().getORB();
            emsclientPtr = (new EMSClientPOATie(ci, root_poa))._this(orb);
            new Thread(){
        	    public void run(){
        		    orb.run();
        	    }
            }.start();
    	}catch(Exception e){
    		System.out.println("Run ORB fail.Exit");
    		System.exit(1);
    	}
    }
    */
    /***************************
     * SetSMProxy() - mimicks ClientServiceInterface functions of getting a proxy connection
     *     - copied from clientServiceInterface
     */
    private void setSMProxy()
    {
        String clientInterfaceName = "SecurityClient_" + sd.SMInterface;
        org.omg.CORBA.Object ob = null;
        try
        {
            ob = ComManager.instance().resolveObjectName(clientInterfaceName);
        }
        catch (Exception e)
        {
            System.out.println("CRITICAL::ClientServiceInterface.setSMProxy" + e);
            System.exit(0);
        }

        if (ob == null)
        {
            System.out.println("CRITICAL::ClientServiceInterface.setSMProxy" +
                               "Unable to resolve " + clientInterfaceName);
            System.exit(0);
        }
        SMProxy = SM.ClientServiceHelper.narrow(ob);
        System.out.println("INFO::ClientServiceInterface.setSMProxy()" +
                           "Resolved " + clientInterfaceName);
        return ;
    }

    public void createUCPushConsumerMonitor()
    {
        ///// extracted from UCFrame.. Commented in that code with /////CHANNEL CODE

        // create the push consumer monitor
        myUCPushConsumerMonitor = UCPushConsumerMonitor.instance();

        // add/connect the event processors to the PushConsumerMonitor
        myUCPushConsumerMonitor.addEventReceiver(CMEventProcessor.instance());
        myUCPushConsumerMonitor.addEventReceiver(new SMEventProcessor());
        myUCPushConsumerMonitor.addEventReceiver(FMEventProcessor.instance());

        // Create all suppliers and consumer and connects the suppliers to
        // the consumer, then start the event queue thread
        myUCPushConsumerMonitor.connectAllSuppliers();

        // start the channel monitors to detect loss-of-connection on the
        // cm and fm channels
        myUCPushConsumerMonitor.startChannelMonitors();
    }

    /***********
     * createUserAuthData()
     *  - mimicks  the portion of SecurityDialog.CmdLineLogin() doing authentication
     *  - returns a newly created SM.SM_UserAuthData.
     *  USES:
     *   - SimCryptor
     *   - SMProxy -- mimicking ClientServiceInterface.
     */
    SM.SM_UserAuthData createUserAuthData(String userId, String passwd, String nonce)
    {
        SM.SM_UserAuthData token = null;

        try
        {
            String encryptPasswd = SimCryptor.generateAuthenticationToken(passwd.trim(),nonce);
//            encryptPasswd = encryptPasswd + nonce;
//            encryptPasswd = SimCryptor.encrypt(encryptPasswd);
            System.out.println("passwd:" + passwd +", encryptPasswd:" + encryptPasswd);
            token = new SM.SM_UserAuthData(userId, encryptPasswd);
        }
        catch (Exception e)
        {
            System.out.println("SimSession::createUserAuthData():" + e);
        }

        return token;
    }

    /***************
     * logon()
     * - mimicks ClientServiceInterface.logon()
     *
     * ComManager  -- returns the corba information.
     * EMSClient   -- only used because a ptr to this needs to be sent with the login.
     *    - ClientImplementation  -- Only used to pass to EMSClient
     * SM_UserAuthData -- created to take care of the MD5.
     *
     * Returns ClientSession
     */
    private SM.ClientSession logon( SM.SM_UserAuthData userAuthData ) throws
                SM.ClientServicePackage.InvalidUser, SM.ClientServicePackage.UserLocked,
                SM.ClientServicePackage.UserActive
    {   
    	
        ClientImplementation ci = new ClientImplementation();
        org.omg.PortableServer.POA root_poa = ComManager.instance().getPOA();
        org.omg.CORBA.ORB orb = ComManager.instance().getORB();
        Client.EMSClient emsclientPtr = (new EMSClientPOATie(ci, root_poa))._this(orb);
        
        SM.ClientSession oClSession = null;
        String ipAddress = "";
        try {
	    ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
        }
        try
        {        	
        	/* logon ems server without version infomation */
        	//oClSession = SMProxy.logon(userAuthData, emsclientPtr, SM.SessionType.EMS_SESSION, ipAddress);
        	
        	/* logon ems server with version infomation, support it from ems fp10.0.2 */ 
        	
        	String versionToMatch = sd.getEMSVersionStr();
        	String clientVer = versionToMatch.substring(versionToMatch.indexOf("INTG_7100_FP"));
        	clientVer = clientVer.substring(0, clientVer.indexOf("_REV"));
       		oClSession = SMProxy.logon(userAuthData, emsclientPtr, SM.SessionType.EMS_SESSION, ipAddress, clientVer);
       		
        }
        catch (org.omg.CORBA.COMM_FAILURE ex)
        {
            System.out.println("Corba COMM_FAILURE exception caught at logon. Make sure SM is up");
            System.exit(1);

        }
        catch ( SM.ClientServicePackage.InvalidUser e )
        {
            System.out.println( "Invalid user exception thrown");
            System.exit(1);
        }
        catch ( Exception e )
        {
            System.out.println( "Exception throw on logon");
            System.exit(1);
        }

        System.out.println("INFO::ClientServiceInterface.logon()" + userAuthData.userName + " logged in.");
        return oClSession;
    }
    
    private void logoff(SM.ClientSession oClSession){
    	try{
    		SMProxy.logoff(oClSession, SM.SessionType.EMS_SESSION);
    	}catch(InvalidSession e){
    		System.out.println( "Invalid user session");
    	}
    }

    public String establishNonce(String username)
    {
        String result = "";
        try
        {
            result = SMProxy.establishNonce(username);
        }
        catch (org.omg.CORBA.COMM_FAILURE ex)
        {
            System.out.println("Corba COMM_FAILURE exception caught at logon. Make sure SM is up");
            System.exit(1);
        }
        catch ( Exception e )
        {
            System.out.println( "Exception throw on logon");
            System.exit(1);
        }

        return result;
    }

    /****************
     * mimicks ChangePasswordDialog.JButton1_actionPerformed()
     */
    private void changePassword()
    {
        String sPwdForT1 = ""; //convert p2 to all uppercase
        String sPo = "";
        String p2 = "";

        try
        {
            sPwdForT1 = SimCryptor.hash(MyAutoClient.newPasswd.toUpperCase()); //NBI pwd
            sPo = SimCryptor.hash(MyAutoClient.origPasswd); //current pwd
            p2 = SimCryptor.hash(MyAutoClient.newPasswd); //new pwd
            SMProxy.changePassword(session.sessionIdVal, sPo, p2, sPwdForT1);
        }
        catch (Exception e)
        {
            System.out.println("Exception in SimSession::changePassword()");
            System.exit(5);
        }
    }

    /**************
     * connectMonitorChannels()
     * Currently not used.
     */

    private void connectMonitorChannels()
    {}

    /********
     * Close all the channels opened to recieve notifications from the server.
     */
    private void closeAllChannels()
    {
        //2-4    if (myCMChannelMonitor != null){
        //2-4      System.out.println("Closing CM Channel");
        //2-4      myCMChannelMonitor.disconnect();
        //2-4    }

        //2-4     if (mySMChannelMonitor != null){
        //2-4        System.out.println("Closing SM Channel");
        //2-4        mySMChannelMonitor.disconnect();
        //2-4      }

    }

    /****
     * connectToSM()
     *   - this function mimicks SecurityDialog.ConnectSession()
     *   - this is copied from SecurityDialog.onInitDialog()-->connectSession()
     */
    public void connectToSM()
    {
        System.out.println("SimSession::ConnectToSM() starting ...");
        try
        {
//            setSMProxy();
            String nonce = establishNonce(MyAutoClient.userId);
            SM.SM_UserAuthData userAuthData = createUserAuthData(MyAutoClient.userId, MyAutoClient.origPasswd, nonce);
            System.out.println("::call logon as " + MyAutoClient.userId + "/" + MyAutoClient.origPasswd + "...");
            session = logon(userAuthData);
            if ( MyAutoClient.chgPasswd )
                changePassword();
            if (session == null)
            {
                System.out.println("CRITICAL::SecurityDialog.java::myOwner.clSession is NULL");
                System.exit(0);
            }
            System.out.println("INFO::SecurityDialog.connectSession()::Session ID = " + session.sessionIdVal);
            Short unsuccessfulLogins = new Short(session.userSessionDataVal.unsuccessfulLoginAttempts);
            System.out.println("INFO::Last Login Time: " + session.userSessionDataVal.lastLoginTimeDate +
                               "\n\nInvalid Login Attempts : " + unsuccessfulLogins.toString() );
        }
        catch (SM.ClientServicePackage.InvalidUser e)
        {
            System.out.println("INFO::SecurityDialog.connectSession()::Invalid User Exception");
        }
        catch (SM.ClientServicePackage.UserLocked excep)
        {
            System.out.println("INFO::SecurityDialog.connectSession()::User Locked Exception");
        }
        catch (SM.ClientServicePackage.UserActive excep)
        {
            System.out.println("INFO::SecurityDialog.connectSession()::User Active Exception");
        }
        System.out.println("SimSession::ConnectToSM() finished ...");
    }
    
    public void disconnectToSM(){
    	logoff(session);
    	System.out.println("SimSession::DisconnectToSM() finished");
    }
    
    public void destroySession(){
    	try{
//    		logoff(session);
    		cm.destroy();
    	}catch(Exception e){
    	}
    	System.out.println("Session destroy finished..");
    }

    /**************
     * sleepFor()
     */
    public static void sleepFor( int time )
    {
        Thread t = Thread.currentThread();
        System.out.println( t.getName());

        try
        {
            t.sleep(time*1000);
            System.out.println( t.getName());
        }
        catch (Exception e)
        {
            System.out.println( e.toString() );
        }
    }

    /**************
     * SimSession()
     */
    public SimSession( SessionData sd )
    {
        this.sd = sd;

        cm = ComManager.instance(sd.SMInterface);
        cm.initializeORB( sd.cmArgs );

        try
        {
            cm.initialize();
        }
        catch (Exception e)
        {
            System.out.println( e.toString() );
        }
        setSMProxy();
//        initClientImpl();
//        connectToSM();
        //someday if we want to have a push-push model.      createUCPushConsumerMonitor();
        //connectMonitorChannels();

    }

    /*********
     * Currently not keeping the ne that was added in the session.
     * May want to change this later.
     */
    public void addNe( NEData neDataArg, Node.NodeSummary sgArg )
    {
        SimNe ne = new SimNe( session , sd.NMInterface, neDataArg, sgArg);
    }
    
    public void delNe ( NEData neDataArg, Node.NodeSummary sgArg ){
	 SimNe ne = new SimNe( session , sd.NMInterface, neDataArg, sgArg, true);
    }

    /**
     * Main method.
     */
    public static void main( String [] args) throws Exception
    {
        SessionData sd = new SessionData( args );
        NEData ned = new NEData();
        try
        {
            SimSession session = new SimSession(sd);
        }
        catch ( OutOfMemoryError ex )
        {
            // Out of memory; output current memory stats but
            // don't use ErrorLogger!
            Runtime rt = Runtime.getRuntime();
            System.out.println( "Free memory: " + rt.freeMemory() +
                                "; Total memory: " + rt.totalMemory() );
        }
    }
    
    
}

