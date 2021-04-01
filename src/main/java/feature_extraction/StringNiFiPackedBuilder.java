package feature_extraction;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacketBuilder;
import org.apache.flink.streaming.connectors.nifi.StandardNiFiDataPacket;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Produces a dictionary file for NiFi containing the log levels that should be
 * collected based on the rate of error and warn messages coming in.
 */
public class StringNiFiPackedBuilder implements NiFiDataPacketBuilder<String> {


    @Override
    public NiFiDataPacket createNiFiDataPacket(String s, RuntimeContext runtimeContext) {
        byte[] content = s.getBytes(StandardCharsets.UTF_8);
        return new StandardNiFiDataPacket(content, new HashMap<>());
    }
}