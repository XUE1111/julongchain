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
package org.bcia.javachain.common.ledger.blockledger.file;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.BlockStorage;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.ledger.blkstorage.BlockStoreProvider;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.FsBlockStoreProvider;
import org.bcia.javachain.common.ledger.blockledger.Factory;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.util.*;

/**
 * 文件账本工厂
 *
 * @author sunzongyu
 * @date 2018/04/26
 * @company Dingxuan
 */
public class FileLedgerFactory implements Factory {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(FileLedgerFactory.class);

    private BlockStoreProvider blkStorageProvider;
    private Map<String, ReadWriteBase> ledgers;

    public FileLedgerFactory(String directory) throws LedgerException {
        IndexConfig indexConfig = new IndexConfig();
        indexConfig.setAttrsToIndex(new String[]{BlockStorage.INDEXABLE_ATTR_BLOCK_NUM});
        this.blkStorageProvider = FsBlockStoreProvider.newProvider(Config.newConf(directory, -1), indexConfig);
        ledgers = new HashMap<>();
    }

    @Override
    public synchronized ReadWriteBase getOrCreate(String groupID) throws LedgerException {
        //已存在账本,直接返回
        ReadWriteBase ledger = ledgers.get(groupID);
        if(ledger != null){
            return ledger;
        }
        BlockStore blkStore = blkStorageProvider.openBlockStore(groupID);
        ledger = new FileLedger(blkStore);
        ledgers.put(groupID, ledger);
        return ledger;
    }

    @Override
    public List<String> groupIDs() throws LedgerException {
        List<String> groupIDS = new ArrayList<String>();
        for (String s : blkStorageProvider.list()) {
            groupIDS.add(s);
        }
        return groupIDS;
    }

    @Override
    public void close() throws LedgerException {
        blkStorageProvider.close();
    }
}