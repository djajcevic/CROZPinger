CROZPinger
==========

Java console network util
 - ping
 - telnet
 
#Usage

    java -jar pinger-standalone.jar -conf pathToConfFile] [-to #]
    java -jar pinger-standalone.jar -host 8.8.8.8

##Parameters

    -conf   path to the hosts list file. File must have hosts separated by new line. Host format is host[:port].
    -host   if you want to check only one host.
    -to     timeout for telnet. Default is 7 s.
    
##Configuration file example

    8.8.8.8
    127.0.0.1:8080


#Thanks

@Dan Jemiolo - CommandLine class
