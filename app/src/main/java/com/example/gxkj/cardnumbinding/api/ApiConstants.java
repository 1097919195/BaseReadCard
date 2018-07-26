/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.gxkj.cardnumbinding.api;

public class ApiConstants {
    public static final String QUALITY_HOST_TEST = "http://rap2api.taobao.org/app/mock/8690/";
//    public static final String QUALITY_HOST = "https://www.npclo.com/";
    public static final String QUALITY_HOST = "https://n.npclo.com/";

    /**
     * 获取对应的host
     *
     * @param hostType host类型
     * @return host
     */
    public static String getHost(int hostType) {
        String host;
        switch (hostType) {
            case HostType.QUALITY_DATA_TEST:
                host = QUALITY_HOST_TEST;
                break;
            case HostType.QUALITY_DATA:
                host = QUALITY_HOST;
                break;
            default:
                host = "";
                break;
        }
        return host;
    }
}
