package cpabe;
import bswabe.*;
import cpabe.policy.LangPolicy;
import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Cpabe {

	/**
	 * 初始化接口，生成公共参数 PK 和主密钥 MK，并分别存储到 pubfile 和 mskfile 对应的文件路径中去。
	 * @param pubfile
	 * @param mskfile
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void setup(String pubfile, String mskfile) throws IOException,
			ClassNotFoundException {
		byte[] pub_byte, msk_byte;
		BswabePub pub = new BswabePub();
		BswabeMsk msk = new BswabeMsk();
		Bswabe.setup(pub, msk);

		/* store BswabePub into mskfile */
		pub_byte = SerializeUtils.serializeBswabePub(pub);
		Common.spitFile(pubfile, pub_byte);

		/* store BswabeMsk into mskfile */
		msk_byte = SerializeUtils.serializeBswabeMsk(msk);
		Common.spitFile(mskfile, msk_byte);
	}

        /**
         * 私钥生成算法接口，从 pubfile 和 mskfile 指定的文件中分别读取公共参 数 PK 和主密钥 MK，根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到prvfile 指定的文件中。 
         * @param pubfile
         * @param prvfile
         * @param mskfile
         * @param attr_str
         * @throws NoSuchAlgorithmException
         * @throws IOException 
         */
	public void keygen(String pubfile, String prvfile, String mskfile,
			String attr_str) throws NoSuchAlgorithmException, IOException {
		BswabePub pub;
		BswabeMsk msk;
		byte[] pub_byte, msk_byte, prv_byte;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* get BswabeMsk from mskfile */
		msk_byte = Common.suckFile(mskfile);
		msk = SerializeUtils.unserializeBswabeMsk(pub, msk_byte);

		String[] attr_arr = LangPolicy.parseAttribute(attr_str);
		BswabePrv prv = Bswabe.keygen(pub, msk, attr_arr);

		/* store BswabePrv into prvfile */
		prv_byte = SerializeUtils.serializeBswabePrv(prv);
		Common.spitFile(prvfile, prv_byte);
	}

        /**
         * 加密算法从pubfile中读取公共参数，在访问策略policy下将inputfile 指定的文件加密为路径为encfile的文件。
         * @param pubfile 公钥路径
         * @param policy	访问策略
         * @param inputfile	明文路径
         * @param encfile	密文路径
         * @throws Exception 抛出异常
         */
	public String enc(String pubfile, String policy, String inputfile,
			String encfile) throws Exception {
		BswabePub pub;
		BswabeCph cph;
		BswabeCphKey keyCph;
		byte[] plt;
		byte[] cphBuf;
		byte[] aesBuf;
		byte[] pub_byte;
		Element m;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		keyCph = Bswabe.enc(pub, policy);
		cph = keyCph.cph;
		m = keyCph.key;
		System.err.println("m = " + m.toString());

		if (cph == null) {
			System.out.println("Error happened in enc");
//			System.exit(0);
		}

		cphBuf = SerializeUtils.bswabeCphSerialize(cph);

		/* read file to encrypted */
		plt = Common.suckFile(inputfile);
		aesBuf = AESCoder.encrypt(m.toBytes(), plt);
		// PrintArr("element: ", m.toBytes());
		Common.writeCpabeFile(encfile, cphBuf, aesBuf);
        return m.toString();
	}

        /**
         * 解密算法从 pubfile 指定的文件中读入公共参数，从 prvfile 中读入用 户私钥，将加密文件 encfile 解密为 decfile。 
         * @param pubfile 公钥路径
         * @param prvfile 私钥路径
         * @param encfile 加密文件路径
         * @param decfile 解密文件路径
         * @throws Exception 抛出异常
         */
	public String dec(String pubfile, String prvfile, String encfile,
			String decfile) throws Exception {
		byte[] aesBuf, cphBuf;
		byte[] plt;
		byte[] prv_byte;
		byte[] pub_byte;
		byte[][] tmp;
		BswabeCph cph;
		BswabePrv prv;
		BswabePub pub;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* read cipher text */
		tmp = Common.readCpabeFile(encfile);
		aesBuf = tmp[0];
		cphBuf = tmp[1];
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);

		/* get BswabePrv form prvfile */
		prv_byte = Common.suckFile(prvfile);
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
		System.err.println("e = " + beb.e.toString());
		if (beb.b) {
			plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
			Common.spitFile(decfile, plt);
		} else {
//			System.exit(0);
		}
                return beb.e.toString();
	}

}
