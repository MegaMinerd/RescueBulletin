package minerd.relic.util;

import java.io.IOException;

public class CodeConverter {
	
	/**
	 * Convert a single command from data to text
	 * @param data game-readable data
	 * @return human-readable command
	 */
	public static String interpretCommand(byte[] data) {
		switch((int)(data[0]&0xFF)) {
			case 0x01:
				return "Warp to:\n "
						+ "s=time, i1=map (2/5)";
			case 0x02:
				return "Warp to dungeon: \n"
						+ "s=time i1=id (2/5)";
			case 0x03:
				return "Warp to friend area: \n"
						+ "s=time i1=id (2/5)";
			case 0x04:
				return "Warp to dungeon /w map: \n"
						+ "s=time i1=id (2/5)";
			case 0x0D:
				return "Call next characters: \n"
						+ "b=id s=unk (3/5)";
			case 0x1B:
				return "Play a cutscene: \n"
						+ "s=id (4/5)";
			case 0x23:
				return "Fade screen: \n"
						+ "b=unk s=time i1=unk (3/5)";
			case 0x27:
				return "Fade to color: \n"
						+ "b=unk s=unk i1=time i2=color (3/5)";
			case 0x28:
				return "Fade from color: \n"
						+ "b=unk s=unk i1=time i2=color (2/5)";
			case 0x2D:
				return "Load data to variables: \n"
						+ "b=type s=char i1=source (3/5)";
			case 0x2E:
				return "Change portait: \n"
						+ "b=loc s=char i1=face (5/5)";
			case 0x30:
				return "Close text box: \n"
						+ "(5/5)";
			case 0x3D:
				return "Rename pokemon: \n"
						+ "i1=id (4/5)";
			case 0x3E:
				return "Rename team: \n"
						+ "(4/5)";
			case 0x42:
				return "Stop music: \n"
						+ "(4/5)";
			case 0x43:
				return "Fade out music: \n"
						+ "s=time (4/5)";
			case 0x44:
				return "Start music: \n"
						+ "s=id (4/5)";
			case 0x45:
				return "Fade in music: \n"
						+ "s=id i1=time (4/5)";
			case 0x46:
				return "Memorize music: \n"
						+ "(1/5)";
			case 0x47:
				return "Remember music: \n"
						+ "(1/5)";
			case 0x4C:
				return "Play sound: \n"
						+ "i1=id (4/5)";
			case 0x4D:
				return "Stop sound: \n"
						+ "i1=id (4/5)";
			case 0x54:
				return "Set animation: \n"
						+ "s=id (4/5)";
			case 0x5B:
				return "Warp to waypoint: \n"
						+ "i1=id (4/5)";
			case 0x6A:
				return "Move to relative location: \n"
						+ "s=speed i1=hor i2=ver (4/5)";
			case 0x6B:
				return "Move to waypoint along grid: \n"
						+ "s=speed i1=id (4/5)";
			case 0x7A:
				return "Move to straight waypoint: \n"
						+ "s=speed i1=id (4/5)";
			case 0x91:
				return "Rotate: \n"
						+ "b=speed s=cw/ccw i1=end dir (3/5)";
			case 0xCF:
				return "Switch (variable text): \n"
						+ "b=type s=cond i1=speaker (4/5)";
			case 0xD0:
				return "Case: \n"
						+ "s=value p=text (4/5)";
			case 0xD1:
				return "Default: \n"
						+ "p=text (4/5)";
			case 0xD5:
				return "Question: \n"
						+ "s=unk i1=speaker p=text (4/5)";
			case 0xD8:
				return "Switch (question): \n"
						+ "s=unk i1=speaker p=text (4/5)";
			case 0xD9:
				return "Option: \n"
						+ "s=label p=text (4/5)";
			case 0xDB:
				return "Wait: \n"
						+ "s=time (5/5)";
			case 0xE3:
				return "Wait for flag: \n"
						+ "s=id (5/5)";
			case 0xE4:
				return "Raise signal flag: \n"
						+ "s=id (5/5)";
			case 0xE7:
				return "Go to label: \n"
						+ "s=id (3/5)";
			case 0xE8:
				return "Execute common subroutine: \n"
						+ "s=id (5/5)";
			case 0xE9:
			case 0xEE:
			case 0xEF:
			case 0xF0:
			case 0xF1:
				return "Some sort of script terminator: \n"
						+ "(1/5)";
			case 0xF4:
				return "Label: \n"
						+ "s=id (5/5)";
			default:
				return "Unknown \n";
		}
	}
}
