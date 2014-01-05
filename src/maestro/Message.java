package maestro;

/**
 *
 * @author Rick
 */
public class Message {

    byte[] data;
    int headerlength;
    int version;
    int type;
    int datalength;
    
    public Message(byte[] identifier) {
        this(identifier, null);
    }

    public Message(byte[] identifier, byte[] data) {
        assert (identifier.length == 16);

        headerlength = StreamUtils.getInt(identifier, 0);
        assert(headerlength == 16);
        version = StreamUtils.getInt(identifier, 4);
        assert(version == 1);
        
        type = StreamUtils.getInt(identifier, 8);
        
        datalength = StreamUtils.getInt(identifier, 12);
        if (data != null) {
            assert (data.length == datalength);
        }
        this.data = data;
    }

    @Override
    public String toString() {
        String str = "Messagetype: " + type;
        if (data != null) {
            //str += "Data (num): " + Arrays.toString(data);
            str += ", Data (str): " + new String(data);
        } else {
            //str += "Data (num): no data";
            str += ", Data (str): no data";
        }
        return str;
    }
}
