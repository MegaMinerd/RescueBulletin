package minerd.relic.file;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RomFile {
	protected ByteBuffer buffer;
	
	public RomFile(int size){
		this.buffer = ByteBuffer.allocate(size);
	}
	
	public RomFile(ByteBuffer bufferIn) {
		this.buffer = bufferIn;
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int getFilePointer() throws IOException {
		return buffer.position();
	}
	
	public void skip(int offset) throws IOException {
		buffer.position(buffer.position()+offset);
	}
	
	public void seek(int offset) throws IOException{
		buffer.position(offset);
	}
	
	public void seek(Pointer pointer) throws IOException{
		buffer.position(pointer.isAbsolute() ? pointer.getOffset() : pointer.getOffset()+getFilePointer());
	}
	
	/**
	 * Reads the next byte and returns the file pointer.
	 * @return The next byte in the file 
	 */
	public int peek() throws IOException {
		int data = readUnsignedByte();
		seek(getFilePointer()-1);
		return data;
	}
	
	/**
	 * Read bytes until a supplied array is full
	 * @param data The array to store the read bytes
	 */
	public void read(byte[] data) throws IOException {
		buffer.get(data);
	}
	
	/**
	 * Reads a little-endian number of specified length
	 * @param length The length of the number in bytes
	 * @throws IOException 
	 */
	public long read(int length) throws IOException {
		long value = 0;
		for(int i=0; i<length; i++) {
			value |= (buffer.get()&0xFF) << (8*i);
		}
		return value;
	}
	
	public int[] readMask(int totalLen, int ... partLens) throws IOException{
		long mask = read(totalLen);
		int[] parts = new int[partLens.length];
		int runningTotal = 0;
		for(int i=0; i<partLens.length; i++){
 			int submask = (1<<partLens[i]) - 1;
			submask <<= runningTotal;
			parts[i] = (int) ((mask & submask) >> runningTotal);
			runningTotal += partLens[i];
		}
		return parts;
	}
	
	/**
	 * Reads a pointer
	 * @return The integer offset represented by the pointer
	 * @throws InvalidPointerException If the bytes read are not a valid pointer
	 */
	public Pointer parsePointer() throws IOException, InvalidPointerException{
		int[] output = readMask(4, 27, 5);
		if (output[1]>2)
			throw new InvalidPointerException(getFilePointer());
		return (output[1]==0 && output[0]==0) ? null : new Pointer(output[0], output[1]==1);
	}
	
	public Pointer parsePointer(int offset) throws IOException, InvalidPointerException{
		seek(offset);
		return parsePointer();
	}
	
	/**
	 * Reads a null-terminated string at a specified offset and returns the file pointer to its previous location
	 * @param pointer
	 * @return The string that was read
	 */
	public String readStringAndReturn(int offset) throws IOException, InvalidPointerException{
		return readStringAndReturn(Pointer.fromInt(offset));
	}
	
	public String readStringAndReturn(Pointer pointer) throws IOException, InvalidPointerException{
		int mark = getFilePointer();
		seek(pointer);
		String str = readString();
		seek(mark);
		return str;
	}

	public String readString(int offset) throws IOException {
		seek(offset);
		return readString();
	}
	
	public String readString(Pointer offset) throws IOException {
		seek(offset);
		return readString();
	}
	
	public String readString() throws IOException {
		String output = "";
		while(true) {
			try {
				byte current = readByte();
				if(current == 0x00)
					break;
				else
					output += (char)current;
			}catch(IOException io) {
				break;
			}
		}
		return output;
	}
	
	public byte readByte() throws IOException{
		return (byte)read(1);
	}
	
	public int readUnsignedByte() throws IOException{
		return (int)(read(1)&0xFF);
	}
	
	public short readShort() throws IOException{
		return (short)read(2);
	}
	
	public int readUnsignedShort() throws IOException{
		return (int)(read(2)&0xFFFF);
	}
	
	public int readInt(int offset) throws IOException{
		seek(offset);
		return (int)read(4);
	}
	
	public int readInt() throws IOException{
		return (int)read(4);
	}
	
	public long readUnsignedInt() throws IOException{
		return read(4)&0xFFFFFFFF;
	}
	
	/**
	 * Writes a little-endian number of specified length
	 * @param length The length of the number in bytes
	 * @param in The data to be written
	 * @throws IOException 
	 */
	public void write(int length, long in) throws IOException {
		for(int i=0; i<length; i++) {
			buffer.put((byte)(in&0xFF));
			in >>= 8;
		}
	}
	
	public void writeMask(int length, int ... data) throws IOException {
		// TODO
	}
	
	public void writeStringAndReturn(String in, int offset) throws IOException {
		int link = getFilePointer();
		seek(offset);
		writeString(in);	
		seek(link);
	}
	
	public void writeString(String in, int offset) throws IOException{
		seek(offset);
		writeString(in);
	}
	
	public void writeString(String in) throws IOException {
		for(char c : in.toCharArray())
			buffer.putChar(c);
		for(int i=0; i<4-(in.length()%4); i++)
			buffer.put((byte)0);
	}
	
	public void writeByte(byte in) throws IOException{
		write(1, in);
	}
	
	public void writeUnsignedShort(int in){
		//Todo
	}
	
	public void writeShort(short in) throws IOException{
		write(2, in);
	}
	
	public void writeInt(int in) throws IOException{
		write(4, in);
	}
	
	public void writePointer(int pointer) throws IOException{
		writeInt(pointer+0x08000000);
	}
	
	public int length() throws IOException{
		return buffer.capacity();
	}
}
