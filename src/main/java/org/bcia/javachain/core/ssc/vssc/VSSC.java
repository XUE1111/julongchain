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
package org.bcia.javachain.core.ssc.vssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.cauthdsl.CAuthDslBuilder;
import org.bcia.javachain.common.cauthdsl.PolicyProvider;
import org.bcia.javachain.common.channelconfig.ApplicationCapabilities;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.policies.IPolicyProvider;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.core.common.privdata.*;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.common.sysscprovider.SystemSmartContractFactory;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.protos.common.Collection;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractDataPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 验证系统智能合约　Validator System Smart Contract,VSSC
 * ValidatorOneValidSignature implements the default transaction validation policy,
 * which is to check the correctness of the read-write set and the endorsement
 * signatures against an endorsement policy that is supplied as argument to
 * every invoke
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class VSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(VSSC.class);

    public final static String DUPLICATED_IDENTITY_ERROR="Endorsement policy evaluation failure might be caused by duplicated identities";
    // sscProvider is the interface with which we call
    // methods of the system chaincode package without
    // import cycles
    private ISystemSmartContractProvider sscProvider;
    // collectionStore provides support to retrieve
    // collections from the ledger
    private ICollectionStore collectionStore;
    // collectionStoreSupport implements privdata.Support
    private IPrivDataSupport collectionStoreSupport;

    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        this.sscProvider= SystemSmartContractFactory.getSystemSmartContractProvider();
        this.collectionStoreSupport=new CollectionStoreSupport();
        this.collectionStore= CollectionStoreFactory.getCollectionStore(collectionStoreSupport);
        log.info("Successfully initialized VSSC");
        return newSuccessResponse();
    }

    // Invoke is called to validate the specified block of transactions
    // This validation system chaincode will check that the transaction in
    // the supplied envelope contains endorsements (that is. signatures
    // from entities) that comply with the supplied endorsement policy.
    // @return a successful SmartContractResponse (code 200) in case of success, or
    // an error otherwise
    // Note that Peer calls this function with 3 arguments, where args[0] is the
    // function name, args[1] is the Envelope and args[2] is the validation policy
    @Override
    public SmartContractResponse invoke(ISmartContractStub stub)  {
        // TODO: document the argument in some white paper or design document
        // args[0] - function name (not used now)
        // args[1] - serialized Envelope
        // args[2] - serialized policy
        log.debug("Enter VSSC invoke function");
        List<byte[]> args = stub.getArgs();
        int size=args.size();
        if(size<3){
            return newErrorResponse(String.format("Incorrect number of arguments , %d)",args.size()));
        }

        byte[] blockBytes=args.get(1);
        if(blockBytes.length==0){
            return newErrorResponse(String.format("No block to validate"));
        }
        byte[] policyBytes=args.get(2);
        if(policyBytes.length==0){
            return newErrorResponse(String.format("No policy supplied"));
        }

        // get the envelope...
        Common.Envelope envelope=null;
        try {
            envelope=ProtoUtils.getEnvelopeFromBlock(blockBytes);
        } catch (Exception e) {
            newErrorResponse(String.format("VSSC error: GetEnvelope failed, err %s",e.getMessage()));
        }

        // ...and the payload...
        // get the envelope...
        Common.Payload payload=null;
        try {
            payload=ProtoUtils.getPayload(envelope);
        } catch (Exception e) {
            newErrorResponse(String.format("VSSC error: GetPayload failed, err %s",e.getMessage()));
        }

        // get the policy
        Common.GroupHeader groupHeader=null;
        try {
            groupHeader=ProtoUtils.unMarshalGroupHeader(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            newErrorResponse(String.format("VSSC error: GetGroupHeader failed, err %s",e.getMessage()));
        }
        IApplicationConfig ac = this.sscProvider.getApplicationConfig(groupHeader.getGroupId());

        IMspManager manager=GlobalMspManagement.getManagerForChain(groupHeader.getGroupId());
        PolicyProvider policyProvider=new PolicyProvider(manager);
        IPolicy policy = policyProvider.newPolicy(policyBytes);

        // validate the payload type
        if(groupHeader.getType()!= Common.HeaderType.ENDORSER_TRANSACTION.getNumber()){
            log.error("Only Endorser Transactions are supported, provided type %d",groupHeader.getType());
            return newErrorResponse(String.format("Only Endorser Transactions are supported, provided type %d",groupHeader.getType()));
        }
        // ...and the transaction...
        TransactionPackage.Transaction transaction =null;
        try {
            transaction = ProtoUtils.getTransaction(payload.getData());
        } catch (InvalidProtocolBufferException e) {
            log.error("VSSC error: GetTransaction failed, err %s",e.getMessage());
            return newErrorResponse(String.format("VSSC error: GetTransaction failed, err %s",e.getMessage()));
        }

        // loop through each of the actions within
        List<TransactionPackage.TransactionAction> list = transaction.getActionsList();
        for (TransactionPackage.TransactionAction action:list) {
            TransactionPackage.SmartContractActionPayload scap =null;
            try {
                scap=TransactionPackage.SmartContractActionPayload.parseFrom(action.getPayload());
            } catch (InvalidProtocolBufferException e) {
                String msg=String.format("VSSC error: GetSmartContractActionPayload failed, err %s",e.getMessage());
                log.error(msg);
                return newErrorResponse(msg);
            }
            List<SignedData> signatureSet =null;
            try {
                signatureSet=deduplicateIdentity(scap);
            } catch (SysSmartContractException e) {
                return newErrorResponse(e.getMessage());
            }
            try {
                policy.evaluate(signatureSet);
            } catch (PolicyException e) {
                log.warn("Endorsement policy failure for transaction txid={}, err:{}",groupHeader.getTxId(),e.getMessage());
                if(signatureSet.size()<scap.getAction().getEndorsementsCount()){
                    // Warning: duplicated identities exist, endorsement failure might be cause by this reason
                    return newErrorResponse(DUPLICATED_IDENTITY_ERROR);
                }
                return newErrorResponse(String.format("VSSC error: policy evaluation failed, err %s",e.getMessage()));
            }

            ProposalPackage.SmartContractHeaderExtension hdrExt =null;
            try {
                hdrExt = ProposalPackage.SmartContractHeaderExtension.parseFrom(groupHeader.getExtension());
            } catch (InvalidProtocolBufferException e) {
                String msg=String.format("VSSC error: GetSmartContractHeaderExtension failed, err %s",e.getMessage());
                log.error(msg);
                return newErrorResponse(msg);
            }
            //do some extra validation that is specific to lssc
            if(hdrExt.getSmartContractId().getName()=="LSSC"){
                log.debug("VSSC info: doing special validation for LSSC");
                try {
                    validateLSSCInvocation(stub,groupHeader.getGroupId(),envelope,scap,payload,ac.getCapabilities());
                } catch (SysSmartContractException e) {
                    String msg=String.format("VSSC error: ValidateLSSCInvocation failed, err %s",e.getMessage());
                    return newErrorResponse(msg);
                }
            }
        }
        log.debug("VSSC exits successfully");
        return newSuccessResponse();
    }

    @Override
    public String getSmartContractStrDescription() {
        String description="与验证相关的系统智能合约";
        return description;
    }

    /**
     * 验证对LSSC的调用
     * @param stub
     * @param groupID
     * @param envelope
     * @param scap
     * @param payload
     * @param ac
     * @throws SysSmartContractException
     */
    private void validateLSSCInvocation(
            ISmartContractStub stub,
            String groupID,
            Common.Envelope envelope,
            TransactionPackage.SmartContractActionPayload scap,
            Common.Payload payload,
            IApplicationCapabilities ac
            )throws SysSmartContractException{
        VSSCSupportForLsscInvocation.validateLSSCInvocation(stub,groupID,envelope,scap,payload,ac,
                collectionStore,sscProvider,log);
    }

    /**
     * 获取实例化的智能合约
     * @param groupID
     * @param smartcontractID
     * @return
     */
    private SmartContractDataPackage.SmartContractData getInstantiatedSmartContract(String groupID,String smartcontractID)
                                                                   throws  SysSmartContractException{
        IQueryExecutor qe =null;
        try{
            qe=this.sscProvider.getQueryExecutorForLedger(groupID);
        }catch(JavaChainException e){
            String msg=String.format("Could not retrieve QueryExecutor for group %s, error %s",groupID,e.getMessage());
            throw new SysSmartContractException(msg);
        }
        byte[] bytes=null;
        try {
            bytes=qe.getState("lssc", smartcontractID);
        } catch (LedgerException e) {
            String msg=String.format("Could not retrieve state for smartcontract %s on group %s, error %s",
                    smartcontractID,groupID,e.getMessage());
            throw new SysSmartContractException(msg);
        }

        if(bytes==null){
            return null;
        }
        SmartContractDataPackage.SmartContractData data=null;
        try {
            data=SmartContractDataPackage.SmartContractData.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Unmarshalling SmartContractQueryResponse failed, error %s",e.getMessage());
            throw new SysSmartContractException(msg);
        }
        return data;
    }

    private List<SignedData> deduplicateIdentity(TransactionPackage.SmartContractActionPayload scap)
                                   throws SysSmartContractException{
        return null;
    }
}
