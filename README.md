# Why Flink?

https://www.bluepiit.com/blog/spark-vs-flink-which-of-the-two-will-win/

# Pre-requisites

    - Java SE 11 - https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_linux-x64_bin.tar.gz
    - Intellij - Community Addition
    - Flink https://www.apache.org/dyn/closer.lua/flink/flink-1.12.2/flink-1.12.2-bin-scala_2.11.tgz
    - NiFi - https://archive.apache.org/dist/nifi/1.12.1/nifi-1.12.1-bin.tar.gz

## Nifi

- RAW Socket site-to-site
  communications https://community.cloudera.com/t5/Support-Questions/Remote-instance-of-NiFi-is-not-configured-to-allow-RAW/td-p/136978
  nifi.remote.input.host=<FQDN of Host>              <-- Set to resolveable FQDN by all Nodes
  nifi.remote.input.secure=false                     <-- Set to True on if NiFi is running HTTPS
  nifi.remote.input.socket.port=<Port used for S2S)  <-- Needs to be set to support Raw/enable S2S
  nifi.remote.input.http.enabled=true                <-- Set if you want to support HTTP transport
  nifi.remote.input.http.transaction.ttl=30 sec

# Clustering With Nifi and Flink

This repo is inspired by  Apache Flink examples for Fraudulent Activity Detetion. The aim of this project is to provide a way to use
Unsupervised Learning to detect clusters in user activity.


To be able to run Flink, the only requirement is to have a working **Java 8 or 11** installation.

## Run this project

### Clean and package the feature extraction project

```mvn
mvn clean package
```

this will generate the .jar file, now use the jar file to run on the flink cluster. Take the jar file inside your flink
repo and run the following commands.

1) start cluster:

```mvn
./bin/start-cluster.sh
```

2) run the jar on flink:

```mvn
./bin/flink run ./frauddetection-0.1.jar
```

3) Additionally, you can check Flink’s Web UI to monitor the status of the Cluster and running Job.
   <br>Firstly, let's config the rest ports for the web UI

```yml
rest.port: 8081
rest.address: 0.0.0.0

```

Now we can access the UI at localhost port 8081 using the link [Flink Local UI](http://localhost:8081)
![UI](https://github.com/blurred-machine/Flink-Fraud-Detection-with-DataStream-API/blob/main/images/ui.png)

4) To stop the cluster:

```mvn
./bin/stop-cluster.sh
```




