@echo off
set EMS_SERVICE=172.29.21.15:41122
set USER_NAME=Admin
set PASSWORD=pa$$w0rd
set EMS_INSTALL_DIR=C:/Coriant/Coriant_7190_EMS
set JAVA_HOME=%EMS_INSTALL_DIR%/jre
set LIBS=lib
set PATH="%JAVA_HOME%/bin";%PATH%
set CONFIG_FILE=test.xml
set CONTROL_FILE=control.xml
set MAIN_CLASS=com.coriant.AutoClient.MyAutoClient

set CLASSPATH=AutoClient-0.0.1-SNAPSHOT.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/EMS/cfg;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/EMS/bin/UCframeworkClasses.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/EMS/bin/Generic.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/EMS/bin/UCIDLClasses.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/EMS/bin/ots_fp30.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jacorb.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/avalon-framework-4.1.5.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/logkit-1.2.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-action.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-shortcut.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-beaninfo.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-common.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-components.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-designer.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-dialogs.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-dock.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-grids.jar;
set CLASSPATH=%CLASSPATH%;%EMS_INSTALL_DIR%/misc/jide-pivot.jar;

java -classic -Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton -cp %CLASSPATH% %MAIN_CLASS% -ORBInitRef NameService=corbaloc:iiop:%EMS_SERVICE%/NameService %CONFIG_FILE% TLAB %CONTROL_FILE% %USER_NAME% %PASSWORD% %EMS_INSTALL_DIR%


