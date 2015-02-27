package com.unicom.sms.util;

import com.alibaba.fastjson.JSONObject;
import com.unicom.sms.exception.ServiceException;

import java.io.*;

/**
 * Created by zhaofrancis on 15/1/22.
 */
public class JSONUtils {

    public static JSONObject parseJSONFile(InputStream inputStream) throws ServiceException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            return JSONObject.parseObject(stringBuffer.toString());
        } catch (FileNotFoundException e) {
            //TODO
        } catch (IOException e) {
            //TODO
        }
        return null;
    }
}
