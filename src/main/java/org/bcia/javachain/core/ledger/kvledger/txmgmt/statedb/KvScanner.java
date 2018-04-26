/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class KvScanner implements ResultsIterator {

    private String nameSpace;
    private Iterator dbItr;

    public static KvScanner KVScanner(String nameSpace, Iterator dbItr){
        KvScanner kvScanner = new KvScanner();
        kvScanner.setNameSpace(nameSpace);
        kvScanner.setDbItr(dbItr);
        return kvScanner;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Iterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(Iterator dbItr) {
        this.dbItr = dbItr;
    }

    @Override
    public QueryResult next() throws LedgerException {
        if(dbItr.hasNext()){
            return null;
        }
        Map.Entry<byte[], byte[]> iterator = (Map.Entry<byte[], byte[]>) dbItr.next();
        byte[] dbKey = iterator.getKey();
        byte[] dbVal = iterator.getValue();
        byte[] dbValCpy = Arrays.copyOf(dbVal, dbVal.length);
        String key = VersionLevelDB.splitCompositeKeyToKey(dbKey);
        byte[] value = StatedDB.decodeValueToBytes(dbValCpy);
        Height version = StatedDB.decodeValueToHeight(dbValCpy);
        QueryResult kv = new VersionedKV();
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setKey(key);
        compositeKey.setNamespace(nameSpace);
        VersionedValue versionedValue = new VersionedValue();
        versionedValue.setVersion(version);
        versionedValue.setValue(value);
        ((VersionedKV) kv).setCompositeKey(compositeKey);
        ((VersionedKV) kv).setVersionedValue(versionedValue);
        return kv;
    }

    @Override
    public void close() throws LedgerException {

    }
}
