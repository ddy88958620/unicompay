package com.chinaunicom.unipay.ws.utils.encrypt;

import com.alibaba.fastjson.JSON;
import com.chinaunicom.unipay.ws.utils.RandomUtil;
import org.junit.Test;

import java.net.URLDecoder;
import java.util.TreeMap;

import static org.junit.Assert.assertTrue;

/**
 * User: Frank
 * Date: 2014/11/6
 * Time: 9:41
 */
//展示加解密流程，用于客户端和服务端之间加解密说明
public class EncryptTest {

    static final String SERVER_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdxyjo1mvmNHCoEEMnidW5fbCbpbR6noUdcbXRT2gDfVA+3uQQC44eTrxlwjLReRyV5T58krfEQNwWE1pIGyqa0AzxGwYpQ68jQHZtrkNk0ES4+1OrlaLTj46szcw1Xs0XPdT5XpxvLLPRg6l9YdFON3RehN7sSn0U8Ka/P5AbqQIDAQAB";
    static final String SERVER_PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJ3HKOjWa+Y0cKgQQyeJ1bl9sJultHqehR1xtdFPaAN9UD7e5BALjh5OvGXCMtF5HJXlPnySt8RA3BYTWkgbKprQDPEbBilDryNAdm2uQ2TQRLj7U6uVotOPjqzNzDVezRc91PlenG8ss9GDqX1h0U43dF6E3uxKfRTwpr8/kBupAgMBAAECgYEAnPd2i65Gyk+BhLIpHTzTlCanfTHty8/JPvHRoNsO7+nL28CDxDK6wQw0AkihpiSHe9kFwYl1qZmdW5kFSJc+WhZviRZ8IsBGF+IKpE9QDnizi9ONUZpqpN/G9/aGPmjMLhX1mqn9sXPLKsnN3e6b8F/FdmTRiPUNKJ0q7GqnHsECQQDPHV1NnWn6ks2z6X/H+dmzYT6IshQBbhcUrAtVhuprM1eetdwbilXw9ahJXTyXZbkv4VeMd77ZOACGFNuyt3tTAkEAwwSv/GEsG4eqQ5qpkqdohrxinD8RG/qxAWdX6jyqsjlaN+Pu9rWERr/9C1X1y4Mk+MnIrFtwQmFr8yGWkjwpkwJBAM1YTtaSGK4P+RTEkCVkCbz40DhVXtJgJVAgl4gvoKGyoM/BfT5s9HJYDYbXgyiT+XyITfX+D2FsTdCiQ4S4t/MCQQCngUgzq7c58j4BkT+jkp1KVVcRw23LrXkJrp0puXAYYwClzua7C1ARgOg1K2FSq1M/SfpOaL+0lH8nNytgccJ3AkAzO4qP3MbJ9Gamt+d+/C70FeuQbdvFjg+PXvSLuCiqZv6eKwAlws1hmhpxUfUzD89UBNb5bN4Rm57/0oJwPKF+";
    static final String CLIENT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCI2UBlCioxUgNgM+KBK/W74ffJebYVgLMpmjWyK79Nnuyw2CuBt02ZR+TJHBKHEv96FmP0JwFREM21GW1a+XUNoK+BzoaAsBviA4McVSw7cvMdEL7N1n1ngg8YFZmBqCZI+3h6RAbU2UDtRsXKi8bfdMwDlTYHaH2JRCYmAxAR8wIDAQAB";
    static final String CLIENT_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIjZQGUKKjFSA2Az4oEr9bvh98l5thWAsymaNbIrv02e7LDYK4G3TZlH5MkcEocS/3oWY/QnAVEQzbUZbVr5dQ2gr4HOhoCwG+IDgxxVLDty8x0Qvs3WfWeCDxgVmYGoJkj7eHpEBtTZQO1GxcqLxt90zAOVNgdofYlEJiYDEBHzAgMBAAECgYB8rMkBBIi6FEjlDYAPRLiE6nKwCIW+A1m2zjqbT0XjdqWW2+qPLCuzOQ+TtVKf4swEM3/uwNZvP4rZsgex+VEMuc5hoNd0/13GqeEC9gZVUU7RBdAgijTS7PRM2yFDk5Y0jOWJsDwo49HQrhNKmT5z+Hh8Km4JWN0D3BiH3WvjSQJBAM16SkMc1DatsoivMBh10mzt6tRNhmVZ/wkRn5TKGKDVIS+21woj/pLBPOo8a9VUQecmsjvKHwuPKPw3TnITpxcCQQCqfyInG7M1/vXpX0rFJar20N1WvfUC5e11OYGuW14VPWOWDKsNTeF80L+LWmNG1SOuyXjv5xdsl5vpmqAGYbWFAkAcHGzofZsy19SjCoSj9AqTyIDmBq0qVIOls/mHG5b++emOY49L3dzIKxOwYA/IobxaaVrc/yv8ItvSlaZvyOyFAkAb6sNB2hRHFB9Z/iN1Eozi6yJC8Mmsls+B8U+dqBJIsgubZyme/RCd6mRiwgMddwrntM+boKBCRLTf1FS9lsQtAkEAp/ShJcPgnpmD3PjszGFxHGC3mBCpwEivxZNeDGdcv5zA1PaB5hcq5Tzgrcpro6WtRCsihXfuOiSYeyNQYXjeyw==";

    @Test
    public void testClientToServer() throws Exception {

        //需要加密的数据，并用客户端私钥对其进行签名防止篡改
        TreeMap<String, Object> entity = new TreeMap<String,Object>();
        entity.put("cardno", "测试测试测试");
        entity.put("terminalid", "00-EO-4C-6C-08-75");
        entity.put("sign", EncryUtil.handleRSA(entity, CLIENT_PRIVATE_KEY));

        //生成随机加密秘钥，加密数据并用服务端公钥加密该秘钥
        final String aesKey = RandomUtil.getRandom(16);
        String data = AES.encryptToBase64(JSON.toJSONString(entity), aesKey);
        String encryptkey = RSA.encrypt(aesKey, SERVER_PUBLIC_KEY);

        /*
        ……将data和encryptkey发送到服务端……
         */

        //对数据进行验签，通过验签之后利用服务端私钥解密加密秘钥，并用解出秘钥进行数据的解密
        boolean checkSign = EncryUtil.checkDecryptAndSign(data, encryptkey, CLIENT_PUBLIC_KEY, SERVER_PRIVATE_KEY);
        if(checkSign) {
            String _aeskey = RSA.decrypt(encryptkey, SERVER_PRIVATE_KEY);
            String _data = AES.decryptFromBase64(data, _aeskey);
            System.out.println(_data);
            entity = JSON.parseObject(_data, TreeMap.class);
            assertTrue(entity.get("cardno").equals("测试测试测试"));
        } else {
            assertTrue("签名异常", false);
        }

    }

    @Test
    public void testRSA() throws Exception {
        /*String key = "Ka8+19vRAEVO7sRmwZKspuneC+lhcfZWKvSQAySToyTrxzOBXpXJyql6ixLxjOMzNayXt686JPlXLx4RcOhGu+HVK7Qcvz1Wpyl6VpHiokO7ZArzKAkyApBSzKdJ2ZoG0HqOTfV84/tF29vd+IKFyY+F5wHpGCu6M9eWooMxui8=";

        String _key = RSA.decrypt(key, SERVER_PRIVATE_KEY);
        assertTrue(true);*/

        String data = "{phone:135}";

        Demo demo = JSON.parseObject(data, Demo.class);
        assertTrue(true);
    }

    static class Demo {
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
