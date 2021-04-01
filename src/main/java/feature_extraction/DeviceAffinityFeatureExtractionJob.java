/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feature_extraction;

import feature_extraction.function.DeviceIdFlatMap;
import feature_extraction.pojo.DeviceActivity;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacketBuilder;
import org.apache.flink.streaming.connectors.nifi.NiFiSink;
import org.apache.flink.streaming.connectors.nifi.NiFiSource;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;

/**
 * Skeleton code for the datastream walkthrough
 */
public class DeviceAffinityFeatureExtractionJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Enabling Checkpoint
        long checkpointInterval = 5000;
        env.enableCheckpointing(checkpointInterval);

        SiteToSiteClientConfig clientConfig = new SiteToSiteClient.Builder()
                .url("http://localhost:8080/nifi")
                .portName("To Flink")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .requestBatchCount(5)
                .buildConfig();

        SourceFunction<NiFiDataPacket> nifiSource = new NiFiSource(clientConfig);
        DataStream<NiFiDataPacket> streamSource =
                env.addSource(nifiSource);
        DeviceIdFlatMap deviceIdFlatMap = new DeviceIdFlatMap("deviceID");
        SingleOutputStreamOperator<DeviceActivity> count = streamSource.flatMap(deviceIdFlatMap).keyBy(v -> v.getDeviceId()).sum("count");
        final OutputTag<String> outputTag = new OutputTag<String>("side-output") {
        };

        SingleOutputStreamOperator<DeviceActivity> process = count.process(new ProcessFunction<DeviceActivity, DeviceActivity>() {
            @Override
            public void processElement(DeviceActivity activity, Context context, Collector<DeviceActivity> collector) throws Exception {
                context.output(outputTag, "deviceId : " + String.valueOf(activity.getDeviceId()) + ", count : " + String.valueOf(activity.getCount()));
            }
        });
        final DataStream<String> sideOutput = process.getSideOutput(outputTag);

        SiteToSiteClientConfig clientSinkConfig = new SiteToSiteClient.Builder()
                .url("http://localhost:8080/nifi")
                .portName("From Flink")
                .transportProtocol(SiteToSiteTransportProtocol.HTTP)
                .requestBatchCount(5)
                .buildConfig();


        NiFiDataPacketBuilder<String> builder = new StringNiFiPackedBuilder();
        sideOutput.addSink(new NiFiSink<>(clientSinkConfig, builder));

        env.execute("DeviceAffinity Feature Extraction Job");
    }

}
