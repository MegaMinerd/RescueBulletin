package minerd.relic.data;

import java.io.IOException;

import minerd.relic.file.BufferedDataHandler;

public class Levelmap {
	int[] exp, hp, att, def, sa, sd;
	
	public Levelmap(BufferedDataHandler data, Pokemon parent) throws IOException {
		data.seek(12);
		
		exp = new int[100];
		hp = new int[100];
		att = new int[100];
		def = new int[100];
		sa = new int[100];
		sd = new int[100];
		
		exp[0] = 0;
		hp[0] = parent.getBaseHp();
		att[0] = parent.getBaseAtk();
		def[0] = parent.getBaseDef();
		sa[0] = parent.getBaseSpa();
		sd[0] = parent.getBaseSpd();
		
		for(int i=1; i<100; i++) {
			exp[i] = data.readInt();

        	//System.out.println(data.getFilePointer() + ": " + data.readByte());
			hp[i] = hp[i-1] + data.readByte();
			data.skip(1);
			att[i] = att[i-1] + data.readByte();
			sa[i] = sa[i-1] + data.readByte();
			def[i] =def[i-1] + data.readByte();
			sd[i] = sd[i-1] + data.readByte();
			data.skip(2);
		}
	}

	public int[] getExp() {
		return exp;
	}

	public int[] getHp() {
		return hp;
	}

	public int[] getAtt() {
		return att;
	}

	public int[] getDef() {
		return def;
	}

	public int[] getSa() {
		return sa;
	}

	public int[] getSd() {
		return sd;
	}
}
