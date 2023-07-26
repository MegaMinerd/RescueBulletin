package minerd.relic.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import minerd.relic.file.BufferedDataHandler;

public class CompressionHandler {
	public static BufferedDataHandler compress(BufferedDataHandler data, boolean isImage) throws IOException {
		data.seek(0);
		int offset = 0;
		ArrayList<Integer> output_nybbles = new ArrayList<Integer>();
		//The list of controls that can't be used because they're taken by something else
		ArrayList<Integer> taken_controls = new ArrayList<Integer>();

		while(offset<data.length()){
			//Compress block
			int subblocks = 0;
			int flags = 0;
			data.seek(offset);
			ArrayList<Integer> block = new ArrayList<Integer>();
			while(subblocks<8 && offset<data.length()){
				//Try to find a repeat
				int max_length = Math.min(19, Math.min(data.length() - offset, offset));
				data.seek(offset);
				byte[] target = new byte[max_length];
				//The way portraits are handled here just be wrapps them in the AT4PX format without actually compressing them.
				//Doing it like level maps causes garbled portraits.
				//Level maps actually will appear compressed if done like portraits, but it'll be all 0s.
				//I do not know what is going on.
				if(isImage)
					data.read(max_length);
				else
					data.read(target);
				int diff = Math.min(0x0FFF, offset);
				int match_diff = -1;
				int match_size = 0;
				int size = 3;
				while(diff - size>=0){
					if(taken_controls.size()>6 && !taken_controls.contains(size - 3)){
						//Can't use a repeat of this size due to conflict with controls
						size++;
						if(size>max_length){
							diff--;
							size = 3;
						}
						continue;
					}
					data.seek(offset - diff);
					byte[] section = new byte[size];
					data.read(section);
					//Compare
					int match = 1;
					for(int i = 0; i<size; i++)
						if(i>=max_length || section[i]!=target[i])
							match = 0;
					if(match==1){
						if(size>match_size){
							match_diff = diff;
							match_size = size;
						}
					}
					size++;
					if(size>max_length){
						diff--;
						size = 3;
					}
				}
				if(match_diff!=-1){
					if(!taken_controls.contains(match_size - 3))
						taken_controls.add(match_size - 3);
					block.add(match_size - 3);
					int value = 0x1000 - match_diff;
					block.add(value >> 8);
					block.add((value >> 4) & 0x0f);
					block.add(value & 0x0f);
					offset += match_size;
					flags = flags << 1;
					subblocks++;
				} else{
					//Try to find a pattern
					byte[] nybbles = { 0, 0, 0, 0 };
					data.seek(offset);
					int temp = data.readUnsignedByte();
					nybbles[0] = (byte) (temp >> 4);
					nybbles[1] = (byte) (temp & 0x0f);
					if(data.getFilePointer()!=data.length()){
						temp = data.readUnsignedByte();
						nybbles[2] = (byte) (temp >> 4);
						nybbles[3] = (byte) (temp & 0x0f);
					}
					byte base = nybbles[0];
					for(int i = 0; i<4; i++)
						nybbles[i] -= base;
					int pattern;
					if(Arrays.equals(nybbles, new byte[] { 0, 0, 0, 0 }))
						pattern = 0x80;
					else if(Arrays.equals(nybbles, new byte[] { 0, 1, 1, 1 }))
						pattern = 0x81;
					else if(Arrays.equals(nybbles, new byte[] { 0, -1, 0, 0 }))
						pattern = 0x82;
					else if(Arrays.equals(nybbles, new byte[] { 0, 0, -1, 0 }))
						pattern = 0x83;
					else if(Arrays.equals(nybbles, new byte[] { 0, 0, 0, -1 }))
						pattern = 0x84;
					else if(Arrays.equals(nybbles, new byte[] { 0, -1, -1, -1 }))
						pattern = 0x85;
					else if(Arrays.equals(nybbles, new byte[] { 0, 1, 0, 0 }))
						pattern = 0x86;
					else if(Arrays.equals(nybbles, new byte[] { 0, 0, 1, 0 }))
						pattern = 0x87;
					else if(Arrays.equals(nybbles, new byte[] { 0, 0, 0, 1 }))
						pattern = 0x88;
					else
						pattern = 0;
					if((pattern & 0x80)!=0 && (data.length() - offset)>=2){
						block.add(pattern);
						block.add(base & 0x0f);
						offset += 2;
						flags = flags << 1;
						subblocks++;
					} else{
						//Add as-is
						data.seek(offset);
						temp = data.readUnsignedByte();
						block.add(temp >> 4);
						block.add(temp & 0x0f);
						offset++;
						flags = flags << 1;
						flags++;
						subblocks++;
					}
				}
			}
			if(subblocks!=0){
				//Pad the flags to 8 bits
				flags <<= (8 - subblocks);
				//Add the block to the output
				output_nybbles.add(flags >> 4);
				output_nybbles.add(flags & 0x0f);
				for(int i = 0; i<block.size(); i++)
					output_nybbles.add(block.get(i));
				System.out.println(offset + " bytes compressed");
			}
		}
		//Generate controls
		ArrayList<Integer> controls = new ArrayList<Integer>();
		for(int i = 0; i<16; i++){
			if(controls.size()==9)
				break;
			if(!taken_controls.contains(i))
				controls.add(i);
		}
		//Inject controls into output nybbles
		for(int i = 0; i<output_nybbles.size(); i++)
			if((output_nybbles.get(i) & 0x80)!=0)
				output_nybbles.set(i, controls.get(output_nybbles.get(i) & 0x0F));
		//Build output
		ArrayList<Integer> output = new ArrayList<Integer>();
		output.add(0x41);
		output.add(0x54);
		output.add(0x34);
		output.add(0x50);
		output.add(0x58);
		int new_size = 18 + (output_nybbles.size()/2);
		output.add(new_size & 0xff);
		output.add(new_size >> 8);
		for(int control : controls)
			output.add(control);
		output.add(data.length() & 0xff);
		output.add(data.length() >> 8);
		for(int i = 0; i<output_nybbles.size(); i += 2)
			output.add((output_nybbles.get(i) << 4) | output_nybbles.get(i + 1));
		return new BufferedDataHandler(ByteBuffer.wrap(toPrimitive(output.toArray(new Integer[1]))));
	}

	private static byte[] toPrimitive(Integer[] input) {
		byte[] output = new byte[input.length];
		for(int i = 0; i<input.length; i++)
			output[i] = (byte) (input[i] & 0xFF);
		return output;
	}
}