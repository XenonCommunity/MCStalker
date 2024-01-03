package ir.xenoncommunity.utils;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SuppressWarnings("unused")
@UtilityClass
public class PacketUtils {

    public void writeVarInt(DataOutputStream out, int value) {
        try {
            while (true) {
                if ((value & 0xFFFFFF80) == 0) {
                    out.writeByte(value);
                    return;
                }
                out.writeByte(value & 0x7F | 0x80);
                value >>>= 7;
            }
        } catch (Exception ignored) {
        }
    }

    public void writeString(DataOutputStream out, String value) {
        try {
            final byte[] data = value.getBytes(StandardCharsets.UTF_8);
            writeVarInt(out, data.length);
            out.write(data, 0, data.length);
        } catch (Exception ignored) {
        }
    }

    public void writeStringC(DataOutputStream out, String value) {
        try {
            final byte[] data = value.getBytes(StandardCharsets.UTF_8);
            final byte[] after = new byte[data.length];
            int i = 0;
            while (true) {
                if (i >= data.length) {
                    writeVarInt(out, after.length);
                    out.write(after, 0, after.length);
                    return;
                }
                after[i] = data[i];
                i++;
            }
        } catch (Exception ignored) {
        }
    }

    public byte[] createLogin(String str, int protocol) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        try {
            out.write(0);
            writeStringPZ(out, str);
            if (protocol > 758) {
                out.writeBoolean(false);
                out.writeBoolean(false);
            }
        } catch (Exception ignored) {
        }
        return bytes.toByteArray();
    }

    public byte[] createUpLogin(String str) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final DataOutputStream login = new DataOutputStream(buffer);
        try {
            writeVarInt(login, 0);
            login.write(0);
            login.write(str.getBytes());
        } catch (Exception ignored) {
        }
        return buffer.toByteArray();
    }

    public void writeString(DataOutputStream out, String string, Charset charset) {
        try {
            final byte[] bytes = string.getBytes();
            writeVarInt(out, bytes.length);
            out.write(bytes);
        } catch (Exception ignored) {
        }
    }

    public void sendPacket(byte[] packet, DataOutputStream out) {
        writePacket(out, packet);
    }

    public void writePacket(DataOutputStream out, byte[] packet) {
        try {
            writeVarInt(out, packet.length);
            out.write(packet);
        } catch (Exception ignored) {
        }
    }

    public void writeVarInt(ByteArrayOutputStream out, int paramInt) {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write(paramInt);
                return;
            }
            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public void writeVarLong(ByteArrayOutputStream out, long paramLong) {
        while (true) {
            if ((paramLong & 0xFFFFFFFFFFFFFF80L) == 0L) {
                out.write((int) paramLong);
                return;
            }
            out.write((int) (paramLong & 0x7FL | 0x80L));
            paramLong >>>= 7L;
        }
    }

    public int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            byte k = in.readByte();
            i |= (k & Byte.MAX_VALUE) << j++ * 7;
            if (j <= 5) {
                if ((k & 0x80) != 128)
                    return i;
                continue;
            }
            throw new RuntimeException("VarInt too big");
        }
    }

    public long readVarLong(DataInputStream in) throws IOException {
        long value = 0L;
        int position = 0;
        while (true) {
            byte currentByte = in.readByte();
            value |= (long) (currentByte & Byte.MAX_VALUE) << position;
            if ((currentByte & 0x80) == 0)
                break;
            position += 7;
            if (position >= 64)
                throw new RuntimeException("VarLong is too big");
        }
        return value;
    }

    public void writeUuid(UUID uuid, DataOutputStream out) {
        try {
            out.writeLong(uuid.getMostSignificantBits());
            out.writeLong(uuid.getLeastSignificantBits());
        } catch (Exception ignored) {
        }
    }

    public String readString(DataInputStream in) throws IOException {
        final int len = readVarInt(in);
        final byte[] data = new byte[len];
        in.readFully(data);
        return new String(data, 0, len, StandardCharsets.UTF_8);
    }

    public byte[] readByteArray(DataInputStream in) throws IOException {
        final int len = readVarInt(in);
        final byte[] data = new byte[len];
        in.readFully(data);
        return data;
    }


    public void writeHackHandshakePacket(DataOutputStream out, String ip, String srcAddr, String Fakeuser, int port, int protocol, int state) {
        try {
            writeVarInt(out, 0);
            writeVarInt(out, protocol);
            writeString(out, String.format("%s\000%s\000%s", ip, srcAddr, UUID.nameUUIDFromBytes(("OffilePlayer:" + Fakeuser).getBytes()).toString().replace("-", "")));
            out.writeShort(port);
            writeVarInt(out, state);
        } catch (Exception ignored) {
        }
    }

    public void writeByteArray(DataOutputStream out, byte[] data) throws IOException {
        writeVarInt(out, data.length);
        out.write(data, 0, data.length);
    }

    public void writeHandshakePacket(DataOutputStream out, String ip, int port, int protocol, int state) {
        try {
            writeVarInt(out, 0);
            writeVarInt(out, protocol);
            writeString(out, ip);
            out.writeShort(port);
            writeVarInt(out, state);
        } catch (Exception ignored) {
        }
    }

    public void writePingPacket(DataOutputStream out, long clientTime) throws IOException {
        writeVarInt(out, 1);
        out.writeLong(clientTime);
    }

    public void writePacket(byte[] packetData, DataOutputStream out) throws IOException {
        writeVarInt(out, packetData.length);
        out.write(packetData);
    }


    public byte[] createLoginPacket() throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeVarInt(out, 0);
        writeString(out, String.valueOf(Math.random()).replaceAll("\\.", ""));
        final byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }

    public void writeVarIntToBuffer(int input, DataOutputStream out) throws IOException {
        while ((input & 0xFFFFFF80) != 0) {
            out.writeByte(input & 0x7F | 0x80);
            input >>>= 7;
        }
        out.writeByte(input);
    }

    public byte[] createPingPacket() throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writePingPacket(out, System.currentTimeMillis());
        final byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }

    public byte[] createStatusPacket(String ip, int port, int protocol) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        out.write(createHandshakePacket(ip, port, protocol, 1).length);
        out.write(createHandshakePacket(ip, port, protocol, 1));
        writeVarInt(out, new byte[]{0}.length);
        writeVarInt(out, 0);
        final byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }


    public byte[] createHandshakePacket(String ip, int port, int protocol, int state) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeHandshakePacket(out, ip, port, protocol, state);
        final byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }


    public String readServerDataPacket(DataInputStream in) throws IOException {
        byte id = in.readByte();
        if (id == 0)
            return readString(in);
        return null;
    }

    public void writeStringPZ(DataOutputStream out, String value) throws IOException {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, data.length);
        out.write(data, 0, data.length);
    }

    public long readPongPacket(DataInputStream in) throws IOException {
        byte id = in.readByte();
        if (id == 1)
            return in.readLong();
        return -1L;
    }

    public byte[] createLogin(String username) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final DataOutputStream login = new DataOutputStream(buffer);
        login.writeByte(0);
        login.write(username.getBytes());
        return buffer.toByteArray();
    }


    public byte[] createClientStatus(int protocol, int stat) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeVarInt(out, 0);
        if (protocol < 49)
            writeVarInt(bytes, 22);
        if (protocol > 50)
            writeVarInt(bytes, 3);
        writeVarInt(out, stat);
        byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }

    public byte[] createClientSetting(int protocol, int viewDistance) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeVarInt(out, 0);
        if (protocol > 476)
            writeVarInt(out, 5);
        if (protocol > 392 && protocol < 475)
            writeVarInt(out, 3);
        if (protocol > 106 && protocol < 390)
            writeVarInt(out, 4);
        if (protocol == 47)
            writeVarInt(out, 21);
        writeString(out, "en_US");
        out.writeByte(viewDistance);
        if (protocol > 49)
            writeVarInt(bytes, 0);
        if (protocol == 47)
            out.writeByte(0);
        out.write(1);
        byte by = 65;
        out.write(by);
        if (protocol > 48)
            writeVarInt(bytes, 0);
        byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }


    public byte[] createPayload(int protocol, String channel, byte[] datas) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final  DataOutputStream out = new DataOutputStream(bytes);
        writeVarInt(out, 0);
        if (protocol > 476)
            writeVarInt(out, 11);
        if (protocol > 392 && protocol < 475)
            writeVarInt(out, 9);
        if (protocol > 106 && protocol < 341)
            writeVarInt(bytes, 9);
        if (protocol == 47)
            writeVarInt(bytes, 23);
        writeString(out, channel);
        out.write(datas);
        byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }



    public String listenDisconnect(int protocol, DataInputStream in) throws IOException {
        byte id = in.readByte();
        if (id == 26)
            return readString(in);
        return "";
    }
}
