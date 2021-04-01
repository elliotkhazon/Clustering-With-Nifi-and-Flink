package feature_extraction.pojo;

public class DeviceActivity {
    private String deviceId;
    private int count;

    public DeviceActivity() {
    }

    public DeviceActivity(String deviceID, int count) {
        this.setDeviceId(deviceID);
        this.setCount(count);

    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getCount() {
        return count;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
