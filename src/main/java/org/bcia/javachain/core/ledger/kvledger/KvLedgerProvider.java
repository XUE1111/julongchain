/*
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
package org.bcia.javachain.core.ledger.kvledger;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.INodeLedgerProvider;
import org.bcia.javachain.core.ledger.StateListener;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.HistoryLevelDB;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.IHistoryDB;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.IHistoryDBProvider;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.CommonStorageDBProvider;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DBPorvider;
import org.bcia.javachain.core.ledger.ledgerstorage.Provider;
import org.bcia.javachain.core.ledger.ledgerstorage.Store;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供操作账本的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class KvLedgerProvider implements INodeLedgerProvider {

    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(KvLedgerProvider.class);
    private static final LedgerException ERR_LEDGER_ID_EXISTS = new LedgerException("LedgerID is already exists");
    private static final LedgerException ERR_NON_EXISTS_LEDGER_ID  = new LedgerException("LedgerID does not exists");
    private static final LedgerException ERR_LEDGER_NOT_OPEN = new LedgerException("Ledger is not opened yet");

    private IdStore idStore = null;
    private Provider ledgerStoreProvider = null;
    private DBPorvider vdbProvider = null;
    private LevelDbProvider provider = null;
    private IHistoryDBProvider historyDBProvider = null;
    private Map<String, StateListener> stateListeners = new HashMap<>();

    /**
     * 新建Provider
     */
    public static INodeLedgerProvider newProvider() throws LedgerException {
        logger.info("Initializing ledger provider");
        IdStore idStore = IdStore.openIDStore();

        Provider ledgerStoreProvider = Store.newProvider();

        CommonStorageDBProvider vdbProvider = CommonStorageDBProvider.NewCommonStorageDBProvider();

        IHistoryDBProvider historyDBProvider = HistoryLevelDB.newHistoryDBProvider();

        logger.info("Ledger provider initialized");
        INodeLedgerProvider provider = new KvLedgerProvider();
        ((KvLedgerProvider) provider).setIdStore(idStore);
        ((KvLedgerProvider) provider).setLedgerStoreProvider(ledgerStoreProvider);
        ((KvLedgerProvider) provider).setVdbProvider(vdbProvider);
        ((KvLedgerProvider) provider).setHistoryDBProvider(historyDBProvider);
        ((KvLedgerProvider) provider).setStateListeners(null);
        provider.recoverUnderConstructionLedger();
        return provider;
    }

    /**
     * 初始化
     */
    public void initialize(Map<String, StateListener> stateListeners){
        this.stateListeners = stateListeners;
    }

    /**
     * 创建KvLedgerProvider
     */
    public INodeLedger create(Common.Block genesisBlock) throws LedgerException{
        String ledgerID = null;
        //获取账本id
        try {
            ledgerID = BlockUtils.getGroupIDFromBlock(genesisBlock);
        } catch (JavaChainException e) {
            logger.error("Got error when creating kvledger provider");
            throw new LedgerException(e);
        }
        //账本以存在,抛出异常
        if(idStore.ledgerIDExists(ledgerID)) {
            throw ERR_LEDGER_ID_EXISTS;
        }
        //设置创建标志
        idStore.setUnderConstructionFlag(ledgerID);

        INodeLedger lgr = openInternal(ledgerID);
        BlockAndPvtData bapd = new BlockAndPvtData();
        bapd.setBlock(genesisBlock);
        lgr.CommitWithPvtData(bapd);
        idStore.createLedgerID(ledgerID, genesisBlock);
        return lgr;
    }

    public INodeLedger open(String ledgerID) throws LedgerException{
        logger.debug("Opening kvledger with ledgerid " + ledgerID);
        //没有创建过ledgerid,抛出异常
        if(!idStore.ledgerIDExists(ledgerID)){
            throw ERR_NON_EXISTS_LEDGER_ID;
        }
        return openInternal(ledgerID);
    }

    @Override
    public Boolean exists(String ledgerID) throws LedgerException {
        return idStore.ledgerIDExists(ledgerID);
    }

    @Override
    public List<String> list() throws LedgerException {
        return idStore.getAllLedgerIDs();
    }

    @Override
    public void close() throws LedgerException {
        idStore.close();
        ledgerStoreProvider.close();
        vdbProvider.close();
        historyDBProvider.close();
    }

    public void recoverUnderConstructionLedger() throws LedgerException{
        logger.debug("Recovering under construction ledger");
        String ledgerID = idStore.getUnderConstructionFlag();

        if(ledgerID == null){
            logger.debug("No under construction leder found.");
            return;
        }

        logger.info(String.format("Ledger [%s] found as under construction", ledgerID));
        INodeLedger ledger = openInternal(ledgerID);
        Ledger.BlockchainInfo bcInfo = ledger.getBlockchainInfo();
        ledger.close();

        switch ((int) bcInfo.getHeight()){
            case 0:
                logger.info("Genesis block was not committed.");
                runCleanup(ledgerID);
                idStore.unsetUnderConstructionFlag();
                break;
            case 1:
                logger.info("Genesis block was committed.");
                Common.Block genesisBlock = ledger.getBlockByNumber((long) 0);
                idStore.createLedgerID(ledgerID, genesisBlock);
                break;
            default:
                throw new LedgerException(String.format(
                        "Under construction flag is set for ledger [%s] while the height of the blockchain is [%d]"
                        , ledgerID, bcInfo.getHeight()));
        }
    }

    private INodeLedger openInternal(String ledgerID) throws LedgerException{
        //账本的block仓库
        Store blockStore = ledgerStoreProvider.open(ledgerID);
        //账本的状态db
        DB vdb = vdbProvider.getDBHandle(ledgerID);
        //账本的历史db
        IHistoryDB historyDB = historyDBProvider.getDBHandle(ledgerID);

        KvLedger kvLedger = KvLedger.newKVLedger(ledgerID, blockStore, vdb, historyDB, stateListeners);
        return kvLedger;
    }

    private void runCleanup(String ledgerID){

    }

    public Provider getLedgerStoreProvider() {
        return ledgerStoreProvider;
    }

    public void setLedgerStoreProvider(Provider ledgerStoreProvider) {
        this.ledgerStoreProvider = ledgerStoreProvider;
    }

    public IdStore getIdStore() {
        return idStore;
    }

    public void setIdStore(IdStore idStore) {
        this.idStore = idStore;
    }

    public LevelDbProvider getProvider() {
        return provider;
    }

    public void setProvider(LevelDbProvider provider) {
        this.provider = provider;
    }

    public IHistoryDBProvider getHistoryDBProvider() {
        return historyDBProvider;
    }

    public void setHistoryDBProvider(IHistoryDBProvider historyDBProvider) {
        this.historyDBProvider = historyDBProvider;
    }

    public Map<String, StateListener> getStateListeners() {
        return stateListeners;
    }

    public void setStateListeners(Map<String, StateListener> stateListeners) {
        this.stateListeners = stateListeners;
    }

    public DBPorvider getVdbProvider() {
        return vdbProvider;
    }

    public void setVdbProvider(DBPorvider vdbProvider) {
        this.vdbProvider = vdbProvider;
    }
}