package minerd.relic.file;

import java.io.IOException;

//TODO; maybe replace with abstract class
public interface DataHandler {
	public int getFilePointer() throws IOException;

	public int length() throws IOException;

	public void skip(int offset) throws IOException;

	public void seek(int offset) throws IOException;

	public void seek(Pointer pointer) throws IOException;

	/**
	 * Reads the next byte and returns the previous file pointer.
	 * 
	 * @return The next byte in the file
	 * @throws IOException
	 */
	public int peek() throws IOException;

	/**
	 * Reads the next length bytes and returns the previous file pointer.
	 *
	 * @param length The number of bytes to read
	 * @return The next length bytes in the file
	 * @throws IOException
	 */
	public long peek(int length) throws IOException;

	/**
	 * Read bytes until a supplied array is full
	 * 
	 * @param data The array to store the read bytes
	 * @throws IOException
	 */
	public void read(byte[] data) throws IOException;

	/**
	 * Reads a little-endian number of specified length
	 * 
	 * @param length The length of the number in bytes
	 * @throws IOException
	 */
	public long read(int length) throws IOException;

	/**
	 * Reads multiple values packed into totalLen bytes
	 * 
	 * @param totalLen The number of bytes to read
	 * @param partLens The lengths of each subvalue in bits
	 * @return An array of subvalues
	 * @throws IOException
	 */
	public int[] readMask(int totalLen, int... partLens) throws IOException;

	/**
	 * Reads a pointer
	 * 
	 * @return The integer offset represented by the pointer
	 * @throws InvalidPointerException If the bytes read are not a valid pointer
	 */
	public Pointer parsePointer() throws IOException, InvalidPointerException;

	public String readString() throws IOException;

	/**
	 * Reads a null-terminated string at a specified offset and returns the file pointer to its previous location
	 * 
	 * @param pointer
	 * @return The string that was read
	 */
	public String readString(Pointer pointer) throws IOException, InvalidPointerException;

	public boolean readBoolean() throws IOException;

	public byte readByte() throws IOException;

	public int readUnsignedByte() throws IOException;

	public short readShort() throws IOException;

	public int readUnsignedShort() throws IOException;

	public int readInt() throws IOException;

	public long readUnsignedInt() throws IOException;

	public void write(byte[] data) throws IOException;

	/**
	 * Writes a little-endian number of specified length
	 * 
	 * @param length The length of the number in bytes
	 * @param in     The data to be written
	 * @throws IOException
	 */
	public void write(int length, long in) throws IOException;

	public void writeMask(int[] data, int totalLen, int... subLens) throws IOException;

	public void writePointer(Pointer pointer) throws IOException;

	public void writeString(String in) throws IOException;

	public void writeString(String in, Pointer pointer) throws IOException;

	public void writeBoolean(boolean in) throws IOException;

	public void writeByte(byte in) throws IOException;

	public void writeUnsignedByte(int in) throws IOException;

	public void writeShort(short in) throws IOException;

	public void writeUnsignedShort(int in) throws IOException;

	public void writeInt(int in) throws IOException;

	public void writeUnsignedInt(long in) throws IOException;
}