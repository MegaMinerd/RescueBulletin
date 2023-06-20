package minerd.relic.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileDataHandler implements DataHandler {
    private RandomAccessFile file;
    private String filename;
    
    public FileDataHandler(String name){
        this(new File(name));
    }
	
    public FileDataHandler(File fileIn){
        if(!fileIn.exists())
            fileIn.mkdirs();
        try{
            file = new RandomAccessFile(fileIn, "rw");
        }catch(FileNotFoundException e){
            //Theoretically unreachsble
        }
        filename = fileIn.getName();
    }
    
    public String getFilename() {
        return filename;
    }
    
    public int getFilePointer() throws IOException{
        return (int)file.getFilePointer();
    }
	    
    public int length() throws IOException{
        return (int)file.length();
    }

    public void skip(int offset) throws IOException{
        file.skipBytes(offset);
    }
        
    public void seek(int offset) throws IOException{
        file.seek(offset);
    }

    public void seek(Pointer pointer) throws IOException{
        if(pointer == null) return;
        seek(pointer.isAbsolute() ? pointer.getOffset() : pointer.getOffset() + getFilePointer());
    }

    /**
     * Reads the next byte and returns the previous file pointer.
     * 
     * @return The next byte in the file
     * @throws IOException
     */
    public int peek() throws IOException{
        int data = file.readUnsignedByte();
        seek(getFilePointer()-1);
        return data;
    }

    /**
     * Reads the next length bytes and returns the previous file pointer.
     *
     * @param length The number of bytes to read
     * @return The next length bytes in the file
     * @throws IOException
     */
    public long peek(int length) throws IOException{
        long data = read(length);
        seek(getFilePointer()-length);
        return data;
    }
    
    /**
     * Read bytes until a supplied array is full
     * 
     * @param data The array to store the read bytes
     * @throws IOException
     */
    public void read(byte[] data) throws IOException{
        file.read(data);
    }

    /**
     * Reads a little-endian number of specified length
     * 
     * @param length The length of the number in bytes
     * @throws IOException
     */
    public long read(int length) throws IOException{
        long value = 0;
        for(int i=0; i<length; i++)
            value |= file.readUnsignedByte() << (8*i);
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
    public int[] readMask(int totalLen, int... partLens) throws IOException{
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
    * 
    * @return The integer offset represented by the pointer
    * @throws InvalidPointerException If the bytes read are not a valid pointer
    */
    //TODO
    public Pointer parsePointer() throws IOException, InvalidPointerException{
    	return null;
    }

    public String readString() throws IOException{
        String output = "";
        while(true) {
            try {
                byte current = file.readByte();
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

    /**
    * Reads a null-terminated string at a specified offset and returns the file
    * pointer to its previous location
    * 
    * @param pointer
    * @return The string that was read
    */
    public String readString(Pointer pointer) throws IOException, InvalidPointerException{
        int mark = getFilePointer();
        seek(pointer);
        String str = readString();
        seek(mark);
        return str;
    }

    public boolean readBoolean() throws IOException{
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
	
    public void write(byte[] data) throws IOException{
    	
    }

    /**
    * Writes a little-endian number of specified length
    * 
    * @param length The length of the number in bytes
    * @param in     The data to be written
    * @throws IOException
    */
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

	@Override
	public void write(int length, long in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeMask(int[] data, int totalLen, int... subLens) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writePointer(Pointer pointer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeString(String in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeString(String in, Pointer pointer) throws IOException {
		// TODO Auto-generated method stub
		
	}
}