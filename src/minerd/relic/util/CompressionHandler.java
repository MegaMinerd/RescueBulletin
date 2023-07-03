package minerd.relic.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import minerd.relic.file.BufferedDataHandler;

public class CompressionHandler {
	public static BufferedDataHandler compress(BufferedDataHandler data) throws IOException{
        data.seek(0);
        int offset = 0;
        ArrayList<Byte> output_nybbles = new ArrayList<Byte>();
        //The list of controls that can't be used because they're taken by something else
        ArrayList<Byte> taken_controls = new ArrayList<Byte>();
    
        while(offset < data.length()){
            //Compress block
            int subblocks = 0;
            int flags = 0;
            data.seek(offset);
            ArrayList<Byte> block = new ArrayList<Byte>();
            while(subblocks<8 && offset<data.length()){
                //Try to find a repeat
                int max_length = Math.min(19, Math.min(data.length()-offset, offset));
                data.seek(offset);
                byte[] target = new byte[max_length];
                data.read(max_length);
                int diff = Math.min(0xFFF, offset);
                int match_diff = -1;
                int match_size = 0;
                int size = 3;
                while(diff-size>=0){
                    if(taken_controls.size()>6 && !taken_controls.contains(size-3)){
                        //Can't use a repeat of this size due to conflict with controls
                        size++;
                        if(size>max_length){
                            diff--;
                            size=3;
                        }
                        continue;
                    }
                    data.seek(offset-diff);
                    byte[] section = new byte[size];
                    data.read(section);
                    //Compare
                    int match = 1;
                    for(int i=0; i<size; i++)
                        if(i>=max_length || section[i]!=target[i])
                            match = 0;
                    if(match==1){
                        if(size > match_size){
                            match_diff = diff;
                            match_size = size;
                        }
                    }
                    size++;
                    if(size>max_length){
                        diff--;
                        size=3;
                    }
                }
                if(match_diff!=-1){
                    if(!taken_controls.contains(match_size-3))
                        taken_controls.add((byte) (match_size-3));
                    block.add((byte) (match_size-3));
                    int value = 0x1000 - match_diff;
                    block.add((byte) (value>>8));
                    block.add((byte) ((value>>4)&0xf));
                    block.add((byte) (value&0xf));
                    offset+=match_size;
                    flags = flags<<1;
                    subblocks++;
                } else {
                    //Try to find a pattern
                    byte[] nybbles = {0,0,0,0};
                    data.seek(offset);
                    byte temp = data.readByte();
                    nybbles[0] = (byte) (temp >> 4);
                    nybbles[1] = (byte) (temp & 0xf);
                    if(data.getFilePointer()==data.length()){
                    	temp = data.readByte();
                    	nybbles[2] = (byte) (temp >> 4);
                    	nybbles[3] = (byte) (temp & 0xf);
                    }
                    byte base = nybbles[0];
                    for(int i=0; i<4; i++)
                        nybbles[i] -= base;
                    int pattern;
                    if(Arrays.equals(nybbles, new byte[] {0, 0, 0, 0}))
                        pattern = -1;
                    else if(Arrays.equals(nybbles, new byte[] {0, 1, 1, 1}))
                        pattern = -2;
                    else if(Arrays.equals(nybbles, new byte[] {0, -1, 0, 0}))
                        pattern = -3;
                    else if(Arrays.equals(nybbles, new byte[] {0, 0, -1, 0}))
                        pattern = -4;
                    else if(Arrays.equals(nybbles, new byte[] {0, 0, 0, -1}))
                        pattern = -5;
                    else if(Arrays.equals(nybbles, new byte[] {0, -1, -1, -1}))
                        pattern = -6;
                    else if(Arrays.equals(nybbles, new byte[] {0, 1, 0, 0}))
                        pattern = -7;
                    else if(Arrays.equals(nybbles, new byte[] {0, 0, 1, 0}))
                        pattern = -8;
                    else if(Arrays.equals(nybbles, new byte[] {0, 0, 0, 1}))
                        pattern = -9;
                    else
                        pattern = 0;
                    if(pattern<0 && (data.length()-offset)>=2){
                        block.add((byte) pattern);
                        block.add(base);
                        offset+=2;
                        flags = flags << 1;
                        subblocks++;
                    }
                    else{
                        //Add as-is
                        data.seek(offset);
                        temp = data.readByte();
                        block.add((byte) (temp>>4));
                        block.add((byte) (temp&0xf));
                        offset++;
                        flags = flags << 1;
                        flags+=1;
                        subblocks++;
                    }
                }
            }
            if(subblocks!=0){
                //Pad the flags to 8 bits
                flags = flags<<(8-subblocks);
                //Add the block to the output
                output_nybbles.add((byte) (flags>>4));
                output_nybbles.add((byte) (flags&0xf));
                for(int i=0; i<block.size(); i++)
                    output_nybbles.add(block.get(i));
                System.out.println(offset + " bytes compressed");
            }
        }
        //Generate controls
        ArrayList<Byte> controls = new ArrayList<Byte>();
        for(int i=0; i<16; i++) {
            if(controls.size()==9)
                break;
            if(!taken_controls.contains(i))
                controls.add((byte) i);
        }
        //Inject controls into output nybbles
        for(int i=0; i<output_nybbles.size(); i++)
            if(output_nybbles.get(i)<0)
                output_nybbles.set(i, controls.get(-1-output_nybbles.get(i)));
        //Build output
        ArrayList<Byte> output = new ArrayList<Byte>();
        output.add((byte) 0x41);
        output.add((byte) 0x54);
        output.add((byte) 0x34);
        output.add((byte) 0x50);
        output.add((byte) 0x58);
        int new_size = 18+(output_nybbles.size()/2);
        output.add((byte) (new_size&0xff));
        output.add((byte) (new_size>>8));
        for(byte control : controls)
            output.add(control);
        output.add((byte) (data.length()&0xff));
        output.add((byte) (data.length()>>8));
		for(int i=0; i<output_nybbles.size(); i+=2)	
			output.add((byte) ((output_nybbles.get(i)<<4)+output_nybbles.get(i+1)));
		return new BufferedDataHandler(ByteBuffer.wrap(toPrimitive(output.toArray(new Byte[1]))));	
	}		
			
	private static byte[] toPrimitive(Byte[] input){		
		byte[] output = new byte[input.length];	
		for(int i=0; i<input.length; i++)	
			output[i] = (byte)input[i];
		return output;	
	}		
}			