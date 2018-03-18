/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.smartcontract;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/18
 * @company Dingxuan
 */
public class SmartContractMessageType {

    public static Integer SMARTCONTRACT_MESSAGE_UNDEFINED_TYPE                = 0;
    public static Integer SMARTCONTRACT_MESSAGE_REGISTER_TYPE                 = 1;
    public static Integer SMARTCONTRACT_MESSAGE_REGISTERED_TYPE               = 2;
    public static Integer SMARTCONTRACT_MESSAGE_INIT_TYPE                     = 3;
    public static Integer SMARTCONTRACT_MESSAGE_READY_TYPE                    = 4;
    public static Integer SMARTCONTRACT_MESSAGE_TRANSACTION_TYPE              = 5;
    public static Integer SMARTCONTRACT_MESSAGE_COMPLETED_TYPE                = 6;
    public static Integer SMARTCONTRACT_MESSAGE_ERROR_TYPE                    = 7;
    public static Integer SMARTCONTRACT_MESSAGE_GET_STATE_TYPE                = 8;
    public static Integer SMARTCONTRACT_MESSAGE_PUT_STATE_TYPE                = 9;
    public static Integer SMARTCONTRACT_MESSAGE_DEL_STATE_TYPE                = 10;
    public static Integer SMARTCONTRACT_MESSAGE_INVOKE_SMARTCONTRACT_TYPE     = 11;
    public static Integer SMARTCONTRACT_MESSAGE_RESPONSE_TYPE                 = 13;
    public static Integer SMARTCONTRACT_MESSAGE_GET_STATE_BY_RANGE_TYPE       = 14;
    public static Integer SMARTCONTRACT_MESSAGE_GET_QUERY_RESULT_TYPE         = 15;
    public static Integer SMARTCONTRACT_MESSAGE_QUERY_STATE_NEXT_TYPE         = 16;
    public static Integer SMARTCONTRACT_MESSAGE_QUERY_STATE_CLOSE_TYPE        = 17;
    public static Integer SMARTCONTRACT_MESSAGE_KEEPALIVE_TYPE                = 18;
    public static Integer SMARTCONTRACT_MESSAGE_GET_HISTORY_FOR_KEY_TYPE      = 19;

    public static String SMARTCONTRACT_MESSAGE_UNDEFINED_NAME                = "UNDEFINED";
    public static String SMARTCONTRACT_MESSAGE_REGISTER_NAME                 = "REGISTER";
    public static String SMARTCONTRACT_MESSAGE_REGISTERED_NAME               = "REGISTERED";
    public static String SMARTCONTRACT_MESSAGE_INIT_NAME                     = "INIT";
    public static String SMARTCONTRACT_MESSAGE_READY_NAME                    = "READY";
    public static String SMARTCONTRACT_MESSAGE_TRANSACTION_NAME              = "TRANSACTION";
    public static String SMARTCONTRACT_MESSAGE_COMPLETED_NAME                = "COMPLETED";
    public static String SMARTCONTRACT_MESSAGE_ERROR_NAME                    = "ERROR";
    public static String SMARTCONTRACT_MESSAGE_GET_STATE_NAME                = "GET_STATE";
    public static String SMARTCONTRACT_MESSAGE_PUT_STATE_NAME                = "PUT_STATE";
    public static String SMARTCONTRACT_MESSAGE_DEL_STATE_NAME                = "DEL_STATE";
    public static String SMARTCONTRACT_MESSAGE_INVOKE_SMARTCONTRACT_NAME     = "INVOKE_CHAINCODE";
    public static String SMARTCONTRACT_MESSAGE_RESPONSE_NAME                 = "RESPONSE";
    public static String SMARTCONTRACT_MESSAGE_GET_STATE_BY_RANGE_NAME       = "GET_STATE_BY_RANGE";
    public static String SMARTCONTRACT_MESSAGE_GET_QUERY_RESULT_NAME         = "GET_QUERY_RESULT";
    public static String SMARTCONTRACT_MESSAGE_QUERY_STATE_NEXT_NAME         = "QUERY_STATE_NEXT";
    public static String SMARTCONTRACT_MESSAGE_QUERY_STATE_CLOSE_NAME        = "QUERY_STATE_CLOSE";
    public static String SMARTCONTRACT_MESSAGE_KEEPALIVE_NAME                = "KEEPALIVE";
    public static String SMARTCONTRACT_MESSAGE_GET_HISTORY_FOR_KEY_NAME      = "GET_HISTORY_FOR_KEY";
    
    public static Map<Integer, String> SMARTCONTRACT_TYPE_NAME_MAP = new HashMap<Integer, String>();
    public static Map<String, Integer> SMARTCONTRACT_NAME_TYPE_MAP = new HashMap<String, Integer>();
    
    static {
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_UNDEFINED_TYPE, SMARTCONTRACT_MESSAGE_UNDEFINED_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_REGISTER_TYPE, SMARTCONTRACT_MESSAGE_REGISTER_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_REGISTERED_TYPE, SMARTCONTRACT_MESSAGE_REGISTERED_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_INIT_TYPE, SMARTCONTRACT_MESSAGE_INIT_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_READY_TYPE, SMARTCONTRACT_MESSAGE_READY_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_TRANSACTION_TYPE, SMARTCONTRACT_MESSAGE_TRANSACTION_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_COMPLETED_TYPE, SMARTCONTRACT_MESSAGE_COMPLETED_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_ERROR_TYPE, SMARTCONTRACT_MESSAGE_ERROR_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_GET_STATE_TYPE, SMARTCONTRACT_MESSAGE_GET_STATE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_PUT_STATE_TYPE, SMARTCONTRACT_MESSAGE_PUT_STATE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_DEL_STATE_TYPE, SMARTCONTRACT_MESSAGE_DEL_STATE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_INVOKE_SMARTCONTRACT_TYPE, SMARTCONTRACT_MESSAGE_INVOKE_SMARTCONTRACT_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_RESPONSE_TYPE, SMARTCONTRACT_MESSAGE_RESPONSE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_GET_STATE_BY_RANGE_TYPE, SMARTCONTRACT_MESSAGE_GET_STATE_BY_RANGE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_GET_QUERY_RESULT_TYPE, SMARTCONTRACT_MESSAGE_GET_QUERY_RESULT_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_QUERY_STATE_NEXT_TYPE, SMARTCONTRACT_MESSAGE_QUERY_STATE_NEXT_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_QUERY_STATE_CLOSE_TYPE, SMARTCONTRACT_MESSAGE_QUERY_STATE_CLOSE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_KEEPALIVE_TYPE, SMARTCONTRACT_MESSAGE_KEEPALIVE_NAME);
        SMARTCONTRACT_TYPE_NAME_MAP.put(SMARTCONTRACT_MESSAGE_GET_HISTORY_FOR_KEY_TYPE, SMARTCONTRACT_MESSAGE_GET_HISTORY_FOR_KEY_NAME);

        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_UNDEFINED_NAME, SMARTCONTRACT_MESSAGE_UNDEFINED_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_REGISTER_NAME, SMARTCONTRACT_MESSAGE_REGISTER_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_REGISTERED_NAME, SMARTCONTRACT_MESSAGE_REGISTERED_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_INIT_NAME, SMARTCONTRACT_MESSAGE_INIT_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_READY_NAME, SMARTCONTRACT_MESSAGE_READY_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_TRANSACTION_NAME, SMARTCONTRACT_MESSAGE_TRANSACTION_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_COMPLETED_NAME, SMARTCONTRACT_MESSAGE_COMPLETED_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_ERROR_NAME, SMARTCONTRACT_MESSAGE_ERROR_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_GET_STATE_NAME, SMARTCONTRACT_MESSAGE_GET_STATE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_PUT_STATE_NAME, SMARTCONTRACT_MESSAGE_PUT_STATE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_DEL_STATE_NAME, SMARTCONTRACT_MESSAGE_DEL_STATE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_INVOKE_SMARTCONTRACT_NAME, SMARTCONTRACT_MESSAGE_INVOKE_SMARTCONTRACT_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_RESPONSE_NAME, SMARTCONTRACT_MESSAGE_RESPONSE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_GET_STATE_BY_RANGE_NAME, SMARTCONTRACT_MESSAGE_GET_STATE_BY_RANGE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_GET_QUERY_RESULT_NAME, SMARTCONTRACT_MESSAGE_GET_QUERY_RESULT_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_QUERY_STATE_NEXT_NAME, SMARTCONTRACT_MESSAGE_QUERY_STATE_NEXT_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_QUERY_STATE_CLOSE_NAME, SMARTCONTRACT_MESSAGE_QUERY_STATE_CLOSE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_KEEPALIVE_NAME, SMARTCONTRACT_MESSAGE_KEEPALIVE_TYPE);
        SMARTCONTRACT_NAME_TYPE_MAP.put(SMARTCONTRACT_MESSAGE_GET_HISTORY_FOR_KEY_NAME, SMARTCONTRACT_MESSAGE_GET_HISTORY_FOR_KEY_TYPE);
    }
    

}
