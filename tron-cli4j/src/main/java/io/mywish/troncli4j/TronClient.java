package io.mywish.troncli4j;

import io.mywish.troncli4j.model.EventResult;
import io.mywish.troncli4j.model.response.BlockResponse;
import io.mywish.troncli4j.model.response.NodeInfoResponse;

import java.util.List;

public interface TronClient {
    NodeInfoResponse getNodeInfo() throws Exception;

    BlockResponse getBlock(String id) throws Exception;

    BlockResponse getBlock(Long number) throws Exception;

//    List<EventResult> getEventResult(String base58ContractAddress, String event, Long blockNum) throws Exception;

    List<EventResult> getEventResult(String txId) throws Exception;

//    BalanceResponse getBalance(String code, String account) throws Exception;
}
