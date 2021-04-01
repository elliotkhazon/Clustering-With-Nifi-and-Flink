package feature_extraction.function;

import feature_extraction.pojo.DeviceActivity;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.util.Collector;

import java.util.Map;

/**
 * A FlatMapFunction that maps a NiFiDataPacket to a tuple of (uuid,1).
 */
public final class DeviceIdFlatMap implements FlatMapFunction<NiFiDataPacket, DeviceActivity> {
    private static final long serialVersionUID = 1L;

    private final String deviceIDAttributeName;

    /**
     * @param deviceIDAttributeName the name of an attribute on the NiFiDataPacket containing the log level
     */
    public DeviceIdFlatMap(final String deviceIDAttributeName) {
        this.deviceIDAttributeName = deviceIDAttributeName;
    }

    @Override
    public void flatMap(NiFiDataPacket niFiDataPacket, Collector<DeviceActivity> collector)
            throws Exception {

        Map<String, String> attributes = niFiDataPacket.getAttributes();

        if (attributes.containsKey(deviceIDAttributeName)) {
            String deviceIdValue = niFiDataPacket.getAttributes().get(deviceIDAttributeName);
            collector.collect(new DeviceActivity(deviceIdValue, 1));
        }
    }

}
