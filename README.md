# JMX CommandLine Client

## Description

* The JMX client for the command line

## Requirement

* Java6 later

## Usage

#### command ####

```
java -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar [JMX Server URL] [Bean] [Attribute] [Interval]
```

or

```
java -Dpath=[metrics file path] -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar [JMX Server URL] [Interval]
```

##### ObjectName List #####

```
java -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar localhost:7085
```

```
Catalina:j2eeType=Servlet,name=default,WebModule=//localhost/,J2EEApplication=none,J2EEServer=none
Catalina:j2eeType=Servlet,name=jsp,WebModule=//localhost/,J2EEApplication=none,J2EEServer=none
Catalina:j2eeType=Servlet,name=default,WebModule=//localhost/docs,J2EEApplication=none,J2EEServer=none
Catalina:j2eeType=Servlet,name=jsp,WebModule=//localhost/docs,J2EEApplication=none,J2EEServer=none
Catalina:j2eeType=Filter,name=Compression Filter,WebModule=//localhost/examples,J2EEApplication=none,J2EEServer=none
...
```

##### JNDI DataSource numActive ######

```
java -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar localhost:7085 "Catalina:type=DataSource,context=/,host=localhost,class=javax.sql.DataSource,name=\"jdbc/postgres\"" numActive
```

```
2015-10-22 19:11:49.876,0
```

##### JNDI DataSource numActive(You get an A in every 2 seconds) #####

```
java -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar localhost:7085 "Catalina:type=DataSource,context=/,host=localhost,class=javax.sql.DataSource,name=\"jdbc/postgres\"" numActive 2
```

```
2015-10-22 19:13:14.090,numActive
2015-10-22 19:13:14.092,0
2015-10-22 19:13:16.094,0
2015-10-22 19:13:18.096,0
2015-10-22 19:13:20.099,0
```

##### JNDI DataSource numActive,numIdle #####

metrics.sample1
``` 
"Catalina:type=DataSource,context=/,host=localhost,class=javax.sql.DataSource,name="jdbc/postgres"" "numActive"
"Catalina:type=DataSource,context=/,host=localhost,class=javax.sql.DataSource,name="jdbc/postgres"" "numIdle"
```

```
java -Dpath=metrics.sample1 -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar localhost:7085
```

```
2015-10-22 19:19:16.364,0,0
```

##### JNDI DataSource numActive,numIdle(You get an A in every 2 seconds) #####

```
java -Dpath=metrics.sample1 -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar localhost:7085 2
```

```
2015-10-22 19:20:03.368,numActive,numIdle
2015-10-22 19:20:03.370,0,0
2015-10-22 19:20:05.372,0,0
2015-10-22 19:20:07.375,0,0
2015-10-22 19:20:09.378,0,0
```

##### You can check a variety of parameters . #####

cat metrics.sample2

```
"java.lang:type=Memory" "HeapMemoryUsage"
"java.lang:name=PS Perm Gen,type=MemoryPool" "Usage"
"java.lang:name=PS Eden Space,type=MemoryPool" "Usage"
"Catalina:type=DataSource,context=/,host=localhost,class=javax.sql.DataSource,name="jdbc/postgres"" "numActive"
"Catalina:type=DataSource,context=/,host=localhost,class=javax.sql.DataSource,name="jdbc/postgres"" "numIdle"
"Catalina:name="http-apr-8080",type=GlobalRequestProcessor" "bytesSent"
"Catalina:name="http-apr-8080",type=GlobalRequestProcessor" "bytesReceived"
"Catalina:name="http-apr-8080",type=GlobalRequestProcessor" "errorCount"
"Catalina:name="http-apr-8080",type=GlobalRequestProcessor" "maxTime"
"Catalina:name="http-apr-8080",type=GlobalRequestProcessor" "requestCount"
"Catalina:type=Manager,context=/,host=localhost" "activeSessions"
"Catalina:type=Manager,context=/,host=localhost" "sessionCounter"
"Catalina:type=Manager,context=/,host=localhost" "expiredSessions"
```

```
java -Dpath=metrics.sample2 -jar jmx-cmdclient-0.1.0-jar-with-dependencies.jar localhost:7085 2
```

```
2015-10-22 19:21:18.395,HeapMemoryUsage@committed,HeapMemoryUsage@init,HeapMemoryUsage@max,HeapMemoryUsage@used,Usage@committed,Usage@init,Usage@max,Usage@used,Usage@committed,Usage@init,Usage@max,Usage@used,numActive,numIdle,bytesSent,bytesReceived,errorCount,maxTime,requestCount,activeSessions,sessionCounter,expiredSessions
2015-10-22 19:21:18.402,132251648,134217728,238616576,43923600,29884416,16777216,67108864,19941992,40632320,33554432,85393408,36589056,0,0,64697,0,2,1088,8,0,0,0
2015-10-22 19:21:20.414,132251648,134217728,238616576,44014024,29884416,16777216,67108864,19941992,40632320,33554432,85393408,36679480,0,0,64697,0,2,1088,8,0,0,0
2015-10-22 19:21:22.425,132251648,134217728,238616576,44474872,29884416,16777216,67108864,19941992,40632320,33554432,85393408,37140328,0,0,64697,0,2,1088,8,0,0,0
2015-10-22 19:21:24.435,132251648,134217728,238616576,44655720,29884416,16777216,67108864,19943968,40632320,33554432,85393408,37321176,0,0,64697,0,2,1088,8,0,0,0
2015-10-22 19:21:26.445,132251648,134217728,238616576,45086264,29884416,16777216,67108864,19943968,40632320,33554432,85393408,37751720,0,0,64697,0,2,1088,8,0,0,0
```

### package

Create jar file(mvn package) or Download jar

## License

MIT

## Author

[uzresk](https://github.com/uzresk)

