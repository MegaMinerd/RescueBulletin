package minerd.relic;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A singleton encapsulation for the ROM file
 */
public class RomManipulator {
	private RandomAccessFile file;
	private String filename;
	public static RomManipulator instance;
	
	public RomManipulator(File fileIn) throws FileNotFoundException{
		file = new RandomAccessFile(fileIn, "rw");
		filename = fileIn.getName();
		instance = this;
	}
	
	public static String getFilename() {
		return instance.filename;
	}
	
	public static void skip(int offset) throws IOException {
		instance.file.skipBytes(offset);
	}
	
	public static void seek(int offset) throws IOException{
		instance.file.seek(offset);
	}
	
	/**
	 * Reads the next byte and returns the file pointer. Used by CodeConverter to check if the next command is a label.
	 * @return The next byte in the file 
	 */
	public static int peek() throws IOException {
		int data = instance.file.readUnsignedByte();
		seek(getFilePointer()-1);
		return data;
	}
	
	public static int getFilePointer() throws IOException {
		return (int)instance.file.getFilePointer();
	}
	
	/**
	 * Read bytes from the ROM until a supplied array is full
	 * @param data The array to store the read bytes
	 */
	public static void read(byte[] data) throws IOException {
		instance.file.read(data);
	}
	
	/**
	 * Reads a pointer from the ROM
	 * @return The integer offset represented by the pointer
	 * @throws InvalidPointerException If the bytes read are not a valid pointer
	 */
	public static int parsePointer() throws IOException, InvalidPointerException{
		int output = 0;
		output += instance.file.readUnsignedByte();
		output += (instance.file.readUnsignedByte() << 8);
		output += (instance.file.readUnsignedByte() << 16);
		int magicByte = instance.file.readUnsignedByte();
		if(magicByte!=0x08 && magicByte!=0x09)
			throw new InvalidPointerException((int)instance.file.getFilePointer() - 4);
		output += ((magicByte&0x01) << 24);
		return output;
	}
	
	public static int parsePointer(int offset) throws IOException, InvalidPointerException{
		instance.file.seek(offset);
		return parsePointer();
	}
	
	/**
	 * Reads a null-terminated string at a specified offset from  the ROM and returns the file pointer to its previous location
	 * @param pointer
	 * @return The string that was read
	 */
	public static String readStringAndReturn(int offset) throws IOException {
		int link = getFilePointer();
		seek(offset);
		String str = readString();
		seek(link);
		return str;
	}

	public static String readString(int offset) throws IOException {		
		seek(offset);
		return readString();
	}
	
	public static String readString() throws IOException {
		String output = "";
		while(true) {
			try {
				byte current = instance.file.readByte();
				if(current == 0x00)
					break;
				else
					output += (char)current;
			}catch(EOFException eof) {
				break;
			}catch(IOException io) {
				break;
			}
		}
		return output;
	}
	
	public static byte readByte(int offset) throws IOException{
		seek(offset);
		return readByte();
	}
	
	public static byte readByte() throws IOException{
		return instance.file.readByte();
	}
	
	public static int readUnsignedByte() throws IOException{
		return instance.file.readUnsignedByte();
	}
	
	public static short readShort(int offset) throws IOException{
		seek(offset);
		return readShort();
	}
	
	public static short readShort() throws IOException{
		short output = 0;
		output += instance.file.readUnsignedByte();
		output += (instance.file.readUnsignedByte() << 8);
		return output;
	}
	
	public static int readInt(int offset) throws IOException{
		seek(offset);
		return readInt();
	}
	
	public static int readInt() throws IOException{
		int output = 0;
		output += instance.file.readUnsignedByte();
		output += (instance.file.readUnsignedByte() << 8);
		output += (instance.file.readUnsignedByte() << 16);
		output += (instance.file.readUnsignedByte() << 24);
		return output;
	}
	
	/**
	 * Find sufficient space in a specified section of the ROM for data of a certain length 
	 * @return The offset at the beginning of the space found or -1 if there is not enough space
	 */
	public static long findSpace(int minOffset, int maxOffset, int length){
		//Align inputs to words
		while(length%4!=0)
			length++;
		while(minOffset%4!=0)
			minOffset++;
		while(maxOffset%4!=0)
			maxOffset--;
		
		int foundRoom = 0;
		int offset = minOffset;
		
		do {
			try {
				int temp = readInt(offset+foundRoom);
				if(temp == 0xFFFFFFFF) {
					foundRoom+=4;
					if(foundRoom>=length)
						return offset;
				}else {
					offset+=foundRoom+4;
					foundRoom=0;
				}
			}catch(EOFException eofe) {
				return -1;
			}catch(IOException ioe) {
				
			}
		}while(offset+foundRoom <= maxOffset-4);
		
		return -1;
	}
	
	public static void writeStringAndReturn(String in, int offset) throws IOException {
		int link = getFilePointer();
		seek(offset);
		writeString(in);
		seek(link);
	}
	
	public static void writeString(String in, int offset) throws IOException{
		instance.file.seek(offset);
		writeString(in);
	}
	
	public static void writeString(String in) throws IOException {
		instance.file.writeBytes(in);
		for(int i=0; i<4-(in.length()%4); i++)
			instance.file.writeByte(0);
	}
	
	public static void writeByte(byte in, long offset) throws IOException{
		instance.file.seek(offset);
		instance.file.writeByte(in);
	}
	
	public static void writeByte(byte in) throws IOException{
		instance.file.writeByte(in);
	}
	
	public static void writeShort(short in, long offset) throws IOException{
		instance.file.seek(offset);
		writeShort(in);
	}
	
	public static void writeShort(short in) throws IOException{
		instance.file.writeByte(in & 0xFF);
		instance.file.writeByte((in & 0xFF00) >> 8);
	}
	
	public static void writeInt(int in, long offset) throws IOException{
		instance.file.seek(offset);
		writeInt(in);
	}
	
	public static void writeInt(int in) throws IOException{
		instance.file.writeByte(in & 0xFF);
		instance.file.writeByte((in & 0xFF00) >> 8);
		instance.file.writeByte((in & 0xFF0000) >> 16);
		instance.file.writeByte((in & 0xFF000000) >> 24);
	}
	
	public static void writePointer(int pointer, int offset) throws IOException{
		writeInt(pointer+0x08000000, offset);
	}
	
	public static void writePointer(int pointer) throws IOException{
		writeInt(pointer+0x08000000);
	}
	
	public static int length() throws IOException{
		return (int)instance.file.length();
	}
	
	public static void setLength(long length) throws IOException{
		instance.file.setLength(length);
	}
}
