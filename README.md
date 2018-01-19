#####################################################
#	INTEGRATED STORAGE SYSTEM		    #
#####################################################

The environment for building the demo software is as follows:
OS: Linux 14.04 LTS
IDE: eclipse LUNA
language: java

*** Convenient execution of the software can be achieved if run on the above OS itself. It is not tested on other platforms. ***

This software consists of three parts:
1. Metadata Server (to be run on any one system in a LAN). (Must be kept running always.)
2. File Server (to be run on two or more systems on the LAN. Running file server implies contributing storage space to ISS). (All file servers must be kept running always for ISS to function well).
3. Client with GUI (to be run on any system in the lan for accessing ISS). It can be run whenever the user wants to access ISS.


######################################################
# Instructions about running the ISS MetaData Server
######################################################
1. The class path is iss.metadataserver.*
2. First run the ISSStat. For now it will allocate some space in the localhost, so that all the files which are on the cloud will be stored in the same host.   
3. Then run the 'rmiregistry' in the path where the Metadata Server is going to run.
4. Then run the ISSServer (Metedata Server).
5. This should be run all the time.
6. This is about Metadata Server. After this run the File Server and Client GUI according to the READ_ME instruction.



#########################################################
#	Instructions to run FileServer			#
#########################################################
1. First run iss.fileserver.FileServerStart.
2. Complete the installation process.[Provide Ipaddress of the System where Meta-data server is running. Path of the storage. Amount of the storage in terms of bytes.]
3. Now run iss.fileserver.FileServermain [Now fileserver starts running]
[Note:- If meta-data server is off you need to again run the Fileserver that is Only run iss.fileserver.FileServermain
No need of installing it again]

#########################################################
#	Instructions to run Client GUI			#
#########################################################
1. Just run the iss.gui.ISSGUInterface class file from the ISSGUInterface module.
2. It will prompt asking the IP address of the system where metadata-server is running.
3. Once the ip address is provided, it will show the contents of the ISS (initially empty). You can upload the contents into ISS by simply copying the files/folders from the native system and right-click--paste operation on the ISS GUI. You can work on the contents using the facilities provided by the GUI which is equivalent to native file explorer.
