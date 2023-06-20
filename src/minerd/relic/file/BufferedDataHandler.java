package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BufferedDataHandler implements DataHandler {
	protected ByteBuffer buffer;

	public BufferedDataHandler(int size) {
		this.buffer = ByteBuffer.allocate(size);
	}

	public BufferedDataHandler(ByteBuffer bufferIn) {
		this.buffer = bufferIn;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int getFilePointer() throws IOException {
		return buffer.position();
	}

	public int length() throws IOException {
		return buffer.capacity();
	}

	public void skip(int offset) throws IOException {
		buffer.position(buffer.position() + offset);
	}

	public void seek(int offset) throws IOException {
		buffer.position(offset);
	}

	public void seek(Pointer pointer) throws IOException {
		if(pointer == null) return;
		buffer.position(pointer.isAbsolute() ? pointer.getOffset() : pointer.getOffset() + getFilePointer());
	}

	/**
	 * Reads the next byte and returns the previous file pointer.
	 * 
	 * @return The next byte in the file
	 * @throws IOException
	 */
	public int peek() throws IOException {
		buffer.mark();
		int data = readUnsignedByte();
		buffer.reset();
		return data;
	}

	/**
	 * Reads the next length bytes and returns the previous file pointer.
	 * 
	 * @param length The number of bytes to read
	 * @return The next length bytes in the file
	 * @throws IOException
	 */
	public long peek(int length) throws IOException {
		buffer.mark();
		long data = read(length);
		buffer.reset();
		return data;
	}

	/**
	 * Read bytes until a supplied array is full
	 * 
	 * @param data The array to store the read bytes
	 * @throws IOException
	 */
	public void read(byte[] data) throws IOException {
		buffer.get(data);
	}

	/**
	 * Reads a little-endian number of specified length
	 * 
	 * @param length The length of the number in bytes
	 * @throws IOException
	 */
	public long read(int length) throws IOException {
		long value = 0;
		for(int i = 0; i<length; i++){
			value |= (buffer.get() & 0xFF) << (8*i);
		}
		return value;
	}

	/**
	 * Reads multiple values packed into totalLen bytes
	 * 
	 * @param totalLen The number of bytes to read
	 * @param partLens The lengths of each subvalue in bits
	 * @return An array of subvalues
	 * @throws IOException
	 */
	public int[] readMask(int totalLen, int... partLens) throws IOException {
		long mask = read(totalLen);
		int[] parts = new int[partLens.length];
		int runningTotal = 0;
		for(int i = 0; i<partLens.length; i++){
			int submask = (1 << partLens[i]) - 1;
			submask <<= runningTotal;
			parts[i] = (int) ((mask & submask) >> runningTotal);
			runningTotal += partLens[i];
		}
		return parts;
	}

	/**
	 * Reads a pointer
	 * 
	 * @return The integer offset represented by the pointer
	 * @throws InvalidPointerException If the bytes read are not a valid pointer
	 */
	public Pointer parsePointer() throws IOException, InvalidPointerException {
		int[] output = readMask(4, 27, 5);
		if(output[1]>2)
			throw new InvalidPointerException(getFilePointer());
		return (output[1]==0 && output[0]==0) ? null : new Pointer(output[0], output[1]==1);
	}

	public String readString() throws IOException {
		String output = "";
		while(true){
			try{
				byte current = readByte();
				if(current==0x00)
					break;
				else
					output += (char) current;
			} catch(IOException e){
				break;
			}
		}
		return output;
	}

	/**
	 * Reads a null-terminated string at a specified offset and returns the file
	 * pointer to its previous location
	 * 
	 * @param pointer
	 * @return The string that was read
	 */
	public String readString(Pointer pointer) throws IOException, InvalidPointerException {
		if(pointer==null)
			return "";
		int mark = getFilePointer();
		seek(pointer);
		String str = readString();
		// Using mark() and reset() cause an InvalidMarkException while trying to read
		// Friend Area names and I have no idea why
		seek(mark);
		return str;
	}
    
	public boolean readBoolean() throws IOException {
		return read(1)!=0;
	}

	public byte readByte() throws IOException {
		return (byte) read(1);
	}

	public int readUnsignedByte() throws IOException {
		return (int) (read(1) & 0xFF);
	}

	public short readShort() throws IOException {
		return (short) read(2);
	}

	public int readUnsignedShort() throws IOException {
		return (int) (read(2) & 0xFFFF);
	}

	public int readInt() throws IOException {
		return (int) read(4);
	}

	public long readUnsignedInt() throws IOException {
		return read(4) & 0xFFFFFFFF;
	}
	
	public void write(byte[] data) throws IOException {
		buffer.put(data);
	}

	/**
	 * Writes a little-endian number of specified length
	 * 
	 * @param length The length of the number in bytes
	 * @param in     The data to be written
	 * @throws IOException
	 */
	public void write(int length, long in) throws IOException {
		for(int i = 0; i<length; i++){
			buffer.put((byte) (in & 0xFF));
			in >>= 8;
		}
	}

	public void writeMask(int[] data, int totalLen, int... subLens) throws IOException {
		long mask = 0;
		int runningTotal = 0;
		for(int i = 0; i<data.length; i++){
			mask |= data[i] << runningTotal;
			runningTotal += subLens[i];
		}
		write(totalLen, mask);
	}

	public void writePointer(Pointer pointer) throws IOException {
		writeInt(pointer.getOffset() + (pointer.isAbsolute() ? 0x08000000 : 0));
	}

	public void writeString(String in) throws IOException {
		for(char c : in.toCharArray())
			buffer.putChar(c);
		for(int i = 0; i<4 - (in.length()%4); i++)
			buffer.put((byte) 0);
	}

	public void writeString(String in, Pointer pointer) throws IOException {
		buffer.mark();
		seek(pointer);
		writeString(in);
		buffer.reset();
	}

	public void writeBoolean(boolean in) throws IOException {
		write(1, in ? 1 : 0);
	}

	public void writeByte(byte in) throws IOException {
		write(1, in);
	}

	public void writeUnsignedByte(int in) throws IOException {
		write(1, in & 0xFF);
	}

	public void writeShort(short in) throws IOException {
		write(2, in);
	}

	public void writeUnsignedShort(int in) throws IOException {
		write(2, in & 0xFFFF);
	}

	public void writeInt(int in) throws IOException {
		write(4, in);
	}

	public void writeUnsignedInt(long in) throws IOException {
		write(4, in & 0xFFFFFFFF);
	}
    
    //Decompress data at an offset and return to original location
    //Comments starting with - indicate original python
    public BufferedDataHandler at4px(int offset, boolean isTiles) throws IOException {
        //System.out.println("AT4");
        //System.out.println("Offset:" + Integer.toHexString(offset));
        seek(offset);
        ArrayList<Byte> data = new ArrayList<Byte>();
        //if rom.read(5) != b'AT4PX':
        //	raise ValueError('Wrong magic bytes for compressed data')
        skip(7);
        //end, = unpack('<H', rom.read(2))
		
        // The control codes used for 0-flags vary
        //controls = list(rom.read(9))
        ArrayList<Byte> controls = new ArrayList<Byte>();
        for(int i=0; i<9; i++) {
            controls.add(readByte());
        }
		
        //length, = unpack('<H', rom.read(2))
        int len = readShort();
        if(isTiles) {
            data.add((byte)3);
            data.add((byte)0);
            data.add((byte)3);
            data.add((byte)0);
            //System.out.println("Length:" + Integer.toHexString(len));
            data.add((byte)((len/0x20)&0xFF));
            data.add((byte)(((len/0x20)&0xFF00)>>8));
            for(int i=0; i<10; i++)
            data.add((byte)0);
        }
        
        while((isTiles ? data.size()-0x10 : data.size()) < len) {
            byte flags = readByte();
            //if(!isTiles) System.out.println("Flags:" + Integer.toHexString(flags));
            for(int i=0; i<8; i++) {
                if((flags&(0x80>>i))!=0) {
                    // Flag 1: append one byte as-is
                    data.add((byte)(readUnsignedByte()&0xFF));
                }else {
                    // Flag 0: do one of two fancy things based on the next byte's
                    // high and low nybbles
                    byte control = readByte();
                    byte high = (byte)((control >> 4)&0x0F);
                    byte low = (byte)(control & 0x0F);

                    if(controls.contains(high)){
                        // Append a pattern of four nybbles. The high bits determine
                        // the pattern, and the low bits determine the base nybble.
                        control = (byte)controls.indexOf(high);
                        byte[] nybbles = {low, low, low, low};
                        
                        if(control==0) {}
                        else if(control<=4) {
                            // Lower a particular pixel
                            if(control==1)
                                for(int j=0; j<4; j++)
                                    nybbles[j]++;
                            nybbles[control-1]--;
                        }
                        else {
                            // 5 <= control <= 8; raise a particular pixel
                            if(control==5)
                                for(int j=0; j<4; j++)
                                    nybbles[j]--;
                            nybbles[control-5]++;
                        }
                        // Pack the pixels into bytes and append them
                        data.add((byte)((nybbles[0]<<4) | nybbles[1]));
                        data.add((byte)((nybbles[2]<<4) | nybbles[3]));
                    }
                    else {
                        // Append a sequence of bytes previously used in the data.
                        // This can overlap with the beginning of the appended bytes!
                        // The high bits determine the length of the sequence, and
                        // the low bits help determine the where the sequence starts.
                        int off = -0x1000;
                        off += ((low << 8) | (readByte()&0xFF));
                        for(int j=0; j<(high+3); j++)
                        try {
                            data.add(data.get(data.size()+off));
                        }catch(IndexOutOfBoundsException e) {
                            data.add((byte)0);
                        }
                    }
                }
                if((isTiles ? data.size()-0x10 : data.size()) >= len)
                    break;
            }
        }
        
        BufferedDataHandler output = new BufferedDataHandler(data.size());
        for(byte b : data)
            output.writeByte(b);
        if(!isTiles){
            output.seek(0xE);
            output.writeShort((short)(data.size()/18));
        }
        return output;
    }
}